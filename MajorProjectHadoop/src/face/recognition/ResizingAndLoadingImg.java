package face.recognition;

import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
public class ResizingAndLoadingImg{

	final public int resized_width=320;

	public void load(Text key, BytesWritable value,Image image){

		try {
			//image is retrived in byte data.
			byte bt[]=value.getBytes();

			//key is splitted to get the data;
			String matstring=key.toString();
			String tokens[]=matstring.split(",");
			System.out.println(tokens[0]);

			//Image object is created.
			image.name = tokens[0];		//name is set		
			int rows=Integer.parseInt(tokens[1]);
			int cols=Integer.parseInt(tokens[2]);
			Mat mt=new Mat(rows,cols,CvType.CV_8UC3);

			Mat mat1 = new Mat(rows,cols,CvType.CV_8UC1);
			mt.put(0, 0, bt);
			Imgproc.cvtColor(mt, mat1, Imgproc.COLOR_RGB2GRAY);
			image.orgImg=mat1;		//setting the image as mat object
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void resize(Image image){

		// Possibly shrink the image, to run much faster.
		float scale=(float)1;
		//Shrink the image while maintaining the same aspect ration.
		if(image.orgImg.cols()>resized_width){

			//finding scale
			scale=image.orgImg.cols()/(float)resized_width;
			image.scale=scale;

			int scaledHeight=Math.round(image.orgImg.rows()/scale);

			//resizing
			Mat smallSize=new Mat();
			Imgproc.resize(image.orgImg, smallSize, new Size(resized_width,scaledHeight));

			//replacing the ith large image with the small image.
			image.resizedImg=smallSize;
		}
	}
	public void equalizeHist(Image image){
		Mat equalizedImg=new Mat();

		//equalizing histogram.
		Imgproc.equalizeHist(image.resizedImg, equalizedImg);

		//replacing the ith image with equalizedImg
		image.resizedImg=equalizedImg;
	}

	public void enlargMatOfRect(Image image){
		// Enlarge the results if the image was temporarily shrunk.

		//Getting original Scale to which the image has been shrunk
		float scale=image.scale;

		for (Iterator<Face> iterator2 = image.faces.iterator(); iterator2
				.hasNext();) {
			Face face = (Face) iterator2.next();

			//Converting the only faceRect to original Image so that eyes and other features can be dtected easily.
			if (image.orgImg.cols() > resized_width) {
				face.faceRectOrg.x = Math.round(face.faceRectResized.x * scale);
				face.faceRectOrg.y = Math.round(face.faceRectResized.y * scale);
				face.faceRectOrg.width = Math.round(face.faceRectResized.width * scale);
				face.faceRectOrg.height = Math.round(face.faceRectResized.height * scale);
			}
		}
	}

	public void cropFaceImg(Image image){
		for (Iterator<Face> iterator2 = image.faces.iterator(); iterator2
				.hasNext();) {
			Face face = (Face) iterator2.next();
			//cropping the face image
			face.faceImg=new Mat(image.orgImg,face.faceRectOrg);
		}
	}
	public void WarpAffineTransformOnList(Image image){
		/*Warp affine transformation do the following jobs
		 * Rotate the face so that the two eyes are horizontal.
		 * Scale the face so that the distance between the two eyes is always the same.
		 * Translate the face so that the eyes are always centered horizontally and at a desired height.
		 * Crop the outer parts of the face, since we want to crop away the image background, hair, forehead, ears, and chin
		 */
		for (Iterator<Face> iterator2 = image.faces.iterator(); iterator2
				.hasNext();) {
			Face face = (Face) iterator2.next();
			face.WarpAffine();
		}
	}

	public void separateEualizationForLRFAce(Image image){
		//calling face functions for equalization of left side and right side of image.
		for (Iterator<Face> iterator2 = image.faces.iterator(); iterator2
				.hasNext();) {
			Face face = (Face) iterator2.next();
			face.separateEualizationForLR();
		}
	}

	public void smoothingBilateral(Image image){
		// smothing face image
		for (Iterator<Face> iterator2 = image.faces.iterator(); iterator2
				.hasNext();) {
			Face face = (Face) iterator2.next();
			face.warped.convertTo(face.warped, CvType.CV_8UC3);
			Mat filtered =new  Mat(face.warped.size(), CvType.CV_8U);
			Imgproc.bilateralFilter(face.warped, filtered, 0, 20.0, 2.0);
			face.warped=filtered;
		}
	}

	public void ellipticalMasking(Image image){
		// smothing face image
		for (Iterator<Face> iterator2 = image.faces.iterator(); iterator2
				.hasNext();) {
			Face face = (Face) iterator2.next();
			Mat mask =new Mat(face.warped.size(), CvType.CV_8UC1,new Scalar(255));
			double dw = face.warped.size().width;
			double dh = face.warped.size().height;
			Point faceCenter =new Point( Math.round(dw * 0.5),Math.round(dh * 0.4) );
			Size size = new Size( Math.round(dw * 0.5), Math.round(dh * 0.8));
			Core.ellipse(mask, faceCenter, size, 0, 0, 360, new Scalar(0), 5);
			// Apply the elliptical mask on the face, to remove corners.
			// Sets corners to gray, without touching the inner face.
			face.warped.setTo(new Scalar(128), mask);
		}
	}

	public void printMatOfRectOnImageAndCreateImage(MatOfRect matOfRect,Mat img,int i){
		for (Rect rect : matOfRect.toArray()) 
			Core.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 255, 0));
		Highgui.imwrite("output/"+i+".jpg", img);
	}
	public void printRectOnImageAndCreateImage(Rect rect,Mat img,int i){
		Core.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 255, 0));
		Highgui.imwrite("output/"+i+".jpg", img);
	}
	public void printCircleOnImageAndCreateImage(Mat img, Point centre,int i){
		Core.circle(img, centre, 5, new Scalar(255, 255, 0));
		Highgui.imwrite("output/"+i+".jpg", img);
	}
	public void showEyes(Mat img, Point left,Point right,int i){
		Core.circle(img, left, 5, new Scalar(255, 255, 0));
		Core.circle(img, right, 5, new Scalar(255, 255, 0));
		Highgui.imwrite("output/"+i+".jpg", img);
	}
}