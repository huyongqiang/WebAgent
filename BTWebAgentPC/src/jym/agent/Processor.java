// CatfoOD 2009-10-3 上午11:15:22

package jym.agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jym.bt.BTLinkClient;
import jym.bt.IBlue;
import jym.bt.IBlueCtrl;
import jym.bt.IBlueListener;

public class Processor implements IBlueListener {
	private RequestQueue queue;
	private boolean stop = false;
	private IBlueCtrl bluectrl;
	private RequestQueue processqueue;
	
	public Processor(RequestQueue q) {
		queue = q;
		processqueue = new RequestQueue();
		pro.start();
		creatBT();
	}
	
	public void stop() {
		stop = true;
	}
	
	private Thread pro = new Thread() {
		public void run() {
			while (!stop) {
				while (bluectrl==null) {
					Tools.sleep(800);
				}
				RequestPack req = queue.get();
				if (req!=null) {
					processqueue.put(req);
					try {
						bluectrl.newLink();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Tools.sleep(800);
				}
			}
		}
	};

	@Override
	public void newlink(IBlue blue) {
		RequestPack req = processqueue.get();
		if (req!=null) {
			process(req, blue);
		}
		blue.close();
	}
	
	private void process(RequestPack req, IBlue blue) {
		Tools.pl("=处理请求:"+req);
		try {
			if (req.isClosed()) return;
			
			DataOutputStream out = blue.getDataOutputStream();
			req.writeRequestProperty(out);
			
			DataInputStream in = blue.getDataInputStream();
			String cm = in.readUTF();
			if (cm.startsWith("ok")) {
				req.write(in, out);
			} else {
				Tools.pl("x终端错误:" + req + cm);
			}

		} catch (IOException e) {
			Tools.pl("-处理请求错误:" + req + e);
		} finally {
			req.close();
		}
		Tools.pl("<处理结束:"+req);
	}
	
	private void creatBT() {
		new Thread() { public void run() {
			
		while (!stop) {
			Tools.pl("+建立蓝牙连接");
			try {
				bluectrl = new BTLinkClient();
				Tools.pl(".连接已建立");
				bluectrl.registerBlueListener(Processor.this);
				bluectrl.start();
				Tools.pl(".蓝牙连接断开");
				
			} catch (Exception e) {
				Tools.pl("-蓝牙连接断开:" + e.getMessage());
			} finally {
				if (bluectrl!=null)
					bluectrl.close();
				bluectrl = null;
			}
			Tools.sleep(800);
		}
		
		}}.start();
	}

}
