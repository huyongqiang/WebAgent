// CatfoOD 2009-10-9 下午12:39:56

package jym.bt;

/**
 * 先进先出队列,已经同步
 */
public class Queue {
	private Node first;
	private Node end;
	
	/**
	 * 先进先出队列
	 */
	public Queue() {
		first = null;
		end = null;
	}
	
	/**
	 * 添加到队列末尾,不接受null
	 */
	public void add(Object o) {
	synchronized (this) {
		if (o!=null) {
			Node n = new Node(o);
			if (end!=null) {
				end.next = n;
			}
			end = n;
			if (first==null) {
				first = n;
			}
		}
		}
	}
	
	/**
	 * 取得队首元素并移除
	 */
	public Object first() {
		Object el = null;
	synchronized (this) {
		if (first!=null) {
			el = first.getElement();
			first.remove();
		}
		}
		return el;
	}
	
	public boolean isEmpty() {
		synchronized (this) {
			return first==null;
		}
	}
	
	private class Node {
		private Object obj;
		private Node next;
		
		private Node(Object el) {
			obj = el;
			next = null;
		}
		
		private Object getElement() {
			return obj;
		}
		
		private void remove() {
			first = next;
			if (next==null) {
				end = null;
			}
		}
	}
}
