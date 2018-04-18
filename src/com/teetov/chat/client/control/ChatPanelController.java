package com.teetov.chat.client.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;

import com.teetov.chat.client.connection.Connection;
import com.teetov.chat.client.connection.ConnectionIsNotAlive;
import com.teetov.chat.client.interaction.ConnectionLifeCircle;
import com.teetov.chat.message.StatusList;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatPanelController implements  ConnectionLifeCircle {

	private StageManager manager;

	/**
	 * ������������ ���������� ������������ �������������� ���������.
	 */
	private final int MAX_MESSAGES = 1000;

	/**
	 * ��������� ������������, ����������� ������������ ������� ����������� ������������
	 */
	private Set<StatusPaneController> statusPanes = new HashSet<>();

	@FXML
	private VBox messageBox;

	@FXML
	private TextArea inputArea;

	@FXML
	private Button inputButton;

	@FXML
	private Button terminateButton;

	@FXML
	private AnchorPane inputPanel;

	@FXML
	private VBox statusesVBox;

	@FXML
	private ChoiceBox<StatusChoiceItem> statusChoiceBox;

	@FXML
	private Button statusButton;

	@FXML
	private AnchorPane statusChoicePane;

	@FXML
	private ToggleButton hideStatusTogglButton;
	
	@FXML
	private Label currentStatusLabel;

	/**
	 * �������� ������� ������������� �������������
	 */
	private boolean hideStatus = false;

	public ChatPanelController() {}

	public void setStageManager(StageManager mainStage) {
		this.manager = mainStage;
	}
	
	/**
	 * ��������� ����� ������ ��������� �� ������ ���������
	 * @param text
	 */
	public void addText(String text) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {			
				Node textNode = new Text(text);
				messageBox.getChildren().add(textNode);

				if(messageBox.getChildren().size() > MAX_MESSAGES) 
					messageBox.getChildren().remove(0);
			}
		});
	}

	@FXML
	private void initialize() {
		prepereChoiceBox();
	}

	/**
	 * ��������� ������� ����������.
	 */
	@FXML
	public void closeConnection() {
		manager.terminateConnection();
	}

	/**
	 * ������������� ������ � ����������, � ������� ����������� ������� �������������.
	 * @return
	 */
	public VBox getStatusesVBox() {
		return statusesVBox;
	}

	/**
	 * ����������� ����� ����������� �������� (��������/����������). � ���������� � ������������ � �������.
	 */
	@FXML
	public void hideStatusToggle() {
		hideStatus = hideStatusTogglButton.selectedProperty().get();
		if(hideStatus) {
			for(StatusPaneController controller : statusPanes) {
				controller.getStatusTitledPane().expandedProperty().set(false);
			}
		}
	}

	/**
	 * ����������� � ������� ������ ����������� �������� (���������/�����������).
	 * @return
	 */
	public boolean isHidingStatuses() {
		return hideStatus;
	}
	
	/**
	 * ��������� ��������� � ������ ������ ������ �� ���������������� ���� � ���������� ��� �� ������.
	 * ������� ��������� ������ � ��������������� �����, ��� ������������ ������������.
	 */
	@FXML
	public void choiceStatus() {
		
		StatusChoiceItem item = statusChoiceBox.getSelectionModel().getSelectedItem();
		
		try {
			manager.getConnection().sendStatus(item.getStatusId());
			
			currentStatusLabel.setText(item.getStatus());
			
		} catch (ConnectionIsNotAlive e) {
			LogManager.getLogger().error("Exception occured on sending status", e);
		}
	}

	/**
	 * ������������� ���� ����� �������, �������� ��� ���������� ����������, ����������������
	 * {@code com.teetov.chat.message.StatusList.getAvailebelStatuses()}. 
	 */
	private void prepereChoiceBox() {
		Map<Integer, String> rawStatusmap = StatusList.getAvailebelStatuses();

		List<StatusChoiceItem> items = new ArrayList<>();

		for(Map.Entry<Integer, String> entry : rawStatusmap.entrySet() ) {
			items.add(new StatusChoiceItem(entry.getKey(), entry.getValue()));
		}

		statusChoiceBox.getItems().addAll(items);
	}

	/**
	 * ������ ����� �� ���� ����� � ������� ��� {@code Connection.send()} ��� �������� �� ������.
	 * ����� ������� ���� �����, � ����� �������� ������� ��� �����,
	 *  ���� �� ��� ���������.
	 */
	@FXML
	public void readAndSend() {
		if(!manager.hasAliveConnection()) {
			LogManager.getLogger().warn("Connection dead or not exist");
			return;
		}

		Connection connection = manager.getConnection();
		
		String text = readInputArea();
		clearInputArea();

		inputArea.requestFocus();
		
		try {
			connection.send(text);
		} catch (ConnectionIsNotAlive e) {
			LogManager.getLogger().warn("Exception occured on sending text", e);
		}
		
	}
	
	private String readInputArea() {
		String text = inputArea.getText();
		return text;
	}

	private void clearInputArea() {
		inputArea.setText("");
	}

	/**
	 * ������ ���������� ������ ����� ��������� � ����������� � ��� ������.
	 * @param disable {@code true}, ����� ��������� ������ �����. 
	 */
	public void disableInputPanel(boolean desable) {
		inputPanel.setDisable(desable);
		statusChoicePane.setDisable(desable);
	}

	/**
	 * ���������� ������� ������ ����������� �������� ������������� �� � �����������.
	 */
	public void clearStatusList() {
		statusesVBox.getChildren().clear();
	}

	/**
	 * ������ ���������� ������ ������ ��������.
	 * @param disable
	 */
	public void disableStatusPane(boolean disable)  {
		statusChoicePane.disableProperty().set(disable);
	}
	
	/**
	 * ��������� ����������, ����������� ������������ ������� ���������� ������������.
	 * @param controller
	 */
	public void addStatusPaneController(StatusPaneController controller) {
		statusPanes.add(controller);
	}
	
	/**
	 * ������� ����������, ����������� ������������ ������� ���������� ������������.
	 * @param controller
	 */
	public void removeStatusPaneController(StatusPaneController controller) {
		statusPanes.remove(controller);
	}
	
	@Override
	public void onActiveateConnection() {
		disableStatusPane(true);
		messageBox.getChildren().clear();
	}

	@Override
	public void onAcceptAccessConnection() {	
		disableInputPanel(false);
	}

	@Override
	public void onDeactiveateConnection() {
		disableInputPanel(true);
		disableStatusPane(true);
		clearStatusList();
	}

}
