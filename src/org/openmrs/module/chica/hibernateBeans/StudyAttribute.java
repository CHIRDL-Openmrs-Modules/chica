package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_study_attribute table
 * 
 * @author Tammy Dugan
 */
public class StudyAttribute implements java.io.Serializable {

	// Fields
	private Integer studyAttributeId = null;
	private String name = null;
	private String description = null;


	// Constructors

	/** default constructor */
	public StudyAttribute() {
	}


	/**
	 * @return the studyAttributeId
	 */
	public Integer getStudyAttributeId()
	{
		return this.studyAttributeId;
	}


	/**
	 * @param studyAttributeId the studyAttributeId to set
	 */
	public void setStudyAttributeId(Integer studyAttributeId)
	{
		this.studyAttributeId = studyAttributeId;
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