package com.kc.ihaigo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/**
 * @ClassName: HttpUtil
 * @Description: 通用工具类
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:04:20
 * 
 */
public class HttpUtil {
	private final static String TAG = "HttpUtil";
	public static String postData(String url, Map<String, Object> reparams,
			HttpReqCallBack backCall) {
		if (Constants.Debug) {
			Log.d(TAG, "url:" + url);
		}
		String strResult = null;

		HttpPost post = new HttpPost(url);
		post.addHeader("Token", "");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Iterator<Entry<String, Object>> it = reparams.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			params.add(new BasicNameValuePair(entry.getKey(), String
					.valueOf(entry.getValue())));
		}
		try {
			HttpClient client = new DefaultHttpClient();
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(res.getEntity(), HTTP.UTF_8);
			} else if (Constants.Debug) {
				Log.e(TAG,
						"strResult:"
								+ res.getStatusLine().getStatusCode()
								+ "**"
								+ EntityUtils.toString(res.getEntity(),
										HTTP.UTF_8));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return strResult;
		}
	}
	/**
	 * 
	 * @Title: postData
	 * @user: ryan.wang
	 * @Description: 通过post方式请求服务端数据
	 * 
	 * @param methodName
	 *            借口名称
	 * @param reparams
	 *            以键值对的传入提交参数
	 * @return String 返回服务端数据
	 * @throws
	 */
	public static String postData(String url, Map<String, Object> reparams) {
		if (Constants.Debug) {
			Log.d(TAG, "url:" + url);
		}
		String strResult = null;

		HttpPost post = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Iterator<Entry<String, Object>> it = reparams.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			params.add(new BasicNameValuePair(entry.getKey(), String
					.valueOf(entry.getValue())));
		}
		try {
			HttpClient client = new DefaultHttpClient();
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(res.getEntity(), HTTP.UTF_8);
			} else if (Constants.Debug) {
				Log.e(TAG,
						"strResult:"
								+ res.getStatusLine().getStatusCode()
								+ "**"
								+ EntityUtils.toString(res.getEntity(),
										HTTP.UTF_8));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return strResult;
		}
	}

	public static String getData(String url) {

		if (Constants.Debug) {
			Log.d(TAG, "url:" + url);
		}
		String strResult = null;
		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse res = client.execute(get);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(res.getEntity(), "UTF-8");
				if (strResult != null) {
					return strResult;
				}
			}
			if (Constants.Debug) {
				Log.d(TAG, "strResult:" + res.getStatusLine().getStatusCode()
						+ "**" + EntityUtils.toString(res.getEntity(), "UTF-8"));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strResult;

	}

	public static String deleteData(String url) {
		if (Constants.Debug) {
			Log.d(TAG, "url:" + url);
		}
		String strResult = null;
		HttpDelete delete = new HttpDelete(url);
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse res = client.execute(delete);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(res.getEntity(), "UTF-8");
				if (strResult != null) {
					return strResult;
				}
			}
			if (Constants.Debug) {
				res.getStatusLine().getStatusCode();
				strResult = EntityUtils.toString(res.getEntity(), "UTF-8");
				Log.d(TAG, "strResult:" + res.getStatusLine().getStatusCode()
						+ "**" + strResult);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return strResult;
	}

	/**
	 * 
	 * @Title: uploadImage
	 * @user: ryan.wang
	 * @Description: 图片上传功能
	 * 
	 * @param url
	 * @param reparams
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 *             String
	 * @throws
	 */
	public static String uploadImage(String url, Map<String, Object> reparams) {
		String strResult = null;
		String uploadFileName = "";
		if (reparams != null && reparams.size() > 0) {
			Set<String> params = reparams.keySet();
			for (String key : params) {
				uploadFileName = reparams.get(key).toString();
			}
		}
		File uploadFile = new File(uploadFileName);
		if (!uploadFile.exists())
			return null;
		HttpPost post = new HttpPost(url);
		try {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			// File file = new File("C://Users/john/Desktop/best_buy.jpg");
			InputStream in = new FileInputStream(uploadFile);
			builder.addPart("image",
					new InputStreamBody(in, uploadFile.getName()));
			post.setEntity(builder.build());
			HttpClient client = new DefaultHttpClient();
			HttpResponse res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(res.getEntity(), "UTF-8");
				if (strResult != null) {
					return strResult;
				}
			}
			if (Constants.Debug) {
				Log.d(TAG, "strResult:" + res.getStatusLine().getStatusCode()
						+ "**" + EntityUtils.toString(res.getEntity(), "UTF-8"));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return strResult;
		}

		// String end = "\r\n";
		// String twoHyphens = "--";
		// String boundary = "******";
		// try {
		// URL url = new URL(uploadUrl);
		// HttpURLConnection httpURLConnection = (HttpURLConnection) url
		// .openConnection();// http连接
		// // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
		// // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
		// httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
		// // 允许输入输出流
		// httpURLConnection.setDoInput(true);
		// httpURLConnection.setDoOutput(true);
		// httpURLConnection.setUseCaches(false);
		// // 使用POST方法
		// httpURLConnection.setRequestMethod("POST");
		// httpURLConnection.setRequestProperty("Connection", "Keep-Alive");//
		// 保持一直连接
		// httpURLConnection.setRequestProperty("Charset", "UTF-8");// 编码
		// httpURLConnection.setRequestProperty("Content-Type",
		// "multipart/form-data;boundary=" + boundary);// POST传递过去的编码
		//
		// DataOutputStream dos = new DataOutputStream(
		// httpURLConnection.getOutputStream());// 输出流
		// dos.writeBytes(twoHyphens + boundary + end);
		// dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\""
		// + uploadFile.getName().substring(
		// uploadFile.getName().lastIndexOf("/") + 1)
		// + "\""
		// + end);
		// dos.writeBytes(end);
		//
		// FileInputStream fis = new FileInputStream(uploadFile);// 文件输入流，写入到内存中
		// byte[] buffer = new byte[8192]; // 8k
		// int count = 0;
		// // 读取文件
		// while ((count = fis.read(buffer)) != -1) {
		// dos.write(buffer, 0, count);
		// }
		// fis.close();
		//
		// dos.writeBytes(end);
		// dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
		// dos.flush();
		//
		// InputStream is = httpURLConnection.getInputStream();//
		// http输入，即得到返回的结果
		// InputStreamReader isr = new InputStreamReader(is, "utf-8");
		// BufferedReader br = new BufferedReader(isr);
		// String result = br.readLine();
		// dos.close();
		// is.close();
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

}