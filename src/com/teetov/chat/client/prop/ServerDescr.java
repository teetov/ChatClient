package com.teetov.chat.client.prop;

import javax.xml.bind.annotation.XmlElement;


/**
 * ����� ������ �������� �������� ���������� ������.
 * ����� ���, ip, ���� � ����������� ������������� ��� �������� ������������� ��� .
 * @author  Aleksey Titov
 */
public class ServerDescr {
	@XmlElement(name = "serverName")
	private String serverName;
	@XmlElement(name = "address")
	private String ipAddress;
	@XmlElement(name = "port")
	private int port;
	
	public ServerDescr() {}
	
	public ServerDescr(String serverName, String ipAddress, int port) {
		super();
		this.serverName = serverName;
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public String getServerName() {
		return serverName;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return "ServerDescr [serverName=" + serverName + ", address=" + ipAddress + ", port=" + port + "]";
	}
	
	
}
