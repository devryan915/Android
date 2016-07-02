package com.example.testskin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends Activity implements Skinable {
	private View view1;
	private Button button1;
	private String selSkinID;
	private Dialog dialogChangeSkin;

	DialogSkinListAdapter adapterSkin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		view1 = findViewById(R.id.view1);
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showChangeSkin();
			}
		});
	}

	private void showChangeSkin() {
		LayoutInflater inflater = (LayoutInflater) MainActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_changeskin, null);
		ListView listViewChangeSkin = (ListView) layout
				.findViewById(R.id.listViewChangeSkin);
		selSkinID = PreferencesManager.getInstance().getString(
				ConstantConfig.PREFERENCES_SKINID);
		adapterSkin = new DialogSkinListAdapter(MainActivity.this, selSkinID);
		listViewChangeSkin.setAdapter(adapterSkin);
		listViewChangeSkin.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DialogSkinListAdapter.ViewHolder holder = (DialogSkinListAdapter.ViewHolder) view
						.getTag();
				selSkinID = holder.skin.getSkinID();
				adapterSkin.setSelSkin(selSkinID);
				adapterSkin.notifyDataSetChanged();
			}
		});
		layout.findViewById(R.id.buttonSave).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!(selSkinID != null && !selSkinID.isEmpty())) {
							UIUtil.showToast(MainActivity.this, "请选择皮肤");
							return;
						}
						if (dialogChangeSkin != null) {
							dialogChangeSkin.cancel();
							dialogChangeSkin.dismiss();
						}
						PreferencesManager.getInstance().putString(
								ConstantConfig.PREFERENCES_SKINID, selSkinID);
						SkinManager.getInstance().initSkin();
					}
				});
		dialogChangeSkin = UIUtil.buildDialog(MainActivity.this, layout);
		dialogChangeSkin.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SkinManager.getInstance().registerSkin(this);
		loadSkin();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SkinManager.getInstance().unRegisterSkin(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void loadSkin() {
		view1.setBackground(SkinManager.getInstance().getDrawable(
				R.string.skin_settings_changeskin));
	}
}
