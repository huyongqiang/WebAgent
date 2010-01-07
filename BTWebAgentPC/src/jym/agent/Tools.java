// CatfoOD 2009-10-3 ионГ11:18:12

package jym.agent;

import trayicon.SystemIcon;

public class Tools {
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}
	
	public static void pl(Object o) {
		p(o+"\n");
	}
	
	public static void p(Object o) {
		System.out.print(o);
		SystemIcon.pl(o);
	}
	
	public static void exit() {
		System.exit(0);
	}
}
