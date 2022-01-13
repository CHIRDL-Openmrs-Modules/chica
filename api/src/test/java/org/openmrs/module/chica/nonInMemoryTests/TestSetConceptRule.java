package org.openmrs.module.chica.nonInMemoryTests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
public class TestSetConceptRule extends BaseModuleContextSensitiveTest
{

	@BeforeEach
	public void runBeforeEachTest() throws Exception {
//		authenticate();
	}
	@Test
	@Disabled
	public void testSetConceptRule() throws Exception
	{
		DssService dssService = Context.getService(DssService.class);
		Patient patient = Context.getPatientService().getPatient(9349);
		
		ArrayList<Rule> ruleList = new ArrayList<Rule>();
		Rule rule = new Rule();
		rule.setTokenName("testSetConcept");
		ruleList.add(rule);
		String stringResult = dssService.runRulesAsString(patient,ruleList);
		System.out.println(stringResult);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase()
	{
		return false;
	}
	
}
