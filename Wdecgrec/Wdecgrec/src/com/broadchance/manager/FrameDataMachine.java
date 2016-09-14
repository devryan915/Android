/**
 * 
 */
package com.broadchance.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import com.broadchance.entity.FileFrameData;
import com.broadchance.entity.FrameData;
import com.broadchance.entity.FrameType;
import com.broadchance.entity.HeartRate;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FilterUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.services.BleDomainService;
import com.broadchance.wdecgrec.services.BluetoothLeService;

/**
 * @author ryan.wang
 * 
 */
public class FrameDataMachine {
	private final static String TAG = FrameDataMachine.class.getSimpleName();
	/**
	 * 第一通道电极脱落
	 */
	public final static String ACTION_ECGMII_DATAOFF_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ECGMII_DATAOFF_AVAILABLE";
	public final static String ACTION_ECGMII_DATAON_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ECGMII_DATAON_AVAILABLE";
	/**
	 * 第二通道电极脱落
	 */
	public final static String ACTION_ECGMV1_DATAOFF_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ECGMV1_DATAOFF_AVAILABLE";
	/**
	 * 第三通道电极脱落
	 */
	public final static String ACTION_ECGMV5_DATAOFF_AVAILABLE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_ECGMV5_DATAOFF_AVAILABLE";
	private static FrameDataMachine Instance = new FrameDataMachine();

	public static FrameDataMachine getInstance() {
		return Instance;
	}

	private FrameDataMachine() {

	}

	/**
	 * 第一通道电压值
	 */
	private LinkedBlockingQueue<Integer> miiQueue = new LinkedBlockingQueue<Integer>();
	/**
	 * 第二通道电压值
	 */
	private LinkedBlockingQueue<Integer> mv1Queue = new LinkedBlockingQueue<Integer>();
	/**
	 * 第三通道电压值
	 */
	private LinkedBlockingQueue<Integer> mv5Queue = new LinkedBlockingQueue<Integer>();

	/**
	 * 第一通道上一帧
	 */
	private FrameData lastMIIFrameData = null;
	/**
	 * 第二通道上一帧
	 */
	private FrameData lastMV1FrameData = null;
	/**
	 * 第三通道上一帧
	 */
	private FrameData lastMV5FrameData = null;
	/**
	 * 暂存三通道数据
	 */
	LinkedBlockingQueue<FrameData> miiFrameDatas = new LinkedBlockingQueue<FrameData>();
	LinkedBlockingQueue<FrameData> mv1FrameDatas = new LinkedBlockingQueue<FrameData>();
	LinkedBlockingQueue<FrameData> mv5FrameDatas = new LinkedBlockingQueue<FrameData>();
	/**
	 * 写入文件使用的ecg点数据
	 */
	private LinkedBlockingQueue<FileFrameData> fileFrameDatas = new LinkedBlockingQueue<FileFrameData>();
	/**
	 * 呼吸波数据
	 */
	private LinkedBlockingQueue<Short> fileBreathDatas = new LinkedBlockingQueue<Short>();
	private LinkedBlockingQueue<Short> fileRealTimeBreathDatas = new LinkedBlockingQueue<Short>();

	/**
	 * 心率
	 */
	private LinkedBlockingQueue<HeartRate> heartRateDatas = new LinkedBlockingQueue<HeartRate>();
	/**
	 * 实时心率
	 */
	private LinkedBlockingQueue<HeartRate> heartRealTimeRateDatas = new LinkedBlockingQueue<HeartRate>();

	/**
	 * 供实时上传使用
	 */
	private LinkedBlockingQueue<FileFrameData> fileRealTimeFrameDatas = new LinkedBlockingQueue<FileFrameData>();
	/**
	 * 实时模式将填充实时数据
	 */
	private boolean isRealTimeMode = false;
	/**
	 * 是否当三通道数据第一次都获取到数据
	 */
	private boolean isNeedCheckFrame = false;
	/**
	 * 设备频率(单通道) 125点/s 12.5HZ
	 */
	private final static float FRAME_FREQUENCY = 12.5f;
	/**
	 * 每帧ecg点数
	 */
	private final static int FRAME_PERFRAME_ECGDOTS = 10;
	/**
	 * 每s可以获取的ecg点数，现在是125点/s
	 */
	public final static float FRAME_DOTS_FREQUENCY = FRAME_FREQUENCY
			* FRAME_PERFRAME_ECGDOTS;
	/**
	 * 一帧所占时间ms
	 */
	private final static int ONEFRAME_TIME = (int) (1000 / FRAME_FREQUENCY);
	private final static int ONEFRAMEPOINT_TIME = (int) (ONEFRAME_TIME * 0.1f);
	private final static int FRAME_SEQMAXLENGTH = 256;
	/**
	 * 一个序号周期时间
	 */
	private final static float FRAME_PERIOD = FRAME_SEQMAXLENGTH
			/ FRAME_FREQUENCY;

	/**
	 * 批量上传数据触发上限
	 */
	private static int MAX_BATCH_LIMIT = Integer.MAX_VALUE;
	/**
	 * 批量上传60s的数据
	 */
	private final static int MAX_BATCH_LIMIT60 = (int) (FRAME_DOTS_FREQUENCY * 60);
	/**
	 * 实时上传数据量触发上限4s的数据
	 */
	private final static int MAX_REAL_LIMIT = (int) (FRAME_DOTS_FREQUENCY * 3.84);

	private Float power = null;
	private float temperature = 0;
	/**
	 * 最近一帧是否脱落
	 */
	// private boolean isFallOff = false;
	/**
	 * 最近一帧，帧类型
	 */
	private FrameType frameType = FrameType.MII;

	// public boolean isFallOff() {
	// return isFallOff;
	// }
	//
	//
	// public FrameType getFrameType() {
	// return frameType;
	// }

	/**
	 * 添加心率
	 * 
	 * @param rate
	 */
	public void addHeartRate(HeartRate rate) {
		synchronized (heartRateDatas) {
			try {
				heartRateDatas.put(rate);
				// if (isRealTimeMode) {
				// heartRealTimeRateDatas.put(rate);
				// }
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取实时心率
	 * 
	 * @return
	 */
	// public Integer getHeartRealTimeRate() {
	// HeartRate rate = heartRealTimeRateDatas.poll();
	// return rate != null ? rate.heart : null;
	// }

	/**
	 * 获取心率
	 * 
	 * @return
	 */
	public Integer getHeartRate() {
		synchronized (heartRateDatas) {
			HeartRate rate = heartRateDatas.poll();
			return rate != null ? rate.heart : null;
		}
	}

	public Float getPower() {
		return power;
	}

	public float getTemperature() {
		return temperature;
	}

	public void startRealTimeMode() {
		isRealTimeMode = true;
	}

	public void endRealTimeMode() {
		isRealTimeMode = false;
		synchronized (fileRealTimeFrameDatas) {
			fileRealTimeFrameDatas.clear();
		}
		synchronized (fileRealTimeBreathDatas) {
			fileRealTimeBreathDatas.clear();
		}
	}

	/**
	 * 获取实时数据
	 * 
	 * @return
	 */
	public FileFrameData getRealTimeFrameData() {
		if (isRealTimeMode) {
			synchronized (fileRealTimeFrameDatas) {
				if (fileRealTimeFrameDatas.size() > 0) {
					FileFrameData fdata = null;
					try {
						fdata = fileRealTimeFrameDatas.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
						return null;
					}
					// 返回滤波后的数据
					fdata.ch1 = (short) FilterUtil.Instance
							.getECGDataIIR(new int[] { fdata.ch1 })[0];
					// fdata.ch2 = (short) FilterUtil.Instance
					// .getECGDataV1R(new int[] { fdata.ch2 })[0];
					// fdata.ch3 = (short) FilterUtil.Instance
					// .getECGDataV5R(new int[] { fdata.ch3 })[0];
					return fdata;
				}
			}
		}
		return null;
	}

	public Short getRealTimeBreathData() {
		if (isRealTimeMode) {
			synchronized (fileRealTimeBreathDatas) {
				if (fileRealTimeBreathDatas.size() > 0)
					try {
						return fileRealTimeBreathDatas.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
		}
		return null;
	}

	/**
	 * 构建补帧
	 * 
	 * @param frameType
	 * @param seq
	 * @return
	 */
	private byte[] buildFrameData(FrameType frameType, byte seq) {
		byte[] frameData = new byte[20];
		if (frameType == FrameType.MII) {
			frameData[0] = (byte) 0x83;
			frameData[1] = seq;
		} else if (frameType == FrameType.MV1) {
			frameData[0] = (byte) 0x93;
			frameData[1] = seq;
		} else if (frameType == FrameType.MV5) {
			frameData[0] = (byte) 0xA3;
			frameData[1] = seq;
		}
		for (int i = 2; i < frameData.length; i++) {
			frameData[i] = 0;
		}
		return frameData;
	}

	/**
	 * 取得三通道的点数据
	 * 
	 * @return
	 */
	public FileFrameData getFileFrameData() {
		synchronized (fileFrameDatas) {
			if (fileFrameDatas.size() > 0)
				return fileFrameDatas.poll();
		}
		return null;
	}

	/**
	 * 获取呼吸波数据
	 * 
	 * @return
	 */
	public Short getFileBreathData() {
		synchronized (fileBreathDatas) {
			if (fileBreathDatas.size() > 0)
				return fileBreathDatas.poll();
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getMII() {
		synchronized (miiQueue) {
			if (miiQueue.size() > 0)
				return miiQueue.poll();
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getMV1() {
		synchronized (mv1Queue) {
			if (mv1Queue.size() > 0)
				return mv1Queue.poll();
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getMV5() {
		synchronized (mv5Queue) {
			if (mv5Queue.size() > 0)
				return mv5Queue.poll();
		}
		return null;
	}

	/**
	 * 将点数据添加到队列供UI画图读取
	 * 
	 * @param frameType
	 * @param datas
	 */
	private void addFramePoint(FrameType frameType, short[] datas) {
		if (frameType == FrameType.MII) {
			synchronized (miiQueue) {
				for (int data : datas) {
					miiQueue.offer(data);
				}
			}
		} else if (frameType == FrameType.MV1) {
			synchronized (mv1Queue) {
				for (int data : datas) {
					mv1Queue.offer(data);
				}
			}
		} else if (frameType == FrameType.MV5) {
			synchronized (mv5Queue) {
				for (int data : datas) {
					mv5Queue.offer(data);
				}
			}
		}
	}

	private void addFileFrameData(FrameData miiData, FrameData mv1Data,
			FrameData mv5Data) throws Exception {
		short[] miiPoints = miiData.getFramePoints();
		// short[] mv1Points = mv1Data.getFramePoints();
		// short[] mv5Points = mv5Data.getFramePoints();
		int length = miiPoints.length;
		// 由于三通道数据不是同时收到，对于三通道的数据点时间，
		// 默认第一通道时间，此误差不超过一个帧间隔时间
		// Date date = miiData.date;
		Calendar cal = Calendar.getInstance();
		cal.setTime(miiData.date);

		// 将点按照顺序放入队列
		for (int i = 0; i < length; i++) {
			FileFrameData fileFrameData = new FileFrameData();
			fileFrameData.ch1 = miiPoints[i];
			// fileFrameData.ch2 = mv1Points[i];
			// fileFrameData.ch3 = mv5Points[i];
			if (i > 0) {
				// 每一个点间隔8ms
				cal.add(Calendar.MILLISECOND, ONEFRAMEPOINT_TIME);
			}
			fileFrameData.date = cal.getTime();
			fileFrameDatas.offer(fileFrameData);
			if (isRealTimeMode) {
				synchronized (fileRealTimeFrameDatas) {
					fileRealTimeFrameDatas.offer(fileFrameData);
				}
			}
		}
	}

	/**
	 * 此方法没有同步miiFrameDatas mv1FrameDatas mv5FrameDatas 所以在其读写之后操作
	 * 
	 * @param isNeedCheckFrame
	 *            是否需要补帧
	 */
	private void processFileFrameData(boolean isCheck) {
		synchronized (fileFrameDatas) {
			// while (miiFrameDatas.size() > 0 && mv1FrameDatas.size() > 0
			// && mv5FrameDatas.size() > 0) {
			while (miiFrameDatas.size() > 0) {
				FrameData miiData = miiFrameDatas.poll();
				// FrameData mv1Data = mv1FrameDatas.poll();
				// FrameData mv5Data = mv5FrameDatas.poll();
				try {
					// 暂时不检查
					/****
					 * if (isCheck) { FrameData dataDate1 = null; FrameData
					 * dataDate2 = null; FrameData dataDate3 = null; //
					 * 按照时间排序，参照最早时间帧，后续帧补帧 if
					 * (miiData.date.before(mv1Data.date)) { if
					 * (miiData.date.before(mv5Data.date)) { dataDate1 =
					 * miiData; if (mv5Data.date.before(mv1Data.date)) {
					 * dataDate2 = mv5Data; dataDate3 = mv1Data; } else {
					 * dataDate2 = mv1Data; dataDate3 = mv5Data; } } else {
					 * dataDate2 = miiData; dataDate1 = mv5Data; dataDate3 =
					 * mv1Data; } } else { if
					 * (mv5Data.date.before(mv1Data.date)) { dataDate1 =
					 * mv5Data; dataDate2 = mv1Data; dataDate3 = miiData; } else
					 * { dataDate1 = mv1Data; if
					 * (mv5Data.date.before(miiData.date)) { dataDate2 =
					 * mv5Data; dataDate3 = miiData; } else { dataDate2 =
					 * miiData; dataDate3 = mv5Data; } } } //
					 * 如果序号一致(其实需要保证flash中的三通道数据position相同) if (miiData.getSeq()
					 * == mv1Data.getSeq() && mv1Data.getSeq() ==
					 * mv5Data.getSeq()) {
					 * 
					 * // 三通道的数据收到的时间差理论不超过单通道一帧的时间 此处按照一个序列周期算 long diffTime =
					 * 0; // diffTime = ONEFRAME_TIME; diffTime = (long)
					 * (FRAME_PERIOD 1000); if (dataDate3.date.getTime() -
					 * dataDate1.date.getTime() <= diffTime) { short[] miiPoints
					 * = miiData.getFramePoints(); short[] mv1Points =
					 * mv1Data.getFramePoints(); short[] mv5Points =
					 * mv5Data.getFramePoints(); // miiData.getBreathPoints();
					 * // 将点按照顺序放入队列 for (int i = 0; i < mv5Points.length; i++)
					 * { FileFrameData fileFrameData = new FileFrameData();
					 * fileFrameData.ch1 = miiPoints[i]; fileFrameData.ch2 =
					 * mv1Points[i]; fileFrameData.ch3 = mv5Points[i];
					 * fileFrameDatas.offer(fileFrameData); } } else {
					 * 
					 * } } else {// 如果序号不等，可能三通道中的某一通道在第一次就收到蓝牙数据时存在丢帧 int
					 * maxSeq = Math.max(Math.max(miiData.getSeq(),
					 * mv1Data.getSeq()), mv5Data.getSeq()); } } else {
					 * 
					 * short[] miiPoints = miiData.getFramePoints(); // short[]
					 * mv1Points = mv1Data.getFramePoints(); // short[]
					 * mv5Points = mv5Data.getFramePoints(); // 将点按照顺序放入队列 for
					 * (int i = 0; i < miiPoints.length; i++) { FileFrameData
					 * fileFrameData = new FileFrameData(); fileFrameData.ch1 =
					 * miiPoints[i]; // fileFrameData.ch2 = mv1Points[i]; //
					 * fileFrameData.ch3 = mv5Points[i]; fileFrameData.date =
					 * miiData.date; fileFrameDatas.offer(fileFrameData); } }
					 ****/
					// addFileFrameData(miiData, mv1Data, mv5Data);
					addFileFrameData(miiData, null, null);

					/**
					 * 呼吸波数据入队列
					 */
					short[] breathPoints = miiData.getBreathPoints();
					if (isRealTimeMode) {
						synchronized (fileRealTimeBreathDatas) {
							fileRealTimeBreathDatas.offer(breathPoints[0]);
							fileRealTimeBreathDatas.offer(breathPoints[1]);
						}
					}
					fileBreathDatas.add(breathPoints[0]);
					fileBreathDatas.add(breathPoints[1]);
					if (isRealTimeMode
							&& (fileRealTimeFrameDatas.size() > MAX_REAL_LIMIT)) {
						List<FileFrameData> fileFrmDatas = new ArrayList<FileFrameData>();
						List<Short> fileBrthDatas = new ArrayList<Short>();
						Short bdata = null;
						FileFrameData fileFrameData = null;
						int count = 0;
						while (count < MAX_REAL_LIMIT) {
							count++;
							fileFrameData = fileRealTimeFrameDatas.poll();
							fileFrmDatas.add(fileFrameData);
						}
						count = 0;
						int breathLimit = MAX_REAL_LIMIT / 5;
						while (count < breathLimit) {
							count++;
							bdata = fileRealTimeBreathDatas.poll();
							fileBrthDatas.add(bdata);
						}
						count = 0;
						JSONArray jHeartRateArray = new JSONArray();
						/*
						 * int heartLimit = heartRealTimeRateDatas.size() - 1;
						 * if (heartLimit > 0) { while (count < heartLimit) {
						 * JSONObject rate = new JSONObject(); try { Integer hr
						 * = getHeartRealTimeRate(); if (hr == null) break;
						 * rate.put("hr", hr); } catch (JSONException e) {
						 * e.printStackTrace(); } count++;
						 * jHeartRateArray.put(rate); } }
						 */
						Integer hr = FilterUtil.Instance.getHeartRate();
						JSONObject rate = new JSONObject();
						rate.put("hr", hr);
						jHeartRateArray.put(rate);
						BleDomainService.startRealTimeMode(fileFrmDatas,
								fileBrthDatas, jHeartRateArray);
					}
					// 心电数据达到上限
					if (fileFrameDatas.size() > MAX_BATCH_LIMIT) {
						List<FileFrameData> fileFrmDatas = new ArrayList<FileFrameData>();
						List<Short> fileBrthDatas = new ArrayList<Short>();
						Short bdata = null;
						FileFrameData fileFrameData = null;
						int count = 0;
						while (count < MAX_BATCH_LIMIT) {
							count++;
							fileFrameData = fileFrameDatas.poll();
							fileFrmDatas.add(fileFrameData);
						}
						count = 0;
						int breathLimit = MAX_BATCH_LIMIT / 5;
						while (count < breathLimit) {
							count++;
							bdata = fileBreathDatas.poll();
							fileBrthDatas.add(bdata);
						}
						BleDomainService.writeECGData2File(fileFrmDatas,
								fileBrthDatas);
						if (MAX_BATCH_LIMIT < MAX_BATCH_LIMIT60) {
							MAX_BATCH_LIMIT = MAX_BATCH_LIMIT60;
						}
					}
				} catch (Exception e) {
					LogUtil.e(TAG, e);
				}
			}

		}
	}

	public void resetData() {
		synchronized (miiQueue) {
			miiQueue.clear();
		}
		synchronized (mv1Queue) {
			miiQueue.clear();
		}
		synchronized (mv5Queue) {
			miiQueue.clear();
		}
		synchronized (fileFrameDatas) {
			fileFrameDatas.clear();
		}
		synchronized (fileBreathDatas) {
			fileBreathDatas.clear();
		}
		lastMIIFrameData = null;
		lastMV1FrameData = null;
		lastMV5FrameData = null;
		MAX_BATCH_LIMIT = Integer.MAX_VALUE;
	}

	// private void testData(FrameData data) throws Exception {
	// 因为设备暂时只有一通道数据为了测试，将一通道的数据填充到二三通道
	// if (ConstantConfig.Debug) {
	// mv1FrameDatas.offer(data);
	// mv5FrameDatas.offer(data);
	// addFramePoint(FrameType.MV1, data.getFramePoints());
	// addFramePoint(FrameType.MV5, data.getFramePoints());
	// processFileFrameData(false);
	// }
	// }

	/**
	 * 处理帧数据
	 * 
	 * @param frameData
	 */
	public void processFrameData(FrameData frameData) {
		try {
			// 取消非空和长度检查以减少不必要运算
			// if(frameData==null)
			FrameData lastFrameData = null;
			Queue<FrameData> frameDatas = null;
			frameData.parseData();
			// BleDataUtil.logEcg(frameData.getFrameData());
			frameType = frameData.getFrameType();
			boolean isECG = true;
			if (frameType == FrameType.MII) {
				lastFrameData = lastMIIFrameData;
				frameDatas = miiFrameDatas;
			} else if (frameType == FrameType.MV1) {
				lastFrameData = lastMV1FrameData;
				frameDatas = mv1FrameDatas;
			} else if (frameType == FrameType.MV5) {
				lastFrameData = lastMV5FrameData;
				frameDatas = mv5FrameDatas;
			} else {
				isECG = false;
				if (frameType == FrameType.POWER) {
					float curPower = frameData.getPower();
					if (power == null || (power != null && curPower != power)) {
						Intent intent = new Intent(
								BluetoothLeService.ACTION_GATT_POWERCHANGED);
						// intent.putExtra(BluetoothLeService.EXTRA_DATA,
						// curPower);
						UIUtil.sendBroadcast(intent);
					}
					// if (power == 0) {
					power = curPower;
					// } else {
					// power = curPower * 0.1f + power * 0.9f;
					// }
					if (temperature == 0) {
						temperature = frameData.getTemperature();
					} else {
						temperature = frameData.getTemperature() * 0.1f
								+ temperature * 0.9f;
					}

				}
			}
			if (isECG) {
				// 补帧
				// lastFrameData = null;
				if (lastFrameData != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(lastFrameData.date);
					// 由于设备序号间隔两所以为了保证序号，将序号除以2
					// 判断补帧 FrameData内检查错位补零
					int diff = (frameData.getSeq() - lastFrameData.getSeq()) / 2;
					// 得到一个周期内相差的序号
					int seqDifference = diff >= 0 ? diff
							: (FRAME_SEQMAXLENGTH / 2 + diff);
					int period = (int) ((frameData.date.getTime() - lastFrameData.date
							.getTime()) / (FRAME_PERIOD * 1000));
					// 补帧 序号/2保证连续
					int frameCount = FRAME_SEQMAXLENGTH / 2 * period
							+ seqDifference - 1;
					if (ConstantConfig.Debug && frameCount > 0) {
						LogUtil.d(TAG, "补了" + frameCount + "帧");
						// UIUtil.showToast("补了" + frameCount + "帧");
					}
					for (int i = 0; i < frameCount; i++) {
						byte[] byteData = buildFrameData(frameType,
								(byte) (lastFrameData.getSeq() + 2 + i));
						cal.add(Calendar.MILLISECOND, ONEFRAME_TIME);
						FrameData data = new FrameData(byteData, cal.getTime());
						data.parseData();
						frameDatas.offer(data);
						// testData(data);
						addFramePoint(frameType, data.getFramePoints());
						// BleDataUtil.logEcg(byteData);
					}

					// 由于心电设备发送的数据间隔时间不是严格的80ms一帧，所以在保证数据连续的情况下，将时间间隔调整为严格80ms时间
					cal.add(Calendar.MILLISECOND, ONEFRAME_TIME);
					frameData.date = cal.getTime();
				} else {
					// 调整第一帧数据时间将毫秒调整为8的整数倍。
					long frameTime = frameData.date.getTime();
					long lTime = frameTime % 8;
					frameTime += 8 - lTime;
					frameData.date = new Date(frameTime);
					Calendar cal = Calendar.getInstance();
					cal.setTime(frameData.date);
					cal.add(Calendar.MINUTE, 1);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					// 计算距离下一个整分会产生多少个点
					MAX_BATCH_LIMIT = (int) ((cal.getTime().getTime() - frameData.date
							.getTime()) / 8);
				}
				// 当前帧
				frameDatas.offer(frameData);
				if (frameData.date == null) {
					LogUtil.e(TAG, new Exception("日期为空"));
				}
				// testData(frameData);
				addFramePoint(frameType, frameData.getFramePoints());
				// BleDataUtil.logEcg(frameData.getFrameData());
				// 为了审核，先不限定三通道
				// if (!isNeedCheckFrame) {
				// boolean isFirst = false;
				// // 判断是否第一次三通道数据都收到了
				// isFirst = lastMIIFrameData == null
				// || lastMV1FrameData == null
				// || lastMV5FrameData == null;
				if (frameType == FrameType.MII) {
					lastMIIFrameData = frameData;
				} else if (frameType == FrameType.MV1) {
					lastMV1FrameData = frameData;
				} else if (frameType == FrameType.MV5) {
					lastMV5FrameData = frameData;
				}
				// isFirst = isNeedCheckFrame
				// && (lastMIIFrameData != null
				// || lastMV1FrameData != null || lastMV5FrameData != null);
				// if (isFirst) {
				// processFileFrameData(true);
				// isNeedCheckFrame = true;
				// }
				// } else {
				processFileFrameData(false);
				// }
			}
		} catch (Exception e) {
			LogUtil.e(TAG, e);
		}
	}
}
