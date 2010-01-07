// CatfoOD 2009-10-3 ����12:07:55

package jym.agent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpParse {
	/** �س� */
	public static final char cr = '\r';

	/** ���� */
	public static final char lf = '\n';
	
	private String httphead;
	private byte[] messBody = new byte[0];
	private String requesturi;
	
	public HttpParse(InputStream in) throws IOException {		
		StringBuffer sb = new StringBuffer();
		String readline = readLine(in);
		
		// ����ǰ������ rfc2616-4.1
		while (readline.length()<=0) {
			readline = readLine(in);
		}
		
		// Ȼ��ʼ��ȡHttpͷ
		while (readline!=null && readline.length()>0) {
			sb.append(readline+"\n");
			readline = readLine(in);
		}
		httphead = sb.toString();
		
		// Content-Length ������Ϣ��
		String cl = get("Content-Length");
		if (cl!=null && cl.trim().length()>0) {
			int len = Integer.parseInt(cl.trim());
			byte[] body = new byte[len];
			if (len==in.read(body)) {
				messBody = body;
			}
		}
	}
	
	public String getRequestHead() {
		return httphead;
	}
	
	public String getNullLine() {
		return "\r\n";
	}
	
	/**
	 * ����δ�����URI
	 */
	public String getRequestURI() { 
		if (requesturi==null) {
			int start = httphead.indexOf(' ');
			int end = httphead.indexOf(' ', start+1);
			if (start>=0 && end>=0) {
				requesturi = httphead.substring(start+1, end);
				if (requesturi!=null) {
					requesturi = decodeURI(requesturi);
				}
			}
		}
		return requesturi;
	}
	
	public String getHost() {
		return get("Host");
	}
	
	public byte[] getBody() {
		return messBody;
	}
	
	/** 
	 * ��inputStream��ȡһ���ַ���,��cr&lf&crlf��β 
	 * @throws IOException 
	 * @throws IOException 
	 */
	private String readLine(InputStream in) throws IOException {
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
		System.out.println(":����,HTTP������û�л���:"+buff.toString());
		return "";
	}
	
	/** 
	 * ����һ����sָ����Request����ȥ������β���ַ�, 
	 * ʧ�ܷ��ؿ��ַ���
	 **/
	public String get(String s) {
		int start = httphead.indexOf(s);
		int end = httphead.indexOf('\n', start);

		if (start>=0 && end<=httphead.length() && end>0) {
			if (httphead.charAt(start+s.length())==':') {
				return httphead.substring(start+s.length()+1, end).trim();
			}
		}
		return "";
	}
	
	/**
	 * ����HTTP����ķ���(GET,POST,PUT...)
	 * @return �Ҳ�������null;
	 */
	public String getMethod() {
		int end = httphead.indexOf(' ');
		return httphead.substring(0, end);
	}
	
	/**
	 * ����URI.<br> 
	 * ʹ��ָ���ı�����ƶ� application/x-www-form-urlencoded �ַ������롣<br>
	 * �����ı�������ȷ���κ� "%xy" ��ʽ���������б�ʾ���ַ��� 
	 * @param uri - �������uri
	 * @return ������uri��������'?'������ַ�,�������ʧ��,����ԭʼ�ַ���
	 */
	private static final String decodeURI(String uri) {
		int fen = uri.indexOf('?');
		String url_s = null;
		String url_e = null;
		if (fen>=0 && fen<uri.length()-1) {
			url_s = uri.substring(0, fen);
			url_e = uri.substring(fen+1);
		} else {
			url_s = uri;
			url_e = null;
		}
		try {
			url_s = URLDecoder.decode(url_s, "UTF-8");
			uri = url_s+ (url_e==null? "": "?"+url_e);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return uri;
	}
	
	/**
	 * ����URI
	 */
	public static final String encodeURI(String uri) {
		final String http = "http://";
		try {
			if (uri.startsWith(http)) {
				uri = uri.substring(http.length(), uri.length());
			}
			String path = "";
			String parm = "";
			int s1 = uri.indexOf('/');
			if (s1>=0) {
				path = uri.substring(s1, uri.length());
				uri  = uri.substring(0, s1);
			}
			
			// ����ֻ�HttpЭ��Ĳ���������Ѳ����еġ�:��
			// ����port�ı�־�Ӷ�����һ������
			int pt = path.indexOf(':');
			if (pt>=0) {
				int s2 = uri.indexOf(':');
				if (s2<0) {
					uri = uri + ":80";
				}
			}
			
			int ps = path.indexOf('?');
			if (ps>0) {
				ps += 1;
				parm = path.substring(ps, path.length());
				path = path.substring(0, ps);
			}
			
			//parm = parm.replaceAll("%", "%25");
			uri = http + uri + path + parm;
			
		} catch(Exception e) {
			Tools.pl("����URIʧ��"+e);
		}
		return uri;
	}
	
	public Map<String, String> getHeads() {
		Map<String, String> map = new HashMap<String, String>();
		int start = 0;
		int end = 0;
		while (true) {
			start = httphead.indexOf('\n', end)+1;
			if (start>=0) {
				end = httphead.indexOf(':', start);
				if (end>=0) {
					int end2 = httphead.indexOf('\n', end);
					if (end2>=0) {
						String key = httphead.substring(start, end).trim();
						String value = httphead.substring(end+1, end2).trim();
						map.put(key, value);
						continue;
					}
				}
			}
			break;
		}
		return map;
	}
	
	public String getHead() {
		return httphead;
	}
	
//	public static void main(String[] as) throws UnsupportedEncodingException {
//		String s = "http://pingfore.qq.com/pingd?dm=web.qq.com&url=/&tt=%u6211%u7684QQ%20%u2013%20%u80FD%u5728%u7F51%u9875%u4E0A%u76F4%u63A5%u804AQQ&rdm=-&rurl=-&pvid=6182360082&scr=1024x768&scl=32-bit&lang=zh-cn&java=1&cc=undefined&pf=Win32&tz=-8&ct=-&vs=3.1&column=&arg=&rarg=&ext=&hurlcn=F91044485883&rand=47222";
//		System.out.println(s);
//		//s = URLEncoder.encode(s,"UTF-8");
//		s = encodeURI(s);
//		System.out.println(s);
//	}
}
