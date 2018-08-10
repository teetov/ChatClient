    package com.teetov.chat.client.interaction;

/**
 * »нтерфейс содержит методы, вызываемые в различные периоды жизненного цикла класса ({@code Connection}).
 * —оответствующие методы выполн¤ютс¤ после установки соединени¤, получени¤ доступа и завершени¤.
 * 
 * @author  Aleksey Titov
 */
public interface PhaseListener {
    
    /**
     * {@code Connection} вызывает метод сразу поле устаноки соединени¤ с сервером.
     */
    void started();

    /**
     * {@code Connection} вызывает метод сразу поле получени¤ доступа 
     * к обмену сообщени¤ми с другими пользовател¤ми.
     */
    void accessed();

    /**
     * {@code Connection} вызывает метод сразу поле отключени¤ от сервера.
     */
    void terminated();
    
}
