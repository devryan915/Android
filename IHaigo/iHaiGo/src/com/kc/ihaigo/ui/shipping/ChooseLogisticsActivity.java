package com.kc.ihaigo.ui.shipping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.myorder.ForecastAddActivity;
import com.kc.ihaigo.ui.shipping.adapter.ChooseLogisticsAdapter;
import com.kc.ihaigo.ui.shipping.listview.CharacterParser;
import com.kc.ihaigo.ui.shipping.listview.PinyinComparator;
import com.kc.ihaigo.ui.shipping.listview.SideBar;
import com.kc.ihaigo.ui.shipping.listview.SideBar.OnTouchingLetterChangedListener;
import com.kc.ihaigo.util.DataConfig;

/**
 * 选择物流公司
 * 
 * @author zouxianbin
 * 
 */
public class ChooseLogisticsActivity extends IHaiGoActivity implements
		OnClickListener {
	/**
	 * 国内物流
	 */
	private TextView domestic;
	/**
	 * 转动物流
	 */
	private TextView turn_the;
	/**
	 * 海外物流
	 */
	private TextView overseas;

	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private ChooseLogisticsAdapter adapter;
	// private ClearEditText mClearEditText;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<JSONObject> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private boolean flag;

	private JSONArray datas;

	private int code;
	private static final int dom = 0;
	private static final int turn = 1;
	private static final int ove = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_logistics);
		initTitle();
		initViews();
		initCooseIitle();
		initComponets();

	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);

	}

	private void initComponets() {

	}

	private void initCooseIitle() {
		domestic = (TextView) findViewById(R.id.domestic);
		domestic.setOnClickListener(this);
		domestic.setBackgroundResource(R.drawable.choose_item_lift_selected_shape);
		domestic.setTextColor(this.getResources().getColor(R.color.white));

		turn_the = (TextView) findViewById(R.id.turn_the);
		turn_the.setOnClickListener(this);
		turn_the.setBackgroundResource(R.color.white);
		turn_the.setTextColor(this.getResources().getColor(R.color.choose));
		overseas = (TextView) findViewById(R.id.overseas);
		overseas.setOnClickListener(this);
		overseas.setBackgroundResource(R.drawable.choose_item_right_shape);
		overseas.setTextColor(this.getResources().getColor(R.color.choose));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			finish();

		case R.id.domestic:
			code = dom;
			SourceDateList = filledData(datas, code);
			Collections.sort(SourceDateList, pinyinComparator);
			adapter.setDatas(SourceDateList);
			adapter.notifyDataSetChanged();

			domestic.setBackgroundResource(R.drawable.choose_item_lift_selected_shape);
			domestic.setTextColor(this.getResources().getColor(R.color.white));

			turn_the.setBackgroundResource(R.color.white);
			turn_the.setTextColor(this.getResources().getColor(R.color.choose));

			overseas.setBackgroundResource(R.drawable.choose_item_right_shape);
			overseas.setTextColor(this.getResources().getColor(R.color.choose));

			break;
		case R.id.turn_the:
			code = turn;
			SourceDateList = filledData(datas, code);
			Collections.sort(SourceDateList, pinyinComparator);
			adapter.setDatas(SourceDateList);
			adapter.notifyDataSetChanged();

			domestic.setBackgroundResource(R.drawable.choose_item_lift_shape);
			domestic.setTextColor(this.getResources().getColor(R.color.choose));

			turn_the.setBackgroundResource(R.color.choose);
			turn_the.setTextColor(this.getResources().getColor(R.color.white));

			overseas.setBackgroundResource(R.drawable.choose_item_right_shape);
			overseas.setTextColor(this.getResources().getColor(R.color.choose));
			break;
		case R.id.overseas:
			code = ove;
			SourceDateList = filledData(datas, code);
			adapter.setDatas(SourceDateList);
			Collections.sort(SourceDateList, pinyinComparator);
			adapter.notifyDataSetChanged();
			domestic.setBackgroundResource(R.drawable.choose_item_lift_shape);
			domestic.setTextColor(this.getResources().getColor(R.color.choose));

			turn_the.setBackgroundResource(R.color.white);
			turn_the.setTextColor(this.getResources().getColor(R.color.choose));

			overseas.setBackgroundResource(R.drawable.choose_item_right_selected_shape);
			overseas.setTextColor(this.getResources().getColor(R.color.white));

			break;

		default:
			break;
		}

	}

	private void initViews() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView textView = (TextView) view.findViewById(R.id.title);
				String idString = String.valueOf(textView.getTag());
				String name = textView.getText().toString().trim();

				Intent data = new Intent();
				data.putExtra("name", name);
				data.putExtra("shipid", idString);
				// 请求代码可以自己设置，这里设置成1
				setResult(2, data);
				// 关闭掉这个Activity
				finish();

			}
		});
		DataConfig dataConfig = new DataConfig(ChooseLogisticsActivity.this);
		String lcompany = dataConfig.getLcompany();
		try {
			JSONObject resData = new JSONObject(lcompany);
			datas = resData.getJSONArray("company");
			SourceDateList = filledData(datas, code);
			Collections.sort(SourceDateList, pinyinComparator);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 根据a-z进行排序源数据
		adapter = new ChooseLogisticsAdapter(this);
		adapter.setDatas(SourceDateList);
		sortListView.setAdapter(adapter);

		// mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// //根据输入框输入值的改变来过滤搜索
		// mClearEditText.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		// //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		// filterData(s.toString());
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// }
		// });
	}

	/**
	 * 
	 * @Title: filledData
	 * @user: ryan.wang
	 * @Description: 将物流公司名称按照拼音首字母排序
	 * 
	 * @param datas
	 * @return JSONArray
	 * @throws
	 */
	private List<JSONObject> filledData(JSONArray datas, int code) {
		List<JSONObject> fillDatas = null;
		try {
			fillDatas = new ArrayList<JSONObject>();
			for (int i = 0; i < datas.length(); i++) {
				JSONObject data = datas.getJSONObject(i);
				int type = data.getInt("type");
				if (type == code) {
					String name = data.getString("name");
					String letter = (String) characterParser.getSelling(name)
							.subSequence(0, 1);
					data.put("letter", letter);
					fillDatas.add(data);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fillDatas;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<JSONObject> filterDateList = new ArrayList<JSONObject>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (JSONObject data : SourceDateList) {
				String name = null;
				try {
					name = data.getString("name");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(data);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	@Override
	public void refresh() {
		// Toast.makeText(PurchasingAgent.this,
		// getIntent().getCharSequenceExtra("key"), 1000).show();;

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void reBack() {

		Intent intent = new Intent(ChooseLogisticsActivity.this,
				ForecastAddActivity.class);
		intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, false);
		intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		ShippingGroupActiviy.group.startiHaiGoActivity(intent);

	}

}
