package com.kc.ihaigo.model.myorder;

import java.util.List;

public class TransportOrderListModel extends BaseModel {

	/**
	 * @Fields serialVersionUID : TODO（转运订单列表页Model）
	 */

	private static final long serialVersionUID = 1L;

	public List<TransportOrders> orders;

	// public String transport ; //转运公司编号
	// 状态不同，传的东西不一样,未完成时：{},未入库： {id:物流公司ID,waybill:物流单号},
	// 待操作: {weight：重量,fee:费用,currency:币种}

}
