package com.kc.ihaigo.model.myorder;

import java.util.List;

public class OrderDetailsModel extends BaseModel{

	/**
	  * @Fields serialVersionUID : TODO（订单详情Model）
	  */
	
	private static final long serialVersionUID = 1L;
	public String id;
	public String status;
	public String created_at;
	public String payType;
	public String remark;
	public List<Goods> goods;
	public String amount;
	public String total;
	public List<AgentModel> agent;//代理商信息
	public String address;
	public List<Logistic> logistic;//物流信息
	
	

}
