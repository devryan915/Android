/**
 * 
 */
package com.broadchance.wdecgrec.services;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.broadchance.entity.FileFrameData;
import com.broadchance.entity.FileType;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.UploadFile;
import com.broadchance.entity.UploadFileResponse;
import com.broadchance.entity.UploadFileStatus;
import com.broadchance.entity.UploadWay;
import com.broadchance.entity.serverentity.UpLoadData;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.FrameDataMachine;
import com.broadchance.manager.PreferencesManager;
import com.broadchance.manager.SettingsManager;
import com.broadchance.utils.ClientGameService;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FileUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.utils.NetUtil;
import com.broadchance.utils.UIUtil;
import com.broadchance.utils.ZipUtil;
import com.broadchance.wdecgrec.HttpReqCallBack;

/**
 * @author ryan.wang
 * 
 */
public class BleDomainService extends Service {
	private static final String TAG = BleDomainService.class.getSimpleName();

	public final static String ACTION_UPLOAD_STARTREALMODE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_UPLOAD_STARTREALMODE";
	public final static String ACTION_UPLOAD_ENDREALMODE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_UPLOAD_ENDREALMODE";

	public final static String ACTION_UPLOAD_STARTONEKEYMODE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_UPLOAD_STARTONEKEYMODE";
	public final static String ACTION_UPLOAD_ENDONEKEYMODE = ConstantConfig.ACTION_PREFIX
			+ "ACTION_UPLOAD_ENDONEKEYMODE";

	public final static String ACTION_UPLOAD_UPLOADCHANGED = ConstantConfig.ACTION_PREFIX
			+ "ACTION_UPLOAD_UPLOADCHANGED";

	private Timer pkgFrameDataTimer = new Timer();
	private TimerTask pkgFrameDataTask;
	private boolean isRealMode = false;
	// private boolean isOneKeyMode = false;
	private boolean isUploading = false;
	private Object objLock = new Object();
	private Object objLockUpload = new Object();
	private String msg;
	private int uploadedFileRetryTimes;
	List<UploadFile> waitUploadFiles;
	/**
	 * 正在进行的上传方式
	 */
	UploadWay curUploadWay;
	/**
	 * 即将进行的上传方式
	 */
	UploadWay nUploadWay = UploadWay.Batch;
	/**
	 * 限定每次打包个数
	 */
	private final static int LIMIT_UPLOADBATCH_COUNT = 5;
	/**
	 * 上传尝试次数
	 */
	private final static int UPLOAD_RETRYTIMES = 3;

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BleDomainService.ACTION_UPLOAD_STARTREALMODE.equals(action)) {
				FrameDataMachine.getInstance().startRealTimeMode();
				isRealMode = true;
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "开启实时上传");
				}
				startRealTimeMode();
			} else if (BleDomainService.ACTION_UPLOAD_ENDREALMODE
					.equals(action)) {
				isRealMode = false;
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "结束实时上传");
				}
				FrameDataMachine.getInstance().endRealTimeMode();
			} else if (BleDomainService.ACTION_UPLOAD_STARTONEKEYMODE
					.equals(action)) {
				if (nUploadWay != UploadWay.OneKey) {
					nUploadWay = UploadWay.OneKey;
					if (ConstantConfig.Debug) {
						LogUtil.d(TAG, "开启一键上传");
					}
					new AsyncTask<Void, Void, String>() {
						@Override
						protected void onPostExecute(String result) {
							startUpload();
							super.onPostExecute(result);
						}

						@Override
						protected String doInBackground(Void... params) {
							initOneKeyUpload();
							return null;
						}

					}.execute();

				}
			} else if (BleDomainService.ACTION_UPLOAD_ENDONEKEYMODE
					.equals(action)) {
				nUploadWay = UploadWay.Batch;
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "结束一键上传");
				}
			}
		}
	};

	/**
	 * 初始化一键上传，扫描目录，并同步数据库
	 */
	private void initOneKeyUpload() {
		String dir = FileUtil.getEcgDir();
		if (dir == null) {
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, "ecg目录不存在:" + dir);
			}
			return;
		}
		File ecgDir = new File(dir);
		File[] files = ecgDir.listFiles();
		if (files != null) {
			int count = 0;
			for (File file : files) {
				if (!DataManager.isUploadFileExist(file.getName())) {
					DataManager.saveUploadFile(file.getName(),
							file.getAbsolutePath(), new Date(), new Date(),
							FileType.Supplement);
					count++;
				}
			}
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, "从本地恢复:" + count + "到数据库");
			}
		}
	}

	/**
	 * 根据时间抓取数据
	 * 
	 * @param date
	 * @param limit
	 * @return
	 */
	private List<UploadFile> getUploadFile(Date date, int limit) {
		synchronized (objLock) {
			List<UploadFile> uploadFiles = DataManager.getUploadFile(date,
					limit);
			boolean retBoolean = DataManager.updateUploadFileStatus(
					uploadFiles, UploadFileStatus.Uploading);
			return retBoolean ? uploadFiles : null;
		}
	}

	private int getCountByStatus(UploadFileStatus status) {
		int count = 0;
		if (waitUploadFiles == null)
			return count;
		for (UploadFile uploadedFile : waitUploadFiles) {
			if (uploadedFile.getStatus() == status) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 将上传中的文件重置状态
	 * 
	 * @param status
	 *            限定UploadFileStatus.UploadFailed UploadFileStatus.Uploaded
	 */
	private void setUploadByStatus(UploadFileStatus status) {
		if (waitUploadFiles == null)
			return;
		for (UploadFile uploadedFile : waitUploadFiles) {
			if (uploadedFile.getStatus() == UploadFileStatus.Uploading) {
				uploadedFile.setStatus(status);
			}
		}
	}

	private void sendUploadBroadCast(int uploadedCount, int totalCount) {
		int count = getCountByStatus(UploadFileStatus.Uploaded);
		int totalWaitUpload = waitUploadFiles != null ? waitUploadFiles.size()
				: 0;
		Intent intent = new Intent(ACTION_UPLOAD_UPLOADCHANGED);
		intent.putExtra(BluetoothLeService.EXTRA_DATA, uploadedCount + ":"
				+ totalCount);
		sendBroadcast(intent);
	}

	private void uploadECGFile() {
		try {
			List<File> files = null;
			List<UpLoadData> upLoadDatas = null;
			String zipPath = FileUtil.ECG_BATCH_UPLOADDIR + "uploadData.zip";
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss.SSS");
			String devID = SettingsManager.getInstance().getDeviceNumber();
			files = new ArrayList<File>();
			upLoadDatas = new ArrayList<UpLoadData>();
			int count = 0;
			for (int i = 0; i < waitUploadFiles.size(); i++) {
				UploadFile uploadFile = waitUploadFiles.get(i);
				if (uploadFile.getStatus() == UploadFileStatus.Uploaded
						|| uploadFile.getStatus() == UploadFileStatus.Uploading) {
					continue;
				}
				File file = new File(uploadFile.getPath());
				if (file.exists()) {
					count++;
					// 限定一次传三个
					if (count > LIMIT_UPLOADBATCH_COUNT) {
						break;
					} else {
						uploadFile.setStatus(UploadFileStatus.Uploading);
						files.add(file);
						UpLoadData data = new UpLoadData();
						data.setBeginDT(sdf.format(uploadFile
								.getDataBeginTime()));
						data.setEndDT(sdf.format(uploadFile.getDataEndTime()));
						data.setUserID(DataManager.getUserInfo().getUserID());
						data.setDeviceID(devID);
						data.setFileName(uploadFile.getFileName());
						upLoadDatas.add(data);
					}
				} else {
					if (ConstantConfig.Debug) {
						LogUtil.d(
								TAG,
								"数据库中存在此文件，但是文件目录中找不到："
										+ uploadFile.getFileName());
					}
					uploadFile.setStatus(UploadFileStatus.Uploaded);
					if (curUploadWay == UploadWay.OneKey) {
						sendUploadBroadCast(
								getCountByStatus(UploadFileStatus.Uploaded),
								waitUploadFiles != null ? waitUploadFiles
										.size() : 0);
					}
					DataManager.deleteUploadFile(uploadFile.getFileName());
				}
			}
			// 无文件可以上传
			if (files.size() < 1) {
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "无文件可以上传");
				}
				endUpload();
				return;
			}
			ZipUtil.zipFiles(files, zipPath);
			File zipFile = new File(zipPath);
			final String zipFileName;
			if (zipFile.exists()) {
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "正在上传" + zipFile.getAbsolutePath());
				}
				zipFileName = zipFile.getAbsolutePath();
				String desDataJson = JSON.toJSONString(upLoadDatas);
				/**
				 * 批量
				 */
				new ClientGameService().uploadFile(zipFile, desDataJson,
						curUploadWay,
						new HttpReqCallBack<UploadFileResponse>() {
							@Override
							public Activity getReqActivity() {
								return null;
							}

							@Override
							public void doSuccess(UploadFileResponse result) {
								if (result.isOk()) {
									if (curUploadWay == UploadWay.OneKey) {
										sendUploadBroadCast(
												getCountByStatus(UploadFileStatus.Uploaded),
												waitUploadFiles != null ? waitUploadFiles
														.size() : 0);
									}
									if (ConstantConfig.Debug) {
										LogUtil.i(TAG, msg + zipFileName
												+ "，上传成功");
									}
									setUploadByStatus(UploadFileStatus.Uploaded);
									int undeal = getCountByStatus(UploadFileStatus.UnDeal);
									int failed = getCountByStatus(UploadFileStatus.UploadFailed);
									if (undeal + failed > 0) {
										if (ConstantConfig.Debug) {
											LogUtil.d(TAG, msg + "，上传列表中还有 "
													+ undeal + failed
													+ "需要上传，继续上传");
										}
										uploadECGFile();
									} else {
										endUpload();
									}
								} else {
									endUpload();
								}
							}

							@Override
							public void doError(String result) {
								if (ConstantConfig.Debug) {
									LogUtil.d(TAG, msg + "，上传失败 " + result);
								}
								UIUtil.showToast(getApplicationContext(),
										"上传失败!");
								setUploadByStatus(UploadFileStatus.UploadFailed);
								int undeal = getCountByStatus(UploadFileStatus.UnDeal);
								int failed = getCountByStatus(UploadFileStatus.UploadFailed);
								if (undeal + failed > 0
										&& uploadedFileRetryTimes < UPLOAD_RETRYTIMES) {
									if (ConstantConfig.Debug) {
										LogUtil.d(TAG, msg + "，上传列表中还有 "
												+ undeal + failed
												+ "需要上传，尝试重新上传，尝试次数"
												+ uploadedFileRetryTimes);
									}
									// 最多尝试三次如果还是失败放弃上传，等待下次批量上传
									uploadedFileRetryTimes++;
									DataManager
											.updateUploadFileTimes(waitUploadFiles);
									uploadECGFile();
								} else {
									endUpload();
								}
							}
						});
			} else {
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "正在上传的文件不存在" + zipFile.getAbsolutePath());
				}
				setUploadByStatus(UploadFileStatus.UploadFailed);
				// 最多尝试三次如果还是失败放弃上传，等待下次批量上传
				uploadedFileRetryTimes++;
				if (uploadedFileRetryTimes < UPLOAD_RETRYTIMES) {
					if (ConstantConfig.Debug) {
						LogUtil.d(TAG, msg + "，上传失败，尝试重新上传，尝试次数"
								+ uploadedFileRetryTimes);
					}
					DataManager.updateUploadFileTimes(waitUploadFiles);
					uploadECGFile();
				}
			}
		} catch (IOException e) {
			setUploadByStatus(UploadFileStatus.UploadFailed);
			endUpload();
			if (ConstantConfig.Debug) {
				LogUtil.e(TAG + " startUpload", e);
			}
		}
	}

	/**
	 * 结束上传
	 */
	private void endUpload() {
		try {
			if (curUploadWay == UploadWay.OneKey) {
				int totalUploaded = waitUploadFiles != null ? waitUploadFiles
						.size() : 0;
				// 结束上传
				sendUploadBroadCast(totalUploaded, totalUploaded);
			}
			List<UploadFile> failedFile = new ArrayList<UploadFile>();
			if (waitUploadFiles != null && waitUploadFiles.size() > 0) {
				for (UploadFile file : waitUploadFiles) {
					if (file.getStatus() == UploadFileStatus.Uploaded) {
						boolean retDelete = new File(file.getPath()).delete();
						if (!retDelete) {
							if (ConstantConfig.Debug) {
								LogUtil.e(
										TAG,
										new Exception("删除文件失败："
												+ file.getPath()));
							}
						}
						boolean retDeleteDB = DataManager.deleteUploadFile(file
								.getFileName());
						if (!retDeleteDB) {
							if (ConstantConfig.Debug) {
								LogUtil.e(TAG, new Exception("删除数据库记录失败："
										+ file.getFileName()));
							}
						}
					} else if (file.getStatus() == UploadFileStatus.UploadFailed) {
						failedFile.add(file);
					}
				}
			}
			// 回写数据库
			if (failedFile.size() > 0) {
				boolean retFailedStatus = DataManager.updateUploadFileStatus(
						failedFile, UploadFileStatus.UploadFailed);
				if (!retFailedStatus) {
					if (ConstantConfig.Debug) {
						LogUtil.e(TAG, new Exception("更新上传文件失败"));
					}
				}
			}
			waitUploadFiles = null;
		} catch (Exception e) {
		} finally {
			// 如果下一次是一键上传，本次是批次上传，则启动批次上传
			// 为了避免通过广播启动一键上传时正在进行批次上传，导致本次批次上传失效
			// 因此做出此检查，来自动启动上述情况中，自动启动一键上传
			boolean isNeedOneKeyUpload = nUploadWay == UploadWay.OneKey
					&& curUploadWay == UploadWay.Batch;

			resetUpload();
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, msg + "，上传结束 ");
			}
			if (isNeedOneKeyUpload) {
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "自动启动一键上传 ");
				}
				startUpload();
			}
		}
	}

	private void resetUpload() {
		if (ConstantConfig.Debug) {
			LogUtil.d(TAG, curUploadWay == UploadWay.OneKey ? "结束一键上传"
					: "结束批量上传");
		}
		isUploading = false;
		nUploadWay = UploadWay.Batch;
	}

	/**
	 * 批量或者一键上传文件
	 */
	private void startUpload() {
		/**
		 * 是否开启所有网络上传
		 */
		boolean netType = SettingsManager.getInstance().getSettingsNetType();
		// 如果仅限定wifi，检查当前是否Wifi网络，如果不是取消本次上传
		if (!netType && !NetUtil.isWifi()) {
			LogUtil.d(TAG, "当前网络为移动网络，停止上传");
			return;
		}
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user == null) {
			LogUtil.d(TAG, "用户数据不存在");
			return;
		}
		Calendar cal = null;
		// 如果是一键上传，立即做出响应，防止在设置页面，点击一键上传反应慢
		if (nUploadWay == UploadWay.OneKey) {
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 1970);
			List<UploadFile> waitUploadFiles = DataManager.getUploadFile(
					cal.getTime(), -1);
			sendUploadBroadCast(0,
					waitUploadFiles != null ? waitUploadFiles.size() : 0);
		}
		synchronized (objLockUpload) {
			if (isUploading) {
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "文件正在上传中");
				}
				return;
			}
			isUploading = true;
			msg = "";
			curUploadWay = nUploadWay;
			if (curUploadWay == UploadWay.OneKey) {
				msg += "一键上传";
				cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, 1970);
				waitUploadFiles = getUploadFile(cal.getTime(), -1);
				uploadedFileRetryTimes = 0;
				if (waitUploadFiles != null) {
					sendUploadBroadCast(
							getCountByStatus(UploadFileStatus.Uploaded),
							waitUploadFiles.size());
				} else {
					sendUploadBroadCast(0, 0);
					resetUpload();
					return;
				}
			} else {
				msg += "批量上传";
				cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, -1);
				waitUploadFiles = getUploadFile(cal.getTime(), -1);
			}
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, "开始" + msg);
			}
			if (waitUploadFiles != null && waitUploadFiles.size() > 0) {
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "准备" + msg);
				}
				uploadECGFile();
			} else {
				resetUpload();
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, "无文件可上传");
				}
			}
		}
	}

	private void startRealTimeMode() {
		new AsyncTask<Void, Integer, String>() {
			@Override
			protected String doInBackground(Void... params) {
				try {
					// 生成实时数据文件，并上传
					File file = writeECGData2File(true);
					if (file == null) {
						if (ConstantConfig.Debug) {
							LogUtil.d(TAG, "无数据可实时上传");
						}
						return null;
					}
					if (ConstantConfig.Debug) {
						LogUtil.d(TAG, "正在实时上传" + file.getAbsolutePath());
					}
					new ClientGameService().uploadRealTimeFile(file,
							new HttpReqCallBack<UploadFileResponse>() {
								@Override
								public Activity getReqActivity() {
									return null;
								}

								@Override
								public void doSuccess(UploadFileResponse result) {
									if (result.isOk()) {
										if (ConstantConfig.Debug) {
											LogUtil.d(TAG, "实时上传成功");
										}
									}
								}

								@Override
								public void doError(String result) {
									if (ConstantConfig.Debug) {
										LogUtil.d(TAG, "实时上传失败" + result);
									}
								}
							});
					return "";
				} catch (Exception e) {
					LogUtil.e(TAG, e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (isRealMode) {
					startRealTimeMode();
				}
			}
		}.execute();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		start();
		return super.onStartCommand(intent, flags, startId);
	}

	private File writeECGData2File(boolean isRealMode) {
		FrameDataMachine machine = FrameDataMachine.getInstance();
		List<FileFrameData> fileFrameDatas = new ArrayList<FileFrameData>();
		FileFrameData fileFrameData = null;
		while ((fileFrameData = isRealMode ? machine.getRealTimeFrameData()
				: machine.getFileFrameData()) != null) {
			fileFrameDatas.add(fileFrameData);
		}
		if (fileFrameDatas.size() > 0) {
			try {
				int capacity = SettingsManager.getInstance()
						.getSettingsOffData();
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, (isRealMode ? "实时模式" : "批量") + "开始写文件(可用空间"
							+ capacity + "MB)");
				}
				if (capacity > 0) {
					FileUtil fileUtil = new FileUtil();
					if (isRealMode) {
						fileUtil.setTempMode();
						fileUtil.clearFiles(FileUtil.ECGTEALTIMEDIR);
					}
					fileUtil.beginWriteFile();
					fileUtil.writeBlock(fileFrameDatas);
					fileUtil.endWriteFile();
					return fileUtil.getECGFile();
				} else {
					if (ConstantConfig.Debug) {
						LogUtil.d(TAG, "无存储空间可用");
					}
				}
				if (ConstantConfig.Debug) {
					LogUtil.d(TAG, (isRealMode ? "实时模式" : "批量") + "结束写文件");
				}
			} catch (Exception e) {
				LogUtil.e(TAG, e);
				e.printStackTrace();
			}
		} else {
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, "无蓝牙数据可写入文件");
			}
		}
		return null;

	}

	private void start() {
		pkgFrameDataTask = new TimerTask() {
			@Override
			public void run() {
				// 批量写文件
				File file = writeECGData2File(false);
				// 不用限制是否有数据
				// if (file == null)
				// return;
				if (curUploadWay == UploadWay.OneKey)
					return;
				startUpload();
			}
		};
		pkgFrameDataTimer.schedule(pkgFrameDataTask, 0, 60 * 1000);
		registerReceiver(mGattUpdateReceiver, makeUploadIntentFilter());
	}

	private void end() {
		unregisterReceiver(mGattUpdateReceiver);
		pkgFrameDataTimer.cancel();
	}

	private static IntentFilter makeUploadIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleDomainService.ACTION_UPLOAD_STARTREALMODE);
		intentFilter.addAction(BleDomainService.ACTION_UPLOAD_ENDREALMODE);
		intentFilter.addAction(BleDomainService.ACTION_UPLOAD_STARTONEKEYMODE);
		intentFilter.addAction(BleDomainService.ACTION_UPLOAD_ENDONEKEYMODE);
		return intentFilter;
	}

	@Override
	public void onDestroy() {

		end();
		super.onDestroy();
	}
}
