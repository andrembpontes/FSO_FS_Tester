package edu.fso.file_system;

public class FATData {
	public static int FREE = 0, EOFF = 1, BUSY = 2;
	
	byte[][] fatBlocks;
	
	public FATData(byte[][] FAT){
		this.fatBlocks = FAT;
	}
	
	byte[] getFATBlock(int block){
		return fatBlocks[block];
	}
	
	static int getFATEntryBlock(int entry){
		return (int) Math.ceil((double) entry / Konstants.ADD_PER_BLOCK);
	}
	
	public int getFATEntry(int entry){
		return fatBlocks[getFATEntryBlock(entry)][entry];
	}
}
