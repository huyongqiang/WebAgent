// CatfoOD 2009-10-3 ÏÂÎç12:56:12

package jym.mid;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import jym.server.Server;
import jym.server.VersionControl;

public class Mainc extends MIDlet implements CommandListener {
	private static Form form;
	
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		form = set();
		new Server();
	}
	
	private Form set() {
		Form f = new Form("CatfoOD 2009");
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
