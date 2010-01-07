// CatfoOD 2009-10-9 ����08:55:03

package jym.bt;

import java.io.IOException;

/**
 * ���������������߼����ӵ�����
 */
public interface IBlueCtrl {
	/** ��ͨ���� */
	final int DATA		= 1;
	/** �ر��߼����� */
	final int CLOSE		= 2;
	/** �½����� */
	final int CREAT		= 3;
	/** �ر��������� */
	final int EXIT		= 6;
	final int OK		= 4;
	final int ERROR		= 5;
	
	
	/**
	 * ��ʼ���񣬴˷��������̣߳�ֱ�����ӱ��رջ�����쳣
	 * @throws IOException 
	 */
	void start() throws IOException;
	
	/**
	 * �ر�������������
	 */
	public void close();
	
	/**
	 * ���󴴽��µ��߼����ӣ��µ�����ͨ��IBlueListener�ӿڴ���
	 * @throws IOException - �����������֧�ִ˷���������������
	 */
	public void newLink() throws IOException;
	
	/**
	 * ע�������¼�������
	 * @param listener - �½���������ͨ������������͵��ⲿ
	 */
	public void registerBlueListener(IBlueListener listener);
}
