package com.kc.ihaigo.model.myorder;

import java.util.List;


public class Tgoods extends BaseModel{

	/**
	  * @Fields serialVersionUID : TODO（//转运订单----未完成订单才有该字段	）
	  */
	
	private static final long serialVersionUID = 1L;

	public  String count;
	public String total;
	
	public List<Items> items;

	public List<Items> getItems() {
		return items;
	}

	public void setItems(List<Items> items) {
		this.items = items;
	}
	
	
}
