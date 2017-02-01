//UT-EID=sr39533
//UT-EID=am73676


import java.util.*;
import java.util.concurrent.*;


public class PSearch implements Callable<Integer>{
	
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	private static Future<Integer> threadList[]; // Instantiate to correct size in parallelSearch method
	private static int x; //the term to search for
	private static int B[]; //the array to search thru
	
	private int start, end;

	
	public PSearch(int start, int end){
		//dummy shell used to create threads
		this.start = start;
		this.end = end;
	}
	
	public Integer call(){
		//this will initiate the search for each thread
		return searchTool(start, end);
	}
	
	public static int parallelSearch(int k, int[] A, int numThreads){
		// this will initiate the threads and allocate chunks of the array
		try {
			ExecutorService es = Executors.newSingleThreadExecutor();
			
			x = k; //set the static global search term to use in the threaded searches
			B = A; //set the static global array
			
			if(A == null || A.length < 1 || numThreads <1){
				es.shutdown ();
			    threadPool.shutdown();
				return -1;
			}
			
			else if(A.length < numThreads){
				//if there are more threads than array elements, create necessary threads and then create dummy threads that don't do anything
				threadList = (Future<Integer>[])(new Future[numThreads]);
				
				int threads = 0; //the number of threads that have been created
				
				while(threads < A.length){ //create as many threads as there are elements
					PSearch p = new PSearch(threads, threads+1);
			        Future<Integer> f = es.submit(p);
			        threadList[threads] = f;
			        threads++; //increment the number of existing threads
				}
				
				while(threads < numThreads){ //create enough threads to fill the thread quota
					PSearch p = new PSearch(0,0); //a dummy thread which will not enter the search process
			        Future<Integer> f = es.submit(p);
			        threadList[threads] = f;
			        threads++; //increment the number of existing threads
				}
		        
		        for(int i = 0; i < numThreads; i++){
		        	int res = (int)((Future<Integer>) threadList[i]).get();
		        	if(res != -1){
				        es.shutdown ();
				        threadPool.shutdown();
		        		return res; //if one of the results of the threads is not -1, return that result
		        	}
		        }
			}
			
			else{
				threadList = (Future<Integer>[])(new Future[numThreads]);
				
				int N = (A.length)/numThreads; //the number of elements handled by each thread
				int index = 0; //the start index of the current thread
				int threads = 0; //the number of thread that has been created
				
				while(threads < numThreads-1){
					PSearch p = new PSearch(index, index + N);
			        Future<Integer> f = es.submit(p);
			        threadList[threads] = f;
			        threads++; //increment the number of existing threads
			        index += N; //increment the next start index to the end of the current thread
				}
				
				PSearch p = new PSearch(index, A.length); //create the last thread
		        Future<Integer> f = es.submit(p);
		        threadList[threads] = f;
		        
		        for(int i = 0; i < numThreads; i++){
		        	int res = (int)((Future<Integer>) threadList[i]).get();
		        	if(res != -1){
				        es.shutdown ();
				        threadPool.shutdown();
		        		return res; //if one of the results of the threads is not -1, return that result
		        	}
		        }
			}
			
	        es.shutdown ();
	        threadPool.shutdown();
			return -1; // if not found
		}
		
		catch (Exception e) { 
			System.out.println("ERROR DELEGATING THREADS");
			return -100; //error code TODO change this to -1
		}
	}
	
	public static int searchTool(int start, int end){
		//the actual search method, given start and end indices
		for (int i = start; i < end; i++){
			if(B[i] == x)
				return i;
		}
		
		return -1;
	}
}
