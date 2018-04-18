package com.teetov.chat.client.interaction;


/**
 * Интерфейст содержит фабричные методы для классов, обеспечивающих 
 * взаимодействие между соединением с сервером
 * ({@code Connection}) и пользовательским интерфейсом.
 * @author  Aleksey Titov
 *
 */
public interface ConnectionController {
	PhaseListener getPhaseListener();
	
	Dialog getDialog();
}
