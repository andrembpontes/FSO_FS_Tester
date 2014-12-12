import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import edu.fso.file_system.FileSystem;
import edu.fso.file_system.FileSystemClass;
import edu.fso.file_system.FileSystemData;
import edu.fso.printer.FileSystemDataPrinter;


public class Main {

	public static PrintStream OUT = System.out;
	public static InputStream IN = System.in;
	public static PrintStream FS_OUT = System.err;
	
	public static void main(String[] args) throws IOException {
		
		File fsShell = new File(args[0]);
		File disk = new File(args[1]);
		int blocks = Integer.parseInt(args[2]);
		
		FileSystem fs = null;
		
		try {
			fs = new FileSystemClass(fsShell, disk, blocks, FS_OUT);
		} catch (IOException e) {
			OUT.println("failed to start fs-shell");
			throw e;
		}
		
		FileSystemDataPrinter.print(fs.debug(), OUT);
		
		OUT.println(fs.mount());
		OUT.println(fs.mount());
		
		
		
		OUT.println("---(end)---");
	}

}
