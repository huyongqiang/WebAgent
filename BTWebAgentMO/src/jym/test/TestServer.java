// CatfoOD 2010-1-8 ÉÏÎç07:22:12

package jym.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Hashtable;

import javax.microedition.io.HttpConnection;

import jym.server.Server;

public class TestServer extends Server {

	protected HttpConnection creatHttpLink(String url) throws IOException {
		HttpConnection http = new HttpConn();
		return http;
	}
	
	private class HttpConn implements HttpConnection {
		private Hashtable map = new Hashtable();

		private HttpConn() {
			map.put("content-length", 1024*1024+"");
		}
		
		public long getDate() throws IOException {
			return new Date().getTime();
		}

		public long getExpiration() throws IOException {
			return 0;
		}

		public String getFile() {
			return null;
		}

		public String getHeaderField(String arg0) throws IOException {
			return (String) map.get(arg0);
		}

		public String getHeaderField(int arg0) throws IOException {
			return "";
		}

		public long getHeaderFieldDate(String arg0, long arg1)
				throws IOException {
			return 0;
		}

		public int getHeaderFieldInt(String arg0, int arg1) throws IOException {
			return 0;
		}

		public String getHeaderFieldKey(int arg0) throws IOException {
			return null;
		}

		public String getHost() {
			return "";
		}

		public long getLastModified() throws IOException {
			return 0;
		}

		public int getPort() {
			return 0;
		}

		public String getProtocol() {
			return "http";
		}

		public String getQuery() {
			return "";
		}

		public String getRef() {
			return "";
		}

		public String getRequestMethod() {
			return "http";
		}

		public String getRequestProperty(String arg0) {
			return "";
		}

		public int getResponseCode() throws IOException {
			return 200;
		}

		public String getResponseMessage() throws IOException {
			return "OK";
		}

		public String getURL() {
			return "";
		}

		public void setRequestMethod(String arg0) throws IOException {
		}

		public void setRequestProperty(String arg0, String arg1)
				throws IOException {
		}

		public String getEncoding() {
			return "gbk";
		}

		public long getLength() {
			return 1024*1024;
		}

		public String getType() {
			return null;
		}

		public DataInputStream openDataInputStream() throws IOException {
			return null;
		}

		public InputStream openInputStream() throws IOException {
			return new NullInput();
		}

		public void close() throws IOException {
		}

		public DataOutputStream openDataOutputStream() throws IOException {
			return null;
		}

		public OutputStream openOutputStream() throws IOException {
			return new NullOutput();
		}
		
	}
	
	private class NullInput extends InputStream {
		public int read() throws IOException {
			return 'a';
		}
	}
	
	private class NullOutput extends OutputStream {
		public void write(int arg0) throws IOException {
		}
	}
}
