package com.langlang.activity;

import com.langlang.adapter.SkinAdapter;
import com.langlang.elderly_langlang.R;
import com.langlang.utils.UIUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SkinActivity extends BaseActivity {
	private SharedPreferences app_sp;
	private SharedPreferences index_sp;
	private ListView listView;
	private SkinAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app_sp = getSharedPreferences("app_skin", MODE_PRIVATE);
		index_sp = getSharedPreferences("skin_index", MODE_PRIVATE);
		getViewId();
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Editor ed = app_sp.edit();
				Editor skin_ed = index_sp.edit();
				skin_ed.putInt("skin_index", arg2);
				skin_ed.commit();
				if (arg2 == 0) {
					UIUtil.setToast(SkinActivity.this, "金镶玉版皮肤选择成功");
					ed.putString("skin", "defal");
				} else if (arg2 == 1) {
					UIUtil.setToast(SkinActivity.this, "太空版皮肤选择成功");
					ed.putString("skin", "skin_one");
				}
				ed.commit();
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void getViewId() {
		setContentView(R.layout.activity_skin);
		listView = (ListView) this.findViewById(R.id.skin_listview);
		adapter = new SkinAdapter(SkinActivity.this);
		listView.setAdapter(adapter);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(SkinActivity.this, SetActivity.class));
			SkinActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
