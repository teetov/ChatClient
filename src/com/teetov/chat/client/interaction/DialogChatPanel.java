package com.teetov.chat.client.interaction;

import com.teetov.chat.client.control.ChatPanelController;
import com.teetov.chat.client.control.StageManager;
import com.teetov.chat.message.Message;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DialogChatPanel implements Dialog {

	ChatPanelController chatPanel;
	StageManager stages;
	public DialogChatPanel(StageManager manager) {
		stages = manager;
		this.chatPanel = manager.getChatPanel();
	}

	@Override
	public String retrieveInfo(String info) {
		return stages.askInfo(info);
	}

	@Override
	public void showInfo(String info) {
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText(info);	
				
				alert.setTitle("Внимание");
				
				alert.showAndWait();
			}
		});	
		
	}

	@Override
	public void printMessage(Message message) {
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				chatPanel.addText(message.toString());
			}		
		});	
	}

	@Override
	public UserStatus getUserStatus(String userName) {
		return new UserStatusChatPanel(chatPanel, userName);
	}
}
