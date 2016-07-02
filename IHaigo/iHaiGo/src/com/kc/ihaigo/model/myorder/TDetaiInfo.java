package com.kc.ihaigo.model.myorder;

import java.util.List;

public class TDetaiInfo extends BaseModel {

	/**
	 * @Fields serialVersionUID : TODO（未操作时:{weight：重量,fee:5} 操作完成，处理过程中）
	 */

	private static final long serialVersionUID = 1L;
	public String weight;
	public String fee;
	// public TdetailFee fee;

	public String reported;// 申报金额
	public String service;// 选择的服务
	public List<TdetailPackages> packages;

}
