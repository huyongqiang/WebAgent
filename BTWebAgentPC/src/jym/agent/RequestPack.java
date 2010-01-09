// CatfoOD 2009-10-3 上午10:27:39

package jym.agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

import trayicon.SystemIcon;

public class RequestPack {
	
	/** 
	 * 输出缓冲的大小，严重影响速度
	 * [缓冲]	[速度]
	 * 256		10K
	 * 2048		20K
	 * 20480	30K
	 * 32768	40K
	 **/
	public final int BUFF_LENGTH = 2048 * 16;
	private final byte[] line = "\r\n".getBytes();
	
	private Socket sk;
	private OutputStream out;
	private InputStream in;
	
	private String uri;
	private byte[] body;
	private String method;
	private HttpParse hp;
	
	public RequestPack(Socket s) throws IOException {
		sk = s;
		in  = sk.getInputStream();
		out = sk.getOutputStream();
		read();
	}
	
	public boolean isClosed() {
		return !sk.isConnected();
	}
	
	private void read() throws IOException {
		hp = new HttpParse(in);
		uri 	= hp.getRequestURI();
		uri		= HttpParse.encodeURI(uri);
		body	= hp.getBody();
		method	= hp.getMethod();
		//Tools.pl("<Request>\n"+hp.getHead()+"</Request>");
	}
	
	public void writeRequestProperty(DataOutputStream out) throws IOException {
		out.writeUTF( getURI() );
		out.writeUTF( getMethod() );
		
		Map<String, String> map = hp.getHeads();
		irregularity(map);
		out.writeInt( map.size() );
		Iterator<String> itr = map.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String value = map.get(key);
			out.writeUTF(key);
			out.writeUTF(value);
			//Tools.pl(key+":"+value);
		}

		out.writeInt(body.length);
		out.write(body);
		out.flush();
		
		SystemIcon.up(body.length);
		SystemIcon.up(hp.getRequestHead().getBytes().length);
	}
	
	private void irregularity(Map<String, String> heads) {
//		heads.put("Connection", "close");
//		heads.put("User-Agent", "Mozilla/4.0");
		heads.remove("Host");
	}
	
	public void write(DataInputStream in, DataOutputStream bout) throws IOException {
		final String END = "end";
		final String DATA = "data";
		
		String responseHead = in.readUTF();
		String msg = "next";
		boolean end = false;
		//Tools.pl("<Response>\n"+responseHead+"</Response>");
		
		try {
			out.write(responseHead.getBytes());
			out.write(line);
		} catch (Exception e) {
			msg = "error";
			end = true;
		}
		
		bout.writeInt(BUFF_LENGTH);
		bout.writeUTF(msg);
		bout.flush();
		
		int totalbytes = 0;
		int len = 0;
		byte[] buff = new byte[BUFF_LENGTH];
		
		while (!end) {
			in.readFully(buff);
			String ctrl = in.readUTF();
			
			if (ctrl.startsWith(DATA)) {
				len = buff.length;
			} else if (ctrl.startsWith(END)) {
				len = Integer.parseInt(
						ctrl.substring(END.length(), ctrl.length()) );
				end = true;
			}
			totalbytes+=len;
			
			try {
				out.write(buff, 0, len);
				msg = ("next");
			} catch (Exception e) {
				msg = ("end");
			}
			
			if (!end) {
				bout.writeUTF(msg);
				bout.flush();
			}
		}
		
		Tools.pl("#内容长度:" + this + responseHead.length()+ "+" + totalbytes);
		SystemIcon.down(totalbytes);
		SystemIcon.down(responseHead.getBytes().length);
		try {
			out.flush();
		} catch (Exception e) {}
	}
	
	public void close() {
		try {
			sk.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 * 返回已经编码的uri
	 */
	public String getURI() {
		return uri;
	}
	
	public byte[] getBody() {
		return body;
	}
	
	private String getMethod() {
		if (method==null || method.startsWith("CONNECT")) {
			method = "GET";
		}		
		return method;
	}
	
	public String toString() {
		if (display==null) {
			int c = Math.abs(uri.hashCode());
			String s = Integer.toHexString(c);
			display = "("+ s +")";
		}
		return display;
	}
	
	private String display = null;
}
