// CatfoOD 2010-1-9 上午09:30:07

package jym.bt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutStreamCreater {
	
	private OutputStream out;
	
	/**
	 * 对out的操作进行同步
	 */
	public OutStreamCreater(OutputStream out) {
		this.out = out;
	}
	
	/**
	 * 对返回的DataOutputStream的操作集合进行同步
	 */
	public DataOutputStream openDataOutputStream() {
		return new SyncDataOutputStream( new DataBuffer() );
	}
	
	private class SyncDataOutputStream extends DataOutputStream {

		public SyncDataOutputStream(OutputStream out) {
			super(out);
		}
	}
	
	private class DataBuffer extends OutputStream {
		private CharBuffer buffer = new CharBuffer(512);

		public void write(int d) throws IOException {
			buffer.append(d);
		}

		public void write(byte[] b) throws IOException {
			buffer.append(b);
		}

		public void close() throws IOException {
			super.close();
		}

		public void flush() throws IOException {
			byte[] data = buffer.toArray();
			synchronized (out) {
				out.write(data);
				out.flush();
			}
			buffer.delete(false);
		}
	}
}
