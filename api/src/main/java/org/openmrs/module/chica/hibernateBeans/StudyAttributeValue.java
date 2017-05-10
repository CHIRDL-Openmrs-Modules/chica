package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_study_attribute_value table
 * 
 * @author Tammy Dugan
 */
public class StudyAttributeValue implements java.io.Serializable {

	// Fields
	private Integer studyAttributeValueId = null;
	private Integer studyId = null;
	private Integer studyAttributeId = null;
	private String value = null;

	// Constructors

	/** default constructor */
	public StudyAttributeValue() {
	}

	/**
	 * @return the studyAttributeValueId
	 */
	public Integer getStudyAttributeValueId()
	{
		return this.studyAttributeValueId;
	}

	/**
	 * @param studyAttributeValueId the studyAttributeValueId to set
	 */
	public void setStudyAttributeValueId(Integer studyAttributeValueId)
	{
		this.studyAttributeValueId = studyAttributeValueId;
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
	 * @return the value
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
}