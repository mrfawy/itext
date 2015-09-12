package com.nw.itext.pdfchanger.app;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class PDFRollBack {
	private String currentFilePath;
	private String suffix;

	public PDFRollBack(String filePath, String suffix) throws IOException,
			URISyntaxException {
		this.currentFilePath = filePath;
		this.suffix = suffix;

	}

	public void processFile() {
		Path bkpFilePath = Paths.get(this.currentFilePath + this.suffix);
		Path originalFilePath = Paths.get(this.currentFilePath);
		System.out.println(originalFilePath);
		try {
			Files.copy(bkpFilePath, originalFilePath,
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("Initializing Rolling Back...");
		// load Configuration
		String configLocation = "Config.properties";
		ConfigLoader configLoader = new ConfigLoader();
		if (!configLoader.loadConfig(configLocation)) {
			System.err
					.println("Error: Loading config file , please check Config.properties File exists.");
			return;
		}
		System.out.println("Generating File list ...");
		// if single file , treat as input file which has list of file paths per
		// line , else scan the directory for PDFS
		List<String> filePathList;
		try {
			filePathList = new FileLocator(configLoader.getInputSrc(), true,
					configLoader.getVerbose()).generateFilePathList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err
					.println("Error: Failed to generate List of file(s) to be processed ,Please recheck InputSrc file");
			return;
		}
		if (filePathList == null) {
			System.err
					.println("Error: No Input files Found!, please check your input. ");
			return;
		}
		System.out.println("RollingBack [" + filePathList.size()
				+ " ] File(s) ...");
		for (String filePath : filePathList) {
			try {
				new PDFRollBack(filePath, "_NBPBKP.pdf").processFile();
			} catch (Exception ex) {
				ex.printStackTrace();
				System.err.println("skipping to next file ");
			}
		}
		if (configLoader.getVerify()) {
			System.out.println("Verifing Files ...");
			for (String filePath : filePathList) {
				if (!PDFVerifier.isVerified(filePath)) {
					System.err.println(new LogRecord(filePath, -1,
							"Failed to Verify currentFile ", "Error"));
				}
			}
		}
		System.out.println("Done!");
	}

}
