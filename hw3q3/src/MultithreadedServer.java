import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

public class MultithreadedServer extends Thread{
	Socket theClient;
	private static Hashtable<String, Integer> items = Server.items;
	private static boolean tcpOn = Server.tcpOn;
	private static OrderLog ol = new OrderLog();
    public MultithreadedServer(Socket s){
    	theClient = s;	
    }
    public void run(){
  	  try{
  		  InputStream input = theClient.getInputStream();
		  OutputStream output = theClient.getOutputStream();
		  PrintStream printStream = new PrintStream(output);
		  InputStreamReader inputStream = new InputStreamReader(input);
		  BufferedReader bufferedReader = new BufferedReader(inputStream);
		  String message = null;
		  message=bufferedReader.readLine();
  		  while(message!=null){
  			  String response=processMessage(message);
  			  printStream.println(response);
    		  message = bufferedReader.readLine();
  		  }
  	  }
  	  catch(Exception e){
  		  System.err.println(e);
  	  }
    }
    public static String processMessage(String m){
    	String[] n = m.split(" ");
    	if(n[0].equals("setmode")){
    		
    	}
    	else if(n[0].equals("purchase")){
    		int state = decreaseInventory(n[2],Integer.valueOf(n[3]));
			if(state == 0){
				Order o = new Order(n[1],n[2],Integer.valueOf(n[3]));
				ol.add(o);
				return "Your order has been placed, " + o.getId() + " " + n[1] +" " + n[2] + " " + n[3];
			}	
    		else if(state == 1)
    			return "Not Available - Not enough items";
    		else if(state == 2)
    			return "Not Available - We do not sell this product";
    	}
    	else if(n[0].equals("cancel")){
    		Order o = ol.cancel(Integer.valueOf(n[1]));
    		if(o!=null){
    			increaseInventory(o.getProduct(), o.getQuantity());
    			return "Order " + n[1] + " is canceled";
    		}
    		else
    			return n[1] + " not found, no such order";
    		
    	}
    	else if(n[0].equals("search")){
    		ArrayList<Order> a = ol.search(n[1]);
    		if(a.isEmpty())
    			return "No orders found for " + n[1];
    		else{
    			return String.valueOf(a.size())+ "\n" + ol.ordersToString(a);
    		}
    			
    	}
    	else if(n[0].equals("list")){
    		String s = "";
    		int lines = 0;
    		Set<String> keys = items.keySet();
    		for(String a: keys){
    			s+= a + " " + items.get(a) + "\n";
    			lines++;
    		}
    		s=String.valueOf(lines) +"\n" + s.trim();
    		return s;
    	}
    	return m;
    }
    public synchronized static void increaseInventory(String product, int amount){
    	int count = items.get(product);
  	  	count+=amount;
  	  	items.put(product, count);
    }
    public synchronized static int decreaseInventory(String product, int amount){
    	if(items.get(product)==null)
    		return 2;
    	else if(amount>items.get(product))
  	  		return 1;
  	  	int count = items.get(product);
  	  	count-=amount;
  	  	items.put(product, count);
  	  	return 0;
    }
    public synchronized void changePort(){
  	  if(!tcpOn)
  		  tcpOn = true;
  	  else
  		  tcpOn = false;
    }
}