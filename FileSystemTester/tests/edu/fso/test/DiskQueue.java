package edu.fso.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class DiskQueue extends LinkedList<DiskTest>{
	
	public static final String DISK_FOLDER = "disks/";
	
	DiskQueue(File diskQueue) throws FileNotFoundException{
		Scanner scan = new Scanner(new FileInputStream(diskQueue));
		
		while(scan.hasNext()){
			File disk = new File(DISK_FOLDER + scan.next());
			int blocks = scan.nextInt();
			
			super.addLast(new DiskTest(disk, blocks));
		}
	}
	
	DiskTest nextDisk(){
		return super.pollFirst();
	}
}
