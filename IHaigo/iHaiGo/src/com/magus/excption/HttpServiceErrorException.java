package com.magus.excption;


public class HttpServiceErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	public HttpServiceErrorException() {
		super();
	}

	public HttpServiceErrorException(String message) {
		super(message);
	}
	
}
