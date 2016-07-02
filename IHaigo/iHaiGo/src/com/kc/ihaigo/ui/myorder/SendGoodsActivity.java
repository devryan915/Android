package com.kc.ihaigo.ui.myorder;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.MyMessageActivity;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.shopcar.adapter.ShopcarChoiceAddressAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DataConfig;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/***
 * @deprecated发货操作
 * @author Lijie
 * **/
public class SendGoodsActivity extends Activity implements OnClickListener {
	private ShopcarChoiceAddressAdapter addressAdapter;
	private TextView divide_box, forecast_address, channel_name;
	// private PopupWindow myChoicePopupWindow;
	// private PopupWindow addServicePopupWindow;
	// private ListView lv_popwin_storage;
	// private ListView lv_popwin_service;
	// private ListView lv_popwin_channel;
	// private JSONArray goods; // 转运商品
	// private String channelInfo; // 渠道信息
	// private ChannelInfoAdapter channelinfoAdapter;
	// private ChoiceIdentifyAdapter choiceIdentifyAdapter;

	private DataConfig dConfig = null;
	private String servicesContent;
	// private List<Vegetable> list;
	// private JSONObject addressParam;
	// private JSONObject billParam;
	// private JSONArray channeljsArray;
	// private AddServicesAdapter addservice;
	private Dialog goodsDialog;
	private Dialog serviceDialog;

	private Dialog channelsDialog;// 选择渠道
	private Dialog identityDialog;// 选择身份信息
	private Dialog addressDialog;// 选择地址
	private MyBackCall channelsCall;// type=1
	private MyBackCall identityCall;// type=2
	private MyBackCall addressCall;// type=3
	private String divideBox;// 分箱标识
	private String voucher;// 保留票据
	private String specialFix;// 特殊加固

	private ChoiceAdapter goodsAdapter;
	private ChoiceAdapter channelAdapter;
	private ChoiceAdapter identityAdapter;
	private View selectedChannelView;
	private LinearLayout transport_send_goods;
	private String goodsId = "";// 传过来的参数
	private String order = "T0000000000017";// 传过来的参数
	private TextView select_service;
	private LinearLayout send_goods_boxes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_goods);
		dConfig = new DataConfig(SendGoodsActivity.this);
		initView();
		insertUserCard();
		initData();
	}

	private void initView() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		select_service = (TextView) findViewById(R.id.select_service);
		select_service.setOnClickListener(this);
		findViewById(R.id.add_address).setOnClickListener(this);
		findViewById(R.id.add_goods).setOnClickListener(this);
		transport_send_goods = (LinearLayout) findViewById(R.id.transport_send_goods);
		channel_name = (TextView) findViewById(R.id.channel_name);
		findViewById(R.id.send_goods_channel).setOnClickListener(this);
		forecast_address = (TextView) findViewById(R.id.forecast_address);
		goodsDialog = DialogUtil.showTransportChoice(SendGoodsActivity.this,
				new BackCall() {
					@Override
					public void deal(int which, Object... obj) {
						goodsDialog.cancel();
						JSONArray datas = goodsAdapter.getDatas();
						if (datas != null) {
							for (int i = 0; i < datas.length(); i++) {
								JSONObject data;
								try {
									data = datas.getJSONObject(i);
									if (data.getBoolean("selected")) {
										boolean ishave = false;
										for (int j = 1; j < transport_send_goods
												.getChildCount(); j++) {
											String id = ((TextView) transport_send_goods
													.getChildAt(j)
													.findViewById(
															R.id.send_goods_name))
													.getTag().toString();
											if (data.getString("id").equals(id)) {
												// 如果当前商品已经选择过，不用重新添加
												ishave = true;
												break;
											}
										}
										if (ishave) {
											continue;
										}
										// transport_send_goods.getchi
										final View selectedGoods = getLayoutInflater()
												.inflate(
														R.layout.activity_send_add_goods,
														null);
										transport_send_goods
												.addView(selectedGoods);
										((TextView) selectedGoods
												.findViewById(R.id.send_goods_name))
												.setText(data.getString("name"));
										((TextView) selectedGoods
												.findViewById(R.id.send_goods_name))
												.setTag(data.getString("id"));
										((TextView) selectedGoods
												.findViewById(R.id.send_goods_weight))
												.setText(data
														.getString("weight"));
										selectedGoods.findViewById(
												R.id.minus_goods)
												.setOnClickListener(
														new OnClickListener() {
															@Override
															public void onClick(
																	View v) {
																transport_send_goods
																		.removeView(selectedGoods);
															}
														});
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}
				});

		goodsAdapter = new ChoiceAdapter(1);// 商品
		ListView goodsListView = ((ListView) goodsDialog
				.findViewById(R.id.dialog_choice_ll));
		goodsListView.setAdapter(goodsAdapter);
		goodsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					if (goodsAdapter.getDatas().getJSONObject(position)
							.getBoolean("selected")) {
						((ViewHolder) view.getTag()).selected
								.setVisibility(View.INVISIBLE);
						goodsAdapter.getDatas().getJSONObject(position)
								.put("selected", false);
					} else {
						((ViewHolder) view.getTag()).selected
								.setVisibility(View.VISIBLE);
						goodsAdapter.getDatas().getJSONObject(position)
								.put("selected", true);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		send_goods_boxes = (LinearLayout) findViewById(R.id.send_goods_boxes);
		serviceDialog = DialogUtil.AddServiceDialog(SendGoodsActivity.this,
				new BackCall() {
					@Override
					public void deal(int whichButton, Object... obj) {
						((Dialog) obj[0]).cancel();
						switch (whichButton) {
							case R.id.add_service_complete :
								Integer[] selected = (Integer[]) obj[1];
								// 对获取的用户选择进行判断
								if (selected[0] == R.id.divide_box) {
									findViewById(R.id.send_goods_selectedbox)
											.setVisibility(View.VISIBLE);
									findViewById(R.id.send_goods_default)
											.findViewById(R.id.box_name)
											.setVisibility(View.VISIBLE);
									View view = getLayoutInflater().inflate(
											R.layout.activity_send_add_boxes,
											null);
									((TextView) view
											.findViewById(R.id.channel_name))
											.setOnClickListener(channelsCall);
									((TextView) view
											.findViewById(R.id.remark_info))
											.setOnClickListener(identityCall);
									((TextView) view
											.findViewById(R.id.forecast_address))
											.setOnClickListener(addressCall);
									send_goods_boxes.addView(view);
									((TextView) view
											.findViewById(R.id.box_name))
											.setText("包裹2");
									divideBox = "1";
								} else if (selected[0] == R.id.not_divide_box) {
									findViewById(R.id.send_goods_selectedbox)
											.setVisibility(View.GONE);
									findViewById(R.id.send_goods_default)
											.findViewById(R.id.box_name)
											.setVisibility(View.GONE);
									for (int i = 1; i < send_goods_boxes
											.getChildCount(); i++) {
										send_goods_boxes.removeViewAt(i);
									}
									divideBox = "0";
								}
								if (selected[1] == R.id.save_voucher) {
									voucher = "1";
								} else if (selected[1] == R.id.not_save_voucher) {
									voucher = "0";
								}
								if (selected[2] == R.id.special_reinforce) {
									specialFix = "1";
								} else {
									specialFix = "0";
								}
								break;
							default :
								break;
						}
					}
				});
		channelsCall = new MyBackCall(1);
		channel_name.setOnClickListener(channelsCall);
		channelsDialog = DialogUtil.showTransportChoice(SendGoodsActivity.this,
				channelsCall);
		channelAdapter = new ChoiceAdapter(2);// 商品
		String channelInfo = dConfig.getTcompanyChannelsById("1");
		try {
			channelAdapter.setDatas(new JSONArray(channelInfo));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		ListView channelListView = ((ListView) channelsDialog
				.findViewById(R.id.dialog_choice_ll));
		channelListView.setAdapter(channelAdapter);
		channelListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// try {
				((ViewHolder) view.getTag()).selected
						.setVisibility(View.VISIBLE);
				// channelAdapter.getDatas().getJSONObject(position)
				// .put("selected", true);
				if (selectedChannelView != null
						&& selectedChannelView != ((ViewHolder) view.getTag()).selected) {
					selectedChannelView.setVisibility(View.INVISIBLE);
					// channelAdapter
					// .getDatas()
					// .getJSONObject(
					// (Integer) selectedChannelView.getTag())
					// .put("selected", false);
				}
				selectedChannelView = ((ViewHolder) view.getTag()).selected;
				selectedChannelView.setTag(position);
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
			}
		});
		identityCall = new MyBackCall(2);// 身份2
		identityAdapter = new ChoiceAdapter(3);
		findViewById(R.id.identify_ll).setOnClickListener(this);
		identityDialog = DialogUtil.showChoiceIdentifyInfo(
				SendGoodsActivity.this, identityCall, null, identityAdapter);
		addressCall = new MyBackCall(3);// 地址3
		forecast_address.setOnClickListener(addressCall);
		addressAdapter = new ShopcarChoiceAddressAdapter(this);
		addressDialog = DialogUtil.showChoiceAddress(SendGoodsActivity.this,
				addressCall, null, addressAdapter);
		findViewById(R.id.remark_info).setOnClickListener(identityCall);
	}
	private void initData() {
		((TextView) findViewById(R.id.send_goods_name)).setTag(goodsId);
		loadGoods();
		loadAddress();
		/***
		 * 通过转运公司Id 获取转运公司的服务信息
		 * **/
		servicesContent = dConfig.getTcompanyServiceById("1");
		loadIdentify();
	}
	private void loadGoods() {
		HttpAsyncTask.fetchData(HttpAsyncTask.GET,
				"http://192.168.1.4:8080/transports/goods"
						+ "/?user=9&transport=1&address=2", null,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						try {
							JSONObject jsobj = new JSONObject(result);
							JSONArray goods = jsobj.getJSONArray("goods");
							goodsAdapter.setDatas(goods);
							goodsAdapter.notifyDataSetChanged();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void loadIdentify() {
		String url = Constants.PER_USERCENTER + "getUserCard";
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("userId", Constants.USER_ID);
		reqParams.put("page", 1 + "");
		reqParams.put("pagesize", Integer.MAX_VALUE + "");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								identityAdapter.setDatas(resData
										.getJSONArray("userCard"));
								identityAdapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);
	}
	private void loadAddress() {
		String url = Constants.PER_USERCENTER + "getUserAddress";
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("userId", Constants.USER_ID);
		reqParams.put("page", 1 + "");
		reqParams.put("pagesize", Integer.MAX_VALUE + "");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								addressAdapter.setDatas(resData
										.getJSONArray("userAddress"));
								addressAdapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, 0);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_left :
				this.finish();
				break;
			case R.id.select_service :
				serviceDialog.show();
				break;
			case R.id.add_goods :
				goodsDialog.show();
				break;
			case R.id.title_right :
				commitSendGoods();
			default :
				break;
		}

	}
	/**
	 * 
	 * @Title: getGoodsId
	 * @user: ryan.wang
	 * @Description: 获取商品ID
	 * 
	 * @return String
	 * @throws
	 */
	private String getGoodsId() {
		StringBuffer ids = new StringBuffer();
		for (int j = 0; j < transport_send_goods.getChildCount(); j++) {
			String id = ((TextView) transport_send_goods.getChildAt(j)
					.findViewById(R.id.send_goods_name)).getTag().toString();
			if (!TextUtils.isEmpty(id)) {
				ids.append(id + ",");
			}
		}
		return ids.length() > 0
				? ids.toString().substring(0, ids.length() - 1)
				: "";
	}
	/**
	 * 
	 * @Title: getPackages
	 * @user: ryan.wang
	 * @Description: 获取包裹信息
	 * 
	 * @return String
	 * @throws
	 */
	private String getPackages() {
		JSONArray pkgs = new JSONArray();
		for (int i = 0; i < send_goods_boxes.getChildCount(); i++) {
			JSONObject data = new JSONObject();
			View view = send_goods_boxes.getChildAt(i);
			TextView package_content = (TextView) view
					.findViewById(R.id.package_content);
			TextView channel_name = (TextView) view
					.findViewById(R.id.channel_name);
			TextView remark_info = (TextView) view
					.findViewById(R.id.remark_info);
			TextView forecast_address = (TextView) view
					.findViewById(R.id.forecast_address);
			TextView desc = (TextView) view.findViewById(R.id.desc);
			try {
				data.put("content", package_content.getText());
				data.put("channel", channel_name.getText());
				data.put("identity", remark_info.getTag());
				data.put("address", forecast_address.getTag());
				data.put("remark", desc.getText());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			pkgs.put(data);
		}
		return pkgs.toString();
	}
	/**
	 * @Title: commitSendGoods
	 * @user: ryan.wang
	 * @Description: TODO void
	 * @throws
	 */
	private void commitSendGoods() {
		String url = "http://192.168.1.4:8080/transports/operate";
		Map<String, Object> reqParams = new HashMap<String, Object>();
		// reqParams.put("user", Constants.USER_ID);
		reqParams.put("user", 9);
		reqParams.put("order", 9);
		reqParams.put("goods", getGoodsId());
		reqParams.put("price", ((EditText) findViewById(R.id.warehouse))
				.getText().toString());
		reqParams.put("service", select_service.getText());
		reqParams.put("package", getPackages());
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, reqParams,
				new HttpReqCallBack() {

					@SuppressLint("ShowToast")
					@Override
					public void deal(String result) {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject resData = new JSONObject(result);
								if ("1".equals(resData.getString("code"))) {
									Toast.makeText(SendGoodsActivity.this,
											"提交成功", 1000).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}
				}, 0, R.string.loading);
	}
	private void insertUserCard() {
		final String url = Constants.INSERTUSERCARD_URL;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", Constants.USER_ID);
		map.put("name", "小李");
		map.put("idNumber", "610523199004127671");
		map.put("idcardImage", "");
		map.put("idcardImageBack", "");
		map.put("status", "1");
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject json = new JSONObject(result);
								String code = json.getString("code");
								JSONObject resData = new JSONObject(result);
								if ("0".equals(code)) {
									Toast.makeText(SendGoodsActivity.this,
											"失败", 1).show();
								} else if ("1".equals(code)) {
									Toast.makeText(SendGoodsActivity.this,
											"才成功ing", 1).show();
									intent.setClass(SendGoodsActivity.this,
											MyMessageActivity.class);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									intent.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									PersonalGroupActivity.group
											.startiHaiGoActivity(intent);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
						}
					}
				}, 1);
	}

	class ChoiceAdapter extends BaseAdapter {
		private JSONArray datas;
		public JSONArray getDatas() {
			return datas;
		}
		public void setDatas(JSONArray datas) {
			this.datas = datas;
		}

		private int itemtype = -1;
		public ChoiceAdapter(int type) {
			this.itemtype = type;
		}
		@Override
		public int getCount() {
			return datas == null ? 0 : datas.length();
		}
		@Override
		public Object getItem(int position) {
			try {
				return datas.get(position);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parents) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(SendGoodsActivity.this)
						.inflate(R.layout.listview_transport_choice_item, null);
				holder = new ViewHolder();
				holder.selected = (ImageView) convertView
						.findViewById(R.id.selected);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				if (itemtype == 3) {
					JSONObject data = datas.getJSONObject(position);
					String name = data.getString("realName");
					String idNumber = data.getString("idNumber");
					holder.name.setText(name + idNumber);
					holder.name.setTag(data.toString());
				} else if (itemtype == 1) {
					// 发货商品
					holder.name.setText(datas.getJSONObject(position)
							.getString("name")
							+ "("
							+ datas.getJSONObject(position).getString("weight")
							+ ")");
					if (datas.getJSONObject(position).isNull("selected")) {
						datas.getJSONObject(position).put("selected", false);
						holder.selected.setVisibility(View.INVISIBLE);
					} else {
						if (datas.getJSONObject(position)
								.getBoolean("selected")) {
							holder.selected.setVisibility(View.VISIBLE);
						} else {
							holder.selected.setVisibility(View.INVISIBLE);
						}
					}
				} else if (itemtype == 2) {
					// 渠道
					holder.name.setText(datas.getJSONObject(position)
							.getString("name"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return convertView;
		}

	}
	class MyBackCall extends BackCall implements OnClickListener {
		public TextView view;
		private int dialogtype = -1;
		public MyBackCall(int type) {
			this.dialogtype = type;
		}
		@Override
		public void deal(int which, Object... obj) {
			switch (dialogtype) {
				case 1 :
					channelsDialog.cancel();
					JSONArray datas = channelAdapter.getDatas();
					if (datas != null) {
						// for (int i = 0; i < datas.length(); i++) {
						JSONObject data;
						try {
							if (selectedChannelView != null
									&& selectedChannelView.getTag() != null) {
								data = datas
										.getJSONObject((Integer) selectedChannelView
												.getTag());
								// if (data.getBoolean("selected")) {
								view.setText(data.getString("name"));
								// view.setTag(data.getInt("id"));
								// }
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						// }
					}
					break;
				case 2 :
					identityDialog.cancel();
					String dataString = obj[1].toString();
					try {
						JSONObject identity = new JSONObject(dataString);
						view.setTag(identity.getString("id"));
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					String text = obj[2].toString();
					view.setText(text);
					break;
				case 3 :
					addressDialog.cancel();
					try {
						switch (which) {
							case R.id.addr_complete :
								try {
									JSONObject address = new JSONObject(
											obj[1].toString());
									// billParam.put("address",
									// addressParam.getString("id"));
									view.setText(obj[2] + "");
									view.setTag(address.getString("id"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
								break;
							case R.id.add_new_addr :
								Intent intent = new Intent(
										SendGoodsActivity.this,
										AddAdressActivity.class);
								startActivity(intent);
								break;
							default :
								break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default :
					break;
			}
		}
		@Override
		public void onClick(View v) {
			this.view = (TextView) v;
			switch (dialogtype) {
				case 1 :
					channelsDialog.show();
					break;
				case 2 :
					identityDialog.show();
					break;
				case 3 :
					addressDialog.show();
					break;
			}
		}

	}
	class ViewHolder {
		public TextView name;
		public ImageView selected;
	}

}
