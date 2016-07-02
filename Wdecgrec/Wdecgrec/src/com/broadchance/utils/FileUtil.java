/**
 * 
 */
package com.broadchance.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Path;
import android.os.Environment;

import com.broadchance.entity.FileFrameData;
import com.broadchance.entity.FileType;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.manager.DataManager;

/**
 * @author ryan.wang
 * 
 */
public class FileUtil {
	private final static String TAG = FileUtil.class.getSimpleName();
	public final static String ECG_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/"
			+ ConstantConfig.APP_DIR;
	/**
	 * 上传文件的zip临时目录
	 */
	public final static String ECG_BATCH_UPLOADDIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/"
			+ ConstantConfig.APP_DIR + "/ecg/batchupload/";
	/**
	 * 存放ecg文件数据
	 */
	private final static String ECG_BATCHDIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/"
			+ ConstantConfig.APP_DIR + "/ecg/batch/";
	/**
	 * 实时上传目录
	 */
	public final static String ECGTEALTIMEDIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/"
			+ ConstantConfig.APP_DIR + "/ecg/realtime/";
	/**
	 * 下载目录
	 */
	public final static String APP_DOWNLOAD = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/"
			+ ConstantConfig.APP_DIR + "/download/";
	public final static String APP_CRASH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/"
			+ ConstantConfig.APP_DIR + "/crash/";

	public FileUtil() throws Exception {
		if (checkDir(ECG_BATCH_UPLOADDIR) && checkDir(ECG_BATCHDIR)
				&& checkDir(ECGTEALTIMEDIR) && checkDir(APP_DOWNLOAD)
				&& checkDir(APP_CRASH)) {
			if (fileHead == null)
				fileHead = buildFileHead();
		}
	}

	public static String getEcgDir() {
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user == null) {
			return null;
		}
		String dir = ECG_BATCHDIR + user.getLoginName() + "/";
		try {
			if (checkDir(dir)) {
				return dir;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (ConstantConfig.Debug) {
				LogUtil.e(TAG, "创建ecg目录失败:" + dir, e);
			}
		}
		return null;
	}

	/**
	 * 文件头
	 */
	private static ByteBuffer fileHead;
	private ByteBuffer blockHead;
	/**
 * 
 */
	private RandomAccessFile randomAccessFile;
	private FileChannel fileChannel;
	private File ecgFile;
	/**
	 * 指向即将写入的下标
	 */
	private long bufferPosition = 0;
	/**
	 * 上一个block的在整个流中的开始位置
	 */
	private long lastBlockPosition = 0;
	/**
	 * 上一个block的最后一个记录的结束位置的下一个字节
	 */
	private long lastRecordEndPosition = 0;
	private ECGFileStatus fileStatus = ECGFileStatus.NONE;
	/**
	 * 是否实时模式
	 */
	private boolean isRealTimeFile = false;
	/**
	 * 文件中第一组数据点的从设备的接收到时间
	 */
	private Date dataBeginTime;
	/**
	 * 文件中最后一组数据点的从设备的接收到时间
	 */
	private Date dataEndTime;

	enum ECGFileStatus {
		NONE, BEGIN, WRITEBLOCK, WRITERECORD, END
	}

	/**
	 * 检查ecg目录是否存在，不存在创建
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean checkDir(String dir) {
		File file = new File(dir);
		if (SDCardUtils.isSDCardAvailable()) {
			if (file.exists()) {
				return true;
			} else {
				return file.mkdirs();
			}
		} else {
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, "当前目录不可用：" + file.getAbsolutePath());
			}
		}
		return false;
	}

	public void setTempMode() {
		isRealTimeFile = true;
	}

	/**
	 * 生成文件名
	 * 
	 * @return
	 */
	private String getFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat(
				ConstantConfig.DATA_DATE_FORMAT);
		String dir = getEcgDir();
		String path = isRealTimeFile ? FileUtil.ECGTEALTIMEDIR : dir;
		if (path == null) {
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, "ecg目录不存在" + dir);
			}
			return null;
		}
		return path + sdf.format(new Date()) + ConstantConfig.ECGDATA_SUFFIX;
	}

	/**
	 * 清除目录下的所有文件
	 * 
	 * @param dir
	 */
	public void clearFiles(String dir) {
		File file = new File(dir);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File temp : files) {
				temp.delete();
			}
		}
	}

	/**
	 * 构建文件头
	 * 
	 * @return
	 */
	private static ByteBuffer buildFileHead() {
		int power = 7;
		ByteBuffer buffer = ByteBuffer.allocate((int) Math.pow(2, power));
		// 版本占1字节
		byte version = 0x01;
		buffer.put(version);
		/**
		 * control 第一个bit数据是否完整：0数据完整，1数据不完整 第二个bit是否为文件头：0文件头，1 数据块 2-7
		 * 保留备用，所有未定义的bit用1表示包括占位 文件头 0011 1111 占1字节
		 */
		byte control = 0x3F;
		buffer.put(control);
		// 整个文件头长度的幂，占1字节
		byte length = (byte) power;
		buffer.put(length);
		// 数据块中第一个数据点在flash中的点序号占5字节
		byte[] firstSeq = new byte[] { 0, 0, 0, 0, 0 };
		buffer.put(firstSeq);
		// 设备序列号未定义，占4字节
		byte[] deviceId = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF };
		buffer.put(deviceId);
		// bootVersion占四字节 0x01020304 = 1.2.3.4
		byte[] bootVersion = new byte[] { 0x01, 0x02, 0x03, 0x04 };
		buffer.put(bootVersion);
		// firmwareVer 占四字节 0x01020304 = 1.2.3.4
		byte[] firmwareVer = new byte[] { 0x01, 0x02, 0x03, 0x04 };
		buffer.put(firmwareVer);
		// ECG 通道数目，现在为1,3， 范围 1-255,此处使用三通道，占1字节
		byte ecgChannel = 0x03;
		buffer.put(ecgChannel);
		// ECG_Resolution ECG采用精度(指每个心电数据点占位，现在是两个字节)，8位，16位，24位，32位，
		// 范围1-64，此处精度使用16，占1字节
		byte ecgResolution = 0x10;
		buffer.put(ecgResolution);
		// ECG_Frequency 采用速率，1000Hz 等，单通道125Hz，占2字节
		byte[] ecgFrequency = new byte[] { 0x00, 0x7D };
		buffer.put(ecgFrequency);
		// 1St Channel Nm 第 1个Channel 的名称占位16字节,暂时未定
		byte[] channel1 = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		buffer.put(channel1);
		// 2nd Channel Nm 第 2个Channel 的名称占位16字节,暂时未定
		byte[] channel2 = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		buffer.put(channel2);
		// 3rd Channel Nm 第 3个Channel 的名称占位16字节,暂时未定
		byte[] channel3 = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		buffer.put(channel3);
		// 剩下字节填充FF
		byte padding = (byte) 0xFF;
		while (buffer.hasRemaining()) {
			buffer.put(padding);
		}
		// limit置为当前position，position置为0
		buffer.flip();
		buffer.asReadOnlyBuffer();
		return buffer;
	}

	/**
	 * 构建数据块头部
	 * 
	 * @return
	 */
	private static ByteBuffer buildBlockHead() {
		// 头一共占用10字节
		ByteBuffer buffer = ByteBuffer.allocate(10);
		// 版本占1字节
		byte version = 0x01;
		buffer.put(version);
		/**
		 * control 第一个bit数据是否完整：0数据完整，1数据不完整 第二个bit是否为文件头：0文件头，1 数据块 2-7
		 * 保留备用，所有未定义的bit用1表示包括占位 文件头 0111 1111 占1字节
		 */
		byte control = 0x7F;
		buffer.put(control);
		// 整个数据块头长度的幂，占1字节，此处默认0留待回写
		byte length = (byte) 0x0;
		buffer.put(length);
		// 数据块中第一个数据点在flash中的点序号占5字节
		byte[] firstSeq = new byte[] { 0, 0, 0, 0, 0 };
		buffer.put(firstSeq);
		// Records 本记录内总的ECG采用数据点，当前块中的总数据点数，占位两个字节，此处默认0留待回写
		byte[] records = new byte[] { 0, 0, };
		buffer.put(records);
		// 剩下字节填充FF
		byte padding = (byte) 0xFF;
		while (buffer.hasRemaining()) {
			buffer.put(padding);
		}
		// limit置为当前position，position置为0
		buffer.flip();
		return buffer;
	}

	/**
	 * 开始写文件
	 * 
	 * @throws Exception
	 */
	public void beginWriteFile() throws Exception {
		if (fileStatus != ECGFileStatus.NONE) {
			throw new Exception("文件状态不对：" + fileStatus);
		}
		if (ConstantConfig.Debug) {
			LogUtil.d(TAG, "beginWriteFile");
		}
		bufferPosition = 0;
		lastBlockPosition = 0;
		fileStatus = ECGFileStatus.BEGIN;
		String fileName = getFileName();
		if (fileName == null)
			return;
		ecgFile = new File(fileName);
		randomAccessFile = new RandomAccessFile(ecgFile, "rw");
		fileChannel = randomAccessFile.getChannel();
		MappedByteBuffer mbb = fileChannel.map(FileChannel.MapMode.READ_WRITE,
				bufferPosition, fileHead.capacity());
		fileHead.position(0);
		// 开始写入文件头
		while (fileHead.hasRemaining()) {
			mbb.put(fileHead.get());
		}
		bufferPosition += mbb.capacity();
	}

	// 由于文件结构限制，随时存储效果不大
	// public void writeRecord(FileFrameData fileFrameData) throws Exception {
	// if (fileFrameData == null)
	// return;
	// if (fileStatus != ECGFileStatus.BEGIN
	// || fileStatus != ECGFileStatus.WRITERECORD) {
	// throw new Exception("文件状态不对：" + fileStatus);
	// }
	// //如果是
	// if (fileStatus == ECGFileStatus.BEGIN) {
	//
	// }
	// }

	/**
	 * 写入文件块
	 * 
	 * @param fileFrameDatas
	 * @throws Exception
	 */
	public void writeBlock(List<FileFrameData> fileFrameDatas) throws Exception {
		if (fileFrameDatas == null)
			return;
		if (fileFrameDatas.size() < 1)
			return;
		if (fileStatus != ECGFileStatus.BEGIN
				&& fileStatus != ECGFileStatus.WRITEBLOCK) {
			throw new Exception("文件状态不对：" + fileStatus);
		}
		fileStatus = ECGFileStatus.WRITEBLOCK;
		ByteBuffer blockHead = buildBlockHead();
		MappedByteBuffer mbb = null;
		// 由于blockHead中Records仅占用两字节，所以此处检查需要写入的点数
		int blocks = (int) Math.ceil((fileFrameDatas.size() * 3f) / (0xFFFF));
		List<FileFrameData> waitWrite = null;
		List<FileFrameData> nWrite = null;
		if (blocks == 1) {
			waitWrite = fileFrameDatas;
		} else {
			waitWrite = new ArrayList<FileFrameData>();
			int maxSize = (int) Math.floor(0xFFFF / (3f));
			for (int i = 0; i < maxSize; i++) {
				waitWrite.add(fileFrameDatas.get(i));
			}
			nWrite = new ArrayList<FileFrameData>();
			for (int i = maxSize; i < fileFrameDatas.size(); i++) {
				nWrite.add(fileFrameDatas.get(i));
			}
		}
		// 回写上一blockLength和FirstSEQ
		// 存在上一block
		if (lastBlockPosition > 0) {
			// 获取上一block的10个字节的头信息
			mbb = fileChannel.map(FileChannel.MapMode.READ_WRITE,
					lastBlockPosition, 10);
			// 回写上一block长度的幂
			byte length = (byte) Math.log(bufferPosition - lastBlockPosition);
			mbb.put(2, length);
			// 获取上一block的firstSeq
			byte[] firstSeq = new byte[5];
			for (int i = 0; i < 5; i++) {
				firstSeq[i] = mbb.get(3 + i);
			}
			long lastFirstSeq = BleDataUtil.bytestoLong(firstSeq);
			byte[] records = new byte[2];
			for (int i = 0; i < 2; i++) {
				records[i] = mbb.get(8 + i);
			}
			short lastRecords = BleDataUtil.bytes2Short(records);
			// 写入当前block的firstSeq
			firstSeq = BleDataUtil.longto5Bytes(lastFirstSeq + lastRecords);
			for (int i = 0; i < 5; i++) {
				blockHead.put(i + 3, firstSeq[i]);
			}
		} else {
			dataBeginTime = fileFrameDatas.get(0).date;
			dataEndTime = fileFrameDatas.get(fileFrameDatas.size() - 1).date;
			byte[] firstSeq = BleDataUtil.longto5Bytes(lastBlockPosition);
			for (int i = 0; i < 5; i++) {
				blockHead.put(i + 3, firstSeq[i]);
			}
		}
		lastBlockPosition = bufferPosition;
		mbb = fileChannel.map(FileChannel.MapMode.READ_WRITE, bufferPosition,
				blockHead.capacity());
		int points = waitWrite.size() * 3;
		byte[] records = BleDataUtil.short2ByteArray((short) (points));
		blockHead.put(8, records[0]);
		blockHead.put(9, records[1]);
		blockHead.position(0);
		// 开始写入文件头
		while (blockHead.hasRemaining()) {
			mbb.put(blockHead.get());
		}
		bufferPosition += blockHead.capacity();
		writeRecord(waitWrite);
		if (nWrite != null && nWrite.size() > 0) {
			writeBlock(nWrite);
		}

	}

	/**
	 * 写入记录
	 * 
	 * @param fileFrameDatas
	 * @throws IOException
	 */
	private void writeRecord(List<FileFrameData> fileFrameDatas)
			throws IOException {
		/**
		 * 检查TLV中的Length是否放得下TL占3字节，剩下部分写数据
		 */
		int records = (int) Math
				.ceil((fileFrameDatas.size() * 6f + 3) / (0xFFFF));
		List<FileFrameData> waitWrite = null;
		List<FileFrameData> nWrite = null;
		if (records == 1) {
			waitWrite = fileFrameDatas;
		} else {
			waitWrite = new ArrayList<FileFrameData>();
			int maxSize = (int) Math.floor((0xFFFF - 3f) / (6f));
			for (int i = 0; i < maxSize; i++) {
				waitWrite.add(fileFrameDatas.get(i));
			}
			nWrite = new ArrayList<FileFrameData>();
			for (int i = maxSize; i < fileFrameDatas.size(); i++) {
				nWrite.add(fileFrameDatas.get(i));
			}
		}
		// 当前记录所需大小为要写入ecg电压值的字节数+TL的长度
		int length = waitWrite.size() * 6 + 3;
		MappedByteBuffer mbb = fileChannel.map(FileChannel.MapMode.READ_WRITE,
				bufferPosition, length);
		// 写入记录头
		// 0x00=ECG数据 0x01=Date 0x02=记步数据 0x03-0xFE=保留 0xFF=结束记录，本Record 比较特殊，
		// 没有Length 及Value字段
		byte type = 0x00;
		mbb.put(type);
		// 写入本记录的长度（包括头）
		byte[] lengths = BleDataUtil.short2ByteArray((short) length);
		for (int i = 0; i < 2; i++) {
			mbb.put(lengths[i]);
		}
		// 开始写入点
		for (int i = 0; i < waitWrite.size(); i++) {
			FileFrameData data = fileFrameDatas.get(i);
			// 写入第一通道点
			byte[] chs = BleDataUtil.short2ByteArray(data.ch1);
			for (int j = 0; j < 2; j++) {
				mbb.put(chs[j]);
			}
			// 写入第二通道点
			chs = BleDataUtil.short2ByteArray(data.ch2);
			for (int j = 0; j < 2; j++) {
				mbb.put(chs[j]);
			}
			// 写入第三通道点
			chs = BleDataUtil.short2ByteArray(data.ch3);
			for (int j = 0; j < 2; j++) {
				mbb.put(chs[j]);
			}
		}
		bufferPosition += mbb.capacity();
		lastRecordEndPosition = bufferPosition;
		if (nWrite != null && nWrite.size() > 0) {
			writeRecord(nWrite);
		}
	}

	/**
	 * 结束文件写入
	 * 
	 * @throws Exception
	 */
	public void endWriteFile() throws Exception {
		if (fileStatus != ECGFileStatus.WRITEBLOCK) {
			throw new Exception("文件状态不对：" + fileStatus);
		}
		randomAccessFile.close();
		if (!isRealTimeFile) {
			// 存入队列
			boolean ret = DataManager.saveUploadFile(ecgFile.getName(),
					ecgFile.getAbsolutePath(), dataBeginTime, dataEndTime,
					FileType.Default);
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, ecgFile.getName()
						+ (ret ? "存入数据库成功" : "存入数据库失败"));
			}
		}
		fileStatus = ECGFileStatus.NONE;
		bufferPosition = 0;
		if (ConstantConfig.Debug) {
			LogUtil.d(TAG, "endWriteFile");
		}
	}

	/**
	 * 反对当前生成的ecg文件
	 * 
	 * @return
	 */
	public File getECGFile() {
		return ecgFile;
	}

	private void test(Context context) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat(
				ConstantConfig.DATA_DATE_FORMAT);
		File ecgFile = new File(FileUtil.ECG_BATCHDIR + sdf.format(new Date())
				+ ConstantConfig.ECGDATA_SUFFIX);
		RandomAccessFile raf = new RandomAccessFile(ecgFile, "rw");
		FileChannel fc = raf.getChannel();
		MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, 10);
		mbb.put(0, (byte) 1);
		mbb.put(9, (byte) 10);
		mbb = fc.map(FileChannel.MapMode.READ_WRITE, 9, 10);
		mbb.put(0, (byte) 11);
		mbb.put(9, (byte) 20);
		raf.close();
	}
}
