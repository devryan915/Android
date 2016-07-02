package com.kc.ihaigo.util.pay;

import java.net.URLEncoder;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.alipay.android.app.sdk.AliPay;
import com.kc.ihaigo.util.Constants;

public class AliPayUtil {
	public static final String TAG = "AliPayUtil";

	public static final int RQF_PAY = 1;

	public static final int RQF_LOGIN = 2;
	/**
	 * 
	 * @Title: payBill
	 * @user: ryan.wang
	 * @Description: TODO
	 * 
	 * @param groupActivity
	 *            通过当前Activity.getParent();
	 * @param handler
	 *            是mainlooper的Handler ; Handler handler = new Handler() { public
	 *            void handleMessage(android.os.Message msg) { Result result =
	 *            new Result((String) msg.obj);
	 * 
	 *            switch (msg.what) { case RQF_PAY : break; case RQF_LOGIN : {
	 * 
	 *            } break; default : break; } }; };
	 * @param billno
	 *            订单编号
	 * @param subject
	 *            商品名称
	 * @param body
	 *            商品详情
	 * @param price
	 *            商品总价
	 * @throws
	 */
	public static void payBill(final Activity groupActivity,
			final Handler handler, String billno, String subject, String body,
			String price) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Constants.ALIPAY_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(billno);
		sb.append("\"&subject=\"");
		sb.append(subject);
		sb.append("\"&body=\"");
		sb.append(body);
		sb.append("\"&total_fee=\"");
		sb.append(price);
		sb.append("\"&notify_url=\"");
		// 网址需要做URL编码
		sb.append(URLEncoder.encode(Constants.ALIPAY_NOTIFY_URL));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode(Constants.ALIPAY_RETURN_URL));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Constants.ALIPAY__SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"" + Constants.ALIPAY_TIMEOUT);
		sb.append("\"");
		try {
			String info = sb.toString();
			String sign = Rsa.sign(info, Constants.ALIPAY_PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&sign_type=\"RSA\"";
			final String orderInfo = info;
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(groupActivity, handler);
					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);
					String result = alipay.pay(orderInfo);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// private void doLogin() {
	// final String orderInfo = getUserInfo();
	// new Thread() {
	// public void run() {
	// String result = new AliPay(AliPayUtil.this, mHandler)
	// .pay(orderInfo);
	//
	// Log.i(TAG, "result = " + result);
	// Message msg = new Message();
	// msg.what = RQF_LOGIN;
	// msg.obj = result;
	// mHandler.sendMessage(msg);
	// }
	// }.start();
	// }

	// private String getUserInfo() {
	// String userId = mUserId.getText().toString();
	// return trustLogin(Keys.DEFAULT_PARTNER, userId);
	//
	// }
	//
	// private String trustLogin(String partnerId, String appUserId) {
	// StringBuilder sb = new StringBuilder();
	// sb.append("app_name=\"mc\"&biz_type=\"trust_login\"&partner=\"");
	// sb.append(partnerId);
	// Log.d("TAG", "UserID = " + appUserId);
	// if (!TextUtils.isEmpty(appUserId)) {
	// appUserId = appUserId.replace("\"", "");
	// sb.append("\"&app_id=\"");
	// sb.append(appUserId);
	// }
	// sb.append("\"");
	//
	// String info = sb.toString();
	//
	// // 请求信息签名
	// String sign = Rsa.sign(info, Keys.PRIVATE);
	// try {
	// sign = URLEncoder.encode(sign, "UTF-8");
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	// info += "&sign=\"" + sign + "\"&" + getSignType();
	//
	// return info;
	// }

	// Handler handler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// Result result = new Result((String) msg.obj);
	//
	// switch (msg.what) {
	// case RQF_PAY :
	// break;
	// case RQF_LOGIN : {
	//
	// }
	// break;
	// default :
	// break;
	// }
	// };
	// };

	public static class Product {
		public String subject;
		public String body;
		public String price;
	}

	public static Product[] sProducts;
}