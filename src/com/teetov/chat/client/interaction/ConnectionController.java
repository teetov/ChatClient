package com.teetov.chat.client.interaction;


/**
 * Интерфейс обязует реализовавший его класс предоставить методы, 
 * необходимые для организации взаимодействия межды 
 * ({@code Connection}) и GUI.
 * @author  Aleksey Titov
 *
 */
public interface ConnectionController {
    PhaseListener getPhaseListener();
    
    Dialog getDialog();
}
