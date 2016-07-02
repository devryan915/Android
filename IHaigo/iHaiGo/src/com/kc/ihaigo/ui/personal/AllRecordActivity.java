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

import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.RecordConsusmptionAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

public class AllRecordActivity extends IHaiGoActivity implements
		OnClickListener {
	
	private String TAG = "AllRecordActivity";
	/**
	 * 消费记录适配器
	 */
	private RecordConsusmptionAdapter tionAdapter;
	
	private String page = "1";
	private String pagesize = "5";
	private String type ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_record);
		initTitle();
	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.personal_bill_all_mounth).setOnClickListener(this);
		findViewById(R.id.personal_bill_one_mounth).setOnClickListener(this);
		findViewById(R.id.personal_bill_three_mounth).setOnClickListener(this);
		findViewById(R.id.personal_bill_six_mounth).setOnClickListener(this);

	}

	
	
	@Override
	public void refresh() {
		super.refresh();
		if(BillRecordActivity.class.equals(parentClass)){
			tionAdapter = new RecordConsusmptionAdapter(AllRecordActivity.this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			Intent intent = new Intent(AllRecordActivity.this,
					BillRecordActivity.class);
			intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
			PersonalGroupActivity.group.startiHaiGoActivity(intent);
			break;
		case R.id.personal_bill_all_mounth:
			billingData();
			break;
		case R.id.personal_bill_one_mounth:
			type = "1";
			billing();
			break;
		case R.id.personal_bill_three_mounth:
			type = "2";
			billing();
			break;
		case R.id.personal_bill_six_mounth:
			type = "3";
			billing();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 
	  * @Title: billing
	  * @user: helen.yang
	  * @Description: 按月份消费记录
	  * void
	  * @throws
	 */
	private void billing() {
		String url = Constants.BILLING_URL +"1";
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("type", type);
		map.put("btype", "2");
		map.put("page", page);
		map.put("pagesize", pagesize);
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
//							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
//							try {
//								JSONObject jsonObject = new JSONObject(result);
//								JSONArray json = jsonObject
//										.getJSONArray("billing");
//									if (json != null) {
//										tionAdapter.setDatas(json);
//										tionAdapter.notifyDataSetChanged();
//									}
//
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
							intent.setClass(AllRecordActivity.this, BillRecordActivity.class);
							intent.putExtra("json", result);
							intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
							intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
							PersonalGroupActivity.group.startiHaiGoActivity(intent);

						} else {
							Log.i(TAG, "*****************收到信息" + result);
						}
					}

				}, 1);
	}

	
	/**
	 * 
	  * @Title: billing
	  * @user: helen.yang
	  * @Description: 消费记录
	  * void
	  * @throws
	 */
	private void billingData() {
		String url = Constants.BILLING_URL +"1";
		Map<String,Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						Intent intent = new Intent();
						if (!TextUtils.isEmpty(result)) {
							Log.i(TAG, "+++++++++++++++++++收到信息" + result);
							intent.setClass(AllRecordActivity.this, BillRecordActivity.class);
							intent.putExtra("json", result);
							intent.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
							intent.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
							PersonalGroupActivity.group.startiHaiGoActivity(intent);
						} else {
							Log.i(TAG, "*****************收到信息" + result);
						}
					}

				}, 1);
	}

}
