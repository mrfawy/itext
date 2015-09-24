package com.nw.itext.pdfchanger.app;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

import com.nw.itext.pdfchanger.multi.FileProcessorFactory;
import com.nw.itext.pdfchanger.multi.FileProcessorTypeEnum;
import com.nw.itext.pdfchanger.multi.ProcessListRecursiveAction;

public class App {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out
					.println("Missing param operaion type , valid values are : Change , rollback ,scan");
			return;
		}
		System.out.println("Initializing ...");
		// load Configuration
		String configLocation = "Config.properties";
		if (!ConfigLoader.initInstance(configLocation)) {
			System.err
					.println("Error: Loading config file , please check Config.properties File exists.");
			return;
		}
		System.out.println("Generating File list ...");
		// if single file , treat as input file which has list of file paths per
		// line , else scan the directory for PDFS
		List<String> filePathList;
		try {
			filePathList = new FileLocator(ConfigLoader.getInstance()
					.getInputSrc()).generateFilePathList();
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
		System.out.println("Processing [" + filePathList.size()
				+ " ] File(s) ...");
		if (ConfigLoader.getInstance().getTestOnly()) {
			System.out
					.println("Tesing only Mode , no changes will be applied  to files ...");
		}

		String operation = args[0].toLowerCase();
		FileProcessorFactory factory = null;
		if ("change".equalsIgnoreCase(operation)) {
			System.out.println("Mode : " + "Change PDFs");
			factory = new FileProcessorFactory(FileProcessorTypeEnum.PDFCHANGER);

		} else if ("rollback".equalsIgnoreCase(operation)) {
			System.out.println("Mode : " + "Rollback PDFs");
			factory = new FileProcessorFactory(
					FileProcessorTypeEnum.PDFROLLBACK);
		} else if ("scan".equalsIgnoreCase(operation)) {
			System.out.println("Mode : " + "Scan PDFs");
			factory = new FileProcessorFactory(FileProcessorTypeEnum.PDFSCANNER);
		} else if ("locate".equalsIgnoreCase(operation)) {
			
			System.out.println("Already located files");
			return ;

		} else {
			System.err.println("Invalid operation , aborting ..");
			return;
		}

		// create Thread pool
		final ForkJoinPool pool = new ForkJoinPool(ConfigLoader.getInstance()
				.getThreadPoolSize());
		List<String> modifiedFilePathList = new CopyOnWriteArrayList<String>();
		ProcessListRecursiveAction action = new ProcessListRecursiveAction(
				filePathList, modifiedFilePathList, 0, filePathList.size(),
				factory);
		pool.invoke(action);
	}

}
