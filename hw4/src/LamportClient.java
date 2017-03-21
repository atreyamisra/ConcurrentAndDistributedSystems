import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;

public class LamportClient extends Thread{
	boolean f;
	public LamportClient(boolean first){
		f=first;
	}
	public void run(){
		while(true){
			while(!Server.wantCS);
			Server.clock.tick();
			Server.requester = String.valueOf(Server.clock.getClock()) + " " + String.valueOf(Server.ID) + " " + Server.message;
			//Server.newRequest=true;
			Server.nod.put(Server.requester, 0);
			//Lamport.addToList(Server.requester);
			if(f){
				for(int i = 1;i<=Server.addresses.size();i++){
					if(i!=Server.ID){
						LamportClientThread t = new LamportClientThread(Server.addresses.get(i), Server.ports.get(i), i);
						t.start();
						Server.request.add(true);
					}
				}
				f=false;
			}
			else{
				for(int i = 0;i<Server.numServers;i++){
					Server.request.set(i, true);
				}
				for(int i = 0;i<Server.numServers;i++){
					if(!Server.dead.get(i) && (i!=(Server.ID-1)))
						while(Server.request.get(i));
				}
			}
			Server.wantCS = false;
			Server.s.release(); //allow other clients to ask for the CS
		}
	}
}