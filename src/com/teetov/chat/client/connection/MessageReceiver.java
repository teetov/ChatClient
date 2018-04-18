package com.teetov.chat.client.connection;

import org.apache.logging.log4j.LogManager;

public class MessageReceiver implements Runnable {
	private Connection connection;
	
	public MessageReceiver(Connection connection) {
		this.connection = connection;
	}
		
	public void run() {
		while(connection.isAlive()) {
			try {
				connection.receive();
			} catch (ConnectionIsNotAlive e) {
				LogManager.getLogger().error("Dead connection", e);
			}
		}
	
	}	
}
