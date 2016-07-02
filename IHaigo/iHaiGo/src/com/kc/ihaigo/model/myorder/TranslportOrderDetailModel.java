package com.kc.ihaigo.model.myorder;

import java.util.List;


public class TranslportOrderDetailModel extends BaseModel{

	/**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 1L;
	public  TDetailGoods good; 
	
	public  List<TDetaiPgogress> process; 
	
	public TDetaiInfo info;
	
	

}
