package edu.fso.printer;

import java.io.PrintStream;

import edu.fso.file_system.FileData;
import edu.fso.file_system.FileSystemData;

public class FileSystemDataPrinter {
	public static void print(FileSystemData data, PrintStream out){
		SuperBlockDataPrinter.print(data.superBlock, out);
		
		for(FileData fData : data.files){
			FileDataPrinter.print(fData, out);
		}
	}
}
