// CatfoOD 2010-1-8 ÉÏÎç07:21:08

package jym.test;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import jym.server.VersionControl;

public class Main extends MIDlet implements CommandListener {
	private static Form form;
	
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		form = set();
		new TestServer();
	}
	
	private Form set() {
		Form f = new Form("²âÊÔ");
		f.append(new StringItem("JavaProject.", VersionControl.string()));
		Display.getDisplay(this).setCurrent(f);
		return f;
	}

	public void commandAction(Command arg0, Displayable arg1) {
	}

	public static void pl(Object s) {
		if (s==null) return;
		if (form.size()>20) {
			form.deleteAll();
		}
		form.append( new StringItem("", s.toString()) );
	}
}
