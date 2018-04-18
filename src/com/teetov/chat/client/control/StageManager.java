package com.teetov.chat.client.control;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;

import com.teetov.chat.client.ClientMainApp;
import com.teetov.chat.client.connection.Connection;
import com.teetov.chat.client.connection.ConnectionBuilder;
import com.teetov.chat.client.connection.ConnectionBuilderApp;
import com.teetov.chat.client.connection.MessageReceiver;
import com.teetov.chat.client.interaction.ConnectionController;
import com.teetov.chat.client.interaction.ConnectionLifeCircle;
import com.teetov.chat.client.interaction.Dialog;
import com.teetov.chat.client.interaction.DialogChatPanel;
import com.teetov.chat.client.interaction.PhaseListener;
import com.teetov.chat.client.interaction.PhaseListenerApp;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * ����� ������ ��������� �������� GUI � ������������ ����� ���� ��������������.
 * @author  Aleksey Titov
 *
 */
public class StageManager implements ConnectionController, ConnectionLifeCircle {

	private Stage primaryStage;
	
	private BorderPane rootLayout;
	
	private RootController rootController;
	
	private ChatPanelController chatPanelController;
	
	private Connection connection;
	
	public StageManager(Stage primaryStage) {
		this.primaryStage = primaryStage;
		preperePrimaryStage();
	}
	
	private void preperePrimaryStage() {
		primaryStage.setMaxWidth(800);
		primaryStage.setMinWidth(600);
		primaryStage.setMinHeight(500);
		
		primaryStage.setOnCloseRequest(event -> terminateConnection());
	}
	
	/**
	 * ���������� �������� {@code Stage} ����� ����������.
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * ���������� ����������, ����������� ���������� ������� ����.  
	 * @return
	 */
	public ChatPanelController getChatPanel() {
		return chatPanelController;
	}
	
	/**
	 * �������������� � �������� ���������� �������� {@code Scene} ����������.
	 * �������� � ���������� �������� ������ ����.
	 */
	public void initRoot() {
		try {
			FXMLLoader loader = new FXMLLoader();
			URL url = ClientMainApp.class.getResource("/fxml/RootLayout.fxml");
			System.out.println(url);
			loader.setLocation(url);
			rootLayout = (BorderPane) loader.load();
			
			rootController = loader.getController();
			rootController.setRootStage(primaryStage, this);
					
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (IOException e) {
			LogManager.getLogger().error("Exception occured during fxml resourses loading", e);
		}
	}
	
	/**
	 * �������������� ����������, ����������� ���������� ������� ����.  
	 */
	public void initChatPanel() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ClientMainApp.class.getResource("/fxml/ChatPanelLayout.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();
			
			rootLayout.setCenter(pane);
			

			ChatPanelController controller = loader.getController();
			chatPanelController = controller;
			
			controller.setStageManager(this);
			
		} catch (IOException e) {
			LogManager.getLogger().error("Exception occured during fxml resourses loading", e);
		}
		
	}
	
	/**
	 * ���������� ������� ���������� ����� ����������.
	 * @return {@code true}, ���� ���������� ���� ����������� 
	 */
	private boolean establishNewConnection() {
		if(chatPanelController == null) {
			LogManager.getLogger().warn("ChatPanelController was not initilaized");
			return false;
		}
		
		if(hasAliveConnection()) {
			LogManager.getLogger().warn("Cant establish new Connection - old Connection still alive");
			return false;	
		}

		ConnectionBuilder conBiolder = new ConnectionBuilderApp(this);
		
		try {
			connection = conBiolder.getConnection();
		} catch (IOException e) {
			LogManager.getLogger().warn("Excetion in ConectionBuilder", e);
		}
		
		if( connection == null){
			LogManager.getLogger().warn("Cant establish connection");
			return false;	
		}
		
		connection.initialize();
		return true;
	}
	
	/**
	 * ������������� ����� ���������� � ��������.
	 * ��������� ��������� ������� �� property ������.
	 * ����� ���������� �� ����� ����������� ���� � 
	 * ���������� ��� ������������ ���������� ����������.
	 * 
	 * @return {@code true}, ���� ���������� ������� �����������.
	 */
	public void startConnection() {
		establishNewConnection();
	}
	

	@Override
	public void onActiveateConnection() {
		chatPanelController.onActiveateConnection();
		rootController.onActiveateConnection();
		
		Thread receive = new Thread(new MessageReceiver(connection));
		receive.setDaemon(true); //?????? is nessecery?
		receive.start();
	}

	@Override
	public void onAcceptAccessConnection() {
		chatPanelController.onAcceptAccessConnection();
	}

	@Override
	public void onDeactiveateConnection() {
		chatPanelController.onDeactiveateConnection();
		rootController.onDeactiveateConnection();
	}
	
	/**
	 * ���������� ����-��������������.
	 * @param title 
	 * @param header
	 * @param content
	 */
	public void alert(String title, String header, String content) {     	
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(primaryStage);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		
		alert.showAndWait();
	}
	
	/**
	 * ����������� � ������������ ���� ���������� ����������.
	 * 
	 * @param info ��������� ��� ������������, ��� �� ���� ����� ��������
	 * @return �������� ������������� �����. {@code null} � ������, ���� ������������ ��������� �������� �� ������.
	 */
	public String askInfo(String info) {

		final AtomicReference<Optional<String>> resQue = new AtomicReference<>();

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog("");
				dialog.setHeaderText(info);

				Optional<String> result = dialog.showAndWait();
				
				synchronized(resQue) {
					resQue.set(result);

					resQue.notifyAll();
				}
			}

		});
		try {synchronized(resQue) {

			resQue.wait();
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Optional<String> opt = resQue.get();
		
		if(opt.isPresent()) {
			return opt.get();
		}
		return null;
	}

	
	
	@Override
	public PhaseListener getPhaseListener() {
		return new  PhaseListenerApp(this);
	}

	@Override
	public Dialog getDialog() {
		return new DialogChatPanel(this);
	}
	
	/**
	 * ����� �������� ���������� � �������� ������� �����������.
	 * � ������, ���� ���������� ��� �� ����������� ��� ��� �������, ������ �� ����������.
	 */
	public void terminateConnection() {
		if(connection != null && connection.isAlive())
			connection.terminate();
	}
	
	/**
	 * ���������� ������� ����������.
	 * @return ���������� ��� {@code null}, ���� ��� ��� �� ���� �����������
	 */
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * ���������, ��������� �� ������� ���������� � �������� ���������.
	 * @return {@code null}, ���� ���������� ��� �� ����������� ��� ��� �������
	 */
	public boolean hasAliveConnection() {
		if(connection == null) 
			return false;
		return connection.isAlive();
	}
}
