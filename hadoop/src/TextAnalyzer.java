import org.apache.hadoop.fs.Path;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.util.Arrays;
import java.util.Map;

// Do not change the signature of this class
public class TextAnalyzer extends Configured implements Tool {

    // Replace "?" with your own output key / value types
    // The four template data types are:
    //     <Input Key Type, Input Value Type, Output Key Type, Output Value Type>
    public static class TextMapper extends Mapper<LongWritable, Text, Text, Tuple> {

        //writes out all of the <query word, {context words and counts}> key-value pairs for a single line
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
        {
            String line = value.toString().toLowerCase(); //convert the Text input into a lower case string
            
            String[] words = line.split("\\W+"); //split all the words into an array

            for(int i = 0; i < words.length; i++){
                if(words[i].length() > 0){ //check if the word is legitimate
                    Tuple contextWords = new Tuple(); //create the Tuple that will hold the context words and counts for this query word

                    for(int j = 0; j < words.length; j++){ //iterate, treating the current word as the context word
                        if(i != j && words[j].length() > 0){ //check if the context word is not the query word and is legit
                            contextWords.add(words[j]); //add the current context word to the tuple
                        }
                    }

                    context.write(new Text(words[i]), contextWords); //write out the final <query word, {context words and counts}> key-value pair
                }
            }
        }
    }

    // Replace "?" with your own key / value types
    // NOTE: combiner's output key / value types have to be the same as those of mapper
    public static class TextCombiner extends Reducer<Text, Tuple, Text, Tuple> {
        public void reduce(Text key, Iterable<Tuple> tuples, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you combiner function
        }
    }

    // Replace "?" with your own input key / value types, i.e., the output
    // key / value types of your mapper function
    public static class TextReducer extends Reducer<Text, Tuple, Text, Text> {
        private final static Text emptyText = new Text("");

        public void reduce(Text key, Iterable<Tuple> queryTuples, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you reducer function

            Tuple contextWords = new Tuple(); //the Tuple that will aggregate Tuples from all lines for the current context word

            //iterate through the individual line tuples and ombine them together into contextWords
            for(Tuple t : queryTuples){
                for(Writable query : t.getMap().keySet()){
                    contextWords.add((Text)query, (IntWritable)t.getMap().get(query)); //add each query word into the aggregate tuple
                }
            }

            Map<Writable, Writable> map = contextWords.getMap();
            
            String[] queryList = contextWords.sortList();

            Text queryWordText = new Text(); //the Text that holds the current query word

            // Write out the results; you may change the following example
            // code to fit with your reducer function.
            //   Write out the current context key
            context.write(key, emptyText);
            //   Write out query words and their count
            for(String queryWord: queryList){
                String count = ((IntWritable)map.get(new Text(queryWord))).toString() + ">";
                queryWordText.set("<" + queryWord + ",");
                context.write(queryWordText, new Text(count));
            }
            //   Empty line for ending the current context key
            context.write(emptyText, emptyText);
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        // Create job
        @SuppressWarnings("deprecation")
		Job job = new Job(conf, "sr39533_am73676"); // Replace with your EIDs
        job.setJarByClass(TextAnalyzer.class);

        // Setup MapReduce job
        job.setMapperClass(TextMapper.class);
        //   Uncomment the following line if you want to use Combiner class
        // job.setCombinerClass(TextCombiner.class);
        job.setReducerClass(TextReducer.class);

        // Specify key / value types (Don't change them for the purpose of this assignment)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //   If your mapper and combiner's  output types are different from Text.class,
        //   then uncomment the following lines to specify the data types.
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Tuple.class);

        // Input
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(TextInputFormat.class);

        // Output
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputFormatClass(TextOutputFormat.class);

        // Execute job and return status
        return job.waitForCompletion(true) ? 0 : 1;
    }

    // Do not modify the main method
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TextAnalyzer(), args);
        System.exit(res);
    }

    public static class Tuple implements Writable {

        private Map<Writable, Writable> map; //holds context word, count pairs

        public Tuple(){
            map = new MapWritable();
        }

        public void add(String word){
            if(map.containsKey(word)){ //if the word to enter already exists in the set, just increment count
                map.put(new Text(word), new IntWritable(((IntWritable)map.get(word)).get() + 1));
            }
            else{
                map.put(new Text(word), new IntWritable(1));
            }
        }

        public void add(Text word){
            if(map.containsKey(word)){ //if the word to enter already exists in the set, just increment count
                map.put(word, new IntWritable(((IntWritable)map.get(word)).get() + 1));
            }
            else{
                map.put(word, new IntWritable(1));
            }
        }
        
        public void add(Text word, IntWritable count){
            if(map.containsKey(word)){ //if the word to enter already exists in the set, just increment count
                map.put(word, new IntWritable(((IntWritable)map.get(word)).get() + count.get()));
            }
            else{
                map.put(word, count);
            }
        }

        public Map<Writable, Writable> getMap(){
            return map;
        }
        
        public String[] sortList(){
        	Writable[] queries = (Writable[]) map.keySet().toArray(new Writable[map.keySet().size()]);
        	String[] q = new String[queries.length];
        	
        	for(int i = 0; i<q.length; i++){
        		q[i] = ((Text)queries[i]).toString();
        	}
        	Arrays.sort(q);
        	return q;
        }

        @Override
        public void write(DataOutput out) throws IOException{
            ((MapWritable)map).write(out); //serialize the map
        }        

        @Override
        public void readFields(DataInput in) throws IOException{
        	((MapWritable)map).readFields(in); //deserialize the map
        }

        @Override
        public boolean equals(Object o)
        {
            if(o instanceof MapWritable)
            {
                return ((MapWritable)map).equals(o);
            }
        
            return false;
        }

        @Override
        public int hashCode(){
            return ((MapWritable)map).hashCode();
        }

     }
}
