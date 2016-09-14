package com.broadchance.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.broadchance.entity.ResponseCodeMsg;
import com.broadchance.entity.Skin;
import com.broadchance.wdecgrec.R;

public class ResponseCodeMsgManager {
	private static ResponseCodeMsgManager Instance = null;
	private Context ctx;
	private Map<String, String> responseCodeMsgConfig;

	public synchronized static ResponseCodeMsgManager getInstance() {
		if (Instance == null)
			Instance = new ResponseCodeMsgManager(AppApplication.Instance);
		return Instance;
	}

	private ResponseCodeMsgManager(Context context) {
		this.ctx = context;
		init();
	}

	/**
	 * 根据Response Code返回提示信息
	 * 
	 * @param code
	 * @return
	 */
	// public String getMsg(String code) {
	// String msg = "";
	// if (responseCodeMsgConfig.containsKey(code)) {
	// msg = responseCodeMsgConfig.get(code);
	// }
	// return msg;
	// }

	private void init() {
		Resources r = this.ctx.getResources();
		XmlResourceParser xrp = r.getXml(R.xml.responsecode_config);
		try {
			ResponseCodeMsg codemsg = null;
			String code;
			String msg;
			responseCodeMsgConfig = new HashMap<String, String>();
			// 当没有达到xml的逻辑结束终点
			// getEventType方法返回读取xml当前的事件
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String name = xrp.getName();
					if (name.equals("ResponseCode")) // 查找符合条件的
					{
						codemsg = new ResponseCodeMsg();
						code = xrp.getAttributeValue(null, "Code");
						msg = xrp.getAttributeValue(null, "Msg");
						codemsg.setCode(code);
						codemsg.setMsg(msg);
						responseCodeMsgConfig.put(code, msg);
					}
				}// 当读取到xml节点是一个元素的尾标记时
				else if (xrp.getEventType() == XmlPullParser.END_TAG) {
					// 控制台输出xml节点结束
					System.out.println(xrp.getName() + "---End！");
				} // 当读取到xml节点是文本时
				else if (xrp.getEventType() == XmlPullParser.TEXT) {
					// 输出文本
					System.out.println(xrp.getText() + "\n");
				}
				xrp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// try {
		// InputStream inputStream = this.ctx.getAssets().open(
		// "ResponseCodeMsg.json");
		// byte[] buffer = new byte[1024];
		// int readLength = -1;
		// StringBuffer stringBuffer = new StringBuffer();
		// while ((readLength = inputStream.read(buffer)) != -1) {
		// stringBuffer.append(new String(buffer, 0, readLength));
		// }
		// List<ResponseCodeMsg> responseCodeMsg = JSON.parseObject(
		// stringBuffer.toString(),
		// new TypeReference<List<ResponseCodeMsg>>() {
		// });
		// if (responseCodeMsg != null) {
		// responseCodeMsgConfig = new HashMap<String, String>();
		// for (ResponseCodeMsg msg : responseCodeMsg) {
		// responseCodeMsgConfig.put(msg.getValue(), msg.getMsg());
		// }
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}
}
