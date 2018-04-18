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
	 * Максимальное количество одновременно отображающихся сообщений.
	 */
	private final int MAX_MESSAGES = 1000;

	/**
	 * Множество контроллеров, управляющих отображением статуса конкретного пользователя
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
	 * Скрывать статусы добавляющихся пользователей
	 */
	private boolean hideStatus = false;

	public ChatPanelController() {}

	public void setStageManager(StageManager mainStage) {
		this.manager = mainStage;
	}
	
	/**
	 * Добавляет текст нового сообщения на панель сообщений
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
	 * Закрывает текущее соединение.
	 */
	@FXML
	public void closeConnection() {
		manager.terminateConnection();
	}

	/**
	 * Предоставляет доступ к контейнеру, в котором отбражаются статусы пользователей.
	 * @return
	 */
	public VBox getStatusesVBox() {
		return statusesVBox;
	}

	/**
	 * Переключает режим отображения статусов (свернуть/развернуть). И отображает в соответствии с выбором.
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
	 * Информирует о текущем режиме отображения статусов (свернутые/развернутые).
	 * @return
	 */
	public boolean isHidingStatuses() {
		return hideStatus;
	}
	
	/**
	 * Считывает выбранный в данный момент статус из соответствующего меню и отправляет его на сервер.
	 * Выводит выбранный статус в соответствующее место, для демонстрации пользователю.
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
	 * Подготаливает меню выбра статуса, заполняя его доступными вариантами, предоставленными
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
	 * Читает текст из поля ввода и передаёт его {@code Connection.send()} для отправки на сервер.
	 * Метод очищает поле ввода, а затем пытается вернуть ему фокус,
	 *  если он был перемещён.
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
	 * Делает неактивной панель ввода сообщения и относящееся к ней кнопки.
	 * @param disable {@code true}, чтобы отключить панель ввода. 
	 */
	public void disableInputPanel(boolean desable) {
		inputPanel.setDisable(desable);
		statusChoicePane.setDisable(desable);
	}

	/**
	 * Польностью очищает панель отображения статусов пользователей от её содержимого.
	 */
	public void clearStatusList() {
		statusesVBox.getChildren().clear();
	}

	/**
	 * Делает неактивной панель выбора статусов.
	 * @param disable
	 */
	public void disableStatusPane(boolean disable)  {
		statusChoicePane.disableProperty().set(disable);
	}
	
	/**
	 * Добавляет контроллер, управляющий отображением статуса конретного пользователя.
	 * @param controller
	 */
	public void addStatusPaneController(StatusPaneController controller) {
		statusPanes.add(controller);
	}
	
	/**
	 * Удаляет контроллер, управляющий отображением статуса конретного пользователя.
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
