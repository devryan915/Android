package com.kc.ihaigo.model.myorder;

import java.util.List;

public class OrdersInfo extends BaseModel {

	/**
	 * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	 */

	private static final long serialVersionUID = 1L;

	// public List<Orderinfo> Orderinfo;

	public List<Orders> orders;

	public List<Orders> getOrders() {
		return orders;
	}

	public void setOrders(List<Orders> orders) {
		this.orders = orders;
	}

}
