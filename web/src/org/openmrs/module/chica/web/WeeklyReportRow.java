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
	Integer data = 0;
	
    /**
     * @return the data
     */
    public Integer getData() {
    	return this.data;
    }


	
    /**
     * @param data the data to set
     */
    public void setData(Integer data) {
    	this.data = data;
    }
    
    public void setData(java.math.BigInteger data){
    	this.data = data.intValue();
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
