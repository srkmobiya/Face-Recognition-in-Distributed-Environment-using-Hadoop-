package hadoop.face.recognition;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.BasicConfigurator;

public class FaceRecognition {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		//BasicConfigurator.configure();

		JobConf jobconf=new JobConf(conf);
		configure(jobconf);

		Job job = Job.getInstance(conf, "FaceRecognition");
		job.setInputFormatClass(SequenceFileInputFormat.class);

		job.setJarByClass(hadoop.face.recognition.FaceRecognition.class);
		job.setMapperClass(hadoop.face.recognition.FaceRecognitionMapper.class);
		
		// TODO: specify output types for mapper
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(BytesWritable.class);

		job.setReducerClass(hadoop.face.recognition.FaceRecognitionReducer.class);

		// TODO: specify output types
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		// TODO: specify input and output DIRECTORIES (not files)
		FileInputFormat.setInputPaths(job, new Path("hdfs://127.0.0.1:9100/user/slave/input"));
		FileOutputFormat.setOutputPath(job, new Path("hdfs://127.0.0.1:9100/user/slave/output"));

		if (!job.waitForCompletion(true))
			return;
	}
	public static void configure(JobConf conf) {
		System.out.println("Number of map tasks: "+conf.get("mapred.map.tasks"));
		System.out.println("Nubmer of reduce tasks: "+conf.get("mapred.reduce.tasks"));
	}

}
