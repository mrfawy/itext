package com.nw.itext.processors;

public class SimpleFileNameRuleMatcher implements RuleMatcherIF {
	
	private String prefix;

	public SimpleFileNameRuleMatcher(String prefix) {		
		this.prefix=prefix;

	}

	/**
	 * Checks only files on format x.pdf , no up or relative paths
	 */
	@Override
	public boolean isRuleMachingTargetFile(String target) {
		if (target == null || target.isEmpty()) {
			return false;
		}
		/*if (target.startsWith("..") || target.startsWith("/")) {
			return false;
		}*/
		if (target.toLowerCase().endsWith(".pdf")) {
			return true;
		}
		return false;
	}

	@Override
	public String createURIStr(String target) {
		String result = prefix+target;
		return result;
	}
}
