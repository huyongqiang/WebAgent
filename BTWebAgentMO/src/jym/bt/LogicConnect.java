// CatfoOD 2009-10-9 上午09:45:45

package jym.bt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LogicConnect implements IBlue {
	/** 输入流阻塞循环的响应时间 */
	public static final int WAIT_TIME = 200;
	
	private int id;
	private BTLink physics;
	
	private boolean connclosed;
	private INS in;
	private OUTS out;
	private DataInputStream din;
	private DataOutputStream dout;
	
	LogicConnect(int id, BTLink link) {
		this.id = id;
		physics = link;
		in = new INS();
		out = new OUTS();
		connclosed = false;
	}
	
	protected void recvData(byte[] data) {
		in.recvData(data);
	}
	
	protected int getID() {
		return id;
	}

	public void close() {
		if (!connclosed) {
			connclosed = true;
			try {
				in.close();
			} catch (IOException e) {}
			try {
				out.close();
			} catch (IOException e) {}
			try {
				physics.closeLogic(id);
			} catch (IOException e) {}
		}
	}

	public DataInputStream getDataInputStream() throws IOException {
		if (din==null) {
			din = new DataInputStream(in);
		}
		return din;
	}

	public DataOutputStream getDataOutputStream() throws IOException {
		if (dout==null) {
			dout = new DataOutputStream(out);
		}
		return dout;
	}

	public InputStream getInputStream() throws IOException {
		return in;
	}

	public OutputStream getOutputStream() throws IOException {
		return out;
	}

	public void start() throws IOException {
	}
	

	private class INS extends InputStream {
		private Queue datas;
		private byte[] buff;
		private int point;
		private boolean closed;
		
		private INS() {
			datas = new Queue();
			buff = null;
			point = 0;
			closed = false;
		}
		
		private void recvData(byte[] data) {
			datas.add(data);
		}
		
		private void getNextData() {
			if ( buff==null || point>=buff.length ) {
				while (datas.isEmpty()) {
					try {
						Thread.sleep(WAIT_TIME);
					} catch (InterruptedException e) {}
				}
				buff = (byte[]) datas.first();
				point = 0;
			}
		}
		
		public int read() throws IOException {
			check();
			getNextData();
			return toInt(buff[point++]);
		}
		
		private int toInt(byte b){
			return b<0? 256+b: b;
		}
		
		public void close() throws IOException {
			closed = true;
		}
		
		private void check() throws BlueToothException {
			if (closed) {
				throw new BlueToothException("蓝牙输入已关闭");
			}
		}
	}
	
	
	private class OUTS extends OutputStream {
		private CharBuffer buff;
		private boolean closed;
		
		private OUTS() {
			buff = new CharBuffer(1000);
			closed = false;
		}
		
		public void write(int d) throws IOException {
			check();
			buff.append(d);
		}

		public void flush() throws IOException {
			check();
			byte[] data = buff.toArray();
			buff.delete(false);
			physics.recvData(LogicConnect.this, data);
		}
		
		public void close() throws IOException {
			closed = true;
		}
		
		private void check() throws BlueToothException {
			if (closed) {
				throw new BlueToothException("蓝牙输出已关闭");
			}
		}
	}
}
