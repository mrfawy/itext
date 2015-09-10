package com.nw.itext.main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystemException;
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

	private boolean testOnly = false;
	private boolean fileChanged=false;

	private static List<RuleMatcherIF> ruleMatchers;
	
	

	public PDFChanger(String filePath, String prefix, boolean testOnly) throws IOException,
			URISyntaxException {
		this.prefix = prefix;
		this.testOnly=testOnly;
		this.currentFilePath = filePath;
		this.reader = new PdfReader(filePath);
		registerRuleMachers();

	}

	private void registerRuleMachers() {
		if (ruleMatchers == null) {
			ruleMatchers = new ArrayList<RuleMatcherIF>();
			ruleMatchers.add(new SimpleFileNameRuleMatcher(prefix,currentFilePath));
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
		try{
			
			String newTargetUrl = ruleMatcher.createURIStr(oldTarget);
			PdfAction action = new PdfAction("http://");
			anotation.put(PdfName.A, action);		
			action.put(PdfName.URI, new PdfString(newTargetUrl));
			if(!testOnly){
				fileChanged = true;
			}			
			System.out
					.println(new LogRecord(currentFilePath, currentPDFPage,
							"Source: " + oldTarget + "\tTarget: " + newTargetUrl,
							"success"));
		}catch(Exception e){
			System.err.println(oldTarget);
			e.printStackTrace();
		}
		
	}

	private boolean isCandidateAnnotation(PdfDictionary annotation) {
		PdfName subType = annotation.getAsName(PdfName.SUBTYPE);
		if (subType != null && PdfName.LINK.equals(subType)) {
			PdfDictionary action = annotation.getAsDict(PdfName.A);
			// skip if already processed before
			if (action != null && !isProcessedBefore(annotation)) {
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
			if (uri!=null && uri.toString().startsWith(prefix)) {
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
				"Unable to extract old file target from annotation");

	}

	public void processFile() {

		int numberOfPages = reader.getNumberOfPages();

		for (int n = 1; n < numberOfPages; n++) {
			currentPDFPage = n;
			processPage();
		}

		
		try {
			if (fileChanged) {
				if (backupOldFile(this.currentFilePath)) {
					Path oldFilePath = Paths.get(this.currentFilePath);
					Path tmpFilePath = getPathAppendStr(this.currentFilePath,"_newTemp");	
					try{
						OutputStream outputStream = Files.newOutputStream(
								tmpFilePath, StandardOpenOption.CREATE);					
						PdfStamper stamper = new PdfStamper(reader, outputStream);						
						stamper.close();
						reader.close();
						Files.delete(oldFilePath);
						Files.copy(tmpFilePath,oldFilePath, StandardCopyOption.REPLACE_EXISTING);
						Files.delete(tmpFilePath);
					}catch(FileSystemException ex){
						ex.printStackTrace();
						System.out.println(new LogRecord(currentFilePath, -1,
								"warning writing file" + ex.getMessage(), "warning"));
					}
					
				}

			}

		} catch (DocumentException | IOException  e) {
			e.printStackTrace();
			System.out.println(new LogRecord(currentFilePath, -1,
					"Failed to write file", "Error"));
		}

	}

	public static Path getPathAppendStr(String filePath,String suffix){			
		String oldFileParent = filePath.substring(0,
				filePath.lastIndexOf("\\"));
		String oldFileName = filePath.substring(filePath.lastIndexOf("\\"));
		oldFileName +=suffix;
		Path newFilePath = Paths.get(oldFileParent + oldFileName);
		return newFilePath;
	}
	private boolean backupOldFile(String filePath) {

		try {
						
			Path oldFilePath = Paths.get(filePath);			
			Path bkpFilePath = getPathAppendStr(filePath, "_NBPBKP.pdf");
			Files.copy(oldFilePath, bkpFilePath,
					StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
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
					.println("Missing input : file path to input.txt which contains all files to be processed, or a directory to scan for PDFs");
			return;
		}
		//default prefix
		String prefix = "../center/NBPElibContentLoad.cfm?FilePath=";
		if(args.length>1){
			prefix=args[1];
		}
		boolean testOnly=false;
		if(args.length>2){
			testOnly=true;
		}
		String inputPath = args[0];
		
		// if single file , treat as input file which has list of file paths per line , else scan the directory for PDFS
		List<String> filePathList=FileLocator.generateFilePathList(inputPath);	
		if(filePathList==null){
			System.err.println("Error: No files Found!, please check your input. ");
			return;
		}
		for(String filePath:filePathList){
			try {
				new PDFChanger(filePath, prefix,testOnly).processFile();
				Path oldFilePath = Paths.get(filePath);
				Path tmpFilePath = getPathAppendStr(filePath,"_newTemp");
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("skipping to next file ");
			}	
		}	
		System.out.println("Verifing Files ...");
		for(String filePath:filePathList){
			if(!PDFVerifier.isVerified(filePath)){
				System.err.println(new LogRecord(filePath, -1,
						"Failed to Verify currentFile ", "Error"));
			}		
		}
		System.out.println("Done");

	}
}
