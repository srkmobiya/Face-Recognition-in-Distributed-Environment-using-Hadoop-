package hadoop.face.recognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import face.recognition.FaceRecognition;
import face.recognition.TrainData;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FaceRecognitionReducer extends Reducer<Text, BytesWritable, Text, Text> {

	public static int x=0;
	public static int numberOfPersons=3; 	//set this without this algo wouldnot work
	public static List<TrainData> listOfFaceImg=new ArrayList<TrainData>();
	public void reduce(Text key, Iterable<BytesWritable> values, Context context)
			throws IOException, InterruptedException {

		int i=0;
		for (BytesWritable val : values) {
			System.out.println("Adding image of : "+key.toString()+" "+i+" on reducer "+x);
			TrainData obj=new TrainData(key.toString(), val);
			listOfFaceImg.add(obj);
			++i;
		}


		

		//learning new data

		if(x+1==numberOfPersons)
		{
			FaceRecognition faceRecognition = new FaceRecognition();
			System.out.println("learning from training data");
			faceRecognition.learn(listOfFaceImg);
		}
		//recognizing the given list
		//faceRecognition.recognizeFileList("input/test.txt");

		//Predicting faces for query file.
		//faceRecognition.predictFileList("input/query.txt");
		++x;
		context.write(new Text("Done"),new Text("yup"));
		
	}
}
