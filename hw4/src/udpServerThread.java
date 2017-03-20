//am73676_sr39533
import java.io.*;
import java.util.*;
import java.net.*;
public class udpServerThread extends Thread{
	private static int udpPort = Server.udpPort;
	private DatagramSocket datasocket;
	private DatagramPacket datapacket;
	public void run(){
	    try {
	    	datasocket = new DatagramSocket(udpPort);
	    	int len = 65507;
	    	byte[] buf;
	        while (true) {
	          try {
	        	  	  buf = new byte[len];
	        		  datapacket = new DatagramPacket (buf, buf.length); 
	        		  datasocket.receive(datapacket);
	        		  udpMultithreading send = new udpMultithreading(datapacket, datasocket);
	        		  send.start();
	                 } catch (IOException e) {
	                	 System.err.println(e); 
	               } 
	        	}
	        }
	        catch (SocketException se) {
	            System.err.println (se); 
	        }
	}
}