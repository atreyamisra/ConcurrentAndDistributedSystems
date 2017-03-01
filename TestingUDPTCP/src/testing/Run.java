package testing;

import edu.umd.cs.mtc.TestFramework;

public class Run {

	
	public static void main(String[] args) throws Throwable {
	
		
		
		TestFramework.runOnce(new TestUDPTCP1());
		System.out.println("All tests passed!");
	}
}
