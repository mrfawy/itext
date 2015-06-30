package com.nw.itext.main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
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
import com.nw.itext.processors.ParentFolderRuleMatcher;
import com.nw.itext.processors.RuleMatcherIF;
import com.nw.itext.processors.ServerRuleMatcher;
import com.nw.itext.processors.SimpleFileNameRuleMatcher;

public class PDFChanger {

	private String prefix;
	PdfReader reader;
	private int currentPDFPage = 1;
	private String currentFilePath;

	private boolean fileChanged = false;

	private static List<RuleMatcherIF> ruleMatchers;

	public PDFChanger(String filePath, String prefix) throws IOException,
			URISyntaxException {
		this.prefix = prefix;
		this.currentFilePath = filePath;
		this.reader = new PdfReader(filePath);
		registerRuleMachers();

	}

	private void registerRuleMachers() {
		if (ruleMatchers == null) {
			ruleMatchers = new ArrayList<RuleMatcherIF>();
			ruleMatchers.add(new SimpleFileNameRuleMatcher(prefix));
			ruleMatchers.add(new ParentFolderRuleMatcher(prefix,
					currentFilePath));
			ruleMatchers.add(new ServerRuleMatcher(prefix));
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
					
					UpdateAnnotation(anotation, oldTarget, ruleMatcher);
					matchedFlag = true;
					break;
				}
			}
			if (!matchedFlag) {
				System.out.println(new LogRecord(currentFilePath,
						currentPDFPage,
						"Unable to match annotation no rule applies : "
								+ oldTarget + anotation, "Error"));

			}

		}

	}

	private void UpdateAnnotation(PdfDictionary anotation, String oldTarget,
			RuleMatcherIF ruleMatcher) {
		String newTargetUrl = ruleMatcher.createURIStr(oldTarget);		
		PdfAction action = new PdfAction("http://");
		anotation.put(PdfName.A, action);
		PdfDictionary ac = anotation.getAsDict(PdfName.A);
		ac.put(PdfName.URI, new PdfString(newTargetUrl));
	//	fileChanged = true;
		System.out.println(new LogRecord(currentFilePath,
				currentPDFPage, "Source: " + oldTarget
						+ "\tTarget: " + newTargetUrl, "success"));
	}

	private boolean isCandidateAnnotation(PdfDictionary annotation) {
		PdfName subType = annotation.getAsName(PdfName.SUBTYPE);
		if (subType != null && PdfName.LINK.equals(subType)) {
			PdfDictionary action = annotation.getAsDict(PdfName.A);
			if (action != null && action.getAsDict(PdfName.F) != null) {
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

	

	public void processFile() {

		int numberOfPages = reader.getNumberOfPages();

		for (int n = 1; n < numberOfPages; n++) {
			currentPDFPage = n;
			processPage();
		}

		PdfStamper stamper;
		try {
			if (fileChanged) {
				if (backupOldFile(this.currentFilePath)) {
					Path oldFilePath = Paths.get(new URI(this.currentFilePath));
					Files.deleteIfExists(oldFilePath);
					OutputStream outputStream = Files.newOutputStream(
							oldFilePath, StandardOpenOption.CREATE);
					stamper = new PdfStamper(reader, outputStream);
					stamper.close();
				}

			}

		} catch (DocumentException | IOException | URISyntaxException e) {
			e.printStackTrace();
			System.out.println(new LogRecord(currentFilePath, -1,
					"Failed to write file", "Error"));
		}

	}

	private boolean backupOldFile(String filePath) {

		try {
			Path oldFilePath = Paths.get(new URI(filePath));
			String oldFileParent = filePath.substring(0,
					filePath.lastIndexOf("/"));
			String oldFileName = filePath.substring(filePath.lastIndexOf("/"));
			oldFileName += "_NBPBKP.pdf";
			Path bkpFilePath = Paths.get(new URI(oldFileParent + oldFileName));
			Files.copy(oldFilePath, bkpFilePath,
					StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(new LogRecord(currentFilePath, -1,
					"Failed to backup currentFile ", "Error"));
			return false;

		}

	}

	public static void main(String[] args) throws IOException {

		if (args == null || args.length < 1) {
			System.err
					.println("Missing input : file path to input.txt which contains all files to be processed");
			return;
		}
		String prefix = "../center/NBPFile.cfm?File=";
		String inputPath = args[0];

		for (String filePath : Files.readAllLines(Paths.get(inputPath),
				Charset.defaultCharset())) {
			if (filePath.isEmpty() || filePath.startsWith("#")) {
				continue;
			} else {
				try {
					filePath = filePath.replaceAll(" ", "%20");// fix spaces

					new PDFChanger(filePath, prefix).processFile();
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("skipping to next file ");
				}

			}

		}
		System.out.println("Done");

	}
}
