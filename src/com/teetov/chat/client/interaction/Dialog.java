package com.teetov.chat.client.interaction;

import com.teetov.chat.message.Message;

/**
 * Обеспечивает взаимодействие между пользователем и системой прёма-отправки сообщений ({@code Connection}).
 * 
 * @author  Aleksey Titov
 */
public interface Dialog {
    
    /**
     * Запрашивает у пользователя ввод конкретной информации.
     * 
     * @param info пояснение для пользователя, что от него хотят получить
     * @return введённый пользователем текст. {@code null} в случае, если пользователь отказался отвечать на запрос.
     */
    String retrieveInfo(String info);
    
    /**
     * Вывод информации на экран. Служит для информирования, без обратной связи.
     * 
     * @param info
     */
    void showInfo(String info);
    
    /**
     * Печатает полученное сообщение в текстовое поле чата.
     * 
     * @param message
     */
    void printMessage(Message message);
    
    /**
     * Возвращает объект, ассоциированный с представлением статуса для конкретного пользователя.
     * Дальнейшее отображение изменеий статуса пользователя слудует осуществлять через этот объект.
     *  
     * @param userName имя пользователя, статус которого будет отображаться
     * @return объект для взаимодействия с представлением статуса
     */
    UserStatus getUserStatus(String userName);
}
