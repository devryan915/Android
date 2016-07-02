package com.broadchance.wdecgrec;

import android.app.Activity;

public interface HttpReqCallBackInterface<T> {
	Activity getReqActivity();

	void doSuccess(T result);

	void doError(String result);
}
