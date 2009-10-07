package org.openmrs.module.chica;


import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dss.util.Util;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class CalculatorTest 
{
	/**
	 * @see {@link Calculator#calculatePercentile(double,double,double,double,String)}
	 * 
	 */
	
	@Test
	@Verifies(value = "should calculate percentile", method = "calculatePercentile(double,double,double,double,String)")
	public void calculatePercentile_shouldCalculatePercentile()
			throws Exception {
		//TODO auto-generated
		
		Calculator calculator = new Calculator();
		Calendar calendar = Calendar.getInstance();

		//height
		calendar.set(2007, Calendar.SEPTEMBER, 4);
		Double percentile = calculator.calculatePercentile(29, "M", calendar
				.getTime(), "length", "in");
		System.out.println(percentile);
		
	}

	/**
	 * @see {@link Calculator#lookupPercentile(String,double,int)}
	 * 
	 */
	@Test
	@Verifies(value = "should lookup percentile", method = "lookupPercentile(String,double,int)")
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
		Assert.fail("Not yet implemented");
	}
}