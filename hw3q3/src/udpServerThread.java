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
	        		  String message = new String(datapacket.getData());
	        		  InetAddress sender=datapacket.getAddress();
	        		  int senderPort=datapacket.getPort();
	        		  String response = MultithreadedServer.processMessage(message);
	        		  byte[] responseBytes=response.getBytes();
	        		  DatagramPacket sendPack=new DatagramPacket(responseBytes, responseBytes.length, sender, senderPort);
	        		  datasocket.send(sendPack);
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