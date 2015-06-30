package com.nw.itext.processors;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.nw.itext.processors.ParentFolderRuleMatcher;


public class ParentFolderRuleMatcherTest {
	
	ParentFolderRuleMatcher matcher;

	@Before
	public void setUp() throws Exception {
		
		matcher=new ParentFolderRuleMatcher("","file://urbwsr01/manuals/commerciallines/emanual/farm/farm-manual/CA-NICOA//CA-FLZ-endsummary.pdf");
	}
	@Test
	public void testCountUps(){
		int ups=matcher.countUps("../../../../PolicyForms/Farm/FL70606-0101-00.PDF");
		Assert.assertEquals("4", ups+"");
	}
	
	@Test
	public void testPopFolders(){
		String result=matcher.popFolders("file://urbwsr01/manuals/commerciallines/emanual/farm/farm-manual/CA-NICOA//CA-FLZ-endsummary.pdf", 4);
		Assert.assertEquals(
				"file://urbwsr01/manuals/commerciallines/",
				result);
	}
	

	@Test
	public void test() {
		String result=matcher.createURIStr("../../../../PolicyForms/Farm/FL70606-0101-00.PDF");
		Assert.assertEquals(
				"\\/manuals\\/commerciallines\\/PolicyForms\\/Farm\\/FL70606-0101-00.PDF",
				result);
	}

}
