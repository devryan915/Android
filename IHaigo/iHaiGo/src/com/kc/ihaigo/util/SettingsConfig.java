/**
 * @Title: DataConfig.java
 * @Package: com.kc.ihaigo.util
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月10日 下午1:53:07

 * @version V1.0

 */

package com.kc.ihaigo.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName: DataConfig
 * @Description: 接口配置文件工具类
 * @author: ryan.wang
 * @date: 2014年7月10日 下午1:53:07
 * 
 */

public class SettingsConfig {
	private SharedPreferences spf;
	/**
	 * 
	 * @Title: setReceiveMsgSwitch
	 * @user: ryan.wang
	 * @Description: 设置是否开启通知
	 * 
	 * @param isopen
	 *            void
	 * @throws
	 */
	public void setReceiveMsgSwitch(boolean isopen) {
		spf.edit().putBoolean("receivemsgswitch", isopen).commit();
	}
	public boolean isReceiveMsgSwitch() {
		return spf.getBoolean("receivemsgswitch", false);
	}
	/**
	 * 
	 * @Title: setReceiveShippingMsg
	 * @user: ryan.wang
	 * @Description: 是否接收运单更新通知
	 * 
	 * @param isopen
	 *            void
	 * @throws
	 */
	public void setReceiveShippingMsg(boolean isopen) {
		spf.edit().putBoolean("receiveshippingmsg", isopen).commit();
	}
	public boolean isReceiveShippingMsg() {
		return spf.getBoolean("receiveshippingmsg", false);
	}
	/**
	 * 
	 * @Title: setReceiveShippingMsg
	 * @user: ryan.wang
	 * @Description: 是否接收订单通知
	 * 
	 * @param isopen
	 *            void
	 * @throws
	 */
	public void setReceiveOrderMsg(boolean isopen) {
		spf.edit().putBoolean("receiveordermsg", isopen).commit();
	}
	public boolean isReceiveOrderMsg() {
		return spf.getBoolean("receiveordermsg", false);
	}
	/**
	 * 
	 * @Title: setReceiveShippingMsg
	 * @user: ryan.wang
	 * @Description: 是否接收话题消息
	 * 
	 * @param isopen
	 *            void
	 * @throws
	 */
	public void setReceiveTopicMsg(boolean isopen) {
		spf.edit().putBoolean("receivetopicmsg", isopen).commit();
	}
	public boolean isReceiveTopicMsg() {
		return spf.getBoolean("receivetopicmsg", false);
	}
	/**
	 * 
	 * @Title: setReceiveShippingMsg
	 * @user: ryan.wang
	 * @Description: 是否接收商品预警
	 * 
	 * @param isopen
	 *            void
	 * @throws
	 */
	public void setReceiveGoodsWarnningMsg(boolean isopen) {
		spf.edit().putBoolean("receivegoodswarnningmsg", isopen).commit();
	}
	public boolean isReceiveGoodsWarnningMsg() {
		return spf.getBoolean("receivegoodswarnningmsg", false);
	}
	/**
	 * 
	 * @Title: setReceiveShippingMsg
	 * @user: ryan.wang
	 * @Description: 是否开启活动推荐
	 * 
	 * @param isopen
	 *            void
	 * @throws
	 */
	public void setReceiveActivityMsg(boolean isopen) {
		spf.edit().putBoolean("receiveactivitymsg", isopen).commit();
	}
	public boolean isReceiveActivityMsg() {
		return spf.getBoolean("receiveactivitymsg", false);
	}
	/**
	 * 
	 * @Title: setReceiveShippingMsg
	 * @user: ryan.wang
	 * @Description: 是否使用声音通知
	 * 
	 * @param isopen
	 *            void
	 * @throws
	 */
	public void setReceiveByVoice(boolean isopen) {
		spf.edit().putBoolean("receivebyvoice", isopen).commit();
	}
	public boolean isReceiveByVoice() {
		return spf.getBoolean("receivebyvoice", false);
	}
	/**
	 * 
	 * @Title: setReceiveShippingMsg
	 * @user: ryan.wang
	 * @Description: 是否通过振动通知
	 * 
	 * @param isopen
	 *            void
	 * @throws
	 */
	public void setReceiveByShake(boolean isopen) {
		spf.edit().putBoolean("receivebyshake", isopen).commit();
	}
	public boolean isReceiveByShake() {
		return spf.getBoolean("receivebyshake", false);
	}
	/**
	 * 
	 * @Title: setReceiveShippingMsg
	 * @user: ryan.wang
	 * @Description: 是否通过闪光灯通知
	 * 
	 * @param isopen
	 *            void
	 * @throws
	 */
	public void setReceiveByFlash(boolean isopen) {
		spf.edit().putBoolean("receivebyflash", isopen).commit();
	}
	public boolean isReceiveByFlash() {
		return spf.getBoolean("receivebyflash", false);
	}
	/**
	 * 
	 * @Title: setStartTime
	 * @user: ryan.wang
	 * @Description: 获取推送开始时间
	 * 
	 * @param time
	 *            void
	 * @throws
	 */
	public void setStartTime(String time) {
		spf.edit().putString("starttime", time).commit();
	}
	public String getStartTime() {
		return spf.getString("starttime", "00:00");
	}
	/**
	 * 
	 * @Title: setEndTime
	 * @user: ryan.wang
	 * @Description: 获取推送结束时间
	 * 
	 * @param time
	 *            void
	 * @throws
	 */
	public void setEndTime(String time) {
		spf.edit().putString("endtime", time).commit();
	}
	public String getEndTime() {
		return spf.getString("endtime", "24:00");
	}
	public SettingsConfig(Context ctx) {
		spf = ctx.getSharedPreferences("SettingsConfig",
				Context.MODE_WORLD_WRITEABLE);
	}
}
