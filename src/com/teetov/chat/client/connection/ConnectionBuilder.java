package com.teetov.chat.client.connection;

import java.io.IOException;

public interface ConnectionBuilder {
    Connection getConnection() throws IOException;
}
