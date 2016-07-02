package com.kc.ihaigo.ui.myorder;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.shipping.ChooseLogisticsActivity;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.ToastUtil;

/***
 * 
 * @Description:添加预报
 * @author: Lijie
 * @date: 2014年7月23日 下午4:09:10
 * **/
public class ForecastAddActivity extends Activity implements OnClickListener {
	private LinearLayout transport_company, goods_receiving, logistics_company;
	private TextView logistic_company, transport_company_name, warehouse_name;
	private EditText express, package_content;
	JSONArray tcompanyInfo;
	private PopupWindow myChoicePopupWindow;
	boolean flag = true;
	private String storageAddress;
	private ListView lv_popwin_storage;
	private JSONArray jsobj;
	private String logisticsId; // 物流公司Id
	private String TransportId; // 转运公司Id
	private String addressId; //

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_forecast_add);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		warehouse_name = (TextView) findViewById(R.id.warehouse_name);
		express = (EditText) findViewById(R.id.express_no);
		package_content = (EditText) findViewById(R.id.package_content);
		transport_company = (LinearLayout) findViewById(R.id.transport_company);
		logistics_company = (LinearLayout) findViewById(R.id.logistics_company);
		logistic_company = (TextView) findViewById(R.id.logistic_company);
		transport_company_name = (TextView) findViewById(R.id.transport_company_name);
		goods_receiving = (LinearLayout) findViewById(R.id.goods_receiving);

		transport_company.setOnClickListener(this);
		logistics_company.setOnClickListener(this);
		goods_receiving.setOnClickListener(this);
		DataConfig dConfig = new DataConfig(this);
		storageAddress = dConfig.getTcompanyStorageInfoById("1"); // 根据转运公司id获取仓库信息
		try {
			jsobj = new JSONArray(storageAddress);
			for (int i = 0; i < jsobj.length(); i++) {
				jsobj.getJSONObject(i).getString("name");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("", storageAddress);

	}

	private void initData() {
		// TODO Auto-generated method stub
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", "1");
		map.put("order", "");//T0000000000012
//		map.put("transport", TransportId);
		map.put("transport", "1");
		map.put("address", "3");
//		map.put("logistics", logisticsId);// 5
		map.put("logistics", "5");// 5
		map.put("waybillNO", "000900044");// 000900044
		map.put("content", "先锋快递");
		map.put("remark", "先锋");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST,
				"http://192.168.1.4:8080/transports/forecast", map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						// TODO Auto-generated method stub
						Log.e("---------------", result + "");

					}
				}, 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();
			break;
		case R.id.title_right:

			if (TextUtils.isEmpty(transport_company_name.getText().toString())) {
				ToastUtil.showShort(this, "请选择 转运公司");
			} else if (TextUtils.isEmpty(logistic_company.getText().toString())) {
				ToastUtil.showShort(this, "请选择物流公司");
			} else if (TextUtils.isEmpty(warehouse_name.getText().toString())) {
				ToastUtil.showShort(this, "请选择收货仓库");
			} else if (TextUtils.isEmpty(express.getText().toString())) {
				ToastUtil.showShort(this, "请填写快递单号");
			} else if (TextUtils.isEmpty(package_content.getText().toString())) {
				ToastUtil.showShort(this, "请填写包裹内容");
			} else {
				ToastUtil.showShort(ForecastAddActivity.this, "请输入正确的订单号");
			}

			break;
		case R.id.transport_company:
			intent.setClass(this, TransportCompanyActivity.class);
			intent.putExtra("flag", "1");
			startActivityForResult(intent, 1);
			break;
		case R.id.goods_receiving:
			showPopupWindows(v);

			break;
		case R.id.logistics_company: // 选择物流公司
			intent.setClass(this, ChooseLogisticsActivity.class);
			intent.putExtra("flag", "2");
			startActivityForResult(intent, 2);
			break;

		default:
			break;
		}

	}

	/***
	 * 添加收货仓库-----Dialog
	 * 
	 * */
	public void showPopupWindows(View parentView) {
		if (myChoicePopupWindow == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(this);

			View view = layoutInflater.inflate(
					R.layout.add_receive_storage_popwin, null);
			lv_popwin_storage = (ListView) view
					.findViewById(R.id.lv_popwin_storage);

			lv_popwin_storage.setAdapter(new StorageAdapter());
			// 创建一个PopuWidow对象
			myChoicePopupWindow = new PopupWindow(view,
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
		}
		myChoicePopupWindow.setFocusable(true);
		myChoicePopupWindow.setTouchable(true);
		myChoicePopupWindow.setOutsideTouchable(true);
		myChoicePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		WindowManager wm = getWindowManager();
		int screenWidth = wm.getDefaultDisplay().getWidth();
		myChoicePopupWindow.showAtLocation(parentView, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
		lv_popwin_storage
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		lv_popwin_storage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				try {
					warehouse_name.setText(jsobj.getJSONObject(position)
							.getString("name"));
					logisticsId = jsobj.getJSONObject(position).getString("id");
					myChoicePopupWindow.dismiss();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		myChoicePopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				myChoicePopupWindow.dismiss();

			}
		});
	}

	class StorageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return jsobj.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return jsobj.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parents) {
			// TODO Auto-generated method stub

			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(ForecastAddActivity.this)
						.inflate(R.layout.listview_storage_item, null);
				holder = new ViewHolder();
				holder.storagename = (TextView) convertView
						.findViewById(R.id.storage_name);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				holder.storagename.setText(jsobj.getJSONObject(position)
						.getString("name"));
				addressId = jsobj.getJSONObject(position).getString("id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}

	}

	class ViewHolder {
		TextView storagename;
	}

	/***
	 * 获取转运公司返回值&获取物流公司信息
	 * **/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (1 == resultCode) {

			transport_company_name.setText(data.getStringExtra("name"));
			TransportId = data.getStringExtra("TcompanyId");
			Log.e("-------------", data.getStringExtra("TcompanyId"));
		}
		if (2 == resultCode) {

			logistic_company.setText(data.getStringExtra("name"));
			logisticsId = data.getStringExtra("shipid");
			Log.e("222222222222222", data.getStringExtra("shipid"));
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}
