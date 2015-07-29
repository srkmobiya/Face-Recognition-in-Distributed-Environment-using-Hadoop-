package hadoop.face.recognition;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.opencv.core.Core;

import face.recognition.Face;
import face.recognition.Image;
import face.recognition.PreProcessing;

public class FaceRecognitionMapper extends
Mapper<Text ,  BytesWritable , Text, BytesWritable> {

	public static int count=0;
	//public static int numberOfPersons=0;
	public void map(Text key,  BytesWritable value, Context context)
			throws IOException, InterruptedException {
		//Loading native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME );
		//In this image variable image properties will be loaded.
		
		System.out.println("Processing image "+(count++));
		Image image=new Image();

		PreProcessing obj=new PreProcessing();
		obj.faceDetectionAndPreProcessing(key,value,image);

		//key is splitted to get the data;
		String matstring=key.toString();
		String tokens[]=matstring.split(",");

		for (Iterator iterator2 = image.faces.iterator(); iterator2
				.hasNext();) {
			Face face = (Face) iterator2.next();

			//converting warped final face image to byte array so as to send it to reducer
			byte[] warped=new byte[(int)face.warped.total()*(int)face.warped.elemSize()];
			face.warped.get(0, 0, warped);

			//Bytes writable object is created so that it can be transmitted.
			BytesWritable dataReducer=new BytesWritable(warped);

			//key with the name of the person and size of the image i.e. to reconstruct the image 
			// byte array.
			tokens[0]=tokens[0]+","+(int)face.warped.total()*(int)face.warped.elemSize();
			Text keyoutput=new Text(tokens[0]);
			context.write(keyoutput,dataReducer);
		}
	}
}
