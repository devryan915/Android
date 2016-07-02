package com.demo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class WebDataGetApi {

	private static final String TAG = "WebDataGetAPI";
	private static final String USER_AGENT = "Mozilla/4.5";

	protected String getRequest(String url) throws Exception {
		return getRequest(url, new DefaultHttpClient(new BasicHttpParams()));
	}

	protected String getRequest(String url, DefaultHttpClient client)
			throws Exception {
		String result = null;
		int statusCode = 0;
		HttpGet getMethod = new HttpGet(url);
		Log.d(TAG, "do the getRequest,url=" + url + "");
		try {
			getMethod.setHeader("User-Agent", USER_AGENT);
			// HttpParams params = new HttpParams();

			// 添加用户密码验证信息
			// client.getCredentialsProvider().setCredentials(
			// new AuthScope(null, -1),
			// new UsernamePasswordCredentials(mUsername, mPassword));

			HttpResponse httpResponse = client.execute(getMethod);
			// statusCode == 200 正常
			statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.d(TAG, "statuscode = " + statusCode);
			// 处理返回的httpResponse信息
			result = retrieveInputStream(httpResponse.getEntity());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			throw new Exception(e);
		} finally {
			getMethod.abort();
		}
		return result;
	}

	/**
	 * 处理httpResponse信息,返回String
	 * 
	 * @param httpEntity
	 * @return String
	 */
	protected String retrieveInputStream(HttpEntity httpEntity) {
		int length = (int) httpEntity.getContentLength();
		if (length < 0)
			length = 10000;
		StringBuffer stringBuffer = new StringBuffer(length);
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					httpEntity.getContent(), HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return stringBuffer.toString();
	}
}
