package edu.fso.file_system;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DirData {
	byte[] dir;
	DirEntry[] dirArr;
	
	class DirEntry{
		boolean valid;
		String name;
		int size;
		int firstBlock;
	}
	
	public DirData(byte[] dir, int filesN){
		this.dir = dir;
		this.dirArr = new DirEntry[filesN];
		
		try {
			parseData();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseData() throws UnsupportedEncodingException{
		ByteBuffer buff = ByteBuffer.wrap(this.dir);
		
		for(int i = 0; i < dirArr.length; i++){
			dirArr[i].valid = !(buff.getChar() == '0');
			
			byte[] nameBuff = new byte[Konstants.MAX_NAME_LEN + 1];
			buff.get(nameBuff, 0, Konstants.MAX_NAME_LEN + 1);
			
			dirArr[i].name =  new String(nameBuff, "ASCII");
			dirArr[i].size = buff.getInt();
			dirArr[i].firstBlock = buff.getInt();
		}	
	}
	
	public DirEntry getEntry(int entry){
		return dirArr[entry];
	}
}
