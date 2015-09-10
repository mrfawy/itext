package com.nw.itext.processors;

public abstract class AbstractRuleMatcher implements RuleMatcherIF{

	public  String fixPath(String path){
		if (path.startsWith("file://")){
			path=path.substring(7);
		}
		path=path.replace("//", "/");
		return path;
	}
}
