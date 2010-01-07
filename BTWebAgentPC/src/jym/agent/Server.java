// CatfoOD 2009-10-3 上午10:12:25

package jym.agent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final int PORT = 8080;
	
	private ServerSocket server;
	private boolean stop = false;
	private RequestQueue queue;
	private Processor proces;
	
	public Server() throws IOException {
		Tools.pl( "-Http proxy server port:" + PORT + "\n" );
		
		server = new ServerSocket(PORT);
		queue = new RequestQueue();
		proces = new Processor(queue);
	}
	
	public void start() {
		new Thread() {
			public void run() {
				server();
			}
		}.start();
	}
	
	public void server() {
		while (!stop) {
			try {
				Socket s = server.accept();
				RequestPack rp = new RequestPack(s);
				queue.put(rp);
				Tools.pl(">请求进入:"+rp+rp.getURI());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			server.close();
			proces.stop();
		} catch (IOException e) {
		}
	}
	
	public void stop() {
		stop = true;
	}
}
