package edu.fso.file_system;

import java.util.List;

public class FileSystemData {
	public final SuperBlockData superBlock;
	public final List<FileData> files;
	
	public FileSystemData(SuperBlockData superBlock, List<FileData> files) {
		this.superBlock = superBlock;
		this.files = files;
	}
	
}
