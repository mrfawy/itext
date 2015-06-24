package com.nw.itext.main;

public class LogRecord {
	
	String file ;
	int page;	
	String message;
	String status;
	public LogRecord(String file, int page, String message, String status) {
		super();
		this.file = file;
		this.page = page;
		this.message = message;
		this.status = status;
	}
	
	@Override
	public String toString() {
		
		return file+"\t, "+page+"\t, "+message+"\t, "+status;
	}
	

}
