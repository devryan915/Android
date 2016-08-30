package com.broadchance.entity.serverentity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author ryan.wang
 * 
 */
public class ServerResponse {
	public ServerResponse() {
	}

	private String result;
	private String errid;
	private String errmsg;
	private String outdata;

	public boolean isOK() {
		return "0".equals(result);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getErrid() {
		return errid;
	}

	public void setErrid(String errid) {
		this.errid = errid;
	}

	public String getErrmsg() {
		// return new String(errmsg.getBytes(Charset.forName("US-ASCII")),
		// Charset.forName("GBK"));
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public JSONObject getDATA() {
		JSONObject data = null;
		if (outdata != null && outdata.trim().length() > 0) {
			try {
				data = new JSONObject(outdata);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public String getOutdata() {
		return outdata;
	}

	public void setOutdata(String outdata) {
		this.outdata = outdata;
	}

}
