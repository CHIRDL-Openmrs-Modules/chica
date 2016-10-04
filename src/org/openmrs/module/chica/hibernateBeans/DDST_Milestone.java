package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the ddst_milestone table
 * 
 * @author Tammy Dugan
 */
public class DDST_Milestone implements java.io.Serializable
{	
	private Integer ddst_id;

	private String category;
	private String milestone;
	private Integer cutoff_age;
	private String reportable;
	

	
	public DDST_Milestone()
	{
		
	}



	public Integer getDdst_id() {
		return ddst_id;
	}



	public void setDdst_id(Integer ddst_id) {
		this.ddst_id = ddst_id;
	}



	public String getCategory() {
		return category;
	}



	public void setCategory(String category) {
		this.category = category;
	}



	public String getMilestone() {
		return milestone;
	}



	public void setMilestone(String milestone) {
		this.milestone = milestone;
	}



	public Integer getCutoff_age() {
		return cutoff_age;
	}



	public void setCutoff_age(Integer cutoff_age) {
		this.cutoff_age = cutoff_age;
	}



	public String getReportable() {
		return reportable;
	}



	public void setReportable(String reportable) {
		this.reportable = reportable;
	}
	
	
}