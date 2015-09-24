package com.nw.itext.pdfchanger.fileprocessors;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.nw.itext.pdfchanger.app.LogRecord;

public class PDFScanner implements FileProcessorIF{

	PdfReader reader;
	private int currentPDFPage = 1;
	private String currentFilePath;

	public PDFScanner(String filePath)  {
		this.currentFilePath=filePath;
		

	}

	private void inspectPage(int pageNumber) {

		PdfArray annots = reader.getPageN(pageNumber)
				.getAsArray(PdfName.ANNOTS);
		if (annots != null) {
			for (int i = 0; i < annots.size(); ++i) {
				inspectAnnotation(annots.getAsDict(i));

			}
		}
	}

	private void inspectAnnotation(PdfDictionary anotation) {
		PdfName subType = anotation.getAsName(PdfName.SUBTYPE);
		if (subType != null && PdfName.LINK.equals(subType)) {
			PdfDictionary action = anotation.getAsDict(PdfName.A);
			if (action != null) {
				PdfDictionary fAnnot = action.getAsDict(PdfName.F);
				if (fAnnot != null) {
					System.out.println(new LogRecord(currentFilePath, currentPDFPage, fAnnot.get(PdfName.F)+"", "success"));
					
				}
			}
		}
	}

	public boolean processFile()  {
		try {
			this.reader = new PdfReader(currentFilePath);
			int numberOfPages = reader.getNumberOfPages();

			for (int n = 1; n < numberOfPages; n++) {
				currentPDFPage=n;
				inspectPage(n);
			}
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		

	}
	
}
