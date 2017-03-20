import java.io.*
import java.net.*;

public class ConnectionListener extends Thread{
	private ServerSocket ss;
	
	public ConnectionListener(int port){
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ConnectionListener error - couldn't set up ServerSocket");
		}
	}
	
	@Override
	public void run(){
		Socket s = null; //the socket that connect to the server
		while(true){
			try {
				s = ss.accept();
			} catch (IOException e) {
				System.out.println("ConnectionListener error - connection could not be accepted");
			}
			
			if (s == null)//if the accepted connection is null, try again
				continue;
			
			//Read the first line of the input buffer to determine if it is a client or another server trying to contact you
	  		InputStream input = s.getInputStream();
			OutputStream output = s.getOutputStream();
			PrintStream printStream = new PrintStream(output);
			InputStreamReader inputStream = new InputStreamReader(input);
			BufferedReader bufferedReader = new BufferedReader(inputStream);
			String message;
			try{
				message = bufferedReader.readLine();
			}
			catch(IOException i){
				System.out.println("ConnectionListener error - couldn't read buffer");
			}
  			if(message.equals("hi")){
  				printStream.println(message);
  				Thread t = new MultithreadedServer(s);
  				t.start();
  			  }
  			else if(message.equals("hello")){
  				printStream.println(message);
				Thread l = new Lamport(s);
				l.start();
  			}
  			
		}
	}
}
