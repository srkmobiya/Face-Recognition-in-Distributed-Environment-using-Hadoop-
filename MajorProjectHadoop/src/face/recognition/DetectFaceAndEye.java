package face.recognition;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import face.recognition.*;
public class DetectFaceAndEye {


	public List<MatOfRect> ListOfMatOfRectFaceImages=new ArrayList<MatOfRect>();
	private float EYE_SX;
	private float EYE_SY;
	private float EYE_SW;
	private float EYE_SH;

	private int leftX ;
	private int topY ;
	private int widthX ;
	private int heightY ;
	private int rightX ;

	public void detectFace(Image image) {

		//Create a face detector from the cascade file in the resources
		String frontFaceResource="resources/lbpcascades/lbpcascade_frontalface.xml";
		CascadeClassifier faceDetector = new CascadeClassifier(frontFaceResource);

		Size minFeatureSize=new Size(10, 10);// Smallest face size.
		int flags = Objdetect.CASCADE_SCALE_IMAGE; 	// Search for many faces.
		//int flags=Objdetect.CASCADE_FIND_BIGGEST_OBJECT; //Search for largest face.
		double searchScaleFactor = 1.1; 	// How many sizes to search.
		int minNeighbors = 3; 				// Reliability vs many faces.
		Size maxFeatureSize=image.resizedImg.size();
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image.resizedImg, faceDetections, searchScaleFactor, minNeighbors, flags, minFeatureSize, maxFeatureSize);
		image.matOfRectFaceResized=faceDetections;
		if(faceDetections.toArray().length==0){

		}
		else{
			for(Rect rect: image.matOfRectFaceResized.toArray()){
				Face face=new Face();
				face.faceRectResized=rect;
				image.faces.add(face);
			}
		}
	}
	void setSizeAndLocationForEye(int val,Mat face){
		switch (val) {
		case 0:
			//haarcascade_eye.xml 
			EYE_SX=(float)0.16;
			EYE_SY=(float)0.26;
			EYE_SW=(float)0.30;
			EYE_SH=(float)0.28;
			break;
		case 1:
			//haarcascade_mcs_lefteye.xml 
			EYE_SX=(float)0.10;
			EYE_SY=(float)0.19;
			EYE_SW=(float)0.40;
			EYE_SH=(float)0.36;
			break;
		case 2:
			//haarcascade_lefteye_2splits.xml
			EYE_SX=(float)0.12;
			EYE_SY=(float)0.17;
			EYE_SW=(float)0.37;
			EYE_SH=(float)0.36;
			break;
		}
		leftX = Math.round(face.cols() * EYE_SX);
		topY = Math.round(face.rows() * EYE_SY);
		widthX = Math.round(face.cols() * EYE_SW);
		heightY = Math.round(face.rows() * EYE_SH);
		rightX = Math.round(face.cols() * (float)(1.0-EYE_SX - EYE_SW));
	}
	public void detectEye(Image obj){
		CascadeClassifier eyeDetector1 = new CascadeClassifier("resources/haarcascades/haarcascade_eye.xml");
		CascadeClassifier eyeDetector2 = new CascadeClassifier("resources/haarcascades/haarcascade_eye_tree_eyeglasses.xml");
		if(eyeDetector1.empty())
			System.out.println("Eye detector 1 is empty.");
		if(eyeDetector2.empty())
			System.out.println("Eye detector 2 is empty.");
		for (Iterator<Face> iterator2 = obj.faces.iterator(); iterator2
				.hasNext();) {
			Face face = (Face) iterator2.next();

			//setting values for first classifier
			setSizeAndLocationForEye(0, face.faceImg);
			//Left eye detection
			Mat topLeftOfFace = new Mat(face.faceImg,new Rect(leftX, topY, widthX,heightY));
			MatOfRect lefteyeRect=new MatOfRect();

			eyeDetector1.detectMultiScale(topLeftOfFace, lefteyeRect, 1.1, 1, Objdetect.CASCADE_FIND_BIGGEST_OBJECT, new Size(0,0),topLeftOfFace.size() );
			// If it failed, search the left region using the 2nd eye detector.
			Rect[] leftEyeRectArray=lefteyeRect.toArray();
			if (leftEyeRectArray.length==0){
				eyeDetector2.detectMultiScale(topLeftOfFace, lefteyeRect, 1.1, 1, Objdetect.CASCADE_FIND_BIGGEST_OBJECT, new Size(0,0),topLeftOfFace.size() );
			}

			// Get the left eye center if one of the eye detectors worked.
			Point leftEyeCentre =new  Point(-1,-1);
			if(leftEyeRectArray.length >0) {
				leftEyeCentre.x = leftEyeRectArray[0].x + leftEyeRectArray[0].width/2 + leftX;
				leftEyeCentre.y = leftEyeRectArray[0].y + leftEyeRectArray[0].height/2 + topY;
			}

			// Right eye Detection
			Mat topRightOfFace = new Mat(face.faceImg,new Rect(rightX, topY, widthX,heightY));
			MatOfRect righteyeRect=new MatOfRect();

			eyeDetector1.detectMultiScale(topRightOfFace, righteyeRect, 1.1, 1, Objdetect.CASCADE_FIND_BIGGEST_OBJECT, new Size(1,1),topRightOfFace.size() );
			// If it failed, search the left region using the 2nd eye detector.
			Rect[] rightEyeRectArray=righteyeRect.toArray();
			if (rightEyeRectArray.length==0){
				eyeDetector2.detectMultiScale(topRightOfFace, righteyeRect, 1.1, 1, Objdetect.CASCADE_FIND_BIGGEST_OBJECT, new Size(1,1),topRightOfFace.size() );
			}

			// Get the right eye center if one of the eye detectors worked.
			Point rightEyeCentre =new  Point(-1,-1);
			if(rightEyeRectArray.length >0) {
				rightEyeCentre.x = rightEyeRectArray[0].x + rightEyeRectArray[0].width/2 + rightX;
				rightEyeCentre.y = rightEyeRectArray[0].y + rightEyeRectArray[0].height/2 + topY;
			}
			if(leftEyeCentre.x>0 && rightEyeCentre.x>0){
				face.leftEyeCentre=leftEyeCentre;
				face.rightEyeCenter=rightEyeCentre;
				//new ResizingAndLoadingImg().showEyes(x, leftEyeCentre, rightEyeCentre, i++);
			}
			else{
				iterator2.remove();
			}
		}
	}
}
