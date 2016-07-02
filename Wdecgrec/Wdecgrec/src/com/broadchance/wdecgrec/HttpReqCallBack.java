package com.broadchance.wdecgrec;

public abstract class HttpReqCallBack<T> implements HttpReqCallBackInterface<T> {
	protected boolean isShowLoading = true;

	public boolean isShowLoading() {
		return isShowLoading;
	}
}
