package edu.fso.test;
import static org.junit.Assert.*;

import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;

import edu.fso.file_system.FileSystem;
import edu.fso.file_system.FileSystemData;
import edu.fso.file_system.Linker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class fs_format {

	public static final String DISK_QUEUE = "fs_format/disk_queue"; 
	public static final String FILE_QUEUE = "fs_format/file_queue";
	public static final String FILE_OUTS = "filesOut/"; 
	
	static DiskQueue readedDiskQueue;
	static FileQueue readedFileQueue;
	
	FileSystem fs;
	
	DiskQueue diskQueue;
	DiskTest actualDisk;
	
	FileQueue fileQueue;
	File actualFile;
	
	
	@BeforeClass
	static public void readTestParams() throws FileNotFoundException{
		File[] oldDiskFiles = new File(DiskQueue.DISK_FOLDER).listFiles();
		for(File oldDisk : oldDiskFiles){
			if(!oldDisk.getName().equals(".gitignore"))
				oldDisk.delete();
		}
		
		readedDiskQueue = Factory.diskQueue(DISK_QUEUE);
		readedFileQueue = Factory.fileQueue(FILE_QUEUE);
	}
	
	@Before
	public void init() throws IOException{
		diskQueue = (DiskQueue) readedDiskQueue.clone();
		fileQueue = (FileQueue) readedFileQueue.clone();
		actualDisk = null;
	}
	
	@After
	public void end(){
		fs.exit();
	}
	
	public void nextDisk(){
		actualDisk = diskQueue.nextDisk();
		this.fs = Factory.fileSystem(actualDisk.disk, actualDisk.blocks, System.err);
	}
	
	public void nextFile(){
		actualFile = fileQueue.nextFile();
	}
	
	//@Ignore
	@Test
	public void a_cant_mount_unformated_device(){
		while(!diskQueue.isEmpty()){
			this.nextDisk();
			
			assertFalse("Mounted an unformated disk at " + actualDisk.disk.getName(), fs.mount());
		}
	}
	
	//@Ignore
	@Test
	public void b_debug_unformated_disks(){
		while(!diskQueue.isEmpty()){
			this.nextDisk();
			
			FileSystemData fsData = fs.debug();

			assertNull("Cant debug unformated disk at " + actualDisk.disk.getName(), fsData);
			//assertFalse("Valid magic number at an unformated disk at " + actualDisk.disk.getName(), fsData.superBlock.validMagicNumber);
		}
	}
	
	//@Ignore
	@Test
	public void c_cant_operate_unmounted_disk(){
		while(!diskQueue.isEmpty()){
			this.nextDisk();

			assertFalse("File created at unmounted disk at " + actualDisk.disk.getName(), fs.create("x"));
			assertFalse("File deleted at unmounted disk at " + actualDisk.disk.getName(), fs.delete("x"));
			assertEquals("File size geted at unmounted disk at " + actualDisk.disk.getName(), -1, fs.getSize("x"));
		}
	}
	
	/**
	 * All disks must be formated at first time
	 */
	//@Ignore
	@Test
	public void d_can_format_ones() {
		int linkerDelay = Linker.DELAY_MS;
		
		while(!diskQueue.isEmpty()){
			this.nextDisk();
			if(actualDisk.blocks > 102400)
				Linker.DELAY_MS *= 3;
			
			assertTrue("Cant format disk " + actualDisk.disk.getName(), fs.format());
			
			Linker.DELAY_MS = linkerDelay;
		}
	}
	
	/**
	 * All disks must fail formated twice
	 */
	//@Ignore
	@Test
	public void e_cant_format_twice(){
		while(!diskQueue.isEmpty()){
			this.nextDisk();
			
			assertFalse("Formated disk " + actualDisk.disk.getName() + " twice", fs.format());
		}
	}
	
	//@Ignore
	@Test
	public void f_assure_right_meta_data(){
		while(!diskQueue.isEmpty()){
			this.nextDisk();
			
			FileSystemData fsData = fs.debug();
			
			assertTrue("Non valid magic number at " + actualDisk.disk.getName(), fsData.superBlock.validMagicNumber);
			assertEquals("Non empty file list at " + actualDisk.disk.getName(), 0, fsData.files.size());
			assertEquals("Incorrect block number at " + actualDisk.disk.getName(), actualDisk.blocks, fsData.superBlock.diskBlocks);
			assertEquals("Incorrect fat blocks number at "  + actualDisk.disk.getName(), (int) Math.ceil((double)actualDisk.blocks / Konstants.ADD_PER_BLOCK), fsData.superBlock.fatBlocks);
		}
	}
	
	@Test
	public void g_copy_files() throws IOException{
		while(!diskQueue.isEmpty()){
			this.nextDisk();
			assertTrue("Cant mount " + actualDisk.disk.getName(), fs.mount());
			
			int freeBlocks =  fs.debug().freeBlocks();
			
			while(!fileQueue.isEmpty()){
				nextFile();
				
				File outfile = new File(FILE_OUTS + actualFile.getName());
				outfile.createNewFile();
				
				int blocksNeeded = (int) Math.ceil(actualFile.length() / (double) Konstants.BLOCK_SIZE);
				
				boolean copyInResult = fs.copyIn(actualFile.getName(), actualFile);
				
				if(blocksNeeded <= freeBlocks){
				
					assertTrue("Cant copy " + actualFile.getName() + " to " + actualDisk.disk.getName(), copyInResult);
					assertTrue("Cant copy " + actualFile.getName() + " from " + actualDisk.disk.getName(), fs.copyOut(actualFile.getName(), outfile));
					
					InputStream expected = new FileInputStream(actualFile);
					InputStream actual = new FileInputStream(outfile);
					
					assertEquals("Not same size", expected.available(), actual.available());
					while(expected.available() > 0 || actual.available() > 0){
						assertEquals("Not same data",expected.read(), actual.read());
						assertEquals("Not same size", expected.available(), actual.available());
					}
					
					expected.close();
					actual.close();
					
					freeBlocks -= blocksNeeded;
				}
				else {
					assertFalse("Copied "+ actualFile.getName() +" larger than "+ actualDisk.disk.getName() +" free space", copyInResult);
					freeBlocks = 0;
				}
				
				
				assertEquals("Not correct free blocks after copy", freeBlocks, fs.debug().freeBlocks());
				
			}
			
		}
	}

}
