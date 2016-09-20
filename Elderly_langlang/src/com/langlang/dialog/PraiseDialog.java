package com.langlang.dialog;

import java.util.ArrayList;

import com.langlang.adapter.PraiseAdapter;
import com.langlang.elderly_langlang.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

public class PraiseDialog extends Dialog {
	private Context context;
	private ListView listView;
	private PraiseAdapter adapter;
	private ArrayList<String> arrayList;

	public PraiseDialog(Context context,ArrayList<String> arrayList) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.arrayList=arrayList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setFullScrean();
		setContentView(R.layout.dialog_praise);
		listView = (ListView) this.findViewById(R.id.dialog_praise_listivew);
		adapter = new PraiseAdapter(context, arrayList);
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
			this.cancel();
		}
		return super.onKeyDown(keyCode, event);
	}
}
