package com.langlang.global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.langlang.data.UserData;

public class UserInfo {
	private static UserInfo userInfo;
//	public static boolean ble_state=false;//蓝牙连接状态
	private UserData userData;
	
	private Object lockUserData = new Object();
	
	private int index;

	public static final String PROMPT_NOT_LOGIN = "匿名登录用户无法使用该功能";
	public static final String PROMPT_CANT_SEE_SLEEP_IF_NOT_LOGIN = "匿名登录用户无法查看睡眠数据";
	
	public static final int CMPR_DAT = 0;
	public static final int CMPR_DATX = 1;

	public static final int LOGIN_MODE_ONLINE = 0;
	public static final int LOGIN_MODE_OFFLINE = 1;
	public static final int LOGIN_MODE_ANONYMOUS = -1;
	private int loginMode = LOGIN_MODE_ONLINE; // -1:匿名登录 , 0:在线登录: 1:离线登录
	
	private Object lockLoginMode = new Object();
	
	private Object lockMacAddr = new Object();
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	private UserInfo(){
		userData=new UserData();
	}
	public static UserInfo getIntance(){
		if(userInfo==null){
			userInfo=new UserInfo();
		}
		return userInfo;
	}
	
	public UserData getUserData(){
		synchronized (lockUserData) {
			return userData;
		}
	}
	public boolean setUserData(String user_data){
		synchronized (lockUserData) {
		System.out.println("String user_data:"+user_data);
		if(user_data!=null&&!"".equals(user_data)){
			setIndex(0);
			try {
				JSONArray jsonArray=new JSONArray(user_data);
				JSONObject jsonObject=jsonArray.getJSONObject(0);
				userData.setName(jsonObject.getString("name"));
				userData.setFinal_name(jsonObject.getString("name"));
				userData.setSex(StringToInt(jsonObject.getString("sex")));
				userData.setAge(StringToInt(jsonObject.getString("age")));
				userData.setDate_of_birth(jsonObject.getString("mbirthDay"));
				userData.setHeight(StringToInt(jsonObject.getString("height")));
				userData.setWeight(StringToInt(jsonObject.getString("weight")));
				userData.setProvince(jsonObject.getString("city").equals("")? "上海":jsonObject.getString("city"));
				userData.setProvince_list(jsonObject.getString("provinces"));
				userData.setTelephone(jsonObject.getString("telephoneNo"));
				userData.setMobile_phone(jsonObject.getString("mobile"));
				userData.setDevice_number(jsonObject.getString("equipmentNumber"));
				userData.setAddress(jsonObject.getString("address"));
				userData.setPostcode(jsonObject.getString("postCode"));
				UserInfo.getIntance().getUserData().setElses("remark");
//				userData.setElses(jsonObject.getString("equipmentNumber"));
//				userData.setPosition(Integer.valueOf(jsonObject.getString("")));
				userData.setAccountCode(jsonObject.getString("accountCode"));
				
				userData.setRole(jsonObject.getString("roleName"));
				userData.setString_monitoring_object(jsonObject.getString("tutelageList"));
				userData.setUserRole(jsonObject.getString("roleName"));
				
				userData.setMy_name(jsonObject.getString("userName"));
				userData.setUser_name(jsonObject.getString("userName"));
				userData.setVersion(jsonObject.getString("newVersion"));
				userData.setVersion_mark(jsonObject.getString("versionMark"));
				
				
				String limit_data=jsonObject.getString("alertStepList");
				JSONArray limitaArray=new JSONArray(limit_data);
				for(int i=0;i<limitaArray.length();i++){
					JSONObject limitObject=limitaArray.getJSONObject(i);
					
					if(limitObject.getString("alertType").equals("1")){
						userData.setLimit_heart_up(StringToInt(limitObject.getString("upperLimit")));
						userData.setLimit_heart_dw(StringToInt(limitObject.getString("floorLimit")));
					}
				}
				System.out.println("vlimit_data:"+userData.getLimit_heart_dw()+""+userData.getLimit_heart_up()+"");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				reset();
				System.out.println("action UserInfo setUserData JSONException e[" + e.toString() + "]");
				return false;
			}			
			return true;
		}
		
		return false;
		}
	}
	/**
	 * 初始化单例
	 */
	public void reset() {
		synchronized (lockUserData) {
			userData=new UserData();
			setLoginMode(LOGIN_MODE_ONLINE);
		}
	}
	private int StringToInt(String daString){
		if(daString==null||daString.equals("")){
			return 0;
		}
		return Integer.valueOf(daString);
	}
	
	public static boolean isInDebugMode() {
		return false;
	}
	
	public static boolean isInDemoMode() {
		return true;
	}
	
	// 主要用于控制调试蓝牙
	public static boolean isDebugVersion() {
		return false;
	}
	
	public static int dataCompressType() {
		return CMPR_DATX;
//		return CMPR_DAT;
	}
	public int getLoginMode() {
		synchronized (lockLoginMode) {
			return loginMode;
		}
	}
	public void setLoginMode(int loginMode) {
		synchronized (lockLoginMode) {
			this.loginMode = loginMode;
		}
	}
	public String getMacAddr() {
		synchronized (lockMacAddr) {
			return macAddr;
		}
	}
	public void setMacAddr(String macAddr) {
		synchronized (lockMacAddr) {
			this.macAddr = macAddr;
		}
	}
	private String macAddr = null;
	
}
