package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_location_tag_attribute table
 * 
 * @author Tammy Dugan
 */
public class LocationTagAttribute implements java.io.Serializable {

	// Fields
	private Integer locationTagAttributeId = null;
	private String name = null;
	private String description = null;


	// Constructors

	/** default constructor */
	public LocationTagAttribute() {
	}
	

	public Integer getLocationTagAttributeId()
	{
		return this.locationTagAttributeId;
	}


	public void setLocationTagAttributeId(Integer locationTagAttributeId)
	{
		this.locationTagAttributeId = locationTagAttributeId;
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