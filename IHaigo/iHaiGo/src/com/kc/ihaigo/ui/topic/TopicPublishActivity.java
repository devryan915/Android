/**
 * @Title: TopicPublishActivity.java
 * @Package: com.kc.ihaigo.ui.topic
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月25日 上午11:02:18

 * @version V1.0

 */

package com.kc.ihaigo.ui.topic;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalLoginActivity;
import com.kc.ihaigo.ui.topic.adpater.TopicTopicTypeAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;

/**
 * @ClassName: TopicPublishActivity
 * @Description: 话题主页
 * @author: ryan.wang
 * @date: 2014年7月25日 上午11:02:18
 * 
 */

@SuppressLint("HandlerLeak")
public class TopicPublishActivity extends IHaiGoActivity
		implements
			OnClickListener {
	private final String TAG = "TopicPublishActivity";
	private TopicTopicTypeAdapter mAdapter;
	private TextView topictype_sel;
	private TextView topic_title;
	private TextView topic_content;
	private Map<String, String> waitUploadImage;
	private Handler mhandler;
	final String start = Environment.getExternalStorageState();
	private static final String PHOTOPATH = "/photo/";
	private int publishImageLength = 0;// 上传图片数量
	private int curupload = 1;// 当前上传图片
	// private ProgressDialog pDialog;
	private Dialog topicTypeDialog;
	private Bitmap bm = null;
	private String inLocal;
	private LinearLayout selected_images;
	private Dialog uploadDialog;
	private TextView dialogContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_publish);
		initcomponets();
	}

	private void initcomponets() {
		topictype_sel = (TextView) findViewById(R.id.topictype_sel);
		topictype_sel.setOnClickListener(this);
		topic_title = (TextView) findViewById(R.id.topic_title);
		topic_content = (TextView) findViewById(R.id.topic_content);
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		mAdapter = new TopicTopicTypeAdapter(TopicPublishActivity.this);
		JSONArray datas = new JSONArray();
		String[] titles = getResources().getStringArray(R.array.topic_titles);
		try {
			for (int i = 0; i < titles.length; i++) {
				JSONObject data = new JSONObject();
				switch (i + 1) {
					case TopicActivity.GONGLUE :
						data.put("id", TopicActivity.GONGLUE);
						data.put("name", titles[i]);
						break;
					case TopicActivity.JIAOLIU :
						data.put("id", TopicActivity.JIAOLIU);
						data.put("name", titles[i]);
						break;
					case TopicActivity.SHAIDAN :
						data.put("id", TopicActivity.SHAIDAN);
						data.put("name", titles[i]);
						break;
					case TopicActivity.ZHUANRANG :
						data.put("id", TopicActivity.ZHUANRANG);
						data.put("name", titles[i]);
						break;
					case TopicActivity.ZHUANYUN :
						data.put("id", TopicActivity.ZHUANYUN);
						data.put("name", titles[i]);
						break;
					default :
						break;
				}
				datas.put(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mAdapter.setDatas(datas);
		waitUploadImage = new HashMap<String, String>();
		mhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 0 :
						publishTopic();
						break;
					default :
						break;
				}
			}
		};
		findViewById(R.id.formphoto).setOnClickListener(this);
		findViewById(R.id.formcamera).setOnClickListener(this);
		topicTypeDialog = DialogUtil.showTopicType(
				TopicPublishActivity.this.getParent(), new BackCall() {
					@Override
					public void deal(int whichButton, Object... obj) {
						((Dialog) obj[0]).dismiss();
						try {

							switch (whichButton) {
								case R.id.complete :
									JSONObject data = new JSONObject(obj[1]
											.toString());
									topictype_sel.setText(data
											.getString("name"));
									topictype_sel.setTag(data.getString("id"));
									break;
								default :
									break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, null, mAdapter);
		selected_images = (LinearLayout) findViewById(R.id.selected_images);
		uploadDialog = DialogUtil.showLoadingDialog(
				TopicPublishActivity.this.getParent(), "");
		dialogContent = ((TextView) uploadDialog.findViewById(R.id.content));
	}

	/*
	 * <p>Title: refresh</p> <p>Description: </p>
	 * 
	 * @see com.kc.ihaigo.IHaiGoActivity#refresh()
	 */

	@Override
	public void refresh() {
		waitUploadImage = new HashMap<String, String>();
		publishImageLength = 0;
		curupload = 0;
		topictype_sel.setText(TopicPublishActivity.this.getResources()
				.getString(R.string.topic_publish_sel));
		topictype_sel.setTag("");
		topic_title.setText("");
		topic_content.setText("");
		onClick(topictype_sel);
	}

	@Override
	protected void back() {
		try {
			showTabHost = true;
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.topic.TopicActivity");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}

	private void uploadImage() {
		dialogContent.setText(getText(R.string.settings_uploadimage).toString()
				.replaceFirst(",", curupload++ + "")
				.replace("$", publishImageLength + ""));
		uploadDialog.show();
		String url = Constants.AVATAR_URL;
		Map<String, Object> reqParams = new HashMap<String, Object>();
		Set<String> images = waitUploadImage.keySet();
		for (final String key : images) {
			reqParams.put("image", waitUploadImage.get(key).toString());
			waitUploadImage.put(key, "");
			HttpAsyncTask.fetchData(HttpAsyncTask.UPLOAD_IMAGE, url, reqParams,
					new HttpReqCallBack() {
						@Override
						public void deal(String result) {
							if (!TextUtils.isEmpty(result)) {
								try {
									JSONObject resData = new JSONObject(result);
									waitUploadImage.put(key,
											resData.getString("url"));
									dialogContent
											.setText(getText(
													R.string.settings_uploadimage)
													.toString()
													.replaceFirst(",",
															curupload++ + "")
													.replace(
															"$",
															publishImageLength
																	+ ""));
									if (Constants.Debug) {
										Log.d(TAG,
												"图片上传成功"
														+ resData
																.getString("url"));
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							} else {
								waitUploadImage.put(key, "-1");
							}
						}
					});
		}
	}
	private void prePublishTopic() {

		uploadImage();
		if (waitUploadImage.size() > 0) {
			// 轮询图片是否上传完毕，当上传完毕或者超时则提交评论
			new Thread(new Runnable() {

				@Override
				public void run() {
					boolean flag = true;
					long nowTime = System.currentTimeMillis();
					while (flag) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						boolean checked = true;
						Set<String> images = waitUploadImage.keySet();
						for (final String key : images) {
							if ("".equals(waitUploadImage.get(key).toString())) {
								checked = false;
								break;
							}
						}
						if (checked
								|| ((System.currentTimeMillis() - nowTime) / 1000 > Constants.UPLOAD_IMAGE_TIMEOUT)) {
							flag = false;
							Message message = mhandler.obtainMessage();
							message.what = 0;
							message.sendToTarget();
						}
					}
				}
			}).start();
		} else {
			Message message = mhandler.obtainMessage();
			message.what = 0;
			message.sendToTarget();
		}
	}

	/**
	 * 
	 * @Title: replaceImgString
	 * @user: ryan.wang
	 * @Description: 将发表的文字中的图片标签去掉
	 * 
	 * @param oldString
	 * @return String
	 * @throws
	 */
	private String replaceImgString(String oldString) {
		Set<String> images = waitUploadImage.keySet();
		for (String key : images) {
			oldString = oldString.replace(key, "");
		}
		return oldString;
	}

	private String getuploadImageString() {
		Set<String> images = waitUploadImage.keySet();
		StringBuffer imagesString = new StringBuffer();
		for (String key : images) {
			imagesString.append(waitUploadImage.get(key) + ";");
		}
		return imagesString.length() > 0 ? imagesString.substring(0,
				imagesString.length() - 1) : "";
	}

	private void publishTopic() {
		String url = Constants.TOPICS_URL + "add";
		String type = topictype_sel.getTag() == null ? "" : topictype_sel
				.getTag().toString();
		String title = topic_title.getText().toString();
		String content = replaceImgString(topic_content.getText().toString());
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("uid", Constants.USER_ID);
		reqParams.put("type", type);
		reqParams.put("title", title);
		reqParams.put("content", content);
		reqParams.put("image", getuploadImageString());
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						uploadDialog.cancel();
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								if ("1".equals(resData.getString("code"))) {
									// pDialog.dismiss();
									Intent intent = new Intent(
											TopicPublishActivity.this,
											TopicActivity.class);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											true);
									resParams = new Bundle();
									resParams.putString("TOPIC_TYPE",
											topictype_sel.getTag() == null
													? ""
													: topictype_sel.getTag()
															.toString());
									intent.putExtras(resParams);
									TopicGroupActivity.group
											.startiHaiGoActivity(intent);
								} else {
									Toast.makeText(TopicPublishActivity.this,
											"提交失败", Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						// if (Constants.Debug) {
						// Toast.makeText(TopicPublishActivity.this, result,
						// Toast.LENGTH_SHORT).show();
						// }
					}
				}, 0, R.string.settings_committing);
	}
	@Override
	public void onClick(final View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.title_left :
				intent = new Intent(TopicPublishActivity.this,
						TopicActivity.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
				TopicGroupActivity.group.startiHaiGoActivity(intent);
				break;
			case R.id.title_right :
				// pDialog = DialogUtil.showProgressDialog(
				// TopicPublishActivity.this.getParent(), R.string.prompt,
				// "正在提交");
				// pDialog.show();
				if (checkLogin()) {
					prePublishTopic();
				} else {
					intent = new Intent(TopicPublishActivity.this,
							PersonalLoginActivity.class);
					intent.putExtra("code", "");
					intent.putExtra("flag", "TopicPublishActivity");
					intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
							false);
					intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
							true);
					TopicGroupActivity.group.startiHaiGoActivity(intent);
				}

				break;
			case R.id.topictype_sel :
				topicTypeDialog.show();
				break;
			case R.id.formphoto :
				// 从相册选取图片
				if (start.equals(Environment.MEDIA_MOUNTED)) {
					Intent picture = new Intent(Intent.ACTION_GET_CONTENT);
					picture.addCategory(Intent.CATEGORY_OPENABLE);
					picture.setType("image/*");
					TopicGroupActivity.group.startActivityForResult(picture, 1);
				}
				break;
			case R.id.formcamera :
				String state = Environment.getExternalStorageState(); // 判断是否存在sd卡
				if (state.equals(Environment.MEDIA_MOUNTED)) { // 直接调用系统的照相机
					Intent formcamera = new Intent(
							"android.media.action.IMAGE_CAPTURE");
					TopicGroupActivity.group.startActivityForResult(formcamera,
							0);
				} else {
					Toast.makeText(TopicPublishActivity.this, "请检查手机是否有SD卡",
							Toast.LENGTH_LONG).show();
				}
				break;
			default :
				break;
		}
	}

	private String getInsertImageString() {
		return "<图片" + (++publishImageLength) + ">";
	}
	private void addImage(String pathName) {
		Drawable drawable = Drawable.createFromPath(pathName);
		drawable.setBounds(10, 10, 10, 10);
		ImageView selectedImage = new ImageView(TopicPublishActivity.this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				Utils.dip2px(TopicPublishActivity.this, 100), Utils.dip2px(
						TopicPublishActivity.this, 80));
		params.leftMargin = 10;
		params.rightMargin = 10;
		params.topMargin = 10;
		params.bottomMargin = 10;
		selectedImage.setLayoutParams(params);
		selectedImage.setBackgroundDrawable(drawable);
		selected_images.addView(selectedImage);
	}
	public void dealDatas(Object... datas) {
		try {
			int requestCode = (Integer) datas[0];
			int resultCode = (Integer) datas[1];
			Intent data = (Intent) datas[2];
			if (requestCode < 0) {
				return;
			} else if (requestCode == 1) {

				// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
				ContentResolver resolver = getContentResolver();
				// 此处的用于判断接收的Activity是不是你想要的那个

				// try {
				// Uri originalUri = data.getData(); // 获得图片的uri
				// bm = MediaStore.Images.Media.getBitmap(resolver,
				// originalUri); // 显得到bitmap图片这里开始的第二部分，获取图片的路径：
				// String[] proj = { MediaStore.Images.Media.DATA };
				// // 好像是android多媒体数据库的封装接口，具体的看Android文档
				// Cursor cursor = managedQuery(originalUri, proj, null, null,
				// null);
				// // 按我个人理解 这个是获得用户选择的图片的索引值
				// int column_index = cursor
				// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// // 将光标移至开头 ，这个很重要，不小心很容易引起越界
				// cursor.moveToFirst();
				// // 最后根据索引值获取图片路径
				// String path = cursor.getString(column_index);
				// if (!TextUtils.isEmpty(path)) {
				// Toast.makeText(TopicPublishActivity.this, path,
				// Toast.LENGTH_LONG).show();
				// String insertiamge = getInsertImageString();
				// waitUploadImage.put(insertiamge, path);
				// topic_content.append(insertiamge);
				// }
				// } catch (IOException e) {
				// Log.e(TAG, e.toString());
				// }

				Bitmap bm = null;
				// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
				// ContentResolver resolver = getContentResolver();
				Uri originalUri = data.getData(); // 获得图片的uri
				ContentResolver cr = this.getContentResolver();
				try {
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
					bitmapOptions.inSampleSize = 4;
					if (bm != null && !bm.isRecycled())
						bm.recycle();
					bm = BitmapFactory.decodeStream(this.getContentResolver()
							.openInputStream(originalUri), null, bitmapOptions);
					inLocal = SavePicInLocal(bm);
					if (!TextUtils.isEmpty(inLocal)) {
						addImage(inLocal);
						// Toast.makeText(TopicPublishActivity.this, inLocal,
						// Toast.LENGTH_LONG).show();
						String insertiamge = getInsertImageString();
						waitUploadImage.put(insertiamge, inLocal);
						// topic_content.append(insertiamge);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				// 两种方式获得拍摄照片的返回值
				Uri uri = data.getData();
				Bitmap photo = null;
				if (uri != null) {
					photo = BitmapFactory.decodeFile(uri.getPath());
				}
				if (photo == null) {
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						photo = (Bitmap) bundle.get("data");
						// 切圆角
						// Bitmap corner = ImgUtil.toRoundCorner(photo, 100,
						// 160,
						// 160);
						String path = SavePicInLocal(photo);
						if (photo != null && !photo.isRecycled()) {
							photo.recycle();
							photo = null;
						}
						if (!TextUtils.isEmpty(path)) {
							addImage(path);
							String insertiamge = getInsertImageString();
							waitUploadImage.put(insertiamge, path);
							// topic_content.append(insertiamge);
						}

					} else {
						Toast.makeText(TopicPublishActivity.this, "未拍摄照片",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	// 保存拍摄的照片到手机的sd卡
	private String SavePicInLocal(Bitmap bitmap) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null; // 字节数组输出流
		String fileName = null;
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组
			String saveDir = Environment.getExternalStorageDirectory()
					+ "/liangPic";
			File dir = new File(saveDir);
			if (!dir.exists()) {
				dir.mkdirs(); // 创建文件夹
			}
			fileName = saveDir + "/" + System.currentTimeMillis() + ".png";
			File file = new File(fileName);
			file.delete();
			if (!file.exists()) {
				file.createNewFile();// 创建文件
				// Toast.makeText(TopicPublishActivity.this, fileName + "保存成功",
				// 1000).show();
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
			return fileName;
		}

	}

	protected void onDestroy() {

		if (bm != null && !bm.isRecycled())
			bm.recycle();
		super.onDestroy();
	}
}
