// CatfoOD 2009-10-9 ����08:26:56

package jym.bt;

import java.io.DataOutputStream;
import java.io.IOException;

public class BTLinkClient extends BTLink {	
	/**
	 * ����������������ҵ������Ӻ󷵻�
	 * @throws IOException - ����������Ҳ��������׳��쳣
	 */
	public BTLinkClient() throws IOException {
		super(new BlueClient());
	}
	
	/**
	 * �������˴����µ�����
	 * @throws IOException - �������û������
	 */
	public void newLink() throws IOException {
		DataOutputStream out = getOutStream();
		out.writeInt(CREAT);
		out.flush();
	}
	
	protected void creatLink() throws IOException {
		int id = in.readInt();
		creatAndSaveLogicLink(id);
	}
}
