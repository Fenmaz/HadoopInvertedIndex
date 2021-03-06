package edu.macalester.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
//import org.apache.log4j.Logger;

public class InvertedIndex extends Configured implements Tool {

//    private static final transient Logger logger = Logger.getLogger(InvertedIndex.class);


    public static void main(String[] args) throws Exception{
//        logger.info("Customized logging running");
        System.exit(ToolRunner.run(new Configuration(), new InvertedIndex(), args));
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set(XmlInputFormat.START_TAG_KEY, "<page>");
        conf.set(XmlInputFormat.END_TAG_KEY, "</page>");

        if (args.length < 3) {
            System.err.println("Arguments: <file_system> <input_folder> <output_folder>");
            return 2;
        }

        Job job = Job.getInstance(conf, "invertedindex");

        job.setJarByClass(InvertedIndex.class);

        job.setMapperClass(InvertedIndexMapper.class);
        job.setReducerClass(InvertedIndexReducer.class);

        job.setInputFormatClass(XmlInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        Path fsPath = new Path(args[0]);
        Path inputPath = new Path(fsPath, args[1]);
        Path outputPath = new Path(fsPath, args[2]);

        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        FileSystem fs = fsPath.getFileSystem(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        if (job.waitForCompletion(true)) {
            return 0;
        } else return 1;
    }
}
