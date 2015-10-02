package com.nw.itext.pdfchanger.fileprocessors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.nw.itext.pdfchanger.app.ConfigLoader;

public class PDFRollBack implements FileProcessorIF {
	private String currentFilePath;

	public PDFRollBack(String filePath) {
		this.currentFilePath = filePath;
	}

	public boolean processFile() {
		
		String fileName=currentFilePath.substring(currentFilePath.lastIndexOf("\\")+1);
		String bkpFolderPath= currentFilePath.substring(0,currentFilePath.lastIndexOf("\\")+1)+ConfigLoader.getInstance().getBkpFolder();
		File bkpFolder=new File(bkpFolderPath);
		if(bkpFolder.exists()){
			//no backup folder  for this file 
			Path originalFilePath=Paths.get(currentFilePath);
			Path bkpFilePath=Paths.get(bkpFolderPath+"\\"+fileName+"_"+ConfigLoader.getInstance().getBkpSuffix()+".pdf");
			File bkpFile=new File(bkpFilePath.toString());
			if(bkpFile.exists()){
				System.out.println(originalFilePath);
				try {
					Files.copy(bkpFilePath, originalFilePath,
							StandardCopyOption.REPLACE_EXISTING);
					return true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		return false;
	}

}
