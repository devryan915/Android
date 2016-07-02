package com.kc.ihaigo.ui.shipping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.personal.PersonalLogisticsActivity;
import com.kc.ihaigo.ui.shipping.adapter.ShippingItemAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 运单详情--手动生成
 * 
 * @author Thinkpad
 * 
 */
public class WayBillInfoActivity extends IHaiGoActivity {
	private ShippingItemAdapter adapter;
	/**
	 * 显示图片
	 */
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	private ListView shippingItem;
	/**
	 * 订单编号
	 */
	private TextView order_Nameber;
	/**
	 * LOGO
	 */
	private ImageView logo_img;
	/**
	 * 名称
	 */
	private TextView uses_Name;
	/**
	 * 运单名称
	 */
	private TextView order_name;
	/**
	 * 备注
	 */
	private TextView note;
	/**
	 * 列新时间
	 */
	private TextView order_Tite;
	/**
	 * 添加BUTTOn
	 * 
	 * 
	 */
	private TextView addTheAwd;
	/**
	 * 物流ID
	 */
	private String shipid;
	/**
	 * 物流名称
	 */
	private String chooseLogis;
	/**
	 * 弹出层
	 */
	private LinearLayout dia_none;
	/**
	 * 订单编写号
	 * 
	 */
	private EditText orerod_number;
	/**
	 * 先择物流
	 */
	private TextView choose;

	/**
	 * 运单号
	 */

	private String number;
	/**
	 * 包裹内容
	 */
	private String content = "";
	/**
	 * 备注
	 */
	private String noteString;
	/**
	 * id快递
	 */
	private String id;
	/**
	 * 运单ID
	 */
	private String contentId;
	private TextView way_Nameber;

	private String orderNameString;
	private String noteString2;
	private Class<IHaiGoActivity> lparentClass;

	private List<Map<String, String>> list;
	private Map<String, String> map;
	private IHaiGoGroupActivity lparentGroupActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waybill_info);

		Image();
		initTitle();

	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		shippingItem = (ListView) findViewById(R.id.shippingItem);
		order_Nameber = (TextView) findViewById(R.id.order_Nameber);
		logo_img = (ImageView) findViewById(R.id.logo_img);
		uses_Name = (TextView) findViewById(R.id.uses_Name);
		order_name = (TextView) findViewById(R.id.order_name);
		note = (TextView) findViewById(R.id.note);
		order_Tite = (TextView) findViewById(R.id.order_Tite);
		addTheAwd = (TextView) findViewById(R.id.addTheAwd);
		addTheAwd.setOnClickListener(this);

		dia_none = (LinearLayout) findViewById(R.id.dia_none);
		findViewById(R.id.dia_title_left).setOnClickListener(this);

		findViewById(R.id.dia_title_right).setOnClickListener(this);
		orerod_number = (EditText) findViewById(R.id.orerod_number);
		findViewById(R.id.companyLayout).setOnClickListener(this);
		choose = (TextView) findViewById(R.id.choose);

		way_Nameber = (TextView) findViewById(R.id.way_Nameber);

	}

	/**
	 * 物流详情请求
	 */
	private void allShipping(String id) {

		String url = Constants.REC_SHIPPING_DATA + id + "?user="
				+ Constants.USER_ID;
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, null,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {

								Long logItemLong = Utils.getCurrentTime();// 获取当前时间
								Long itmeLongbt = null;

								String timeString = null;
								String locString = null;
								List<Map<String, String>> list = new ArrayList<Map<String, String>>();

								JSONObject jsonObject = new JSONObject(result);

								JSONObject logistics = jsonObject
										.getJSONObject("logistics");

								DataConfig dataConfig = new DataConfig(
										WayBillInfoActivity.this);
								String name = dataConfig
										.getLcompanyName(logistics
												.getString("company"));
								String icon = dataConfig
										.getLcompanyIcon(logistics
												.getString("company"));
								if (!TextUtils.isEmpty(icon)) {
									imageLoader.displayImage(icon, logo_img,
											options, animateFirstListener);
								}

								String waybillNumber = logistics
										.getString("waybillNumber");// 运单号
								order_Nameber.setText(waybillNumber);
								way_Nameber.setText(name);

								JSONArray items = logistics
										.optJSONArray("items");

								for (int i = 0; i < items.length(); i++) {
									itmeLongbt = items.getJSONObject(0)
											.getLong("time");
									locString = items.getJSONObject(i)
											.getString("location");
									Long time = items.getJSONObject(i).getLong(
											"time");
									timeString = Utils.getCurrentTime(time,
											"yyyy-MM-dd  HH:mm");
									Map<String, String> map = new HashMap<String, String>();
									map.put("time", timeString);
									map.put("location", locString);
									list.add(map);
								}

								adapter = new ShippingItemAdapter(
										WayBillInfoActivity.this, list);
								shippingItem.setAdapter(adapter);
								adapter.notifyDataSetChanged();
								if (itmeLongbt != null) {
									String itemString = Utils.compareTime(
											logItemLong, itmeLongbt);// 时间差

									order_Tite.setText(itemString);
								}
								orderNameString = logistics
										.getString("content");

								noteString2 = logistics.getString("remark");
								order_name.setText(orderNameString);
								note.setText(noteString2);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							Toast.makeText(WayBillInfoActivity.this,
									getString(R.string.connection_time),
									Toast.LENGTH_LONG).show();
						}

					}

				}, 0, R.string.loading);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.addTheAwd:
			dia_none.setVisibility(View.VISIBLE);

			break;
		case R.id.companyLayout:
			// 选择物流
			Intent inte = new Intent(WayBillInfoActivity.this,
					WayChooseInfoActivity.class);
			inte.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			inte.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			lparentGroupActivity.startiHaiGoActivity(inte);
			// if (ShippingActivity.class.equals(lparentClass)) {
			// ShippingGroupActiviy.group.startiHaiGoActivity(inte);
			// } else if (PersonalLogisticsActivity.class.equals(lparentClass))
			// {
			// PersonalGroupActivity.group.startiHaiGoActivity(inte);
			// }
			break;
		case R.id.dia_title_left:
			dia_none.setVisibility(View.GONE);
			break;
		case R.id.dia_title_right:
			number = orerod_number.getText().toString().trim();
			if (TextUtils.isEmpty(number)) {
				Toast.makeText(getApplication(), "请输入运单号", Toast.LENGTH_SHORT)
						.show();

			} else if (TextUtils.isEmpty(id)) {
				Toast.makeText(getApplication(), "请选择物流公司", Toast.LENGTH_SHORT)
						.show();
			} else {
				setAdd();
				dia_none.setVisibility(View.GONE);

			}

			break;

		case R.id.title_right:
			Intent intent = new Intent(WayBillInfoActivity.this,
					WayBillEditorActivity.class);
			Bundle dle = new Bundle();
			dle.putString("id", contentId);
			dle.putString("orderNameString", orderNameString);
			dle.putString("noteString2", noteString2);
			intent.putExtras(dle);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			lparentGroupActivity.startiHaiGoActivity(intent);
			// if (ShippingActivity.class.equals(parentClass)) {
			// ShippingGroupActiviy.group.startiHaiGoActivity(intent);
			//
			// } else if (PersonalLogisticsActivity.class.equals(parentClass)) {
			//
			// PersonalGroupActivity.group.startiHaiGoActivity(intent);
			//
			// }

			break;
		default:
			break;
		}

	}

	private void setAdd() {
		String url = Constants.REC_SHIPPING_ADD + Constants.USER_ID + "/add";
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("logistics", id);
		map.put("waybill", number);
		map.put("content", content);
		map.put("remark", noteString);
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
									Toast.makeText(getApplication(),
											"网络异常,请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}
							} else {
								Toast.makeText(WayBillInfoActivity.this,
										getString(R.string.connection_time),
										Toast.LENGTH_LONG).show();
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}, 0, R.string.loading);

	}

	private void Image() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	@Override
	public void refresh() {
		super.refresh();
		Bundle resParams = getIntent().getExtras();
		if (ShippingActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			contentId = resParams.getString("id");
			lparentGroupActivity = parentGroupActivity;
		}

		if (PersonalLogisticsActivity.class.equals(parentClass)) {
			lparentClass = parentClass;
			contentId = resParams.getString("id");
			lparentGroupActivity = parentGroupActivity;
		}

		if (!TextUtils.isEmpty(contentId)) {
			allShipping(contentId);
		}

		// if (WayChooseInfoActivity.class.equals(parentClass)) {
		// try {
		// lparentClass = (Class<IHaiGoActivity>) Class
		// .forName("com.kc.ihaigo.ui.shipping.ShippingActivity");
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		// 获取到选择物流公司返回的值
		chooseLogis = resParams.getString("name");
		id = resParams.getString("shipid");
		if (!TextUtils.isEmpty(id)) {
			choose.setText(chooseLogis);
			choose.setTextColor(this.getResources().getColor(
					R.color.add_edt_title));
		}

	}

	@Override
	protected void back() {

		parentClass = lparentClass;
		showTabHost = true;
		refreshActivity = true;
		resParams = new Bundle();
		// resParams.putInt(key, value);
		super.back();
	}

}
