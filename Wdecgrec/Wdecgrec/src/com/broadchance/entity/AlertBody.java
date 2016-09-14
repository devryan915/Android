package com.broadchance.entity;

import org.json.JSONObject;

import com.broadchance.wdecgrec.alert.AlertType;

public class AlertBody {
	/**
	 * 自增id
	 */
	private Integer _id;
	/**
	 * 用户账号
	 */
	private String userid;
	/**
	 * 用户业务订单
	 */
	private String orderno;
	/**
	 * 预警id(类型)
	 */
	private AlertType id;
	/**
	 * 预警状态1触发0取消
	 */
	private Integer state;
	/**
	 * 预警时间yyyyMMdd HH:mm:ss.SSS
	 */
	private String time;
	/**
	 * 预警内容
	 */
	private JSONObject value;
	/**
	 * 记录时间
	 */
	private String creattime;

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public AlertType getId() {
		return id;
	}

	public void setId(AlertType id) {
		this.id = id;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public JSONObject getValue() {
		return value;
	}

	public void setValue(JSONObject value) {
		this.value = value;
	}

	public String getCreattime() {
		return creattime;
	}

	public void setCreattime(String creattime) {
		this.creattime = creattime;
	}

}
