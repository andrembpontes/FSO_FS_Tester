package edu.fso.file_system;

public enum DefaultMessages {
	FS_MOUNT_FAIL("mount failed!"),
	FS_MOUNT_SUCCEED("disk mounted."),
	
	FS_FORMAT_FAIL("format failed!"),
	FS_FORMAT_SUCCEED("disk formatted."),
	
	CREATE_FILE_FAIL("create failed!"),
	CREATE_FILE_SUCCEED("created file %s", 1),
	
	DELETE_FILE_FAIL("delete failed!"),
	DELETE_FILE_SUCCEED("file %s deleted.", 1),
	
	GET_FILE_SIZE_FAIL("getsize failed!"),
	GET_FILE_SIZE_SUCCEED("file %s has size %s", 2),
	
	FILE_IMPORT_SUCCEED("copied file %s to  %s", 2),
	
	FILE_EXPORT_SUCCEED("copied myfs_file %s to file %s", 2),
	
	NO_BYTES_TRANSFERED("0 bytes copied"),
	BYTES_TRANSFERED("%s bytes copied", 1),
	RANDOM_BYTES_TRANSFERED(" bytes copied"),
	
	VALID_MAGIC_NUMBER("magic number is valid"),
	
	
	;
	
	public static final String ARG_ID = "%s";
	
	private String msg;
	int args;
	
	DefaultMessages(String msg){
		this(msg, 0);
	}
	
	DefaultMessages(String msg, int args){
		this.msg = msg;
		this.args = args;
	}
	
	public String msg(String... args){
		if(args.length != this.args)
			throw new InvalidParamsException();
		
		String msg = this.msg;
		if(this.args > 0)
			for(String arg : args)
				msg = msg.replaceFirst(ARG_ID, arg);
		
		return msg;
	}
	
	boolean equals(String msg){
		return this.msg.equals(msg);
	}
}
