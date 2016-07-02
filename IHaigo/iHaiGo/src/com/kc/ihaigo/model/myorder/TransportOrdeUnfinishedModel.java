package com.kc.ihaigo.model.myorder;

import java.util.List;

public class TransportOrdeUnfinishedModel extends BaseModel {

	/**
	 * @Fields serialVersionUID : TODO（未完成订单详情--model）
	 */

	private static final long serialVersionUID = 1L;
	public Info info;
	public List<Tprocess> process;
	public Tgoods  goods;

}
