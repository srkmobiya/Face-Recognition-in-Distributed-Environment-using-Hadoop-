package face.recognition;

import java.io.File;
import java.util.Comparator;

public class FileObj implements Comparator<FileObj>, Comparable<FileObj>
{
	public File file; 
	public FileObj()
	{
		
	}
	public FileObj(File f)
	{
		this.file=f;
	}
	@Override
	public int compareTo(FileObj obj) {
		// TODO Auto-generated method stub
		return (this.file.getName()).compareTo(obj.file.getName());
	}
	@Override
	public int compare(FileObj arg0, FileObj arg1) {
		// TODO Auto-generated method stub
		if(arg0.file.lastModified()==arg1.file.lastModified())
			return (arg0.file.getName()).compareTo(arg1.file.getName()); 
		else
			return arg0.file.lastModified()>arg1.file.lastModified()?1:-1;
	}
}