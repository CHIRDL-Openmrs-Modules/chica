package org.openmrs.module.chica.xmlBeans.growthcharts;

/**
 * Contains plotting information for a particular chart concept for a growth chart.
 *
 * @author Steve McKee
 */
public class ChartConcept {

	private ConceptXAxis conceptXAxis;
	private ConceptYAxis conceptYAxis;
	
    /**
     * @return the conceptXAxis
     */
    public ConceptXAxis getConceptXAxis() {
    	return this.conceptXAxis;
    }
	
    /**
     * @param conceptXAxis the conceptXAxis to set
     */
    public void setConceptXAxis(ConceptXAxis conceptXAxis) {
    	this.conceptXAxis = conceptXAxis;
    }
	
    /**
     * @return the conceptYAxis
     */
    public ConceptYAxis getConceptYAxis() {
    	return this.conceptYAxis;
    }
	
    /**
     * @param conceptYAxis the conceptYAxis to set
     */
    public void setConceptYAxis(ConceptYAxis conceptYAxis) {
    	this.conceptYAxis = conceptYAxis;
    }
}
