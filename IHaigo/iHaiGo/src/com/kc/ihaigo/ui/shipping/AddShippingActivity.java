package com.kc.ihaigo.ui.shipping;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;

/**
 * 添加运单
 * 
 * @author zouxianbin
 * 
 */
public class AddShippingActivity extends IHaiGoActivity {
	/**
	 * 快递公司
	 */
	private TextView chooseLogis;
	/**
	 * 运单号
	 */
	private EditText courier_number;
	/**
	 * 包裹内容
	 */
	private EditText courier_content;
	/**
	 * 备注
	 */
	private EditText note;
	/**
	 * 快递公司
	 */
	private String choose;

	/**
	 * 运单号
	 */

	private String number;
	/**
	 * 包裹内容
	 */
	private String content;
	/**
	 * 备注
	 */
	private String noteString;
	/**
	 * id
	 */
	private String id;

	private Class<IHaiGoActivity> lparentClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_shipping);
		initTitle();
		initComponets();

	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);

	}

	private void initComponets() {
		findViewById(R.id.companyLayout).setOnClickListener(this);
		chooseLogis = (TextView) findViewById(R.id.choose);
		courier_number = (EditText) findViewById(R.id.courier_number);
		courier_content = (EditText) findViewById(R.id.courier_content);
		note = (EditText) findViewById(R.id.note);

	}

	private void setAdd() {
		String url = Constants.REC_SHIPPING_ADD + Constants.USER_ID + "/add";
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("logistics", id);
		map.put("waybill", number);
		map.put("content", content);
		map.put("remark", noteString);
		Log.e("map", map.toString());
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {

						try {
							Log.e("MaP", map.toString());

							if (!TextUtils.isEmpty(result)) {
								JSONObject jsonObject = new JSONObject(result);
								int code = jsonObject.getInt("code");
								if (code == 1) {
									back();
								} else {
									Toast.makeText(getApplication(), "失败",
											Toast.LENGTH_SHORT).show();
								}
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}, 0, R.string.loading);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_right:// 完成
			number = courier_number.getText().toString().trim();
			content = courier_content.getText().toString().trim();
			noteString = note.getText().toString().trim();
			if (TextUtils.isEmpty(id)) {
				Toast.makeText(getApplication(), "请选择物流公司", Toast.LENGTH_SHORT)
						.show();
			} else if (TextUtils.isEmpty(number)) {
				Toast.makeText(getApplication(), "请输入运单号", Toast.LENGTH_SHORT)
						.show();
			} else if (TextUtils.isEmpty(content)) {
				Toast.makeText(getApplication(), "请输入包裹内容", Toast.LENGTH_SHORT)
						.show();
			} else {

				setAdd();

			}

			break;
		case R.id.title_left:// 返回

			back();
			break;
		case R.id.companyLayout:// 选择快递公司

			Intent inte = new Intent(AddShippingActivity.this,
					WayChooseInfoActivity.class);
			inte.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			inte.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			ShippingGroupActiviy.group.startiHaiGoActivity(inte);

			break;
		default:
			break;
		}

	}

	@Override
	public void refresh() {
		Bundle reBundle;

		if (ShippingActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
		}

		reBundle = getIntent().getExtras();
		chooseLogis.setText(reBundle.getString("name"));
		// chooseLogis.setTextColor(this.getResources().getColor(
		// R.color.add_edt_title));
		id = reBundle.getString("shipid");

	}

	/*
	 * <p>Title: dispatchKeyEvent</p> <p>Description: </p>
	 * 
	 * @param event
	 * 
	 * @return
	 * 
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */

	@Override
	protected void back() {

		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.shipping.ShippingActivity");
			showTabHost = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		super.back();
	}

}
