package classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class NSocket {

	private static Socket socket;
	private static BufferedReader bufferedReader = null;
	private static PrintStream printStream = null;
	private static String line = null;
	
	private static long heart = new Date().getTime();
	private static long beat = new Date().getTime();
	private static long timeout = 5000;
	private static boolean isConnected = false;
	
	public static class HeartBeat implements Runnable {
		public void run() {
			try {
				while(!Thread.interrupted()) {
					NSocket.write("HEART");
					Thread.sleep(timeout/5);
					NSocket.heart = new Date().getTime();
				}
			} catch(InterruptedException e) {
			}		
		}
	}
	
	public static Socket connect(String server, int port) {
	       try {
	           socket = new Socket();
	           socket.connect(new InetSocketAddress(server, port), 1000);
	           socket.setSoTimeout(30000);
	       } catch (UnknownHostException e) {
	           //Log.printf("Don't know about host");
	    	   isConnected = false;
	    	   return null;
	       } catch(IOException e) {
	    	   //Log.printf("Could not get I/O for connection");
	    	   isConnected = false;
	    	   return null;
	       }
	       Thread thread = new Thread(new NSocket.HeartBeat());
	       thread.start();	    
    	   isConnected = true;
	       return socket;		
	}
	
	public static boolean connected() {
		if(isConnected) {
			if((NSocket.heart-NSocket.beat) > timeout) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	public static void close() {
		try {
			if(bufferedReader != null) {
				bufferedReader.close();
			}
			if(printStream != null) {
				printStream.close();
			}
			if(socket != null) {
				socket.close();
			}
		} catch(IOException e) {
			//Log.printf("Could not close connection to server");
		}
	}
	
	public static String getLine() {
		return line;
	}
	
	public static boolean read(int size) {
		if(size == 0) {
			size = 1025;
		}
		try {
			if(bufferedReader == null) {
				bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"), size);
			}
			if(bufferedReader.ready()) {
				if((line = bufferedReader.readLine()) != null) {
					if(new String("BEAT").equals(line)) {
						NSocket.beat = new Date().getTime();
						return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch(IOException e) {
			isConnected = false;
			//Log.printf("Failed to receive messages from server");
		}
		return true;
	}
	
	public static boolean write(String message) {
		try {
			if(printStream == null) {
				printStream = new PrintStream(socket.getOutputStream(), false);
			}
			printStream.print(message+"\n");
			printStream.flush();
			Thread.sleep(10);
			return true;
		} catch(InterruptedException e) {
			isConnected = false;
			//Log.printf("Failed to receive messages from server");
		} catch(IOException e) {
			isConnected = false;
			//Log.printf("Failed to write messages to server");
		}
		return false;
	}
}
