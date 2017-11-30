package org.up.mapReduce;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Test {
    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
        private Text trimestre = new Text();
        private IntWritable Qte=new IntWritable();
        
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] line = value.toString().split(",");
            
            trimestre.set(line[1]);
            Qte.set(Integer.parseInt(line[2]));
            context.write(trimestre,Qte);
        }
    }
    public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }


    public static void main(String[] args) throws Exception {
    	 Configuration conf = new Configuration();

         Job job = new Job(conf, "Quantités vendues par trimestre");
         job.setJarByClass(Test.class);
         job.setOutputKeyClass(Text.class);
         job.setOutputValueClass(IntWritable.class);

         job.setMapperClass(Map.class);
         job.setReducerClass(Reduce.class);

         job.setInputFormatClass(TextInputFormat.class);
         job.setOutputFormatClass(TextOutputFormat.class);

         FileInputFormat.addInputPath(job, new Path(args[0]));
         FileOutputFormat.setOutputPath(job, new Path(args[1]));

         job.waitForCompletion(true);
    }
    
    
}
