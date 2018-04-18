package com.teetov.chat.client.interaction;

import com.teetov.chat.message.Message;

/**
 * ������������ �������������� ����� ������������� � �������� ����-�������� ��������� ({@code Connection}).
 * 
 * @author  Aleksey Titov
 */
public interface Dialog {
	
	/**
	 * ����������� � ������������ ���� ���������� ����������.
	 * 
	 * @param info ��������� ��� ������������, ��� �� ���� ����� ��������
	 * @return �������� ������������� �����. {@code null} � ������, ���� ������������ ��������� �������� �� ������.
	 */
	String retrieveInfo(String info);
	
	/**
	 * ����� ���������� �� �����. ������ ��� ��������������, ��� �������� �����.
	 * 
	 * @param info
	 */
	void showInfo(String info);
	
	/**
	 * �������� ���������� ��������� � ��������� ���� ����.
	 * 
	 * @param message
	 */
	void printMessage(Message message);
	
	/**
	 * ���������� ������, ��������������� � �������������� ������� ��� ����������� ������������.
	 * ���������� ����������� �������� ������� ������������ ������� ������������ ����� ���� ������.
	 *  
	 * @param userName ��� ������������, ������ �������� ����� ������������
	 * @return ������ ��� �������������� � �������������� �������
	 */
	UserStatus getUserStatus(String userName);
}
