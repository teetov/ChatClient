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
	 * ��������������, ���������� �� ������������ ������� ��������� ��������� � �������� �������.
	 * @return {@code true}, ���� �������� �������� ������� ���������
	 */
	public boolean isChanged() {
		return changed;
	}
	
	/**
	 * �������� ����������� ������� {@code Stage}, � ������� �� ����� ������������.
	 * @param editStage ������������ {@code Stage}
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
	 * �������� ����������� �������� ������� ��� ��������������. 
	 * ���� ����� �� ��� ������ ����� ������������ ������� ����, 
	 * ����� ����������� �� �������������� ���������� ��������, � �������� ������. 
	 * @param server �������� ���������� �������
	 */
	public void setServerDescr(ServerDescr server) {
		this.server = server;
		
		serverNameLabel.setText(server.getServerName());
		ipAddressLabel.setText(server.getIpAddress());
		portLabel.setText(String.valueOf(server.getPort()));
		
	}
	
	/**
	 * ���������� �������� �������, �������� �������������.
	 * ����� ������� �������� ����� �������� �������, ���� {@code isChanged()} ���������� {@code true}.
	 * @return ����� �������� ������� ��� {@code null}, ���� ������������ ��������� ������������ ���������
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
        	alert.setTitle("������������ ����");
        	alert.setHeaderText("��������� ��������� �� ���������� ��������");
        	alert.setContentText(response.toString());
        	
        	alert.showAndWait();
        	return false;
		}
	}
}
