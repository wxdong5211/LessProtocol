package com.impler.less.exception;

public class UnsupportLessVersionException extends Exception{

	private static final long serialVersionUID = 6401310560979000026L;
	
	public UnsupportLessVersionException() {}

	public UnsupportLessVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportLessVersionException(String message) {
		super(message);
	}

	public UnsupportLessVersionException(Throwable cause) {
		super(cause);
	}
	
}
