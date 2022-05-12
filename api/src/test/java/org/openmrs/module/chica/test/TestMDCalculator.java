package org.openmrs.module.chica.test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.chica.Calculator;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * @author Seema Sarala
 * 
 */
public class TestMDCalculator extends BaseModuleContextSensitiveTest
{

    /**
     * Set up the database with the initial dataset before every test method in
     * this class.
     * 
     * Require authorization before every test method in this class
     * 
     */
    @BeforeEach
    public void runBeforeEachTest() throws Exception {
        
         initializeInMemoryDatabase();
         executeDataSet(TestUtil.MD_LENGTH_AGE_FILE);
         executeDataSet(TestUtil.MD_WEIGHT_AGE_FILE);
         executeDataSet(TestUtil.MD_BMI_AGE_FILE);
         authenticate();
    }

    @Test
    public void testCalculatePercentileForMeanAgeRange() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(5).minusMonths(3).minusDays(2);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Double percentile = calculator.calculatePercentile(new Double(110), date, "mdlength", Calendar.getInstance().getTime());
        Assertions.assertNotNull(percentile);
        
        percentile = calculator.calculatePercentile(new Double(17), date, "mdweight", Calendar.getInstance().getTime());
        Assertions.assertNotNull(percentile);
        
        percentile = calculator.calculatePercentile(new Double(15), date, "mdbmi", Calendar.getInstance().getTime());
        Assertions.assertNotNull(percentile);
    }  
    
    @Test
    public void testCalculatePercentileAbove90() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(5);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Double percentile = calculator.calculatePercentile(new Double(112), date, "mdlength", Calendar.getInstance().getTime());
        Assertions.assertEquals(percentile, new Double(99));
        
        percentile = calculator.calculatePercentile(new Double(25), date, "mdweight", Calendar.getInstance().getTime());
        Assertions.assertEquals(percentile, new Double(99));
        
        percentile = calculator.calculatePercentile(new Double(22), date, "mdbmi", Calendar.getInstance().getTime());
        Assertions.assertEquals(percentile, new Double(99));
    } 
    
    @Test
    public void testCalculatePercentileBelow10() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(5);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Double percentile = calculator.calculatePercentile(new Double(13), date, "mdlength", Calendar.getInstance().getTime());
        Assertions.assertEquals(percentile, new Double(1));
        
        percentile = calculator.calculatePercentile(new Double(13), date, "mdweight", Calendar.getInstance().getTime());
        Assertions.assertEquals(percentile, new Double(1));
        
        percentile = calculator.calculatePercentile(new Double(13), date, "mdbmi", Calendar.getInstance().getTime());
        Assertions.assertEquals(percentile, new Double(1));
    } 
    
    @Test
    public void testCalculatePercentileWhenLeftRowNull() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(2);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Double percentile = calculator.calculatePercentile(new Double(95), date, "mdlength", Calendar.getInstance().getTime());
        Assertions.assertNotNull(percentile);
        
        percentile = calculator.calculatePercentile(new Double(95), date, "mdweight", Calendar.getInstance().getTime());
        Assertions.assertNotNull(percentile);
        
        percentile = calculator.calculatePercentile(new Double(95), date, "mdbmi", Calendar.getInstance().getTime());
        Assertions.assertNotNull(percentile);
    } 
    
    @Test
    public void testCalculatePercentileWhenRightRowNull() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(12);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Double percentile = calculator.calculatePercentile(new Double(95), date, "mdlength", Calendar.getInstance().getTime());
        Assertions.assertNotNull(percentile);
        
        percentile = calculator.calculatePercentile(new Double(95), date, "mdweight", Calendar.getInstance().getTime());
        Assertions.assertNotNull(percentile);
        
        percentile = calculator.calculatePercentile(new Double(95), date, "mdbmi", Calendar.getInstance().getTime());
        Assertions.assertNotNull(percentile);
    }
    
    @Test
    public void testIsBMITwoBelowSD() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(10);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        boolean bmiAboveSD = calculator.isBMITwoBelowSD(new Double(5), date, "mdbmi2belowsd", Calendar.getInstance().getTime());
        Assert.assertTrue(bmiAboveSD);
        
        boolean bmiBelowSD = calculator.isBMITwoBelowSD(new Double(20), date, "mdbmi2belowsd", Calendar.getInstance().getTime());
        Assert.assertFalse(bmiBelowSD);
    }
    
    @Test
    public void testIsBMITwoBelowSDRowNull() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dobLeftNull = LocalDate.now().minusYears(2);
        Date dateLeftNull = Date.from(dobLeftNull.atStartOfDay(ZoneId.systemDefault()).toInstant());
        boolean bmiAboveSDLeftRowNull = calculator.isBMITwoBelowSD(new Double(5), dateLeftNull, "mdbmi2belowsd", Calendar.getInstance().getTime());
        Assert.assertTrue(bmiAboveSDLeftRowNull);
        
        LocalDate dobRightNull = LocalDate.now().minusYears(12);
        Date dateRightNull = Date.from(dobRightNull.atStartOfDay(ZoneId.systemDefault()).toInstant());
        boolean bmiAboveSDRightRowNull = calculator.isBMITwoBelowSD(new Double(5), dateRightNull, "mdbmi2belowsd", Calendar.getInstance().getTime());
        Assert.assertTrue(bmiAboveSDRightRowNull);
    } 
 
}

