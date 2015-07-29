package face.recognition;

import java.awt.image.BufferedImage;

import org.apache.hadoop.io.BytesWritable;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class TrainData {
	public static int currNumber=0;
	public int number;
	public IplImage faceImg=new IplImage();
	public String name=new String("");
	public TrainData(String key,BytesWritable image) {
		// TODO Auto-generated constructor stub

		String [] tokens=key.split(",");
		
		this.name=tokens[0];
		
		int size=Integer.parseInt(tokens[1]);
		
		this.faceImg=IplImage.createFrom(Mat2Buffer(image,size));
		number=currNumber;
		++currNumber;
	}
	public BufferedImage Mat2Buffer(BytesWritable image,int size ) {
		BufferedImage out;
		byte[] data=image.getBytes();
		int type=BufferedImage.TYPE_BYTE_GRAY;
		out=new BufferedImage((int)Math.sqrt(size), (int)Math.sqrt(size), type);
		out.getRaster().setDataElements(0, 0, (int)Math.sqrt(size), (int)Math.sqrt(size), data);
		return out;
	}
}
