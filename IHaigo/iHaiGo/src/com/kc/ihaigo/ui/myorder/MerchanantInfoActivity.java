package com.kc.ihaigo.ui.myorder;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.myorder.SubmitedOrderDetailActivity.OrderDetailBuyOtherAdapter;
import com.kc.ihaigo.ui.myorder.SubmitedOrderDetailActivity.OrderDetailBuyOtherAdapter.ViewHolder;
import com.kc.ihaigo.ui.selfwidget.WrapListView;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.magus.MagusTools;
import com.tencent.mm.sdk.platformtools.Log;

/***
 * @deprecated 商家信息
 * @author Lijie
 * 
 * */
public class MerchanantInfoActivity extends Activity implements OnClickListener {
	private WrapListView lv_purdetail_comments;
	private String name;
	private String headIcon;
	private String charge;
	private String logistics;
	private String service;
	private String introduce;
	private String statement;
	private ImageView puragent_head;
	private TextView merchant_name, puragent_feeval
	, puragent_shippingval,
			puragent_serviceval, intro_info, promise_info;
	JSONArray comments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_info);
		initView();
		ininData();
	}

	private void ininData() {
		// TODO Auto-generated method stub
		// "http://api.ihaigo.com/ihaigo/orders/?user=1";Constants.USER_ID =9
		String url = "http://192.168.1.3:8080/agents/findAgents?id=1";
		Map<String, Object> map = new HashMap<String, Object>();
		HttpAsyncTask.fetchData(HttpAsyncTask.GET, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {
						// TODO Auto-generated method stub
						if (!TextUtils.isEmpty(result)) {
							Log.e("返回数据-----", result);
							try {
								JSONObject jsobj = new JSONObject(result);

								name = jsobj.getString("agentsName");
								headIcon = jsobj.getString("headPortrait");
								charge = jsobj.getString("charge");
								logistics = jsobj.getString("logistics");
								service = jsobj.getString("service");
								introduce = jsobj.getString("introduce");
								statement = jsobj.getString("statement");
								statement = jsobj.getString("statement");
								comments = jsobj.getJSONArray("comments");

								setData();
								lv_purdetail_comments
										.setAdapter(new CommentsAdapter());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}

					private void setData() {
						// TODO Auto-generated method stub
						merchant_name.setText(name);
						puragent_feeval.setText(charge);
						puragent_shippingval.setText(logistics);
						puragent_serviceval.setText(service);
						intro_info.setText(introduce);
						promise_info.setText(statement);
						MagusTools.setImageView(headIcon, puragent_head,
								R.drawable.ic_launcher);

					}

				}, 1, R.string.loading);

	}

	class CommentsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return comments.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return comments.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parents) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater
						.from(MerchanantInfoActivity.this)
						.inflate(R.layout.listview_purdetailcomments_item, null);
				holder = new ViewHolder();
				holder.user_name = (TextView) convertView
						.findViewById(R.id.user_name);
				holder.puragent_feeval = (TextView) convertView
						.findViewById(R.id.puragent_feeval);
				holder.puragent_shippingval = (TextView) convertView
						.findViewById(R.id.puragent_shippingval);
				holder.puragent_serviceval = (TextView) convertView
						.findViewById(R.id.puragent_serviceval);
				holder.purdetail_comments_contents = (TextView) convertView
						.findViewById(R.id.purdetail_comments_contents);
				holder.puragentdetail_comments_time = (TextView) convertView
						.findViewById(R.id.puragentdetail_comments_time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				holder.user_name.setText(comments.getJSONObject(position)
						.getString("nickName"));
				holder.puragent_feeval.setText(comments.getJSONObject(position)
						.getString("charge"));
				holder.puragent_shippingval.setText(comments.getJSONObject(
						position).getString("logistics"));
				holder.puragent_serviceval.setText(comments.getJSONObject(
						position).getString("service"));
				holder.purdetail_comments_contents.setText(comments
						.getJSONObject(position).getString("introduce"));
				holder.puragentdetail_comments_time.setText(comments
						.getJSONObject(position).getString("createTime"));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}

		class ViewHolder {
			TextView user_name, puragent_feeval, puragent_shippingval,
					puragent_serviceval, purdetail_comments_contents,
					puragentdetail_comments_time;
			ImageView goodsimage;
		}

	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.title_left).setOnClickListener(this);
		puragent_head = (ImageView) findViewById(R.id.puragent_head);
		merchant_name = (TextView) findViewById(R.id.merchant_name);
		puragent_feeval = (TextView) findViewById(R.id.puragent_feeval);
		puragent_shippingval = (TextView) findViewById(R.id.puragent_shippingval);
		puragent_serviceval = (TextView) findViewById(R.id.puragent_serviceval);
		intro_info = (TextView) findViewById(R.id.intro_info);
		promise_info = (TextView) findViewById(R.id.promise_info);
		lv_purdetail_comments = (WrapListView) findViewById(R.id.purdetail_comments);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();
			break;

		default:

			break;
		}

	}

}
