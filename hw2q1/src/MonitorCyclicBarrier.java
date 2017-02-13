/*
 * EID's of group members
 * 
 */

public class MonitorCyclicBarrier {
	
	final int barrierNum;
	int numReleased; //the number of threads that have been released from the barrier
	int index;
	
	public MonitorCyclicBarrier(int parties) {
		barrierNum = parties;
		index = parties - 1; //set the starting index
		numReleased = 0;
	}
	
	// Waits until all parties have invoked await on this barrier.
	// If the current thread is not the last to arrive then it is
	// disabled for thread scheduling purposes and lies dormant until
	// the last thread arrives.
	// Returns: the arrival index of the current thread, where index
	// (parties - 1) indicates the first to arrive and zero indicates
	// the last to arrive.
	public synchronized int await() throws InterruptedException {
		int myIndex = index; //assign this thread the current index
        index--; //decrement the index when a new thread enters
		
        if(index == -1) //if the latest thread to arrive fulfills the quota, wake all the threads and reset the counter
        	notifyAll();
        
        while(index != -1)
        	wait();
         
        numReleased++; //increment the number of threads released from the barrier
        
        if(numReleased == barrierNum){ //if all of the waiting threads have been released, reset the barrier
        	numReleased = 0;
        	index = barrierNum-1;
        }
	    return myIndex;
	}
}
