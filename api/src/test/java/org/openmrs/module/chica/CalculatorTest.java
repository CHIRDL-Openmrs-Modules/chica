package org.openmrs.module.chica;


import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class CalculatorTest extends BaseModuleContextSensitiveTest 
{
	
	@BeforeEach
	public void runBeforeEachTest() throws Exception {
		// create the basic user and give it full rights
		initializeInMemoryDatabase();
		// authenticate to the temp database
		authenticate();
	}
	
	/**
	 * @see {@link Calculator#calculatePercentile(double,double,double,double,String)}
	 * 
	 */
	
	@Test
	public void calculatePercentile_shouldCalculatePercentile()
			throws Exception {
		//TODO auto-generated
		executeDataSet(TestUtil.LENGTH_AGE_FILE);
		Calculator calculator = new Calculator();
		Calendar calendar = Calendar.getInstance();

		//height
		calendar.set(2007, Calendar.SEPTEMBER, 4);
		Double percentile = calculator.calculatePercentile(new Double(29), "M", calendar
				.getTime(), "length", "in");
		System.out.println(percentile);
		
	}

	/**
	 * @see {@link Calculator#lookupPercentile(String,double,int)}
	 * 
	 */
	@Test
	public void lookupPercentile_shouldLookupPercentile() throws Exception {
		//TODO auto-generated
		
		double ageInMonths = 24;
		int sex = 1;
		double s = 0;
		double l = 0;
		double m = 0;
		String type = "height";
		
		Calculator calculator = new Calculator();
		Calendar calendar = Calendar.getInstance();

		//look up the percentile for the age in months
		//Percentile percentileTable = calculator.lookupPercentile(type,ageInMonths,sex);
//		Assertions.fail("Not yet implemented");
	}
}
