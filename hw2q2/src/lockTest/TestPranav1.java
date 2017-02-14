package lockTest;
import edu.umd.cs.mtc.MultithreadedTestCase;
public class TestPranav1 extends MultithreadedTestCase{
	

	
	
	public void thread1() throws InterruptedException {
		waitForTick(1);
		Run.lock.beginRead();
		waitForTick(2);
		Run.lock.endRead();
	}
	
	public void thread2() throws InterruptedException {
		waitForTick(1);
		Run.lock.beginRead();
		assertTick(1);
		waitForTick(3);
		Run.lock.endRead();
	}
	public void thread3() throws InterruptedException {
		waitForTick(2);
		Run.lock.beginWrite();
		assertTick(3);
		waitForTick(4);
		Run.lock.endWrite();
	}
	public void thread4() throws InterruptedException {
		waitForTick(3);
		Run.lock.beginRead();
		assertTick(4);
		waitForTick(5);
		Run.lock.endRead();
		
	}
	public void thread5() throws InterruptedException {
		waitForTick(4);
		Run.lock.beginWrite();
		assertTick(5);
		Run.lock.endWrite();
	}
	
	public void thread6() throws InterruptedException {
		waitForTick(5);
		Run.lock.beginRead();
		waitForTick(8);
		Run.lock.endRead();
		
	}
	public void thread7() throws InterruptedException {
		waitForTick(6);
		Run.lock.beginWrite();
		assertTick(8);
		waitForTick(9);
		Run.lock.endWrite();
	}
	public void thread8() throws InterruptedException {
		waitForTick(7);
		Run.lock.beginRead();
		assertTick(9);
		Run.lock.endRead();
		
	}
	public void thread9() throws InterruptedException {
		waitForTick(5);
		Run.lock.beginRead();
		waitForTick(6);
		Run.lock.endRead();
		
		
	}
	
	
}