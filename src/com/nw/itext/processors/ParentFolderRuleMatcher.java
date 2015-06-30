package com.nw.itext.processors;

import java.util.ArrayList;
import java.util.List;

public class ParentFolderRuleMatcher implements RuleMatcherIF{
	
	private String filePath;
	
	private List<RuleMatcherIF> complementMatchers;
	
	public ParentFolderRuleMatcher(String prefix,String filePath) {
		this.filePath=filePath;
		if(filePath.startsWith("file://")){
			filePath=filePath.substring(6);
		}
		complementMatchers=new ArrayList<RuleMatcherIF>();
		complementMatchers.add(new ServerRuleMatcher(prefix));
	}
	
	

	@Override
	public boolean isRuleMachingTargetFile(String target) {
		if (target.startsWith("../")){
			return true;
		}
		return false;
	}

	public int countUps(String target){
		int count=0;
		int lastIndex=-1;
		do{
			lastIndex=target.indexOf("../", lastIndex+1);
			if(lastIndex>-1){
				count++;
			}
		}while(lastIndex!=-1);
		return count;
	}
	public String popFolders(String path,int count){
		path=path.replace("//", "/");
		path=path.replace("file:/","file://");
		for (int i=0;i<=count;i++){
			int lastIndex=path.lastIndexOf("/");
			path=path.substring(0,lastIndex);			
		
		}
		path+="/";
		return path;
	}
	@Override
	public String createURIStr(String target) {
		int countUps=countUps(target);
		String result=popFolders(filePath, countUps);
		result+=target.substring(countUps*3);// each countup counts for ../
		for(RuleMatcherIF matcher:complementMatchers){
			if(matcher.isRuleMachingTargetFile(result)){
				result=matcher.createURIStr(result);
			}
		}
		return result;
	}

}
