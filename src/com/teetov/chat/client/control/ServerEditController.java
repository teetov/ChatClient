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
	 * —видетельстует, подтвердил ли пользователь желание сохранить изменени€ в описании сервера.
	 * @return {@code true}, если введЄнное описание следует сохранить
	 */
	public boolean isChanged() {
		return changed;
	}
	
	/**
	 * ѕередача контроллеру объекта {@code Stage}, в котором он будет отображатьс€.
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
	 * ѕередача контроллеру описани€ сервера дл€ редактировани€. 
	 * ≈сли метод не был вызван перед отображением данного окна, 
	 * будет произведено не редактирование имеющегос€ описани€, а создание нового. 
	 * @param server описание выбранного сервера
	 */
	public void setServerDescr(ServerDescr server) {
		this.server = server;
		
		serverNameLabel.setText(server.getServerName());
		ipAddressLabel.setText(server.getIpAddress());
		portLabel.setText(String.valueOf(server.getPort()));
		
	}
	
	/**
	 * ¬озвращает описание сервера, введЄнное пользователем.
	 * ћетод следует вызавать после закрыти€ сервера, если {@code isChanged()} возвращает {@code true}.
	 * @return новое описание сервера или {@code null}, если пользователь отказалс€ подтверждать изменени€
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
        	alert.setTitle("Ќеправильное поле");
        	alert.setHeaderText("ѕожалуста исправьте на допустимое значение");
        	alert.setContentText(response.toString());
        	
        	alert.showAndWait();
        	return false;
		}
	}
}
