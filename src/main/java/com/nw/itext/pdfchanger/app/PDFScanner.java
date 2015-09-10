package com.nw.itext.pdfchanger.app;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;

public class PDFScanner {

	PdfReader reader;
	private int currentPDFPage = 1;
	private String currentFilePath;

	public PDFScanner(String filePath) throws IOException {
		this.currentFilePath=filePath;
		this.reader = new PdfReader(filePath);

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

	public void processFile() throws IOException, DocumentException {

		int numberOfPages = reader.getNumberOfPages();

		for (int n = 1; n < numberOfPages; n++) {
			currentPDFPage=n;
			inspectPage(n);
		}

	}

	public static void main(String[] args) throws IOException {
		if (args == null || args.length < 1) {
			System.err
					.println("Missing input : file path to input.txt which contains all files to be processed");
			return;
		}
		String inputPath = args[0];

		for (String filePath : Files.readAllLines(Paths.get(inputPath),
				Charset.defaultCharset())) {
			if (filePath.isEmpty() || filePath.startsWith("#")) {
				continue;
			} else {
				try {
					filePath = filePath.replaceAll(" ", "%20");// fix spaces
					new PDFScanner(filePath).processFile();
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("skipping to next file ");
				}

			}

		}

		System.out.println("Done");
	}
}
