package com.kc.ihaigo.model.myorder;

public class Tdetailtems extends BaseModel{

	/**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 1L;
	
	public String id; //商品ID
	public String name; //名称
	public String source; //来源
	public String  icon;//图标
	public String   color;//颜色
	public String size; //  尺码
	public String currency; // 币种
	public String  price; // 价格
	public String  discount; // 折扣
	public String  amount; //数量
	public String total;// 总价

}
