package com.teetov.chat.client.connection;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teetov.chat.client.interaction.Dialog;
import com.teetov.chat.client.interaction.PhaseListener;
import com.teetov.chat.client.interaction.UserStatus;
import com.teetov.chat.message.Message;
import com.teetov.chat.message.MessageFactory;
import com.teetov.chat.message.MessageProtocol;
import com.teetov.chat.message.StatusList;


public class Connection implements Closeable {
	
	private ServerConnection connection;
	private MessageFactory messager;	
	
	private Dialog dialog;
	
	private volatile boolean alive = true;
	
	private PhaseListener phase;
	
	private Logger logger = LogManager.getLogger();
	
	private Map<Integer, UserStatus> userStatuses = new HashMap<>();
	
	private int statusIndex = StatusList.NONE;
	
	public Connection(ServerConnection connection, MessageFactory messager, 
			Dialog dialog, PhaseListener listener) {
		this.connection = connection;
		this.messager = messager;
		this.dialog = dialog;
		
		this.phase = listener;
		
		logger.info("Connection object created");
	}

	
	/**
	 * ����� ��������� � ������������ ��������� �� ������� � ������������ � {@code Message.getDestination()}. 
	 * ����� ������� ������ ���������� �������� ��������� 
	 * ��������������� ��������� �� ������({@code initialize()}).
	 * ������� �������� ���������� �� ����������� ��� ����� �������� ���������� (���� {@code isAlive() == false})
	 * �������� � ������������ ����������.
	 * 
	 * @throws ConnectionIsNotAlive 
	 */
	public void receive() throws ConnectionIsNotAlive {		
		
		if(!alive) {
			throw new ConnectionIsNotAlive();
		}
		
		Message message = null;

		try {
			message = connection.readMesage();
		} catch (Exception e) {
			logger.error("Message was not sent", e);
			phase.terminated();
			stop();
			close();
			return;
		}

		switch(message.getDestination()) {
		case MessageProtocol.TERMINATE:
			stop();
			close();
			break;
		case MessageProtocol.LOGIN:
			loginParameters(message);
			break;
		case MessageProtocol.TEXT:
			dialog.printMessage(message);
			break;
		case MessageProtocol.STATUS:
			statusParameters(message);
			break;
		case MessageProtocol.INITIALIZE:
			initParameters(message);
			break;
		default:
			logger.warn("Can not recognize message destination");
		}
	}
	
	/**
	 * ���������� �� ������ �����, �������� �������������.
	 * ����� ������������ ��� ������� � ������� ���������, � �� �� ��� �������������� ������-������.
	 * ������� ��������� ���������� �� ����������� ��� ����� �������� ���������� (���� {@code isAlive() == false})
	 * �������� � ������������ ����������.
	 * 
	 * @param text ���������, ������������ ������ ��������
	 * @throws ConnectionIsNotAlive
	 */
	public void send(String text) throws ConnectionIsNotAlive {				
		if(!alive) {
			throw new ConnectionIsNotAlive();
		}
		
		Message message = messager.getMesage(text);
		try {
			connection.sendMessage(message);
		} catch (IOException e) {
			logger.error("Message was not sent ", e);
		}
	}
	
	/**
	 * ���������� ��������� � ����� ������� ������ �������������.
	 * ���� �������� ������� ��������� � ������� - ��������� �� ����� ����������.
	 * ������� ��������� ���������� �� ����������� ��� ����� �������� ���������� (���� {@code isAlive() == false})
	 * �������� � ������������ ����������.
	 * 
	 * @param index ����� �������� �������
	 * @throws ConnectionIsNotAlive
	 */
	public void sendStatus(int index) throws ConnectionIsNotAlive {				
		if(!alive) {
			throw new ConnectionIsNotAlive();
		}
		
		if(index == statusIndex) 
			return;
		
		statusIndex = index;
		
		Message message = messager.getMesage(String.valueOf(index), MessageProtocol.STATUS);
		try {
			connection.sendMessage(message);
		} catch (IOException e) {
			logger.error("Message {} was not sent ", message, e);
		}
	}
	
	/**
	 * ���������� �� ������ ���������������� ���������, �������� ������ �������.
	 * ����� ������ ���� ������ ����� ������� ����� ������ �������������� � ��������.
	 */
	public void initialize() {
		try {
			connection.sendMessage(messager.getMesage("", MessageProtocol.INITIALIZE));
		} catch (IOException e) {
			logger.error("Connection was not inetialized", e);
			return;
		}
		
		alive = true;
		
		phase.started();
	}
	
	/**
	 * ����������� �������� � ������������ ������� �� ����������� �� �������.
	 * 
	 * @param parameters ��������� ����������  
	 */
	private void loginParameters(Message parameters) {
		String param = parameters.getBody();
		
		if(param.contains(MessageProtocol.ACCESSED)) {
			logger.info("Access accepted");
			
			phase.accessed();
		}
	}
	
	/**
	 * ����������� ���������������� �������� �� �������.
	 * � ����������� �� ��� ���������� ���������� ��������, 
	 * ����������� ��� ��������� ���������� �� ������.
	 * 
	 * @param parameters ��������� ���������� ����������������� ���������
	 */
	private void initParameters(Message parameters) {
		String param = parameters.getBody();
		
		try {
			connection.sendMessage(loginMessage(param));
		} catch (IOException e) {
			logger.error("Connection can not receiver initial parameters", e);
			terminate();
		}
	}
	
	/**
	 * ���������� ������ �� ��������� ������� � ������� 
	 * � ����������� �� ���������� ���������������� ���������.
	 * 
	 * @param param ���������� ����������������� ��������� �� �������
	 * @return ������� ��� �������� �� ������ ���������
	 */
	private Message loginMessage(String param) {
		if(param.contains(MessageProtocol.REQUIRED_PASSWORD)) {
			return passwordMessage();
		} else {
			return messager.getMesage("", MessageProtocol.LOGIN);
		}
	}
	
	/**
	 * ����������� � ������������ ������ ��� ��������� ������� � �������.
	 * � ����������� �� ������ ��������� �������� ���������.
	 * 
	 * @return ��������� ���� {@code MessageProtocol.TERMINATE} ���� ������������ ��������� ������� ������, 
	 * ��� {@code MessageProtocol.LOGIN}, � ������� � ���� ���������, � ������ ������
	 */
	private Message passwordMessage() {
		String pass = dialog.retrieveInfo("��� ����������� ��������� ������");
		
		//������������ ������ �� ���
		if (pass == null) {
			return messager.getMesage("", MessageProtocol.TERMINATE);
		}
		
		return messager.getMesage(pass, MessageProtocol.LOGIN);
	}
	
	/**
	 * ������� ��� ������ ����������� �������� ������� ������������, �� �������� ������ ��� ���������. 
	 * @param statusMessage ��������� ���������� ��������� �������
	 */
	private void statusParameters(Message statusMessage) {
		String[] parts = statusMessage.getBody().split("#");
		logger.info("Income user id {}, all args {}", parts[0], parts.length);
		
		int id = Integer.valueOf(parts[0]);
		
		if(!userStatuses.containsKey(id)) {
			UserStatus status = dialog.getUserStatus(statusMessage.getName());
			userStatuses.put(id, status);

			logger.info("[{}] join to us", statusMessage.getName());
			
		} 
		if(parts.length > 1) {
			int statusIndx = Integer.valueOf(parts[1]);

			if(statusIndx == StatusList.getExitStustus()) {	
				userStatuses.remove(id).exit();
				
				logger.info("[{}] escaped", statusMessage.getName());
			} else {
				userStatuses.get(id).setNewStatus(StatusList.getStatus(statusIndx));
				
				logger.info("Set new status: {} from [{}]", StatusList.getStatus(statusIndx), statusMessage.getName());
			}
		}
	}
	
	/**
	 * ���������� �� ������ ��������� � ����������� ���������� ������� � ������� ����������.
	 * � ���������� �������� ���������������� �������� ���������� ������������ ����������� 
	 * ����������, ���������� � �����.
	 * ���� ��������� �� �������� ���������, �������� ���������� ����� ����������� � ������������� �������.
	 */
	public void terminate() {
		try {
			connection.sendMessage(messager.getMesage("", MessageProtocol.TERMINATE));
		} catch (IOException e) {
			logger.error("Connection can not send terminate message", e);
			stop();
		}
		
	}
	
	/**
	 * ������������� ���� � �������� ���������.
	 */
	private void stop() {
		alive = false;
		logger.info("Connection was stopped");
		phase.terminated();
	}
	
	/**
	 * ������������� � ��������� ������. 
	 * 
	 * @return true ���� ����� ����� ���������� � ��������� ���������.
	 */
	public boolean isAlive() {
		return alive;
	}
	
	@Override
	public void close() {		
		connection.close();
	}
}
