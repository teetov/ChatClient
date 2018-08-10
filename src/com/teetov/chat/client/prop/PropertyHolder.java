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
 *  Класс содержит для чтония и записи свойств подключения.
 * Таких как имя клиента, список извесных серверов и сведения о последнем использованом сервере.
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
     * Получить ранее введённое имя пользователя.
     * @return имя пользователя либо {@code null} в случае отсуствия записи
     */
    public static String getUserName() {
        return getProp(nameProp);
    }

    /**
     * Сохраняет имя пользователя.
     * @param name новое имя
     */
    public static void setUserName(String name) {
        setProp(nameProp, name);
    }
    
    /**
     * Пѕроверяет можно ли использовать строку в качестве имени пользователя.
     * @param name проверяемая стока
     * @return строку с описанием причины отказа или пустую строку в случае прохождени¤ всех проверок
     */
    public static String isCorrectUserName(String name) {
        if(name == null || name.equals(""))
            return "имя не может быть пустым";
        
        if(name.startsWith("server")) 
            return "имя не может начинаться со слова server";
            
        return "";
    }
    
    /**
     * Сохраняет сервер, к которому производилось последнее подключение.
     * @param server объект, содержащий сведени¤ о последнем сервере
     */
    public static void setLastServer(ServerDescr server) {
        setProp(lastServerName, server.getServerName());
        setProp(lastServerAddess, server.getIpAddress());
        setProp(lastServerPort, String.valueOf(server.getPort()));
    }
    
    /**
     * Возвращает сервер, выбранный в какчестве основного для подключения.
     * @return объект, содержащий сведения о последнем сервере либо {@code null}, если данные отсутствуют
     */
    public static ServerDescr getCurrentServer() {
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
     * Возвращает список известных серверов.
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
     * Сохраяет переданный список серверов в использования в будующем.
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
