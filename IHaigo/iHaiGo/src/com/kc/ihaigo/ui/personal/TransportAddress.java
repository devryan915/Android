/**
 * @Title: TransportChargeIntro.java
 * @Package: com.kc.ihaigo.ui.personal
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年8月11日 下午1:37:34

 * @version V1.0

 */


package com.kc.ihaigo.ui.personal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.TransportAddressAdapter;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName: TransportChargeIntro
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年8月11日 下午1:37:34
 *
 */

public class TransportAddress extends IHaiGoActivity {

	/**
	 * 收费详细
	 */
	private TextView transport_status_intro;
	private StringBuffer buff;
	/**
	 * 商品ICOM
	 */
	private ImageView puragent_head;
	/**
	 * 名称
	 */
	private TextView merchant_name;
	/**
	 * 转运
	 */
	private TextView transfer;
	/**
	 * 个性签名
	 */
	private TextView pruagent_creditlevel;
	/**
	 * 收费
	 */
	private TextView puragent_feeval;
	/**
	 * 物流
	 */
	private TextView puragent_shippingval;
	/**
	 * 服务
	 */
	private TextView puragent_serviceval;
	
	/**
	 * 显示图片
	 */
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	private ListView trans_addr_list;
	private TransportAddressAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activiity_transport_address);
		init();
		Image();
	}
	

	private void init() {
		puragent_head = (ImageView) findViewById(R.id.puragent_head);
		merchant_name = (TextView) findViewById(R.id.merchant_name);
		transfer = (TextView) findViewById(R.id.transfer);
		pruagent_creditlevel = (TextView) findViewById(R.id.pruagent_creditlevel);
		puragent_feeval = (TextView) findViewById(R.id.puragent_feeval);
		puragent_shippingval = (TextView) findViewById(R.id.puragent_shippingval);
		puragent_serviceval = (TextView) findViewById(R.id.puragent_serviceval);
		
		
		trans_addr_list = (ListView) findViewById(R.id.trans_addr_list);
		adapter = new TransportAddressAdapter(TransportAddress.this);
		trans_addr_list.setAdapter(adapter);
	}


	@Override
	public void refresh() {
		super.refresh();
		if(TransportMerchantDetailActivity.class.equals(parentClass)){
			
			getTransport();
		}
	}
	private void Image() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}
	/**
	 * 转动商详情
	 * 
	 * @param cid
	 */

	private void getTransport() {
		DataConfig da = new DataConfig(TransportAddress.this);
//		String Tcompany = da.getTcompanySty(cid);
		String Tcompany = da.getTcompany();
		JSONArray com;
		try {
			JSONObject resData = new JSONObject(Tcompany);
			com = resData.getJSONArray("company");
			Log.i("geek", "转运公司"+com.toString());
			if (com != null && com.length() > 0) {
				for (int i = 0; i < com.length(); i++) {
					String id = com.getJSONObject(i).getString("id");
					String name = com.getJSONObject(i).getString("name");
					String icon = com.getJSONObject(i).getString("icon");
					String signature = com.getJSONObject(i).getString(
							"signature");
					String open = com.getJSONObject(i).getString("open");
					String charge = com.getJSONObject(i).getString("charge");
					String logis = com.getJSONObject(i).getString("logistics");
					String service = com.getJSONObject(i).getString("service");
					
					JSONArray addrjsonArray = com.getJSONObject(i).getJSONArray("addresses");
					adapter.setDatas(addrjsonArray);
					adapter.notifyDataSetChanged();
					
					if (!TextUtils.isEmpty(icon)) {
						imageLoader.displayImage(icon, puragent_head, options,
								animateFirstListener);
					}
					merchant_name.setText(name);
					puragent_shippingval.setText(logis);
					pruagent_creditlevel.setText(signature);
					puragent_serviceval.setText(service);
					puragent_feeval.setText(charge);
					transfer.setText(open);
					/**
					 * 
					 * "addresses": [ { "id": 3, "name":
					 * "美国加利福尼亚州洛杉矶(CA)XIANFENGEX仓库", "firstName": "",
					 * "lastName": "NRTC", "address": "245 South 8th Ave",
					 * "unit": "38390", "city": "La Puente/City of industry",
					 * "state": "CA", "zipCode": "91746", "tel": "626-538-8583",
					 * "remark":
					 * "友情提醒： 请大家在购物网站下单时一定记得写上姓后面的4个字母专有识别码和单元号,单元号可以写在第一行地址后面也可以写在地址第二行.(如果网站不认5位数字编号可以不用添加，但4位英文专有码要加上）"
					 * , "currency": "USD", "rate": 6.5}]
					 */

				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
}
