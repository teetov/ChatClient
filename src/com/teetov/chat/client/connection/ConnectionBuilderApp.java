package com.teetov.chat.client.connection;

import java.io.IOException;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;

import com.teetov.chat.client.control.ChatPanelController;
import com.teetov.chat.client.control.StageManager;
import com.teetov.chat.client.interaction.Dialog;
import com.teetov.chat.client.interaction.PhaseListener;
import com.teetov.chat.client.prop.PropertyHolder;
import com.teetov.chat.client.prop.ServerDescr;
import com.teetov.chat.message.MessageFactory;

public class ConnectionBuilderApp implements ConnectionBuilder {

	StageManager manager;
	
	public ConnectionBuilderApp(StageManager manager) {
		this.manager = manager;
	}
	
	@Override
	public Connection getConnection() throws IOException {
		ServerDescr serverDescr = PropertyHolder.getLastServer();
		System.out.println(serverDescr);
		
		if(serverDescr == null) {
			manager.alert("Подключение невозможно", "Сервер не выбран",
					"Настройки > Список срверов > Выбрать один из существующих или ввести новый");
			return null;
		}
		
		String name = PropertyHolder.getUserName();
		System.out.println(name);
		String checkName = PropertyHolder.isCorrectUserName(name);
		if(checkName.length() > 0) {
			manager.alert("Подключение невозможно", checkName, 
					"Настройки > Выбрать имя > Ввести новое имя");
			return null;
		}

		ServerConnection connection;
		try {
			connection = ServerConnectionFactory.getServerConnection(serverDescr);
		} catch (ConnectionFailed e) {
			LogManager.getLogger().error("Server {} not avalieble", serverDescr);
			manager.alert("Подключение невозможно", "Выбранный сервер недоступен", 
					"Настройки > Список срверов > Выбрать один из существующих или ввести новый");
			return null;
		}

		Dialog dialog = manager.getDialog();

		MessageFactory messanger = new MessageFactory(name, connection.getSocket().getLocalAddress().toString());		
		
		PhaseListener listener = manager.getPhaseListener();
		
		return new Connection(connection, messanger, dialog, listener);	
	}

}
