package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_location_attribute table
 * 
 * @author Tammy Dugan
 */
public class LocationAttribute implements java.io.Serializable {

	// Fields
	private Integer locationAttributeId = null;
	private String name = null;
	private String description = null;


	// Constructors

	/** default constructor */
	public LocationAttribute() {
	}
	

	public Integer getLocationAttributeId()
	{
		return this.locationAttributeId;
	}


	public void setLocationAttributeId(Integer locationAttributeId)
	{
		this.locationAttributeId = locationAttributeId;
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