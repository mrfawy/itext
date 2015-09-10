package com.nw.itext.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
	private Properties prop;

	public boolean loadConfig(String configPath) {		
		InputStream input = null;
		try {

			input = new FileInputStream(configPath);
			prop= new Properties();
			// load a properties file
			prop.load(input);

			return true;

		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getInputSrc(){
		if(prop==null||!prop.containsKey("InputSrc")){
			throw new RuntimeException("Property Not found: InputSrc , check Config.properties ");
		}
		return prop.get("InputSrc").toString();
		
	}
	public boolean getVerify(){
		return getBooleanValue("Verify");
		
	}
	public boolean getTestOnly(){
		return getBooleanValue("TestOnly");
	}
	public boolean getVerbose(){
		return getBooleanValue("Verbose");
	}
	public String getPrefix(){		
		if(prop==null||!prop.containsKey("Prefix")){
			throw new RuntimeException("Property Not found: Prefix , check Config.properties ");
		}
		return prop.get("Prefix").toString();
		
	}
	public boolean getBooleanValue(String key){
		if(prop==null||!prop.containsKey(key)){
			throw new RuntimeException("Property Not found: "+key +" , check Config.properties ");
		}
		try{
			boolean value=Boolean.parseBoolean(prop.get(key).toString());
			return value;
		}catch(Exception e){
			throw new RuntimeException("Invalid boolean value for "+key+" , check Config.properties ");
		}
	}

}