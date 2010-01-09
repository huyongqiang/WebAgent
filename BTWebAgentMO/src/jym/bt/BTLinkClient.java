// CatfoOD 2009-10-9 上午08:26:56

package jym.bt;

import java.io.DataOutputStream;
import java.io.IOException;

public class BTLinkClient extends BTLink {	
	/**
	 * 搜索蓝牙服务，如果找到并连接后返回
	 * @throws IOException - 蓝牙错误或，找不到连接抛出异常
	 */
	public BTLinkClient() throws IOException {
		super(new BlueClient());
	}
	
	/**
	 * 请求服务端创建新的连接
	 * @throws IOException - 如果蓝牙没有连接
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
