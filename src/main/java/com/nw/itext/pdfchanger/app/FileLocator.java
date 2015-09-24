package com.nw.itext.pdfchanger.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.apache.commons.io.IOUtils;

import com.nw.itext.pdfchanger.multi.FileLocatorTask;

public class FileLocator {

	private String inputSrcFile;
	private boolean verbose;

	public FileLocator(String inputSrcFile) {
		super();
		this.inputSrcFile = inputSrcFile;
		this.verbose = ConfigLoader.getInstance().getVerbose();
	}

	private List<String> loadFilePathsFromFile(List<String> inputFilesLines)
			throws IOException {
		List<String> result = new CopyOnWriteArrayList<String>();
		ForkJoinPool forkJoinPool = new ForkJoinPool(ConfigLoader.getInstance().getThreadPoolSize());
		
		for (String filePath : inputFilesLines) {
			if (filePath.isEmpty() || filePath.startsWith("#")) {
				continue;
			} else {
			
				File f;
				try {
					f = new File(filePath);
					if (f.exists() && f.canRead()) {
						if (f.isDirectory()) {
							FileLocatorTask task = new FileLocatorTask(filePath);
							result.addAll(forkJoinPool.invoke(task));
							
						} else {
							result.add(filePath);
						}
					} else {
						System.err.println("Can't access path : " + filePath);
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

	public List<String> generateFilePathList() throws IOException {
		InputStream input = null;
		input = this.getClass().getClassLoader()
				.getResourceAsStream(this.inputSrcFile);
		List<String> inputFilesLines = IOUtils.readLines(input,
				Charset.defaultCharset());

		List<String> filePathList = new ArrayList<String>();
		filePathList.addAll(loadFilePathsFromFile(inputFilesLines));
		if (verbose) {
			for (String filePath : filePathList) {
				System.out.println(filePath);
			}

		}
		return filePathList;
	}

}
