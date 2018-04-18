	package com.teetov.chat.client.interaction;

/**
 * ��������� �������� ������, ���������� � ��������� ������� ���������� ����� ������ ({@code Connection}).
 * ��������������� ������ ����������� ����� ��������� ����������, ��������� ������� � ����������.
 * 
 * @author  Aleksey Titov
 */
public interface PhaseListener {
	
	/**
	 * {@code Connection} �������� ����� ����� ���� �������� ���������� � ��������.
	 */
	void started();

	/**
	 * {@code Connection} �������� ����� ����� ���� ��������� ������� 
	 * � ������ ����������� � ������� ��������������.
	 */
	void accessed();

	/**
	 * {@code Connection} �������� ����� ����� ���� ���������� �� �������.
	 */
	void terminated();
	
}
