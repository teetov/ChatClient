package com.teetov.chat.client.interaction;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;

import com.teetov.chat.client.control.ChatPanelController;
import com.teetov.chat.client.control.StatusPaneController;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TitledPane;

public class UserStatusChatPanel implements UserStatus {

    private ChatPanelController chatPanel;
    private TitledPane userTitle;
    private StatusPaneController controller;
    
    public UserStatusChatPanel(ChatPanelController chatPanel, String userName) {
        this.chatPanel = chatPanel;
        
         try {
             FXMLLoader loader = new FXMLLoader();
             loader.setLocation(ChatPanelController.class.getResource("/fxml/StatusPaneLayout.fxml"));
             TitledPane pane = (TitledPane) loader.load();

             userTitle = pane;
             controller = loader.getController();
             
             pane.setText(userName);
             
             Platform.runLater(new Runnable() {

                 @Override
                 public void run() {
                     chatPanel.getStatusesVBox().getChildren().add(pane);
                 }

             });

         } catch (IOException e) {
            LogManager.getLogger().error("Exception occured during fxml resourses loading", e);
         }
    }
    
    @Override
    public void setNewStatus(String newStatus) {
        chatPanel.addStatusPaneController(controller);
        
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                controller.setStatusText(newStatus);
                
                if(!chatPanel.isHidingStatuses() && !"".equals(newStatus))
                    userTitle.expandedProperty().set(true);
            }
            
        });
    }

    @Override
    public void exit() {
        chatPanel.removeStatusPaneController(controller);
        
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                chatPanel.getStatusesVBox().getChildren().remove(userTitle);
            }
            
        });
    }

}
