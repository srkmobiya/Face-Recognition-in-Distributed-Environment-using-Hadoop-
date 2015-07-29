package face.recognition;

import org.bytedeco.javacpp.opencv_core.IplImage;

public class QueryObj{
	public String path=new String();
	public String name=new String();
	public IplImage img=new IplImage();

	public QueryObj() {
		// TODO Auto-generated constructor stub
		path="";
		name="";
		img.setNull();
	}
}