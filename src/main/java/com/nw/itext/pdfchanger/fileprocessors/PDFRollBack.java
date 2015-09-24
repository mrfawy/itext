package com.nw.itext.pdfchanger.fileprocessors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class PDFRollBack implements FileProcessorIF {
	private String currentFilePath;
	private String suffix;

	public PDFRollBack(String filePath, String suffix) {
		this.currentFilePath = filePath;
		this.suffix = suffix;

	}

	public boolean processFile() {
		Path bkpFilePath = Paths.get(this.currentFilePath + this.suffix);
		Path originalFilePath = Paths.get(this.currentFilePath);
		System.out.println(originalFilePath);
		try {
			Files.copy(bkpFilePath, originalFilePath,
					StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
