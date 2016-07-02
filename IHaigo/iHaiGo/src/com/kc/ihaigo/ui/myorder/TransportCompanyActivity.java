package com.kc.ihaigo.ui.myorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import com.kc.ihaigo.R;
import com.kc.ihaigo.adapter.myorder.ChooseTransportCompanyAdapter;
import com.kc.ihaigo.ui.shipping.listview.CharacterParser;
import com.kc.ihaigo.ui.shipping.listview.PinyinComparator;
import com.kc.ihaigo.ui.shipping.listview.SideBar;
import com.kc.ihaigo.ui.shipping.listview.SideBar.OnTouchingLetterChangedListener;
import com.kc.ihaigo.util.DataConfig;

/**
 * @Description: 转运公司列表
 * @author: Lijie
 * @date: 2014年7月24日 下午5:09:18
 * 
 */
public class TransportCompanyActivity extends IHaiGoActivity implements
		OnClickListener {

	private ListView transport_lcompany_list;
	private SideBar sideBar;
	private TextView dialog;
	private String TcompanyId;
	private JSONArray datas;
	private ChooseTransportCompanyAdapter adapter;
	public static HashMap<String, String> TransportCompanyMap;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<JSONObject> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transport_company);
		initTitle();

		initViews();

	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();

		case R.id.domestic:
			break;

		default:
			break;
		}

	}

	private void initViews() {

		transport_lcompany_list = (ListView) findViewById(R.id.transport_lcompany_list);
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
					transport_lcompany_list.setSelection(position);
				}

			}
		});

		transport_lcompany_list
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent data = new Intent();
						TextView textView = (TextView) view
								.findViewById(R.id.title);
						String idString = String.valueOf(textView.getTag());
						String name = textView.getText().toString().trim();
						if (TransportCompanyActivity.this.getIntent()
								.getStringExtra("flag").equals("1")) {
							try {
								TcompanyId=datas.getJSONObject(position).getString("id");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							data.putExtra("name", name);
							data.putExtra("TcompanyId", TcompanyId);
							Log.e("=======================", TcompanyId);
							// 请求代码可以自己设置，这里设置成1
							setResult(1, data);
							// 关闭掉这个Activity
							finish();

						}

					}
				});
		DataConfig dataConfig = new DataConfig(TransportCompanyActivity.this);
		String Tcompany = dataConfig.getTcompany();
		try {
			TransportCompanyMap = new HashMap<String, String>();
			JSONObject resData = new JSONObject(Tcompany);
			datas = resData.getJSONArray("company");
			for (int i = 0; i < datas.length(); i++) {
				String transportIcon = datas.getJSONObject(i).getString("icon");
				String transportId = datas.getJSONObject(i).getString("id");
				TransportCompanyMap.put("icon", transportIcon);
			}
			Log.e("icconertyuioooooooooooooo", TransportCompanyMap.get("icon")
					.toString());
			SourceDateList = filledData(datas);
			Collections.sort(SourceDateList, pinyinComparator);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 根据a-z进行排序源数据
		adapter = new ChooseTransportCompanyAdapter(this, SourceDateList);
		transport_lcompany_list.setAdapter(adapter);

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
	 * 
	 * @user: Lijie
	 * @Description: 将物流公司名称按照拼音首字母排序
	 * @return JSONArray
	 * 
	 */
	private List<JSONObject> filledData(JSONArray datas) {
		List<JSONObject> fillDatas = null;
		try {
			fillDatas = new ArrayList<JSONObject>();
			for (int i = 0; i < datas.length(); i++) {
				JSONObject data = datas.getJSONObject(i);
				String name = data.getString("name");
				String letter = (String) characterParser.getSelling(name)
						.subSequence(0, 1);
				data.put("letter", letter);
				fillDatas.add(data);
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

}
