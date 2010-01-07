// CatfoOD 2009-10-9 上午08:38:54

package jym.bt;

/**
 * 当蓝牙服务端接收到新建连接的请求后<br>
 * 新建立的连接被发送到这里
 */
public interface IBlueListener {
	/**
	 * 新的蓝牙逻辑连接被建立,<br>
	 * 这个方法已经在新的线程中启动
	 * @param blue - 一个蓝牙逻辑连接
	 */
	void newlink(IBlue blue);
}
