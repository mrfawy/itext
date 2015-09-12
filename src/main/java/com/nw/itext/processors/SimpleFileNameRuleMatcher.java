package com.nw.itext.processors;

import java.util.ArrayList;
import java.util.List;

public class SimpleFileNameRuleMatcher extends AbstractRuleMatcher {

	private String prefix;
	private String filePath;
	private List<RuleMatcherIF> complementMatchers;

	public SimpleFileNameRuleMatcher(String prefix, String filePath) {
		this.prefix = prefix;
		this.filePath = filePath;
		if (filePath.startsWith("file://")||filePath.startsWith("File://")) {
			filePath = filePath.substring(6);
		}
		complementMatchers = new ArrayList<RuleMatcherIF>();
		complementMatchers.add(new ServerRuleMatcher(prefix));

	}

	/**
	 * Checks only files on format x.pdf , no up or relative paths
	 */
	@Override
	public boolean isRuleMachingTargetFile(String target) {
		if (target == null || target.isEmpty()) {
			return false;
		}
		/*
		 * if (target.startsWith("..") || target.startsWith("/")) { return
		 * false; }
		 */
		if (target.toLowerCase().endsWith(".pdf") && !target.contains("/")) {
			return true;
		}
		return false;
	}

	public String getParentPath(String path) {
		path = path.replace("//", "/");
		path.replace("File:", "file:");
		path = path.replace("file:/", "file://");
		path = path.replace("/", "\\");

		int lastIndex = path.lastIndexOf("\\");
		if(lastIndex!=-1){
			path = path.substring(0, lastIndex);	
		}
		

		path += "\\";
		return path;
	}

	@Override
	public String createURIStr(String target) {
		String parentpath=getParentPath(filePath);
		// remove server
		for (RuleMatcherIF matcher : complementMatchers) {
			if (matcher.isRuleMachingTargetFile(parentpath)) {
				parentpath = matcher.createURIStr(parentpath);
			}
		}
		String result = parentpath+ target;
		return result;
	}
}
