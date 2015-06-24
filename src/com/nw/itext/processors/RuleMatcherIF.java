package com.nw.itext.processors;

public interface RuleMatcherIF {
	boolean isRuleMachingTargetFile(String target);
	 String createURIStr(String target);
}
