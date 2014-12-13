package edu.fso.test;

import java.io.File;

public class DiskTest {
	final File disk;
	final int blocks;
	
	DiskTest(File disk, int blocks){
		this.disk = disk;
		this.blocks = blocks;
	}
}
