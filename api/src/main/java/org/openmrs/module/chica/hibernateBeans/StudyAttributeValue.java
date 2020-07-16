package org.openmrs.module.chica.hibernateBeans;

import org.openmrs.module.chirdlutilbackports.BaseChirdlMetadata;

/**
 * Holds information to store in the chica_study_attribute_value table
 * 
 * @author Tammy Dugan
 */
public class StudyAttributeValue extends BaseChirdlMetadata implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	private Integer studyAttributeValueId = null;
	private Integer studyId = null;
	private Integer studyAttributeId = null;
	private String value = null;
	private String name = null;
	private String description = null;

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

	@Override
	public Integer getId() {
		return getStudyAttributeValueId();
	}

	@Override
	public void setId(Integer id) {
		setStudyAttributeValueId(id);
		
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