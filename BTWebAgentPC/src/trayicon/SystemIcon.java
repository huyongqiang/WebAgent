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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
		click();
		setTip();
		try {
			SystemTray.getSystemTray().add(ticon);
		} catch (AWTException e) {
		}
	}
	
	private void click() {
		ticon.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON1) {
					Log.display();
				}
			}
		});
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
	
	private String unitTran(float i) {
		String unit = null;
		
		if (i<1024) {
			unit = "Byte";
		}
		else if (i<1048576) {
			i = i/1024;
			unit = "KB";
		}
		else {
			i = i/1048576;
			unit = "MB";
		}
		i *= 100f;
		i = (int)i / 100f;
		
		return i + unit;
	}
	
	private final String title =
		VersionControl.programname+ " "+ VersionControl.version;
	
	private void pop(String mess) {
		ticon.displayMessage(title, mess, MessageType.INFO);
	}
	
	private PopupMenu getPopupMenu() {
		PopupMenu pm = new PopupMenu();
		pm.add(getLogMenu());
		pm.add(getAboutMenu());
		pm.add(getExitMenu());
		return pm;
	}
	
	private MenuItem getExitMenu() {
		MenuItem mi = new MenuItem("退出");
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
		MenuItem mi = new MenuItem("关于");
		mi.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutDialog(null);
			}
		});
		return mi;
	}
	
	private MenuItem getLogMenu() {
		MenuItem mi = new MenuItem("日志");
		mi.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Log.display();
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
