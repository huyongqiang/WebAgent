// CatfoOD 2009-10-9 ����08:23:46

package jym.bt;

import java.io.IOException;

public class BTLinkService extends BTLink {
	private static int numid = 1;
	
	/**
	 * ����һ����������Э�飬���ӳɹ��󷵻�
	 * @throws IOException - ���ӳ����׳��쳣
	 */
	public BTLinkService() throws IOException {
		super(new BlueSevice());
	}

	protected void creatLink() throws IOException {
	synchronized (out) {
		int id = numid++;
		out.writeInt(CREAT);
		out.writeInt(id);
		out.flush();
		creatAndSaveLogicLink(id);
		}
	}

	public void newLink() throws IOException {
		throw new BlueToothException("��֧�ֵķ���");
	}
}
