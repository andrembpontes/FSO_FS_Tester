package edu.fso.file_system;

public enum Command {
	FORMAT_FS("format"),
	MOUNT_FS("mount"),
	GET_FS_DATA("debug"),
	CREATE_FILE("create","file_name"),
	DELETE_FILE("delete","file_name"),
	DUMP_FILE("cat","file_name"),
	GET_FILE_SIZE("getsize","file_name"),
	IMPORT_FILE("copyin","source_file", "destination_name"),
	EXPORT_FILE("copyout","source_name", "destination_file"),
	DUMP_BLOCK("dump","block_number"),
	HELP("help"),
	EXIT("exit");
	
	String cmd;
	String[] args;
	
	Command(String cmd, String... args){
		this.cmd = cmd;
		this.args = args;
	}
	
	String[] args(){
		return this.args;
	}
	
	String format(String... args){
		if(args.length != this.args.length)
			throw new InvalidParamsException();
		
		String command = cmd;
		
		for(String arg : args){
			command += " " + arg;
		}
		
		return command;
	}
	
	@Override
	public String toString(){
		String command = cmd;
		
		if(this.args != null && this.args.length > 0)
			for(String arg : args)
				command += " <" + arg + ">";
		
		return command;
	}

}
