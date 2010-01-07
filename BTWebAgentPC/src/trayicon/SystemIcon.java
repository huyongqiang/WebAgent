// CatfoOD 2008-7-30 上午10:11:42

package trayicon;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jym.agent.Tools;
import jym.server.VersionControl;

public class SystemIcon {
	private static SystemIcon si = null;
	private static int upsize;
	private static int downsize;
	
	public static void init() {
		if (si==null) {
			si = new SystemIcon();
		}
	}
	
	/** 网络上传流量 */
	public static void up(int size) {
		upsize += size;
	}
	
	/** 网络下载流量 */
	public static void down(int size) {
		downsize += size;
	}
	
	public static void pl(Object o) {
		if (o!=null) {
			char c = o.toString().charAt(0);
			switch (c) {
			case '#':
			case '>':
			case '<':
			case '=':
				break;
			default:
				si.pop(o.toString());
			}
		}
	}
	
	private TrayIcon ticon;
	
	private SystemIcon() {
		ticon = new TrayIcon(getImage());
		ticon.setPopupMenu(getPopupMenu());
		ticon.setImageAutoSize(true);
		setTip();
		try {
			SystemTray.getSystemTray().add(ticon);
		} catch (AWTException e) {
		}
	}
	
	private void setTip() {
		Thread t = new Thread() {
			public void run() {
				while (true) {
				ticon.setToolTip( "服务器监听端口:8080"
						+ "\n下载流量:" + unitTran(downsize) 
						+ "\n上传流量:" + unitTran(upsize) );
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}
	
	private String unitTran(int i) {
		String s = null;
		if (i<1024) {
			s = i + "Byte";
		}
		else if (i<1048576) {
			s = i/1024 + "KB";
		}
		else {
			s = i/1048576 + "MB";
		}
		return s;
	}
	
	private final String title =
		VersionControl.programname+ " "+ VersionControl.version;
	
	private void pop(String mess) {
		ticon.displayMessage(title, mess, MessageType.INFO);
	}
	
	private PopupMenu getPopupMenu() {
		PopupMenu pm = new PopupMenu();
		pm.add(getExitMenu());
		pm.add(getAboutMenu());
		return pm;
	}
	
	private MenuItem getExitMenu() {
		MenuItem mi = new MenuItem("Exit");
		mi.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
				Tools.exit();
			}
		});
		return mi;
	}
	
	private MenuItem getAboutMenu() {
		MenuItem mi = new MenuItem("About");
		mi.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutDialog(null);
			}
		});
		return mi;
	}
	
	private Image getImage() {
		return Toolkit.getDefaultToolkit().createImage(icon_gif.getImage());
	}

	public void exit() {
		SystemTray.getSystemTray().remove(ticon);
	}
}
