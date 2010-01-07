package jym.bt;

import java.io.IOException;
import java.io.OutputStream;

// CatfoOD 2008.3.26

/**
 * 字符缓冲
 * v 2.2
 */
public class CharBuffer {
	private int DEFAULTLEN = 30;
	private byte[] buff = new byte[0];
	private int point = 0;
	
	/**
	 * 建立默认的缓冲区长度:30 
	 */
	public CharBuffer() {
		this(30);
	}
	
	/**
	 * 建立一个字符缓冲区,长度为len
	 * @param len - 缓冲区的长度
	 */
	public CharBuffer(int len) {
		buff = new byte[len];
		DEFAULTLEN = len;
	}
	
	/**
	 * 将字符添加到末尾
	 * @param c - 要添加的字符
	 */
	public void append(byte c) {
		if (point>=buff.length) {
			reAllotArray();
		}
		buff[point++] = c;
	}
	
	/**
	 * 将字符添加到末尾
	 */
	public void append(char c) {
		append((byte)c);
	}
	
	/**
	 * 将字符添加到缓冲区末尾
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
		// 垃圾回收!!
		System.gc();
	}
	
	private void copy(byte[] src, final byte[] dec) {
		if (src.length<dec.length) throwsErr();
		
		for (int i=0; i<dec.length; ++i) {
			src[i] = dec[i];
		}
	}
	
	/**
	 * 字符缓冲区的字符串表示
	 */
	public String toString() {
		return new String(buff, 0, point);
	}
	
	/**
	 * 清空缓冲区,循环利用???
	 * @cut - 如果为true则删除数组，并用默认长度建立新的数组
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
	 * 通过OutputStream向CharBuffer中添加数据
	 */
	public OutputStream getOutputStream() {
		return new OutputStream() {
			public void write(int b) throws IOException {
				append(b);
			}
		};
	}
}
