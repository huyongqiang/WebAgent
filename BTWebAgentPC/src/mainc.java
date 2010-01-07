// CatfoOD 2009-10-3 ÉÏÎç09:18:54

import java.io.IOException;

import trayicon.SystemIcon;

import jym.agent.Server;
import jym.server.VersionControl;

public class mainc {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		welcome();
		
		Server server = new Server();
		server.start();
	}
	
	public static void welcome() {
		pl( "|------------------------- CatfoOD 2010 Java Project -------------------------|" );
		pl( " HTTP Agent for Mobil to PC " + VersionControl.version	);
		pl( " qq:412475540" 					);
		pl( " yanming-sohu@sohu.com" 			);
		pl( "*------------------------- ------------------------- -------------------------*" );
		SystemIcon.init();
	}

	private static void pl(String s) {
		System.out.println(s);
	}
}
