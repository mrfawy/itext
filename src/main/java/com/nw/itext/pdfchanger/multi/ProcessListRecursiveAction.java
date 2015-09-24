package com.nw.itext.pdfchanger.multi;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import com.nw.itext.pdfchanger.app.ConfigLoader;
import com.nw.itext.pdfchanger.app.LogRecord;
import com.nw.itext.pdfchanger.fileprocessors.FileProcessorIF;

public class ProcessListRecursiveAction extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> srcFilePathList;
	private List<String> modifiedFilePathList;
	private int startIndex;
	private int endIndex;
	private FileProcessorFactory factory;
	private ConfigLoader configLoader;
	private static int SEQUENTIAL_THRESHOLD;

	public ProcessListRecursiveAction(List<String> srcFilePathList,
			List<String> modifiedFilePathList, int startIndex, int endIndex,
			FileProcessorFactory factory) {
		super();
		this.srcFilePathList = srcFilePathList;
		this.modifiedFilePathList = modifiedFilePathList;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		SEQUENTIAL_THRESHOLD = configLoader.getSequentialThreshold();
		this.factory = factory;
		this.configLoader = ConfigLoader.getInstance();
	}

	@Override
	protected void compute() {
		if (endIndex > startIndex) {
			if (endIndex - startIndex <= SEQUENTIAL_THRESHOLD) {
				System.out.println("start:" + startIndex + " " + "end:"
						+ endIndex);
				for (int i = startIndex; i < endIndex; i++) {
					String filePath = srcFilePathList.get(i);
					FileProcessorIF fileProcessor = factory
							.createFileProcessor(filePath);
					if (fileProcessor.processFile()) {
						modifiedFilePathList.add(filePath);
					}
					//if verification is enabled , verify file
					if (configLoader.getVerify()) {
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
				ProcessListRecursiveAction left = new ProcessListRecursiveAction(
						srcFilePathList, modifiedFilePathList, startIndex,
						midIndex, factory);
				left.fork();
				ProcessListRecursiveAction right = new ProcessListRecursiveAction(
						srcFilePathList, modifiedFilePathList, midIndex,
						endIndex, factory);
				right.fork();
			}
		}

	}

}
