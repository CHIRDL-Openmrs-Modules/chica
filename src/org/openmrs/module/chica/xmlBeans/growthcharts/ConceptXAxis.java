package org.openmrs.module.chica.xmlBeans.growthcharts;


public class ConceptXAxis {
	
	private String name;
	private Float minPosition;
	private Float maxPosition;
	private Float minVal;
	private Float maxVal;
	
    /**
     * @return the name
     */
    public String getName() {
    	return name;
    }
	
    /**
     * @param name the name to set
     */
    public void setName(String name) {
    	this.name = name;
    }
	
    /**
     * @return the minPosition
     */
    public Float getMinPosition() {
    	return minPosition;
    }
	
    /**
     * @param minPosition the minPosition to set
     */
    public void setMinPosition(Float minPosition) {
    	this.minPosition = minPosition;
    }
	
    /**
     * @return the maxPosition
     */
    public Float getMaxPosition() {
    	return maxPosition;
    }
	
    /**
     * @param maxPosition the maxPosition to set
     */
    public void setMaxPosition(Float maxPosition) {
    	this.maxPosition = maxPosition;
    }
	
    /**
     * @return the minVal
     */
    public Float getMinVal() {
    	return minVal;
    }
	
    /**
     * @param minVal the minVal to set
     */
    public void setMinVal(Float minVal) {
    	this.minVal = minVal;
    }
	
    /**
     * @return the maxVal
     */
    public Float getMaxVal() {
    	return maxVal;
    }
	
    /**
     * @param maxVal the maxVal to set
     */
    public void setMaxVal(Float maxVal) {
    	this.maxVal = maxVal;
    }
}
