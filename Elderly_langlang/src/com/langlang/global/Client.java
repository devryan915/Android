package com.langlang.global;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.langlang.data.FormFile;

public class Client {

	private static final String NAMESPACE = "http://testws.vaga.cn/";
//	private static final String NAMESPACE = "http://www.langlangit.com/xinweishi/services/Login.jws";
	public static int service_state;
	
	public static String UPLOAD_URL = "http://www.langlangit.com:8082/PullService/index!list.action";
	private static String URL = "http://www.langlangit.com/llx/services/Login.jws?wsdl";
//	private static String URL = "http://www.langlangit.com/xinweishi/services/Login.jws?wsdl";

//	private static String URL = "http://192.168.0.110:8080/Langlang/services/Login.jws?wsdl";
	
//	private static String URL = "http://192.168.0.200:8080/llx/services/Login.jws?wsdl";
//	public static String UPLOAD_URL = "http://192.168.0.200:8082/PullService/index!list.action";
	
	public static final String ECG_CMD_SERVER_IP = "192.168.0.67";
	public static final int ECG_CMD_SERVER_PORT = 6000;
 
	/**
	 * 获取登录数据
	 * @return
	 */
	public static String getLogin(String userInfo) {
		System.out.println("clint userInfo" + userInfo);
		String METHOD_NAME = "getUserInfo";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);// 带参数的方法调用，若调用无参数的，则无需此句
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			str = result.toString();
			System.out.println(" clint userInfo=========:" + str);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("clint userInfo Exception");
			str = null;
		}
		return str;
	}

	/**
	 * 注册
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getRegister(String userInfo) {
		String METHOD_NAME = "registerUserInfo";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			str = result.toString();
			System.out.println("=========" + str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 修改密码
	 * 
	 * @param userInfo
	 * @return
	 */

	public static String getSetPassword(String username) {
		System.out.println("getSetPassword setposswordactivity:" + username);
		String METHOD_NAME = "updatePwd";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("password_data", username);
			envelope.dotNet = true;
			System.out.println("getSetPassword setposswordactivity:xxl");
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);

			Object result = envelope.getResponse();

			str = result.toString();
			System.out.println("getSetPassword setposswordactivity:" + str);

		} catch (Exception e) {
			e.printStackTrace();
			str = null;
		}
		return str;
	}

	public static String getupdateUserInfo(String userInfo) {
		System.out.println("getupdateUserInfo userInfo" + userInfo);
		String METHOD_NAME = "updateUserInfo";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			str = result.toString();
			System.out.println("getupdateUserInfo userInfo" + str);
		} catch (Exception e) {
			e.printStackTrace();
			str = null;
		}
		return str;
	}

	/**
	 * 获取累计运动模版信息
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getUserExercise(String userInfo) {
		System.out.println("Client getUserExercise1");
		String METHOD_NAME = "getUserExercise";
		String str = "";
		System.out.println("Client getUserExercise2");
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("Client getUserExercise3");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			System.out.println("Client getUserExercise");
			Object result = envelope.getResponse();
			System.out.println("Client getUserExercise result:" + result);
			str = result.toString();
			System.out.println("Client getUserExercise str:" + str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 获取临时运动模版信息
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getUserCalorie(String userInfo) {
		String METHOD_NAME = "getUserCalorie";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("Client getUserCalorie3");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	// 获取跌倒报警
	public static String getsendAlarm(String userInfo) {
		System.out.println("getsendAlarm userInfo" + userInfo);
		String METHOD_NAME = "sendAlarms";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getsendAlarm envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getsendAlarm getAcceptInvite result" + result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return str;
	}

	/**
	 * 获取关注请求列表
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getMessage(String userInfo) {
		System.out.println("getMessage userInfo" + userInfo);
		String METHOD_NAME = "getMessage";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getMessage envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getMessage getAcceptInvite result" + result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 获取上传设备号之后的操作
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getuploadMAC(String userInfo) {
		System.out.println("getuploadMAC userInfo" + userInfo);
		String METHOD_NAME = "getAccountByEquipmentNumber";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getuploadMAC envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getuploadMAC getAcceptInvite result" + result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 获取体检消息通知
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getaccountNotice(String userInfo) {
		System.out.println("getaccountNotice userInfo" + userInfo);
		String METHOD_NAME = "accountNotice";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getaccountNotice envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getaccountNotice getAcceptInvite result"
					+ result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 上传加速度
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String uploadXYZ(String userInfo) {
		System.out.println("uploadXYZ userInfo" + userInfo);
		String METHOD_NAME = "initPost";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("uploadXYZ envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("uploadXYZ getAcceptInvite result" + result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			str = null;
		}
		return str;
	}

	/**
	 * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能: <FORM METHOD=POST
	 * ACTION="http://192.168.1.101:8083/upload/servlet/UploadServlet"
	 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
	 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
	 * type="file" name="zip"/> </FORM>
	 * 
	 * @param path
	 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://
	 *            www.iteye.cn或http://192.168.1.101:8083这样的路径测试)
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static boolean post(String path, Map<String, String> params,
			FormFile[] files) throws Exception {
		final String BOUNDARY = "---------------------------7da2137580612"; // 数据分隔线
		final String endline = "--" + BOUNDARY + "--\r\n";// 数据结束标志
		int fileDataLength = 0;
		for (FormFile uploadFile : files) {// 得到文件类型数据的总长度
			StringBuilder fileExplain = new StringBuilder();
			fileExplain.append("--");
			fileExplain.append(BOUNDARY);
			fileExplain.append("\r\n");
			fileExplain.append("Content-Disposition: form-data;name=\""
					+ uploadFile.getParameterName() + "\";filename=\""
					+ uploadFile.getFilname() + "\"\r\n");
			fileExplain.append("Content-Type: " + uploadFile.getContentType()
					+ "\r\n\r\n");
			fileExplain.append("\r\n");
			fileDataLength += fileExplain.length();
			if (uploadFile.getInStream() != null) {
				fileDataLength += uploadFile.getFile().length();
			} else {
				fileDataLength += uploadFile.getData().length;
			}
		}
		StringBuilder textEntity = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {// 构造文本类型参数的实体数据
			textEntity.append("--");
			textEntity.append(BOUNDARY);
			textEntity.append("\r\n");
			textEntity.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"\r\n\r\n");
			textEntity.append(entry.getValue());
			textEntity.append("\r\n");
		}
		// 计算传输给服务器的实体数据总长度
		int dataLength = textEntity.toString().getBytes().length
				+ fileDataLength + endline.getBytes().length;

		URL url = new URL(path);
		int port = url.getPort() == -1 ? 80 : url.getPort();
		Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
		OutputStream outStream = socket.getOutputStream();
		// 下面完成HTTP请求头的发送
		String requestmethod = "POST " + url.getPath() + " HTTP/1.1\r\n";
		outStream.write(requestmethod.getBytes());
		String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
		outStream.write(accept.getBytes());
		String language = "Accept-Language: zh-CN\r\n";
		outStream.write(language.getBytes());
		String contenttype = "Content-Type: multipart/form-data; boundary="
				+ BOUNDARY + "\r\n";
		outStream.write(contenttype.getBytes());
		String contentlength = "Content-Length: " + dataLength + "\r\n";
		outStream.write(contentlength.getBytes());
		String alive = "Connection: Keep-Alive\r\n";
		outStream.write(alive.getBytes());
		String host = "Host: " + url.getHost() + ":" + port + "\r\n";
		outStream.write(host.getBytes());
		// 写完HTTP请求头后根据HTTP协议再写一个回车换行
		outStream.write("\r\n".getBytes());
		// 把所有文本类型的实体数据发送出来
		outStream.write(textEntity.toString().getBytes());
		// 把所有文件类型的实体数据发送出来
		for (FormFile uploadFile : files) {
			StringBuilder fileEntity = new StringBuilder();
			fileEntity.append("--");
			fileEntity.append(BOUNDARY);
			fileEntity.append("\r\n");
			fileEntity.append("Content-Disposition: form-data;name=\""
					+ uploadFile.getParameterName() + "\";filename=\""
					+ uploadFile.getFilname() + "\"\r\n");
			fileEntity.append("Content-Type: " + uploadFile.getContentType()
					+ "\r\n\r\n");
			outStream.write(fileEntity.toString().getBytes());
			if (uploadFile.getInStream() != null) {
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = uploadFile.getInStream().read(buffer, 0, 1024)) != -1) {
					outStream.write(buffer, 0, len);
				}
				uploadFile.getInStream().close();
			} else {
				outStream.write(uploadFile.getData(), 0,
						uploadFile.getData().length);
			}
			outStream.write("\r\n".getBytes());
		}
		// 下面发送数据结束标志，表示数据已经结束
		outStream.write(endline.getBytes());
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		System.out.println("xdrty" + reader.readLine());
		// if (reader.readLine().indexOf("200") == -1) {//
		// 读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
		// System.out.println("aabcss");
		// return false;
		// }
		outStream.flush();
		outStream.close();
		reader.close();
		socket.close();
		return true;
	}

	/**
	 * 提交数据到服务器
	 * 
	 * @param path
	 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://
	 *            www.itcast.cn或http://192.168.1.10:8080这样的路径测试)
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static boolean post(String path, Map<String, String> params,
			FormFile file) throws Exception {
		return post(path, params, new FormFile[] { file });
	}

	/**
	 * 获取天气信息
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getweatherData(String userInfo) {
		System.out.println("getaccountNotice userInfo" + userInfo);
		String METHOD_NAME = "initWeather";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getaccountNotice envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getaccountNotice getAcceptInvite result"
					+ result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			str = null;

		}
		return str;
	}

	/**
	 * 获取子界面的小知识
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getTips(String userInfo) {
		System.out.println("getTips userInfo" + userInfo);
		String METHOD_NAME = "getTips";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getTips envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getTips getAcceptInvite result" + result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			str = null;
		}
		return str;
	}

	/**
	 * 获取睡眠界面的数据
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getSleepTips(String userInfo) {
		System.out.println("getTips userInfo" + userInfo);
		String METHOD_NAME = "getSleepTips";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getTips envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			// System.out.println("getTips getAcceptInvite result"+result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return str;
	}

	/**
	 * 获取监护对象的数据
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getRealTimeByUserInfo(String userInfo) {
		System.out.println("getRealTimeByUserInfo userInfo" + userInfo);
		String METHOD_NAME = "getRealTimeByUserInfo";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getRealTimeByUserInfo envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getRealTimeByUserInfo getAcceptInvite result"
					+ result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return str;
	}

	/**
	 * 获取姿态界面的数据
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getStepTips(String userInfo) {
		System.out.println("getStepTips userInfo" + userInfo);
		String METHOD_NAME = "getStepTips";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getStepTips envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getStepTips getAcceptInvite result" + result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return str;
	}

	/**
	 * 获取压力界面的数据
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getPressureTips(String userInfo) {
		System.out.println("getPressureTips userInfo" + userInfo);
		String METHOD_NAME = "getPressureTips";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getPressureTips envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getPressureTips getAcceptInvite result"
					+ result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return str;
	}

	/**
	 * 获取心率界面的数据
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getHeartRateTips(String userInfo) {
		System.out.println("getHeartRateTips userInfo" + userInfo);
		String METHOD_NAME = "getHeartRateTips";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getHeartRateTips envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getHeartRateTips getAcceptInvite result"
					+ result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return str;
	}

	/**
	 * 获取点赞和动一动的发送接口
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String sendRequest(String userInfo) {
		System.out.println("sendRequest userInfo" + userInfo);
		String METHOD_NAME = "sendRequest";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("sendRequest envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("sendRequest getAcceptInvite result" + result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return str;
	}

	/**
	 * 获取点赞和动一动的接受接口
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getMessageCount(String userInfo) {
		System.out.println("getMessageCount userInfo" + userInfo);
		String METHOD_NAME = "getMessageCount";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getMessageCount envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getMessageCount getAcceptInvite result"
					+ result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return str;
	}

	/**
	 * 获取点赞和动一动的查看接口
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getNewMessage(String userInfo) {
		System.out.println("getNewMessage userInfo" + userInfo);
		String METHOD_NAME = "getMessage";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			System.out.println("getNewMessage envelope ");
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			System.out.println("getNewMessage getAcceptInvite result" + result);
			str = result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return str;
	}

	/**
	 * 向服务器发送log信息
	 * 
	 * @return
	 */
	public static String logToServer(String log) {
		System.out.println("client logToServer===" + log);
		String METHOD_NAME = "logger";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", log);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			str = result.toString();
			System.out.println("client logToServer=========:" + str);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("client logToServer Exception");
			str = null;
		}
		return str;
	}
	
	/**
	 * 向服务器发送log信息
	 * 
	 * @return
	 */
	public static String getAlarmDetail(String log) {
		System.out.println("client getAlarmDetail===" + log);
		String METHOD_NAME = "getAlarmDetail";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", log);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			str = result.toString();
			System.out.println("client getAlarmDetail=========:" + str);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("client getAlarmDetail Exception");
			str = null;
		}
		return str;
	}
	
	/**
	 * GPS向服务器发送当前位置
	 * 
	 * @return
	 */
	public static String updataGPSLocation(String log) {
		System.out.println("client setGPS===" + log);
		String METHOD_NAME = "setGPS";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", log);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			str = result.toString();
			System.out.println("client setGPS=========:" + str);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("client setGPS Exception");
			str = null;
		}
		return str;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getupdateHeart(String userInfo) {
		System.out.println("client updateHeart===" + userInfo);
		String METHOD_NAME = "updateHeart";
		String str = "";
		try {
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			rpc.addProperty("name", userInfo);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE httpTranstation = new HttpTransportSE(URL);
			httpTranstation.call(NAMESPACE + METHOD_NAME, envelope);
			Object result = envelope.getResponse();
			str = result.toString();
			System.out.println("client updateHeart=========:" + str);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("client updateHeart Exception");
			str = null;
		}
		return str;
	}
}
