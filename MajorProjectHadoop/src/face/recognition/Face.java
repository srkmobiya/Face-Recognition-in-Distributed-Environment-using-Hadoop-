package face.recognition;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Face {

	public Rect faceRectResized=new Rect();
	public Rect faceRectOrg=new Rect();
	public Mat faceImg=new Mat();
	public Point rightEyeCenter=new Point();
	public Point leftEyeCentre=new Point();
	public Mat warped=new Mat();

	void Print(){
		System.out.println("\tSize of Face rectangle detected in resized image:"+faceRectResized.size());
		System.out.println("\tSize of Face rectangel in original image:"+faceRectOrg.size());
		System.out.println("\tsize of Face image cropped:"+faceImg.size());
		System.out.println("\tLeft Center Of eye: "+leftEyeCentre);
		System.out.println("\tRight Center Of eye: "+rightEyeCenter);
	}
	void CreateFaceImg(int i){
		//Core.circle(faceImg, leftEyeCentre, 5, new Scalar(255, 255, 0));
		//Core.circle(faceImg, rightEyeCenter, 5, new Scalar(255, 255, 0));
		DirectoryStructure ds=new DirectoryStructure();
		ds.createFolders(ds.outputDir+"/");
		Highgui.imwrite(ds.outputDir+"/"+i+".jpg", faceImg);
	}
	void CreateWarpedImg(String name){
		String[] tokens=name.split("/");
		DirectoryStructure ds=new DirectoryStructure();
		String path=ds.tempDir+"/"+tokens[tokens.length-2]+"/";
		ds.createFolders(path);
		Highgui.imwrite(path+tokens[tokens.length-1], warped);
	}
	
	void WarpAffine(){
		/*
		 * After performing affine transformation lefteyecentre and righteyecenter
		 * varibles will become useless. so we will never going to use these varibles.
		*/
		Point eyesCenter=new Point();
		eyesCenter.x = ( rightEyeCenter.x+leftEyeCentre.x) * 0.5;
		eyesCenter.y = (rightEyeCenter.y+leftEyeCentre.y ) * 0.5;
		
		// Get the angle between the 2 eyes.
		double dy = (rightEyeCenter.y - leftEyeCentre.y);
		double dx = (rightEyeCenter.x - leftEyeCentre.x);
		double len = Math.sqrt(dx*dx + dy*dy);

		// Convert Radians to Degrees.
		double angle = Math.atan2(dy, dx) * 180.0/Math.PI;
		//Point center = new Point(cvImage.cols()/2, cvImage.rows()/2);
		
		// Hand measurements shown that the left eye center should ideally be roughly at (0.16, 0.14) of a scaled face image.
		double DESIRED_LEFT_EYE_X = 0.16;
		double DESIRED_RIGHT_EYE_X = 1.0-DESIRED_LEFT_EYE_X;
		double DESIRED_LEFT_EYE_Y=0.14;

		// Get the amount we need to scale the image to be the desired fixed size we want.
		int DESIRED_FACE_WIDTH = 100;
		int DESIRED_FACE_HEIGHT = 100;
		double desiredLen = DESIRED_RIGHT_EYE_X - DESIRED_LEFT_EYE_X;
		double scale = desiredLen * DESIRED_FACE_WIDTH / len;

		//in dummy image warped output will be stored
		Mat dummy=new Mat(new Size(DESIRED_FACE_WIDTH, DESIRED_FACE_HEIGHT), CvType.CV_8U);
		
		//this is the rotation matrix which will be used to perform transformation
		Mat rotImage = Imgproc.getRotationMatrix2D(eyesCenter, angle, scale);
		
		//shifting the center of eyes to the required position
		double ex=DESIRED_FACE_WIDTH*.5-(eyesCenter.x*scale);
		double ey=(DESIRED_LEFT_EYE_Y*DESIRED_FACE_HEIGHT)-(eyesCenter.y*scale);

		/*  changing the value of eyescentre
		 *  Note the value of eyescenter in rotation matrix is located at 
		 *  for x (0,1)  and 
		 *  for y (1,1)
		*/
		double []temp=rotImage.get(0, 1);
		for (int i = 0; i < temp.length; i++) {
			temp[i]=temp[i]+ex;
		}
		rotImage.put(0, 2, temp);
		double[] temp2=rotImage.get(1, 1);
		for (int i = 0; i < temp2.length; i++) {
			temp2[i]=temp2[i]+ey;
		}
		rotImage.put(1, 2, temp2);

		//performing affine transformation
		Imgproc.warpAffine(faceImg, dummy, rotImage,dummy.size());
		warped=dummy;
	} 
	void separateEualizationForLR(){
		int w = warped.cols();
		int h = warped.rows();
		Mat wholeFace=new Mat();
		Imgproc.equalizeHist(warped, wholeFace);
		int midX = w/2;
		Mat leftSide =new Mat(warped,new Rect(0,0, midX,h));
		Mat rightSide =new Mat(warped,new Rect(midX,0, w-midX,h));
		Imgproc.equalizeHist(leftSide, leftSide);
		Imgproc.equalizeHist(rightSide, rightSide);
		
		
		//creating an image by combining leftside and rightside and centre part
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
			double v[]=new double[1];
			if (x < w/4) {
			// Left 25%: just use the left face.
				v=leftSide.get(y, x);
			}
			else if (x < w*2/4) {
			// Mid-left 25%: blend the left face & whole face.
			double[] lv = leftSide.get(y,x);
			double [] wv = wholeFace.get(y,x);
			// Blend more of the whole face as it moves
			// further right along the face.
			float f = (x - w*1/4) / (float)(w/4);
			v[0] = Math.round((double)(1.0 - f) * lv[0] + (double)((f) * wv[0]));
			}
			else if (x < w*3/4) {
			// Mid-right 25%: blend right face & whole face.
				double[] rv = rightSide.get(y,x-midX);
				double[] wv = wholeFace.get(y,x);
			// Blend more of the right-side face as it moves
			// further right along the face.
			float f = (x - w*2/4) / (float)(w/4);
			v[0] = Math.round((double)(1.0f - f) * wv[0] + (f) * rv[0]);
			}
			else {
			// Right 25%: just use the right face.
			v = rightSide.get(y,x-midX);
			}
			warped.put(y,x,v);
			}// end x loop
			}//end y loop
	}
}
