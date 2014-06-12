package org.openmrs.module.chica.xmlBeans.growthcharts;

import java.util.ArrayList;


/**
 * Contains an ArrayList of growth chart configurations.
 * 
 * @author Steve McKee
 */
public class GrowthCharts {

	private ArrayList<GrowthChart> growthCharts;
	
	/**
	 * Sets the growth chart configurations.
	 * 
	 * @param growthCharts ArrayList of GrowthChart objects.
	 */
	public void setGrowthCharts(ArrayList<GrowthChart>growthCharts) {
		this.growthCharts = growthCharts;
	}
	
	/**
	 * @return ArrayList of GrowthChart objects.
	 */
	public ArrayList<GrowthChart> getGrowthCharts() {
		return growthCharts;
	}
}
