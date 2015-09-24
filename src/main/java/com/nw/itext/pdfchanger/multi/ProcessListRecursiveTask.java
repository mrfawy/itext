package com.nw.itext.pdfchanger.multi;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.RecursiveTask;

import com.nw.itext.pdfchanger.app.ConfigLoader;
import com.nw.itext.pdfchanger.app.LogRecord;
import com.nw.itext.pdfchanger.fileprocessors.FileProcessorIF;

public class ProcessListRecursiveTask extends RecursiveTask<List<String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> srcFilePathList;
	private int startIndex;
	private int endIndex;
	private volatile FileProcessorFactory factory;
	private static volatile int SEQUENTIAL_THRESHOLD;

	public ProcessListRecursiveTask(List<String> srcFilePathList,
			int startIndex, int endIndex, FileProcessorFactory factory) {
		super();
		this.srcFilePathList = srcFilePathList;

		this.startIndex = startIndex;
		this.endIndex = endIndex;
		SEQUENTIAL_THRESHOLD = ConfigLoader.getInstance()
				.getSequentialThreshold();
		this.factory = factory;

	}

	@Override
	protected List<String> compute() {
		List<String> modifiedFiles = new Vector<String>();
		if (endIndex > startIndex) {
			if (endIndex - startIndex <= SEQUENTIAL_THRESHOLD) {
				for (int i = startIndex; i < endIndex; i++) {
					String filePath = srcFilePathList.get(i);
					FileProcessorIF fileProcessor = null;

					fileProcessor = factory.createFileProcessor(filePath);

					if (fileProcessor.processFile()) {

						modifiedFiles.add(filePath);
					}
					// if verification is enabled , verify file
					if (ConfigLoader.getInstance().getVerify()) {
						FileProcessorIF verifier = factory.createFileProcessor(
								filePath, FileProcessorTypeEnum.PDFVERIFIER);

						if (!verifier.processFile()) {
							System.err.println(new LogRecord(filePath, -1,
									"Failed to Verify currentFile ", "Error"));

						}
					}
				}
				// run forks , divide and conquer list into two halfs
			} else {
				int midIndex = (startIndex + endIndex) / 2;
				ProcessListRecursiveTask left = new ProcessListRecursiveTask(
						srcFilePathList, startIndex, midIndex, factory);
				left.fork();
				ProcessListRecursiveTask right = new ProcessListRecursiveTask(
						srcFilePathList, midIndex, endIndex, factory);
				right.fork();

				modifiedFiles.addAll(left.join());
				modifiedFiles.addAll(right.join());
			}
		}

		return modifiedFiles;

	}

}
