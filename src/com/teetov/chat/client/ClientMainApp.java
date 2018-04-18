package com.teetov.chat.client;

import java.io.IOException;	
import java.net.URL;

import com.teetov.chat.client.connection.ConnectionBuilderApp;
import com.teetov.chat.client.connection.Connection;
import com.teetov.chat.client.connection.MessageReceiver;
import com.teetov.chat.client.control.ChatPanelController;
import com.teetov.chat.client.control.StageManager;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ClientMainApp extends Application {

	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Chat");
		StageManager mainStage = new StageManager(primaryStage);
		
		mainStage.initRoot();
		mainStage.initChatPanel();
		
	}

}
