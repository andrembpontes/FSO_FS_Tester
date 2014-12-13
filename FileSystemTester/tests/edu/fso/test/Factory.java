package edu.fso.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import edu.fso.file_system.FileSystem;
import edu.fso.file_system.FileSystemClass;

public class Factory {

	public static final File fsShell = new File("exec/fs-shell");
	
	static FileSystem fileSystem(File disk, int blocks, PrintStream redir){
		try {
			return new FileSystemClass(fsShell, disk, blocks, redir);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	static FileSystem fileSystem(File disk, int blocks){
		return Factory.fileSystem(disk, blocks, null);
	}
	
	static DiskQueue diskQueue(String diskQueueListPath) throws FileNotFoundException{
		return new DiskQueue(new File(diskQueueListPath));
	}
	
	static FileQueue fileQueue(String fileQueueListPath) throws FileNotFoundException{
		return new FileQueue(new File(fileQueueListPath));
	}
}
