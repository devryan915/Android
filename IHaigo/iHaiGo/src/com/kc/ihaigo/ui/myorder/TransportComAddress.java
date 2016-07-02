/**
 * @Title: TransUncompleteComDetail.java
 * @Package: com.kc.ihaigo.ui.myorder
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年8月6日 上午10:46:11

 * @version V1.0

 */

package com.kc.ihaigo.ui.myorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.R.id;
import com.kc.ihaigo.util.DataConfig;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName: TransUncompleteComDetail
 * @Description: 我的订单-未完成订单-转运公司详情
 * @author: ryan.wang
 * @date: 2014年8月6日 上午10:46:11
 * 
 */

public class TransportComAddress extends Activity implements OnClickListener {
	private JSONArray addressArray;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_tcomdetail_showaddress);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.title_left).setOnClickListener(this);
		
		
	}

	/**
	 * @Title: initData
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */
	private void initData() {
		Bundle reqParams = getIntent().getExtras();
		try {
			if (reqParams != null && reqParams.getString("address") != null) {
				int transcompanyId = reqParams.getInt("transcompanyId");
				int used = reqParams.getInt("used");
				DataConfig config = new DataConfig(TransportComAddress.this);
				String transCom = config.getTcompanySty(transcompanyId + "");
				JSONObject com;
				com = new JSONObject(transCom);
				String iconurl = com.getString("icon");
				String name = com.getString("name");
				String signature = com.getString("signature");
				String charge = com.getString("charge");
				String logistics = com.getString("logistics");
				String service = com.getString("service");
				ImageLoader.getInstance().displayImage(iconurl,
						(ImageView) findViewById(R.id.puragent_head));
				((TextView) findViewById(R.id.user_name)).setText(name);
				((TextView) findViewById(R.id.user_introduce))
						.setText(signature);
				((TextView) findViewById(R.id.puragent_feeval)).setText(charge);
				((TextView) findViewById(R.id.puragent_shippingval))
						.setText(logistics);
				((TextView) findViewById(R.id.puragent_serviceval))
						.setText(service);
				((TextView) findViewById(R.id.transport_transtimes))
						.setText(getText(R.string.transport_transtimes)
								.toString().replace("$", used + ""));
				addressArray = new JSONArray(reqParams.getString("address"));
				((ListView) findViewById(R.id.transport_company_addresses))
						.setAdapter(new AddressAdapter());
			}
		} catch (JSONException e1) {

			e1.printStackTrace();
		}
	}
	class AddressAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return addressArray == null ? 0 : addressArray.length();
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {

			try {
				return addressArray.getJSONObject(position).getLong("id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater
						.from(TransportComAddress.this)
						.inflate(R.layout.listview_transport_address_item, null);
				holder = new ViewHolder();
				holder.transport_company_name = (TextView) convertView
						.findViewById(R.id.transport_company_name);
				holder.transport_address_firstname = (TextView) convertView
						.findViewById(R.id.transport_address_firstname);
				holder.transport_address_lastname = (TextView) convertView
						.findViewById(R.id.transport_address_lastname);
				holder.transport_address_address = (TextView) convertView
						.findViewById(R.id.transport_address_address);
				holder.transport_address_city = (TextView) convertView
						.findViewById(R.id.transport_address_city);
				holder.transport_address_zipcode = (TextView) convertView
						.findViewById(R.id.transport_address_zipcode);
				holder.transport_address_state = (TextView) convertView
						.findViewById(R.id.transport_address_state);
				holder.transport_address_unit = (TextView) convertView
						.findViewById(R.id.transport_address_unit);
				holder.transport_address_tel = (TextView) convertView
						.findViewById(R.id.transport_address_tel);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				JSONObject data = addressArray.getJSONObject(position);
				holder.transport_company_name.setText(data.getString("name"));
				holder.transport_address_firstname.setText(data
						.getString("firstName"));
				holder.transport_address_lastname.setText(data
						.getString("lastName"));
				holder.transport_address_address.setText(data
						.getString("address"));
				holder.transport_address_city.setText(data.getString("city"));
				holder.transport_address_zipcode.setText(data
						.getString("zipCode"));
				holder.transport_address_state.setText(data.getString("state"));
				holder.transport_address_unit.setText(data.getString("unit"));
				holder.transport_address_tel.setText(data.getString("tel"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return convertView;
		}
		class ViewHolder {
			TextView transport_company_name;
			TextView transport_address_firstname;
			TextView transport_address_lastname;
			TextView transport_address_address;
			TextView transport_address_city;
			TextView transport_address_zipcode;
			TextView transport_address_state;
			TextView transport_address_unit;
			TextView transport_address_tel;

		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();
			
			break;

		default:
			break;
		}
		
	}
}
