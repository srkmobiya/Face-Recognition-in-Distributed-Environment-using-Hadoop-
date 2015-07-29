package face.recognition;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

public class Image {
	public Mat orgImg=new Mat(); //original Image

	public Mat resizedImg=new Mat(); //resized image
	public float scale=(float)1;	//scaling factor 
	
	public MatOfRect matOfRectFaceResized=new MatOfRect(); //faces detected in each image
	public List<Face> faces=new ArrayList<Face>();  	//face list for each image
	
	public String name=new String();		//path of each image

	public void Print(){
		System.out.println();
		System.out.println("Size Of Original Image:"+orgImg.size());
		System.out.println("Size Of resized Image:"+resizedImg.size());
		System.out.println("Resizdd image is : "+scale+" times of original image");
		System.out.println("Path Of Original image: "+name);
		for (Iterator<Face> iterator = faces.iterator(); iterator.hasNext();) {
			Face face2 = (Face) iterator.next();
			System.out.println();
			face2.Print();
		}
	}
	public void CreateFaceImg(int i){
		for (Iterator<Face> iterator = faces.iterator(); iterator.hasNext();) {
			Face face2 = (Face) iterator.next();
			face2.CreateFaceImg(i);
			++i;
		}
	}
	public void CreateWarpedImg(int i){
		for (Iterator<Face> iterator = faces.iterator(); iterator.hasNext();) {
			Face face2 = (Face) iterator.next();
			//face2.CreateWarpedImg(name);
			++i;
		}
	}
	
}
