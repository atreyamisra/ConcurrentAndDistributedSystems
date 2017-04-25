import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.apache.hadoop.io.Text;
import org.apache.spark.SparkConf;
import scala.Tuple2;



public class SparkTextAnalyzer {
  public static void main(String[] args) {

    // create Spark context with Spark configuration
    JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("Spark Count"));

    if(args.length!=2){
    	System.out.println("Incorrect number of arguments");
    	
    	return;
    }
    // get threshold
   // final int threshold = Integer.parseInt(args[1]);

    // read in text file map each word in each line to a Tuple keyed on the word and a treemap with the occurences of the other words in the line
    JavaPairRDD<String,TreeMap<String,Integer>> tokenized = sc.textFile(args[0]).flatMapToPair(new PairFlatMapFunction<String,String,TreeMap<String,Integer>>(){

		@Override
		public Iterator<Tuple2<String, TreeMap<String, Integer>>> call(String arg0) throws Exception {

        	String line=arg0.toString().toLowerCase();
        	String[] words=line.split("\\W+");
        	TreeMap<String,Integer> w=new TreeMap<String,Integer>();
        	ArrayList<Tuple2<String,TreeMap<String,Integer>>> res=new ArrayList<Tuple2<String,TreeMap<String,Integer>>>();
        	for(int i=0;i<words.length;i++){
        		String word1=words[i];
        		if(word1.length()>0){
        			if(w.get(word1)==null){
        				w.put(word1, 1);
        				
        			}else{
        				w.put(word1, 1+w.get(word1));
        				
        			}
        		}
        	}
        	for(String a:w.keySet()){
        		TreeMap<String,Integer> cop=new TreeMap<String,Integer>();
        		cop.putAll(w);
        		if(cop.get(a)==1){
        			cop.remove(a);
        		}
        		else{
        			cop.put(a, cop.get(a)-1);
        		}
        		res.add(new Tuple2(a,cop));
        		
        	}
        	return res.iterator();
		}
    });
    
    
    
    JavaPairRDD<String, TreeMap<String, Integer>> reducedPairs=tokenized.reduceByKey(new Function2<TreeMap<String,Integer>,TreeMap<String,Integer>,TreeMap<String,Integer>>(){

		@Override
		public TreeMap<String, Integer> call(TreeMap<String, Integer> arg0, TreeMap<String, Integer> arg1){
			TreeMap<String,Integer> larger;
			TreeMap<String,Integer> smaller;
				if(arg0.size()>arg1.size()){
					larger=arg0;
					smaller=arg1;
					
				}else{
					larger=arg1;
					smaller=arg0;
					
				}
				
				for(String a:smaller.keySet()){
					if(larger.get(a)!=null){
						larger.put(a, larger.get(a)+smaller.get(a));
					}
					else{
						larger.put(a, smaller.get(a));
					}
				}
				return larger;
		}
    	
    });
  reducedPairs=reducedPairs.sortByKey(true, 1);
    JavaRDD<String> wordQueries=reducedPairs.map(new Function<Tuple2<String,TreeMap<String,Integer>>,String>(){

		@Override
		public String call(Tuple2<String, TreeMap<String, Integer>> arg0) throws Exception {
			String ret="\n";
			ret=ret+arg0._1;
			TreeMap<String,Integer> map=arg0._2;
			for(String k:map.keySet()){
				ret=ret+"\n<"+k+", "+map.get(k)+">";
			}
		//	ret=ret+"\n";
			return ret;
			
		}
    	
    });
  
    wordQueries.coalesce(1,true).saveAsTextFile(args[1]);
   
  }
}