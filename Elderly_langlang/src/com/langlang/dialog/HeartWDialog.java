package com.langlang.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.langlang.adapter.HeartWAdapter;
import com.langlang.data.ValueEntry;
import com.langlang.elderly_langlang.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

public class HeartWDialog extends Dialog{
	private Context context;
	private ListView listView;
	private HeartWAdapter adapter;
	private ArrayList<ValueEntry> arrayList;

	public HeartWDialog(Context context,ArrayList<ValueEntry> arrayList) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.arrayList=arrayList;
		
	}
	private void setAdapterList(){
		for(int i=0;i<arrayList.size();i++){
			ValueEntry valueEntry=arrayList.get(i);
			String item="时间："+longToString(valueEntry.timestamp)+"，心率："+valueEntry.value;
			adapter.addItem(item);
		}
		adapter.notifyDataSetChanged();
	}
	private String longToString(long datal){
		Date date=new Date();
		date.setTime(datal);
		String datesString=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		return datesString;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setFullScrean();
		setContentView(R.layout.dialog_praise);
		listView = (ListView) this.findViewById(R.id.dialog_praise_listivew);
		adapter = new HeartWAdapter(context);
		setAdapterList();
		listView.setAdapter(adapter);
	}
	public void setFullScrean() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
//			adapter.clear();
			this.cancel();
		}
		return super.onKeyDown(keyCode, event);
	}
}
