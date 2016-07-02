package com.broadchance.xiaojian.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.broadchance.xiaojian.R;

/**
 * @ClassName: HttpAsyncTask
 * @Description: 异步请求Http,参数为可变长参数，预定义，第一个参数为请求方式，第二个为请求url，第三个为请求参数，第四个为回调函数
 * @author: ryan.wang
 * @date: 2014年7月10日 下午3:57:44
 * 
 */

public class HttpAsyncTask extends AsyncTask<Object, Integer, SoapObject> {
	public interface HttpReqCallBack {
		void deal(SoapObject result);
	}

	public static String URL = "http://192.168.1.39/XiaoyunServices.asmx?wsdl";
	private static String NAMESPACE = "http://www.tiannma.com/app/demo/";
	private HttpReqCallBack callBack;
	private static final String TAG = HttpAsyncTask.class.getSimpleName();
	private static Dialog progressDialog;
	private static TextView dialogContent;

	/**
	 * 
	 * @Title: fetchData
	 * @user: ryan.wang
	 * @Description: 异步获取http数据方法
	 * 
	 * @param type
	 *            HttpAsyncTask.POST HttpAsyncTask.GET HttpAsyncTask.DELETE
	 * @param url
	 *            请求url
	 * @param reqParams
	 *            如果是post方法需要将参数传递过来
	 * @param callBack
	 *            回调方法用来处理返回结果
	 * @param objects
	 *            数组参数，预留参数非必须参数。
	 * 
	 *            数组 第一个参数为整型定义数据缓存时间，如没有则使用系统默认缓存时间 数组;
	 *            第二参数定义为阻塞窗口需要显示的提示性信息resId,如果使用此参数则认为需要使用阻塞;
	 * @throws
	 */
	public static void fetchData(String method_name,
			HashMap<String, Object> propertys, HttpReqCallBack callBack,
			Object... params) {
		new HttpAsyncTask().execute(method_name, propertys, callBack);
		try {
			if (params != null) {
				Context ctx = (Context) params[0];
				String content = params[1].toString();
				progressDialog = showLoadingDialog(ctx, "");
				dialogContent = ((TextView) progressDialog
						.findViewById(R.id.content));
				dialogContent.setText(content);
				progressDialog.show();
			}
		} catch (Exception e) {

		}
	}

	public static Dialog showLoadingDialog(final Context context, String content) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_show_loading, null);
		((TextView) layout.findViewById(R.id.content)).setText(content);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		return dlg;
	}

	@Override
	protected SoapObject doInBackground(Object... params) {
		SoapObject result = null;
		try {
			String METHOD_NAME = params[0].toString();
			HashMap<String, Object> PROPERTYS = (HashMap<String, Object>) params[1];
			this.callBack = (HttpReqCallBack) params[2];
			// namespace,methodname
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			System.out.println("rpc" + request);
			// 参数名，参数值
			if (PROPERTYS != null) {
				Iterator<String> keySets = PROPERTYS.keySet().iterator(); // 获得传进来的HashMap的key值
				while (keySets.hasNext()) {
					String key = keySets.next();
					request.addProperty(key, PROPERTYS.get(key));
				}
			}
			// url
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.debug = true;
			// soap协议必须和服务端一致
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			// soapaction
			String soapAction = NAMESPACE + METHOD_NAME;
			ht.call(soapAction, envelope);
			if (envelope.getResponse() != null) {
				result = (SoapObject) envelope.bodyIn;
			}

		} catch (SoapFault e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(SoapObject result) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.hide();
		}
		super.onPostExecute(result);
		callBack.deal(result);
	}

}
