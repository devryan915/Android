package com.kc.ihaigo.ui.myorder;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.kc.ihaigo.R;
import com.kc.ihaigo.util.CheckUtil;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/**
 * 添加收货地址信息
 * 
 * @author Lijie
 * 
 */
public class AddAdressActivity extends Activity implements OnClickListener {

	private String TAG = "AddressActivity";
	private EditText et_contacts;
	private EditText et_contactNumber;
	private EditText et_userArea;
	private EditText et_userAddr;
	private EditText et_postalCode;

	private String contacts;
	private String contactNumber;
	private String userArea;
	private String userAddr;
	private String postalCode;
	private String status = "1";
	private String mobile;

	private boolean isDefault = true;
	private CheckBox settings_address_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_adress);
		initTitle();
		initComponents();
	}

	private void initComponents() {
		et_contacts = (EditText) findViewById(R.id.contacts);
		et_contactNumber = (EditText) findViewById(R.id.contact_number);
		et_userArea = (EditText) findViewById(R.id.area_selection);
		et_userAddr = (EditText) findViewById(R.id.detailed_address);
		et_postalCode = (EditText) findViewById(R.id.postalCode);

		settings_address_status = (CheckBox) findViewById(R.id.settings_address_status);
		settings_address_status.setOnClickListener(this);
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();
			break;
		case R.id.title_right:
			contacts = et_contacts.getText().toString().trim();
			contactNumber = et_contactNumber.getText().toString().trim();
			userArea = et_userArea.getText().toString().trim();
			userAddr = et_userAddr.getText().toString().trim();
			postalCode = et_postalCode.getText().toString().trim();
			boolean checkPhoneNumber = CheckUtil
					.checkPhoneNumber(contactNumber);
			if (" ".equals(contacts)) {
				ToastUtil.showShort(AddAdressActivity.this, getResources()
						.getString(R.string.personal_linkman));
			} else if (" ".equals(contactNumber)) {
				ToastUtil.showShort(AddAdressActivity.this, getResources()
						.getString(R.string.personal_linkman_tel));
			} else if ("".equals(userArea)) {
				ToastUtil.showShort(AddAdressActivity.this, getResources()
						.getString(R.string.personal_enter_address_area));
			} else if ("".equals(userAddr)) {
				ToastUtil.showShort(AddAdressActivity.this, getResources()
						.getString(R.string.personal_enter_address_info));
			} else if ("".equals(postalCode)) {
				ToastUtil.showShort(AddAdressActivity.this, getResources()
						.getString(R.string.personal_enter_postal));
			} else if (!CheckUtil.checkPhoneNumber(contactNumber)) {
				ToastUtil.showShort(AddAdressActivity.this, getResources()
						.getString(R.string.personal_linkman_tel_check));
			} else if (!CheckUtil.checkPostCode(postalCode)) {
				ToastUtil.showShort(AddAdressActivity.this, getResources()
						.getString(R.string.personal_enter_postal_check));
			} else if ((CheckUtil.checkPhoneNumber(contactNumber))
					&& (CheckUtil.checkPostCode(postalCode))) {
				insertUserAddress();
			}
		case R.id.settings_address_status:
			if (settings_address_status.isChecked()) {
				ToastUtil.showShort(AddAdressActivity.this, "选中默认了.....");
				status = "2";
			} else {
				ToastUtil.showShort(AddAdressActivity.this, "取消默认了.....");
				status = "1";
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 
	 * @Title: insertUserAddress
	 * @user: helen.yang
	 * @Description: 添加收货地址 void
	 * @throws
	 */
	private void insertUserAddress() {
		final String url = Constants.INSERTUSERADDRESS_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
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
								String status = json.getString("status");
								Log.i(TAG, "/-----" + status);
								if ("0".equals(status)) {
									Toast.makeText(AddAdressActivity.this,
											"保存失败", 1).show();
								} else {
									Toast.makeText(AddAdressActivity.this,
											"保存成功", 1).show();
									// if (which != null) {
									// intent.setClass(
									// AddressActivity.this,
									// ShopcarConfirmBillActivity.class);
									// intent.putExtra("TAG", "Personal");
									// intent.putExtra(
									// IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
									// true);
									// intent.putExtra(
									// IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
									// false);
									// PersonalGroupActivity.group
									// .startiHaiGoActivity(intent);
									// } else {
									// intent.setClass(AddressActivity.this,
									// MyMessageActivity.class);
									// intent.putExtra(
									// IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
									// true);
									// intent.putExtra(
									// IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
									// false);
									// PersonalGroupActivity.group
									// .startiHaiGoActivity(intent);
									// }

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

}
