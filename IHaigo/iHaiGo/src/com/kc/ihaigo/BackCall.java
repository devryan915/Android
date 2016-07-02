package com.kc.ihaigo;

/**
 * 
 * @ClassName: BackCall
 * @Description: 定义所有回调规范
 * @author: ryan.wang
 * @date: 2014年7月16日 下午4:44:24
 * 
 */
public abstract class BackCall {
	public int whichId;
	public void deal(int which, Object... obj) {
	};

}