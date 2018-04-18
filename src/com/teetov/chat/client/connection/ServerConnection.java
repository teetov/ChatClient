package com.teetov.chat.client.connection;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teetov.chat.message.Message;

/**
 * ����� ��������� ������ �� ��������� � �������� ��������� �� ������.
 * @author  Aleksey Titov
 *
 */
public class ServerConnection implements Closeable {
	private Socket socket;
	
	private ObjectOutputStream objOut;
	private ObjectInputStream objIn;
	
	private Logger logger = LogManager.getLogger();
	
	public ServerConnection(Socket socket) throws IOException {
		this.socket = socket;	
		
		objOut = new ObjectOutputStream(socket.getOutputStream());

		objIn = new ObjectInputStream(socket.getInputStream());
	}
	
	/**
	 * ���������� ��������� �� ������.
	 * 
	 * @param message ��������� ��� ��������
	 * @throws IOException
	 */
	public synchronized void sendMessage(Message message) throws IOException {
		logger.debug("Message write {} [dest: {}]", message, message.getDestination());
		
		objOut.writeObject(message);
	}
	
	/**
	 * ��������� ��������� �� �������.
	 * 
	 * @return ���������� ���������
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Message readMesage() throws ClassNotFoundException, IOException {
		Message message = (Message) objIn.readObject();

		logger.debug("Message reseive {} [dest: {}]", message, message.getDestination());
		
		return message;
	}

	/**
	 * ���������� ������ {@ java.net.Socket} ����� ����������.
	 * @return {@ Socket}, ������������ ��� ������� � ��������
	 */
	public Socket getSocket() {
		return socket;
	}
	
	@Override
	public void close(){
		try {
			objOut.close();
		} catch (IOException e) {
			logger.error("Exception on closing ObjectOutputStream ", e);
		}
		try {
			objIn.close();
		} catch (IOException e) {
			logger.error("Exception on closing ObjectInputStream ", e);
		}
		try {
			socket.close();
		} catch (IOException e) {
			logger.error("Exception on closing Socket ", e);
		}
	}
	
}
