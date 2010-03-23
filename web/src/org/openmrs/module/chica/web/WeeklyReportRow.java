/**
 * 
 */
package org.openmrs.module.chica.web;


/**
 * @author tmdugan
 *
 */
public class WeeklyReportRow
{
	String dateRange = null;
	Double data = 0D;
	
    /**
     * @return the data
     */
    public Double getData() {
    	return this.data;
    }


	
    /**
     * @param data the data to set
     */
    public void setData(Double data) {
    	this.data = data;
    }
    
    public void setData(java.math.BigInteger data){
    	this.data = data.doubleValue();
    }


	/**
     * @return the dateRange
     */
    public String getDateRange() {
    	return this.dateRange;
    }

	
    /**
     * @param dateRange the dateRange to set
     */
    public void setDateRange(String dateRange) {
    	this.dateRange = dateRange;
    }

	
}
