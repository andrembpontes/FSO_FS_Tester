package edu.fso.file_system;

import java.util.List;

public class FileSystemData {
	public final SuperBlockData superBlock;
	public final List<FileData> files;
	
	public FileSystemData(SuperBlockData superBlock, List<FileData> files) {
		this.superBlock = superBlock;
		this.files = files;
	}
	
	public int freeBlocks(){
		int freeDiskBlocks = this.superBlock.diskBlocks;
		
		freeDiskBlocks -= 2; //super and dir block
		freeDiskBlocks -= this.superBlock.fatBlocks;
		
		for(FileData fileData : files){
			freeDiskBlocks -= fileData.blocks.size();
		}
		
		return freeDiskBlocks;
	}
	
}
