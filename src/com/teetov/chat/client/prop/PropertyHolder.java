package com.teetov.chat.client.prop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;

/**
 *  ласс содержит дл€ чтони€ и записи свойст подключени€.
 * “аких как им€ клиента, список извесных серверов и сведени€ о последнем использованом сервере.
 * 
 * @author  Aleksey Titov
 *
 */
public class PropertyHolder {
	
	private static String xml = PropertyHolder.class.getResource("/xml/ServerDescr.xml").getFile();
	
	private static File xmlFile = new File(xml);
	private static String propPath = PropertyHolder.class.getResource("/userPref.properties").getFile();
	private static String nameProp = "userName";
	private static String lastServerName = "lastSrverName";
	private static String lastServerAddess = "lastServerAddress";
	private static String lastServerPort = "lastServerPort";
	 
	private static String getProp(String properities) {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(propPath));

			return prop.getProperty(properities);
		} catch (Exception e) {
			LogManager.getLogger().error("Exception while reading the properties file", e);
		} 
		return null;
	}
	
	private static void setProp(String properities, String value) {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(propPath));
			prop.setProperty(properities, value);
			prop.store(new FileOutputStream(propPath), "");

		} catch (Exception e) {
			LogManager.getLogger().error("Exception while writing the properties file", e);
		} 
	}
	
	/**
	 * ѕолучить ранее введЄнное им€ пользовател€.
	 * @return им€ пользовател€ либо {@code null} в случае отсустви€ записи
	 */
	public static String getUserName() {
		return getProp(nameProp);
	}

	/**
	 * —охран€ет им€ пользовател€
	 * @param name новое им€
	 */
	public static void setUserName(String name) {
		setProp(nameProp, name);
	}
	
	/**
	 * ѕровер€ет можно ли использовать строку в качестве имени пользовател€.
	 * @param name провер€ема€ стока
	 * @return строку с описанием причины отказа или пустую строку в случае прохождени€ всех проверок
	 */
	public static String isCorrectUserName(String name) {
		if(name == null || name.equals(""))
			return "»м€ не может быть пустым";
		
		if(name.startsWith("server")) 
			return "»м€ не может начинатьс€ со слова server";
			
		return "";
	}
	
	/**
	 * —охран€ет сервер, к которому производилось последнее подключение.
	 * @param server объект, содержащий сведени€ о последнем сервере
	 */
	public static void setLastServer(ServerDescr server) {
		setProp(lastServerName, server.getServerName());
		setProp(lastServerAddess, server.getIpAddress());
		setProp(lastServerPort, String.valueOf(server.getPort()));
	}
	
	/**
	 * ¬озвращает сервер, к которому производилось последнее подключение.
	 * @return объект, содержащий сведени€ о последнем сервере либо {@code null} если данные отсутствуют
	 */
	public static ServerDescr getLastServer() {
		String name, address, port;
		name = getProp(lastServerName);
		address = getProp(lastServerAddess);
		port = getProp(lastServerPort);
		if(port == null || address == null || port.equals("") || address.equals(""))
			return null;
		name = name == null ? "" : name;
		int portInt = Integer.valueOf(port);
		return new ServerDescr(name, address, portInt);
	}
	
	/**
	 * ¬озвращает список известных серверов.
	 * @return список ранее добавленных серверов или пустой, если записей не обнаружено
	 */
	public static List<ServerDescr> getServers() { 
		if(xmlFile.exists()) {
			try {
				JAXBContext context = JAXBContext.newInstance(ServersStore.class);
				Unmarshaller um = context.createUnmarshaller();

				ServersStore holder = (ServersStore) um.unmarshal(xmlFile);

				return holder.getServers();
			} catch (JAXBException e) {
				LogManager.getLogger().error("Exception while saving the list of servers", e);
			}
		}
		return new ArrayList<>();
	}
	
	/**
	 * —охран€ет переданный список серверов в использовани€ в будующем.
	 * @param servers список серверов, который нужно сохранить
	 */
	public static void saveServers(List<ServerDescr> servers) {
		
		try {
			JAXBContext context = JAXBContext.newInstance(ServersStore.class);
			Marshaller marshaller = context.createMarshaller();
			
			ServersStore holder = new ServersStore();
			
			holder.setServers(servers);
			
			marshaller.marshal(holder, xmlFile);
			
		} catch (JAXBException e) {
			LogManager.getLogger().error("Exception while reading the list of servers", e);
		}
	}
}
