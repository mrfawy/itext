package com.nw.itext.pdfchanger.multi;

import com.nw.itext.pdfchanger.fileprocessors.FileProcessorIF;
import com.nw.itext.pdfchanger.fileprocessors.PDFChanger;
import com.nw.itext.pdfchanger.fileprocessors.PDFRollBack;
import com.nw.itext.pdfchanger.fileprocessors.PDFScanner;
import com.nw.itext.pdfchanger.fileprocessors.PDFVerifier;

public class FileProcessorFactory {

	private FileProcessorTypeEnum fileProcessorTypeEnum;

	public FileProcessorFactory(FileProcessorTypeEnum fileProcessorTypeEnum) {
		super();
		this.fileProcessorTypeEnum = fileProcessorTypeEnum;

	}

	public synchronized FileProcessorIF  createFileProcessor(String filePath,
			FileProcessorTypeEnum fileProcessorType) {
		FileProcessorIF result = null;
		switch (fileProcessorType) {
		case PDFCHANGER: {
			result = new PDFChanger(filePath);
		}
			break;
		case PDFROLLBACK: {
			result = new PDFRollBack(filePath);
		}
			;
			break;
		case PDFSCANNER: {
			result = new PDFScanner(filePath);
		}
			;
			break;
		case PDFVERIFIER: {
			result = new PDFVerifier(filePath);
		}
			;
			break;
		default:
			break;
		}
		return result;

	}

	public synchronized FileProcessorIF createFileProcessor(String filePath) {
		return createFileProcessor(filePath, this.fileProcessorTypeEnum);
	}

}
