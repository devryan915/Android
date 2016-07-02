/**
 * @Title: EditDefaultCardInfo.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月19日 下午5:50:04

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ImgUtil;
import com.kc.ihaigo.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: EditDefaultCardInfo
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月19日 下午5:50:04
 * 
 */

public class EditDefaultCardInfo extends IHaiGoActivity {

	private String TAG = "EditDefaultCardInfo";
	private TextView tv_title_right;

	private EditText et_realName;
	private EditText et_realCardNumber;
	private ImageView iv_card_poImage;
	private ImageView iv_card_reImage;
	private LinearLayout setting_defalut_card;
	private TextView tv_delete;
	private TextView tv_flag_default_card;

	private String realName;
	private String realCardNumber;
	private String status;
	private String idcardImage;
	private String idcardImageBack;
	private String title_right;
	private String id;
	private CheckBox settings_card_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_edit_default_identity);
		initTitle();
		initComponents();

		et_realName.setEnabled(false);
		et_realCardNumber.setEnabled(false);
		iv_card_poImage.setEnabled(false);
		iv_card_reImage.setEnabled(false);

	}

	@Override
	public void refresh() {
		super.refresh();
		if (MyMessageActivity.class.equals(parentClass)) {
			id = getIntent().getStringExtra("id");
			realName = getIntent().getStringExtra("name");
			realCardNumber = getIntent().getStringExtra("idNumber");
			idcardImage = getIntent().getStringExtra("idcardImage");
			idcardImageBack = getIntent().getStringExtra("idcardImageBack");
			status = getIntent().getStringExtra("status");
			if ("2".equals(status)) {
				setting_defalut_card.setVisibility(View.GONE);
				settings_card_status.setChecked(true);
				settings_card_status.setClickable(false);
			} else {
				setting_defalut_card.setVisibility(View.VISIBLE);
				tv_flag_default_card.setVisibility(View.GONE);
				settings_card_status.setChecked(false);
				settings_card_status.setClickable(true);
			}
			et_realName.setText(realName);
			et_realCardNumber.setText(realCardNumber);

			ImageLoader.getInstance().loadImage(idcardImage + "",
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {

						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {

						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {

							iv_card_poImage.setImageBitmap(ImgUtil
									.createBitmapBySize(loadedImage, 158, 78));
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {

						}
					});

			ImageLoader.getInstance().loadImage(idcardImageBack + "",
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {

						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {

						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {

							iv_card_reImage.setImageBitmap(ImgUtil
									.createBitmapBySize(loadedImage, 158, 78));
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {

						}
					});
		}

	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		tv_title_right = (TextView) findViewById(R.id.title_right);
		tv_title_right.setOnClickListener(this);
	}

	private void initComponents() {
		et_realName = (EditText) findViewById(R.id.certificateName);
		et_realCardNumber = (EditText) findViewById(R.id.certificateNumber);
		/**
		 * 身份证正面
		 */
		iv_card_poImage = (ImageView) findViewById(R.id.edit_certificate_positive);
		/**
		 * 身份证背面
		 */
		iv_card_reImage = (ImageView) findViewById(R.id.edit_certificate_reverse);

		setting_defalut_card = (LinearLayout) findViewById(R.id.setting_defalut_card);
		tv_delete = (TextView) findViewById(R.id.deleteInfo);
		tv_delete.setOnClickListener(this);
		
		tv_flag_default_card = (TextView) findViewById(R.id.tv_flag_default_card);

		settings_card_status = (CheckBox) findViewById(R.id.settings_card_status);
		settings_card_status.setOnClickListener(this);
	}

	// @Override
	// protected void back() {
	// try {
	// parentClass = (Class<IHaiGoActivity>) Class
	// .forName("com.kc.ihaigo.ui.personal.MyMessageActivity");
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// }
	// // resParams = new Bundle();
	// // resParams.putString("status", status);
	// super.back();
	// }
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			intent.setClass(EditDefaultCardInfo.this, MyMessageActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.title_right:
			title_right = EditDefaultCardInfo.this.getString(R.string.save);
			tv_title_right.setText(title_right);
			updateUserCard();
			break;
		case R.id.deleteInfo:
			DialogUtil.showDelete(EditDefaultCardInfo.this.getParent(),
					new BackCall() {

						@Override
						public void deal(int which, Object... obj) {
							switch (which) {
							case R.id.choose_oks:
								((Dialog) obj[0]).dismiss();
								deleteUserCard();
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
			updateUserCard();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @Title: updateUserCard
	 * @user: helen.yang
	 * @Description: 修改身份信息 void
	 * @throws
	 */
	private void updateUserCard() {
		final String url = Constants.UPDATEUSERCARD_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("userId", Constants.USER_ID);
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

								if ("1".equals(code)) {
									ToastUtil.showShort(
											EditDefaultCardInfo.this, "修改成功");
									intent.setClass(EditDefaultCardInfo.this,
											MyMessageActivity.class);
									intent.putExtra("status", status);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								} else if ("0".equals(code)) {
									ToastUtil.showShort(
											EditDefaultCardInfo.this, "修改失败");
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

	private void deleteUserCard() {
		final String url = Constants.DELETEUSERCARD_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
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
								if ("1".equals(code)) {
									ToastUtil.showShort(
											EditDefaultCardInfo.this, "删除成功");
									intent.setClass(EditDefaultCardInfo.this,
											MyMessageActivity.class);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								} else if ("0".equals(code)) {
									ToastUtil.showShort(
											EditDefaultCardInfo.this, "删除失败");
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

	// 通过网络url获取图片
	public Bitmap getBitmap(String s) {
		Bitmap bitmap = null;
		try {
			URL url = new URL(s);
			bitmap = BitmapFactory.decodeStream(url.openStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}
}
