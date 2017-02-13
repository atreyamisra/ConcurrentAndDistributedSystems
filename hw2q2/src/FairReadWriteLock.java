/*
 * Implement a Java class FairReadWriteLock that synchronizes reader and writer
threads using monitors with wait, notify and notifyAll methods. The class should provide
the following methods: void beginRead(), void endRead(), void beginWrite(), and void
endWrite(). A reader thread only invokes beginRead() and endRead() while a writer thread
only invokes beginWrite() and endWrite(). In addition, the lock (instance of this class)
should provide the following properties:
1
(a) There is no read-write or write-write conflict.
(b) A writer thread that invokes beginWrite() will be blocked until all preceding reader
and writer threads have acquired and released the lock.
(c) A reader thread that invokes beginRead() will be blocked until all preceding writer
threads have acquired and released the lock.
(d) A reader thread cannot be blocked if any preceding writer thread has acquired and
released the lock.
The precedence of threads is determined by the timestamp (sequence number) that threads
obtain on arrival.
 */

public class FairReadWriteLock {
	
    int numWriters = 0;
    int numReaders = 0;
    int numWriteRequests = 0;
    int nextUp = 0; //holds the ID of the next writer thread that should execute
	
	public synchronized void beginRead() {
		while(numWriters > 0 || numWriteRequests > 0){ //if there are writers currently writing or waiting to write, block this reader until they finish
			try{
				wait();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
		numReaders++;
	}
	
	public synchronized void endRead() {
		numReaders--;
		notifyAll();
	}
	
	public synchronized void beginWrite() {
		int writeID = nextUp;
		numWriteRequests++;
		
		while(numReaders > 0 || numWriters > 0 || writeID != nextUp){ //if there are readers or other writers in the CS, wait
			try{
				wait();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
		nextUp++; //the next highest
		numWriteRequests--; //this thread is no longer just a request - it is a writer
		numWriters++;
	}
	
	public synchronized void endWrite() {
		numWriters--;
		notifyAll();
	}
}
