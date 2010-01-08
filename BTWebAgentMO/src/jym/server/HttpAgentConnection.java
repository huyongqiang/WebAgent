// CatfoOD 2009-10-7 上午07:32:19

package jym.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SocketConnection;

import jym.bt.CharBuffer;

/**
 * Http代理类，请求时无任何默认头域
 */
public class HttpAgentConnection implements HttpConnection {
	private final int SETUP = 1;
	private final int CONNECTED = 2;
	private final int CLOSED = 3;
	
	private SocketConnection m_sc;
	private Hashtable request;
	private Hashtable response;
	private Vector responseKeys;
	private InputStream responseData;
	private int state;
	private int port = 80;
	private String host;
	private String method;
	private String uri;
	private String responseMethod;
	private CharBuffer outbuffer;
	
	public HttpAgentConnection(String url) throws IOException {
		uri = url;
		creatSocket();
		init();
	}
	
	private void creatSocket() throws IOException {
		String url = uri;
		try {
			final String http = "http://";
			if (url.startsWith(http)) {
				url = url.substring(http.length()+1);
			} else {
				int s = 0;
				while (url.charAt(s)=='/') s++;
				url = url.substring(s);
			}
			
			int e = url.indexOf('/');
			if (e>0) {
				url = url.substring(0, e);
			}
			
			int sf = url.indexOf(':');
			if (sf<0) {
				sf = url.length();
			} else {
				String p = url.substring(sf+1, url.length());
				port = Integer.parseInt(p);
			}
			host = url.substring(0, sf);
		} catch (Exception e) {
			throw new IOException("url无效:"+url);
		}

		m_sc = (SocketConnection) Connector.open("socket://"+ host +":"+ port);
	}
	
	private void init() {
		state = SETUP;
		request = new Hashtable();
		response = new Hashtable();
		responseKeys = new Vector();
		method = "GET";
		outbuffer = new CharBuffer();
	}

	/**
	 * 只要参数不为空(0长度,null)<br>
	 * 不检查参数的有效性
	 */
	public void setRequestMethod(String s) throws IOException {
		expectationState(SETUP);
		
		if (s==null || s.length()<=0) {
			throw new IOException("无效的请求方法:"+s);
		}
		method = s;
	}

	/**
	 * 设置请求头域，如果v为空(0长度,null)则清除指定的头域
	 */
	public void setRequestProperty(String p, String v) throws IOException {
		expectationState(SETUP);
		
		if (p!=null && p.length()>0) {
			if (v==null || v.length()==0) {
				request.remove(p);
			} else {
				request.put(p, v);
			}
		}
	}
	
	public OutputStream openOutputStream() throws IOException {
		expectationState(SETUP);
		return outbuffer.getOutputStream();
	}
	
	public String getRequestMethod() {
		return method;
	}

	public String getRequestProperty(String key) {
		return (String) request.get(key);
	}
	
	// -----------------------------以下方法调用后状态变为CONNECTED
	
	public String getHeaderField(String key) throws IOException {
		connecting();
		return (String) response.get(key);
	}

	public String getHeaderField(int index) throws IOException {
		connecting();
		return (String) response.get( getHeaderFieldKey(index) );
	}

	public String getHeaderFieldKey(int index) throws IOException {
		connecting();
		String fieldkey = null;
		if (index<responseKeys.size()) {
			fieldkey = (String) responseKeys.elementAt(index);
		}
		return fieldkey;
	}

	public int getResponseCode() throws IOException {
		connecting();
		int code = -1;
		try {
			int s = responseMethod.indexOf(' ') + 1;
			int e = responseMethod.indexOf(' ', s);
			String c = responseMethod.substring(s,e);
			code = Integer.parseInt(c);
		} catch (Exception e) {}
		return code;
	}

	public String getResponseMessage() throws IOException {
		connecting();
		String mess = "";
		try {
			int s = responseMethod.indexOf(' ') + 1;
			s = responseMethod.indexOf(' ', s) + 1;
			mess = responseMethod.substring(s, responseMethod.length());
		} catch (Exception e) {}
		return mess;
	}
	
	public long getLength() {
		int len = -1;
		try {
			connecting();
			String cl = (String) response.get("Content-Length");
			len = Integer.parseInt(cl); 
		} catch (Exception e) {
		}
		return len;
	}

	public InputStream openInputStream() throws IOException {
		connecting();
		return responseData;
	}

	public void close() throws IOException {
		state = CLOSED;
		try {
			responseData.close();
		} catch (Exception e) {}
		m_sc.close();
	}
	
	private void expectationState(int s) throws IOException {
		if (s!=state) {
			throw new IOException("在错误的时候调用方法");
		}
	}
	
	private void connecting() throws IOException {
		if (state==SETUP) {
			state = CONNECTED;
			try {
				OutputStream out = m_sc.openOutputStream();
				out.write(getRequestHead());
				out.write(outbuffer.toArray());
				out.close();
			} catch (Exception e) {
				throw new IOException("发出请求时错误:"+e.getMessage());
			}
			try {
				InputStream in = m_sc.openInputStream();
				getReponseHead(in);
				responseData = in;
			} catch (Exception e) {
				throw new IOException("取回应答时错误:"+e.getMessage());
			}
		}
		else if (state==CLOSED) {
			expectationState(CONNECTED);
		}
	}
	
	private byte[] getRequestHead() {
		StringBuffer buff = new StringBuffer();
		buff.append(method);
		buff.append(' ');
		buff.append(uri);
		buff.append(" HTTP/1.1\r\n");
		
		Enumeration keys = request.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			buff.append(key);
			buff.append(": ");
			buff.append((String) request.get(key));
			buff.append("\r\n");
		}
		buff.append("\r\n");
		
		return buff.toString().getBytes();
	}
	
	private void getReponseHead(InputStream in) throws IOException {
		String readline = readLine(in);
		// 忽略前导空行 rfc2616-4.1
		while (readline.length()<=0) {
			readline = readLine(in);
		}
		responseMethod = readline;
		readline = readLine(in);
		while (readline!=null && readline.length()>0) {
			int fl = readline.indexOf(':');
			if (fl>0) {
				String key = readline.substring(0, fl);
				String prop = readline.substring(fl+1, readline.length());
				responseKeys.addElement(key);
				response.put(key, prop);
			}
			readline = readLine(in);
		}
	}
	
	/** 
	 * 从inputStream读取一行字符串,以cr&lf&crlf结尾 
	 * @throws IOException 
	 */
	private String readLine(InputStream in) throws IOException {
		final int cr ='\r';
		final int lf ='\n';
		CharBuffer buff = new CharBuffer(150);
		int read = in.read();
		while (read>=0 && read!=cr && read!=lf) {
			buff.append(read);
			read = in.read();
		}
		if (read==cr) {
			read = in.read();
			if (read==lf) {
				return buff.toString();
			}
		}
		throw new IOException("当前行不以换行结尾");
	}
	
	// ------------------------------------------ 以下方法不被支持
	
	public DataOutputStream openDataOutputStream() throws IOException {
		unsupport();
		return null;
	}
	
	public DataInputStream openDataInputStream() throws IOException {
		unsupport();
		return null;
	}
	
	public long getDate() throws IOException {
		unsupport();
		return -1;
	}

	public long getExpiration() throws IOException {
		unsupport();
		return -1;
	}

	public String getFile() {
		unsupport();
		return null;
	}

	public long getHeaderFieldDate(String arg0, long arg1) throws IOException {
		unsupport();
		return 0;
	}

	public int getHeaderFieldInt(String arg0, int arg1) throws IOException {
		unsupport();
		return 0;
	}


	public long getLastModified() throws IOException {
		unsupport();
		return 0;
	}
	
	public String getRef() {
		unsupport();
		return null;
	}
	
	// --------------------------------------------------------- end
	
	public String getProtocol() {
		return "http";
	}

	public String getQuery() {
		String query = null;
		int s = uri.indexOf('?');
		if (s>0) {
			query = uri.substring(s+1);
		}
		return query;
	}

	public String getEncoding() {
		return get("Content-Encoding");
	}

	public String getType() {
		return get("Content-Type");
	}
	
	public int getPort() {
		return port;
	}
	
	public String getURL() {
		return uri;
	}
	
	public String getHost() {
		return host;
	}
	
	/** 便捷的取得响应头域的方法，<br>找不到的头域返回null */
	private String get(String responseheadname) {
		String value = null;
		try {
			value = getHeaderField(responseheadname);
		} catch (IOException e) {
		}
		return value;
	}

	private void unsupport() throws RuntimeException {
		throw new RuntimeException("方法不被支持");
	}
}
