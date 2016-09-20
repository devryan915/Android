package com.langlang.dialog;


import java.util.ArrayList;

import com.langlang.elderly_langlang.R;
import com.langlang.elderly_langlang.R.color;
import com.langlang.global.UserInfo;
import com.langlang.interfaces.CityCallBack;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SelectCityDialog extends Dialog {
	private ListView listView;
	private MyAdapter adapter;
	private ArrayList<String> list;
	private Context context;
	private CityCallBack cityCallBack;
	private SharedPreferences city_index_sp;
	public SelectCityDialog(Context context, ArrayList<String> list,CityCallBack cityCallBack) {
		super(context);
		this.list = list;
		this.context = context;
		this.cityCallBack=cityCallBack;
		city_index_sp = context.getSharedPreferences("city_index",
				0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setFullScrean();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_selectuser);
		
		listView = (ListView) this.findViewById(R.id.selectuser_listview);
		adapter = new MyAdapter();
		for (int i = 0; i < list.size(); i++) {
			adapter.addListItem(list.get(i));
		}
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				UserInfo.getIntance().getUserData().setTemporary_province(list.get(position));
				cityCallBack.callBacks(position);

			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.cancel();
		}
		return super.onKeyDown(keyCode, event);

	}

	public void setFullScrean() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	class MyAdapter extends BaseAdapter {
		private ArrayList<String> arrayList;
		public MyAdapter() {
			arrayList = new ArrayList<String>();
		}
		public void addListItem(String item) {
			arrayList.add(item);
		}

		public void clear() {
			arrayList.clear();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textView = new TextView(context);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setTextColor(color.golden);
			textView.setPadding(30, 0, 0, 0);
			textView.setTextSize(20);
			textView.setBackgroundResource(R.drawable.user_list_off);
//			if(city_index_sp.getInt(UserInfo.getIntance().getUserData().getMy_name(), 2)==position){
//				textView.setBackgroundResource(R.drawable.user_list_choose);
//			}
			
			textView.setText(arrayList.get(position));
			return textView;
		}

	}

}
