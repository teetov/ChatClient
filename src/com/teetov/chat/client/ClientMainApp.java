package com.teetov.chat.client;

import com.teetov.chat.client.control.StageManager;

import javafx.application.Application;
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
