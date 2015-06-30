package com.nw.itext.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerRuleMatcher extends AbstractRuleMatcher {

	private String prefix;

	public ServerRuleMatcher(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public boolean isRuleMachingTargetFile(String target) {
		String patternString = "urbwsr01";

		Pattern pattern = Pattern.compile(patternString,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(target);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public String removeServer(String target) {
		target = fixPath(target);
		String patternString = "urbwsr01";

		Pattern pattern = Pattern.compile(patternString,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(target);
		if (matcher.find()) {
			int index = (target.indexOf("/", matcher.start() + 1));// looks for
																	// first /
																	// after
																	// server
																	// name
			String result = target.substring(index);
			result = result.replace("/", "\\/");
			return result;
		}
		throw new RuntimeException("Server name not found");
	}

	@Override
	public String createURIStr(String target) {
		String result = removeServer(target);
		return prefix + result;
	}

}
