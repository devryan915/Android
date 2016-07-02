/**
 * @Title: ToastUtil.java
 * @Package: com.kc.ihaigo.util
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月22日 下午10:19:07

 * @version V1.0

 */


package com.kc.ihaigo.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @ClassName: ToastUtil
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月22日 下午10:19:07
 *
 */

public class ToastUtil {

	public ToastUtil(Context context) {
		super();
	}
	
	/**
	 * 短时间显示Toast
	 * @param context
	 * @param msg
	 */
	public static void showShort(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	/**
	 * 长时间显示Toast  
	 * @param context
	 * @param msg
	 */
	public static void showLong(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 
	  * @Title: showLocation
	  * @user: helen.yang
	  * @Description:改变显示的位置
	  *
	  *  @param context
	  *  @param msg void
	  * @throws
	 */
	public static void showLocation(Context context, String msg){
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	
}
