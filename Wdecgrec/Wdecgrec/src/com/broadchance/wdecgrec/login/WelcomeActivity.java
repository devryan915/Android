package com.broadchance.wdecgrec.login;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.SkinManager;
import com.broadchance.utils.AESEncryptor;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.FileUtil;
import com.broadchance.utils.FilterUtil;
import com.broadchance.utils.LogUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.adapter.WecomeViewPagerAdapter;
import com.broadchance.wdecgrec.services.BluetoothLeService;
import com.broadchance.wdecgrec.test.TestJNI;

public class WelcomeActivity extends BaseActivity {
	private final static String TAG = WelcomeActivity.class.getSimpleName();
	private ViewPager viewPager;
	private WecomeViewPagerAdapter vpAdapter;
	private List<View> views;
	private final static int PAGER_COUNT = 4;
	// 底部小店图片
	private ImageView[] dots;

	// 记录当前选中位置
	private int currentIndex;

	/**
	 * GPS
	 */
	// private GPSReceiver receiver = null;

	// private void sendECGData(IntBuffer buffer, Integer value, String action)
	// {
	// if ((!buffer.hasRemaining() || value == null) && buffer.position() > 0) {
	// Intent intent = new Intent();
	// intent.setAction(action);
	// int[] des = new int[buffer.position()];
	// System.arraycopy(buffer.array(), 0, des, 0, des.length);
	// buffer.position(0);
	// intent.putExtra(BluetoothLeService.EXTRA_DATA, des);
	// value = 9;
	// }
	// }

	/**
	 * @throws IOException
	 * @throws UnknownHostException
	 * 
	 */
	void testSocket() throws UnknownHostException, IOException {
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user == null) {
			LogUtil.d(TAG, "用户数据不存在");
			return;
		}
		// 创建一个Socket对象，指定服务器端的IP地址和端口号
		Socket socket = new Socket("127.0.0.1", 8885);
		// 从Socket当中得到OutputStream
		OutputStream os = socket.getOutputStream();
		DataOutputStream outputStream = new DataOutputStream(os);
		outputStream.write("(".getBytes("US-ASCII"));
		outputStream.write("NOW".getBytes("US-ASCII"));
		outputStream.write(user.getUserID().getBytes("US-ASCII"));
		// outputStream.writeLong(length);
		byte buffer[] = new byte[4 * 1024];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSS");
		outputStream.write(sdf.format(new java.util.Date())
				.getBytes("US-ASCII"));
		outputStream.write(")".getBytes("US-ASCII"));
		outputStream.flush();
		outputStream.close();
		socket.close();
	}

	void testJNI() {
		String jniStr = new TestJNI().stringFromJNI();
		String jniStrStatic = TestJNI.stringFromJNIStatic();
		TestJNI out;
		TestJNI in = new TestJNI();
		out = in.testObjParam();
		TestJNI testValue = new TestJNI();
		testValue.testSetValue(12);
		int value = testValue.testGetValue();
		TestJNI testValue1 = new TestJNI();
		testValue1.testSetValue(15);
		int value1 = testValue1.testGetValue();
		TestJNI testValue2 = new TestJNI();
		int value2 = testValue2.testGetValue();
		int[] intArray = new int[] { 0, 1, 2, 3 };
		int[] outIntArray = new TestJNI().testIntArray(intArray);
		intArray = new int[] { 1, 2, 3 };
		outIntArray = new TestJNI().testIntArray(intArray);
		System.out.println(jniStr);
	}

	void testFilter() {
		FilterUtil filter = new FilterUtil();
		Random random = new Random();
		int count = 0;
		while (true) {
			count++;
			int[] inputData = new int[] { random.nextInt(65535),
					random.nextInt(65535), random.nextInt(65535),
					random.nextInt(65535) };
			inputData = new int[] { 4352, 34, 4369, 21845 };
			StringBuffer sbBuffer = new StringBuffer();
			for (int i = 0; i < inputData.length; i++) {
				sbBuffer.append(inputData[i] + " ");
			}
			System.out.println("intput " + count + " " + sbBuffer.toString());
			int[] outData = filter.getECGDataII(inputData);
			sbBuffer = new StringBuffer();
			for (int i = 0; i < outData.length; i++) {
				sbBuffer.append(outData[i] + " ");
			}
			System.out.println("output " + count + " " + sbBuffer.toString());
		}
	}

	void testRandomRead() throws IOException {
		int pow = (int) Math.pow(2, 6);
		System.out.println(pow);
		// 测试随机读取
		SimpleDateFormat sdf = new SimpleDateFormat(
				ConstantConfig.DATA_DATE_FORMAT);
		File ecgFile = new File(FileUtil.getEcgDir() + sdf.format(new Date())
				+ ConstantConfig.ECGDATA_SUFFIX);
		ecgFile = new File(FileUtil.getEcgDir() + "test"
				+ ConstantConfig.ECGDATA_SUFFIX);
		boolean canWrite = false;
		if (!ecgFile.exists()) {
			canWrite = ecgFile.createNewFile();
		} else {
			canWrite = true;
		}
		if (canWrite) {
			RandomAccessFile raf = new RandomAccessFile(ecgFile, "rw");
			FileChannel fc = raf.getChannel();
			MappedByteBuffer mbb = fc
					.map(FileChannel.MapMode.READ_WRITE, 0, 10);
			mbb.put((byte) 1);
			mbb.put(9, (byte) 10);
			mbb = fc.map(FileChannel.MapMode.READ_WRITE, 9, 10);
			mbb.put(0, (byte) 11);
			mbb.put(9, (byte) 20);
			raf.close();
		}
		FileInputStream fis = new FileInputStream(ecgFile);
		FileChannel fc = fis.getChannel();
		ByteBuffer byteBuffer = ByteBuffer.allocate(20);
		fc.read(byteBuffer);
		fis.close();
	}

	void testDate() {
		Date date = new Date(123456789);
		long dateTime = date.getTime();
		System.out.println(dateTime);
	}

	void testQueue() {
		ConcurrentLinkedQueue<Float> queue = new ConcurrentLinkedQueue<Float>();
		queue.add(1f);
		queue.add(2f);
		queue.add(3f);
		queue.add(4f);
		queue.add(5f);
		queue.add(6f);
		Float[] queueArray = new Float[0];
		System.out.println(queueArray.length);
		queueArray = queue.toArray(queueArray);
		// System.arraycopy(queue, 0, queueArray, 0, 6);
		System.out.println(queueArray.length);
	}

	int count = 0;

	void testExecutorService() {
		double test = Math.floor(1762 * 0.125f);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println(count++);
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
		executor.shutdown();
	}

	void testAES() throws Exception {
		String seed = "13045659626";
		String cleartext = "123214";
		String encrypted = AESEncryptor.encrypt(seed, cleartext);
		String hexStr = AESEncryptor.fromHex(encrypted);
		String decryptStr = AESEncryptor.decrypt(seed, encrypted);
		System.out.println(decryptStr);
	}

	@SuppressWarnings("unused")
	void test() {
		try {
			testAES();
			// testQueue();
			// testExecutorService();
			// byte b = 48;
			// char ch = (char) b;
			// System.out.println(ch);
			// testJNI();
			// testDate();
			// testFilter();
			// testSocket();
			// testRandomRead();
			/*
			 * // 测试 // Integer value = null; // IntBuffer miiBuffer =
			 * IntBuffer.allocate(20); // miiBuffer.put(1, 1); //
			 * miiBuffer.put(2); // miiBuffer.put(3); // miiBuffer.put(4); // //
			 * sendECGData(miiBuffer, value, "ss"); // Intent intent = new
			 * Intent(); // int[] des = new int[miiBuffer.position()]; // int[]
			 * dis = new int[miiBuffer.position()]; //
			 * System.arraycopy(miiBuffer.array(), 0, des, 0, des.length); //
			 * miiBuffer.position(0); // miiBuffer.get(dis); //
			 * miiBuffer.put(5); // miiBuffer.put(6); // intent.putExtra("test",
			 * des); // int[] array = intent.getIntArrayExtra("test"); //
			 * array[2] = 9; // System.out.println(array);
			 */

			/*
			 * 测试设备绑定替换修改密码 String userID = "09e95aba62f24df78c80bccf3043530b";
			 * String iDCode = "74:DA:EA:9F:93:20"; String password = "111111";
			 * String oldpassword = "123456";
			 * serverService.ChangeDeviceUserID(userID, iDCode, password,
			 * oldpassword, new HttpReqCallBack<StringResponse>() {
			 * 
			 * @Override public Activity getReqActivity() { return
			 * LoginActivity.this; }
			 * 
			 * @Override public void doSuccess(StringResponse result) { if
			 * (result.isIsOK()) { showToast("提交成功！"); } else {
			 * showResponseMsg(result.Code); } }
			 * 
			 * @Override public void doError(String result) { if
			 * (ConstantConfig.Debug) { showToast(result); } } });
			 */
			/*
			 * String userID = "09e95aba62f24df78c80bccf3043530b";
			 * serverService.GetUserBase(userID, new
			 * HttpReqCallBack<UIUserInfoBaseResponse>() {
			 * 
			 * @Override public Activity getReqActivity() { return
			 * LoginActivity.this; }
			 * 
			 * @Override public void doSuccess(UIUserInfoBaseResponse result) {
			 * if (result.isIsOK()) { showToast("GetUserBase请求成功！"); } else {
			 * showResponseMsg(result.Code); } }
			 * 
			 * @Override public void doError(String result) { if
			 * (ConstantConfig.Debug) { showToast(result); } } });
			 */
		} catch (Exception e) {
			LogUtil.e(TAG, e);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ConstantConfig.Debug) {
			test();
		}
		startBleService();
		boolean isHideWelcome = getPreferencesBoolean(ConstantConfig.PREFERENCES_ISHIDEWELCOME);
		if (isHideWelcome) {
			showLogin();
			return;
		}
		setContentView(R.layout.activity_welcome);
		initView();
		putPreferencesBoolean(ConstantConfig.PREFERENCES_ISHIDEWELCOME, true);

	}

	/**
	 * 启动ble服务
	 */
	private void startBleService() {
		Intent bleServiceintent = new Intent(WelcomeActivity.this,
				BluetoothLeService.class);
		startService(bleServiceintent);
	}

	private void stopBleService() {
		Intent bleServiceintent = new Intent(WelcomeActivity.this,
				BluetoothLeService.class);
		stopService(bleServiceintent);
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.welcomeViews);
		initViews();
		vpAdapter = new WecomeViewPagerAdapter(views);
		viewPager.setAdapter(vpAdapter);
		// 绑定回调
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 设置底部小点选中状态
				setCurDot(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		// 初始化底部小点
		initDots();
	}

	public Bitmap readBitmap(int id) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;// 表示16位位图 565代表对应三原色占的位数
		opt.inInputShareable = true;
		opt.inPurgeable = true;// 设置图片可以被回收
		InputStream is = getResources().openRawResource(id);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	private void initViews() {
		views = new ArrayList<View>();
		View view = null;
		ImageView imageView = null;
		for (int i = 0; i < PAGER_COUNT; i++) {
			view = LayoutInflater.from(WelcomeActivity.this).inflate(
					SkinManager.getInstance().getLocalLayout(
							"viewpager_welcome_page" + (i + 1)), null);
			imageView = (ImageView) view.findViewById(R.id.imageView);
			// Drawable drawable = null;
			// TODO 从服务端获取图片
			switch (i) {
			case 0:
				// drawable =
				// getResources().getDrawable(R.drawable.welcome_page4);
				imageView.setImageBitmap(readBitmap(R.drawable.welcome_page1));
				break;
			case 1:
				// drawable =
				// getResources().getDrawable(R.drawable.welcome_page2);
				// imageView.setImageBitmap(ZipUtil.compressBitMap(drawable));
				imageView.setImageBitmap(readBitmap(R.drawable.welcome_page2));
				// imageView.setImageResource(R.drawable.welcome_page2);
				break;
			case 2:
				// drawable =
				// getResources().getDrawable(R.drawable.welcome_page3);
				// imageView.setImageBitmap(ZipUtil.compressBitmapByResid(
				// getResources(), R.drawable.welcome_page3));
				imageView.setImageBitmap(readBitmap(R.drawable.welcome_page3));
				// imageView.setImageBitmap(ZipUtil.compressBitMap(drawable));
				// imageView.setImageResource(R.drawable.welcome_page3);
				break;
			case 3:
				// drawable =
				// getResources().getDrawable(R.drawable.welcome_page4);
				// imageView.setImageBitmap(ZipUtil.compressBitmapByResid(
				// getResources(), R.drawable.welcome_page4));
				// imageView.setImageBitmap(ZipUtil.compressBitMap(drawable));
				imageView.setImageBitmap(readBitmap(R.drawable.welcome_page4));
				// imageView.setImageResource(R.drawable.welcome_page4);
				view.findViewById(R.id.welcomeShowLogin).setOnClickListener(
						WelcomeActivity.this);
				break;
			default:
				break;
			}
			views.add(view);
		}
	}

	private void showLogin() {
		finish();
		Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
		startActivity(intent);
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.llDot);
		dots = new ImageView[PAGER_COUNT];
		// 循环取得小点图片
		for (int i = 0; i < PAGER_COUNT; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// 都设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= PAGER_COUNT) {
			return;
		}
		viewPager.setCurrentItem(position);
	}

	/**
	 * 这只当前引导小点的选中
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > PAGER_COUNT - 1 || currentIndex == positon) {
			return;
		}
		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);
		currentIndex = positon;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.welcomeShowLogin:
			showLogin();
			break;
		default:
			break;
		}
	}
}
