package com.teetov.chat.client.control;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teetov.chat.client.interaction.ConnectionLifeCircle;
import com.teetov.chat.client.prop.PropertyHolder;
import com.teetov.chat.client.prop.ServerDescr;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RootController implements ConnectionLifeCircle {
    
    @FXML
    MenuItem startMenuItem;
    
    @FXML
    MenuItem stopMenuItem;
    
    private Logger logger = LogManager.getLogger();
    
    private Stage rootStage;
    private StageManager stages;

    public void setRootStage(Stage rootStage, StageManager stages) {
        this.rootStage = rootStage;
        this.stages = stages;
    }
    
    @FXML
    private void start() {
        stages.startConnection();
    }
    
    @FXML
    private void stop() {
        stages.terminateConnection();
    }
    
    /**
     * Создаёт и отображает окно управляния списком серверов.
     */
    @FXML
    public void showServerWindow() {
        try {
            List<ServerDescr> servers = PropertyHolder.getServers();
            
            logger.info("ServerDescr list befoe edition {}", servers);
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RootController.class.getResource("/fxml/ServerSelectLayout.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();
            
            Stage serversStage = new Stage();
            serversStage.setTitle("Известные сервера");
            serversStage.initOwner(rootStage);
            serversStage.initModality(Modality.WINDOW_MODAL);
            serversStage.setScene(new Scene(pane));
            serversStage.resizableProperty().set(false);
            
            ServerSelectController controller = loader.getController();
            controller.putServerList(servers);
            controller.setStage(serversStage);
            
            serversStage.showAndWait();
            
            if(controller.isChanged()) {
                PropertyHolder.saveServers(controller.getServersList());
                
                logger.info("ServerDescr list after edition {}", PropertyHolder.getServers());
            }
            
        } catch (IOException e) {
            logger.error("Exception occured during fxml resourses loading", e);
        }    
    }
    
    /**
     * Создаёт и отображает окно ввода имени пользователя.
     */
    @FXML
    public void selectName() {
        String oldProp = PropertyHolder.getUserName();
        oldProp = oldProp == null ? "" : oldProp;
        
        TextInputDialog dialog = new TextInputDialog(oldProp);
        dialog.setTitle(oldProp);
        dialog.setHeaderText("Надоело быть " + oldProp + "?");
        dialog.setContentText("Введите новое имя:");
        
        Optional<String> response = dialog.showAndWait();
        
        if(response.isPresent()) {
            PropertyHolder.setUserName(response.get());
        }
    }

    @Override
    public void onActiveateConnection() {
        startMenuItem.disableProperty().set(true);
        stopMenuItem.disableProperty().set(false);
    }

    @Override
    public void onDeactiveateConnection() {
        startMenuItem.disableProperty().set(false);
        stopMenuItem.disableProperty().set(true);
    }

    @Override
    public void onAcceptAccessConnection() {
        
    }
}
