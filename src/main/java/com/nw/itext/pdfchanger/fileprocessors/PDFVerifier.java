package com.nw.itext.pdfchanger.fileprocessors;

import com.itextpdf.text.pdf.PdfReader;

public class PDFVerifier implements FileProcessorIF {

	private String filePath;

	public PDFVerifier(String filePath) {
		super();
		this.filePath = filePath;
	}

	@Override
	public boolean processFile() {
		try {
			PdfReader reader = new PdfReader(filePath);
			reader.close();
			return true;
		} catch (Exception e) {

			return false;
		}
	}

}
