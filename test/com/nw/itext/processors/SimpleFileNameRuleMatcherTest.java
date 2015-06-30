package com.nw.itext.processors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimpleFileNameRuleMatcherTest {
	private RuleMatcherIF matcher;

	@Before
	public void setUp() throws Exception {
		matcher = new SimpleFileNameRuleMatcher("PREFIX/");
	}

	@Test
	public void testIsRuleMachingTargetFile() {
		Assert.assertFalse(matcher.isRuleMachingTargetFile("../myfile.pdf"));
		Assert.assertFalse(matcher.isRuleMachingTargetFile("/myfile.pdf"));
		Assert.assertFalse(matcher.isRuleMachingTargetFile("asad/myfile.pdf"));
		
		Assert.assertTrue(matcher.isRuleMachingTargetFile("myfile.pdf"));
	}

	@Test
	public void test() {
		String result = matcher.createURIStr("myfile.pdf");
		Assert.assertEquals("PREFIX/myfile.pdf", result);
	}

}
