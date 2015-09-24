package com.nw.itext.pdfchanger.multi;

import com.nw.itext.pdfchanger.app.ConfigLoader;
import com.nw.itext.pdfchanger.fileprocessors.FileProcessorIF;
import com.nw.itext.pdfchanger.fileprocessors.PDFChanger;
import com.nw.itext.pdfchanger.fileprocessors.PDFRollBack;
import com.nw.itext.pdfchanger.fileprocessors.PDFScanner;
import com.nw.itext.pdfchanger.fileprocessors.PDFVerifier;

public class FileProcessorFactory {

	private FileProcessorTypeEnum fileProcessorTypeEnum;
	private ConfigLoader configLoader;

	public FileProcessorFactory(FileProcessorTypeEnum fileProcessorTypeEnum,
			ConfigLoader configLoader) {
		super();
		this.fileProcessorTypeEnum = fileProcessorTypeEnum;
		this.configLoader = configLoader;
	}

	public FileProcessorIF createFileProcessor(String filePath,
			FileProcessorTypeEnum fileProcessorType) {
		FileProcessorIF result = null;
		switch (fileProcessorType) {
		case PDFCHANGER: {
			result = new PDFChanger(filePath, configLoader.getPrefix(),
					configLoader.getTestOnly(), configLoader.getVerbose());
		}
			break;
		case PDFROLLBACK: {
			result = new PDFRollBack(filePath, configLoader.getBkpSuffix());
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

	public FileProcessorIF createFileProcessor(String filePath) {
		return createFileProcessor(filePath, this.fileProcessorTypeEnum);
	}

}
