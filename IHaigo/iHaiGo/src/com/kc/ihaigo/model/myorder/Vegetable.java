package com.kc.ihaigo.model.myorder;

public class Vegetable extends BaseModel{

	
	/**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String price;
	private String time;
	private String picture;
	private String servicername="";
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getServicername() {
		return servicername;
	}
	public void setServicername(String servicername) {
		this.servicername = servicername;
	}
	
	
	
	
}
