package org.openmrs.module.chica.hibernateBeans;

import java.util.Date;

/**
 * Holds information to store in the chica_study table
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
public class Study implements java.io.Serializable {

	// Fields
	private Integer studyId = null;
	private Date startDate = null;
	private Date endDate = null;
	private String title = null;
	private String investigators = null;
	private Integer studyConceptId = null;
	private Boolean status = false;
	private String purpose = null;

	// Constructors

	/** default constructor */
	public Study() {
	}

	/**
	 * @return the studyId
	 */
	public Integer getStudyId()
	{
		return this.studyId;
	}

	/**
	 * @param studyId the studyId to set
	 */
	public void setStudyId(Integer studyId)
	{
		this.studyId = studyId;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate()
	{
		return this.startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate()
	{
		return this.endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the investigators
	 */
	public String getInvestigators()
	{
		return this.investigators;
	}

	/**
	 * @param investigators the investigators to set
	 */
	public void setInvestigators(String investigators)
	{
		this.investigators = investigators;
	}

	/**
	 * @return the studyConceptId
	 */
	public Integer getStudyConceptId()
	{
		return this.studyConceptId;
	}

	/**
	 * @param studyConceptId the studyConceptId to set
	 */
	public void setStudyConceptId(Integer studyConceptId)
	{
		this.studyConceptId = studyConceptId;
	}

	/**
	 * @return the status
	 */
	public Boolean getStatus()
	{
		return this.status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Boolean status)
	{
		this.status = status;
	}

	/**
	 * @return the purpose
	 */
	public String getPurpose()
	{
		return this.purpose;
	}

	/**
	 * @param purpose the purpose to set
	 */
	public void setPurpose(String purpose)
	{
		this.purpose = purpose;
	}
}