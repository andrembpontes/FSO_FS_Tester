package edu.fso.printer;

import java.io.PrintStream;

import edu.fso.file_system.SuperBlockData;

public class SuperBlockDataPrinter {
	static void print(SuperBlockData superBlockData, PrintStream out){
		out.println("valid magic number: " + superBlockData.validMagicNumber);
		out.println("blocks: " + superBlockData.diskBlocks);
		out.println("fat: " + superBlockData.fatBlocks);
	}
}
