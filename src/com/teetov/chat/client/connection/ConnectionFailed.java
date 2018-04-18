package com.teetov.chat.client.connection;

public class ConnectionFailed extends Exception {


	private static final long serialVersionUID = 1L;

	public ConnectionFailed() {
		super();
	}
	
	public ConnectionFailed(Throwable t) {
		super(t);
	}

}
