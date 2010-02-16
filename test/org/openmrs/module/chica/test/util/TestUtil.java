/**
 * 
 */
package org.openmrs.module.chica.test.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.Calendar;

import org.openmrs.module.chica.util.Util;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author tmdugan
 *
 */
@SkipBaseSetup
public class TestUtil extends BaseModuleContextSensitiveTest
{
	/**
	 * 
	 */
	@Test
	public void testAdjustAgeUnits()
	{
		Calendar birthdate = Calendar.getInstance();
		Calendar cutoff = Calendar.getInstance();
		cutoff.set(2007, Calendar.OCTOBER, 26);

		// age > 2 years
		birthdate.set(2000, Calendar.DECEMBER, 1);
		String age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("6 yo", age);

		// age = 2 years
		birthdate.set(2005, Calendar.FEBRUARY, 1);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("2 yo", age);

		// age > 2 months and < 2 years
		birthdate.set(2006, Calendar.FEBRUARY, 1);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("20 mo", age);

		// age = 2 months
		birthdate.set(2007, Calendar.AUGUST, 1);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("2 mo", age);

		// age > 30 days and < than 2 months
		birthdate.set(2007, Calendar.SEPTEMBER, 1);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("7 wk", age);

		// age = 30 days
		birthdate.set(2007, Calendar.SEPTEMBER, 26);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("30 do", age);

		// age > 0 and < 30 days
		birthdate.set(2007, Calendar.SEPTEMBER, 30);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("26 do", age);

		// age == 0
		birthdate.set(2007, Calendar.OCTOBER, 26);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("0 do", age);

		// age < 0
		birthdate.set(2007, Calendar.OCTOBER, 27);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("0 do", age);

		// both null
		age = Util.adjustAgeUnits(null, null);
		assertEquals("0 do", age);

		// age only null
		age = Util.adjustAgeUnits(null, cutoff.getTime());
		assertEquals("0 do", age);
		
		//test case testing previous error
		birthdate.set(2007, Calendar.SEPTEMBER, 4);
		cutoff.set(2008, Calendar.MAY, 21);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("8 mo", age);
		
		//test when birthday is in the current month but hasn't happened yet
		birthdate.set(2006, Calendar.OCTOBER, 26);
		cutoff = Calendar.getInstance();
		cutoff.set(2007, Calendar.OCTOBER, 2);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("11 mo", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 27);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 10);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("6 wk", age);
		
		birthdate.set(2009, Calendar.NOVEMBER, 2);
		cutoff = Calendar.getInstance();
		cutoff.set(2009, Calendar.DECEMBER, 23);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("7 wk", age);
		
		birthdate.set(2009, Calendar.NOVEMBER, 10);
		cutoff = Calendar.getInstance();
		cutoff.set(2009, Calendar.DECEMBER, 29);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("7 wk", age);
		
		birthdate.set(2009, Calendar.NOVEMBER, 11);
		cutoff = Calendar.getInstance();
		cutoff.set(2009, Calendar.DECEMBER, 23);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("6 wk", age);
		
		birthdate.set(2009, Calendar.NOVEMBER, 13);
		cutoff = Calendar.getInstance();
		cutoff.set(2009, Calendar.DECEMBER, 30);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("6 wk", age);
		
		birthdate.set(2009, Calendar.NOVEMBER, 22);
		cutoff = Calendar.getInstance();
		cutoff.set(2009, Calendar.DECEMBER, 23);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("4 wk", age);
		
		birthdate.set(2009, Calendar.NOVEMBER, 23);
		cutoff = Calendar.getInstance();
		cutoff.set(2009, Calendar.DECEMBER, 28);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("5 wk", age);
		
		
		
		birthdate.set(2009, Calendar.NOVEMBER, 26);
		cutoff = Calendar.getInstance();
		cutoff.set(2009, Calendar.DECEMBER, 30);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("4 wk", age);
		
		birthdate.set(2009, Calendar.NOVEMBER, 27);
		cutoff = Calendar.getInstance();
		cutoff.set(2009, Calendar.DECEMBER, 30);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("4 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 27);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.JANUARY, 28);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("4 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 31);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 2);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("4 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 28);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 2);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("5 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 29);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 3);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("5 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 30);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 5);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("5 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 31);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 5);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("5 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 27);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 9);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("6 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 27);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 10);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("6 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 29);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 12);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("6 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 30);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 11);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("6 wk", age);
		
		birthdate.set(2009, Calendar.DECEMBER, 31);
		cutoff = Calendar.getInstance();
		cutoff.set(2010, Calendar.FEBRUARY, 12);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("6 wk", age);
		
		birthdate.set(2009, Calendar.NOVEMBER, 25);
		cutoff = Calendar.getInstance();
		cutoff.set(2009, Calendar.DECEMBER, 21);
		age = Util.adjustAgeUnits(birthdate.getTime(), cutoff.getTime());
		assertEquals("26 do", age);
	}
}
