import java.util.Arrays;

public class SimpleTest {
  public static void main (String[] args) {
	int[] A6 = {6099, 8774, 4060, 8090, 9635, 8204, 1404, 3707, 6735, 7368, 8748, 9149, 6565, 9223, 1086, 1552, 3244, 6038, 1624, 2551, 7243, 9900, 8995, 7404, 8654, 8425, 7327, 876, 9635, 3240, 9880, 9363, 8709, 6752, 5635, 1183, 4007, 2195, 911, 6000, 6027, 3034, 4849, 9957, 3055, 431, 8459, 5222, 3967, 1637};
	verifyParallelSort(A6);
	    
	int[] A1 = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    verifyParallelSort(A1);
    
    int[] A2 = {1, 3, 5, 7, 9};
    verifyParallelSort(A2);
    
    int[] A3 = {13, 59, 24, 18, 33, 20, 11, 11, 13, 50, 10999, 97};
    verifyParallelSort(A3);
    
    int[] A4 = {4, 9, 69, 3, 3, 7, 13, 0, 0, 9, 15, 13, 19};
    verifyParallelSort(A4);
    
    int[] A5=arrayMaker(50);
    verifyParallelSort(A5);
  }
  public static int[] arrayMaker(int n){
	  
	  int[] ar=new int[n];
	  for(int i=0;i<n;i++){
		  ar[i]=(int)(Math.random()*10000);
	  }
	  return ar;
	  
  }
  static void verifyParallelSort(int[] A) {
    int[] B = new int[A.length];
    System.arraycopy(A, 0, B, 0, A.length);

    System.out.println("Verify Parallel Sort for array: ");
    printArray(A);

    Arrays.sort(A);
    PSort.parallelSort(B, 0, B.length);
   
    boolean isSuccess = true;
    for (int i = 0; i < A.length; i++) {
      if (A[i] != B[i]) {
        System.out.println("Your parallel sorting algorithm is not correct");
        System.out.println("Expect:");
        printArray(A);
        System.out.println("Your results:");
        printArray(B);
        isSuccess = false;
        break;
      }
    }

    if (isSuccess) {
      System.out.println("Great, your sorting algorithm works for this test case");
    }
    System.out.println("=========================================================");
  }

  public static void printArray(int[] A) {
    for (int i = 0; i < A.length; i++) {
      if (i != A.length - 1) {
        System.out.print(A[i] + " ");
      } else {
        System.out.print(A[i]);
      }
    }
    System.out.println();
  }
}
