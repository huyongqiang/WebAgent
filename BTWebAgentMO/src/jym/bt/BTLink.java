// CatfoOD 2009-10-9 ����09:03:29

package jym.bt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * ��������Э��ʵ�ֿ��
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
	 * ��ʼ���񣬴˷��������̣߳�ֱ�����ӱ��رջ�����쳣
	 * @throws IOException 
	 */
	public void start() throws IOException {
		if (listener==null) {
			throw new BlueToothException("û�����������¼�������");
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
	 * ���������������������������������Ĳ������̰߳�ȫ��
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
	 * �������Ӵ���������չ����<br>
	 * ����׳��쳣��ǰ�߳�ֹͣ�����ҹر�������������
	 * @param cmd - ��������
	 * @throws IOException
	 */
	protected void progress(int cmd) throws IOException {
	}

	/**
	 * �������ӽ����߼����ӷ�����������
	 * @param log - �߼�����
	 * @param data - ����
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
	 * �����������߼����ӷ�������
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
	 * ���߼������Ƴ�Э��ջ
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
	 * �����µ��߼����ӵĹ��̣���BTLink�ڲ�����
	 */
	protected abstract void creatLink() throws IOException;
	
	/**
	 * ����id����һ���µ��߼����ӣ���֪ͨ������
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
	 * �ر�������������
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
