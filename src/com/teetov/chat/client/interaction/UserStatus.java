package com.teetov.chat.client.interaction;

/**
 * Класс обеспечивает взаимодействие между системой приёма-отправки сообщенией ({@code Connection}) и 
 * системой отображения статусов активных в данный момент пользавателей.
 * Каждый объект этого класса представляет собой конкретного пользователя.
 * Создание нового экземпляра должно служить сигналом пользовательскому интерфейсу к началу отображения,
 * ассоциированного с ним пользователя, как активного.
 * Система приёма сообщенией может управлять его отображением (изменение стаутуса, выход из сети) через методы данного интерфейса.
 * @author  Aleksey Titov
 *
 */
public interface UserStatus {
    
    /**
     * Изменяет отображение текущего статуса
     * @param newStatus новое статусное собщение
     */
    void setNewStatus(String newStatus);
    
    /**
     * Команда прекратить отображение пользователя и его статуса в списке активных в данный клиентов.
     */
    void exit();
}
