//UT-EID=am73676_sr39533


import java.util.*;
import java.util.concurrent.*;

public class PSort implements Callable<Void>{
      public static ExecutorService threadPool = Executors.newCachedThreadPool();
	  int n;
	  public PSort(int A[], int begin, int end){
		  this.A = A;
		  this.begin = begin;
		  this.end = end;
	  }
	  int A[], begin, end, index;
	  
		public static void parallelSort(int[] A, int begin, int end){
			threadPool = Executors.newCachedThreadPool();
			PSort start = new PSort(A, begin, end);
	    	Future<Void> s = threadPool.submit(start);
	    	try{
	    		s.get();
	    	}
	    	catch (Exception e) { System.err.println (e);}
	        threadPool.shutdown();
		}
		
	  public Void call() {
	    try {
	    	int index = partition(A, begin, end);
	    	if(A == null)
	    		return null;
	    	if(end - begin < 4){
		    	int b = begin;
		    	int e = end - 1;
		    	int value = 0;
		    	for(; b <= e; b++){
		    		if ((b + 1) == A.length) return null;
		    		value = A[b + 1];
		    		int j = b;
		    		
		    		while(j>=(begin-1) && A[j] > value){
		    			A[j+1] = A[j];
		    			j = j - 1;
		    			if(j == -1)
		    				break;
		    		}
		    		A[j+1] = value;		
		    	}
		    	return null;
		    }
		    Future<Void> l = null;
		    Future<Void> r = null;
		    if (begin < index -1){
		    	PSort left = new PSort(A, begin, index);
		    	l = threadPool.submit(left);
		    	}
		    if (index < end){
		    	PSort right = new PSort(A, index, end);
		    	r = threadPool.submit(right);
		    	}
	    	try{
	    		l.get();
	    	}
	    	catch (Exception e) { //System.err.println (e);
	    	int sriram = 1;}
	    	try{
	    		r.get();
	    	}
	    	catch (Exception e) { System.err.println (e);
	    	int sriram = 1;}
		    
	       return null;
	    } catch (Exception e) { //System.err.println (e); 
	    return null;}
	  }
 
  public static int partition(int[] A, int begin, int end){
	  int pivot = A[(begin+end)/2]; 
	  int b = begin;
	  int e = end - 1;
	  while(b <= e){
		  while(A[b] < pivot) b++;
		  while(A[e] > pivot) e--;
		  if(b <= e){
			  int temp = A[b];
			  A[b] = A[e];
			  A[e] = temp;
			  b++;
			  e--;
		  }
	  }
	  return b;
  }

}
