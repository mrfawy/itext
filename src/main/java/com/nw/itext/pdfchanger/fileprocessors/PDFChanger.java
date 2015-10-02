package com.nw.itext.pdfchanger.fileprocessors;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.nw.itext.pdfchanger.app.ConfigLoader;
import com.nw.itext.pdfchanger.app.LogRecord;
import com.nw.itext.processors.ParentFolderRuleMatcher;
import com.nw.itext.processors.RuleMatcherIF;
import com.nw.itext.processors.ServerRuleMatcher;
import com.nw.itext.processors.SimpleFileNameRuleMatcher;

public class PDFChanger implements FileProcessorIF {

	private String prefix;
	PdfReader reader;
	private int currentPDFPage = 1;
	private String currentFilePath;
	
	
	private boolean fileChanged = false;
	

	private List<RuleMatcherIF> ruleMatchers;

	public PDFChanger(String filePath) {		
		this.currentFilePath = filePath;	
		this.prefix=ConfigLoader.getInstance().getPrefix();
		registerRuleMachers();
		

	}

	private void registerRuleMachers() {
		if (ruleMatchers == null) {
			ruleMatchers = new ArrayList<RuleMatcherIF>();
			ruleMatchers.add(new SimpleFileNameRuleMatcher(prefix,
					currentFilePath));
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
		try {

			String newTargetUrl = ruleMatcher.createURIStr(oldTarget);
			PdfAction action = new PdfAction("http://");
			anotation.put(PdfName.A, action);
			action.put(PdfName.URI, new PdfString(newTargetUrl));
			if (!ConfigLoader.getInstance().getTestOnly()) {
				fileChanged = true;
			}
			if (ConfigLoader.getInstance().getVerbose()) {
				System.out.println(new LogRecord(currentFilePath,
						currentPDFPage, "Source: " + oldTarget + "\tTarget: "
								+ newTargetUrl, "success"));
			}
		} catch (Exception e) {
			System.err.println(oldTarget);
			e.printStackTrace();
		}

	}

	private boolean isCandidateAnnotation(PdfDictionary annotation) {
		PdfName subType = annotation.getAsName(PdfName.SUBTYPE);
		if (subType != null && PdfName.LINK.equals(subType)) {
			PdfDictionary action = annotation.getAsDict(PdfName.A);
			// skip if already processed before
			if (action != null
					&& action.getAsDict(PdfName.F) != null
					&& action.getAsDict(PdfName.F).get(PdfName.F) != null
					&& action.getAsDict(PdfName.F).get(PdfName.F).toString()
							.toLowerCase().contains(".pdf")
					&& !isProcessedBefore(annotation)) {
				return true;
			}
		}

		return false;
	}

	public boolean isProcessedBefore(PdfDictionary annotation) {
		if (annotation == null || annotation.getAsDict(PdfName.A) == null) {
			return true;
		}

		try {
			PdfDictionary ac = annotation.getAsDict(PdfName.A);
			PdfString uri = (PdfString) ac.get(PdfName.URI);
			if (uri != null && uri.toString().startsWith(prefix)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				"Unable to extract old file target from annotation, PATH: "
						+ this.currentFilePath);

	}

	public boolean processFile() {
		try {
			
			
			this.reader = new PdfReader(currentFilePath);			
			int numberOfPages = reader.getNumberOfPages();
			
			for (int n = 1; n < numberOfPages; n++) {
				currentPDFPage = n;
				processPage();
			}
			
			if (fileChanged) {
				if (backupOldFile(this.currentFilePath)) {
					Path oldFilePath = Paths.get(this.currentFilePath);
					Path tmpFilePath = getPathAppendStr(this.currentFilePath,
							"_newTemp");
					try {
						OutputStream outputStream = Files.newOutputStream(
								tmpFilePath, StandardOpenOption.CREATE);
						PdfStamper stamper = new PdfStamper(reader,
								outputStream);
						stamper.close();
						reader.close();
						Files.delete(oldFilePath);
						Files.copy(tmpFilePath, oldFilePath,
								StandardCopyOption.REPLACE_EXISTING);
						Files.delete(tmpFilePath);
						System.out.println(currentFilePath);
						return true;
					} catch (FileSystemException ex) {
						ex.printStackTrace();
						System.err.println(new LogRecord(currentFilePath, -1,
								"Error writing file" + ex.getMessage(),
								"warning"));
						return false;
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(new LogRecord(currentFilePath, -1,
					"Failed to process file", "Error"));
			return false;
		}
		return false;

	}

	public Path getPathAppendStr(String filePath, String suffix) {
		String oldFileParent = filePath
				.substring(0, filePath.lastIndexOf("\\"));
		String oldFileName = filePath.substring(filePath.lastIndexOf("\\"));
		oldFileName += suffix;
		Path newFilePath = Paths.get(oldFileParent + oldFileName);
		return newFilePath;
	}

	private boolean backupOldFile(String filePath) {

		try {			
			String fileName=filePath.substring(filePath.lastIndexOf("\\")+1);
			String bkpFolderPath= filePath.substring(0,filePath.lastIndexOf("\\")+1)+ConfigLoader.getInstance().getBkpFolder();
						
			File bkpFolder=new File(bkpFolderPath);
			if(!bkpFolder.exists()){
				bkpFolder.mkdir();
			}
			Path oldFilePath=Paths.get(filePath);
			Path bkpFilePath=Paths.get(bkpFolderPath+"\\"+fileName+"_"+ConfigLoader.getInstance().getBkpSuffix()+".pdf");			
			Files.copy(oldFilePath, bkpFilePath,
					StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(new LogRecord(currentFilePath, -1,
					"Failed to backup currentFile ", "Error"));
			return false;

		}

	}

}
