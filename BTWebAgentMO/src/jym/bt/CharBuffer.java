package jym.bt;

import java.io.IOException;
import java.io.OutputStream;

// CatfoOD 2008.3.26

/**
 * �ַ�����
 * v 2.2
 */
public class CharBuffer {
	private int DEFAULTLEN = 30;
	private byte[] buff = new byte[0];
	private int point = 0;
	
	/**
	 * ����Ĭ�ϵĻ���������:30 
	 */
	public CharBuffer() {
		this(30);
	}
	
	/**
	 * ����һ���ַ�������,����Ϊlen
	 * @param len - �������ĳ���
	 */
	public CharBuffer(int len) {
		buff = new byte[len];
		DEFAULTLEN = len;
	}
	
	/**
	 * ���ַ���ӵ�ĩβ
	 * @param c - Ҫ��ӵ��ַ�
	 */
	public void append(byte c) {
		if (point>=buff.length) {
			reAllotArray();
		}
		buff[point++] = c;
	}
	
	/**
	 * ���ַ���ӵ�ĩβ
	 */
	public void append(char c) {
		append((byte)c);
	}
	
	/**
	 * ���ַ���ӵ�������ĩβ
	 */
	public void append(int c) {
		append((byte)c);
	}
	
	public void append(byte[] bs) {
		for (int i=0; i<bs.length; ++i) {
			append(bs[i]);
		}
	}
	
	public void append(String s) {
		append(s.getBytes());
	}
	
	private void reAllotArray() {
		byte[] newbuff = new byte[buff.length*2];
		copy(newbuff, buff);
		buff = newbuff;
		// ��������!!
		System.gc();
	}
	
	private void copy(byte[] src, final byte[] dec) {
		if (src.length<dec.length) throwsErr();
		
		for (int i=0; i<dec.length; ++i) {
			src[i] = dec[i];
		}
	}
	
	/**
	 * �ַ����������ַ�����ʾ
	 */
	public String toString() {
		return new String(buff, 0, point);
	}
	
	/**
	 * ��ջ�����,ѭ������???
	 * @cut - ���Ϊtrue��ɾ�����飬����Ĭ�ϳ��Ƚ����µ�����
	 */
	public void delete(boolean cut) {
		if (cut) {
			buff = new byte[DEFAULTLEN];
			System.gc();
		}
		point = 0;
	}
	
	public byte[] toArray() {
		byte[] t = new byte[point];
		for (int i=0; i<t.length; ++i) {
			t[i] = buff[i];
		}
		return t;
	}
	
	public byte[] toArray(int offset, int length) {
		if (offset<0 || length<0) throwsErr();
		if (offset+length>point) throwsErr();
		
		byte[] t = new byte[length];
		for (int i=offset; i<t.length; ++i) {
			t[i] = buff[offset+length];
		}
		return t;
	}
	
	public int length() {
		return point;
	}
	
	private final void throwsErr() {
		throw new IllegalArgumentException();
	}
	
	/**
	 * ͨ��OutputStream��CharBuffer���������
	 */
	public OutputStream getOutputStream() {
		return new OutputStream() {
			public void write(int b) throws IOException {
				append(b);
			}
		};
	}
}
