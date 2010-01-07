// CatfoOD 2009-10-3 ионГ10:11:37

package jym.agent;

import java.util.ArrayDeque;
import java.util.Queue;

public class RequestQueue {
	private Queue<RequestPack> queue;
	
	public RequestQueue() {
		queue = new ArrayDeque<RequestPack>();
	}
	
	public void put(RequestPack rp) {
	synchronized (queue) {
		queue.offer(rp);
		}
	}
	
	public RequestPack get() {
	synchronized (queue) {
		return queue.poll();
		}
	}
	
	public int getQueueSize() {
	synchronized (queue) {
		return queue.size();
		}
	}
	
	public boolean isEmpty() {
		return getQueueSize()==0;
	}
}
