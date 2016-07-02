package com.kc.ihaigo.ui.shipping;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.IHaiGoActivity;
import com.kc.ihaigo.IHaiGoGroupActivity;
import com.kc.ihaigo.IHaiGoMainActivity;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.PersonalCollectionActivity;
import com.kc.ihaigo.ui.personal.PersonalGroupActivity;
import com.kc.ihaigo.ui.personal.PersonalLogisticsActivity;
import com.kc.ihaigo.ui.shipping.adapter.ShippingItemAdapter;
import com.kc.ihaigo.util.Constants;
import com.kc.ihaigo.util.DialogUtil;
import com.kc.ihaigo.util.HttpAsyncTask;
import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;
import com.kc.ihaigo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class WayBillEditorActivity extends IHaiGoActivity {
	/**
	 * 编辑运单
	 * 
	 * @author Thinkpad
	 * 
	 */
	private String id;
	private String noteString2;
	private String orderNameString;
	private ShippingItemAdapter adapter;
	private ProgressDialog progressDialog;
	/**
	 * 显示图片
	 */
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	/**
	 * 包裹内容
	 */
	private EditText goods_content;
	/**
	 * 备注
	 */
	private EditText goods_note;
	/**
	 * 隐藏运单
	 */
	private TextView goodsEditor;
	private Class<IHaiGoActivity> lparentClass;

	private ImageView note_clcer;
	private ImageView content_clcer;
	private IHaiGoGroupActivity lparentGroupActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		Image();
		initTitle();

	}

	private void initTitle() {
		findViewById(R.id.title_left).setOnClickListener(this);
		findViewById(R.id.title_right).setOnClickListener(this);
		goods_content = (EditText) findViewById(R.id.goods_content);
		goods_note = (EditText) findViewById(R.id.goods_note);
		goodsEditor = (TextView) findViewById(R.id.goodsEditor);
		goodsEditor.setOnClickListener(this);
		note_clcer = (ImageView) findViewById(R.id.note_clcer);
		content_clcer = (ImageView) findViewById(R.id.content_clcer);
		content_clcer.setOnClickListener(this);
		note_clcer.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// case R.id.title_left:
		// if (WayBillInfoActivity.class.equals(parentClass)) {
		// Intent in = new Intent(WayBillEditorActivity.this,
		// WayBillInfoActivity.class);
		// in.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
		// in.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		// ShippingGroupActiviy.group.startiHaiGoActivity(in);
		//
		// } else if (PersonalLogisticsActivity.class.equals(parentClass)) {
		// Intent in = new Intent(WayBillEditorActivity.this,
		// PersonalLogisticsActivity.class);
		// in.putExtra(IHaiGoMainActivity.FLAG_REFRESHACTIVITY, true);
		// in.putExtra(IHaiGoMainActivity.FLAG_DISPLAYTABHOST, false);
		// PersonalGroupActivity.group.startiHaiGoActivity(in);
		// }
		//
		// break;
		case R.id.title_right:
			final String cont = goods_content.getText().toString().trim();
			final String rem = goods_note.getText().toString().trim();

			if (TextUtils.isEmpty(cont)) {
				Toast.makeText(getApplication(), "请输入内容", Toast.LENGTH_SHORT)
						.show();

			} else if (TextUtils.isEmpty(rem)) {
				Toast.makeText(getApplication(), "请输入备注", Toast.LENGTH_SHORT)
						.show();

			} else if (!rem.equals(noteString2)
					|| !cont.equals(orderNameString)) {
				selectWay(id, cont, rem);
			}
			break;
		case R.id.goodsEditor:
			final String content = goods_content.getText().toString().trim();
			final String remark = goods_note.getText().toString().trim();
			DialogUtil.showNoDelete(WayBillEditorActivity.this.getParent(),
					new BackCall() {

						@Override
						public void deal(int whichButton, Object... obj) {
							switch (whichButton) {

							case R.id.choose_ok:
								((Dialog) obj[0]).dismiss();

								break;
							case R.id.warning:
								if (!TextUtils.isEmpty(id)) {
									setWay(id, content, remark);
								}

								((Dialog) obj[0]).dismiss();

							default:
								break;
							}
						}
					}, null);
			break;
		case R.id.content_clcer:
			goods_content.setText("");
			break;
		case R.id.note_clcer:
			goods_note.setText("");
			break;
		default:
			break;
		}

	}

	/**
	 * 修改保存
	 */
	private void selectWay(String id, String content, String remark) {

		String url = Constants.WAY_SELECT + id;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", Constants.USER_ID);
		map.put("hide", "1");// 是否隐藏 （当需要隐藏是，传值为2，否则为1或者不传）
		map.put("content", content);// 包裹内容（当不是隐藏时，该字段必输）
		map.put("remark", remark);// 备注（当不是隐藏时，该字段必输）

		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {

						try {

							if (!TextUtils.isEmpty(result)) {
								JSONObject jsonObject = new JSONObject(result);
								int code = jsonObject.getInt("code");
								if (code == 1) {
									// if (WayBillInfoActivity.class
									// .equals(parentClass)) {
									Intent in = new Intent(
											WayBillEditorActivity.this,
											WayBillInfoActivity.class);
									in.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									in.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											false);
									lparentGroupActivity
											.startiHaiGoActivity(in);
									// ShippingGroupActiviy.group
									// .startiHaiGoActivity(in);

								} else {
									Toast.makeText(getApplication(),
											"网络异常,请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}
							} else {
								Toast.makeText(WayBillEditorActivity.this,
										getString(R.string.connection_time),
										Toast.LENGTH_LONG).show();
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}, 0, R.string.loading);

	}

	/**
	 * 隐藏
	 * 
	 * @param contentId
	 */
	private void setWay(String id, String content, String remark) {
		String url = Constants.WAY_SELECT + id;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", Constants.USER_ID);
		map.put("hide", "2");// 是否隐藏 （当需要隐藏是，传值为2，否则为1或者不传）
		map.put("content", content);// 包裹内容（当不是隐藏时，该字段必输）
		map.put("remark", remark);// 备注（当不是隐藏时，该字段必输）

		HttpAsyncTask.fetchData(HttpAsyncTask.POST, url, map,
				new HttpReqCallBack() {

					@Override
					public void deal(String result) {

						try {

							if (!TextUtils.isEmpty(result)) {
								JSONObject jsonObject = new JSONObject(result);
								int code = jsonObject.getInt("code");
								if (code == 1) {
									// if (WayBillInfoActivity.class
									// .equals(parentClass)) {
									Intent in = new Intent(
											WayBillEditorActivity.this,
											ShippingActivity.class);
									in.putExtra(
											IHaiGoMainActivity.FLAG_REFRESHACTIVITY,
											true);
									in.putExtra(
											IHaiGoMainActivity.FLAG_DISPLAYTABHOST,
											true);
									lparentGroupActivity
											.startiHaiGoActivity(in);
									// ShippingGroupActiviy.group
									// .startiHaiGoActivity(in);

									// }
								} else {
									Toast.makeText(getApplication(),
											"网络异常,请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}
							} else {
								Toast.makeText(WayBillEditorActivity.this,
										getString(R.string.connection_time),
										Toast.LENGTH_LONG).show();
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}, 0, R.string.loading);

	}

	private void Image() {
		imageLoader = ImageLoader.getInstance();
		options = Utils.getDefaultImageOptions();
		animateFirstListener = Utils.getDefaultAnimateListener();
	}

	@Override
	public void refresh() {
		super.refresh();
		lparentGroupActivity = parentGroupActivity;
		Bundle resParams = getIntent().getExtras();
		// if (PersonalCollectionActivity.class.equals(parentClass)) {
		lparentClass = parentClass;

		id = resParams.getString("id");
		orderNameString = resParams.getString("orderNameString");
		noteString2 = resParams.getString("noteString2");

		// }

		// if (WayBillInfoActivity.class.equals(parentClass)) {
		// lparentClass = parentClass;
		// resParams = getIntent().getExtras();
		// id = resParams.getString("id");
		// orderNameString = resParams.getString("orderNameString");
		// noteString2 = resParams.getString("noteString2");
		//
		// }
		goods_content.setText(orderNameString);
		goods_note.setText(noteString2);

	}

	// @Override
	// protected void back() {
	//
	// lparentClass = parentClass;
	// if (PersonalCollectionActivity.class.equals(lparentClass)) {
	// refreshActivity = true;
	// showTabHost = false;
	// } else if (WayBillInfoActivity.class.equals(lparentClass)) {
	// refreshActivity = true;
	// showTabHost = false;
	// }
	//
	// super.back();
	// }

}
