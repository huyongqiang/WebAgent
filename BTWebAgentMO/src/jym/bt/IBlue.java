// CatfoOD 2009-10-3 上午08:41:24

package jym.bt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 蓝牙服务器接口
 */
public interface IBlue {
	/**
	 * 启动服务，在连接成功或失败前阻塞线程
	 * @throws IOException
	 */
	void start() throws IOException;
	
	void close();
	
	DataInputStream getDataInputStream() throws IOException;
	
	DataOutputStream getDataOutputStream() throws IOException;
	
	InputStream getInputStream() throws IOException;
	
	OutputStream getOutputStream() throws IOException;
}
