package com.langlang.service;

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;



















import com.langlang.ble.FrameAnalyzer;
import com.langlang.ble.FrameStateMachine;
import com.langlang.ble.GattServicesInfo;
import com.langlang.ble.SampleGattAttributes;
import com.langlang.data.FormFile;
//import com.langlang.db.WarningTumbleManager;



import com.langlang.data.UploadTaskInfo;
import com.langlang.elderly_langlang.R;
import com.langlang.global.Client;
import com.langlang.global.GlobalStatus;
import com.langlang.global.SettingInfo;
import com.langlang.manager.LastUploadManager;
import com.langlang.manager.UploadTaskManager;
import com.langlang.utils.MiscUtils;
import com.langlang.utils.Program;
import com.langlang.utils.SocketHttpRequester;
import com.langlang.utils.UIUtil;
import com.langlang.view.MyToast;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
	private final static String TAG = BluetoothLeService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	@SuppressWarnings("unused")
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;
	private static final int SEND_RSSI = 3;
	
	private static final int SEND_DISCONNECTED = 11;

	public final static String ACTION_GATT_CONNECTED = "com.langlang.android.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_RSSI = "com.langlang.android.bluetooth.le.ACTION_GATT_RSSI";
	public final static String ACTION_GATT_DISCONNECTED = "com.langlang.android.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.langlang.android.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.langlang.android.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA_TYPE = "com.langlang.android.bluetooth.le.EXTRA_DATA_TYPE";
	public final static String EXTRA_DATA = "com.langlang.android.bluetooth.le.EXTRA_DATA";
	
	public final static String ACTION_GATT_DATA_ALIVE
								= "com.langlang.android.bluetooth.le.ACTION_GATT_DATA_ALIVE";

	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID
			.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

	private GattServicesInfo gattServicesInfo;
	// -------------------------------------------------------------
	public static final int DATA_TYPE_ECG = 100;
	public static final int DATA_TYPE_BREATH = 101;
	public static final int DATA_TYPE_ACCE = 102;
	public static final int DATA_TYPE_FALL = 103;
	public static final int DATA_TYPE_TEMP = 104;

	public static final int DATA_TYPE_ACCE_X = 1020;
	public static final int DATA_TYPE_ACCE_Y = 1021;
	public static final int DATA_TYPE_ACCE_Z = 1022;

	public static final int DATA_FRAME_UNKNOWN = -1;
	public static final int DATA_FRAME_60 = 0; // for ECG, breath
	public static final int DATA_FRAME_90 = 1; // for Accelaration x, y, z, ...,
												// temperature
	public static final int DATA_FRAME_61 = 2;
	public static final int DATA_FRAME_62 = 3;
	public static final int DATA_FRAME_63 = 4;
	public static final int DATA_FRAME_64 = 5;
	public static final int DATA_FRAME_65 = 6;
	
	private Queue<Byte> receivedQueue = new LinkedList<Byte>();
	
	byte[] receivedBuffer = new byte[1024 * 10];
	int receivedNumber = 0;
	
	StringBuilder globalStringBuilder = new StringBuilder();
	private int globalSeqCount = 0;
	// ----------------------------------------------------

	LastUploadManager lastUploadManager = new LastUploadManager(this);
	UploadTaskManager uploadTaskManager = new UploadTaskManager(this);
//	WarningTumbleManager warningTumbleManager = new WarningTumbleManager(this);
	
	private byte[] bufferedFrames = new byte[1024];
	private int receivedByte = 0;
	private FrameAnalyzer frameAnalyzer = new FrameAnalyzer();
	
	private int[] totalECGData = new int[1024];
	private int totalECG = 0;
	public final static String ACTION_ECG_DATA_AVAILABLE = "com.langlang.android.bluetooth.le.ACTION_ECG_DATA_AVAILABLE"; 

	String nextLine = System.getProperty("line.separator");
	
	public byte bPreFrame60SequenceNO = (byte)0xff;
	public boolean isFirstPoint = false;
	
//	long mCountFrames = 0;
//	long mAnalyzedFrames = 0;
	
	StringBuilder global70FrameCountStringBuilder = new StringBuilder();
	private int global70FrameCount = 0;
	
	FrameStateMachine frameStateMachine = new FrameStateMachine();
	
	private Timer mTimer = new Timer();
	private TimerTask mTask;
	
	private int mDataCount = 0;
	private int countFrame=0;
	
	private SettingInfo settingInfo;
	
	
	private void showBox(final Context context) {
		System.out.println("action BluetoothLeService showBox called3.");

		Builder builder = new Builder(getApplicationContext());
		builder.setTitle("Title");
		builder.setMessage("This is message");
		builder.setNegativeButton("OK", null);
		Dialog dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();

		System.out.println("action BluetoothLeService showBox completed.");
	}

	private void showAlert() {
		// <showBox(BluetoothLeService.this);
		System.out.println("action BluetoothLeService showAlert()");
		showBox(getApplicationContext());
	}

	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			Log.i(TAG, "--- onConnectionStateChange status=" + status);
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				System.out.println("action BluetoothLeService onConnectionStateChange STATE_CONNECTED detected.");
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
//				Log.i(TAG, "Attempting to start service discovery:"
//						+ mBluetoothGatt.discoverServices());
				mBluetoothGatt.discoverServices();
				
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				System.out.println("action BluetoothLeService onConnectionStateChange STATE_DISCONNECTED detected.");
				
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;

				// rescan();

				// showAlert();
				// <new AlertDialog.Builder(Pedometer.this)
				// new AlertDialog.Builder(BluetoothLeService.this)
				// .setTitle("Error")
				// .setMessage("Sensor Initialize Failed. Application will close")
				// .setPositiveButton("OK", new
				// DialogInterface.OnClickListener()
				// {
				// public void onClick(DialogInterface dialog, int whichButton)
				// {
				// //<setResult(RESULT_OK);
				// //<finish();
				// }
				// })
				// .show();
				broadcastUpdate(intentAction);
				//设置蓝牙断开时是否发出提示音
				if(settingInfo.getDisconnectedNotification()==0){
					UIUtil.setMessage(handler, SEND_DISCONNECTED);	
				}
				

			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			System.out.println("action BluetoothLeService onConnectionStateChange onServicesDiscovered:"+status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
//			System.out.println("蓝牙连接状态 bletoothleservice onCharacteristicRead:" + status);
			
//			if (status == BluetoothGatt.GATT_SUCCESS) {
//				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
//			Log.i(TAG, "--- onDescriptorWriteonDescriptorWrite status="
//					+ status + ", descriptor="
//					+ descriptor.getUuid().toString());
			System.out.println("action BluetoothLeService writeFrame2 onDescriptorWrite" + status);
//			System.out.println("onServicesDiscovered onDescriptorWrite:"+status);
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			
			System.out.println("蓝牙连接状态 bletoothleservice onCharacteristicChanged:"+characteristic);
			
			
			
			// ---System.out.println("action onCharacteristicChanged");
			// ---Log.i(TAG, "--- onCharacteristicChanged");
			
//			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//			if (characteristic.getValue() != null) {
			
//			boolean s= getRssiVal() ;
			
			sendRssi();
			
//			System.out.println("action BluetoothLeService onCharacteristicChanged getRssiVal["
//					+ s + "]");
//			mCountFrames++;
//			System.out.println("action BluetoothLeService onCharacteristicChanged mCountFrames["
//								+ mCountFrames + "]");
			
			final byte[] data = characteristic.getValue();
			
//			final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
//			intent.putExtra("langlangdata", data);
//			
//			sendBroadcast(intent);
			
			synchronized (receivedQueue) {
				for (int i = 0; i < data.length; i++) {
					receivedQueue.offer(data[i]);
				}
			}
			
			
//			if (Program.convertByteToUnsignedInt(data[3]) != 112) {
//			
//			globalSeqCount++;
//			int currSeq = Program.convertByteToUnsignedInt(data[4]);
//			globalStringBuilder.append(Integer.toString(currSeq));
//			globalStringBuilder.append("\n");
//			System.out.println("action processReceivedByte globalSeqCount:" + globalSeqCount);
//			System.out.println("action processReceivedByte currSeq:" + currSeq);
//			if (globalSeqCount >= 3000) {
//				String debugSeq = globalStringBuilder.toString();
//				Program.logToSDCard(debugSeq, "debug_seq.txt");
//				globalSeqCount = 0;				
//				globalStringBuilder.setLength(0);
//			}			
//			
//			}
			
			// print original frame
			final StringBuilder stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
//			System.out.println("action data==========:"
//					+ stringBuilder.toString()+"count:["+count+"]");
			
			
			// end print original frame

//			if (data != null && data.length > 0) {
//				int pos = receivedByte;
//				for (int i = 0; i < data.length; i++) {
//					bufferedFrames[pos] = data[i];
//					pos++;
//				}
//				receivedByte = pos;
//				if (receivedByte >= 1000) {
//					processReceivedByte();
//					receivedByte = 0;
//				}
//			}		
			

//				
//				byte[]date=characteristic.getValue();
//				final StringBuilder stringBuilder = new StringBuilder(date.length);
//				for (byte byteChar : date)
//					stringBuilder.append(String.format("%02X ", byteChar));
//				Log.i(TAG, characteristic.getStringValue(0));
//				System.out.println("action buletoothservice onCharacteristicChanged:["+stringBuilder.toString()+"]");
//			}

		}

//		private void processReceivedByte() {
//			for (int i = 0; i < receivedByte; i++) {
//				frameStateMachine.consume(bufferedFrames[i]);
//				if (frameStateMachine.isFrameReady()) {
//					byte[] frameData = frameStateMachine.getFrame();
//					if (frameData.length == 17) {
//						int intValueOfByte = Program.convertByteToUnsignedInt(frameData[0]);
//						
//						if (intValueOfByte != 112) {
//
//							System.out.println("action check intValueOfByte[" + intValueOfByte + "] 112");
//	//						mAnalyzedFrames++;
//							
//							byte[] frame = new byte[frameData.length + 3];
//							frame[0] = 0x5A;
//							frame[1] = 0x5A;
//							frame[2] = 0x12;
//							
//							for (int j = 0; j < frameData.length; j++) {
//								frame[j + 3] = frameData[j];
//							}
//						
//							globalSeqCount++;
//							int currSeq = Program.convertByteToUnsignedInt(frameData[4]);
//							globalStringBuilder.append(Integer.toString(currSeq));
//							globalStringBuilder.append("\n");
//							System.out.println("action processReceivedByte globalSeqCount:" + globalSeqCount);
//							System.out.println("action processReceivedByte currSeq:" + currSeq);
//							if (globalSeqCount >= 5000) {
//								String debugSeq = globalStringBuilder.toString();
//								Program.logToSDCard(debugSeq, "debug_seq.txt");
//								globalSeqCount = 0;
//							}
//						
//							final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
//							intent.putExtra("langlangdata", frame);
//							
//							sendBroadcast(intent);
//							
//							final StringBuilder stringBuilder = new StringBuilder(frame.length);
//							for (byte byteChar : frame)
//								stringBuilder.append(String.format("%02X ", byteChar));
//							System.out.println("action data processReceivedByte:[" + stringBuilder.toString()
//											+ "] mAnalyzedFrames [" + mAnalyzedFrames + "] mCountFrames [" + mCountFrames + "]");
//						}						
//						else {
//							System.out.println("action check intValueOfByte frame70[" + intValueOfByte + "] action frame70 here");
////							System.out.println("action frame70 here.");
//							byte[] frame = new byte[frameData.length + 3];
//							frame[0] = 0x5A;
//							frame[1] = 0x5A;
//							frame[2] = 0x12;
//							
//							for (int j = 0; j < frameData.length; j++) {
//								frame[j + 3] = frameData[j];
//							}
//							
//							int aVal = Program.convertByteToUnsignedInt(frame[5]) * 256 
//												+ Program.convertByteToUnsignedInt(frame[6]);
//							int bVal = Program.convertByteToUnsignedInt(frame[7]) * 256 
//									+ Program.convertByteToUnsignedInt(frame[8]);
//							int cVal = Program.convertByteToUnsignedInt(frame[9]) * 256 
//									+ Program.convertByteToUnsignedInt(frame[10]);
//							
//							System.out.println("action aval,bval,cval:" + aVal + " " + bVal + " " + cVal);
//							
//							if ((aVal < 990) || (bVal < 990) || (cVal < 990)) {
//								final StringBuilder stringBuilder = new StringBuilder(frame.length);
//								for (byte byteChar : frame)
//									stringBuilder.append(String.format("%02X ", byteChar));
//								System.out.println("action verify data check 70 lost[" + stringBuilder.toString() + "]");								
//							} else {
//								final StringBuilder stringBuilder = new StringBuilder(frame.length);								
//								for (byte byteChar : frame)
//									stringBuilder.append(String.format("%02X ", byteChar));
//								System.out.println("action verify data check 70 do not lost[" + stringBuilder.toString() + "]");								
//							}
//						}
//						
//					}
//				}
//			} 
//
////				frameAnalyzer.consume(bufferedFrames[i]);
////				if (frameAnalyzer.canGetOneFrame()) {
////					
////					mAnalyzedFrames++;
////					byte[] frame = frameAnalyzer.getOneFrame();
////					
////					int intValueOfByte = Program.byteToInt(frame[3]);
////					if (intValueOfByte != 112) {
////						
////						globalSeqCount++;
////						int currSeq = Program.convertByteToUnsignedInt(frame[4]);
////						globalStringBuilder.append(Integer.toString(currSeq));
////						globalStringBuilder.append("\n");
////						System.out.println("action processReceivedByte globalSeqCount:" + globalSeqCount);
////						System.out.println("action processReceivedByte currSeq:" + currSeq);
////						if (globalSeqCount >= 5000) {
////							String debugSeq = globalStringBuilder.toString();
////							Program.logToSDCard(debugSeq, "debug_seq.txt");
////							globalSeqCount = 0;
////						}
////					
////					final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
////					intent.putExtra("langlangdata", frame);
////					
////					int[] ECGData = Program.getECGValues(frame);
////					intent.putExtra("ECGData", ECGData);
////					
////					sendBroadcast(intent);
////					
////					final StringBuilder stringBuilder = new StringBuilder(frame.length);
////					for (byte byteChar : frame)
////						stringBuilder.append(String.format("%02X ", byteChar));
////					System.out.println("action data processReceivedByte:[" + stringBuilder.toString()
////									+ "] mAnalyzedFrames [" + mAnalyzedFrames + "] mCountFrames [" + mCountFrames + "]");
////					
////					}
//////					Log.i(TAG, new String(data) + "\n" + stringBuilder.toString());
////					
////				}
////			}			
//		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			// ---Log.i(TAG, "--- onReadRemoteRssi status=" + status +
			// ", rssi="+ rssi);
			Intent rssIntent=new Intent(ACTION_GATT_RSSI);
			rssIntent.putExtra("rssi", rssi);
			sendBroadcast(rssIntent);
			
			GlobalStatus.getInstance().setRssi(rssi);

			if (settingInfo.getWeaknessNotification() == 0) {

				if (rssi < -85) {// 在信号强度较弱时发出声音
					UIUtil.setMessage(handler, SEND_RSSI);
				}
				System.out.println("蓝牙连接状态 bletoothleservice onReadRemoteRssi:"
						+ status);
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// ---Log.i(TAG, "--- onCharacteristicWrite status=" + status);
			
			System.out.println("蓝牙连接状态 bletoothleservice onCharacteristicWrite :"+status);
		};
	};
	
	private void processReceivedByte() {
		for (int i = 0; i < receivedNumber; i++) {
			frameStateMachine.consume(receivedBuffer[i]);
			if (frameStateMachine.isFrameReady()) {
				byte[] frameData = frameStateMachine.getFrame();
				if (frameData.length == 17) {
					int intValueOfByte = Program.convertByteToUnsignedInt(frameData[0]);
					
					if (intValueOfByte != 112) {

						System.out.println("action check intValueOfByte[" + intValueOfByte + "] 112");
//						mAnalyzedFrames++;
						
						byte[] frame = new byte[frameData.length + 3];
						frame[0] = 0x5A;
						frame[1] = 0x5A;
						frame[2] = 0x12;
						
						for (int j = 0; j < frameData.length; j++) {
							frame[j + 3] = frameData[j];
						}
					
//						globalSeqCount++;
//						int currSeq = Program.convertByteToUnsignedInt(frame[4]);
//						globalStringBuilder.append(Integer.toString(currSeq));
//						globalStringBuilder.append("\n");
//						System.out.println("action processReceivedByte globalSeqCount:" + globalSeqCount);
//						System.out.println("action processReceivedByte currSeq:" + currSeq);
//						if (globalSeqCount >= 3000) {
//							String debugSeq = globalStringBuilder.toString();
//							Program.logToSDCard(debugSeq, "debug_seq.txt");
//							globalStringBuilder.setLength(0);
//							globalSeqCount = 0;
//						}
					
						final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
						intent.putExtra("langlangdata", frame);
						
						try {
							Thread.sleep(25);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						sendBroadcast(intent);
						
						mDataCount++;
						if (mDataCount >= 75) {
							final Intent notifyDataAlive = new Intent(ACTION_GATT_DATA_ALIVE);
							sendBroadcast(notifyDataAlive);
							
							mDataCount = 0;
						}
						
//						final StringBuilder stringBuilder = new StringBuilder(frame.length);
//						for (byte byteChar : frame)
//							stringBuilder.append(String.format("%02X ", byteChar));
//						System.out.println("action data processReceivedByte:[" + stringBuilder.toString()+"]");
					}						
					else {
//						System.out.println("action check intValueOfByte frame70[" + intValueOfByte + "] action frame70 here");
////						System.out.println("action frame70 here.");
//						byte[] frame = new byte[frameData.length + 3];
//						frame[0] = 0x5A;
//						frame[1] = 0x5A;
//						frame[2] = 0x12;
//						
//						for (int j = 0; j < frameData.length; j++) {
//							frame[j + 3] = frameData[j];
//						}
//						
//						int aVal = Program.convertByteToUnsignedInt(frame[5]) * 256 
//											+ Program.convertByteToUnsignedInt(frame[6]);
//						int bVal = Program.convertByteToUnsignedInt(frame[7]) * 256 
//								+ Program.convertByteToUnsignedInt(frame[8]);
//						int cVal = Program.convertByteToUnsignedInt(frame[9]) * 256 
//								+ Program.convertByteToUnsignedInt(frame[10]);
//						
//						global70FrameCount++;
//						for (byte byteChar : frame) {
//							global70FrameCountStringBuilder.append(String.format("%02X ", byteChar));
//						}
//						global70FrameCountStringBuilder.append("\n");
//						System.out.println("action global70FrameCount[" + global70FrameCount + "]");
//						if (global70FrameCount >= 100) {
//							String debug70Frame = global70FrameCountStringBuilder.toString();
//							Program.logToSDCard(debug70Frame, "debug_70_frame.txt");
//							global70FrameCountStringBuilder.setLength(0);
//							global70FrameCount = 0;
//						}						
//						
//						System.out.println("action aval,bval,cval:" + aVal + " " + bVal + " " + cVal);
//						
//						if ((aVal < 990) || (bVal < 990) || (cVal < 990)) {
//							final StringBuilder stringBuilder = new StringBuilder(frame.length);
//							for (byte byteChar : frame)
//								stringBuilder.append(String.format("%02X ", byteChar));
//							System.out.println("action verify data check 70 lost[" + stringBuilder.toString() + "]");								
//						} else {
//							final StringBuilder stringBuilder = new StringBuilder(frame.length);								
//							for (byte byteChar : frame)
//								stringBuilder.append(String.format("%02X ", byteChar));
//							System.out.println("action verify data check 70 do not lost[" + stringBuilder.toString() + "]");								
//						}
					}
					
				}
			}
		} 		
	}

	/**
	 * Broadcast message
	 * 
	 * @param action
	 *            action type.
	 */
	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	/**
	 * Broadcast message with characteristic values.
	 * 
	 * @param action
	 *            action type
	 * @param characteristic
	 */
	int count=0;
	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
//		System.out.println("action broadcastUpdate size:["+count+"]");
		// This is special handling for the Heart Rate Measurement profile. Data
		// parsing is
		// carried out as per profile specifications:
		// http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
		if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
			System.out
					.println("action mainsactivity disconnectide oldaddress UUID_HEART_RATE_MEASUREMENT");
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
				Log.d(TAG, "Heart rate format UINT16.");
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
				Log.d(TAG, "Heart rate format UINT8.");
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			// --- Log.d(TAG, String.format("Received heart rate: %d",
			// heartRate));
			intent.putExtra(EXTRA_DATA_TYPE,
					UUID_HEART_RATE_MEASUREMENT.toString());
			intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));

		} else {
		
			// ---System.out.println("action: broadcastUpdate called------");
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			intent.putExtra("langlangdata", data);
			if (data != null && data.length > 0) {
				boolean isECGDataEmpty = totalECG == 0? true: false;
				// ---
				// System.out.println("action: data[data != null && data.length > 0]");
				boolean hasLostFrame = false;
				int[] tmpECGData = null;
				int[] tmpRespData = null;

				int dataFrameType = DATA_FRAME_UNKNOWN;
				int intValueOfByte = Program.byteToInt(data[3]);
//				if (intValueOfByte == 96) {
//					dataFrameType = DATA_FRAME_60;
//				} else if (intValueOfByte == 144) {
//					dataFrameType = DATA_FRAME_90;
//				}
				if (intValueOfByte == 96) {
					dataFrameType = DATA_FRAME_60;
				}
				if (intValueOfByte == 97) {
					dataFrameType = DATA_FRAME_61;
				}
				if (intValueOfByte == 98) {
					dataFrameType = DATA_FRAME_62;
				}
				if (intValueOfByte == 99) {
					dataFrameType = DATA_FRAME_63;
				} 
				if (intValueOfByte == 100) {
					dataFrameType = DATA_FRAME_64;
				}
				if (intValueOfByte == 101) {
					dataFrameType = DATA_FRAME_65;
				}
				
				///<if (dataFrameType == DATA_FRAME_60) {
//				int[] origECGData = Program.getECGValues(data);
//				int[] origRespData = Program.getResp(data);
				
				int[] ECGData = Program.getECGValues(data);
				int[] respData = Program.getResp(data);

//				int[] ECGData = new int[origECGData.length * 2];
//				int[] respData = new int[origRespData.length * 2];
//				for (int i = 0; i < origECGData.length; i++) {
//					ECGData[i * 2] = origECGData[i];
//					ECGData[i * 2 + 1] = origECGData[i];
//				}
//				for (int i = 0; i < origRespData.length; i++) {
//					respData[i * 2] = origRespData[i];
//					respData[i * 2 + 1] = origRespData[i];
//				}
				
//				if (isFirstPoint) {
//					isFirstPoint = false;
//				} else {
//					//-- check whether we have lost some frames--
//					int uintValOfFrame60SeqNo = Program.convertByteToUnsignedInt(data[4]);
//					int uintValOfPreFrame60SeqNo = Program.convertByteToUnsignedInt(bPreFrame60SequenceNO);
//							
//					if ((uintValOfFrame60SeqNo - uintValOfPreFrame60SeqNo) > 1) {
//						hasLostFrame = true;
//						
//						System.out.println("action BluetoothLeService received ble data frame error detected0.[" 
//										+ String.format("%d %d", uintValOfFrame60SeqNo, uintValOfPreFrame60SeqNo) + "],[" 
//										+ String.format("%02X %02X", data[4], bPreFrame60SequenceNO) + "]");
//								
//								// fix the lost data
//						int lostFrame = uintValOfFrame60SeqNo - uintValOfPreFrame60SeqNo - 1;
//						
//						//125HZ采样率插值成250HZ
////						lostFrame = lostFrame * 2;
////								int[] lostECGData = new int[lostFrame * 5];
////								for (int i = 0; i < lostFrame * 5; i++) {
////									lostECGData[i] = ECGData[0];
////								}								
////								int[] lostRespData = new int[lostFrame * 2];
////								for (int i = 0; i < lostFrame * 5; i++) {
////									lostRespData[i] = respData[0];
////								}
//								
//						tmpECGData = new int[lostFrame * 5 + ECGData.length];
//						for(int i = 0; i < lostFrame * 5; i++) {
//							tmpECGData[i] = ECGData[0] ;
//						}
//						for(int i = 0; i < ECGData.length; i++) {
//							tmpECGData[lostFrame * 5 + i] = ECGData[i];
//						}						
//						
//						tmpRespData = new int[lostFrame * 2 + respData.length];
//						for(int i = 0; i < lostFrame * 2; i++) {
//							tmpRespData[i] = respData[0] ;
//						}
//						for(int i = 0; i < respData.length; i++) {
//							tmpRespData[lostFrame * 2 + i] = respData[i];
//						}								
//					} else if (uintValOfFrame60SeqNo < uintValOfPreFrame60SeqNo) {
//						hasLostFrame = true;
//						
//						System.out.println("action BluetoothLeService received ble data frame error detected1.[" 
//								+ String.format("%d %d", uintValOfFrame60SeqNo, uintValOfPreFrame60SeqNo) + "],[" 
//								+ String.format("%02X %02X", data[4], bPreFrame60SequenceNO) + "]");
//				
//						// fix the lost data
//						int lostFrame = (255 - uintValOfPreFrame60SeqNo) + uintValOfFrame60SeqNo;
//						
//						//125HZ采样率插值成250HZ
////						lostFrame = lostFrame * 2;
//						
////								int[] lostECGData = new int[lostFrame * 5];
////								for (int i = 0; i < lostFrame * 5; i++) {
////									lostECGData[i] = ECGData[0];
////								}								
////								int[] lostRespData = new int[lostFrame * 2];
////								for (int i = 0; i < lostFrame * 5; i++) {
////									lostRespData[i] = respData[0];
////								}
//						
//						tmpECGData = new int[lostFrame * 5 + ECGData.length];
//						for(int i = 0; i < lostFrame * 5; i++) {
//							tmpECGData[i] = ECGData[0] ;
//						}
//						for(int i = 0; i < ECGData.length; i++) {
//							tmpECGData[lostFrame * 5 + i] = ECGData[i];
//						}
//						tmpRespData = new int[lostFrame * 2 + respData.length];
//						for(int i = 0; i < lostFrame * 2; i++) {
//							tmpRespData[i] = respData[0] ;
//						}
//						for(int i = 0; i < respData.length; i++) {
//							tmpRespData[lostFrame * 2 + i] = respData[i];
//						}								
//					}
//				}						
//				bPreFrame60SequenceNO =  data[4];
				
				if (hasLostFrame) {
					intent.putExtra("ECGData", tmpECGData);
//						totalECGPoint += (tmpECGData.length);	
					
//					for (int i = 0; i < tmpECGData.length; i++) {
//						totalECGData[i + totalECG] = tmpECGData[i];
//					}
//					totalECG += tmpECGData.length;
//					
//					if (!isECGDataEmpty) {
//						final Intent ecgDataAvailableintent = new Intent(ACTION_ECG_DATA_AVAILABLE);
//						int[] intentECG = new int[totalECG];
//						for (int i = 0; i < totalECG; i++) {
//							intentECG[i] = totalECGData[i];
//						}
//						ecgDataAvailableintent.putExtra("ECGData", intentECG);
//						sendBroadcast(ecgDataAvailableintent);
//						totalECG = 0;
//					}
					
					intent.putExtra("RespData", tmpRespData);						
				} else {
					intent.putExtra("ECGData", ECGData);
//						totalECGPoint += (ECGData.length);
//					for (int i = 0; i < ECGData.length; i++) {
//						totalECGData[i + totalECG] = ECGData[i];
//					}
//					totalECG += ECGData.length;
//					
//					if (!isECGDataEmpty) {
//						final Intent ecgDataAvailableintent = new Intent(ACTION_ECG_DATA_AVAILABLE);
//						int[] intentECG = new int[totalECG];
//						for (int i = 0; i < totalECG; i++) {
//							intentECG[i] = totalECGData[i];
//						}
//						ecgDataAvailableintent.putExtra("ECGData", intentECG);
//						sendBroadcast(ecgDataAvailableintent);
//						totalECG = 0;
//					}
					
					intent.putExtra("RespData", respData);
				}
				System.out.println("action: data[ broadcastUpdate]:"
							+ respData);

				///<}
				if (dataFrameType == DATA_FRAME_60) {
					int[] TEMPData = Program.getTemp(data);
				}
				if (dataFrameType == DATA_FRAME_61) {
					int[] accelerationX = Program.getAccelerationXData(data);
				}
				if (dataFrameType == DATA_FRAME_62) {
					int[] accelerationY = Program.getAccelerationYData(data);
				}
				if (dataFrameType == DATA_FRAME_63) {
					int[] accelerationZ = Program.getAccelerationZData(data);
				}
				if (dataFrameType == DATA_FRAME_64) {
					int[] stepCount = Program.getStepCountData(data);
				}
				if (dataFrameType == DATA_FRAME_65) {
					int[] tumbleData = Program.getTumbleData(data);
				}
				
				/***
				else if (dataFrameType == DATA_FRAME_90) {
					//-----------------------------------------------------------------
					final StringBuilder stringBuilder = new StringBuilder(data.length);
					for (byte byteChar : data)
						stringBuilder.append(String.format("%02X ", byteChar));
					Log.i(TAG, new String(data) + "\n" + stringBuilder.toString());
					intent.putExtra(EXTRA_DATA,
							new String(data) + "\n" + stringBuilder.toString());
//					System.out.println("action stringBuilder data==========:"
//							+ stringBuilder.toString());
					//-----------------------------------------------------------------
					int[] TEMPData = Program.getTemp(data);
					int[] tumbleData = Program.getTumbleData(data);
					int[] accelerationX = Program.getAccelerationXData(data);
					int[] accelerationY = Program.getAccelerationYData(data);
					int[] accelerationZ = Program.getAccelerationZData(data);
//					intent.putExtra("TEMPData", TEMPData);
//					intent.putExtra("tumbleData", tumbleData);
//					intent.putExtra("accelerationX", accelerationX);
//					intent.putExtra("accelerationY", accelerationY);
//					intent.putExtra("accelerationZ", accelerationZ);
					System.out.println("action mainsactivity ble tumbleData2:"+Arrays.toString(tumbleData));
					if(!"[0]".equals(Arrays.toString(tumbleData))){
						System.out.println("action mainsactivity ble tumbleData3:"+Arrays.toString(tumbleData));
						intent.putExtra("tumbleData", tumbleData);
						Date now = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat(Program.MINUTE_FORMAT);
//						warningTumbleManager.increase(sdf.format(now)); 
						if (HttpTools.isNetworkAvailable(getApplicationContext()) == false) {
							Toast.makeText(getApplicationContext(), "网络连接失败,请连接网络",
									Toast.LENGTH_SHORT).show();
						}else{
							System.out.println("action mainsactivity ble tumbleData"+Arrays.toString(tumbleData));
									new SendAlarm().start();
									
						}
					}else {
						System.out.println("action mainsactivity ble tumbleData1:"+Arrays.toString(tumbleData));
					}
					
				}
				***/
			}
			final StringBuilder stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
			Log.i(TAG, new String(data) + "\n" + stringBuilder.toString());
			
//			intent.putExtra(EXTRA_DATA,
//					new String(data) + "\n" + stringBuilder.toString());

//			System.out.println("action data==========:"
//					+ stringBuilder.toString()+"count:["+count+"]");
			System.out.println("action data==========:"
					+ stringBuilder.toString());
			
//			saveDebugInfoToFile(stringBuilder.toString());
		}
		sendBroadcast(intent);
	}
	private SharedPreferences peopledatas;
	private String mName;
	private final static int SEND_A=4;
	private void mappingData(){
		peopledatas = this.getSharedPreferences("peopleinfo",
				MODE_PRIVATE);
		String info = peopledatas.getString("userinfo", "");
		try {
			JSONArray jsonArray = new JSONArray(info);
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			mName = jsonObject.getString("userName");
		
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	class SendAlarm extends Thread{
		@Override
		public void run() {
			Date date = new Date();
			String currDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(date);
			String tuminfo = "[{username:\"" + mName + "\",alertType:\"" + "5"
					+ "\",alertTime:\"" + currDate + "\",value:\"" + "1"
					+ "\",deviceNumber:\"" + "1" + "\"}]";
			Message message = Message.obtain();
			message.what = SEND_A;
			message.obj = Client.getsendAlarm(tuminfo);
			handler.sendMessage(message);
		}
	}
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SEND_A) {
				String daString = msg.obj.toString();
				if ("1".equals(daString)) {
					Toast.makeText(getApplicationContext(), "",
							Toast.LENGTH_SHORT).show();
				} else {
					new SendAlarm().start();
				}
			}
			if(msg.what==SEND_RSSI){
				MyToast.show(getApplication(), "", true, Toast.LENGTH_SHORT, R.raw.ble_rssi).show();
			}
			if(msg.what==SEND_DISCONNECTED){
				MyToast.show(getApplication(), "", true, Toast.LENGTH_SHORT, R.raw.ble_deconected).show();
			}
		};
	};
	/**
	 * 保存原始数据文件
	 * 
	 * @param string
	 */
	private void saveDebugInfoToFile(String string) {
		String fileName = Program.getSDPath() + File.separator + "deb_kkk.csv";
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName, true)));
			out.write(string + nextLine);
		} catch (Exception e) {
			// --- System.out.println("action saveListToFile IOException");
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out = null;
		}
	}

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		// close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}
		return true;
	}

	@Override
	public void onCreate() {
//		android.os.Process.setThreadPriority(19);
		super.onCreate();
//		new Thread(uploadRoutine).start();
//		reastOncreat(); 
		isFirstPoint = true;
//		totalECGPoint = 0;
		totalECG = 0;
		mappingData();
		frameStateMachine.reset();
		frameAnalyzer.reset();
		
		mDataCount = 0;
		
		receivedNumber = 0;
		receivedQueue.clear();
		startTimer();
		settingInfo=new SettingInfo(getApplicationContext());
		System.out.println("action BluetoothLeSetvice Oncreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cancelTimer();
		this.close();
		receivedQueue.clear();
		receivedQueue = null;
		System.out.println("action onDestroy------- bluele ");
	};

//	Runnable uploadRoutine = new Runnable() {
//		@Override
//		public void run() {
//			while (true) {
//				try {
//					// System.out
//					// .println("action BluetoothLeService uploadRoutine is running.");
//					Thread.sleep(10 * 1000);
//					checkAndUpload();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//	};

//	private void checkAndUpload() {
//		System.out.println("action BluetoothLeService checkAndUpload.");
//		UploadTaskInfo uploadTaskInfo = uploadTaskManager.fetchOneUploadTask();
//		System.out
//				.println("action BluetoothLeService checkAndUpload. uploadTaskInfo="
//						+ uploadTaskInfo);
//		if (uploadTaskInfo != null) {
//			boolean ok = false;
//			String path = Program
//					.getDataPathByMinute(uploadTaskInfo.minuteData);
//			System.out
//					.println("action checkAndUpload UploadTaskInfo["
//							+ uploadTaskInfo.id + ","
//							+ uploadTaskInfo.minuteData + "]");
//			System.out.println("action checkAndUpload path[" + path + "]");
//
//			File dir = new File(path);
//
//			if (!dir.exists()) {
//				System.out.println("action checkAndUpload path does not exist["
//						+ path + "]");
//				uploadTaskManager.delete(uploadTaskInfo.id);
//				return;
//			}
//			System.out.println("action checkAndUpload path exist[" + path
//					+ "] ");
//
//			File files[] = dir.listFiles();
//			for (int i = 0; i < files.length; i++) {
//				System.out.println("action checkAndUpload start uploading["
//						+ files[i] + "]");
//				try {
//					System.out.println("action uploadFile start["
//							+ files[i].getPath() + "]");
//					ok = uploadFile(files[i]);
//					System.out.println("action uploadFile completed ["
//							+ files[i].getPath() + "] ok [" + ok + "]");
//				} catch (Exception e) {
//					System.out.println("action uploadFile exception["
//							+ files[i].getPath() + "] e = [" + e.toString()
//							+ "]");
//					ok = false;
//					break;
//				}
//			}
//			if (ok) {
//				uploadTaskManager.delete(uploadTaskInfo.id);
//			}
//		}
//	}

//	public boolean uploadFile(File file) throws Exception {
//		boolean flag = false;
//		try {
//			// //<String requestUrl =
//			// "http://192.168.0.118:8082/PullService/index.action";
//			String requestUrl = Client.UPLOAD_URL;
//			// 请求普通信息
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("username", "android");
//			params.put("pwd", "zhangsan");
//			params.put("age", "21");
//			params.put("fileName", file.getName());
//
//			System.out.println("action: uploadFile file.size[" + file.length()
//					+ "]");
//			// 上传文件
//			FormFile formfile = new FormFile(file.getName(), file, "file",
//					"application/octet-stream");
//			flag = SocketHttpRequester.post(requestUrl, params, formfile);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//		return flag;
//	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		System.out
				.println("action bleconnection bluetoothseviceconnect1 ");
		if (mBluetoothAdapter == null || address == null) {
			System.out
					.println("action bleconnection bluetoothseviceconnect2 ");
			return false;
		}
		// Previously connected device. Try to reconnect. (先前连接的设备。 尝试重新连接)
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			System.out
					.println("action bleconnection bluetoothseviceconnect3 ");
			if (mBluetoothGatt.connect()) {
				System.out
						.println("action bleconnection bluetoothseviceconnect4 ");
				mConnectionState = STATE_CONNECTING;
				// connect(address);//add
				return true;
			} else {
				return false;
			}
		}
		System.out
				.println("action bleconnection bluetoothseviceconnect5 ");
		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			System.out
					.println("action bleconnection bluetoothseviceconnect6");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		System.out.println("action: connectGatt");
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		System.out
				.println("action bleconnection bluetoothseviceconnect7");
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		System.out
				.println("action action ssssssmainsactivity disconnectide oldaddress  bleservice DISCONNECT1");
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {

			System.out
					.println("action action ssssssmainsactivity disconnectide oldaddress  bleservice DISCONNECT2");
			return;
		}
		System.out
				.println("action action ssssssmainsactivity disconnectide oldaddress  bleservice DISCONNECT3");
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.writeCharacteristic(characteristic);
	}
	
	public void startTimer() {	
		mTask = new TimerTask() {
			@Override
			public void run() {
				synchronized (receivedQueue) {
					receivedNumber = 0;
					int qLen = receivedQueue.size();
					for (int i = 0; i < qLen; i++) {
						receivedBuffer[i] = receivedQueue.poll();
						receivedNumber++;
					}
				}
				
				processReceivedByte();
			}
		};
		//mTimer.schedule(mTask, 0, 40);
//		mTimer.schedule(mTask, 0, 2000);
		mTimer.schedule(mTask, 1000, 500);
	}
	
	public void cancelTimer() {
		mTimer.cancel();
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
				.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		if (descriptor != null) {
			Log.i(TAG, "write descriptor");
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
		/*
		 * // This is specific to Heart Rate Measurement. if
		 * (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		 * System
		 * .out.println("characteristic.getUuid() == "+characteristic.getUuid
		 * ()+", "); BluetoothGattDescriptor descriptor =
		 * characteristic.getDescriptor
		 * (UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		 * descriptor
		 * .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		 * mBluetoothGatt.writeDescriptor(descriptor); }
		 */
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */

	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;
		return mBluetoothGatt.getServices();
	}
	
	/**
	 * 两秒发送一次蓝牙信号
	 */
	private void sendRssi(){
		int checkTimes = 1;
		if (GlobalStatus.getInstance().getCurrentECGMode() == DataStorageService.MODE_ECG_ECG) {
			checkTimes = 50;
		}
		countFrame++;
		System.out.println("countFrame:"+countFrame);
		if(countFrame >= checkTimes){
			getRssiVal();
			countFrame=0;
		}
		
	}

	/**
	 * Read the RSSI for a connected remote device.
	 */
	public boolean getRssiVal() {
		if (mBluetoothGatt == null)
			return false;
		return mBluetoothGatt.readRemoteRssi();
	}

	/**
	 * Return gatt services info object.
	 * 
	 * @param resources
	 * @param refresh
	 * @return
	 */
	public GattServicesInfo getGattServicesInfo(boolean refresh,
			Resources resources) {
		if (gattServicesInfo == null || refresh) {
			gattServicesInfo = new GattServicesInfo(getSupportedGattServices(),
					resources);
		}
		return gattServicesInfo;
	}

	@Override
	public boolean stopService(Intent name) {
		// TODO Auto-generated method stub
		System.out.println("action serviced ble");
		return super.stopService(name);
	}

	public BluetoothAdapter getBluetoothAdapter() {
		return mBluetoothAdapter;
	}

	private void rescan() {
		mBluetoothAdapter.startLeScan(mLeScanCallback);
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			new Runnable() {
				@Override
				public void run() {
					System.out
							.println("action BluetoothLeService mLeScanCallback");
				}
			};
		}
	};
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	private void reastOncreat() {
		Notification notification = new Notification();
		startForeground(1, notification);
	}
	
	public void writeFrame(BluetoothGattCharacteristic characteristic, byte[] frame) {
		if (mBluetoothGatt != null) {
			characteristic.setValue(frame);
			System.out.println("action BluetoothLeService writeFrame3[" + MiscUtils.dumpBytesAsString(frame));
//			mBluetoothGatt.beginReliableWrite();
			mBluetoothGatt.writeCharacteristic(characteristic);
//			mBluetoothGatt.executeReliableWrite();
		}
	}
}
