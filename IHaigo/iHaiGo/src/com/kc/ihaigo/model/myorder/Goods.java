package com.kc.ihaigo.model.myorder;

import java.util.List;

public class Goods extends BaseModel{

	/**
	  * @Fields serialVersionUID : TODO（商品信息）
	  */
	
	private static final long serialVersionUID = 1L;
	
	public String id;
	public String name;
	public String source;
	public String icon;
	public String color;
	public String size;
	public String currency;
	public String price;
	public String discount;
	public String amount;
	public String total;
	public List<Items>  items;
	public String count;
	

}
