package com.langlang.dialog;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.langlang.elderly_langlang.R;
import com.langlang.elderly_langlang.R.color;
import com.langlang.global.UserInfo;
import com.langlang.interfaces.RoleCallBack;

import android.app.Dialog;
import android.content.Context;
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

public class SelectUserDialog extends Dialog {
	private ListView listView;
	private MyAdapter adapter;
	private ArrayList<String> list;
	private Context context;
	private RoleCallBack increateCallBack;
	
	public SelectUserDialog(Context context, ArrayList<String> list,
			RoleCallBack increateCallBack) {
		super(context);
		this.list = list;
		this.context = context;
		this.increateCallBack = increateCallBack;
		mapping();
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

				UserInfo.getIntance().getUserData()
						.setMonitoring_object(position);
				UserInfo.getIntance().getUserData().setName(list.get(position));
				UserInfo.getIntance().getUserData().setUser_name(monitoring_list.get(position));
				UserInfo.getIntance().setIndex(position);
				if (position == 0) {// 监护人

					increateCallBack.gaurdianCallBacks();

				} else {// 被监护人

					increateCallBack.userCallBacks();
				}

			}
		});
	}
	private ArrayList<String> monitoring_list = new ArrayList<String>();
	private void mapping(){
		String monitoringString = UserInfo.getIntance().getUserData()
				.getString_monitoring_object();
		if (monitoringString != null || !"".equals(monitoringString)) {
			try {
				JSONArray jsonArray = new JSONArray(monitoringString);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					monitoring_list.add(jsonObject.getString("userName"));

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			if(UserInfo.getIntance().getIndex()==position){
				textView.setBackgroundResource(R.drawable.user_list_choose);
			}
			
			textView.setText(arrayList.get(position));
			return textView;
		}

	}

}
