package com.kc.ihaigo.ui.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.MyAddressAdapter;
import com.kc.ihaigo.ui.personal.adapter.MyAddressAdapter.ViewHolder;
import com.kc.ihaigo.ui.personal.adapter.MyIdentityAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 我的信息
 * 
 * @author zonxianbin
 * 
 */
public class MyMessageActivity extends IHaiGoActivity {
	/**
	 * 收货地址
	 */
	private TextView ress;

	/**
	 * 身份信息
	 */
	private TextView identity;
	/**
	 * 收货地址适配器
	 */
	private MyAddressAdapter addAdapter;
	/**
	 * 身份信息适配器
	 */
	private MyIdentityAdapter idAdapter;

	private String FLAG = "0";
	private String FLAG_RESS = "0";
	private String FLAG_IDENTITY = "1";
	private ListView cardlistView;
	private ListView addrListView;

	private String TAG = "MyMessageActivity";
	private String mobile;
	private String page = "1";
	private String head_image_url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_message);
		initTitle();
		initCooseIitle();
		setAddress();

	}

	@Override
	public void refresh() {
		super.refresh();
		if (PersonalActivity.class.equals(parentClass)) {
			mobile = getIntent().getStringExtra("mobile");
			head_image_url = getIntent().getStringExtra("headPortnextrait");
//			ImageLoader.getInstance().displayImage(head_image_url+"",
//					 ((ImageView) findViewById(R.id.iv_edit_personal_user_head)));
		} else if (AddressActivity.class.equals(parentClass)) {
			getUserAddress();
		} else if (EditDefaultAddressInfo.class.equals(parentClass)) {
			getUserAddress();
		} else if (AddIdentityActivity.class.equals(parentClass)) {
			getUserCard();
		} else if (EditDefaultCardInfo.class.equals(parentClass)) {
			getUserCard();
		}
	}

	/***
	 * 
	 * @Title: initComponents
	 * @user: helen.yang
	 * @Description: TODO void
	 * @throws
	 */

	private void initCooseIitle() {
		ress = (TextView) findViewById(R.id.ress);
		ress.setOnClickListener(this);
		ress.setBackgroundResource(R.drawable.choose_item_lift_selected_shape);
		ress.setTextColor(this.getResources().getColor(R.color.white));

		identity = (TextView) findViewById(R.id.identity);
		identity.setOnClickListener(this);
		identity.setBackgroundResource(R.drawable.choose_item_right_shape);
		identity.setTextColor(this.getResources().getColor(R.color.choose));

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
		findViewById(R.id.title_right).setOnClickListener(this);
		cardlistView = (ListView) findViewById(R.id.mycardlistView);
		cardlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Intent intent = new Intent(MyMessageActivity.this,
						EditDefaultCardInfo.class);
				MyIdentityAdapter.ViewHolder holder = (com.kc.ihaigo.ui.personal.adapter.MyIdentityAdapter.ViewHolder) view
						.getTag();
				String idcardImage = (String) holder.id.getTag();
				String idcardImageBack = (String) holder.userName.getTag();
				String status = (String) holder.userNameber.getTag();
				intent.putExtra("id", holder.id.getText());
				intent.putExtra("status", status);
				intent.putExtra("name", holder.userName.getText());
				intent.putExtra("idNumber", holder.userNameber.getText());
				intent.putExtra("idcardImage", idcardImage);
				intent.putExtra("idcardImageBack", idcardImageBack);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
			}
		});
		addrListView = (ListView) findViewById(R.id.my_address_listView);
		addrListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
//				Toast.makeText(MyMessageActivity.this,
//						"点击了addrListView" + id + position, 1).show();
				Intent intent = new Intent(MyMessageActivity.this,
						EditDefaultAddressInfo.class);
				MyAddressAdapter.ViewHolder holder = (ViewHolder) view.getTag();
				String userid = (String) holder.userName.getTag();
				String status = (String) holder.userRegion.getTag();
				intent.putExtra("id", holder.id.getText());
				intent.putExtra("userId", userid);
				intent.putExtra("status", status);
				intent.putExtra("contacts", holder.userName.getText());
				intent.putExtra("contactNumber",
						holder.contact_number.getText());
				intent.putExtra("userArea", holder.userRegion.getText());
				intent.putExtra("userAddr", holder.infoRegion.getText());
				intent.putExtra("postalCode", holder.postalCode.getText());
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);
			}
		});

		if ("0".equals(FLAG)) {
			addrListView.setVisibility(View.VISIBLE);
			cardlistView.setVisibility(View.GONE);

		} else {
			addrListView.setVisibility(View.GONE);
			cardlistView.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalActivity");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		resParams = new Bundle();
		resParams.putString("mobile", mobile);
		showTabHost = true;
		super.back();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			Intent intent = new Intent(MyMessageActivity.this,
					PersonalActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;

		case R.id.title_right:
			if (FLAG.equals(FLAG_IDENTITY)) {
				Intent inte = new Intent(MyMessageActivity.this,
						AddIdentityActivity.class);
				inte.putExtra("mobile", mobile);
				inte.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				inte.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				PersonalGroupActivity.group.startiHaiGoActivity(inte);
			} else {
				Intent inte = new Intent(MyMessageActivity.this,
						AddressActivity.class);
				inte.putExtra("mobile", mobile);
				inte.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				inte.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				PersonalGroupActivity.group.startiHaiGoActivity(inte);
			}

			break;
		case R.id.ress:
			FLAG = FLAG_RESS;
			addrListView.setVisibility(View.VISIBLE);
			cardlistView.setVisibility(View.GONE);
			ress.setBackgroundResource(R.drawable.choose_item_lift_selected_shape);
			ress.setTextColor(this.getResources().getColor(R.color.white));

			identity.setBackgroundResource(R.drawable.choose_item_right_shape);
			identity.setTextColor(this.getResources().getColor(R.color.choose));
			setAddress();

			break;

		case R.id.identity:

			FLAG = FLAG_IDENTITY;
			addrListView.setVisibility(View.GONE);
			cardlistView.setVisibility(View.VISIBLE);
			setIdentity();

			ress.setBackgroundResource(R.drawable.choose_item_lift_shape);
			ress.setTextColor(this.getResources().getColor(R.color.choose));

			identity.setBackgroundResource(R.drawable.choose_item_right_selected_shape);
			identity.setTextColor(this.getResources().getColor(R.color.white));

			break;

		default:
			break;
		}

	}

	private void setIdentity() {
		List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("11", "1");
		lists.add(map);
		idAdapter = new MyIdentityAdapter(MyMessageActivity.this,head_image_url);
		getUserCard();
	}

	private void setAddress() {
		List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("11", "1");
		lists.add(map);
		addAdapter = new MyAddressAdapter(MyMessageActivity.this);
		getUserAddress();

	}

	/**
	 * 显示我的收获地址列表
	 */
	private void getUserAddress() {
		final String url = Constants.GETUSERADDRESS_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", Constants.USER_ID);
		map.put("page", page);
		map.put("pagesize", Constants.APP_DATA_LENGTH);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject jsonObject = new JSONObject(result);
								JSONArray json = jsonObject
										.getJSONArray("userAddress");

								if (json != null) {
									addAdapter.setDatas(json);
									addrListView.setAdapter(addAdapter);
									addAdapter.notifyDataSetChanged();
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
	 * @Title: getUserCard
	 * @user: helen.yang
	 * @Description: 得到身份信息列表 void
	 * @throws
	 */
	private void getUserCard() {
		final String url = Constants.GETUSERCARD_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", Constants.USER_ID);
		map.put("page", page);
		map.put("pagesize", Constants.APP_DATA_LENGTH);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject jsonObject = new JSONObject(result);
								JSONArray json = jsonObject
										.getJSONArray("userCard");

								if (json != null) {
									idAdapter.setDatas(json);
									cardlistView.setAdapter(idAdapter);
									idAdapter.notifyDataSetChanged();
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
