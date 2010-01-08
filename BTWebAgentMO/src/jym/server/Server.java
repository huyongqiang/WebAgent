// CatfoOD 2009-10-3 下午01:03:59

package jym.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import jym.bt.BTLinkService;
import jym.bt.BlueToothException;
import jym.bt.IBlue;
import jym.bt.IBlueCtrl;
import jym.bt.IBlueListener;
import jym.mid.Mainc;

public class Server implements IBlueListener {
	
	private boolean stop = false;
	private boolean supportAgent = true;
	
	public Server() {
		new Thread() {
			public void run() {
				while (!stop) {
					try {
						waitLink();
					} catch (Exception e) {
						Mainc.pl("丢失连接:"+e.getMessage());
					}
				}
			}
		}.start();
	}
	
	public void waitLink() throws IOException {
		while (!stop) {
			Mainc.pl("[启动蓝牙监听]");
			IBlueCtrl blue = new BTLinkService();
			blue.registerBlueListener(this);
			Mainc.pl("[蓝牙物理连接已建立]");
			blue.start();
			Mainc.pl("[蓝牙连接已断开]");
		}
	}
	
	public void newlink(IBlue blue) {
		Request req = null;
		
		try {
			req = new Request(blue);
			req.request();
			req.responsion();
			
		} catch (BlueToothException e) {
			Mainc.pl("请求关闭:["+e+"]");
			
		} catch (IOException e) {
			String em = "网络错误-["+e+"]";
			Mainc.pl(em);
			
			try {
				DataOutputStream out = blue.getDataOutputStream();
				out.writeUTF("error:"+em);
				out.flush();
			} catch (Exception e2) {}
			
		} finally {
			if (req!=null)
				req.close();
		}
	}
	
	private class Request {
		private HttpConnection con;
		private IBlue blue;
		
		private String method;
		private byte[] body;
		private String url;
		
		private Request(IBlue b) {
			blue = b;
		}
		
		public void request() throws IOException {
			Hashtable map = new Hashtable();
			
			DataInputStream in = blue.getDataInputStream();
			url		= in.readUTF();
			method	= in.readUTF();
			
			int headcount = in.readInt();
			for (int i=0; i<headcount; ++i) {
				String key = in.readUTF();
				String value = in.readUTF();
				map.put(key, value);
			}
			
			int len	= in.readInt();
			body	= new byte[len];
			in.readFully(body);
			
			con = creatHttpLink(url);
			setRequestProperty(map);
		}
		
		/**
		 * in的读取顺序很重要!
		 */
		private void setRequestProperty(Hashtable map) throws IOException {
			con.setRequestMethod(method);
			//Mainc.pl(con.getRequestProperty(""));
			
			Enumeration keys = map.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = (String) map.get(key);

				if (value.length()>0) {
					con.setRequestProperty(key, value);
				}
			}
			
			if (body!=null && body.length>0) {
				OutputStream out = con.openOutputStream();
				out.write(body);
				out.flush();
				out.close();
			}
		}
		
		/**
		 * 应答数据流定义：
		 *  frame = datas + ctrl;
		 *  datas = 15bytes;
		 *  ctrl = string;
		 *  string = cmd:length;
		 *  cmd = ('data'|'end')
		 *  length = integer; 
		 * @throws IOException
		 */
		private void responsion() throws IOException {
			InputStream in = con.openInputStream();
			DataInputStream bin = blue.getDataInputStream();
		 try {
			DataOutputStream out;
			int len;
			try {
				out = blue.getDataOutputStream();
				out.writeUTF("ok");
				out.writeUTF(getResponseHead());
				out.flush();
				
				len = getConntentLength();
				if (len<0) len = (int) con.getLength();
				if (len<0) len = Integer.MAX_VALUE;
			} catch(IOException e) {
				throw new BlueToothException(e);
			}
			
			int buffLength = bin.readInt();
			
			if (nextStep(bin)) {
				int d = 0;
				int ctrl = 0;
				for (int i=0; i<len; ++i) {
					try {
						d = in.read(); 
					} catch (IOException e) {
						d = -1;
					}
					if (d<0) break;				
					out.write(d);
					
					if (ctrl==buffLength-1) {
						out.writeUTF("data");
						out.flush();
						ctrl = 0;
						
						if (!nextStep(bin)) {
							break;
						}
					} else {
						ctrl++;
					}
				}
				
				for (int i=0; i < buffLength-ctrl; ++i) {
					out.write(0);
				}
				out.writeUTF("end"+ctrl);
				out.flush();
			}
		 } finally {
			 in.close();
		 }
		}
		
		private boolean nextStep(DataInputStream in) throws IOException {
			String msg = in.readUTF();
			if (msg.startsWith("next")) {
				return true;
			}
			return false;
		}
		
		private String getResponseHead() throws IOException {
			final String line = "\r\n";
			
			StringBuffer buff = new StringBuffer();
			
			buff.append("HTTP/1.1 ");
			buff.append(con.getResponseCode());
			buff.append(' ');
			buff.append(con.getResponseMessage());
			buff.append(line);
			
			for (int i=0; i<30; ++i) {
				String headname = con.getHeaderFieldKey(i);
				String headvalue= con.getHeaderField(i);
				if (headname==null) break;
				buff.append(headname);
				buff.append(":");
				buff.append(headvalue);
				buff.append(line);
			}
			return buff.toString();
		}
		
		private int getConntentLength() throws IOException {
			String contentLength = con.getHeaderField("content-length");
			int len = -1;
			try {
				len = Integer.parseInt(contentLength);
			} catch (NumberFormatException e) {}
			return len;
		}
		
		private void close() {
			try {
				if (con!=null) {
					con.close();
				}
			} catch (IOException e) {
			}
//			blue.close();
		}
	}
	
	protected HttpConnection creatHttpLink(String url) throws IOException {
		HttpConnection httpconn = null;
		
		if (supportAgent) {
			try {
				httpconn = new HttpAgentConnection(url);
			} catch (SecurityException e) {
				Mainc.pl("直接代理不被支持:" + e);
				supportAgent = false;
			} catch (IOException e) {
				Mainc.pl("直接代理错误:" + e.toString());
			}
		}
		
		try {
			httpconn = (HttpConnection) 
				Connector.open(url, Connector.READ_WRITE, true);
		} catch (Exception e) {
			throw new IOException("打开URL错误:" + e.getMessage());
		}
		
		return httpconn;
	}
	
}
