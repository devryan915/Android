package com.kc.ihaigo.ui.personal;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.RecordConsusmptionAdapter;
import com.kc.ihaigo.ui.personal.adapter.RecordTipUpAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

public class BillRecordActivity extends IHaiGoActivity implements
		OnClickListener {
	private ListView list_consume_record;
	/**
	 * 消费记录
	 */
	private TextView consumption_record;
	/**
	 * 消费记录适配器
	 */
	private RecordConsusmptionAdapter tionAdapter;
	/**
	 * 充值记录
	 */
	private TextView tipUp_record;
	/**
	 * 充值记录适配器
	 */
	private RecordTipUpAdapter tipAdapter;

	private String TAG = "BillRecordActivity";

	private ListView list_pay_record;
	private String FLAG;
	private String FLAG_PAY = "0";
	private String FLAG_CONSUSMPTION = "1";
	private FrameLayout billing_fl;
	private FrameLayout billing_empty;
	private TextView billing_pay_hint;
	private ImageView billing_empty_hint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_record);
		initTitle();
		initCooseIitle();
		
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		list_consume_record = (ListView) findViewById(R.id.list_consume_record);
		list_pay_record = (ListView) findViewById(R.id.list_pay_record);

	}

	/**
	 * 充值记录
	 */
	private void tipUp() {
		tipAdapter = new RecordTipUpAdapter(this);
//		tipAdapter.setDatas(new String[] { "1", "2", "3", "4", "5", "6" });
//		list_pay_record.setAdapter(tipAdapter);
//		billing();
	}

	/**
	 * 消费记录
	 */
	private void Consusmption() {
		tionAdapter = new RecordConsusmptionAdapter(this);
		// tionAdapter.setDatas(new String[] { "1", "2", "3", "4", "5", "6"});
		// list_record.setAdapter(tionAdapter);
		billing();
	}

	private void initCooseIitle() {
		tipUp_record = (TextView) findViewById(R.id.tipUp_record);
		tipUp_record.setOnClickListener(this);

		consumption_record = (TextView) findViewById(R.id.consumption_record);
		consumption_record.setOnClickListener(this);

		tipUp_record.setBackgroundResource(R.drawable.choose_item_lift_shape);
		tipUp_record.setTextColor(this.getResources().getColor(R.color.choose));

		consumption_record
				.setBackgroundResource(R.drawable.choose_item_right_selected_shape);
		consumption_record.setTextColor(this.getResources().getColor(
				R.color.white));
		findViewById(R.id.all_record).setOnClickListener(this);
		
		billing_fl = (FrameLayout) findViewById(R.id.billing_fl);
		billing_empty = (FrameLayout) findViewById(R.id.billing_empty);
		billing_pay_hint = (TextView) findViewById(R.id.billing_pay_hint);
		billing_empty_hint = (ImageView) findViewById(R.id.billing_empty_hint);
	}

	
	
	@Override
	public void refresh() {
		super.refresh();
		if(PersonalActivity.class.equals(parentClass)){
			FLAG = FLAG_CONSUSMPTION;
			Consusmption();
		}else if(AllRecordActivity.class.equals(parentClass)){
			String result = getIntent().getStringExtra("json");
			try {
				JSONObject jsonData = new JSONObject(result);
				JSONArray json = jsonData
						.getJSONArray("billing");
				if (json != null) {
					list_consume_record.setVisibility(View.VISIBLE);
					tionAdapter.setDatas(json);
					list_consume_record.setAdapter(tionAdapter);
					tionAdapter.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void back() {
		try {
			parentClass = (Class<IHaiGoActivity>) Class
					.forName("com.kc.ihaigo.ui.personal.PersonalActivity");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		showTabHost = true;
		super.back();
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			intent.setClass(BillRecordActivity.this,
					PersonalActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, true);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.all_record:
			intent.setClass(BillRecordActivity.this,
					AllRecordActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);

			break;
		case R.id.tipUp_record:
			FLAG = FLAG_PAY;
			
			tipUp_record
					.setBackgroundResource(R.drawable.choose_item_lift_selected_shape);
			tipUp_record.setTextColor(this.getResources().getColor(
					R.color.white));

			consumption_record
					.setBackgroundResource(R.drawable.choose_item_right_shape);
			consumption_record.setTextColor(this.getResources().getColor(
					R.color.choose));
			if(FLAG_PAY.equals(FLAG)){
				billing_fl.setVisibility(View.GONE);
				billing_empty.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.consumption_record:
			FLAG = FLAG_CONSUSMPTION;
			
			tipUp_record
					.setBackgroundResource(R.drawable.choose_item_lift_shape);
			tipUp_record.setTextColor(this.getResources().getColor(
					R.color.choose));

			consumption_record
					.setBackgroundResource(R.drawable.choose_item_right_selected_shape);
			consumption_record.setTextColor(this.getResources().getColor(
					R.color.white));
			if(FLAG_CONSUSMPTION.equals(FLAG)){
				billing_fl.setVisibility(View.VISIBLE);
				billing_empty.setVisibility(View.GONE);
				Consusmption();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 
	  * @Title: billing
	  * @user: helen.yang
	  * @Description: 消费记录
	  * void
	  * @throws
	 */
	private void billing() {
		String url = Constants.BILLING_URL +"1";
		Map<String,Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							try {
								JSONObject jsonObject = new JSONObject(result);
								JSONArray json = jsonObject
										.getJSONArray("billing");
									if (json != null) {
										list_consume_record.setVisibility(View.VISIBLE);
										tionAdapter.setDatas(json);
										list_consume_record.setAdapter(tionAdapter);
										tionAdapter.notifyDataSetChanged();
									}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
							Log.i(TAG, "*****************收到信息" + result);
						}
					}

				}, 1);
	}

}
