// CatfoOD 2009-10-9 上午09:03:29

package jym.bt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 蓝牙连接协议实现框架
 */
abstract class BTLink implements IBlueCtrl {
	
	private IBlue blue;
	protected DataInputStream in;
	private OutStreamCreater outcreater;
	private IBlueListener listener;
	private Hashtable logics;
	private boolean runing;
	
	BTLink(IBlue b) throws IOException {
		blue = b;
		blue.start();
		in = blue.getDataInputStream();
		outcreater = new OutStreamCreater(blue.getOutputStream());
		logics = new Hashtable();
		runing = false;
	}
	
	/**
	 * 开始服务，此方法阻塞线程，直到连接被关闭或出现异常
	 * @throws IOException 
	 */
	public void start() throws IOException {
		if (listener==null) {
			throw new BlueToothException("没有设置蓝牙事件监听器");
		}
		try {
			runing = true;
			while (runing) {
				int cmd = pretreatment();
				progress(cmd);
			}
		} finally {
			close();
		}
	}
	
	public void registerBlueListener(IBlueListener listener) {
		this.listener = listener;
	}
	
	/**
	 * 返回蓝牙物理连接数据输出流，输出流的操作是线程安全的
	 */
	protected DataOutputStream getOutStream() {
		return outcreater.openDataOutputStream();
	}
	
	private int pretreatment() throws IOException {
		int cmd = in.readInt();

		switch (cmd) {
			case CREAT: {
				creatLink();
				break;
			}
			
			case DATA: {
				sendData();
				break;
			}
			
			case CLOSE: {
				closeLogic();
				break;
			}
			
			case EXIT: {
				close();
				break;
			}
		}
		return cmd;
	}
	
	/**
	 * 蓝牙连接处理器，扩展操作<br>
	 * 如果抛出异常当前线程停止，并且关闭蓝牙物理连接
	 * @param cmd - 操作代码
	 * @throws IOException
	 */
	protected void progress(int cmd) throws IOException {
	}

	/**
	 * 物理连接接收逻辑连接发送来的数据
	 * @param log - 逻辑连接
	 * @param data - 数据
	 * @throws IOException 
	 */
	protected void recvData(LogicConnect log, byte[] data) throws IOException {
		DataOutputStream out = getOutStream();
		out.writeInt(DATA);
		out.writeInt(log.getID());
		out.writeInt(data.length);
		out.write(data);
		out.flush();
	}
	
	/**
	 * 物理连接向逻辑连接发送数据
	 * @throws IOException 
	 */
	private void sendData() throws IOException {
		int id = in.readInt();
		LogicConnect log = (LogicConnect) logics.get(new Integer(id));
		if (log!=null) {
			int len = in.readInt();
			byte[] data = new byte[len];
			in.readFully(data);
			log.recvData(data);
		}
	}
	
	private void closeLogic() throws IOException {
		int id = in.readInt();
		Object logid = new Integer(id);
		LogicConnect log = (LogicConnect) logics.get(logid);
		if (log!=null) {
			log.close();
		}
	}
	
	/**
	 * 把逻辑连接移出协议栈
	 */
	protected void closeLogic(int id) throws IOException {
		Integer logid = new Integer(id);
		if (logics.remove(logid)!=null) {
			DataOutputStream out = getOutStream();
			out.writeInt(CLOSE);
			out.writeInt(id);
			out.flush();
		}
	}
	
	/**
	 * 建立新的逻辑连接的过程，被BTLink内部调用
	 */
	protected abstract void creatLink() throws IOException;
	
	/**
	 * 根据id创建一个新的逻辑连接，并通知监听器
	 */
	protected void creatAndSaveLogicLink(int id) {
		LogicConnect lc = new LogicConnect(id, this);
		Integer ids = new Integer(id);
		logics.put(ids, lc);
		notifyListener(lc);
	}
	
	private void notifyListener(final IBlue blue) {
		new Thread() {
			public void run() {
				listener.newlink(blue);
			}
		}.start();
	}
	
	/**
	 * 关闭蓝牙物理连接
	 */
	public void close() {
		if (runing) {
			runing = false;
			
			Enumeration values = logics.elements();
			while (values.hasMoreElements()) {
				LogicConnect log = (LogicConnect) values.nextElement();
				log.close();
			}
			logics.clear();
			
			try {
				DataOutputStream out = getOutStream();
				out.writeInt(EXIT);
				out.flush();
			} catch (IOException e) {}

			blue.close();
		}
	}
}
