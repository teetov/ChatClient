package com.teetov.chat.client.control;

public class ValidateUtil {
	private static final String ipRegex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	/**
	 * ѕровер¤ет на корректность им¤ сервера.
	 * @param name строка дл¤ проверки
	 * @return пустую строку, если проверка успешно пройдена или описание возникшей проблемы 
	 * (в этом случае в конец строки будет добален {@code System.lineSeparator()})
	 */
	public static String validateServerName(String name) {
		return "";
	}
	
	/**
	 * ѕровер¤ет на корректность текстовое описание IP.
	 * @param ip строка дл¤ проверки
	 * @return пустую строку, если проверка успешно пройдена или описание возникшей проблемы 
	 * (в этом случае в конец строки будет добален {@code System.lineSeparator()})
	 */
	public static String validateIp(String ip) {
		
		if(ip == null || !ip.matches(ipRegex)) {
			return "Ќеверный формат ip адреса (0-255.0-255.0-255.0-255)" + System.lineSeparator();
		}
		return "";
	}
	
	/**
	 * ѕровер¤ет на корректность текстовое описание порта.
	 * @param port строка дл¤ проверки
	 * @return пустую строку, если проверка успешно пройдена или описание возникшей проблемы 
	 * (в этом случае в конец строки будет добален {@code System.lineSeparator()})
	 */
	public static String validatePort(String port) {
		if(port == null || !port.matches("\\d+")) {
			return "Ќеверный формат порта (число от 0 до 65535)" + System.lineSeparator();
		}
		return "";
	}
}
