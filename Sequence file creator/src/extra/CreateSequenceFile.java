package extra;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import TarHadoop.TarToSeqFile;

public class CreateSequenceFile {

	/** Runs the converter at the command line. */
	public static void main(String[] args) {
		try {
		TarToSeqFile obj=new TarToSeqFile();
		
			obj.setOutput(new File("input3people.seq"));
			obj.execute("input/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
