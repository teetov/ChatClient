package com.teetov.chat.client.connection;

public class ConnectionIsNotAlive extends Exception {

    private static final long serialVersionUID = 1L;

    public ConnectionIsNotAlive() {}
    
    public ConnectionIsNotAlive(Throwable t) {
        super(t);
    }
}
        