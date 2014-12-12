package edu.fso.file_system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;


public class Linker {

	public static final int TIMES_WAIT = 3;
	public static final int WAINTING_TIME_MS = 100;
	
	File fsShell;
	Process process;
	BufferedReader fsOut;
	PrintStream fsIn;
	boolean streamsInit;
	
	PrintStream redirOutput;
	
	
	Linker(File fsShell){
		this.fsShell = fsShell;
		this.process = null;
		this.fsOut = null;
		this.fsIn = null;
		this.streamsInit = false;
	}
	
	boolean isRunning(){
		return this.process != null && this.process.isAlive() && this.streamsInit;
	}
	
	void terminate() throws IOException{
		if(this.process != null){
			this.fsOut.close();
			this.fsIn.close();
			
			this.fsOut = null;
			this.fsIn = null;
			this.streamsInit = false;
			
			this.process.destroy();
			this.process = null;
		}
	}
	
	void start(File diskFile, int blocks, PrintStream redirOutput) throws IOException{
		String[] cmdArr = {fsShell.getAbsolutePath(),
							diskFile.getAbsolutePath(),
							Integer.toString(blocks)};
		
		this.redirOutput = redirOutput;
		
		this.process = Runtime.getRuntime().exec(cmdArr);
		this.initStreams();
		
		this.getOutput();
	}
	
	void initStreams(){
		this.fsIn = new PrintStream(this.process.getOutputStream());
		this.fsOut = new BufferedReader( new InputStreamReader(this.process.getInputStream()));
		this.streamsInit = true;
	}
	
	void writeCommand(String command){
		if(!this.isRunning())
			throw new NotRunningException();
		
		this.fsIn.println(command);
		this.fsIn.flush();
	}
	
	void writeCommand(Command command){
		this.writeCommand(command.format());
	}
	
	List<String> getOutput(){
		List<String> lines = new LinkedList<String>();
		try{
			while(hasNextOutputLine()){
				//this.fsIn.println();
				String line = this.fsOut.readLine();
				
				lines.add(line);
				
				if(redirOutput != null)
					redirOutput.println(line);
			}
			
			return lines;
		}
		catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean hasNextOutputLine() throws IOException {
		boolean hasNextLine = this.fsOut.ready();
		
		for(int i = 0; i < TIMES_WAIT && !hasNextLine; i++, hasNextLine = this.fsOut.ready()){
			try {
				Thread.sleep(WAINTING_TIME_MS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return hasNextLine;
	}

}
