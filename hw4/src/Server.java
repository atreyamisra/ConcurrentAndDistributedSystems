import java.util.Scanner;
import java.util.ArrayList;

public class Server {
	
	public static int ID; //this server's ID
	public static int numServers; //the total number of servers
	public static String myAddress; //this server's IP
	public static int myPort;
	public static ArrayList<String> addresses; //the list of servers' addresses
	public static ArrayList<Integer> ports; //the list of servers' ports
	public static LogicalClock clock;
	
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		
		String filename;
		boolean inputOK = true;
		
		//Grab the first line of inputs
		do{ //if inputs are incorrect, try again
			String params = sc.nextLine(); //grab the first line of the input
			String[] inputs = (params.trim()).split(" "); //array that holds the three first inputs to the server
			
			try{
				ID = Integer.parseInt(inputs[0]);
				numServers = Integer.parseInt(inputs[1]);
				filename = inputs[2];
			}
			catch(Exception e){
				inputOK = false;
				System.out.println("Wrong input - try again!");
			}
		}
		while(!inputOK);
		
		//Grab the IPs and ports of all the servers
		for(int i = 0; i < numServers; i++){
			do{ //if inputs are incorrect, try again
				String next = sc.nextLine();
				String[] parsed = (next.trim()).split(":");
				
				try{
					ports.add(Integer.parseInt(parsed[1]));
					addresses.add(parsed[0]);
				}
				catch(Exception e){
					inputOK = false;
				}
			}
			while(!inputOK);
		}
		
		//Extract this server's info from the lists of IPs and Ports
		myAddress = addresses.get(ID - 1);
		myPort = ports.get(ID - 1);
		
		sc.close();
		
		//TODO: spin off Server communication threads, link to other servers, listen for clients, spin off client handlers
		clock = new LogicalClock();
		
	}
}
