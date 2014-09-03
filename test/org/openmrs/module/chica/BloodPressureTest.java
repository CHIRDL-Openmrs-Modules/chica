package org.openmrs.module.chica;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class BloodPressureTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void testBloodPressure() throws Exception {
		executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, Calendar.JULY, 17);
		Date birthdate = calendar.getTime();
		String gender = "M";
		String bloodPressureType = "diastolic";
		double observedBloodPressure = 59;
		double height = 36;
		org.openmrs.module.chica.Calculator calculator = new org.openmrs.module.chica.Calculator();
		
		double bloodPressurePercentile = calculator.computeBloodPressurePercentile(birthdate, gender, height,
		    observedBloodPressure, bloodPressureType,org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_IN);
		
		System.out.println("Birthdate: " + birthdate);
		System.out.println("Gender: " + gender);
		System.out.println("Height: " + height + " inches");
		System.out.println("BP type: " + bloodPressureType);
		System.out.println("BP: " + observedBloodPressure);
		System.out.println("Blood Pressure percentile is: " + Double.valueOf(bloodPressurePercentile));
		
	}
}
