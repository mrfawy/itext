package com.nw.itext.pdfchanger.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

public class FileLocator {
	
	private String inputSrcFile;
	private boolean locateBkps;
	private boolean verbose;
	
	public FileLocator(String inputSrcFile,boolean locateBkps,boolean verbose) {
		super();
		this.inputSrcFile = inputSrcFile;
		this.locateBkps=locateBkps;
		this.verbose=verbose;
	}
	private List<String> findPdfFilesFromDir(String dirPath){
		Collection<File> files = FileUtils.listFiles(
				FileUtils.getFile(dirPath), 
				  new RegexFileFilter("^(.*pdf)"), 
				  DirectoryFileFilter.DIRECTORY
				);
		List<String> result=new ArrayList<String>();
		for(File f:files){
			if(this.locateBkps){
				if(f.getAbsolutePath().contains("NBPBKP")){
					result.add(f.getAbsolutePath());
				}
			}
			else{
				if(!f.getAbsolutePath().contains("NBPBKP")){
					result.add(f.getAbsolutePath());
				}
			}
			
			
		}
		return result;
		
	}
	private  List<String> loadFilePathsFromFile(List<String> inputFilesLines) throws IOException{
		List<String> result=new ArrayList<String>();		
		for (String filePath :inputFilesLines) {
			if (filePath.isEmpty() || filePath.startsWith("#")) {
				continue;
			} else {
				File f;
				try {					
					f = new File(filePath);					
					if(f.exists()&&f.canRead() ) {
						if(f.isDirectory()){
							result.addAll(findPdfFilesFromDir(filePath));
						}
						else{
							result.add(filePath);
						}
					}else{
						System.err.println("Can't access path : "+filePath);
						throw new RuntimeException();
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
					System.err.println("skipping to next file ");
				}				

			}

		}
		return result;
		
	}
	public List<String> generateFilePathList() throws IOException{
		InputStream input = null;
		input = this.getClass().getClassLoader().getResourceAsStream(this.inputSrcFile);
		List<String> inputFilesLines=IOUtils.readLines(input, Charset.defaultCharset());
		
		List<String> filePathList=new ArrayList<String>();
		filePathList.addAll(loadFilePathsFromFile(inputFilesLines));
		if(verbose){
			for(String filePath:filePathList){
				System.out.println(filePath);
			}
			
		}
		return filePathList;
	}
	
	public static void main(String[] args) {
		System.out.println("Initializing File Locator ...");
		//load Configuration
		String configLocation = "Config.properties";
		ConfigLoader configLoader=new ConfigLoader();
		if(!configLoader.loadConfig(configLocation)){
			System.err.println("Error: Loading config file , please check Config.properties File exists.");
			return;
		}
		System.out.println("Generating File list ...");
		// if single file , treat as input file which has list of file paths per line , else scan the directory for PDFS
		List<String> filePathList;
		try {
			filePathList = new FileLocator(configLoader.getInputSrc(),false,configLoader.getVerbose()).generateFilePathList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error: Failed to generate List of file(s) to be processed ,Please recheck InputSrc file");
			return ;
		}	
		if(filePathList==null){
			System.err.println("Error: No Input files Found!, please check your input. ");
			return;
		}
		System.out.println("Done!");
	}
	
	
	

}
