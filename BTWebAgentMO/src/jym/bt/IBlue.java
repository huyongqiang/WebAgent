// CatfoOD 2009-10-3 ����08:41:24

package jym.bt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * �����������ӿ�
 */
public interface IBlue {
	/**
	 * �������������ӳɹ���ʧ��ǰ�����߳�
	 * @throws IOException
	 */
	void start() throws IOException;
	
	void close();
	
	DataInputStream getDataInputStream() throws IOException;
	
	DataOutputStream getDataOutputStream() throws IOException;
	
	InputStream getInputStream() throws IOException;
	
	OutputStream getOutputStream() throws IOException;
}
