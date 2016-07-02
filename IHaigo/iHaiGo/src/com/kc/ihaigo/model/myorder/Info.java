package com.kc.ihaigo.model.myorder;

public class Info extends BaseModel{

	/**
	  * @Fields serialVersionUID : TODO（状态不同，传的东西不一样,未完成时：{},未入库： {id:物流公司ID,waybill:物流单号},
	                          //待操作:  {weight：重量,fee:费用,currency:币种},,其他：{service：选择的服务}）
	  */
	
	private static final long serialVersionUID = 1L;
	
	
	public String id;  //物流公司ID
	public String waybill;   //物流单号
	public String weight;
	public String fee;
	public String currency;
	public String service;  //选择的服务
	 

}
