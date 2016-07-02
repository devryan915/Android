package com.langlang.data;

public class UserData {
	private Object lockUserData = new Object();
	
	private String name; 
	private String final_name; 
	private int sex;
	private int age;
	private int height;
	private int weight;
	private String telephone;
	private String mobile_phone;
	private String date_of_birth;
	private String device_number;
	private String address;
	private String postcode;
	private String elses;
	private String accountCode;
	private String province;//所在省份（天气预报）
	private String province_list;//省份列表
	private int position;
	private String role;
	private int monitoring_object = 0;//
	private String string_monitoring_object;
	private String userRole;
	
	private String user_name;
	private String my_name;
	
	private int login_mode;//登录模式(是否有网络)
	private int limit_heart_up;//心率上限
	private int limit_heart_dw;//心率下限
	private String version;//版本号
	private String version_mark;//判断是否必须更新版本
	private double latitude;//纬度
	private double longitude;//经度
	private String update_GPS_time;//GPS上传时间
	private String temporary_province;//临时省份
	private String device_temperature;//设备温度集
	public String getDevice_temperature() {
		return device_temperature;
	}
	public void setDevice_temperature(String device_temperature) {
		this.device_temperature = device_temperature;
	}
	public String getVersion_mark() {
		return version_mark;
	}
	public void setVersion_mark(String version_mark) {
		this.version_mark = version_mark;
	}
	public String getTemporary_province() {
		return temporary_province;
	}
	public void setTemporary_province(String temporary_province) {
		this.temporary_province = temporary_province;
	}
	public String getProvince_list() {
		return province_list;
	}
	public void setProvince_list(String province_list) {
		this.province_list = province_list;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getUpdate_GPS_time() {
		return update_GPS_time;
	}
	public void setUpdate_GPS_time(String update_GPS_time) {
		this.update_GPS_time = update_GPS_time;
	}
	public String getFinal_name() {
		return final_name;
	}
	public void setFinal_name(String final_name) {
		this.final_name = final_name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getLimit_heart_up() {
		return limit_heart_up;
	}
	public void setLimit_heart_up(int limit_heart_up) {
		this.limit_heart_up = limit_heart_up;
	}
	public int getLimit_heart_dw() {
		return limit_heart_dw;
	}
	public void setLimit_heart_dw(int limit_heart_dw) {
		this.limit_heart_dw = limit_heart_dw;
	}
	public int getLogin_mode() {
		return login_mode;
	}
	public void setLogin_mode(int login_mode) {
		this.login_mode = login_mode;
	}

	public String getMy_name() {
		return my_name;
	}

	public void setMy_name(String my_name) {
		this.my_name = my_name;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getString_monitoring_object() {
		return string_monitoring_object;
	}

	public void setString_monitoring_object(String string_monitoring_object) {
		this.string_monitoring_object = string_monitoring_object;
	}

	public void setMonitoring_object(int monitoring_object) {
		this.monitoring_object = monitoring_object;
	}

	public int getMonitoring_object() {
		return monitoring_object;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getElses() {
		return elses;
	}

	public void setElses(String elses) {
		this.elses = elses;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile_phone() {
		return mobile_phone;
	}

	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}

	public String getDate_of_birth() {
		return date_of_birth;
	}

	public void setDate_of_birth(String date_of_birth) {
		this.date_of_birth = date_of_birth;
	}

	public String getDevice_number() {
		return device_number;
	}

	public void setDevice_number(String device_number) {
		this.device_number = device_number;
	}

	public String getUserRole() {
		synchronized (lockUserData) {
			return userRole;	
		}
	}
	public void setUserRole(String userRole) {
		synchronized (lockUserData) {
			this.userRole = userRole;
		}	
	}
}
