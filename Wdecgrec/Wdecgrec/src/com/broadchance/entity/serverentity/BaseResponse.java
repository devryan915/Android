package com.broadchance.entity.serverentity;

public class BaseResponse<T> {
	public final static String OK = "0";
	public final static String FAILED = "-1";
	// / <summary>
	// / 代码
	// / </summary>
	public String Code;

	// / <summary>
	// / 接口授权码
	// / </summary>
	public String Message;

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public T getData() {
		return Data;
	}

	public void setData(T data) {
		Data = data;
	}

	// / <summary>
	// / 数据
	// / </summary>
	public T Data;

	// / <summary>
	// / 构造函数
	// / </summary>
	public BaseResponse() {

	}

	public boolean isOk() {
		return OK.equals(Code);
	}
}