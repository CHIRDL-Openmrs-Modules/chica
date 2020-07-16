package org.openmrs.module.chica.hibernateBeans;

import org.openmrs.module.chirdlutilbackports.BaseChirdlMetadata;

/**
 * Holds information to store in the chica_ddst table
 * 
 * @author Tammy Dugan
 */
public class DDST_Milestone extends BaseChirdlMetadata implements java.io.Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer ddst_id;

	private String category;
	private String milestone;
	private Integer cutoff_age;
	private String reportable;
	private String name = null;
	private String description = null;
	

	
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



	@Override
	public Integer getId() {
		return getDdst_id();
	}



	@Override
	public void setId(Integer id) {
		setDdst_id(id);
	}
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
}