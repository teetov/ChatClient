package com.teetov.chat.client.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teetov.chat.client.prop.PropertyHolder;
import com.teetov.chat.client.prop.ServerDescr;
import com.teetov.chat.client.prop.ServerDescrWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ServerSelectController {
	
	private Stage serverSelectStage;
	
	@FXML
	private TableView<ServerDescrWrapper> serversTable;
	
	@FXML
	private TableColumn<ServerDescrWrapper, String> serverNameColumn;
	@FXML
	private TableColumn<ServerDescrWrapper, String> ipAddressColumn;
	@FXML
	private TableColumn<ServerDescrWrapper, Integer> portColumn;
	@FXML
	private TableColumn<ServerDescrWrapper, String> isActive;
	@FXML
	private TableColumn<ServerDescrWrapper, String> whithPassword;
	
	@FXML
	private Label currentServerLabel;
	@FXML
	private Label infoLabel;
	
	private Logger logger = LogManager.getLogger();
	
	private boolean changed = false;
	
	private ObservableList<ServerDescrWrapper> observer = FXCollections.observableArrayList();
	
	private String notSelected = "���������� ������� ���������� �������";
	
	public ServerSelectController() {}
	
	/**
	 * �������� ����������� ������� {@code Stage}, � ������� �� ����� ������������.
	 * @param selectStage ������������ {@code Stage}
	 */
	public void setStage(Stage selectStage) {
		serverSelectStage = selectStage;
	}
	
	/**
	 * ��������������, ������� �� ������������ ��������� � ������ ��������.
	 * @return {@code true}, � ������ �������� ��������� ��������� � ��� ������� ���������
	 */
	public boolean isChanged() {
		return changed;
	}
	
	/**
	 * �������� ���������� ������ �������� ��������, ������� ���������� ����������.
	 * @param servers ������ ��� ��������������
	 */
	public void putServerList(List<ServerDescr> servers) {
		observer.clear();
		for(ServerDescr server : servers) {
			observer.add(new ServerDescrWrapper(server));
		}
		
		logger.info("Observer list size afer adding servers {}", observer.size());
	}
	
	/**
	 * ������� ������ �������� ��������, ������� ��������� ����� �������������� ��� �������������.
	 * @return ���������� ������ ��������
	 */
	public List<ServerDescr> getServersList() {
		List<ServerDescr> result = new ArrayList<>();
		for(ServerDescrWrapper wrapper : observer) {
			result.add(wrapper.getServerDescr());
		}
		return result;
	}
	
	
	@FXML
	private void initialize() {
		serverNameColumn.setCellValueFactory(cellData -> cellData.getValue().serverNameProperty());
		ipAddressColumn.setCellValueFactory(cellData -> cellData.getValue().ipAddressProperty());
		portColumn.setCellValueFactory(cellData -> cellData.getValue().portProperty().asObject());

		isActive.setCellValueFactory(cellData -> cellData.getValue().getAliveProperty());
		whithPassword.setCellValueFactory(cellData -> cellData.getValue().getPasswordProperty());
		
		serversTable.setItems(observer);
		showCurrentServer(PropertyHolder.getLastServer());
	}
	
	/**
	 * ���������� ��������� � ������� ������ ������ � ��������������� ����.
	 * @param server �������� ���������� � ������ ������ ������� ��� �����������
	 */
	private void showCurrentServer(ServerDescr server) {
		if(server == null) {
			currentServerLabel.setText("�����������");
		} else {
			StringBuilder text = new StringBuilder();
			text.append(server.getServerName());
			text.append(" ip: ");
			text.append(server.getIpAddress());
			text.append(" port: ");
			text.append(server.getPort());

			currentServerLabel.setText(text.toString());
		}
	}
	
	private int getSelectedIndex() {
		return serversTable.getSelectionModel().getSelectedIndex();
	}
	
	@FXML
	public void onSelect() {
		logger.info("Call ServerSelectController.onSelect()");
		
		int index = getSelectedIndex();
		if(index < 0) {
			infoLabel.setText(notSelected);
		} else {
			ServerDescr server = serversTable.getItems().get(index).getServerDescr();
			
			PropertyHolder.setLastServer(server);
			
			showCurrentServer(server);
		}
	}
	
	/**
	 * ����������� ��� ������� ������ ���������� ������ ��������.
	 */
	@FXML
	public void onReaload() {
		logger.info("Call ServerSelectController.onReaload()");
		for(ServerDescrWrapper wrapper : observer)
			wrapper.refresh();
		
		serversTable.refresh();
	}

	/**
	 * ����������� ��� ������� ������ ��������� ������ �������� �������.
	 */
	@FXML
	public void onAdd() {
		logger.info("Call ServerSelectController.onAdd()");
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerSelectController.class.getResource("/fxml/ServerEditLayout.fxml"));
			
			AnchorPane pane = (AnchorPane) loader.load();
			
			Stage editStage = new Stage();
			editStage.setTitle("���������� ������ �������");
			editStage.initModality(Modality.NONE);
			editStage.initOwner(serverSelectStage);
			editStage.setScene(new Scene(pane));
			
			ServerEditController controller = loader.getController();
			
			controller.setStage(editStage);
			
			editStage.showAndWait();
			
			if(controller.isChanged()) {
				serversTable.getItems().add(new ServerDescrWrapper(controller.getServerDescr()));
				changed = true;
			}
			
		} catch (IOException e) {
			logger.error("Exception occured during fxml resourses loading", e);
		}
		
	}

	/**
	 * ����������� ��� ������� ������ ��������� ����������� �������� �������.
	 */
	@FXML
	public void onChange() {
		logger.info("Call ServerSelectController.onChange()");
		
		
		int index = getSelectedIndex();
		if(index < 0) {
			infoLabel.setText(notSelected);
			return;
		} 
		
		try {
			ServerDescr server = serversTable.getItems().get(index).getServerDescr();
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerSelectController.class.getResource("/fxml/ServerEditLayout.fxml"));
			
			AnchorPane pane = (AnchorPane) loader.load();
			
			Stage editStage = new Stage();
			editStage.setTitle("�������������� �������");
			editStage.initModality(Modality.NONE);
			editStage.initOwner(serverSelectStage);
			editStage.setScene(new Scene(pane));
			
			ServerEditController controller = loader.getController();
			
			controller.setStage(editStage);
			controller.setServerDescr(server);
			
			editStage.showAndWait();
			
			if(controller.isChanged()) {
				serversTable.getItems().set(index, new ServerDescrWrapper(controller.getServerDescr()));
				changed = true;
			}
			
		} catch (IOException e) {
			logger.error("Exception occured during fxml resourses loading", e);
		}
	}

	/**
	 * ����������� ��� ������� ������ �������� ����������� �������� �������.
	 */
	@FXML
	public void onDelete() {
		logger.info("Call ServerSelectController.onDelete()");
		
		int index = getSelectedIndex();
		if(index < 0) {
			infoLabel.setText(notSelected);
		} else {
			changed = true;
			serversTable.getItems().remove(index);
		}
	}
	
	/**
	 * ����������� ��� ������� ������ ���������� ������ �� ������� �������� ��������.
	 */
	@FXML
	public void onClose() {
		logger.info("Call ServerSelectController.onCancel()");
		
		serverSelectStage.close();
	}
}
