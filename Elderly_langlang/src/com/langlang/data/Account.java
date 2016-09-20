package com.langlang.data;

import java.util.Date;
/**
 * 用户表
 * @author shuaimeng.sun
 */
public class Account {

	private String accountCode;
	private String name;			//姓名
	private String userName;
	private String password;
	private String equipmentNumber; //设备号
	private String roomNumber;		//房间号
	private String mbirthDay;			//出生日期
	private String sex;
	private String weight;			//体重
	private String height;			//身高
	private String sensorPosition;	//传感器位置
	private String email;
	private String weChat;
	private String city;			//省份
	private String postCode;		//邮编
	private String address;			//地址
	private String telephoneNo;		//电话
	private String mobile;			//手机
	private String remark;			//备注
	private String emergencyContact;	
	private String emergencyAddress;
	private String emergencyTel;
	private String relation;
	private Date expirationDate;
	private String deleteFlag;
	private String physicalName;
	private String physicalAddress;
	private String physicalContacts;
	private String physicalMail;
	private String doctorPosition;
	private String hospital;
	private String departments;
	private String hospitalAddress;
	private String hospitalMail;
	private String pubPhone1;
	private String pubPhone2;
	private String pubWeChat;
	private String pubWebAddress;
	private String currentCourse;
	private String cBeginDate;
	private Date cEndDate;
	private String subscribeCourse;
	private Date sBeginDate;
	private Date sEndDate;
	private String createBy;
	private Date createDate;
	private String updateBy;
	private Date updateDate;
	private Integer status;
	private Integer enabled;//是否可用 
	
	
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMbirthDay() {
		return mbirthDay;
	}
	public void setMbirthDay(String mbirthDay) {
		this.mbirthDay = mbirthDay;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getSensorPosition() {
		return sensorPosition;
	}
	public void setSensorPosition(String sensorPosition) {
		this.sensorPosition = sensorPosition;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWeChat() {
		return weChat;
	}
	public void setWeChat(String weChat) {
		this.weChat = weChat;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephoneNo() {
		return telephoneNo;
	}
	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}
	public String getEmergencyContact() {
		return emergencyContact;
	}
	public void setEmergencyContact(String emergencyContact) {
		this.emergencyContact = emergencyContact;
	}
	public String getEmergencyAddress() {
		return emergencyAddress;
	}
	public void setEmergencyAddress(String emergencyAddress) {
		this.emergencyAddress = emergencyAddress;
	}
	public String getEmergencyTel() {
		return emergencyTel;
	}
	public void setEmergencyTel(String emergencyTel) {
		this.emergencyTel = emergencyTel;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public String getPhysicalName() {
		return physicalName;
	}
	public void setPhysicalName(String physicalName) {
		this.physicalName = physicalName;
	}
	public String getPhysicalAddress() {
		return physicalAddress;
	}
	public void setPhysicalAddress(String physicalAddress) {
		this.physicalAddress = physicalAddress;
	}
	public String getPhysicalContacts() {
		return physicalContacts;
	}
	public void setPhysicalContacts(String physicalContacts) {
		this.physicalContacts = physicalContacts;
	}
	public String getPhysicalMail() {
		return physicalMail;
	}
	public void setPhysicalMail(String physicalMail) {
		this.physicalMail = physicalMail;
	}
	public String getDoctorPosition() {
		return doctorPosition;
	}
	public void setDoctorPosition(String doctorPosition) {
		this.doctorPosition = doctorPosition;
	}
	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	public String getDepartments() {
		return departments;
	}
	public void setDepartments(String departments) {
		this.departments = departments;
	}
	public String getHospitalAddress() {
		return hospitalAddress;
	}
	public void setHospitalAddress(String hospitalAddress) {
		this.hospitalAddress = hospitalAddress;
	}
	public String getHospitalMail() {
		return hospitalMail;
	}
	public void setHospitalMail(String hospitalMail) {
		this.hospitalMail = hospitalMail;
	}
	public String getPubPhone1() {
		return pubPhone1;
	}
	public void setPubPhone1(String pubPhone1) {
		this.pubPhone1 = pubPhone1;
	}
	public String getPubPhone2() {
		return pubPhone2;
	}
	public void setPubPhone2(String pubPhone2) {
		this.pubPhone2 = pubPhone2;
	}
	public String getPubWeChat() {
		return pubWeChat;
	}
	public void setPubWeChat(String pubWeChat) {
		this.pubWeChat = pubWeChat;
	}
	public String getPubWebAddress() {
		return pubWebAddress;
	}
	public void setPubWebAddress(String pubWebAddress) {
		this.pubWebAddress = pubWebAddress;
	}
	public String getCurrentCourse() {
		return currentCourse;
	}
	public void setCurrentCourse(String currentCourse) {
		this.currentCourse = currentCourse;
	}
	public String getcBeginDate() {
		return cBeginDate;
	}
	public void setcBeginDate(String cBeginDate) {
		this.cBeginDate = cBeginDate;
	}
	public Date getcEndDate() {
		return cEndDate;
	}
	public void setcEndDate(Date cEndDate) {
		this.cEndDate = cEndDate;
	}
	public String getSubscribeCourse() {
		return subscribeCourse;
	}
	public void setSubscribeCourse(String subscribeCourse) {
		this.subscribeCourse = subscribeCourse;
	}
	public Date getsBeginDate() {
		return sBeginDate;
	}
	public void setsBeginDate(Date sBeginDate) {
		this.sBeginDate = sBeginDate;
	}
	public Date getsEndDate() {
		return sEndDate;
	}
	public void setsEndDate(Date sEndDate) {
		this.sEndDate = sEndDate;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getEquipmentNumber() {
		return equipmentNumber;
	}
	public void setEquipmentNumber(String equipmentNumber) {
		this.equipmentNumber = equipmentNumber;
	}
	public String getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getEnabled() {
		return enabled;
	}
	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
}
