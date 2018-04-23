package com.teetov.chat.client.control;

/**
 *  Класс предназначен для использования в меню выбора текущего статуса.
 * Объединяет в себе его id и текстовое описание.
 * @author  Aleksey Titov
 *
 */
public class StatusChoiceItem {
	private Integer id;
	private String status;
	
	public Integer getStatusId() {
		return id;
	}
	public String getStatus() {
		return status;
	}
	public StatusChoiceItem(Integer id, String status) {
		super();
		this.id = id;
		this.status = status;
	}
	
	@Override
	public String toString() {
		return status;
	}
}
