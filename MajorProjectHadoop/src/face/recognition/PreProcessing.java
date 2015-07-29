package face.recognition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import face.recognition.*;
public class PreProcessing{

	public void faceDetectionAndPreProcessing(Text key, BytesWritable value,Image image) {

		ResizingAndLoadingImg obj=new ResizingAndLoadingImg();

		//Getting the list Of images in gray scale or list of Mat of objects.
		////System.out.println("Loading images from memory in Gray scale...");
		obj.load(key,value,image);

		//List of resized images for faster processing to change the resize go to ResizingAndLoadingImg.java file.
		////System.out.println("Resizing images for widht : "+obj.resized_width+"x scaled_heght");
		obj.resize(image);
		////System.out.println("nfaces resize = "+image.size());

		//Equalizing histograms for each image. maintaing the contrast and brightness for each image.
		//System.out.println("Equalizing Histograms for all images...");
		obj.equalizeHist(image);

		//Detecting Faces in all the faces.
		//System.out.println("Detecting all Faces in list of images.");
		DetectFaceAndEye DetectFE=new DetectFaceAndEye();
		DetectFE.detectFace(image);

		if(image.faces.size()!=0){
			//enlarging rect detected in previous step
			//System.out.println("Enlarging faces detected in previous step.");
			obj.enlargMatOfRect(image);

			//Cropping the face image using rect detected.
			//System.out.println("Cropping the face image detected.");
			obj.cropFaceImg(image);

			//Detecting eyes
			//System.out.println("Detecting eyes in list of face iamges cropped in previos step.");
			DetectFE.detectEye(image);

			//Warp affine transformation
			obj.WarpAffineTransformOnList(image);

			//Equalizing left face and right face and creating image from all of them.
			obj.separateEualizationForLRFAce(image);

			//Smoothing of images  using Bilateral Filter.
			obj.smoothingBilateral(image);

			//Applying elliptical mask
			//obj.ellipticalMasking(image);
		}

	}

	/*	//	To create images on hardrive.
	public void CreateImagesAndTraingAndTestingFile(int type){
		int i=0;
		switch (type) {
		case 1:
			//System.out.println("Creating Warpedd images...");
			for (Iterator<Image> iterator = image.iterator(); iterator
					.hasNext();) {
				Image face = (Image) iterator.next();
				face.CreateWarpedImg(i++);
			}
			break;
		case 2:
			//System.out.println("Creating face images...");
			for (Iterator<Image> iterator = image.iterator(); iterator
					.hasNext();) {
				Image face = (Image) iterator.next();
				face.CreateFaceImg(i++);
			}
			break;
		default:
			//System.out.println("type not allowed");
			break;
		}
		DirectoryStructure ds=new DirectoryStructure();
		ds.createTrainingAndTestFile(ds.tempDir,ds.inputDir);
	}

	//to print details of each object of image.
	public void Print(){
		//System.out.println("Listing image and face properties");
		for (Iterator<Image> iterator = image.iterator(); iterator
				.hasNext();) {
			Image img = (Image) iterator.next();
			img.Print();
		}
	}*/
}
