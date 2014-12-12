package edu.fso.file_system;

public class SuperBlockData {
	public final boolean validMagicNumber;
	public final int diskBlocks, fatBlocks;
	
	public SuperBlockData(boolean validMagicNumber, int diskBlocks,
			int fatBlocks) {
		this.validMagicNumber = validMagicNumber;
		this.diskBlocks = diskBlocks;
		this.fatBlocks = fatBlocks;
	}
}
