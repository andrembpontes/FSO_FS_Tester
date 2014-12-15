package edu.fso.test;
import static org.junit.Assert.*;

import org.junit.runners.MethodSorters;

import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;

import edu.fso.file_system.FileData;
import edu.fso.file_system.FileSystem;
import edu.fso.file_system.FileSystemData;
import edu.fso.file_system.Konstants;
import edu.fso.file_system.Linker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class fs_format {

	public static final String DISK_QUEUE = "fs_format/disk_queue"; 
	public static final String FILE_QUEUE = "fs_format/file_queue";
	public static final String FILE_OUTS = "filesOut/"; 
	
	public static final int STRESS_TESTS_MAX_MS = 360000;
	
	public static final int READ_ATTEMPTS = 3;
	public static final int DELAY_BETWEEN_MS = 10;
	
	public static final PrintStream OUT_REDIRECT = null;
	
	public static final PrintStream OUT_STATS = System.out;
	public static final PrintStream OUT_DEBUG = null;
	
	static DiskQueue readedDiskQueue;
	static FileQueue readedFileQueue;
	
	FileSystem fs;
	
	DiskQueue diskQueue;
	DiskTest actualDisk;
	
	FileQueue fileQueue;
	File actualFile;
	
	boolean saveDiskMod;
	
	void statsPrintln(String line){
		if(OUT_STATS != null)
			OUT_STATS.println(line);
	}
	
	void debugPrintln(String line){
		if(OUT_DEBUG != null)
			OUT_DEBUG.println(line);
	}
	
	
	
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
		
		Linker.READ_ATTEMPTS = READ_ATTEMPTS;
		Linker.DELAY_MS = DELAY_BETWEEN_MS;
		
		saveDiskMod = true;
	}
	
	@After
	public void end(){
		
	}
	
	public void nextDisk(){
		if(fs != null){
			if(saveDiskMod)
				assert(fs.exit());
			else {
				fs.kill();
			}
		}
		
		actualDisk = diskQueue.nextDisk();
		this.fs = Factory.fileSystem(actualDisk.disk, actualDisk.blocks, OUT_REDIRECT);
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
		
		statsPrintln("Cant mount unformated disks. Check!");
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
		
		statsPrintln("Can debug unformated disks. Check!");
	}
	
	//@Ignore
	@Test
	public void c_cant_operate_unformated_disk(){
		while(!diskQueue.isEmpty()){
			this.nextDisk();
			
			assertFalse("FileSystem mounted at unformated disk at " + actualDisk.disk.getName(), fs.mount());
			assertFalse("File created at unformated disk at " + actualDisk.disk.getName(), fs.create("x"));
			assertFalse("File deleted at unformated disk at " + actualDisk.disk.getName(), fs.delete("x"));
			assertEquals("File size geted at unformated disk at " + actualDisk.disk.getName(), -1, fs.getSize("x"));
		}
		
		statsPrintln("Cant operate unformated disks. Check!");
	}
	
	/**
	 * All disks must be formated at first time
	 */
	//@Ignore
	@Test
	public void d_can_format_once() {
		
		while(!diskQueue.isEmpty()){
			this.nextDisk();
			if(actualDisk.blocks > 102400)
				Linker.READ_ATTEMPTS = 100;
			else
				Linker.READ_ATTEMPTS = READ_ATTEMPTS;
			
			assertTrue("Cant format disk " + actualDisk.disk.getName(), fs.format());
		}
		
		statsPrintln("Can format all disks once. Check!");
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
		
		statsPrintln("Cant format already formated disks. Check!");
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
		
		statsPrintln("Cheking disks metadata... Check!");
	}
	
	//@Ignore
	@Test
	public void g_cant_operate_unmounted_disk(){
		while(!diskQueue.isEmpty()){
			this.nextDisk();

			assertFalse("File created at unmounted disk at " + actualDisk.disk.getName(), fs.create("x"));
			assertFalse("File deleted at unmounted disk at " + actualDisk.disk.getName(), fs.delete("x"));
			assertEquals("File size geted at unmounted disk at " + actualDisk.disk.getName(), -1, fs.getSize("x"));
		}
		
		statsPrintln("Cant operate unmounted disks. Check!");
	}
	
	@Test 
	public void g_dir_stress_test(){
		saveDiskMod = false;
		
		Linker.READ_ATTEMPTS = 2;
		Linker.DELAY_MS = 1;
		
		Random rand = new Random();
		
		int maxTestDurationPerDisk = STRESS_TESTS_MAX_MS / diskQueue.size();
		
		statsPrintln("----- DIRECTORY STRESS TEST START -----");
		statsPrintln("Starting directory stress test (max duration: " + (maxTestDurationPerDisk * diskQueue.size() / 1000) + "s)");
		
		while(!diskQueue.isEmpty()){
			this.nextDisk();
			
			long filesAdded = 0;
			long filesDeleted = 0;
			
			FileSystemData fsData = fs.debug();
			
			if(fsData != null){	
				fs.mount();
				
				debugPrintln("Starting directory stress test at " + actualDisk.disk.getName());
				
				int currentFileN = fsData.files.size();
				long startTime = System.currentTimeMillis();
				while(currentFileN < Konstants.MAX_FILES){
					
					debugPrintln("Current file n: " + currentFileN);
					
					if(currentFileN == 0 || rand.nextInt(maxTestDurationPerDisk) < System.currentTimeMillis() - startTime){
						String newFile = Integer.toString(rand.nextInt((int) Math.pow(10, Konstants.MAX_NAME_LEN)));
						assert(newFile.length() <= Konstants.MAX_NAME_LEN);
						
						assertTrue("Cant create file", fs.create(newFile));
						
						boolean fileAddConfirmed = false;
						
						for(FileData fileData : fs.debug().files){
							if(fileData.name.equals(newFile)){
								fileAddConfirmed = true;
								break;
							}
						}
						
						assertTrue("File not added", fileAddConfirmed);
						currentFileN++;
						filesAdded++;
						debugPrintln("file " + newFile + " added");
					}
					else {
						List<FileData> fileDataList = fs.debug().files;
						int fileToDelete = rand.nextInt(fileDataList.size());
						
						assertTrue("Cant delete file", fs.delete(fileDataList.get(fileToDelete).name));
						currentFileN--;
						filesDeleted++;
						debugPrintln("file " + fileDataList.get(fileToDelete).name + "removed");
					}
				}
				
				assert(currentFileN == Konstants.MAX_FILES);
				
				String newFile = Integer.toString(rand.nextInt(10 ^ Konstants.MAX_NAME_LEN));
				assert(newFile.length() <= Konstants.MAX_NAME_LEN);
				
				assertFalse("File created with full dir", fs.create(newFile));
				
				debugPrintln("Completed directory stress test at " + actualDisk.disk.getName());
				
				statsPrintln("Directory stress tested completed at " + actualDisk.disk.getName());
				statsPrintln("Files created: " + filesAdded);
				statsPrintln("Files deleted: " + filesDeleted);
				statsPrintln("Duration: " + (System.currentTimeMillis() - startTime) + "ms");
				statsPrintln("");
				
				
			}
		}
		
		statsPrintln("----- DIRECTORY STRESS TEST END -----");
		statsPrintln("");
	}
	
	public int copyInCopyOutFile(File file, String nameInFS) throws IOException{
		int freeBlocks = fs.debug().freeBlocks();
		
		File outfile = new File(FILE_OUTS + actualFile.getName());
		outfile.createNewFile();
		
		int blocksNeeded = (int) Math.ceil(file.length() / (double) Konstants.BLOCK_SIZE);
		
		boolean copyInResult = fs.copyIn(nameInFS, file);
		
		if(blocksNeeded <= freeBlocks){
		
			assertTrue("Cant copy " + file.getName() + " to " + actualDisk.disk.getName(), copyInResult);
			assertTrue("Cant copy " + nameInFS + " from " + actualDisk.disk.getName(), fs.copyOut(nameInFS, outfile));
			
			InputStream expected = new FileInputStream(file);
			InputStream actual = new FileInputStream(outfile);
			
			assertEquals("Not same size", expected.available(), actual.available());
			while(expected.available() > 0 || actual.available() > 0){
				assertEquals("Not same data",expected.read(), actual.read());
				assertEquals("Not same size", expected.available(), actual.available());
			}
			
			expected.close();
			actual.close();
			
			assertEquals("Not correct free blocks after copy", freeBlocks - blocksNeeded, fs.debug().freeBlocks());
			
		}
		else {
			assertFalse("Copied "+ actualFile.getName() +" larger than "+ actualDisk.disk.getName() +" free space", copyInResult);
			assertEquals("Not correct free blocks after copy", 0, fs.debug().freeBlocks());
		}
		
		return Math.min(blocksNeeded, freeBlocks);
	
	}
	
	@Test
	public void h_copy_files() throws IOException{
		statsPrintln("Starting copy in, copy out and data comparasion test");
		while(!diskQueue.isEmpty()){
			this.nextDisk();
			assertTrue("Cant mount " + actualDisk.disk.getName(), fs.mount());
			
			int freeBlocks =  fs.debug().freeBlocks();
			int blocksCopied = 0;
			
			while(!fileQueue.isEmpty()){
				nextFile();
				
				blocksCopied += copyInCopyOutFile(actualFile, actualFile.getName());
			}
			
			assertEquals("Not correct free blocks after copy", freeBlocks - blocksCopied, fs.debug().freeBlocks());
		}
		statsPrintln("done!");
		statsPrintln("");
	}
	
	@Test
	public void i_delete_all_files(){
		saveDiskMod = false;
		while(!diskQueue.isEmpty()){
			this.nextDisk();

			for(FileData fileData : fs.debug().files){
				assertTrue("Cant delete " + fileData.name + " at " + actualDisk.disk.getName(), fs.delete(fileData.name));
			}
			
			assertEquals("Non all files deleted at " + actualDisk.disk.getName(), 0, fs.debug().files.size());
		}
	}
	
	@Test
	public void h_rand_copyInCopyOutDelete(){
		//TODO implement some day in the future
	}

}
