package com.kc.ihaigo.model.myorder;

public class TransportOrders extends BaseModel {

	/**
	 * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	 */

	private static final long serialVersionUID = 1L;

	public Tgoods goods;
	public String id;
	public String time;
	public String status;
	public Title title;
	public String transport;
	public Info info;
	public String storage; // 入库时间

}
