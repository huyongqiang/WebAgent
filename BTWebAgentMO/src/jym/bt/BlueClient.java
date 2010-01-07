// CatfoOD 2009-2-7 下午02:18:22

package jym.bt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class BlueClient implements IBlue {
	private static final String uuid = "c81add9efee14d55860057d8f02f506a";
	private static final UUID[] uids = new UUID[] {new UUID(uuid, false)};
	
	private DiscoveryAgent agent;
	private DiscoveryListenerImpl listener;
	private StreamConnection con;
	private LocalDevice local;
	
	private String error = "unknow";
	private boolean started = false;
	
	/**
	 * 蓝牙服务器，主动连接(本地)
	 * @throws IOException
	 */
	public BlueClient() throws IOException {
		local = LocalDevice.getLocalDevice();
		agent = local.getDiscoveryAgent();
	}

	public void start() throws IOException {
		synchronized (local) {
			if (started) return;
			seatchDevice();
			listener.waitCompleted();
			
			if (con!=null) {
				started = true;
			} else {
				throw new IOException(error);
			}
		}
	}
	
	private void seatchDevice() throws BluetoothStateException {
		listener = new DiscoveryListenerImpl();
		agent.startInquiry(DiscoveryAgent.GIAC, listener);
	}
	
	private void error(String s) {
		error = s;
	}
	
	class DiscoveryListenerImpl implements DiscoveryListener {
		private int pccount = 0;
		private int svcount = 0;
		private int sover = 0;
		private boolean over = false;
		
		private void waitCompleted() {
			while (!over) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
			}
		}
		
		public void deviceDiscovered(RemoteDevice arg0, DeviceClass arg1) {
			try {
				pccount++;
				agent.searchServices(null, uids, arg0, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void inquiryCompleted(int arg0) {
			if (pccount==0) {
				error("没有找到任何网络终端");
				over = true;
			}
			
			sover = pccount;
		}		
		
		public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {
			for (int i=0; i<arg1.length; ++i) {
				try {
					svcount++;
					String url = arg1[i].getConnectionURL(
							ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
					con = (StreamConnection)Connector.open(url);
					break;
				} catch (Exception e) {
					error(e.getMessage());
				}
			}
		}
		
		public void serviceSearchCompleted(int arg0, int arg1) {
			if (svcount==0) {
				error("在主机上没有找到相关服务");
			}
			if (--sover<=0) {
				over = true;
			}
		}
	}

	private DataInputStream din;
	private DataOutputStream dout;
	private InputStream in;
	private OutputStream out;

	public DataInputStream getDataInputStream() throws IOException {
		if (din==null) {
			din = con.openDataInputStream();
		}
		return din;
	}

	public DataOutputStream getDataOutputStream() throws IOException {
		if (dout==null) {
			dout = con.openDataOutputStream();
		}
		return dout;
	}

	public InputStream getInputStream() throws IOException {
		if (in==null) {
			in = con.openInputStream();
		}
		return in;
	}

	public OutputStream getOutputStream() throws IOException {
		if (out==null) {
			out = con.openOutputStream();
		}
		return out;
	}

	public void close() {
		try {
			if (con!=null) {
				con.close();
			}
		} catch (IOException e) {
		}
	}
}

