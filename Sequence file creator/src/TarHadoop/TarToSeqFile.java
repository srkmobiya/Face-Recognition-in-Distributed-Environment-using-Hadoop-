package TarHadoop;

/* TarToSeqFile.java - Convert tar files into Hadoop SequenceFiles.
 *
 * Copyright (C) 2008 Stuart Sierra
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * http:www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */



/* From ant.jar, http://ant.apache.org/ */
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;
import org.opencv.core.*;
/* From hadoop-*-core.jar, http://hadoop.apache.org/
 * Developed with Hadoop 0.16.3. */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import com.sun.corba.se.spi.orbutil.fsm.Input;

import extra.DirectoryStructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;


/** Utility to convert tar files into Hadoop SequenceFiles.  The tar
 * files may be compressed with GZip or BZip2.  The output
 * SequenceFile will be stored with BLOCK compression.  Each key (a
 * Text) in the SequenceFile is the name of the file in the tar
 * archive, and its value (a BytesWritable) is the contents of the
 * file.
 *
 * <p>This class can be run at the command line; run without
 * arguments to get usage instructions.
 *
 * @author Stuart Sierra (mail@stuartsierra.com)
 * @see <a href="http://hadoop.apache.org/core/docs/r0.16.3/api/org/apache/hadoop/io/SequenceFile.html">SequenceFile</a>
 * @see <a href="http://hadoop.apache.org/core/docs/r0.16.3/api/org/apache/hadoop/io/Text.html">Text</a>
 * @see <a href="http://hadoop.apache.org/core/docs/r0.16.3/api/org/apache/hadoop/io/BytesWritable.html">BytesWritable</a>
 */
public class TarToSeqFile {

	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	private File outputFile;
	private LocalSetup setup;

	/** Sets up Configuration and LocalFileSystem instances for
	 * Hadoop.  Throws Exception if they fail.  Does not load any
	 * Hadoop XML configuration files, just sets the minimum
	 * configuration necessary to use the local file system.
	 */
	public TarToSeqFile() throws Exception {
		setup = new LocalSetup();
	}


	/** Sets the output SequenceFile. */
	public void setOutput(File outputFile) {
		this.outputFile = outputFile;
	}

	/** Performs the conversion. */
	public void execute(String inputFolder) throws Exception {
		SequenceFile.Writer output = null;
		try {
			output = openOutputFile();
			DirectoryStructure obj=new DirectoryStructure();
			List<String> listOfImgPath=obj.getAllImgPaths(inputFolder);	
			int i=0;
			//String prevName="";
			//String value="";
			Text key=new Text();
			for (Iterator<String> iterator = listOfImgPath.iterator(); iterator
					.hasNext();) {
				String imgPath = (String) iterator.next();

				
				Mat img1=Highgui.imread(imgPath);
				
				String[] tokens=imgPath.split("/");
				String currName=tokens[tokens.length-2];
				System.out.println(i+" of imgPath = "+currName+"   "+listOfImgPath.size());
				
				byte[] data=new byte[(int)img1.total()*(int)img1.elemSize()];
				img1.get(0,0,data);
				
				key=new Text(currName+","+img1.rows()+","+img1.cols());
				BytesWritable value=new BytesWritable(data);
				output.append(key, value);

				/*if(i==0)
					prevName=currName;
				if(currName.compareTo(prevName)==0){
					byte[] data=new byte[(int)img1.total()*(int)img1.elemSize()];
					img1.get(0,0,data);
					String x=new String(data,"UTF-8");
					value+=x+"###";
				}
				else{
					key=new Text(imgPath+","+img1.rows()+","+img1.cols());
					hadoopvalue=new Text(value);
					output.append(key, hadoopvalue);
					prevName=currName;
					value="";
				}*/
				++i;
			}
		} finally {
	           //if (input != null) { input.close(); }
	           if (output != null) { output.close(); }
	       }
	}


	private SequenceFile.Writer openOutputFile() throws Exception {
		Path outputPath = new Path(outputFile.getAbsolutePath());
		return SequenceFile.createWriter(setup.getLocalFileSystem(), setup.getConf(),
				outputPath,
				Text.class, BytesWritable.class,
				SequenceFile.CompressionType.BLOCK);
	}

	public static void exitWithHelp() {
		System.err.println("Usage: java org.altlaw.hadoop.TarToSeqFile <tarfile> <output>\n\n" +
				"<tarfile> may be GZIP or BZIP2 compressed, must have a\n" +
				"recognizable extension .tar, .tar.gz, .tgz, .tar.bz2, or .tbz2.");
		System.exit(1);
	}
}