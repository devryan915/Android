package com.kc.ihaigo.model.myorder;

import java.util.List;

public class TDetailGoods extends BaseModel{

	/**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 1L;
	
	public String count;
	public String total;
	public List<Tdetailtems> items;

}
