package com.langlang.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import com.langlang.activity.BaseActivity;
import com.langlang.data.FormFile;
import com.langlang.data.UploadTaskInfo;
import com.langlang.global.Client;
import com.langlang.global.GlobalStatus;
import com.langlang.global.OfflineLoginManager;
import com.langlang.global.SettingInfo;
import com.langlang.global.UserInfo;
import com.langlang.manager.MinuteECGResultManager;
import com.langlang.manager.UploadTaskManager;
import com.langlang.manager.WarningHteManager;
import com.langlang.manager.WarningTumbleManager;
import com.langlang.utils.DateUtil;
import com.langlang.utils.HttpUtils;
import com.langlang.utils.Program;
import com.langlang.utils.SDCardUtils;
import com.langlang.utils.SDChecker;
import com.langlang.utils.SocketHttpRequester;
import com.langlang.utils.UIUtil;
import com.langlang.utils.UtilStr;
import com.langlang.data.UploadTask;
import com.langlang.elderly_langlang.R;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.RemoteViews;

public class UploadService extends Service {
	
	UploadTaskManager uploadTaskManager = new UploadTaskManager(this);
	MinuteECGResultManager minuteECGResultManager = new MinuteECGResultManager(this);
	WarningHteManager warningHteManager = new WarningHteManager(this);
	WarningTumbleManager warningTumbleManager = new WarningTumbleManager(this);
	
	public final static String SERVICE_NAME = "com.langlang.service.UploadService";
	public static final int UPDATE_LACOTION=111;
	public static final int UPDATE_LACOTION_WARNING=1111;
	private final int UPDATE_HEAR=121;
	
	Queue<UploadTask> taskQueue = new LinkedList<UploadTask>();
	
	Queue<UploadTask> clearTaskQueue = new LinkedList<UploadTask>();
	
	private long mCountClearSDCard = 0;
	private long mCountCheckSDCard = 0;
	
	private int mClearDays = 9;
	
    private double updata_longitude;
	private double updata_latitude;
	private String updata_time;
	private volatile boolean mIsNowNwWiFi = false;
	private SettingInfo settingInfo;
	SDChecker sdChecker = new SDChecker(this, SDChecker.SPACE_M_2);
    private Timer timer20m=null;
    
    private BackgroundLogin mBackgroundLogin = null;
    private OfflineLoginManager mOfflineLoginManager;
    
	private TaskThread mTaskThread = null;
	private SDMonitorThread mSDMonitorThread = null;
	private UploadRoutineThread mUploadRoutineThread = null;
	
	private ECGCmdListener mECGCmdListener = null;
	
	private static final String ACTION_ALARM_CHECK_ECG_CMD_LISTENER
					= "com.langlang.android.bluetooth.le.ACTION_ALARM_CHECK_ECG_CMD_LISTENER"; 
	private boolean isCheckEcgCmdListenerTimerStarted = false;
	
	private static final String ACTION_ON_ECG_CMD_LISTENER_READY
					= "com.langlang.android.bluetooth.le.ACTION_ON_ECG_CMD_LISTENER_READY"; 
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		settingInfo=new SettingInfo(getApplicationContext());
		checkNwWifi(); 
		System.out.println("当前GPS:"+hasGPSDevice());
		if(hasGPSDevice()){
			initLocation();	
//			setTimer();
		}
		
		mCountClearSDCard = 0;
		mCountCheckSDCard = 0;
		
//		getUserInfo();
		taskQueue.clear();
		
		clearTaskQueue.clear();
		
		getClearDays();
		
		mTaskThread = new TaskThread(taskQueue);
		mTaskThread.start();
		
		mSDMonitorThread = new SDMonitorThread(clearTaskQueue);
		mSDMonitorThread.start();
		
		mOfflineLoginManager = new OfflineLoginManager(getApplicationContext());
		mBackgroundLogin = new BackgroundLogin();
		
//		new Thread(uploadRoutine).start();
		mUploadRoutineThread = new UploadRoutineThread();
		mUploadRoutineThread.start();
		
		new UpdatehearThread().start();
		
//		mECGCmdListener = new ECGCmdListener(Client.ECG_CMD_SERVER_IP,
//											 Client.ECG_CMD_SERVER_PORT,
//											 UserInfo.getIntance().getUserData().getMy_name());
//		mECGCmdListener.start();
		
		registerReceiver(StorageReceiver, makeGattUpdateIntentFilter());

//		startCheckEcgCmdListenerTimer();
		
//		sendCheckConnectionCommand();
		
//		notifyClearSDAuto();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	private class UploadRoutineThread extends Thread {
//	Runnable uploadRoutine = new Runnable() {
		private volatile boolean quit = false;
		
		public UploadRoutineThread() {
			quit = false;
		}
		public void setQuit() {
			quit = true;
		}
		@Override
		public void run() {
			while (true) {				
				try {
					System.out.println("action UploadService uploadRouting.Run one time.");
					Thread.sleep(10 * 1000);
					synchronized(taskQueue) {
						taskQueue.offer(new UploadTask(1));
						taskQueue.notifyAll();
					}
					
					mCountClearSDCard++;
					if (mCountClearSDCard >= (6 * 60 * 4)) {
//					if (mCountClearSDCard >= (2)) {
						mCountClearSDCard = 0;
						sendIntentToClearStorage();
					}
					
					mCountCheckSDCard++;
					if (mCountCheckSDCard >= (6 * 30 * 1)) {
//					if (mCountCheckSDCard >= (2)) {
						mCountCheckSDCard = 0;
						checkSDSpace();
						
						checkAndLogin();
						new UpdatehearThread().start();
					}
				} catch (InterruptedException e) {
					System.out.println("action UploadService uploadRouting.Run one time. InterruptedException");
					e.printStackTrace();
				}
				
				if (quit) {
					break;
				}
			}
		}
	};
	
	private class TaskThread extends Thread {
		private Queue<UploadTask> queue;
		private volatile boolean quit = false;
		
		public TaskThread(Queue<UploadTask> taskQueue) {
			queue = taskQueue;
			quit = false;
		}
		public void setQuit() {
			quit = true;
		}
		@Override
		public void run() {
			while (true) {
				UploadTask task = null;
				synchronized(queue) {
					while (queue.size() == 0) {
						try {
							queue.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					task = queue.poll();
				}
				if (task != null) {
					if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
					} else {
						System.out.println("上传的模式 补传："+settingInfo.getUploadNetwork());
						if(settingInfo.getUploadNetwork()==SettingInfo.CHECK_UPLOAD_WF){
							if(checkNwWifi()){
								checkAndUpload();
								System.out.println("上传的模式 补传："+settingInfo.getUploadNetwork()+"仅WF");
							}
						}else{
							checkAndUpload();
							System.out.println("上传的模式 补传："+settingInfo.getUploadNetwork()+"所有网络");
						}

				
					}
				}
				if (quit) {
					break;
				}
			}
		}
	};
	
	private class SDMonitorThread extends Thread {
		private Queue<UploadTask> clearTaskQueue;
		private volatile boolean quit = false;
		
		public SDMonitorThread(Queue<UploadTask> taskQueue) {
			clearTaskQueue = taskQueue;
			quit = false;
		}
		public void setQuit() {
			quit = true;
		}
		@Override
		public void run() {
			while (true) {
				UploadTask task = null;
				synchronized(clearTaskQueue) {
					while (clearTaskQueue.size() == 0) {
						try {
							clearTaskQueue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					task = clearTaskQueue.poll();
				}
				if (task != null) {
					if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
					} else {
						checkAndClearStorage();
					}
				}
				if (quit) {
					break;
				}
			}
		}
	}
	
	// 清除mClearDays天以前的数据文件
	// 清除mClearDays天以前的数据库记录
	private void checkAndClearStorage() {
//		boolean isAvailable = SDCardUtils.isAvailableSpace(15);
		long available = SDCardUtils.getAvaiableSpace();
		if (available > 0 && available <= 15) {
			if (mClearDays > 1) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, mClearDays - 1);
				
				Date clearDate = calendar.getTime();
				
				clearSDCard(clearDate);
				clearDB(clearDate);
				
				notifyClearSDAuto();
			}
		}
		if (available > 15 && available <= 50) {
			if (mClearDays > 3) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, mClearDays - 3);
				
				Date clearDate = calendar.getTime();
				
				clearSDCard(clearDate);
				clearDB(clearDate);
				
				notifyClearSDAuto3();
			}			
		}
		else {
			Date now = new Date();
			
			clearSDCard(now);
			clearDB(now);
		}
	}
	
	private void notifyClearSDAuto() {
        NotificationManager manager 
        		 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
//		Notification notify2 = new Notification.Builder(this)  
//				.setSmallIcon(R.drawable.new_login)  
//				.setTicker(":" + "您有新短消息，请注意查收！")  
//				.setContentTitle("朗朗心提示") // 设置在下拉status  
//											  // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题  
//				.setContentText("您的外部存储空间小于15M,两天以前的存储数据以被清除")// TextView中显示的详细内容  
////				.setContentIntent(pendingIntent2) // 关联PendingIntent  
////				.setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。  
//				.getNotification(); // 需要注意build()是在API level  
//		// 16及之后增加的，在API11中可以使用getNotificatin()来代替  
//		notify2.flags |= Notification.FLAG_AUTO_CANCEL;  
//		manager.notify(1, notify2);
        
//        Notification notify1 = new Notification();  
//        notify1.icon = R.drawable.new_login;  
//        notify1.tickerText = "朗朗心自动清理存储空间程序已经启动";  
//        notify1.when = System.currentTimeMillis();  
//        notify1.setLatestEventInfo(this, "朗朗心提醒",
//        		 "您的外部存储空间已经小于15\n,应用程序已经自动清除掉2天以前的数据", null);  
////        notify1.number = 1;  
//        notify1.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。  
//        // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示  
//        manager.notify(1, notify1);
        
        Notification myNotify = new Notification();  
        myNotify.icon = R.drawable.new_login;  
        myNotify.tickerText = "朗朗心自动清理存储空间程序已经启动";  
        myNotify.when = System.currentTimeMillis();  
        myNotify.flags |= Notification.FLAG_AUTO_CANCEL;  
        RemoteViews rv = new RemoteViews(getPackageName(),  
                R.layout.notification_clear_sd_auto);  
        rv.setTextViewText(R.id.text_content, "您的外部存储空间已经小于15M,朗朗心已经自动清除掉1天以前的本地数据");  
        myNotify.contentView = rv;  
        Intent intent = new Intent(Intent.ACTION_MAIN);  
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 1,  
//                intent, 1);  
//        myNotify.contentIntent = contentIntent;  
        myNotify.contentIntent = null;  
        manager.notify(1, myNotify);
	}
	
	private void notifyClearSDAuto3() {
        NotificationManager manager 
        		 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		        
        Notification myNotify = new Notification();  
        myNotify.icon = R.drawable.new_login;  
        myNotify.tickerText = "朗朗心自动清理存储空间程序已经启动";  
        myNotify.when = System.currentTimeMillis();  
        myNotify.flags |= Notification.FLAG_AUTO_CANCEL;  
        RemoteViews rv = new RemoteViews(getPackageName(),  
                R.layout.notification_clear_sd_auto);
        rv.setTextViewText(R.id.text_content, "您的外部存储空间已经小于50M,朗朗心已经自动清除掉3天以前的本地数据");  
        myNotify.contentView = rv;  
        myNotify.contentIntent = null;  
        manager.notify(1, myNotify);
	}

	private void clearSDCard(Date now) {
		String dataPath = Program.getSDLangLangDataPath();
		File dataDir = new File(dataPath);
		
		List<String> userList = new ArrayList<String>();
		
		// get user's path
		File[] userDirs =  dataDir.listFiles();
		if (userDirs != null) {
			for (int i = 0; i < userDirs.length; i++) {
				File userDir = userDirs[i];
				if ((userDir != null) && userDir.isDirectory()) {
					userList.add(userDir.getAbsolutePath());
				}
			}
			for (int i = 0; i < userList.size(); i++) {
				deleteDayDirs(userList.get(i), now);
			}
			for (int i = 0; i < userDirs.length; i++) {
				userDirs[i].delete();
			}
		}
	}
	
	private void deleteDayDirs(String userDirName, Date now) {
		File userDir = new File(userDirName);
		
		File[] dayDirs = userDir.listFiles();
		
		if (dayDirs != null) {
			for (int i = 0; i < dayDirs.length; i++) {
				if ((dayDirs[i] != null) && (dayDirs[i].isDirectory())) {
					String dayStr = dayDirs[i].getName();
					SimpleDateFormat sdf = new SimpleDateFormat(Program.DATE_FORMAT);
					try {
						Date date = sdf.parse(dayStr);
						
						if (DateUtil.isDateBeforDays(now, date, mClearDays)) {
							deleteMinuteDirs(dayDirs[i]);
						}
					} catch (Exception e) {
						System.out.println("action UploadService deleteDayDirs parseException [" 
																			+ dayStr + "]");
						continue;
					}
				}
			}
			
			for (int i = 0; i < dayDirs.length; i++) {
				dayDirs[i].delete();
			}
		}
	}
	
	private void deleteMinuteDirs(File dayDir) {
		File[] minuteDirs = dayDir.listFiles();
		
		if (minuteDirs != null) {
			for (int i = 0; i < minuteDirs.length; i++) {
				String minuteName = minuteDirs[i].getName();
				SimpleDateFormat sdf 
								= new SimpleDateFormat(Program.MINUTE_FORMAT);
				try {
					Date minute = sdf.parse(minuteName);
				} catch (Exception e) {
					System.out.println("action UploadService deleteMinuteDirs parseException [" + minuteName + "]");
					continue;
				}
				deleteDataFiles(minuteDirs[i]);
			}
			
			for (int i = 0; i < minuteDirs.length; i++) {
				minuteDirs[i].delete();
			}
		}
	}
	
	private void deleteDataFiles(File minuteDir) {
		String minuteName = minuteDir.getName();
		File[] dataFiles = minuteDir.listFiles();
		
		if (dataFiles != null) {
			String checkNameCSV = minuteName + Program.CSV_POSTFIX;
			String checkNameDat = minuteName + Program.DAT_POSTFIX;
			String checkNameDatX = minuteName + Program.DATX_POSTFIX;
			
			for (int i = 0; i < dataFiles.length; i++) {
				if ((dataFiles[i] != null) && dataFiles[i].isDirectory()) { continue; }
				
				String dataFileName = dataFiles[i].getName();			
				if (dataFileName.endsWith(checkNameCSV) || 
					dataFileName.endsWith(checkNameDat) ||
					dataFileName.endsWith(checkNameDatX)
				) {
					try {
						dataFiles[i].delete();
					} catch (Exception e) {
						System.out.println("action deleteDataFiles exception delete[" + dataFileName + "]");
					}
				}
			}
		}
	}
	
	private void clearDB(Date now) {
		uploadTaskManager.clearDataBeforeNDays(now, mClearDays);
		minuteECGResultManager.clearDataBeforeNDays(now, mClearDays);
		warningHteManager.clearDataBeforeNDays(now, mClearDays);
		warningTumbleManager.clearDataBeforeNDays(now, mClearDays);
	}
	
	private void checkAndUpload() {
		String uid = UserInfo.getIntance().getUserData().getMy_name();
		if (uid == null || uid.length() <= 0) {
			System.out.println("action UploadService checkAndUpload uid is empty.");
			return;
		}
		System.out.println("action UploadService checkAndUpload.");
//		UploadTaskInfo uploadTaskInfo = uploadTaskManager.fetchOneUploadTask();
		UploadTaskInfo uploadTaskInfo = uploadTaskManager.fetchOneUploadTaskByUser(uid);
//		System.out.println("action uploadService  checkAndUpload. uploadTaskInfo=" + uploadTaskInfo);
		
//		while (uploadTaskInfo != null) {
		if (uploadTaskInfo == null) {
			System.out.println("action UploadService checkAndUpload uploadTaskInfo is null");
		} else{
			System.out.println("action UploadService checkAndUpload UploadTaskInfo[" 
					+ uploadTaskInfo.id + "," 
					+ uploadTaskInfo.minuteData + "," + uploadTaskInfo.uid + "]");			
		}
		if (uploadTaskInfo != null) {
			boolean ok = false;
			String path = Program.getDataPathByMinute(uploadTaskInfo.minuteData, uid);
			System.out.println("action UploadService checkAndUpload path[" + path + "]");
	        File dir = new File(path);
	        if (!dir.exists()) {
	        	System.out.println("action UploadService checkAndUpload path does not exist[" + path + "] ");
	        	uploadTaskManager.delete(uploadTaskInfo.id);
	        	return;
	        }
	        System.out.println("action UploadService checkAndUpload path exist[" + path + "] ");
	        
	        File files[] = dir.listFiles();
	        for (int i = 0; i < files.length; i++) {
	        	System.out.println("action UploadService checkAndUpload start uploading[" + files[i] + "] ");
	        	try {
	        		System.out.println("action UploadService checkAndUpload uploadFile start[" + files[i].getPath() + "]");
	        		if (files[i].length() == 0 || !files[i].isFile()) {
	        			System.out.println("action UploadService checkAndUpload uploadFile file is a directory or its size is 0 [" + files[i].length() + "]");
	        			ok = true;
	        		} else {
	        			Date startUploadDate = new Date();
	        			ok = uploadFile(files[i]);
	        			Date endUploadDate = new Date();
	        			System.out.println("action UploadService checkAndUpload uploadFile timestamp[" 
	        						+ (endUploadDate.getTime() - startUploadDate.getTime()) + "]");
	        		}
	        		System.out.println("action UploadService checkAndUpload uploadFile completed [" + files[i].getPath() + "] ok [" + ok + "]");
				} catch (Exception e) {
					System.out.println("action UploadService checkAndUpload uploadFile exception[" + files[i].getPath() + "] e = [" + e.toString() + "]");
					ok = false;
					break;
				}
	        }
	        if (ok) {
	        	uploadTaskManager.delete(uploadTaskInfo.id);
	        }	        
		}
	}
	
	private void upload(String minuteData) {		
		System.out.println("action UploadService upload[" + minuteData + "]");
		boolean ok = false;
		String path = Program.getDataPathByMinute(minuteData);
		System.out.println("action UploadService upload path[" + path + "]");
		
        File dir = new File(path);
        if (!dir.exists()) {
        	System.out.println("action UploadService upload path does not exist[" + path + "] ");
        	uploadTaskManager.deleteByMinuteData(minuteData);
        	return;
        }
        System.out.println("action UploadService upload path exist[" + path + "] ");
	        
        File files[] = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
        	System.out.println("action UploadService upload start uploading[" + files[i] + "] ");
        	try {
        		System.out.println("action UploadService upload uploadFile start[" + files[i].getPath() + "]");
        		if (files[i].length() == 0 || !files[i].isFile()) {
        			System.out.println("action UploadService upload uploadFile file is a directory or its size is 0 [" + files[i].length() + "]");
        			ok = true;
        		} else {
        			Date startUploadDate = new Date();
        			ok = uploadFile(files[i]);
        			Date endUploadDate = new Date();
        			System.out.println("action UploadService upload uploadFile timestamp[" 
        						+ (endUploadDate.getTime() - startUploadDate.getTime()) + "]");
        		}
        		System.out.println("action UploadService upload completed [" + files[i].getPath() + "] ok [" + ok + "]");
			} catch (Exception e) {
				System.out.println("action UploadService upload exception[" + files[i].getPath() + "] e = [" + e.toString() + "]");
				ok = false;
				break;
			}
        }
        if (ok) {
        	uploadTaskManager.deleteByMinuteData(minuteData);
        }	        
	}
	
	private void upload(String minuteData, String uid) {		
		boolean ok = false;
		String path = Program.getDataPathByMinute(minuteData, uid);
		
        File dir = new File(path);
        if (!dir.exists()) {
        	System.out.println("action UploadService upload path does not exist[" + path + "] ");
        	uploadTaskManager.deleteByMinuteData(minuteData);
        	return;
        }
        System.out.println("action UploadService upload path exist[" + path + "] ");
	        
        File files[] = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
        	System.out.println("action UploadService upload start uploading[" + files[i] + "] ");
        	try {
        		System.out.println("action UploadService upload uploadFile start[" + files[i].getPath() + "]");
        		if (files[i].length() == 0 || !files[i].isFile()) {
        			System.out.println("action UploadService upload uploadFile file is a directory or its size is 0 [" + files[i].length() + "]");
        			ok = true;
        		} else {
        			Date startUploadDate = new Date();
        			ok = uploadFile(files[i]);
        			Date endUploadDate = new Date();
        			System.out.println("action UploadService upload uploadFile timestamp[" 
        						+ (endUploadDate.getTime() - startUploadDate.getTime()) + "]");
        		}
        		System.out.println("action UploadService upload completed [" + files[i].getPath() + "] ok [" + ok + "]");
			} catch (Exception e) {
				System.out.println("action UploadService upload exception[" + files[i].getPath() + "] e = [" + e.toString() + "]");
				ok = false;
				break;
			}
        }
        if (ok) {
//        	uploadTaskManager.deleteByMinuteData(minuteData);
        	uploadTaskManager.deleteByMinuteData(minuteData, uid);
        }	        
	}

	/**
	 * 映射数据
	 */
	private String mappingData() {
//		SharedPreferences	sharedPreferences = this.getSharedPreferences("peopleinfo",
//				MODE_PRIVATE);
//		String username = null;
//		String info = sharedPreferences.getString("userinfo", "");
//		System.out.println("action HteActivity limit:" + info);
//		try {
//			JSONArray jsonArray = new JSONArray(info);
//			JSONObject jsonObject = jsonArray.getJSONObject(0);
////			username = jsonObject.getString("mobile");
//			username = jsonObject.getString("userName");
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return username;
		return UserInfo.getIntance().getUserData().getMy_name();
	}

	String usernames;
	/**
	 * 上传文件
	 * @param file所上传的文件
	 * @return
	 * @throws Exception
	 */
	public boolean uploadFile(File file) throws Exception {
		usernames=mappingData();
		if (usernames == null || usernames.length() <= 0) {
			return false;
		}
		System.out.println("action usernames"+usernames);
		boolean flag = false;
		try {
			////<String requestUrl = "http://192.168.0.118:8082/PullService/index.action";
			String requestUrl = Client.UPLOAD_URL;
			// 请求普通信息
			Map<String, String> params = new HashMap<String, String>();
			System.out.println("action UploadService uploadFile username[" + usernames + "]");
			System.out.println("action UploadService uploadFile Client.UPLOAD_URL[" + Client.UPLOAD_URL + "]");
			
			params.put("username", usernames);
			params.put("pwd", "zhangsan");
			params.put("age", "21");
			params.put("fileName", file.getName());
			
			System.out.println("action: uploadFile file.size[" + file.length() + "]");
			
			// 上传文件
			FormFile formfile = new FormFile(file.getName(), file, "file",
					"application/octet-stream");
			flag = SocketHttpRequester.post(requestUrl, params, formfile);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return flag;
	}
	private final BroadcastReceiver StorageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {			  
			final String action = intent.getAction();
			if (DataStorageService.ACTION_START_UPLOAD.equals(action)) {
				System.out.println("action UploadService ACTION_START_UPLOAD received.");
//				checkAndUpload();
//				synchronized(taskQueue) {
//					taskQueue.offer(new UploadTask(1));
//					taskQueue.notifyAll();
//				}
				String minuteData = intent.getStringExtra("LAST_UPLOAD");
				String uid = intent.getStringExtra("UID");
				if (minuteData != null && minuteData.length() > 0) {
					System.out.println("上传的模式："+settingInfo.getUploadNetwork());
					if(settingInfo.getUploadNetwork()==SettingInfo.CHECK_UPLOAD_WF){
						if(checkNwWifi()){
							new UploadThread(minuteData.trim(), uid.trim()).start();
							System.out.println("上传的模式："+settingInfo.getUploadNetwork()+"仅WF");
						}
					}else{
						new UploadThread(minuteData.trim(), uid.trim()).start();
						System.out.println("上传的模式："+settingInfo.getUploadNetwork()+"所有网络");
					}
				}
			}
			else if (DataStorageService.ACTION_CLEAR_STORAGE.equals(action)) {
				synchronized(clearTaskQueue) {
					clearTaskQueue.offer(new UploadTask(1));
					clearTaskQueue.notifyAll();
				}				
			}			
			else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				System.out.println("55555555555555555555555555555555555555");
				 if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {  
					releaseWifiLock();
					return;
				 }
				 
				 ConnectivityManager connectMgr = (ConnectivityManager) UploadService.this
					        .getSystemService(Context.CONNECTIVITY_SERVICE);					 
				 NetworkInfo info = connectMgr.getActiveNetworkInfo();					 
				 if (info == null) {
					releaseWifiLock();
				 } else {
					if (info.getType() == ConnectivityManager.TYPE_WIFI) {
						takeWifiLock();
					}else{
						releaseWifiLock();
					}
					
//					if (HttpUtils.isNetworkAvailable(getApplicationContext())) {
////						mECGCmdListener.checkConnected();
//						sendCheckConnectionCommand();
//					}
				 }				 
			}
			else if (ACTION_ALARM_CHECK_ECG_CMD_LISTENER.equals(action)) {
//				if (HttpUtils.isNetworkAvailable(getApplicationContext())) {
////					mECGCmdListener.checkConnected();
//					sendCheckConnectionCommand();
//				}
			}
//			else if (ACTION_ON_ECG_CMD_LISTENER_READY.equals(action)) {
//				mECGCmdListener.checkConnection();
//			}
		}
	};

	private  boolean checkNwWifi() {

		ConnectivityManager connectMgr = (ConnectivityManager) UploadService.this
				.getSystemService(Context.CONNECTIVITY_SERVICE);					 
		NetworkInfo info = connectMgr.getActiveNetworkInfo();					 
		if (info == null) {
			mIsNowNwWiFi = false;
		} else {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				mIsNowNwWiFi = true;
			}else{
				mIsNowNwWiFi = false;
			}
		}
		return mIsNowNwWiFi;
	}

	private void sendIntentToClearStorage() {
		String uid = UserInfo.getIntance().getUserData().getMy_name();
		if ((uid != null) && (uid.length() >0)) {
			Intent intent = new Intent(DataStorageService.ACTION_CLEAR_STORAGE);
			intent.putExtra(DataStorageService.CLEAR_UID, uid);
			sendBroadcast(intent);
		}
	}
	
	private void checkSDSpace() {
		// 检测SD卡,如果空间太小则提示用户
		if (!sdChecker.hasEnoughSpace()) {
			Intent intent = new Intent(DataStorageService.ACTION_ALERT_SD_STATUS);
			intent.putExtra(DataStorageService.ALERT_SD_STATUS_LEVEL, SDChecker.SPACE_M_2);
			sendBroadcast(intent);
		}
	}
	
	private class UploadThread extends Thread {
		private String minuteData;
		private String uid;
		public UploadThread(String minuteData, String uid) {
			this.minuteData = minuteData;
			this.uid = uid;
		}
		
		public void run() {
			if (UserInfo.getIntance().getUserData().getLogin_mode() == 1) {
			} else {
//			upload(minuteData);
			upload(minuteData, uid);
			}
		}
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DataStorageService.ACTION_START_UPLOAD);
		intentFilter.addAction(DataStorageService.ACTION_CLEAR_STORAGE);
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		
		intentFilter.addAction(ACTION_ALARM_CHECK_ECG_CMD_LISTENER);
//		intentFilter.addAction(ACTION_ON_ECG_CMD_LISTENER_READY);
		return intentFilter;
	}
	@Override
	public boolean stopService(Intent name) {
		// TODO Auto-generated method stub
		this.unregisterReceiver(StorageReceiver);
		System.out.println("action serviced upload");
		return super.stopService(name);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseWifiLock();
		if(lm!=null){
			lm.removeUpdates(locationListener);
		}
		
//		cancelTimer();
		if (mBackgroundLogin != null) {
			mBackgroundLogin.stop();
			mBackgroundLogin = null;
		}
		
		if (mTaskThread != null) {
			mTaskThread.setQuit();
			mTaskThread = null;
		}
		
		if (mSDMonitorThread != null) {
			mSDMonitorThread.setQuit();
			mSDMonitorThread = null;
		}
		
		if (mUploadRoutineThread != null) {
			mUploadRoutineThread.setQuit();
			mUploadRoutineThread = null;
		}
		
//		if (mECGCmdListener != null) {
//			mECGCmdListener.setQuit();
//			mECGCmdListener = null;
//		}
		
//		stopCheckEcgCmdListenerTimer();
		
		System.out.println("action onDestroy------- upload");
	}
	
	private void getClearDays() {
		mClearDays = 9;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	private WifiLock wifiLock=null;
	private void takeWifiLock()
    {
        if (wifiLock == null)
        {
            WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiLock = manager.createWifiLock("UploadService");
            wifiLock.setReferenceCounted(false);
        }
        wifiLock.acquire();
    }

    private void releaseWifiLock()
    {
        if (wifiLock != null)
        {
            wifiLock.release();
            wifiLock = null;
        }
    }
    //-----------------------GPS---------------------
    private LocationManager lm=null;
    
    private void initLocation(){
    	lm=(LocationManager)getApplication().getSystemService(Context.LOCATION_SERVICE);
    	//为获取地理位置信息时设置查询条件
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        //获取位置信息
        //如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location= lm.getLastKnownLocation(bestProvider);    
        updateLocation(location);
        //监听状态
        lm.addGpsStatusListener(listener);
		//绑定监听，有4个参数    
        //参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        //参数2，位置信息更新周期，单位毫秒    
        //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息    
        //参数4，监听    
        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新   
        // 1秒更新一次，或最小位移变化超过1米更新一次；
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

    }
    //位置监听
    private LocationListener locationListener=new LocationListener() {
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
        	
        	updateLocation(location);
//        	SimpleDateFormat formats=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        	String loacation_timeString=formats.format( location.getTime());
//			UIUtil.setToast(
//					getApplication(),
//					"时间：" + loacation_timeString + "经度："
//							+ location.getLongitude() + "纬度："
//							+ location.getLatitude() + "海拔："
//							+ location.getAltitude());
            
        }
        
        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            //GPS状态为可见时
            case LocationProvider.AVAILABLE:
//                UIUtil.setToast(getApplication(), "当前GPS状态为可见状态");
                break;
            //GPS状态为服务区外时
            case LocationProvider.OUT_OF_SERVICE:
//                UIUtil.setToast(getApplication(), "当前GPS状态为服务区外状态");
                break;
            //GPS状态为暂停服务时
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                UIUtil.setToast(getApplication(), "当前GPS状态为暂停服务状态");
                break;
            }
        }
    
        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location=lm.getLastKnownLocation(provider);
            updateLocation(location);
        }
    
        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
        	updateLocation(null);
        }
    };
  //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            //获取当前状态
            GpsStatus gpsStatus=lm.getGpsStatus(null);
            switch (event) {
            //第一次定位
            case GpsStatus.GPS_EVENT_FIRST_FIX:
//            	UIUtil.setToast(getApplication(), "第一次定位时间："+gpsStatus.getTimeToFirstFix());
                break;
            //卫星状态改变
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//            	UIUtil.setToast(getApplication(), "卫星状态改变");
            
//                //获取卫星颗数的默认最大值
//                int maxSatellites = gpsStatus.getMaxSatellites();
//                //创建一个迭代器保存所有卫星 
//                Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
//                int count = 0;     
//                while (iters.hasNext() && count <= maxSatellites) {     
//                    GpsSatellite s = iters.next();     
//                    count++;     
//                }   
////                UIUtil.setToast(getApplication(), "搜索到："+count+"颗卫星");
                break;
            //定位启动
            case GpsStatus.GPS_EVENT_STARTED:
            	setTimer();
            	UIUtil.setToast(getApplication(), "GPS定位已开始");
            	
                break;
            //定位结束
            case GpsStatus.GPS_EVENT_STOPPED:
            	UIUtil.setToast(getApplication(), "GPS定位已结束");
            	cancelTimer();
                break;
            }
        };
    };
    private Criteria getCriteria(){
    	Criteria criteria=new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);//获取精确度
    	criteria.setSpeedRequired(false);//速度
    	criteria.setCostAllowed(false);//允许收费
    	criteria.setBearingRequired(true);//是否需要方位信息
    	criteria.setAltitudeRequired(false);//设置是否需要海拔的信息
    	criteria.setPowerRequirement(Criteria.POWER_LOW);//设置对电源的需求
    	
    	return criteria;
    }
    /** 
     * 更新位置 
     */
    @SuppressLint("SimpleDateFormat")
	private void updateLocation(Location location){
//    	UIUtil.setToast(getApplication(), location+"");
    	if(location!=null){
    		updata_latitude=location.getLatitude();//纬度
    		updata_longitude=location.getLongitude();//经度
//    		long now_time=location.getTime();
    		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		updata_time=simpleDateFormat.format(location.getTime());
    		
    		UserInfo.getIntance().getUserData().setLongitude(updata_longitude);
			UserInfo.getIntance().getUserData().setLatitude(updata_latitude);
			UserInfo.getIntance().getUserData().setUpdate_GPS_time(updata_time);
    		String location_data="当前时间："+updata_time+"\n纬度："+updata_latitude+"\n经度："+updata_longitude;
//    		UIUtil.setToast(getApplication(), location_data);
    	}
    }
	
    /**
     * 启动位置更新的线程 
     * @author Administrator
     *
     */
	class startLocationThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String userInfo ="[{username:\""
					+ UserInfo.getIntance().getUserData().getMy_name()
					+ "\",longitude:\"" + updata_longitude + "\",latitude:\""
					+ updata_latitude + "\",positioningTime:\""+updata_time+"\"}]";
			
			String updataString=Client.updataGPSLocation(userInfo);
			System.out.println("startLocationThread updatasting:"+updataString);
			if(updataString==null){
				UIUtil.setMessage(handler, UPDATE_LACOTION_WARNING);
				return;
			}
			UIUtil.setMessage(handler, UPDATE_LACOTION,updataString);
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressLint("SimpleDateFormat")
		public void handleMessage(android.os.Message msg) {
			if (msg.what == UPDATE_LACOTION) {
				if (msg.obj.toString().equals("1")) {
					UIUtil.setToast(getApplication(), "已获取最新位置");
				} else {
					// GPS上传失败
				}
			}
			if (msg.what == UPDATE_LACOTION_WARNING) {
				// GPS网络异常
			}
			if(msg.what==UPDATE_HEAR){
				long service_time = 0;
				Date mData=new Date();
				Date datesDate=null;
				SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					datesDate=dateFormat.parse(msg.obj.toString());
					service_time=datesDate.getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					datesDate=null;
					service_time=0;
				}
				if(service_time!=0){				
					if(mData.getTime()-service_time>60000||mData.getTime()-service_time<-60000){
					UIUtil.setLongToast(getApplicationContext(), "手机时间与服务器时间不符,服务器时间为："+msg.obj.toString());
					UIUtil.setLongToast(getApplicationContext(), "手机时间与服务器时间不符,服务器时间为："+msg.obj.toString());
					UIUtil.setLongToast(getApplicationContext(), "手机时间与服务器时间不符,服务器时间为："+msg.obj.toString());
					}
				}
				System.out.println("实时更新数据up："+msg.obj.toString()+":"+service_time+":"+mData.getTime());
			}
		};
	};

    private void setTimer(){
    	timer20m=new Timer();
    		timer20m.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					System.out.println("startLocationThread updatasting_data:"+updata_time);
		        	new startLocationThread().start();
				}
			}, 500,1000*60 * 15);
    }
    private void cancelTimer(){
    	if(timer20m!=null){
    		timer20m.cancel();
    	}
    }

	public boolean hasGPSDevice()
	{lm=(LocationManager)getApplication().getSystemService(Context.LOCATION_SERVICE);
		if ( lm == null ) 
			return false;
		final List<String> providers = lm.getAllProviders();
		if ( providers == null ) 
			return false;
		return providers.contains(LocationManager.GPS_PROVIDER);
	}
	
	private void checkAndLogin() {
//		if (UserInfo.getIntance().getLoginMode() == UserInfo.LOGIN_MODE_OFFLINE) {
			if (mBackgroundLogin != null) {
				mBackgroundLogin.start();
			}
//		}
	}
	
	private class BackgroundLogin {
		private BackLoginThread mBackLoginThread = null;
		private boolean loginStarted = false;
		private Object lock = new Object();
		
		public void start() {
			synchronized (lock) {
				if (loginStarted) {
					return;
				}
				loginStarted = true;
				
				mBackLoginThread = new BackLoginThread(this);
				mBackLoginThread.start();
			}
		}
		
		public void stop() {
			synchronized (lock) {
				if (!loginStarted) {
					return;
				}
				loginStarted = false;
				
				if (mBackLoginThread != null) {
					mBackLoginThread.setCancel();
				}
				mBackLoginThread = null;
			}			
		}
		
		public void resetStatus() {
			synchronized (lock) {
				loginStarted = false;
			}				
		}
	}
	
	class BackLoginThread extends Thread {
		private volatile boolean state = false;
		private BackgroundLogin mBackgroundLogin = null;
		
		public BackLoginThread(BackgroundLogin backgroundLogin) {
			mBackgroundLogin = backgroundLogin;
		}

		public void setCancel() {
			state = true;
		}

		@SuppressWarnings("static-access")
		@Override
		public void run() {
			String uid = UserInfo.getIntance().getUserData().getMy_name();
			if (uid == null || uid.length() <= 0) {
			}
			else {
				String pwd = mOfflineLoginManager.getPassword(uid);
				String MD5pwd = UtilStr.getEncryptPassword(pwd);
				
				String userInfo = "[{username:\"" + uid + "\",password:\""
						+ MD5pwd + "\",version:\"" 
						+ BaseActivity.m_version_base + "\"}]";
				
				String result = Client.getLogin(userInfo);
				System.out.println("action UploadService result:" + result);

				if (state) {
					return;
				}
				
				if (result == null) {
					// 网络异常
				} else if (result.equals("0")) {
					// 用户名不存在
				} else if (result.equals("1")) {
					// 密码不正确
				} else {
					Date now = new Date();
					mOfflineLoginManager.setLastLogin(uid, now);
					UserInfo.getIntance().setLoginMode(UserInfo.LOGIN_MODE_ONLINE);
				}
			}
			mBackgroundLogin.resetStatus();
		}
	}
	class UpdatehearThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String user_name=UserInfo.getIntance().getUserData().getMy_name();
			String userInfo="[{username:\""+user_name+"\",heart:\""+GlobalStatus.getInstance().getBleState()+"\"}]";
			String resultString=Client.getupdateHeart(userInfo);
			if(resultString==null){
				return;
			}
			if(resultString.equals("0")){
				resultString=Client.getupdateHeart(userInfo);
			}
			if(!resultString.equals("0")){
				UIUtil.setMessage(handler, UPDATE_HEAR,resultString);
				System.out.println("resultString:"+0);
			}
			
		}
	}
	
	@SuppressLint("HandlerLeak")
	private final class ECGCmdListener extends Thread {
		private String ip;
		private int port;
		private String uid;
		
		private Socket clientSocket;
		private ReceiveThread receiveThread;
		
		private KeepAliveThread keepAliveThread;

		private static final int KEEP_ALIVE_INTERVAL = 20;
		public static final int CHECK_COMMAND_INTERVAL = 35;
		
		private Object lockConnectionStatus = new Object();
		private volatile boolean connected = false;
		
		// Commands
		private static final int CMD_CHECK_CONNECTION = 1;
		private static final int CMD_SEND_KEEP_ALIVE = 2;
		private static final int CMD_SEND_QUIT_APP = 3;
		private static final int CMD_ACK_REQUEST = 4;
		private static final int CMD_ACK_EXEC = 5;
		
		// Notifications
		private static final int NT_SWITCH_ON_ECG = 101;
		private static final int NT_SWITCH_OFF_ECG = 102;
		
		// Errors
		private static final int ERR = 100000;
				
		private Looper mLooper;
		private ECGCmdListenerHandler mECGCLHandler;
		
		private BufferedWriter writer;
		
		public ECGCmdListener(String ip, int port, String uid) {
			this.ip = ip;
			this.port = port;
			this.uid = uid;
			
			clientSocket = null;
			keepAliveThread = null;
			receiveThread = null;
		}		
		
		@Override
		public void run() {
			Looper.prepare() ;
		    mLooper = Looper.myLooper() ;
		    mECGCLHandler = new ECGCmdListenerHandler(mLooper);
		    
		    mECGCLHandler.post(new Runnable() {
				@Override
				public void run() {
					sendCmd(CMD_CHECK_CONNECTION);
				}
			});
		    
//		    sendEcgCmdListenerReadyIntent();
		    Looper.loop();
		}
		public synchronized void checkConnection() {
			sendCmd(CMD_CHECK_CONNECTION);
		}		
		public synchronized void setQuit() {
			getLooper().quit();
		}
		private String getCommandString(int cmd) {
			String cmdStr = null;
			if (CMD_SEND_KEEP_ALIVE == cmd) {
				cmdStr = uid + "|0|00";
			}
			else if (CMD_SEND_QUIT_APP == cmd) {
				cmdStr = uid + "|1|00";
			}
			else if (CMD_ACK_REQUEST == cmd) {
				cmdStr = uid + "|0|10|01";
			}
			else if (CMD_ACK_EXEC == cmd) {
				cmdStr = uid + "|0|10|02";
			}
			
			return cmdStr;
		}
		private Handler getHandler() {
			return mECGCLHandler;
		}
		private Looper getLooper() {
			return mLooper;
		}
		private void setConnected(boolean conn) {
			synchronized(lockConnectionStatus) {
				connected = conn;
			}
		}
		private boolean getConnected() {
			synchronized(lockConnectionStatus) {
				return connected;
			}
		}
		private synchronized void checkConnected() {
			synchronized (lockConnectionStatus) {
				if (!getConnected()) reset();
			}
		}
		private void reset() {
			synchronized(lockConnectionStatus) {
				disconnect();
				connect();
			}
		}
		private synchronized void sendMsg(String msg) throws IOException {
			if (msg == null) { return; }
			
			System.out.println("action UploadService ECGCmdListener sendMsg " + msg);
			writer.write(msg);
			writer.flush();			
		}
		private synchronized void connect() {
			synchronized (lockConnectionStatus) {
				if (getConnected()) return;
				
		        try {
					clientSocket = new Socket(ip, port);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		        
		        try {
		        	System.out.println("action UploadService ECGCmdListener connect " + ip + "," + port);
					clientSocket = new Socket(ip, port);
				} catch (UnknownHostException e) {
					System.out.println("action UploadService ECGCmdListener connect exception e " + e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("action UploadService ECGCmdListener connect exception e " + e.toString());
					e.printStackTrace();
				}
		        
		        if (clientSocket == null) {
		        	System.out.println("action UploadService ECGCmdListener connect clientSocket is null");
		        	setConnected(true);
		        }
		        else {
					try {
						writer = new BufferedWriter(new OutputStreamWriter(
								clientSocket.getOutputStream()));
						System.out.println("action UploadService ECGCmdListener connect clientSocket is not null");
						
					} catch (IOException e) {
						System.out.println("action UploadService ECGCmdListener connect create clientSocket exception");
						e.printStackTrace();
						sendCmd(ERR);
						return;
					}
		        	
		        	System.out.println("action UploadService ECGCmdListener connect clientSocket create successfully.");
		        }
		        
		        System.out.println("action UploadService ECGCmdListener connect clientSocket " + clientSocket);
				if (clientSocket != null) {
					keepAliveThread = new KeepAliveThread();
					receiveThread = new ReceiveThread(clientSocket, this);
					
					keepAliveThread.start();
					receiveThread.start();
					
					setConnected(true);
				}
			}
		}
		private synchronized void disconnect() {
			synchronized (lockConnectionStatus) {
				if (clientSocket == null) { return; }
				else {
					if (keepAliveThread != null) {
						keepAliveThread.setQuit();
						keepAliveThread = null;
					}
					if (receiveThread != null) {
						receiveThread.setQuit();
						receiveThread = null;
					}
					try {
						clientSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					clientSocket = null;
					
					setConnected(false);
				}
			}
		}
		private void onErr() {
			if (HttpUtils.isNetworkAvailable(getApplicationContext())) {
				reset();
			}
		}
		private void onMsg(String msg) {
			if (msg == null) { return; }
			// 解析MSG;
		}
		private class KeepAliveThread extends Thread {
			private volatile boolean quit = false;
			public void setQuit() { quit = true; }
			
			public KeepAliveThread() {}			
			
			@Override
			public void run() {
				while (!quit) {
					System.out.println("action UploadService ECGCmdListener KeepAliveThread is running");
					sendCmd(CMD_SEND_KEEP_ALIVE);
					
					try {
						Thread.sleep(KEEP_ALIVE_INTERVAL * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}	
		private class ReceiveThread extends Thread {
			private volatile boolean quit = false;
			public void setQuit() { quit = true; }
			
			private Socket connection;
			private BufferedReader reader;
			private ECGCmdListener listener;
			
			public ReceiveThread(Socket conn, ECGCmdListener listener) {
				connection = conn;
				this.listener = listener;
				
				try {
					reader = new BufferedReader(new InputStreamReader(  
							connection.getInputStream()));
				} catch (IOException e) {
					e.printStackTrace();
					sendCmd(ERR);
				}
			}
			@Override
			public void run() {
				while (!quit) {
					String msg = null;
					try {
						msg = receiveMsg();
						if (msg != null) {
							if ("10".equals(msg)) {
								sendCmd(NT_SWITCH_ON_ECG);
							}
							if ("11".equals(msg)) {
								sendCmd(NT_SWITCH_OFF_ECG);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						sendCmd(ERR);
					}
//					if (listener != null) {
//						listener.onMsg(msg);
//					}
				}
			}
			private String receiveMsg() throws IOException {   
				String line = null;  
				while ((line = reader.readLine()) != null) {  
					System.out.println(line);
					return line;
				}
				return line;  
			}
		}
		private synchronized void sendCmd(int cmd) {			
			getHandler().obtainMessage(cmd).sendToTarget();
		}
		private final class ECGCmdListenerHandler extends Handler 
		{	  
			public ECGCmdListenerHandler(Looper looper)
			{
				super(looper) ;
			} 
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what)
				{
				case CMD_CHECK_CONNECTION:
					{
						checkConnected();
					}
					break;
				case CMD_SEND_KEEP_ALIVE:
				case CMD_SEND_QUIT_APP:
				case CMD_ACK_REQUEST:
				case CMD_ACK_EXEC:
					{
						try {
							sendMsg(getCommandString(msg.what));
						} catch (IOException e) {
							sendCmd(ERR);
							e.printStackTrace();
						}
					}
					break;
				case NT_SWITCH_ON_ECG:
					{
						sendIntentToSwitchEcgOn();
					}
					break;
				case NT_SWITCH_OFF_ECG:
					{
						sendIntentToSwitchEcgOff();
					}
					break;
				case ERR:
					{
						onErr();
					}
					break;
				default:
					break;
				}
			}		
		}
	}
	
	private void startCheckEcgCmdListenerTimer() {
		if (isCheckEcgCmdListenerTimerStarted) return;
		isCheckEcgCmdListenerTimerStarted = true;
		
		Intent intent = new Intent(); 
		intent.setAction(ACTION_ALARM_CHECK_ECG_CMD_LISTENER);  
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);		
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
		
		Calendar c=Calendar.getInstance();
		long triggerAtTime=c.getTimeInMillis();

		am.setRepeating(AlarmManager.RTC, triggerAtTime, 1000 * ECGCmdListener.CHECK_COMMAND_INTERVAL, pi);
	}
	private void stopCheckEcgCmdListenerTimer() {
		if (!isCheckEcgCmdListenerTimerStarted) return;
		isCheckEcgCmdListenerTimerStarted = false;

		Intent intent = new Intent();  
		intent.setAction(ACTION_ALARM_CHECK_ECG_CMD_LISTENER);  
		PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		alarm.cancel(sender);
	}
	private void sendCheckConnectionCommand() {
		mECGCmdListener
		.getHandler()
		.obtainMessage(ECGCmdListener.CMD_CHECK_CONNECTION)
		.sendToTarget();
	}
//	private void sendEcgCmdListenerReadyIntent() {
//		final Intent intent = new Intent(ACTION_ON_ECG_CMD_LISTENER_READY);
//		sendBroadcast(intent);
//	}
	private void sendIntentToSwitchEcgOn() {
		final Intent intentToSwitchEcgOn = new Intent(BleConnectionService.ACTION_SET_ECG_MODE);
		intentToSwitchEcgOn.putExtra("POLICY", 
									 BleConnectionService.POLICY_ECG_ONLY);
		intentToSwitchEcgOn.putExtra("SUB_POLICY", 
									 BleConnectionService.POLICY_SUB_UNKNOWN);
		intentToSwitchEcgOn.putExtra("FROM", 
									 BleConnectionService.POLICY_CTRL_FROM_SERVER);
		
		sendBroadcast(intentToSwitchEcgOn);
	}
	private void sendIntentToSwitchEcgOff() {
		final Intent intentToSwitchEcgOff = new Intent(BleConnectionService.ACTION_UNSET_ECG_MODE);
		intentToSwitchEcgOff.putExtra("FROM", 
									  BleConnectionService.POLICY_CTRL_FROM_SERVER);
		sendBroadcast(intentToSwitchEcgOff);
	}
}
