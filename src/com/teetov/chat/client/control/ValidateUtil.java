package com.teetov.chat.client.control;

public class ValidateUtil {
	private static final String ipRegex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	/**
	 * ��������� �� ������������ ��� �������.
	 * @param name ������ ��� ��������
	 * @return ������ ������, ���� �������� ������� �������� ��� �������� ��������� �������� 
	 * (� ���� ������ � ����� ������ ����� ������� {@code System.lineSeparator()})
	 */
	public static String validateServerName(String name) {
		return "";
	}
	
	/**
	 * ��������� �� ������������ ��������� �������� IP.
	 * @param ip ������ ��� ��������
	 * @return ������ ������, ���� �������� ������� �������� ��� �������� ��������� �������� 
	 * (� ���� ������ � ����� ������ ����� ������� {@code System.lineSeparator()})
	 */
	public static String validateIp(String ip) {
		
		if(ip == null || !ip.matches(ipRegex)) {
			return "�������� ������ ip ������ (0-255.0-255.0-255.0-255)" + System.lineSeparator();
		}
		return "";
	}
	
	/**
	 * ��������� �� ������������ ��������� �������� �����.
	 * @param port ������ ��� ��������
	 * @return ������ ������, ���� �������� ������� �������� ��� �������� ��������� �������� 
	 * (� ���� ������ � ����� ������ ����� ������� {@code System.lineSeparator()})
	 */
	public static String validatePort(String port) {
		if(port == null || !port.matches("\\d+")) {
			return "�������� ������ ����� (����� �� 0 �� 65535)" + System.lineSeparator();
		}
		return "";
	}
}
