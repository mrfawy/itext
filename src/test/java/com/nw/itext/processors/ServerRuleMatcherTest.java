package com.nw.itext.processors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.nw.itext.processors.ServerRuleMatcher;

public class ServerRuleMatcherTest {

	ServerRuleMatcher matcher;

	@Before
	public void setUp() throws Exception {
		matcher = new ServerRuleMatcher("");
	}
	
	@Test
	public void testIsRuleMachingTargetFile() {
		Assert.assertFalse(matcher.isRuleMachingTargetFile("../myfile.pdf"));
		Assert.assertFalse(matcher.isRuleMachingTargetFile("/myfile.pdf"));
		Assert.assertFalse(matcher.isRuleMachingTargetFile("asad/myfile.pdf"));
		Assert.assertFalse(matcher.isRuleMachingTargetFile("myfile.pdf"));
		
		Assert.assertTrue(matcher.isRuleMachingTargetFile("/urbwsr01/a/b/c/myfile.pdf"));
		Assert.assertTrue(matcher.isRuleMachingTargetFile("urbwsr01/a/b/c/myfile.pdf"));
		Assert.assertTrue(matcher.isRuleMachingTargetFile("/urbwsr01.host.com/a/b/c/myfile.pdf"));
		Assert.assertTrue(matcher.isRuleMachingTargetFile("/URBWSR01.host.com/a/b/c/myfile.pdf"));
	}

	@Test
	public void testRemoveServer() {
		String result = matcher
				.removeServer("/urbwsr01/manuals/Commerciallines/eManual/BOP-Premier//BOP-Manual-DAA/DAA-BP_-StatePages-SC-2014-08.pdf");
		Assert.assertEquals(
				"\\manuals\\Commerciallines\\eManual\\BOP-Premier\\BOP-Manual-DAA\\DAA-BP_-StatePages-SC-2014-08.pdf",
				result);
	}
	@Test
	public void testRemoveServerNoRoot() {
		String result = matcher
				.removeServer("urbwsr01/manuals/Commerciallines/eManual/BOP-Premier//BOP-Manual-DAA/DAA-BP_-StatePages-SC-2014-08.pdf");
		Assert.assertEquals(
				"\\manuals\\Commerciallines\\eManual\\BOP-Premier\\BOP-Manual-DAA\\DAA-BP_-StatePages-SC-2014-08.pdf",
				result);
	}

	
	@Test
	public void testRemoveServerCapital() {
		String result = matcher
				.removeServer("/URBWSR01/manuals/Commerciallines/eManual/BOP-Premier//BOP-Manual-DAA/DAA-BP_-StatePages-SC-2014-08.pdf");
		Assert.assertEquals(
				"\\manuals\\Commerciallines\\eManual\\BOP-Premier\\BOP-Manual-DAA\\DAA-BP_-StatePages-SC-2014-08.pdf",
				result);
	}
	@Test
	public void testRemoveServerHostName() {
		String result = matcher
				.removeServer("/urbwsr01.allied.nwie.net/manuals/Commerciallines/eManual/BOP-Premier//BOP-Manual-DAA/DAA-BP_-StatePages-SC-2014-08.pdf");
		Assert.assertEquals(
				"\\manuals\\Commerciallines\\eManual\\BOP-Premier\\BOP-Manual-DAA\\DAA-BP_-StatePages-SC-2014-08.pdf",
				result);
	}

}
