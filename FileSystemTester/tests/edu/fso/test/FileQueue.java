package edu.fso.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class FileQueue extends LinkedList<File>{
	
	public static final String FILES_FOLDER = "filesIn/";
	
	FileQueue(File diskQueue) throws FileNotFoundException{
		Scanner scan = new Scanner(new FileInputStream(diskQueue));
		
		while(scan.hasNext()){
			super.addLast(new File(FILES_FOLDER + scan.next()));
		}
	}
	
	File nextFile(){
		return super.pollFirst();
	}
}
