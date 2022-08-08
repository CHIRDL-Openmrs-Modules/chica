package org.openmrs.module.chica.xmlBeans.growthcharts;

import java.util.ArrayList;

/**
 * Contains all the chart concept configuration information for a growth chart.
 *
 * @author Steve McKee
 */
public class ChartConcepts {

	private ArrayList<ChartConcept> chartConcepts;

    /**
     * @return the chartConcepts
     */
    public ArrayList<ChartConcept> getChartConcepts() {
    	return this.chartConcepts;
    }

    /**
     * @param chartConcepts the chartConcepts to set
     */
    public void setChartConcepts(ArrayList<ChartConcept> chartConcepts) {
    	this.chartConcepts = chartConcepts;
    }
}
