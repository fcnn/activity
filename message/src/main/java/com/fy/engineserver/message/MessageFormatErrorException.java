package com.fy.engineserver.message;

public class MessageFormatErrorException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 4636829563163017096L;

	public MessageFormatErrorException(String message){
		super(message);
	}

	public MessageFormatErrorException(String message,Throwable e){
		super(message,e);
	}
}
