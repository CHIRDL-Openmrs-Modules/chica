package org.openmrs.module.chica.test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.chica.Calculator;
import org.openmrs.test.BaseModuleContextSensitiveTest;

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
    @Before
    public void runBeforeEachTest() throws Exception {
        
         initializeInMemoryDatabase();
         executeDataSet(TestUtil.MD_LENGTH_AGE_FILE);
         authenticate();
    }

    @Test
    public void testCalculatePercentileForMeanAgeRange() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(5).minusMonths(3).minusDays(2);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Double percentile = calculator.calculatePercentile(new Double(110), date, "mdlength", Calendar.getInstance().getTime());
        System.out.println("Mean age range height percentile - "+percentile);
        Assert.assertNotNull(percentile);
    }  
    
    @Test
    public void testCalculatePercentileAbove90() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(5);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Double percentile = calculator.calculatePercentile(new Double(112), date, "mdlength", Calendar.getInstance().getTime());
        System.out.println("Above 90 percentile - "+percentile);
        Assert.assertEquals(percentile, new Double(99));
    } 
    
    @Test
    public void testCalculatePercentileBelow10() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(5);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Double percentile = calculator.calculatePercentile(new Double(95), date, "mdlength", Calendar.getInstance().getTime());
        System.out.println("Below 10 percentile - "+percentile);
        Assert.assertEquals(percentile, new Double(1));
    } 
    
    @Test
    public void testCalculatePercentileWhenLeftRowNull() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(2);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Double percentile = calculator.calculatePercentile(new Double(95), date, "mdlength", Calendar.getInstance().getTime());
        System.out.println("Percentile when left row is null - "+percentile);
        Assert.assertNotNull(percentile);
    } 
    
    @Test
    public void testCalculatePercentileWhenRightRowNull() throws Exception {
        Calculator calculator = new Calculator();
        LocalDate dob = LocalDate.now().minusYears(12);
        Date date = Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Double percentile = calculator.calculatePercentile(new Double(95), date, "mdlength", Calendar.getInstance().getTime());
        System.out.println("Percentile when right row is null - "+percentile);
        Assert.assertNotNull(percentile);
    }
 
}

