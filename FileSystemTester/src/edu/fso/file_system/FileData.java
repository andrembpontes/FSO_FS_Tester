package edu.fso.file_system;

import java.util.List;

public class FileData {
	public final String name;
	public final int size;
	public final List<Integer> blocks;
	
	public FileData(String name, int size, List<Integer> blocks) {
		this.name = name;
		this.size = size;
		this.blocks = blocks;
	}
}
