package org.openmrs.module.chica.test;

import org.junit.Test;
import java.util.Calendar;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.openmrs.module.chica.Calculator;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
public class TestCalculator extends BaseModuleContextSensitiveTest
{

	/**
	 * Set up the database with the initial dataset before every test method in
	 * this class.
	 * 
	 * Require authorization before every test method in this class
	 * 
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		// authenticate to the temp database
		authenticate();
	}
	/**
	 * 
	 */
	@Test
	public void testTranslateGender()
	{
		Calculator calculator = new Calculator();

		// male
		assertEquals(1, calculator.translateGender("M"));

		// female
		assertEquals(2, calculator.translateGender("F"));

		// null
		assertEquals(0, calculator.translateGender(null));

		// invalid string
		assertEquals(0, calculator.translateGender("U"));
	}

	@Test
	public void testCalculatePercentile()
	{
		Calculator calculator = new Calculator();
		Calendar calendar = Calendar.getInstance();

		//height
		calendar.set(2007, Calendar.SEPTEMBER, 4);
		Double percentile = calculator.calculatePercentile(29, "M", calendar
				.getTime(), "length", "in");
		System.out.println(percentile);
		
		calendar.set(2006, Calendar.APRIL,6);
		percentile = calculator.calculatePercentile(34, "M", calendar
				.getTime(), "length", "in");
		System.out.println(percentile);
		
		calendar.set(2001, Calendar.MAY,5);
		percentile = calculator.calculatePercentile(45, "M", calendar
				.getTime(), "length", "in");
		System.out.println(percentile);
		
		//weight
		calendar.set(2007, Calendar.SEPTEMBER, 4);
		percentile = calculator.calculatePercentile(22, "M", calendar
				.getTime(), "weight", "lb");
		System.out.println(percentile);
		
		calendar.set(2006, Calendar.APRIL,6);
		percentile = calculator.calculatePercentile(29.2, "M", calendar
				.getTime(), "weight", "lb");
		System.out.println(percentile);
		
		calendar.set(2001, Calendar.MAY,5);
		percentile = calculator.calculatePercentile(47.4, "M", calendar
				.getTime(), "weight", "lb");
		System.out.println(percentile);
		
		//bmi
		calendar.set(2007, Calendar.SEPTEMBER, 4);
		double heightNum = 29;
		double weightNum = 22;
		double bmi = ( weightNum / 
				(heightNum * heightNum) ) * 703;
		percentile = calculator.calculatePercentile(bmi, "M", calendar
				.getTime(), "bmi", null);
		System.out.println(percentile);
		
		calendar.set(2006, Calendar.APRIL,6);
		heightNum = 34;
		weightNum = 29.2;
		bmi = ( weightNum / 
				(heightNum * heightNum) ) * 703;
		percentile = calculator.calculatePercentile(bmi, "M", calendar
				.getTime(), "bmi", null);
		System.out.println(percentile);
		
		calendar.set(2001, Calendar.MAY,5);
		heightNum = 45;
		weightNum = 47.4;
		bmi = ( weightNum / 
				(heightNum * heightNum) ) * 703;
		percentile = calculator.calculatePercentile(bmi, "M", calendar
				.getTime(), "bmi", null);
		System.out.println(percentile);
	}	
}
