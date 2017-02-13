import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Garden {
	final Lock lock = new ReentrantLock();
	final Condition fourDug = lock.newCondition();
	final Condition eightUnfilled = lock.newCondition();
	int holesDug = 0;
    int seedsFilled = 0;
    int holesFilled = 0;
	public Garden() {   }; 
	public void startDigging() {
		holesDug++;
		lock.lock();
		try{
			//int i;
			holesDug++;
			
		}
		finally{
			lock.unlock();
		}
	}; 
	public void doneDigging() {   }; 
	public void startSeeding() {   };
	public void doneSeeding() {   }; 
	public void startFilling() {   }; 
	public void doneFilling() {   }; 
 
    /*
    * The following methods return the total number of holes dug, seeded or 
    * filled by Newton, Benjamin or Mary at the time the methods' are 
    * invoked on the garden class. */
   public int totalHolesDugByNewton() {return holesDug;}; 
   public int totalHolesSeededByBenjamin() {return seedsFilled;}; 
   public int totalHolesFilledByMary() {return holesFilled;}; 
}
