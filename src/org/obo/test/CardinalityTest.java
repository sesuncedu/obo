package org.obo.test;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.bbop.io.AuditedPrintStream;
import org.obo.datamodel.IdentifiedObject;
import org.obo.datamodel.Link;
import org.obo.datamodel.OBOObject;
import org.obo.datamodel.OBORestriction;
import org.obo.util.TermUtil;

public class CardinalityTest extends AbstractReasonerTest {

	public CardinalityTest(String name) {
		super(name);
	}

	public Collection<String> getFilesToLoad() {
		String[] files = { "card.obo"};
		return Arrays.asList(files);
	}
	

	public void testForCard() throws Exception {

		OBOObject obj = (OBOObject) session.getObject("GO:0022614");
		for (Link link : obj.getParents()) {
			OBORestriction r = (OBORestriction)link;
			System.out.println(r);
			Integer card = r.getCardinality();
			System.out.println("c="+card);
			assertTrue(card==2);
		}
		writeTempOBOFile();
	}
}