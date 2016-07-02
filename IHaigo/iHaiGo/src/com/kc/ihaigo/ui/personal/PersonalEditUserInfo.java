/**
 * @Title: PersonalEditUserInfo.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月13日 下午1:14:50

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.HttpUtil;
import com.kc.ihaigo.util.ImgUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName: PersonalEditUserInfo
 * @Description: 进入用户信息编辑操作页面
 * @author: helen.yang
 * @date: 2014年7月13日 下午1:14:50
 * 
 */

public class PersonalEditUserInfo extends IHaiGoActivity {

	private TextView tv_username;
	private TextView tv_userlevel;
	private TextView tv_userintroduce;
	private TextView tv_usersex;
	private ImageView iv_personal_user_header;
	private TextView tv_personal_user_name;

	private String nickName;
	private String introduce;
	private String rank;
	private String sex;
	private int code;
	private String loadavatar_url;

	private Bitmap photo = null;
	private String fileName;

	final String start = Environment.getExternalStorageState();
	private static final String PHOTOPATH = "/photo/";

	private String TAG = "PersonalEditUserInfo";

	private File file;
	private String saveDir;
	private ByteArrayInputStream stream;

	private File inLocal;
	private String head_image_url;

	private Class<IHaiGoActivity> lparentClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actiivity_edit_personal_info);
		initTitle();
		initComponents();

	}

	/***
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initComponents() {
		findViewById(R.id.bingding_tel).setOnClickListener(this);
		tv_username = (TextView) findViewById(R.id.tv_nickName);
		tv_username.setOnClickListener(this);
		tv_userintroduce = (TextView) findViewById(R.id.tv_self_intru);
		tv_userintroduce.setOnClickListener(this);
		tv_usersex = (TextView) findViewById(R.id.tv_sex);
		tv_usersex.setOnClickListener(this);

		tv_personal_user_name = (TextView) findViewById(R.id.tv_personal_user_name);
		tv_userlevel = (TextView) findViewById(R.id.tv_personal_user_rank);

		iv_personal_user_header = (ImageView) findViewById(R.id.iv_edit_personal_user_head);
		iv_personal_user_header.setOnClickListener(this);

		findViewById(R.id.user_header).setOnClickListener(this);
		findViewById(R.id.user_nickName).setOnClickListener(this);
		findViewById(R.id.user_sex).setOnClickListener(this);
		findViewById(R.id.user_introduce).setOnClickListener(this);
		findViewById(R.id.user_bingding).setOnClickListener(this);

	}

	@Override
	public void refresh() {
		super.refresh();
		if (PersonalNickName.class.equals(parentClass)) {
			Bundle resParams = getIntent().getExtras();
			nickName = resParams.getString("nickName");
			introduce = resParams.getString("introduce");
			sex = resParams.getString("sex");
			head_image_url = resParams.getString("headPortrait");
			tv_userintroduce.setText(introduce);
			tv_username.setText(nickName);

			if ("1".equals(sex)) {
				tv_usersex.setText("男");
			} else {
				tv_usersex.setText("女");
			}

			ImageLoader
					.getInstance()
					.displayImage(
							head_image_url,
							((ImageView) findViewById(R.id.iv_edit_personal_user_head)));
			tv_personal_user_name.setText(nickName);
		} else if (PersonalSelfIntroduction.class.equals(parentClass)) {
			Bundle resParams = getIntent().getExtras();
			nickName = resParams.getString("nickName");
			introduce = resParams.getString("introduce");
			sex = resParams.getString("sex");
			head_image_url = resParams.getString("headPortrait");
			tv_userintroduce.setText(introduce);
			tv_username.setText(nickName);
			if ("1".equals(sex)) {
				tv_usersex.setText("男");
			} else {
				tv_usersex.setText("女");
			}

			ImageLoader
					.getInstance()
					.displayImage(
							head_image_url,
							((ImageView) findViewById(R.id.iv_edit_personal_user_head)));
			tv_personal_user_name.setText(nickName);
		} else if (PersonalActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			nickName = getIntent().getStringExtra("nickName");
			tv_username.setText(nickName);
			introduce = getIntent().getStringExtra("introduce");
			tv_userintroduce.setText(introduce);
			sex = getIntent().getStringExtra("sex");
			if ("1".equals(sex)) {
				tv_usersex.setText("男");
			} else {
				tv_usersex.setText("女");
			}
			head_image_url = getIntent().getStringExtra("headPortnextrait");
			ImageLoader
					.getInstance()
					.displayImage(
							head_image_url,
							((ImageView) findViewById(R.id.iv_edit_personal_user_head)));
			tv_personal_user_name.setText(nickName);
		}

	}

	/**
	 * 
	 * @Title: initTitle
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */
	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);

	}

	@Override
	protected void back() {
		// parentClass = lparentClass;
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalActivity");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		showTabHost = true;
		resParams = new Bundle();
		resParams.putString("nickName", nickName);
		resParams.putString("introduce", introduce);
		resParams.putString("headPortrait", head_image_url);
		super.back();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			intent.setClass(PersonalEditUserInfo.this, PersonalActivity.class);
			intent.putExtra("nickName", nickName);
			intent.putExtra("introduce", introduce);
			intent.putExtra("headPortrait", head_image_url);
			intent.putExtra("which", "PersonalEditUserInfo");
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.user_bingding:
			intent.setClass(PersonalEditUserInfo.this,
					PersonalBingdingMobile.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.user_nickName:
			intent.setClass(PersonalEditUserInfo.this, PersonalNickName.class);
			intent.putExtra("nickName", nickName);
			intent.putExtra("introduce", introduce);
			intent.putExtra("sex", sex);
			intent.putExtra("headPortnextrait", head_image_url);
			intent.putExtra("which", "PersonalEditUserInfo");
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.user_introduce:
			intent.setClass(PersonalEditUserInfo.this,
					PersonalSelfIntroduction.class);
			intent.putExtra("nickName", nickName);
			intent.putExtra("introduce", introduce);
			intent.putExtra("sex", sex);
			intent.putExtra("headPortnextrait", head_image_url);
			intent.putExtra("which", "PersonalEditUserInfo");
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.user_sex:
			DialogUtil.showUserSexDialog(PersonalEditUserInfo.this.getParent(),
					new BackCall() {

						@Override
						public void deal(int whichButton, Object... obj) {
							switch (whichButton) {
							case R.id.dialog_user_man:
								((Dialog) obj[0]).dismiss();
								Toast.makeText(PersonalEditUserInfo.this, "男",
										1000).show();
								sex = "1";
								updateUser();
								break;
							case R.id.dialog_user_woman:
								((Dialog) obj[0]).dismiss();
								Toast.makeText(PersonalEditUserInfo.this, "女",
										1000).show();
								sex = "2";
								updateUser();
								break;
							default:
								break;
							}
						}
					}, null);

			break;
		case R.id.user_header:
			DialogUtil.showUserPhotoDialog(
					PersonalEditUserInfo.this.getParent(), new BackCall() {

						@Override
						public void deal(int whichButton, Object... obj) {
							switch (whichButton) {
							case R.id.dialog_user_photo:
								((Dialog) obj[0]).dismiss();
								Toast.makeText(PersonalEditUserInfo.this,
										"相机拍照", 1000).show();

								String state = Environment
										.getExternalStorageState(); // 判断是否存在sd卡
								if (state.equals(Environment.MEDIA_MOUNTED)) { // 直接调用系统的照相机
									Intent intent = new Intent(
											"android.media.action.IMAGE_CAPTURE");
									PersonalGroupActivity.group
											.startActivityForResult(intent, 0);

								} else {
									Toast.makeText(PersonalEditUserInfo.this,
											"请检查手机是否有SD卡", Toast.LENGTH_LONG)
											.show();
								}

								break;
							case R.id.dialog_user_Album:
								((Dialog) obj[0]).dismiss();
								Toast.makeText(PersonalEditUserInfo.this,
										"本地相册", 1000).show();
								if (start.equals(Environment.MEDIA_MOUNTED)) {
									// Intent picture = new Intent(
									// Intent.ACTION_PICK,
									// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									Intent picture = new Intent(
											Intent.ACTION_GET_CONTENT);
									picture.addCategory(Intent.CATEGORY_OPENABLE);
									picture.setType("image/*");
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
		default:
			break;
		}
	}

	/**
	 * 
	 * @Title: updateUser
	 * @user: helen.yang
	 * @Description: 修改用户信息——性别的修改 void
	 * @throws
	 */
	private void updateUser() {
		final String url = Constants.UPDATEUSER_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		nickName = getIntent().getStringExtra("nickName");
		Log.i("geek", " +++++++++++++++++????????????" + Constants.USER_ID);
		map.put("userId", Constants.USER_ID);
		map.put("nickName", nickName);
		map.put("sex", sex);
		map.put("introduce", introduce);
		if (loadavatar_url != null) {
			map.put("headPortrait", loadavatar_url);
		} else {
			map.put("headPortrait", head_image_url);
		}
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject obj = new JSONObject(result);
								code = obj.getInt("code");
								if (code == 1) {
									Toast.makeText(PersonalEditUserInfo.this,
											"修改信息成功", 0).show();
									if (loadavatar_url != null) {
										String url = Constants.REC_IMAGE_URL
												+ loadavatar_url;
										ImageLoader
												.getInstance()
												.displayImage(
														url,
														((ImageView) findViewById(R.id.iv_edit_personal_user_head)));
										head_image_url = loadavatar_url;
									}
									if (sex != null) {
										if ("1".equals(sex)) {
											tv_usersex.setText("男");
										} else if ("2".equals(sex)) {
											tv_usersex.setText("女");
										}
									}
								} else if (code == 0) {
									Toast.makeText(PersonalEditUserInfo.this,
											"修改信息失败", 0).show();
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
		final String url = Constants.AVATAR_URL;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("image", file.getAbsolutePath());
		String result = HttpUtil.uploadImage(url, map);
		if (!TextUtils.isEmpty(result)) {
			Log.i("geek", "*********************嘻嘻" + result);
			JSONObject json = new JSONObject(result);
			String imageurl = json.getString("url");
			Log.i("image", imageurl);
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
				Bitmap bm = null;
				// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
				// ContentResolver resolver = getContentResolver();
				Uri originalUri = data.getData(); // 获得图片的uri
				ContentResolver cr = this.getContentResolver();
				try {
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
					bitmapOptions.inSampleSize = 4;
					bm = BitmapFactory.decodeStream(this.getContentResolver()
							.openInputStream(originalUri), null, bitmapOptions);
					inLocal = SavePicInLocal(bm);
					iv_personal_user_header.setImageBitmap(bm);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				// 两种方式获得拍摄照片的返回值
				Uri uri = data.getData();
				if (uri != null) {
					photo = BitmapFactory.decodeFile(uri.getPath());
				}
				if (photo == null) {
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						photo = (Bitmap) bundle.get("data");
						// Bitmap corner = ImgUtil.toRoundCorner(photo, 100,
						// 160,
						// 160);

						inLocal = SavePicInLocal(photo);
						if (photo != null && !photo.isRecycled()) {
							photo.recycle();
							photo = null;
						}
						// if(corner != null && !corner.isRecycled()){
						// corner.recycle();
						// corner = null;
						// }
					} else {
						Toast.makeText(PersonalEditUserInfo.this, "未拍摄照片",
								Toast.LENGTH_LONG).show();
					}
				}
			}

			new Thread(new Runnable() {

				public void run() {
					try {
						loadavatar_url = loadavatar(inLocal);
						updateUser();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}// 保存到本地
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
			saveDir = Environment.getExternalStorageDirectory() + "/liangPic";
			File dir = new File(saveDir);
			if (!dir.exists()) {
				dir.mkdirs(); // 创建文件夹
			}
			fileName = saveDir + "/" + System.currentTimeMillis() + ".png";
			file = new File(fileName);
			file.delete();
			if (!file.exists()) {
				file.createNewFile();// 创建文件
				Log.i("PicDir", file.getPath());
//				Toast.makeText(PersonalEditUserInfo.this, fileName + "保存成功",
//						1000).show();
			}
			Log.i("PicDir", file.getPath());
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
