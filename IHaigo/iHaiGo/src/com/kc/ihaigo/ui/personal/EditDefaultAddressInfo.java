/**
 * @Title: EditDefaultAddressInfo.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月19日 下午5:47:39

 * @version V1.0

 */

package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.kc.ihaigo.util.ToastUtil;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * @ClassName: EditDefaultAddressInfo
 * @Description: 点击收货地址详情编辑收货地址
 * @author: helen.yang
 * @date: 2014年7月19日 下午5:47:39
 * 
 */

public class EditDefaultAddressInfo extends IHaiGoActivity
		implements
			OnClickListener {

	private String TAG = "EditDefaultAddressInfo";

	private EditText et_contacts;
	private EditText et_contactNumber;
	private EditText et_userArea;
	private EditText et_userAddr;
	private EditText et_postalCode;
	private TextView tv_title_right;
	private LinearLayout setting_defalut_addr;
	private TextView tv_delete;
	private TextView tv_flag_default_addr;

	private String contacts;
	private String contactNumber;
	private String userArea;
	private String userAddr;
	private String postalCode;
	private String status = "1";
	private String mobile;
	private String title_right;
	private String id;

	private boolean isDefault = true;

	private CheckBox settings_address_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_edit_default_address);
		initTitle();
		initComponents();
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		tv_title_right = (TextView) findViewById(R.id.title_right);
		tv_title_right.setOnClickListener(this);
	}

	@Override
	public void refresh() {
		super.refresh();
		if(MyMessageActivity.class.equals(parentClass)){
			id = getIntent().getStringExtra("id");
			status = getIntent().getStringExtra("status");
			contacts = getIntent().getStringExtra("contacts");
			contactNumber = getIntent().getStringExtra("contactNumber");
			userArea = getIntent().getStringExtra("userArea");
			userAddr = getIntent().getStringExtra("userAddr");
			postalCode = getIntent().getStringExtra("postalCode");
			et_contactNumber.setText(contactNumber);
			et_userArea.setText(userArea);
			et_userAddr.setText(userAddr);
			et_contacts.setText(contacts);
			et_postalCode.setText(postalCode);
			setEditStatus();
			if("1".equals(status)){
				tv_flag_default_addr.setVisibility(View.GONE);
				settings_address_status.setChecked(false);
				settings_address_status.setClickable(true);
			}else if("2".equals(status)){
				settings_address_status.setChecked(true);
				settings_address_status.setClickable(false);
			}
			
		}
	}

	private void initComponents() {
		et_contacts = (EditText) findViewById(R.id.contacts);
		et_contactNumber = (EditText) findViewById(R.id.contact_number);
		et_userArea = (EditText) findViewById(R.id.area_selection);
		et_userAddr = (EditText) findViewById(R.id.detailed_address);
		et_postalCode = (EditText) findViewById(R.id.postalCode);
	
		settings_address_status = (CheckBox) findViewById(R.id.settings_address_status);
		settings_address_status.setOnClickListener(this);

		setting_defalut_addr = (LinearLayout) findViewById(R.id.setting_defalut_addr);
		tv_delete = (TextView) findViewById(R.id.tv_delete);
		tv_delete.setOnClickListener(this);
		tv_flag_default_addr = (TextView) findViewById(R.id.tv_flag_default_addr);
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.title_left :
				intent.setClass(EditDefaultAddressInfo.this,
						MyMessageActivity.class);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
				break;
			case R.id.title_right :
				if ("编辑".equals(tv_title_right.getText().toString())) {
					title_right = EditDefaultAddressInfo.this
							.getString(R.string.save);
					tv_title_right.setText(title_right);
					tv_flag_default_addr.setVisibility(View.GONE);
					if("1".equals(status)){
						setting_defalut_addr.setVisibility(View.VISIBLE);
					}else if("2".equals(status)){
						setting_defalut_addr.setVisibility(View.GONE);
					}
					
					tv_delete.setVisibility(View.VISIBLE);
					et_contacts.setEnabled(true);
					et_contactNumber.setEnabled(true);
					et_userArea.setEnabled(true);
					et_userAddr.setEnabled(true);
					et_postalCode.setEnabled(true);
					settings_address_status.setEnabled(true);

				} else if ("保存".equals(tv_title_right.getText().toString())) {

					title_right = EditDefaultAddressInfo.this
							.getString(R.string.editor);
					tv_title_right.setText(title_right);
					tv_flag_default_addr.setVisibility(View.VISIBLE);
					setting_defalut_addr.setVisibility(View.GONE);
					tv_delete.setVisibility(View.GONE);
					contacts = et_contacts.getText().toString().trim();
					contactNumber = et_contactNumber.getText().toString().trim();
					userArea = et_userArea.getText().toString().trim();
					userAddr = et_userAddr.getText().toString().trim();
					postalCode = et_postalCode.getText().toString().trim();
					if(" ".equals(contacts)){
						ToastUtil.showShort(EditDefaultAddressInfo.this, getResources().getString(R.string.personal_linkman));
					}else if(" ".equals(contactNumber)){
						ToastUtil.showShort(EditDefaultAddressInfo.this, getResources().getString(R.string.personal_linkman_tel));
					}else if("".equals(userArea)){
						ToastUtil.showShort(EditDefaultAddressInfo.this, getResources().getString(R.string.personal_enter_address_area));
					}else if("".equals(userAddr)){
						ToastUtil.showShort(EditDefaultAddressInfo.this, getResources().getString(R.string.personal_enter_address_info));
					}else if("".equals(postalCode)){
						ToastUtil.showShort(EditDefaultAddressInfo.this, getResources().getString(R.string.personal_enter_postal));
					}else if(!CheckUtil.checkPhoneNumber(contactNumber)){
						ToastUtil.showShort(EditDefaultAddressInfo.this, getResources().getString(R.string.personal_linkman_tel_check));
					}else if(!CheckUtil.checkPostCode(postalCode)){
						ToastUtil.showShort(EditDefaultAddressInfo.this, getResources().getString(R.string.personal_enter_postal_check));
					}else if((CheckUtil.checkPhoneNumber(contactNumber)) && (CheckUtil.checkPostCode(postalCode))){
						updateUserAddress();
						et_contacts.setEnabled(false);
						et_contactNumber.setEnabled(false);
						et_userArea.setEnabled(false);
						et_userAddr.setEnabled(false);
						et_postalCode.setEnabled(false);
						settings_address_status.setEnabled(false);
					}
					
				}
				break;
			case R.id.tv_delete :
				DialogUtil.showDeleteAddressDialog(EditDefaultAddressInfo.this.getParent(), new BackCall() {
					
					@Override
					public void deal(int which, Object... obj) {
						switch (which) {
						case R.id.exit_oks:
							((Dialog)obj[0]).dismiss();
							deleteUserAddress();
							break;

						default:
							break;
						}
					}
				}, null);
				
				break;
			case R.id.settings_address_status :
				if(settings_address_status.isChecked()){
//					ToastUtil.showShort(EditDefaultAddressInfo.this, "选中默认了.....");
					status = "2";
				}else{
//					ToastUtil.showShort(EditDefaultAddressInfo.this, "取消默认了.....");
					status = "1";
				}
				break;
			default :
				break;
		}
	}
	
	/**
	 * 
	 * @Title: updateUserAddress
	 * @user: helen.yang
	 * @Description: 修改收货地址信息 void
	 * @throws
	 */
	private void updateUserAddress() {
		final String url = Constants.UPDATEUSERADDRESS_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("userId", Constants.USER_ID);
		map.put("contacts", contacts);
		map.put("contactNumber", contactNumber);
		map.put("userArea", userArea);
		map.put("userAddr", userAddr);
		map.put("postalCode", postalCode);
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
								String code = json.getString("status");
								Log.i(TAG, "/-----" + code);

								if ("1".equals(code)) {

									Toast.makeText(EditDefaultAddressInfo.this,
											"修改成功", 1).show();
									intent.setClass(
											EditDefaultAddressInfo.this,
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
									Toast.makeText(EditDefaultAddressInfo.this,
											"修改失败", 1).show();

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
	 * 
	 * @Title: deleteUserAddress
	 * @user: helen.yang
	 * @Description:删除收货地址信息 void
	 * @throws
	 */
	private void deleteUserAddress() {
		final String url = Constants.DELETEUSERADDRESS_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		id = getIntent().getStringExtra("id");
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
								String status = json.getString("status");
								Log.i(TAG, "/-----" + status);
								if ("1".equals(status)) {
//									Toast.makeText(EditDefaultAddressInfo.this,
//											"删除成功", 1).show();
									intent.setClass(
											EditDefaultAddressInfo.this,
											MyMessageActivity.class);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											false);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);

								} else if ("0".equals(status)) {
									Toast.makeText(EditDefaultAddressInfo.this,
											"删除失败", 1).show();
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
	 * 
	 * @Title: setEditStatus
	 * @user: helen.yang
	 * @Description: 当title_right== 编辑的时候让EditText失去焦点，当其等于保存的时候让其获得焦点 void
	 * @throws
	 */
	private void setEditStatus() {
			title_right = EditDefaultAddressInfo.this
					.getString(R.string.editor);
			tv_title_right.setText(title_right);
			tv_flag_default_addr.setVisibility(View.VISIBLE);
			setting_defalut_addr.setVisibility(View.GONE);
			tv_delete.setVisibility(View.GONE);
			et_contacts.setEnabled(false);
			et_contactNumber.setEnabled(false);
			et_userArea.setEnabled(false);
			et_userAddr.setEnabled(false);
			et_postalCode.setEnabled(false);
	}

}
