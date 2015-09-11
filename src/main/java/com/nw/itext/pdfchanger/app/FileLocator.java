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
	
	public FileLocator(String inputSrcFile) {
		super();
		this.inputSrcFile = inputSrcFile;
	}
	private static List<String> findPdfFilesFromDir(String dirPath){
		Collection<File> files = FileUtils.listFiles(
				FileUtils.getFile(dirPath), 
				  new RegexFileFilter("^(.*pdf)"), 
				  DirectoryFileFilter.DIRECTORY
				);
		List<String> result=new ArrayList<String>();
		for(File f:files){
			if(!f.getAbsolutePath().contains("NBPBKP")){
				result.add(f.getAbsolutePath());
			}
			
		}
		return result;
		
	}
	private  static List<String> loadFilePathsFromFile(List<String> inputFilesLines) throws IOException{
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
							result.addAll(FileLocator.findPdfFilesFromDir(filePath));
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
		return filePathList;
	}
	
	
	

}
