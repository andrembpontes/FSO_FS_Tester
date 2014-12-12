package edu.fso.file_system;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;


public class TestLinker {
	public static PrintStream OUT = System.out;
	public static InputStream IN = System.in;
	public static PrintStream FS_OUT = OUT;
	
	public static void main(String[] args) throws IOException {
		
		File fsShell = new File(args[0]);
		File disk = new File(args[1]);
		int blocks = Integer.parseInt(args[2]);
		
		Linker lk = null;
		
		try {
			lk = new Linker(fsShell);
			lk.start(disk, blocks, OUT);
		} catch (IOException e) {
			OUT.println("failed to start fs-shell");
			throw e;
		}
		
		lk.writeCommand("help");
		lk.writeCommand("help");
		
		while(true)
			lk.getOutput();

	}

}
