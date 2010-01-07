// CatfoOD 2009-10-9 上午08:55:03

package jym.bt;

import java.io.IOException;

/**
 * 蓝牙物理连接与逻辑连接的桥梁
 */
public interface IBlueCtrl {
	/** 普通数据 */
	final int DATA		= 1;
	/** 关闭逻辑连接 */
	final int CLOSE		= 2;
	/** 新建连接 */
	final int CREAT		= 3;
	/** 关闭物理连接 */
	final int EXIT		= 6;
	final int OK		= 4;
	final int ERROR		= 5;
	
	
	/**
	 * 开始服务，此方法阻塞线程，直到连接被关闭或出现异常
	 * @throws IOException 
	 */
	void start() throws IOException;
	
	/**
	 * 关闭蓝牙物理连接
	 */
	public void close();
	
	/**
	 * 请求创建新的逻辑连接，新的连接通过IBlueListener接口传送
	 * @throws IOException - 如果服务器不支持此方法，或其他错误
	 */
	public void newLink() throws IOException;
	
	/**
	 * 注册蓝牙事件监听器
	 * @param listener - 新建立的连接通过这个方法发送到外部
	 */
	public void registerBlueListener(IBlueListener listener);
}
