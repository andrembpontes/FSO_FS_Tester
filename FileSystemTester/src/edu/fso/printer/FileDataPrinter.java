package edu.fso.printer;

import java.io.PrintStream;

import edu.fso.file_system.FileData;

public class FileDataPrinter {
	static void print(FileData fileData, PrintStream out){
		out.println("name: " + fileData.name);
		out.println("size: " + fileData.size);
		
		out.print("blocks:");
		for(Integer block : fileData.blocks)
			out.print(" " + block);
		
		out.println();
	}
}
