package com.teetov.chat.client.control;

public class ValidateUtil {
	private static final String ipRegex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	private static String wrongIp = "Неверный формат ip адреса (0-255.0-255.0-255.0-255)" + System.lineSeparator();
	private static String wrongName = "";
	private static String wrongPort = "Неверный формат порта (число от 0 до 65535)" + System.lineSeparator();
	
	/**
	 * Проверяет на корректность имя сервера.
	 * @param name строка для проверки
	 * @return пустую строку, если проверка успешно пройдена или описание возникшей проблемы 
	 * (в этом случае в конец строки будет добален {@code System.lineSeparator()})
	 */
	public static String validateServerName(String name) {
		return "";
	}
	
	/**
	 * Проверяет на корректность текстовое описание IP.
	 * @param ip строка для проверки
	 * @return пустую строку, если проверка успешно пройдена или описание возникшей проблемы 
	 * (в этом случае в конец строки будет добален {@code System.lineSeparator()})
	 */
	public static String validateIp(String ip) {
		
		if(ip == null || !ip.matches(ipRegex)) {
			return wrongIp;
		}
		return "";
	}
	
	/**
	 * Проверяет на корректность текстовое описание порта.
	 * @param port строка для проверки
	 * @return пустую строку, если проверка успешно пройдена или описание возникшей проблемы 
	 * (в этом случае в конец строки будет добален {@code System.lineSeparator()})
	 */
	public static String validatePort(String port) {
		if(port == null || !port.matches("\\d+")) {
			return wrongPort;
		} else {
			int portInt = Integer.valueOf(port);
			if(portInt < 0 || portInt > 65535) 
				return wrongPort;
		}
		return "";
	}
}
