package com.kc.ihaigo.model.myorder;

import java.util.List;
// //状态不同，该字段内容不容
public class Title extends BaseModel{
	
	
	/**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 1L;
	
	
	////未发货操作前
	public String content;
	public String remark;
	 ////发货操作后-------------
	public List<Tpackage> Tpackage;
	
	

}
