
public class LogicalClock implements Comparable<LogicalClock>{
	
	int clock;
	int serverID;
	
	public LogicalClock (){
		clock = 1;
	}
	
	public synchronized void tick(){
		clock++;
	}
	
	public synchronized void sendAction(){
		clock++;
	}
	
	public synchronized int getClock(){
		return clock;
	}
	
	public synchronized int getID(){
		return serverID;
	}
	
	public synchronized void recieveAction(int other){
		if(clock > other)
			clock++;
		else
			clock = other + 1;
	}
	
	public synchronized void recieveAction(LogicalClock other){
		if(clock > other.clock)
			clock++;
		else
			clock = other.clock + 1;
	}

	@Override
	public int compareTo(LogicalClock arg0) {
		if(clock < arg0.clock)
			return -1;
		else if (clock > arg0.clock)
			return 1;
		else{
			if(serverID < arg0.serverID)
				return -1;
			else if (serverID == arg0.serverID)
				return 0;
			else
				return 1;
		}
	}
}
