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
	 * Метод принимает и обрабатывает сообщения от сервера в соответствии с {@code Message.getDestination()}. 
	 * Перед вызовом метода необходимо провести отправить 
	 * инициализаионое сообщение на сервер({@code initialize()}).
	 * Попытка получить сообщеиния до инииализаии или после закрытия соединения (если {@code isAlive() == false})
	 * приводит к выбрасыванию исключения.
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
			
			dialog.showInfo("Соединение с сервером разорвано");
			
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
	 * Отправляет на сервер текст, введённый пользователем.
	 * Метод преднозначен для общения с другими клиентами, а не не для взаимодействия клиент-сервер.
	 * Попытка отправить сообщеиния до инииализаии или после закрытия соединения (если {@code isAlive() == false})
	 * приводит к выбрасыванию исключения.
	 * 
	 * @param text сообщение, отпраляетмое другим клиентам
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
	 * Отправляет сообщение о смене статуса другим пользователям.
	 * Если значение статуса совпадает с текущим - сообщение не будет отправлено.
	 * Попытка отправить сообщеиния до инииализаии или после закрытия соединения (если {@code isAlive() == false})
	 * приводит к выбрасыванию исключения.
	 * 
	 * @param index новое значение статуса
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
	 * Отправляет на сервер инициализирующее сообщения, запуская проесс общения.
	 * Метод должен быть вызван перед началом любых других взаимодействий с сервером.
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
	 * Анализирует собщение с сезультатами запроса на подключение от сервера.
	 * 
	 * @param parameters сообщение содержащее  
	 */
	private void loginParameters(Message parameters) {
		String param = parameters.getBody();
		
		if(param.contains(MessageProtocol.ACCESSED)) {
			logger.info("Access accepted");
			
			phase.accessed();
		}
	}
	
	/**
	 * Анализирует инициализирующее собщение от сервера.
	 * В зависимости от его содержания производит действия, 
	 * необходимые для получения разрешения на доступ.
	 * 
	 * @param parameters сообщение содержащее инициализационные параметры
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
	 * Генерирует запрос на получения доступа к серверу 
	 * в зависимости от полученных инииализационных парамтров.
	 * 
	 * @param param содержимое инициализационнго сообщения от сервера
	 * @return готовое для отправке на сервер сообщение
	 */
	private Message loginMessage(String param) {
		if(param.contains(MessageProtocol.REQUIRED_PASSWORD)) {
			return passwordMessage();
		} else {
			return messager.getMesage("", MessageProtocol.LOGIN);
		}
	}
	
	/**
	 * Запрашивает у пользователя пароль для получения доступа к серверу.
	 * В зависимости от ответа формирует ответное сообщение.
	 * 
	 * @return сообщение типа {@code MessageProtocol.TERMINATE} если пользователь отказался вводить пароль, 
	 * или {@code MessageProtocol.LOGIN}, с паролем в теле сообщения, в другом случае
	 */
	private Message passwordMessage() {
		String pass = dialog.retrieveInfo("Для подключения необходим пароль");
		
		//пользователь ничего не ввёл
		if (pass == null) {
			return messager.getMesage("", MessageProtocol.TERMINATE);
		}
		
		return messager.getMesage(pass, MessageProtocol.LOGIN);
	}
	
	/**
	 * Изменят или создаёт отображение текущего статуса пользователя, от которого пришло это сообщение. 
	 * @param statusMessage сообщение содержащее параметры статуса
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
	 * Отправляет на сервер сообщение с требованием прекратить общение и закрыть соединение.
	 * В нормальных условиях непосредственное закрытие соединения инициируется аналогичным 
	 * сообщением, получаемым в ответ.
	 * Если сообщение не удасться отправить, закрытие соединения будет произведено в одностороннем порядке.
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
	 * Останавливает приём и отправку сообщений.
	 */
	private void stop() {
		alive = false;
		logger.info("Connection was stopped");
		phase.terminated();
	}
	
	/**
	 * Сигнализирует о состоянии потока. 
	 * 
	 * @return true если поток может отправлять и принимать сообщения.
	 */
	public boolean isAlive() {
		return alive;
	}
	
	@Override
	public void close() {		
		connection.close();
	}
}
