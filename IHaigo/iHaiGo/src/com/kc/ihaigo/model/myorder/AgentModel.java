package com.kc.ihaigo.model.myorder;

import java.util.List;

public class AgentModel extends BaseModel {

	/**
	 * @Fields serialVersionUID : TODO（订单详情 ---代理商信息model）
	 */

	private static final long serialVersionUID = 1L;

	public String id;
	public String agentsName;
	public String headPortrait;
	public String price;
	public String credit;
	public String charge;
	public String logistics;
	public String service;
	public List<Promise> promise;
	public String introduce;
	public String statement;

}
