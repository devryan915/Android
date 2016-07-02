package com.kc.ihaigo.ui.personal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.adapter.myorder.QualityTransportAdapter;
import com.kc.ihaigo.ui.myorder.ForecastAddActivity;
import com.kc.ihaigo.ui.personal.PersonalActivity;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.selfwidget.PullUpRefreshListView;
import com.kc.ihaigo.util.DataConfig;
import com.tencent.mm.sdk.platformtools.Log;

/***
 * 优质转运
 */
public class QualityTransportActivity extends IHaiGoActivity implements
		OnClickListener {
	private PullUpRefreshListView lv_quality_transport;
	private QualityTransportAdapter mAdapter;

	// private List<Vegetable> list = new ArrayList<Vegetable>();
	private JSONArray datas;
	private String transId;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quality_transport);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		lv_quality_transport = (PullUpRefreshListView) findViewById(R.id.lv_quality_transport);
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub

		for (int i = 0; i < 8; i++) {
			// Vegetable vege = new Vegetable();
			// vege.setName("赤兔马快递" + i);
			// list.add(vege);

		}

		DataConfig dataConfig = new DataConfig(QualityTransportActivity.this);
		String Tcompany = dataConfig.getTcompany();
		try {
			JSONObject resData = new JSONObject(Tcompany);
			datas = resData.getJSONArray("company");

			for (int i = 0; i < datas.length(); i++) {
				String transportIcon = datas.getJSONObject(i).getString("icon");
				transId = datas.getJSONObject(i).getString("id");
				Log.i("geek", transId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mAdapter = new QualityTransportAdapter(QualityTransportActivity.this,
				datas);
		lv_quality_transport.setAdapter(mAdapter);
		lv_quality_transport.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(QualityTransportActivity.this,
						TransportMerchantDetailActivity.class);
				intent.putExtra("transId", transId);
				intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
				intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
				PersonalGroupActivity.group.startiHaiGoActivity(intent);

			}
		});

	}

	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalActivity");
			showTabHost = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.back();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			intent.setClass(QualityTransportActivity.this,
					PersonalActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.title_right:
			intent.setClass(QualityTransportActivity.this,
					ForecastAddActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
