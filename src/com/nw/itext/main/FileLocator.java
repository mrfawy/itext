package com.nw.itext.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

public class FileLocator {
	
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
	private  static List<String> loadFilePathsFromFile(String inputPath) throws IOException{
		List<String> result=new ArrayList<String>();
		for (String filePath : Files.readAllLines(Paths.get(inputPath),
				Charset.defaultCharset())) {
			if (filePath.isEmpty() || filePath.startsWith("#")) {
				continue;
			} else {
				try {
					filePath = filePath.replaceAll(" ", "%20");// fix spaces
					File f = new File(filePath);
					if(f.exists() ) {
						if(f.isDirectory()){
							result.addAll(FileLocator.findPdfFilesFromDir(filePath));
						}
						else{
							result.add(filePath);
						}
					}					
					
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("skipping to next file ");
				}

			}

		}
		return result;
		
	}
	public static List<String> generateFilePathList(String inputPath) throws IOException{
		List<String> filePathList=new ArrayList<String>();;
		File f = new File(inputPath);
		if(f.exists() ) {
			if(f.isDirectory()){
				filePathList.addAll(FileLocator.findPdfFilesFromDir(inputPath));
			}
			else{
				filePathList.addAll(FileLocator.loadFilePathsFromFile(inputPath));
			}
		}
		
		return filePathList;
	}
	
	
	public static void main(String[] args) {
		//String path="C:\\Users\\abdelm2\\Downloads";
		String path="\\\\urbwsr01\\manuals\\Commerciallines\\emanual/Farm/Farm-Manual/TN-NWAG";
		List<String>r=FileLocator.findPdfFilesFromDir(path);
		
		System.out.println(r);
	}

}
