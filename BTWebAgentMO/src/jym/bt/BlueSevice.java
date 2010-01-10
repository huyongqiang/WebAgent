// CatfoOD 2009-2-7 上午11:46:37

package jym.bt;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;


public class BlueSevice implements IBlue {
	
	private final String url;
	private LocalDevice localdevice;
	private StreamConnection con;
	private StreamConnectionNotifier notifier;
	private boolean started = false;
	
	/**
	 * 蓝牙服务器，被动连接(本地)
	 * 
	 * @param uuid - 蓝牙服务唯一ID
	 * @throws IOException 
	 */
	public BlueSevice(String uuid) throws IOException {
		url = "btspp://localhost:"+uuid.toString();
		
		localdevice = LocalDevice.getLocalDevice();
		localdevice.setDiscoverable(DiscoveryAgent.GIAC);
	}
	
	public void start() throws IOException {
		synchronized (localdevice) {
			if (started) return;
			discoveryThread();
			if (con!=null) {
				started = true;
			}
		}
	}
	
	private void discoveryThread() throws IOException {
		notifier = (StreamConnectionNotifier)Connector.open(url);
		con = notifier.acceptAndOpen();
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
			if (notifier!=null) {
				notifier.close();
			}
			if (dout!=null) {
				dout.close();
			}
		} catch (IOException e) {
		}
	}
}
