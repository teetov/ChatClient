package com.teetov.chat.client.interaction;


/**
 * ���������� �������� ��������� ������ ��� �������, �������������� 
 * �������������� ����� ����������� � ��������
 * ({@code Connection}) � ���������������� �����������.
 * @author  Aleksey Titov
 *
 */
public interface ConnectionController {
	PhaseListener getPhaseListener();
	
	Dialog getDialog();
}
