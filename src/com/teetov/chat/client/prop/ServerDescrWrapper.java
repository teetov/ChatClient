package com.teetov.chat.client.prop;

import com.teetov.chat.client.connection.test.CheckConnection;
import com.teetov.chat.client.connection.test.ServerInfo;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * �����-������ ��� {@code ServerDescr}. 
 * ����������� ��� �������� ��������� ��� ������������� � FX �������.
 * ��� �� ��������� ������� {@code ServerDescr} ����������������� �������� �������� �������� ������� 
 * (�� ������ {@code com.teetov.chat.connection.test}).
 * 
 * @author  Aleksey Titov
 *
 */
public class ServerDescrWrapper {
	private final StringProperty serverName;
	private final StringProperty ipAddress;
	private final IntegerProperty port;
	
	private final StringProperty isAlive;
	private final StringProperty isPassword;
	
	/**
	 * ����������, ��������� �� ������ �������� �������� ����������� ������� ��� ������� �������������� ��������.
	 * {@code true} - ������� ���������� ��������, {@code false} - ���������� ���������� �������� ����������.
	 */
	private volatile boolean updated = false;
	/**
	 * {@code true} ��������, ��� ������� ���������� ���������� ������������ � ������ ������.
	 */
	private volatile boolean processCheckInfo = false;
	
	private final String UNKNOWN = "����������";
	private final String PASSWORD = "���������";
	private final String WITHOUT_PASSWORD = "�� ���������";
	private final String ACTIVE = "��������";
	private final String NOT_ACTIVE = "�� � ����";
	
	ServerDescrWrapper(String name, String ipAddress, int port) {
		this.serverName = new SimpleStringProperty(name);
		this.ipAddress = new SimpleStringProperty(ipAddress);
		this.port = new SimpleIntegerProperty(port);
		isAlive = new SimpleStringProperty(UNKNOWN);
		isPassword = new SimpleStringProperty(UNKNOWN);
	}

	public ServerDescrWrapper(ServerDescr server) {
		this(server.getServerName(), server.getIpAddress(), server.getPort());
	}

	public StringProperty serverNameProperty() {
		return serverName;
	}

	public StringProperty ipAddressProperty() {
		return ipAddress;
	}

	public IntegerProperty portProperty() {
		return port;
	}
	
	public String getServerName() {
		return serverName.get();
	}

	public String getIpAddress() {
		return ipAddress.get();
	}

	public int getPort() {
		return port.get();
	}

	public ServerDescr getServerDescr() {
		return new ServerDescr(serverName.getValue(), ipAddress.getValue(), port.getValue());
	}
	
	public void setServerName(String name) {
		this.serverName.set(name);
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress.set(ipAddress);
	}
	
	public void setPort(int port) {
		this.port.set(port);
	}
	
	private void setServerInfo(ServerInfo server) {
		if(server.isAlive()) {
			isAlive.set(ACTIVE);
			
			if(server.isPassword())
				isPassword.set(PASSWORD);
			else
				isPassword.set(WITHOUT_PASSWORD);
		}		
		else
			isAlive.set(NOT_ACTIVE);		
	}
	
	public void refresh() {
		updated = false;
	}
	
	public StringProperty getAliveProperty() {
		checkServerInfo();
		
		return isAlive;
	}
	
	public StringProperty getPasswordProperty() {
		
		checkServerInfo();
		
		return isPassword;
		
	}
	
	private void checkServerInfo() {
		if(!updated && !processCheckInfo) {
			processCheckInfo = true;	
			updated = true;
			Thread infoThread = new Thread() {
				@Override
				public void run() {

					ServerInfo info = CheckConnection.check(getServerDescr());
					processCheckInfo = false;

					Platform.runLater(new Runnable() {

						@Override
						public void run() {

							setServerInfo(info);
						}

					});
				}
			};

			infoThread.setDaemon(true);
			infoThread.start();
		}
	}
}
