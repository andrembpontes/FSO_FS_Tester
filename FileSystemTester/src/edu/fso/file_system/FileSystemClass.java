package edu.fso.file_system;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class FileSystemClass implements FileSystem {

	Linker linker;
	
	public FileSystemClass(File fsShell, File diskFile, int blocks, PrintStream output) throws IOException{
		this.linker = new Linker(fsShell);
		this.linker.start(diskFile, blocks, output);
	}
	
	@Override
	public boolean format() {
		this.linker.writeCommand(Command.FORMAT_FS);
		List<String> lines = this.linker.getOutput();
		
		for(String line : lines){
			if(DefaultMessages.FS_FORMAT_FAIL.equals(line))
				return false;
			if(DefaultMessages.FS_FORMAT_SUCCEED.equals(line))
				return true;
		}
		
		throw new InvalidOutputException();
	}

	@Override
	public boolean mount() {
		this.linker.writeCommand(Command.MOUNT_FS);
		List<String> lines = this.linker.getOutput();
		
		for(String line : lines){
			if(DefaultMessages.FS_MOUNT_FAIL.equals(line))
				return false;
			if(DefaultMessages.FS_MOUNT_SUCCEED.equals(line))
				return true;
		}
		
		throw new InvalidOutputException();
	}

	@Override
	public FileSystemData debug() {
		this.linker.writeCommand(Command.GET_FS_DATA);
		List<String> lines = this.linker.getOutput();
		
		if(lines.size() < 4) //minimum allowed
			return null;
		
		if((lines.size() - 4) % 3 != 0) //defined format
			throw new InvalidOutputException();
		
		boolean validMagicNumber = DefaultMessages.VALID_MAGIC_NUMBER.equals(lines.get(1));
		int diskBlocks = Integer.parseInt(lines.get(2).split(" ")[0].substring(1));
		int fatBlocks = Integer.parseInt(lines.get(3).split(" ")[0].substring(1));
		
		SuperBlockData superBlockData = new SuperBlockData(validMagicNumber, diskBlocks, fatBlocks);
		
		List<FileData> files = new LinkedList<FileData>();
		int actualI = 4;
		
		while(actualI < lines.size()){
			String name = lines.get(actualI++).split(" ")[1];
			name = name.substring(1, name.length() - 2);
			
			int size = Integer.parseInt(lines.get(actualI++).split(" ")[1].substring(1));
			
			List<Integer> blocks = new LinkedList<Integer>();
			String[] blockSplit = lines.get(actualI++).split(" ");
			
			for(int i = 1; i < blockSplit.length; i++){
				blocks.add(Integer.parseInt(blockSplit[i]));
			}
			
			files.add(new FileData(name, size, blocks));
		}
		
		return new FileSystemData(superBlockData, files);
	}

	@Override
	public boolean create(String fileName) {
		this.linker.writeCommand(Command.CREATE_FILE.format(fileName));
		List<String> lines = this.linker.getOutput();
		
		for(String line : lines){
			if(DefaultMessages.CREATE_FILE_FAIL.equals(line))
				return false;
			if(DefaultMessages.CREATE_FILE_SUCCEED.msg(fileName).equals(line))
				return true;
		}
		
		throw new InvalidOutputException();
	}

	@Override
	public boolean delete(String fileName) {
		this.linker.writeCommand(Command.DELETE_FILE.format(fileName));
		List<String> lines = this.linker.getOutput();
		
		for(String line : lines){
			if(DefaultMessages.DELETE_FILE_FAIL.equals(line))
				return false;
			if(DefaultMessages.DELETE_FILE_SUCCEED.msg(fileName).equals(line))
				return true;
		}
		
		throw new InvalidOutputException();
	}

	@Override
	public String[] cat(String fileName) {
		this.linker.writeCommand(Command.DUMP_FILE.format(fileName));
		List<String> lines = this.linker.getOutput();
		
		boolean bytesTransfered = !lines.get(lines.size() - 1).equals(DefaultMessages.NO_BYTES_TRANSFERED.msg());
		
		if(!bytesTransfered && lines.size() > 1)
			throw new InvalidOutputException();
		
		if(bytesTransfered && lines.size() < 2)
			throw new InvalidOutputException();
		
		String[] fileDump = new String[lines.size() - 1];
		
		int i = 0;
		for(String line : lines)
			if(i < fileDump.length)
				fileDump[i++] = line;
			
		
		return fileDump;
	}

	@Override
	public int getSize(String fileName) {
		this.linker.writeCommand(Command.GET_FILE_SIZE.format(fileName));
		List<String> lines = this.linker.getOutput();
		
		for(String line : lines){
			if(DefaultMessages.GET_FILE_SIZE_FAIL.equals(line))
				return -1;
		}
		
		if(lines.size() > 1)
			throw new InvalidOutputException();
		
		return Integer.parseInt(lines.get(0).split(" ")[1]);
	}

	@Override
	public boolean copyIn(String destination, File file) {
		String importedFileName = file.getAbsolutePath();
		
		this.linker.writeCommand(Command.IMPORT_FILE.format(importedFileName, destination));
		List<String> lines = this.linker.getOutput();
		
		int succeed = 0;
		for(String line : lines){
			if(DefaultMessages.FILE_IMPORT_SUCCEED.msg(importedFileName, destination).equals(line))
				succeed++;
			
			if(DefaultMessages.BYTES_TRANSFERED.msg(Long.toString(file.length())).equals(line))
				succeed++;
			
			if(DefaultMessages.NO_BYTES_TRANSFERED.equals(line))
				return false;
			
			if(succeed == 2)
				return true;
		}
		
		throw new InvalidOutputException();
	}

	@Override
	public boolean copyOut(String source, File file) {
		String exportedFileName = file.getAbsolutePath();
		
		this.linker.writeCommand(Command.IMPORT_FILE.format(source, exportedFileName));
		List<String> lines = this.linker.getOutput();
		
		int succeed = 0;
		for(String line : lines){
			if(DefaultMessages.FILE_EXPORT_SUCCEED.msg(source, exportedFileName).equals(line))
				succeed++;
			
			if(DefaultMessages.BYTES_TRANSFERED.msg(Long.toString(file.length())).equals(line))
				succeed++;
			
			if(DefaultMessages.NO_BYTES_TRANSFERED.equals(line))
				return false;
			
			if(succeed == 2)
				return true;
		}
		
		throw new InvalidOutputException();
	}
}