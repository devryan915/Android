package com.broadchance.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.net.ParseException;
import android.os.Handler;
import android.os.Message;

import com.broadchance.entity.DownLoadAPPResponse;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.UploadFileResponse;
import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.manager.DataManager;
import com.broadchance.manager.SettingsManager;

public class HttpUtil {
	private final static String TAG = "HttpUtil";

	public static DownLoadAPPResponse downloadFile(String url,
			Map<String, Object> reparams) {
		DownLoadAPPResponse resultResponse = new DownLoadAPPResponse();
		resultResponse.setCode(resultResponse.FAILED);
		try {
			File downFile = (File) reparams.get("downFile");
			Handler handler = (Handler) reparams.get("handler");
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response;
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			long length = entity.getContentLength();
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {
				fileOutputStream = new FileOutputStream(downFile);
				byte[] buf = new byte[2048];
				int readLength = -1;
				int downLength = 0;
				int times = 0;
				String sedStr = null;
				while ((readLength = is.read(buf)) != -1) {
					times++;
					fileOutputStream.write(buf, 0, readLength);
					downLength += readLength;
					NumberFormat nf = NumberFormat.getPercentInstance();
					sedStr = downLength + "/" + length + " ("
							+ nf.format(downLength * 1f / length) + ")";
					// 避免UI线程拥挤
					if (times % 150 == 0) {
						Message msg = new Message();
						msg.obj = sedStr;
						handler.sendMessage(msg);
					}
					if (ConstantConfig.Debug) {
						LogUtil.i(TAG, "downloadFile " + sedStr);
					}
				}
				Message msg = new Message();
				NumberFormat nf = NumberFormat.getPercentInstance();
				msg.obj = downLength + "/" + length + " ("
						+ nf.format(downLength * 1f / length) + ")";
				handler.sendMessage(msg);
				if (length == downLength) {

				}
			}
			fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
			resultResponse.setCode(resultResponse.OK);
			resultResponse.setDownLoadFile(downFile);
			return resultResponse;
		} catch (ClientProtocolException e) {
			resultResponse.setData(e.toString());
			LogUtil.e(TAG, e);
		} catch (IOException e) {
			resultResponse.setData(e.toString());
			LogUtil.e(TAG, e);
		}
		return resultResponse;
	}

	@SuppressWarnings({ "deprecation", "resource" })
	public static StringResponse postData(String url,
			Map<String, Object> reparams) {
		StringResponse strResponse = new StringResponse();
		strResponse.setCode(strResponse.FAILED);
		String errorMsg = "请求失败" + " url:" + url + "\r\n";
		String strResult = null;
		try {
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, "正在请求url:" + url);
			}
			HttpPost post = new HttpPost(url);
			post.addHeader("Authorization", "Bearer "
					+ ConstantConfig.AUTHOR_TOKEN);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			Iterator<Entry<String, Object>> it = reparams.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				params.add(new BasicNameValuePair(entry.getKey(), String
						.valueOf(entry.getValue())));
			}
			HttpClient client = new DefaultHttpClient();
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(res.getEntity(), HTTP.UTF_8);
				strResponse.setCode(strResponse.OK);
				strResponse.setData(strResult);
			} else {
				int statusCode = res.getStatusLine().getStatusCode();
				String resStr = EntityUtils.toString(res.getEntity(),
						HTTP.UTF_8);
				errorMsg = "请求失败" + " url:" + url + "\r\n" + "StatusCode:"
						+ statusCode + "\t" + resStr;
				if (statusCode == 400) {
					JSONObject jsonObject = new JSONObject(resStr);
					errorMsg = jsonObject.getString("error_description");
				} else if (statusCode == 401) {
					JSONObject jsonObject = new JSONObject(resStr);
					// jsonObject = jsonObject.getJSONObject("Error");
					errorMsg = jsonObject.getString("Message");
				}
				strResponse.setData(errorMsg);
			}
		} catch (Exception e) {
			if (ConstantConfig.Debug) {
				LogUtil.e(TAG, errorMsg + e.toString());
			}
			// throw new Exception(errorMsg + e.toString());
			strResponse.setData(errorMsg + "\r\n" + e.toString());
		}
		return strResponse;
	}

	public static String getData(String url) {

		if (ConstantConfig.Debug) {
			LogUtil.d(TAG, "url:" + url);
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
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, "strResult:"
						+ res.getStatusLine().getStatusCode() + "**"
						+ EntityUtils.toString(res.getEntity(), "UTF-8"));
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
		if (ConstantConfig.Debug) {
			LogUtil.d(TAG, "url:" + url);
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
			if (ConstantConfig.Debug) {
				res.getStatusLine().getStatusCode();
				strResult = EntityUtils.toString(res.getEntity(), "UTF-8");
				LogUtil.d(TAG, "strResult:"
						+ res.getStatusLine().getStatusCode() + "**"
						+ strResult);
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

	public static UploadFileResponse uploadRealTimeFile(String url, int port,
			Map<String, Object> reparams) {
		UploadFileResponse response = new UploadFileResponse();
		response.setCode(response.FAILED);
		/**
		 * 是否开启所有网络上传
		 */
		boolean netType = SettingsManager.getInstance().getSettingsNetType();
		// 如果仅限定wifi，检查当前是否Wifi网络，如果不是取消本次上传
		if (!netType && !NetUtil.isWifi()) {
			response.setData("当前网络为移动网络，停止上传");
			return response;
		}
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user == null) {
			response.setData("用户数据不存在");
			return response;
		}
		try {
			File file = (File) reparams.get("uploadFile");
			if (!file.exists()) {
				response.setData("实时上传文件不存在:" + file.getAbsolutePath());
				return response;
			}
			// 创建一个Socket对象，指定服务器端的IP地址和端口号
			Socket socket = new Socket(url, port);
			// Socket socket = new Socket("192.168.1.109", 9999);
			long length = file.length();
			// 使用InputStream读取硬盘上的文件
			FileInputStream inputStream = new FileInputStream(file);
			// 从Socket当中得到OutputStream
			OutputStream os = socket.getOutputStream();
			DataOutputStream outputStream = new DataOutputStream(os);
			outputStream.write("(".getBytes("US-ASCII"));
			outputStream.write(3);
			outputStream.write("NOW".getBytes("US-ASCII"));
			outputStream.write(user.getUserID().length());
			outputStream.write(user.getUserID().getBytes("US-ASCII"));
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss.SSS");
			String dateStr = sdf.format(new java.util.Date());
			outputStream.write(dateStr.length());
			outputStream.write(dateStr.getBytes("US-ASCII"));
			// 服务端是C#使用小端字节
			byte[] dataLength = BleDataUtil.intToByteArrayReverse((int) length);
			outputStream.write(dataLength[0]);
			outputStream.write(dataLength[1]);
			outputStream.write(dataLength[2]);
			outputStream.write(dataLength[3]);
			byte buffer[] = new byte[4 * 1024];
			int readLength = 0;
			// 将InputStream当中的数据取出，并写入到OutputStream当中
			while ((readLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, readLength);
			}
			outputStream.write(")".getBytes("US-ASCII"));
			outputStream.flush();

			InputStream inputStreamSocket = socket.getInputStream();
			readLength = inputStreamSocket.read(buffer);
			char endChar = '1';
			if (readLength != -1 && readLength > 0) {
				byte endbyte = buffer[0];
				endChar = (char) endbyte;
			}
			inputStreamSocket.close();
			outputStream.close();
			socket.close();
			if (endChar == '0') {
				response.setCode(response.OK);
			} else {
				response.setData("实时上传完毕，服务端返回" + endChar);
			}
			return response;
		} catch (UnknownHostException e) {
			response.setData("实时上传失败：" + e.toString());
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			response.setData("实时上传失败：" + e.toString());
		} catch (IOException e) {
			// e.printStackTrace();
			response.setData("实时上传失败：" + e.toString());
		}
		return response;
	}

	/**
	 * 
	 * @Title: uploadImage
	 * @user: ryan.wang
	 * @Description: 上传文件功能
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
	public static UploadFileResponse uploadFile(String url,
			Map<String, Object> reparams) {
		UploadFileResponse response = new UploadFileResponse();
		response.setCode(response.FAILED);
		/**
		 * 是否开启所有网络上传
		 */
		boolean netType = SettingsManager.getInstance().getSettingsNetType();
		// 如果仅限定wifi，检查当前是否Wifi网络，如果不是取消本次上传
		if (!netType && !NetUtil.isWifi()) {
			response.setData("当前网络为移动网络，停止上传");
			return response;
		}
		try {
			String strResult = null;
			File uploadFile = (File) reparams.get("uploadFile");
			if (!uploadFile.exists()) {
				response.setData("上传文件不存在:" + uploadFile.getAbsolutePath());
				return response;
			}
			String desDataJson = (String) reparams.get("desDataJson");
			String userID = (String) reparams.get("userID");
			int upLoadWay = (Integer) reparams.get("upLoadWay");
			HttpPost post = new HttpPost(url);
			post.addHeader("Authorization", "Bearer "
					+ ConstantConfig.AUTHOR_TOKEN);
			// post.addHeader("Token", ConstantConfig.AUTHOR_TOKEN);
			// List<NameValuePair> params = new ArrayList<NameValuePair>();
			// params.add(new BasicNameValuePair("desDataJson", desDataJson));
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			InputStream in = new FileInputStream(uploadFile);
			builder.addPart("file",
					new InputStreamBody(in, uploadFile.getName()));
			ContentBody contentBody = new StringBody(desDataJson);
			builder.addPart("desDataJson", contentBody);
			contentBody = new StringBody(userID);
			builder.addPart("userID", contentBody);
			contentBody = new StringBody(upLoadWay + "");
			builder.addPart("upLoadWay", contentBody);
			// builder.addTextBody("desDataJson", desDataJson);
			// builder.addTextBody("userID", userID);
			post.setEntity(builder.build());
			HttpClient client = new DefaultHttpClient();
			HttpResponse res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(res.getEntity(), "UTF-8");
				if (strResult != null) {
					response.setCode(response.OK);
					return response;
				}
			}
			int statusCode = res.getStatusLine().getStatusCode();
			String restr = EntityUtils.toString(res.getEntity(), "UTF-8");
			response.setData("上传文件服务端返回statusCode：" + statusCode + " " + restr);
			if (ConstantConfig.Debug) {
				LogUtil.d(TAG, "uploadFile strResult:" + statusCode + "**"
						+ restr);
			}
		} catch (ClientProtocolException e) {
			response.setData("上传文件失败：" + e.toString());
		} catch (ParseException e) {
			// e.printStackTrace();
			response.setData("上传文件失败：" + e.toString());
		} catch (IOException e) {
			response.setData("上传文件失败：" + e.toString());
		}
		return response;

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
