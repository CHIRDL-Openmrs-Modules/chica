package org.openmrs.module.chica.xmlBeans.growthcharts;

/**
 * Contains the configuration for a growth chart.
 *
 * @author Steve McKee
 */
public class GrowthChart {

	private String fileLocation;
	private String chartType;
	private int ageInMonthsMin;
	private int ageInMonthsMax;
	private String gender;
	private ChartConcepts chartConcepts;
	
    /**
     * @return the fileLocation
     */
    public String getFileLocation() {
    	return fileLocation;
    }
	
    /**
     * @param fileLocation the fileLocation to set
     */
    public void setFileLocation(String fileLocation) {
    	this.fileLocation = fileLocation;
    }
	
    /**
     * @return the chartType
     */
    public String getChartType() {
    	return chartType;
    }
	
    /**
     * @param chartType the chartType to set
     */
    public void setChartType(String chartType) {
    	this.chartType = chartType;
    }
	
    /**
     * @return the ageInMonthsMin
     */
    public int getAgeInMonthsMin() {
    	return ageInMonthsMin;
    }
	
    /**
     * @param ageInMonthsMin the ageInMonthsMin to set
     */
    public void setAgeInMonthsMin(int ageInMonthsMin) {
    	this.ageInMonthsMin = ageInMonthsMin;
    }
	
    /**
     * @return the ageInMonthsMax
     */
    public int getAgeInMonthsMax() {
    	return ageInMonthsMax;
    }
	
    /**
     * @param ageInMonthsMax the ageInMonthsMax to set
     */
    public void setAgeInMonthsMax(int ageInMonthsMax) {
    	this.ageInMonthsMax = ageInMonthsMax;
    }
	
    /**
     * @return the gender
     */
    public String getGender() {
    	return gender;
    }
	
    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
    	this.gender = gender;
    }
	
    /**
     * @return the chartConcepts
     */
    public ChartConcepts getChartConcepts() {
    	return chartConcepts;
    }
	
    /**
     * @param chartConcepts the chartConcepts to set
     */
    public void setChartConcepts(ChartConcepts chartConcepts) {
    	this.chartConcepts = chartConcepts;
    }
}
