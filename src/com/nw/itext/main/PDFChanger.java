package com.nw.itext.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.nw.itext.processors.RuleMatcherIF;
import com.nw.itext.processors.SimpleFileNameRuleMatcher;

public class PDFChanger {

	PdfReader reader;
	private int currentPDFPage = 1;
	private String currentFilePath;
	private String OUTPUT_PATH;

	private static List<RuleMatcherIF> ruleMatchers;

	public PDFChanger(String filePath) throws IOException, URISyntaxException {
		this.currentFilePath = filePath;
		
		Path p = Paths.get(new URI(filePath));		
		String fileName = p.getFileName().toString();
		
		OUTPUT_PATH = "result/" + fileName;
		//OUTPUT_PATH=OUTPUT_PATH.replaceAll(" ","%20");//fix spaces if found
		this.reader = new PdfReader(filePath);
		registerRuleMachers();

	}

	private void registerRuleMachers() {
		if (ruleMatchers == null) {
			ruleMatchers = new ArrayList<RuleMatcherIF>();
			ruleMatchers.add(new SimpleFileNameRuleMatcher(
					"../center/NBPFile.cfm?File="));
		}

	}

	private void processPage() {
		PdfDictionary pageDic = reader.getPageN(currentPDFPage);
		PdfArray annots = (PdfArray) PdfReader.getPdfObject(pageDic
				.get(PdfName.ANNOTS));
		if (annots != null && !annots.isEmpty()) {
			for (int i = 0; i < annots.size(); ++i) {
				processAnnotation(annots.getAsDict(i));

			}
		}
	}

	private void processAnnotation(PdfDictionary anotation) {
		if (isCandidateAnnotation(anotation)) {
			String oldTarget = extractTargetFile(anotation);
			boolean matchedFlag = false;
			for (RuleMatcherIF ruleMatcher : ruleMatchers) {
				if (ruleMatcher.isRuleMachingTargetFile(oldTarget)) {
					matchedFlag = true;
					String newTargetUrl = ruleMatcher.createURIStr(oldTarget);
					newTargetUrl = createNewUrl(oldTarget);
					PdfAction action = new PdfAction("http://");
					anotation.put(PdfName.A, action);
					PdfDictionary ac = anotation.getAsDict(PdfName.A);
					ac.put(PdfName.URI, new PdfString(newTargetUrl));
					System.out.println(
							new LogRecord(currentFilePath, currentPDFPage, "Source: " + oldTarget + "\tTarget: "
									+ newTargetUrl, "success"));
				}
			}
			if (!matchedFlag) {
				System.out.println(
						new LogRecord(currentFilePath, currentPDFPage, "Unable to match annotation no rule applies : "+oldTarget
								+ anotation, "Error"));
				
			}

		}

	}

	private boolean isCandidateAnnotation(PdfDictionary annotation) {
		PdfName subType = annotation.getAsName(PdfName.SUBTYPE);
		if (subType != null && PdfName.LINK.equals(subType)) {
			PdfDictionary action = annotation.getAsDict(PdfName.A);
			if (action != null && action.getAsDict(PdfName.F) != null) {
				System.out.println(new LogRecord(currentFilePath,
						currentPDFPage, "annotation found" + annotation, "Info"));
				return true;
			}
		}
		
		return false;
	}

	public String extractTargetFile(PdfDictionary anotation) {
		PdfDictionary action = anotation.getAsDict(PdfName.A);
		PdfDictionary fAnnot = action.getAsDict(PdfName.F);
		if (fAnnot != null && fAnnot.get(PdfName.F) != null) {
			return fAnnot.get(PdfName.F).toString();
		}
		throw new RuntimeException(
				"Unable to extract old file target from annotation");

	}

	public String createNewUrl(String s) {
		String result = "../center/NBPFile.cfm?File=" + s;
		return result;

	}

	public void processFile() {

		int numberOfPages = reader.getNumberOfPages();

		for (int n = 1; n < numberOfPages; n++) {
			currentPDFPage = n;
			processPage();
		}

		PdfStamper stamper;
		try {
			stamper = new PdfStamper(reader, new FileOutputStream(OUTPUT_PATH));
			stamper.close();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException {

		for (String filePath : Files.readAllLines(Paths.get("inputFiles.txt"),
				Charset.defaultCharset())) {
			if (filePath.isEmpty() || filePath.startsWith("#")) {
				continue;
			} else {
				try{
					filePath=filePath.replaceAll(" ","%20");//fix spaces if found
					new PDFChanger(filePath).processFile();
				}catch(Exception ex){
				ex.printStackTrace();
				System.out.println("skipping to next file ");
				}
				
			}

		}

	}
}
