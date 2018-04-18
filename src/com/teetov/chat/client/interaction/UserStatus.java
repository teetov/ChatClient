package com.teetov.chat.client.interaction;

/**
 * ����� ������������ �������������� ����� �������� �����-�������� ���������� ({@code Connection}) � 
 * �������� ����������� �������� �������� � ������ ������ �������������.
 * ������ ������ ����� ������ ������������ ����� ����������� ������������.
 * �������� ������ ���������� ������ ������� �������� ����������������� ���������� � ������ �����������,
 * ���������������� � ��� ������������, ��� ���������.
 * ������� ����� ���������� ����� ��������� ��� ������������ (��������� ��������, ����� �� ����) ����� ������ ������� ����������.
 * @author  Aleksey Titov
 *
 */
public interface UserStatus {
	
	/**
	 * �������� ����������� �������� �������
	 * @param newStatus ����� ��������� ��������
	 */
	void setNewStatus(String newStatus);
	
	/**
	 * ������� ���������� ����������� ������������ � ��� ������� � ������ �������� � ������ ��������.
	 */
	void exit();
}
