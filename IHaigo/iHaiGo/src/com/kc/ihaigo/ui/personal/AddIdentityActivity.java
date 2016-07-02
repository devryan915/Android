package com.kc.ihaigo.ui.personal;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.CheckUtil;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.HttpUtil;
import com.kc.ihaigo.util.ImgUtil;
import com.kc.ihaigo.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 添加身份证信息
 * 
 * @author Thinkpad
 * 
 */
public class AddIdentityActivity extends IHaiGoActivity {

	private String TAG = "AddIdentityActivity";

	private EditText et_realName;
	private EditText et_realCardNumber;
	private ImageView positive;
	private ImageView reverse;

	private String realName;
	private String realCardNumber;

	private String loadavatar_url;
	private String idImageUrl;
	private String backImageUrl;

	private Bitmap photo = null;
	private String fileName;

	private File file;
	private String saveDir;
	private ByteArrayInputStream stream;

	private File inLocal;
	private File backLocal;
	private String status = "1";
	public String FLAG = "0";
	public String FLAG_ID = "1";
	public String FLAG_BACK = "2";

	private CheckBox settings_card_status;

	private int resqutFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_identity);
		initTitle();
		initComponents();

	}

	private void initComponents() {

		et_realName = (EditText) findViewById(R.id.certificateName);
		et_realCardNumber = (EditText) findViewById(R.id.certificateNamber);
		findViewById(R.id.title_right).setOnClickListener(this);
		positive = (ImageView) findViewById(R.id.certificate_positive);
		positive.setOnClickListener(this);
		reverse = (ImageView) findViewById(R.id.certificate_reverse);
		reverse.setOnClickListener(this);
		settings_card_status = (CheckBox) findViewById(R.id.settings_card_status);
		settings_card_status.setOnClickListener(this);
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);

	}

	
	@Override
	public void refresh() {
		super.refresh();
		if(MyMessageActivity.class.equals(parentClass)){
			et_realName.setText("");
			et_realCardNumber.setText("");
			positive.setImageResource(R.drawable.certificate_bg);
			reverse.setImageResource(R.drawable.certificate_bg);
			settings_card_status.setChecked(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			Intent intent = new Intent(AddIdentityActivity.this,
					MyMessageActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);

			break;
		case R.id.title_right:
			realName = et_realName.getText().toString().trim();
			realCardNumber = et_realCardNumber.getText().toString().trim();
			Boolean checkNID = CheckUtil.checkNID(realCardNumber);
			if ("".equals(realName)) {
				ToastUtil.showShort(AddIdentityActivity.this, getResources()
						.getString(R.string.personal_enter_realnname));
			} else if ("".equals(realCardNumber)) {
				ToastUtil.showShort(AddIdentityActivity.this, getResources()
						.getString(R.string.personal_enter_cardNumber));
			} else if (!checkNID) {
				ToastUtil.showShort(AddIdentityActivity.this, getResources()
						.getString(R.string.personal_enter_cardNumber_check));
			} else if ("".equals(idImageUrl)) {
				ToastUtil.showShort(AddIdentityActivity.this, getResources()
						.getString(R.string.personal_enter_cardImage));
			} else if ("".equals(backImageUrl)) {
				ToastUtil.showShort(AddIdentityActivity.this, getResources()
						.getString(R.string.personal_enter_cardImage));
			} else {
				insertUserCard();
			}

			break;
		case R.id.certificate_positive:
			FLAG = FLAG_ID;
			DialogUtil.showUserPhotoDialog(
					AddIdentityActivity.this.getParent(), new BackCall() {

						@Override
						public void deal(int whichButton, Object... obj) {
							switch (whichButton) {
							case R.id.dialog_user_photo:
								((Dialog) obj[0]).dismiss();
								Toast.makeText(AddIdentityActivity.this,
										"相机拍照", 1000).show();

								String state = Environment
										.getExternalStorageState(); // 判断是否存在sd卡
								if (state.equals(Environment.MEDIA_MOUNTED)) { // 直接调用系统的照相机
									Intent intent = new Intent(
											"android.media.action.IMAGE_CAPTURE");
									PersonalGroupActivity.group
											.startActivityForResult(intent, 0);

								} else {
									Toast.makeText(AddIdentityActivity.this,
											"请检查手机是否有SD卡", Toast.LENGTH_LONG)
											.show();
								}

								break;
							case R.id.dialog_user_Album:
								((Dialog) obj[0]).dismiss();
								Toast.makeText(AddIdentityActivity.this,
										"本地相册", 1000).show();
								String stater = Environment
										.getExternalStorageState(); // 判断是否存在sd卡
								if (stater.equals(Environment.MEDIA_MOUNTED)) {
									Intent picture = new Intent(
											Intent.ACTION_PICK,
											android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									PersonalGroupActivity.group
											.startActivityForResult(picture, 1);
								}
								break;
							default:
								break;
							}
						}
					}, null);

			break;
		case R.id.certificate_reverse:
			FLAG = FLAG_BACK;
			DialogUtil.showUserPhotoDialog(
					AddIdentityActivity.this.getParent(), new BackCall() {

						@Override
						public void deal(int whichButton, Object... obj) {
							switch (whichButton) {
							case R.id.dialog_user_photo:
								((Dialog) obj[0]).dismiss();
								Toast.makeText(AddIdentityActivity.this,
										"相机拍照", 1000).show();

								String state = Environment
										.getExternalStorageState(); // 判断是否存在sd卡
								if (state.equals(Environment.MEDIA_MOUNTED)) { // 直接调用系统的照相机
									Intent intent = new Intent(
											"android.media.action.IMAGE_CAPTURE");
									PersonalGroupActivity.group
											.startActivityForResult(intent, 0);

								} else {
									Toast.makeText(AddIdentityActivity.this,
											"请检查手机是否有SD卡", Toast.LENGTH_LONG)
											.show();
								}

								break;
							case R.id.dialog_user_Album:
								((Dialog) obj[0]).dismiss();
								Toast.makeText(AddIdentityActivity.this,
										"本地相册", 1000).show();
								String stater = Environment
										.getExternalStorageState(); // 判断是否存在sd卡
								if (stater.equals(Environment.MEDIA_MOUNTED)) {
									Intent picture = new Intent(
											Intent.ACTION_PICK,
											android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									PersonalGroupActivity.group
											.startActivityForResult(picture, 1);
								}
								break;
							default:
								break;
							}
						}
					}, null);
			break;
		case R.id.settings_card_status:
			if (settings_card_status.isChecked()) {
				status = "2";
			} else {
				status = "1";
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * @Title: insertUserCard
	 * @user: helen.yang
	 * @Description: 添加身份信息 void
	 * @throws
	 */
	private void insertUserCard() {
		final String url = Constants.INSERTUSERCARD_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", Constants.USER_ID);
		map.put("name", realName);
		map.put("idNumber", realCardNumber);
		map.put("idcardImage", idImageUrl);
		map.put("idcardImageBack", backImageUrl);
		map.put("status", status);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject json = new JSONObject(result);
								String code = json.getString("code");
								Log.i(TAG, "/-----" + code);
								if ("0".equals(code)) {
									Toast.makeText(AddIdentityActivity.this,
											"失败", 1).show();
								} else if ("1".equals(code)) {
									Toast.makeText(AddIdentityActivity.this,
											"才成功ing", 1).show();
									intent.setClass(AddIdentityActivity.this,
											MyMessageActivity.class);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
							Log.i(TAG, "*****************收到信息" + result);
						}
					}
				}, 1);
	}

	/**
	 * 用户上传头像
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private String loadavatar(File file) throws ClientProtocolException,
			IOException, JSONException {
		final String url = Constants.ID_CARD_URL;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("image", file.getAbsolutePath());
		String result = HttpUtil.uploadImage(url, map);
		if (!TextUtils.isEmpty(result)) {
			Log.i("geek", "*********************嘻嘻" + result);
			JSONObject json = new JSONObject(result);
			String imageurl = json.getString("url");
			return imageurl;
		} else {
			Log.i("geek", "+++++++++++++++++++++哈哈" + result);
		}
		return null;
	}

	public void dealDatas(Object... datas) {
		try {
			int requestCode = (Integer) datas[0];
			int resultCode = (Integer) datas[1];
			Intent data = (Intent) datas[2];
			if (requestCode < 0) {
				return;
			} else if (requestCode == 1) {
				resqutFlag =  1;
				Bitmap bm = null;
				// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
				ContentResolver resolver = getContentResolver();
				// 此处的用于判断接收的Activity是不是你想要的那个

				try {
					Uri originalUri = data.getData(); // 获得图片的uri
					bm = MediaStore.Images.Media.getBitmap(resolver,
							originalUri); // 显得到bitmap图片这里开始的第二部分，获取图片的路径：
					String[] proj = { MediaStore.Images.Media.DATA };
					// 好像是android多媒体数据库的封装接口，具体的看Android文档
					Cursor cursor = managedQuery(originalUri, proj, null, null,
							null);
					// 按我个人理解 这个是获得用户选择的图片的索引值
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					// 将光标移至开头 ，这个很重要，不小心很容易引起越界
					cursor.moveToFirst();
					// 最后根据索引值获取图片路径
					String path = cursor.getString(column_index);
					// cursor.close();
					Log.i("geek", "path:::::::::::::::::::::::" + path);
					
					Bitmap imageCard = ImgUtil.getImageCard(path);
					Bitmap createBitmapBySize = ImgUtil.createBitmapBySize(imageCard, 158, 98);
					if(FLAG_ID.equals(FLAG)){
						positive.setImageBitmap(createBitmapBySize);
					}else{
						reverse.setImageBitmap(createBitmapBySize);
					}
//					if(imageCard != null && !imageCard.isRecycled()){   
//						imageCard.recycle();         
//						imageCard = null;  
//				    } 
//					if(createBitmapBySize != null && !createBitmapBySize.isRecycled()){   
//						createBitmapBySize.recycle();         
//						createBitmapBySize = null;  
//					} 
//					File file = new File(path);
					if(FLAG_ID.equals(FLAG)){
						inLocal = SavePicInLocal(createBitmapBySize);
					} else {
						backLocal = SavePicInLocal(createBitmapBySize);
					}
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}

			} else {
				resqutFlag = 0;
				// 两种方式获得拍摄照片的返回值
				Uri uri = data.getData();
				if (uri != null) {
					photo = BitmapFactory.decodeFile(uri.getPath());
				}

				if (photo == null) {
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						photo = (Bitmap) bundle.get("data");
						Bitmap createBitmapBySize = ImgUtil.createBitmapBySize(
								photo, 250, 152);
						if (FLAG_ID.equals(FLAG)) {
							positive.setImageBitmap(createBitmapBySize);
						} else if(FLAG_BACK.equals(FLAG)){
							reverse.setImageBitmap(createBitmapBySize);
						}
						inLocal = SavePicInLocal(photo);
						if (photo != null && !photo.isRecycled()) {
							photo.recycle();
							photo = null;
						}
//						if(createBitmapBySize != null && !createBitmapBySize.isRecycled()){   
//							createBitmapBySize.recycle();         
//							createBitmapBySize = null;  
//						} 
					} else {
						Toast.makeText(AddIdentityActivity.this, "未拍摄照片",
								Toast.LENGTH_LONG).show();
					}
				}
			}

			new Thread(new Runnable() {

				public void run() {
					try {
						if(resqutFlag == 1){
							if (FLAG_ID.equals(FLAG)) {
								loadavatar_url = loadavatar(inLocal);
								idImageUrl = loadavatar_url;
								Log.i("geek", "打印的图片路径idImageUrl" + idImageUrl);
							} else if(FLAG_BACK.equals(FLAG)){
								loadavatar_url = loadavatar(backLocal);
								backImageUrl = loadavatar_url;
								Log.i("geek", "打印的图片路径backImageUrl" + backImageUrl);
							}
						}else if(resqutFlag == 0){
							loadavatar_url = loadavatar(inLocal);
							if(FLAG_ID.equals(FLAG)){
								idImageUrl = loadavatar_url;
								Log.i("geek", "打印的图片路径idImageUrl"+idImageUrl);
							}else{
								backImageUrl = loadavatar_url;
								Log.i("geek", "打印的图片路径backImageUrl"+backImageUrl);
							}
						}
						

//						if (photo != null && !photo.isRecycled()) {
//							photo.recycle();
//							photo = null;
//						}
//						if(createBitmapBySize != null && !createBitmapBySize.isRecycled()){   
//							createBitmapBySize.recycle();         
//							createBitmapBySize = null;  
//						} 
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	// 保存拍摄的照片到手机的sd卡
	private File SavePicInLocal(Bitmap bitmap) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null; // 字节数组输出流
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组
			saveDir = Environment.getExternalStorageDirectory() + "/idCard";
			File dir = new File(saveDir);
			if (!dir.exists()) {
				dir.mkdirs(); // 创建文件夹
			}
			fileName = saveDir + "/" + System.currentTimeMillis() + ".png";
			file = new File(fileName);
			file.delete();
			if (!file.exists()) {
				file.createNewFile();// 创建文件
			}
			Log.i("PicDir", file.getPath());
			Toast.makeText(AddIdentityActivity.this, fileName + "保存成功", 1000)
					.show();
			// 将字节数组写入到刚创建的图片文件中
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return file;
		}

	}

}
