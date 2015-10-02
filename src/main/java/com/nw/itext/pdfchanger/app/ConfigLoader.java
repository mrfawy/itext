package com.nw.itext.pdfchanger.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.sun.swing.internal.plaf.synth.resources.synth;

public class ConfigLoader {
	private Properties prop;

	private static ConfigLoader me;

	public static boolean initInstance(String configPath) {

		me = new ConfigLoader();
		return me.loadConfig(configPath);
		
	}

	public synchronized static ConfigLoader getInstance() {
		if (me == null) {
			throw new RuntimeException(
					"Config loader InitMethod should be called first");
		}
		return me;
	}

	private ConfigLoader() {
		
	}

	private boolean loadConfig(String configPath) {
		InputStream input = null;
		try {

			input = this.getClass().getClassLoader()
					.getResourceAsStream(configPath);
			prop = new Properties();
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

	public synchronized String getInputSrc() {
		if (prop == null || !prop.containsKey("InputSrc")) {
			throw new RuntimeException(
					"Property Not found: InputSrc , check Config.properties ");
		}
		return prop.get("InputSrc").toString();

	}

	public synchronized boolean getVerify() {
		return getBooleanValue("Verify");

	}

	public synchronized boolean getTestOnly() {
		return getBooleanValue("TestOnly");
	}

	public synchronized boolean getVerbose() {
		return getBooleanValue("Verbose");
	}

	public synchronized String getPrefix() {
		if (prop == null || !prop.containsKey("Prefix")) {
			throw new RuntimeException(
					"Property Not found: Prefix , check Config.properties ");
		}
		return prop.get("Prefix").toString();

	}

	public synchronized int getThreadPoolSize() {
		if (prop == null || !prop.containsKey("ThreadPoolSize")) {
			throw new RuntimeException(
					"Property Not found: ThreadPoolSize , check Config.properties ");
		}
		return Integer.parseInt(prop.get("ThreadPoolSize").toString());

	}

	public synchronized int getSequentialThreshold() {
		if (prop == null || !prop.containsKey("SequentialThreshold")) {
			throw new RuntimeException(
					"Property Not found: SequentialThreshold , check Config.properties ");
		}
		return Integer.parseInt(prop.get("SequentialThreshold").toString());

	}

	public synchronized String getBkpSuffix() {
		if (prop == null || !prop.containsKey("BkpSuffix")) {
			throw new RuntimeException(
					"Property Not found: BkpSuffix , check Config.properties ");
		}
		return prop.get("BkpSuffix").toString();
	}
	public synchronized String getBkpFolder() {
		if (prop == null || !prop.containsKey("BkpFolder")) {
			throw new RuntimeException(
					"Property Not found: BkpFolder , check Config.properties ");
		}
		return prop.get("BkpFolder").toString();
	}

	public synchronized boolean getBooleanValue(String key) {
		if (prop == null || !prop.containsKey(key)) {
			throw new RuntimeException("Property Not found: " + key
					+ " , check Config.properties ");
		}
		try {
			boolean value = Boolean.parseBoolean(prop.get(key).toString());
			return value;
		} catch (Exception e) {
			throw new RuntimeException("Invalid boolean value for " + key
					+ " , check Config.properties ");
		}
	}

}
