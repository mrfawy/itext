package com.nw.itext.pdfchanger.multi;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import com.nw.itext.pdfchanger.app.ConfigLoader;

public class FileLocatorTask extends RecursiveTask<List<String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String filePath;

	public FileLocatorTask(String filePath) {

		this.filePath = filePath;

	}

	@Override
	protected List<String> compute() {
		List<String> foundFilesList = new ArrayList<String>();
		List<RecursiveTask<List<String>>> forks = new LinkedList<>();
		File file = new File(filePath);
		if (file.exists()) {
			if (!file.isDirectory()) {
				// add only pdf files
				if (filePath.toLowerCase().endsWith(".pdf")) {
					if (!filePath.contains(ConfigLoader.getInstance()
							.getBkpSuffix())) {
						foundFilesList.add(filePath);
					}

				}
			}
			// it's a directory , add all direct files , and fork sub-folders
			else {
				for (File entry : file.listFiles()) {
					if (entry.isDirectory()) {
						FileLocatorTask task = new FileLocatorTask(
								entry.getPath());
						forks.add(task);
						task.fork();
					} else {
						// add only pdf files
						if (entry.getPath().toLowerCase().endsWith(".pdf")) {
							if (!entry.getPath().contains(
									ConfigLoader.getInstance().getBkpSuffix())) {
								foundFilesList.add(entry.getPath());
							}
						}
					}
				}
			}
		}
		for (RecursiveTask<List<String>> task : forks) {
			foundFilesList.addAll(task.join());
		}
		return foundFilesList;

	}

}