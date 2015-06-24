package com.nw.itext.main;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;

public class PDFScanner {

	PdfReader reader;

	public PDFScanner(String filePath) throws IOException {
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
					System.out.println(" " + fAnnot.get(PdfName.F));
				}
			}
		}
	}

	public void processFile() throws IOException, DocumentException {

		int numberOfPages = reader.getNumberOfPages();

		for (int n = 1; n < numberOfPages; n++) {
			inspectPage(n);
		}

	}

	public static void main(String[] args) throws IOException {
		String filePath = "result.pdf";
		try {
			new PDFScanner(filePath).processFile();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
