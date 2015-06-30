package com.nw.itext.processors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AbstractRuleMatcherTest {

	AbstractRuleMatcher matcher;

	@Before
	public void setUp() throws Exception {
		matcher = new SimpleFileNameRuleMatcher("");
	}

	@Test
	public void testRemoveProtocol() {
		String path = matcher.fixPath("file://a/b/c.pdf");
		Assert.assertEquals("a/b/c.pdf", path);
	}

}
