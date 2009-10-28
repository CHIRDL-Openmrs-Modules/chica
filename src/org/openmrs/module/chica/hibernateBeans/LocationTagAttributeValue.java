package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_location_tag_attribute_value table
 * 
 * @author Tammy Dugan
 */
public class LocationTagAttributeValue implements java.io.Serializable {

	// Fields
	private Integer locationTagAttributeValueId = null;
	private Integer locationTagId = null;
	private Integer locationId = null;
	private Integer locationTagAttributeId = null;
	private String value = null;

	// Constructors

	/** default constructor */
	public LocationTagAttributeValue() {
	}

	
	public Integer getLocationTagAttributeValueId()
	{
		return this.locationTagAttributeValueId;
	}


	public void setLocationTagAttributeValueId(Integer locationTagAttributeValueId)
	{
		this.locationTagAttributeValueId = locationTagAttributeValueId;
	}


	public Integer getLocationTagId()
	{
		return this.locationTagId;
	}


	public void setLocationTagId(Integer locationTagId)
	{
		this.locationTagId = locationTagId;
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


	public Integer getLocationId()
	{
		return this.locationId;
	}


	public void setLocationId(Integer locationId)
	{
		this.locationId = locationId;
	}

}