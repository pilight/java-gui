import gui.LoaderDlg;
import gui.MainDlg;
import gui.SettingsDlg;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import classes.Config;
import classes.NSocket;

public class Main {

	private static String server = null;
	private static int port = 0;
	final static SettingsDlg dlgSettings = new SettingsDlg();
	final static MainDlg dlgMain = new MainDlg();
	final static Preferences prefs = Preferences.userNodeForPackage(Main.class);

	public enum Steps {
		WELCOME,
		IDENTIFY,
		REJECT,
		REQUEST,
		CONFIG,
		SYNC
	}
	
	public static void main(String[] args) {
		LoaderDlg dlgLoader = new LoaderDlg();
		dlgLoader.create();
		dlgLoader.show();

		String line = null;
		DatagramSocket ssdp = null;

		String msg = "M-SEARCH * HTTP/1.1\r\n"
					 + "Host:239.255.255.250:1900\r\n"
				     + "ST:urn:schemas-upnp-org:service:pilight:1\r\n"
					 + "Man:\"ssdp:discover\"\r\n"
					 + "MX:3\r\n\r\n";		

		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
	        for(NetworkInterface netint : Collections.list(nets)) {
	        	Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	        	for(InetAddress inetAddress : Collections.list(inetAddresses)) {
	        		if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {   
						try {
		        			ssdp = new DatagramSocket(new InetSocketAddress(inetAddress.getHostAddress().toString(), 0));
							byte[] buff = msg.getBytes();
							DatagramPacket sendPack = new DatagramPacket(buff, buff.length);
							sendPack.setAddress(InetAddress.getByName("239.255.255.250"));
							sendPack.setPort(1900);
				
							try {
								ssdp.send(sendPack);
								ssdp.setSoTimeout(50);
								boolean loop = true;
								while(loop) {
									DatagramPacket recvPack = new DatagramPacket(new byte[1024], 1024);
								  	ssdp.receive(recvPack);
								  	byte[] recvData = recvPack.getData();
								  	InputStreamReader recvInput = new InputStreamReader(new ByteArrayInputStream(recvData), Charset.forName("UTF-8"));
									StringBuilder recvOutput = new StringBuilder();
									for(int value; (value = recvInput.read()) != -1;) {
										recvOutput.append((char)value);
									}
									BufferedReader bufReader = new BufferedReader(new StringReader(recvOutput.toString()));
									Pattern pattern = Pattern.compile("Location:([0-9.]+):(.*)");
									while((line=bufReader.readLine()) != null) {
										Matcher matcher = pattern.matcher(line);
										if(matcher.matches()) {
											server = matcher.group(1);
											port = Integer.parseInt(matcher.group(2));
											loop = false;
											break;
										}
									}
								}
							} catch(SocketTimeoutException e) {
							} catch(IOException e) {
								dlgLoader.update(0, "no pilight ssdp connections found");
								ssdp.close();
								try {
								    Thread.sleep(3000);
								} catch(InterruptedException ex) {
								    Thread.currentThread().interrupt();
								}
								System.exit(0);
							}
						} catch(UnknownHostException e) {
						}
					}
	        	}
	        }
        } catch(SocketException e) {
		}
	
		if(server == null || port == 0) {
			dlgLoader.update(0, "no pilight ssdp connections found");
			try {
			    Thread.sleep(3000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			if(ssdp != null) {
				ssdp.close();
			}			
			System.exit(0);
		}

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
	
	    		if(steps != steps.WELCOME) {
		    		if(NSocket.read()) {
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
	    		} else {
	    			has_data = true;
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
	    					NSocket.write("{\"message\":\"client gui\"}");
	    					steps = Steps.IDENTIFY;
	    					dlgLoader.update(25, "Identifying...");
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
