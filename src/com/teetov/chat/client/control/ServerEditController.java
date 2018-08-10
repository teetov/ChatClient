package com.teetov.chat.client.control;

import org.apache.logging.log4j.LogManager;

import com.teetov.chat.client.prop.ServerDescr;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ServerEditController {
        
    private Stage editStage;
    
    @FXML
    TextField serverNameLabel;
    @FXML
    TextField ipAddressLabel;
    @FXML
    TextField portLabel;
    
    private boolean changed = false;
    
    private ServerDescr server;
    
    /**
     * Свидетельстует, подтвердил ли пользователь желание сохранить изменения в описании сервера.
     * @return {@code true}, если введённое описание следует сохранить
     */
    public boolean isChanged() {
        return changed;
    }
    
    /**
     * Передача контроллеру объекта {@code Stage}, в котором он будет отображаться.
     * @param editStage родетельский {@code Stage}
     */
    public void setStage(Stage editStage) {
        this.editStage = editStage;
    }
    
    @FXML
    public void onOk() {
        LogManager.getLogger().info("Call ServerEditController.onOk()");

        if(!validate()) {
            return;
        }

        changed = true;
        
        server = new ServerDescr(serverNameLabel.getText(),
                ipAddressLabel.getText(), Integer.valueOf(portLabel.getText()));
        
        editStage.close();
    }
    
    @FXML
    public void onCancel() {
        LogManager.getLogger().info("Call ServerEditController.onCancel()");
        
        editStage.close();
    }
    
    /**
     * Передача контроллеру описания сервера для редактирования. 
     * Если метод не был вызван перед отображением данного окна, 
     * будет произведено не редактирование имеющегося описания, а создание нового. 
     * @param server описание выбранного сервера
     */
    public void setServerDescr(ServerDescr server) {
        this.server = server;
        
        serverNameLabel.setText(server.getServerName());
        ipAddressLabel.setText(server.getIpAddress());
        portLabel.setText(String.valueOf(server.getPort()));
        
    }
    
    /**
     * Возвращает описание сервера, введённое пользователем.
     * Метод следует вызавать после закрытия сервера, если {@code isChanged()} возвращает {@code true}.
     * @return новое описание сервера или {@code null}, если пользователь отказался подтверждать изменения
     */
    public ServerDescr getServerDescr() {
        return server;
    }
    
    private boolean validate() {
        
        String name = serverNameLabel.getText();
        String port = portLabel.getText();
        String ip = ipAddressLabel.getText();
                        
        StringBuilder response = new StringBuilder();
        
        
        response.append(ValidateUtil.validateServerName(name));
 
        response.append(ValidateUtil.validateServerName(ip));
        
        response.append(ValidateUtil.validateServerName(port));
        
        if(response.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(editStage);
            alert.setTitle("Неправильное поле");
            alert.setHeaderText("Пожалуста исправьте на допустимое значение");
            alert.setContentText(response.toString());
            
            alert.showAndWait();
            return false;
        }
    }
}
