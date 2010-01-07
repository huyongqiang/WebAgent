package trayicon;

// CatfoOD 2008.2.27

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import jym.server.VersionControl;


public class AboutDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -7160083830139754457L;
	
	private JButton close;
	private Image img = 
		Toolkit.getDefaultToolkit().createImage(logo_jpg.getImage());
	
	public AboutDialog(Frame f) {
		super(f, "关于.");
		boolean modal = false;
		if (f==null) {
			modal = false;
		} else {
			modal = true;
		}
		this.setModal(modal);
		this.setResizable(false);
		this.setUndecorated(true);		
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 550;
		int height= 400;
		int x = (int)( (dim.width-width)/2 );
		int y = (int)( (dim.height-height)/2);
		setBounds(x, y, width, height);
		MediaTracker md = new MediaTracker(this);
		img = img.getScaledInstance(width, -1, 0);
		md.addImage(img, 0);
		try {
			md.waitForAll();
		} catch (InterruptedException e) {}
		
		Panel pan = new Panel(new FlowLayout(FlowLayout.CENTER,8,10));
		close = new JButton("  关闭  ");
		close.addActionListener(this);
		pan.add(close);
	
		setLayout(new BorderLayout());
		add(new JLabel( new ImageIcon(img)), BorderLayout.NORTH);
		add(pan,	BorderLayout.SOUTH);

		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
		this.dispose();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		String name = VersionControl.programname;
		
		String s1 = "我看到光明的所在，物质世界的自由意志";
		String s2 = "象一粒被雨水打湿的沙砾，发出银灰色的饱满的光泽";
		FontMetrics mm = g.getFontMetrics();
		int l1 = mm.charsWidth(s1.toCharArray(), 0, s1.toCharArray().length);
		int l2 = mm.charsWidth(s2.toCharArray(), 0, s2.toCharArray().length);

		g.drawString(s1, 500-l1, 205);
		g.drawString(s2, 500-l2, 225);
		g.drawString("Copyright CatfoOD 2009 QQ:412475540", 25, 290);
		g.drawString(name+" "+VersionControl.version, 25,310);
		g.drawString("", 25, 330);
		g.drawLine(0, 340, 550, 340);
		g.drawLine(20, 180, 20, 380);
	}
}
