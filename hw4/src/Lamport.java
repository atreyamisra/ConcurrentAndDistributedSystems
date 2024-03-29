//sr39533_am73676
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;

public class Lamport extends Thread{
	Socket theServer;
	private InputStream input;
    private OutputStream output;
    private PrintStream printStream;
    private InputStreamReader inputStream;
    private BufferedReader bufferedReader;
    private String message = null;
	public Lamport(Socket s){
    	theServer = s;	
    }
	public void run(){
		try{
	  		  input = theServer.getInputStream();
			  output = theServer.getOutputStream();
			  printStream = new PrintStream(output);
			  inputStream = new InputStreamReader(input);
			  bufferedReader = new BufferedReader(inputStream);
			  message = null;
			  message=bufferedReader.readLine();
	  		  while(message!=null){
	  	  			if(message.equals("hello")){
	  	  				  printStream.println(message);
	  	  				  message=bufferedReader.readLine();
	  	  			}
	  	  			else{
	  	  				if(!message.equals("release"))
	  	  					printStream.println(message);
	  	  				processMessage(message);	  	  					
	  	  				message = bufferedReader.readLine();
	  	  			  }
	  			  
	  		  }
		} catch(Exception e){
			System.err.println(e);
		}
	}
	private synchronized String processMessage(String message){
		if(message.equals("release")){
			String tokens = Server.lamport.pollFirst();
			String process = tokens.substring(tokens.indexOf(' ', tokens.indexOf(' ')+1)+1);
			MultithreadedServer.processMessage(process, true);
			if(Server.lamport.size()!=0){
				tokens = Server.lamport.peek();
				String token[] = tokens.split(" ");
				int nextID = Integer.valueOf(token[1]);
				if(nextID==Server.ID){
					Server.top=true;
				}
			}
			return "";
		}
		else{
			addToList(message, false);
			return message;
		}
	}
	public synchronized static void addToList(String message, boolean local){
		String requestID = message.substring(0, message.indexOf(' ', message.indexOf(' ')+1)+1);
		String[] requestTick = requestID.split(" ");
		int tick = Integer.valueOf(requestTick[0]);
		int sid = Integer.valueOf(requestTick[1]);
		if(!local)
			Server.clock.recieveAction(tick);
		for(int i = 0; i<=Server.lamport.size(); i++){
			if(i==Server.lamport.size()){
				Server.lamport.add(message);
				break;
			}	
			String compareID = Server.lamport.get(i);
			int compareTick = Integer.valueOf(compareID.substring(0, compareID.indexOf(' ')));
			if(compareTick>tick){
				Server.lamport.add(i, message);
				break;
			}	
			else if(compareTick==tick){
				int compareServer = Integer.valueOf(compareID.substring(compareID.indexOf(' ') + 1, compareID.indexOf(' ', compareID.indexOf(' ')+1)+1));
				if (compareServer>sid){
					Server.lamport.add(i, message);
					break;
				}
			}
		}
	}
}