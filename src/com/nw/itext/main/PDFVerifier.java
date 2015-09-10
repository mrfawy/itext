package com.nw.itext.main;

import com.itextpdf.text.pdf.PdfReader;

public class PDFVerifier {

	public static boolean isVerified(String filePath) {
		try{
			PdfReader reader=new PdfReader(filePath);
			reader.close();
			return true;
		}catch (Exception e) {
			
			return false;
		}
	}
}
