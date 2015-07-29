package face.recognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class DirectoryStructure {

	public String parentdirector;
	public String[]	subDirectory;
	public int[] no_of_images;
	String tempDir="temp";
	String inputDir="input";
	String outputDir="output";

	//only image paths used before preprocessing.
	List<String> ListOfImgPaths=new ArrayList<String>();

	//used in case removal of duplicates.
	public List<FileObj> listOfImgObj=new ArrayList<FileObj>();

	String name="";
	int count=1;
	int personCount=0;

	List<String> dataTrainSetList=new ArrayList<String>();
	List<String> dataTestSetList=new ArrayList<String>(); 
	public DirectoryStructure()
	{
	}
	public DirectoryStructure(String p,String[] subDir,int[] no)
	{
		parentdirector=p;
		subDirectory=subDir;
		no_of_images=no;
	}
	public void printVal()
	{
		System.out.println("parent Directory"+parentdirector);
		System.out.println("Sub directory:");
		for(int i=0;i<subDirectory.length;++i)
			System.out.println(subDirectory[i]+"\t\t"+no_of_images[i]);
	}
	public List<String> getAllImgPaths(String folderPath)
	{
		File folder=new File(folderPath);
		File [] listOfFiles=folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			String inputPath="";
			if(listOfFiles[i].isDirectory()){
				//recursive call to get the list of files present in that folder.
				getAllImgPaths(folderPath+"/"+listOfFiles[i].getName());
			}
			else{
				//adding files to list of paths. 
				inputPath=listOfFiles[i].getPath();
				inputPath=inputPath.replace("\\","/");
				ListOfImgPaths.add(inputPath);
			}
		}
		return  ListOfImgPaths;
	}

	public void getAllObjImgs(String folderPath)
	{
		File folder=new File(folderPath);
		File [] listOfFiles=folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].isDirectory()){
				//recursive call to get the list of files present in that folder.
				getAllObjImgs(folderPath+"/"+listOfFiles[i].getName());
			}
			else{
				if(listOfFiles[i].getName().endsWith(".JPG") || listOfFiles[i].getName().endsWith(".jpg")){
					//adding files to list of paths.
					listOfImgObj.add(new FileObj(listOfFiles[i]));
				}
			}
		}
		Collections.sort(listOfImgObj, new FileObj());
	}
	public void createFolders(String path){
		File f = new File(path);
		if (!f.isDirectory()) {
			boolean success = f.mkdirs();
			if (!success) {
				System.out.println("Could not create path: " + f.getPath());
			}
		}
	}
	public void getAllFaceData(String folderPath)
	{
		File folder=new File(folderPath);
		File [] listOfFiles=folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			String inputPath="";
			if(listOfFiles[i].isDirectory()){
				//recursive call to get the list of files present in that folder.
				name=listOfFiles[i].getName();
				count=1;    //used to set the values of testing set.
				++personCount;
				getAllFaceData(folderPath+"/"+listOfFiles[i].getName());
			}
			else{
				//adding files to list of paths. 
				inputPath=listOfFiles[i].getPath();
				inputPath=inputPath.replace("\\","/");
				if((i+1)%10==0){
					//creating a testing dataset.
					dataTestSetList.add( personCount+" "+name+" "+inputPath);
				}
				else{
					//creating a training dataset.
					dataTrainSetList.add( personCount+" "+name+" "+inputPath);
				}
				++count;
			}
		}
	}
	public void createTrainingAndTestFile(String folderOfFaces,String storeFileFolder) {
		//getting face data.
		
		dataTestSetList.clear();
		dataTrainSetList.clear();
		getAllFaceData(folderOfFaces);
		PrintWriter writer;

		//For training set
		try {
			writer = new PrintWriter(storeFileFolder+"/train.txt", "UTF-8");

			for (Iterator<String> iterator = dataTrainSetList.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				writer.println(string);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//For testing Set
		try {
			writer = new PrintWriter(storeFileFolder+"/test.txt", "UTF-8");

			for (Iterator<String> iterator = dataTestSetList.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				writer.println(string);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteFile(String imgPath){
		File file=new File(imgPath);
		try {
			Files.deleteIfExists(file.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Cannot be deleted :"+file.getName());
			e.printStackTrace();
		}
	}
};
