import gui.LoaderDlg;
import gui.MainDlg;
import gui.SettingsDlg;

import java.util.prefs.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import classes.Config;
import classes.NSocket;

public class Main {

	private static String server = null;
	private static int port;
	final static SettingsDlg dlgSettings = new SettingsDlg();
	final static MainDlg dlgMain = new MainDlg();
	final static Preferences prefs = Preferences.userNodeForPackage(Main.class);
	
	private static final String ipPattern = 
	        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	private static final String portPattern = 
			"^([0-9]{1,5})$";
	
	public enum Steps {
		WELCOME,
		IDENTIFY,
		REJECT,
		REQUEST,
		CONFIG,
		SYNC
	}
	
	public static void main(String[] args) {
		if(args.length == 2) {
			if(!args[0].matches(ipPattern)) {
				System.console().writer().println(args[0]+" is not a valid IP address");
				System.exit(0);
			} else if(!args[1].matches(portPattern)) {
				System.console().writer().println(args[1]+" is not a valid port");
				System.exit(0);
			} else {
				prefs.put("server", args[0]);
				prefs.put("port", args[1]);
			}
		}
		
		LoaderDlg dlgLoader = new LoaderDlg();
		

//		dlgSettings.setOnSave(new Runnable() {
//			public void run() {
//				prefs.put("server", dlgSettings.getServer());
//				prefs.put("port", dlgSettings.getPort());
//				dlgSettings.close();
//				System.exit(0);
//			}
//		});		
		
		if(prefs.get("server", "").length() == 0 || prefs.get("port", "").length() == 0) {
//			dlgSettings.create();
//			dlgSettings.show();
			System.out.println("No server and/or port are given.");
			System.exit(0);
		} else {

//			dlgLoader.setOnSettings(new Runnable() {
//				public void run() {
//					dlgSettings.create();
//					if(prefs.get("server", "").length() > 0)
//						dlgSettings.setPort(Integer.valueOf(prefs.get("port", "")));
//					if(prefs.get("port", "").length() > 0)
//						dlgSettings.setServer(prefs.get("server", ""));
//					dlgSettings.show();
//				}
//			});			
			
			server = prefs.get("server", "");
			port = Integer.valueOf(prefs.get("port", ""));
			
			dlgLoader.create();
			dlgLoader.show();
			dlgLoader.update(0, "Connecting to server...");
			
	    	NSocket.connect(server, port);
	    	
	    	JSONObject json = null;
	    	String message = null;
	    	boolean isConnected = false;
	    	
	    	Steps steps = Steps.WELCOME;
	    	Boolean loop = true;
	    	Boolean has_data = false;
	    	int size = 1025;
	
	    	while(loop) {
	        	if(NSocket.connected()) {
					dlgLoader.progress();        		
	        		isConnected = true;
		    		has_data = false;
		
		    		if(steps == Steps.CONFIG) {
		    			size = 102500;
		    		} else {
		    			size = 1025;
		    		}
		
		    		if(NSocket.read(size)) {
		    			try {
		    				json = new JSONObject(NSocket.getLine());
		
		    				if(json.has("message")) {
		    					message = json.getString("message");
		    				}
		    				has_data = true;
		    			} catch(JSONException e) {
		    				has_data = false;
		    			}
					}
		    		if(has_data) {
		    			
		    			if(new String("reject client").equals(message)) {
							loop = false;
							dlgLoader.update(100, "Rejected by server...");
							NSocket.close();
							break;
						}
	
		    			switch(steps) {
		    				case WELCOME:
		    					if(new String("accept connection").equals(message)) {
		    						NSocket.write("{\"message\":\"client gui\"}");
		    						steps = Steps.IDENTIFY;
		    						dlgLoader.update(25, "Identifying...");
		    					}
		    				break;
		    				case IDENTIFY:
		    					if(new String("accept client").equals(message)) {
		    						steps = Steps.REQUEST;
		    						dlgLoader.update(50, "Accepted by server...");
		    		    		}
		    				case REQUEST:
		    					NSocket.write("{\"message\":\"request config\"}");
		    					dlgLoader.update(75, "Retrieving configuration...");
								steps = Steps.CONFIG;
							break;
		    				case CONFIG:
		    					if(json.has("config")) {
		    						Config.parse(json.getJSONObject("config"));

		    						dlgLoader.update(100, "Building main window...");
//		     						dlgMain.setOnSettings(new Runnable() {
//		     							public void run() {
//		     								dlgSettings.create();
//		     								if(prefs.get("server", "").length() > 0)
//		     									dlgSettings.setPort(Integer.valueOf(prefs.get("port", "")));
//		     								if(prefs.get("port", "").length() > 0)
//		     									dlgSettings.setServer(prefs.get("server", ""));
//		     								dlgSettings.show();
//		     								//dlgMain.disable();
//		     							}
//									});
		     						dlgMain.create();
		     						dlgLoader.hide();
		     						dlgMain.show();
		
		    						steps = Steps.SYNC;
		    					}
		    				case SYNC:
		    					if(json.has("origin") && new String("config").equals(json.getString("origin"))) {
		    						if(dlgMain != null) {
		    							dlgMain.update(json);
		    						}
		    					}
		    				break;
		    				default:	
		    				break;
		    			}
		    		}
	    		} else {
	    			if(!dlgLoader.enabled()) {
	    				break;
	    			}
	
	    			if(!isConnected) {
	    				if(dlgMain != null)
	    					dlgMain.disable();
					
						try {
							Thread.sleep(1000);
						} catch(InterruptedException e) {
						}
							
						NSocket.connect(server, port);
	    			} else {
	    				break;
	    			}
	    		}
	    		try {
					Thread.sleep(10);
				} catch(InterruptedException e) {
				}
	    	}
	    	NSocket.close();
	    	System.exit(0);
		}
    }
}
