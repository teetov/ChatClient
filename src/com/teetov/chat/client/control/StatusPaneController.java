package com.teetov.chat.client.control;


import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;

public class StatusPaneController {
    @FXML
    private TitledPane statusTitledPane;
    
    @FXML
    private Text statusText;
    
    @FXML
    public void initialize() {}
    
    public void setStatusText(String text) {
        statusText.setText(text);
    }

    public TitledPane getStatusTitledPane() {
        return statusTitledPane;
    }    
}
