//am73676_sr39533
public class FairReadWriteLock {
	
    int numWriting = 0;
    int numReaders = 0;
    int nextUp = 0; //holds the ID of the next writer thread that should execute
    int counter = 0;
	
	public synchronized void beginRead() {
		int readID = counter; //give this reader an ID and then increment for the next thread
		counter++;
		
		while(numWriting > 0 || readID!=nextUp){ //if there are writers currently writing or waiting to write, block this reader until they finish
			try{
				wait();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
		nextUp++; //when this thread finishes gaining access, allow another thread in
		numReaders++;
	}
	
	public synchronized void endRead() {
		numReaders--;
		notifyAll();
	}
	
	public synchronized void beginWrite() {
		int writeID = counter; //give this writer an ID then increment 
		counter++;
		
		while(numReaders > 0 || numWriting > 0 || writeID != nextUp){ //if there are readers or other writers in the CS, wait
			try{
				wait();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
		nextUp++; //after this thread finishes gaining access, allow the next thread up
		numWriting++;
	}
	
	public synchronized void endWrite() {
		numWriting--;
		notifyAll();
	}
}
