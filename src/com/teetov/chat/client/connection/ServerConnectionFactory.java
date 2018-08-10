package com.teetov.chat.client.connection;

import java.net.Socket;

import com.teetov.chat.client.prop.ServerDescr;

public class ServerConnectionFactory {
    public static ServerConnection getServerConnection(ServerDescr serverDesc) throws ConnectionFailed {
        try {
            Socket socket = new Socket(serverDesc.getIpAddress(), serverDesc.getPort());
            return new ServerConnection(socket);
        } catch (Exception exc) {
            throw new ConnectionFailed(exc);
        }     
    }
}
