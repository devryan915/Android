package com.broadchance.wdecgrec;

public interface Skinable {
	/**
	 * 所有可更换皮肤的ui都在此方法里操作
	 * 且实现类需要在onResume和onPause中注册和取消注册
	 */
	void loadSkin();
}
