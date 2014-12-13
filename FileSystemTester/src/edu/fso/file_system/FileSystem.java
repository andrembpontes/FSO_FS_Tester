package edu.fso.file_system;

import java.io.File;

public interface FileSystem {
	boolean format();
	boolean mount();
	FileSystemData debug();
	boolean create(String fileName);
	boolean delete(String fileName);
	String[] cat(String fileName);
	int getSize(String fileName);
	boolean copyIn(String destination, File file);
	boolean copyOut(String source, File file);
	boolean exit();
	void kill();
}
