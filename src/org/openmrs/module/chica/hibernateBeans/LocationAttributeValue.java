package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_location_attribute_value table
 * 
 * @author Tammy Dugan
 */
public class LocationAttributeValue implements java.io.Serializable {

	// Fields
	private Integer locationAttributeValueId = null;
	private Integer locationId = null;
	private Integer locationAttributeId = null;
	private String value = null;

	// Constructors

	/** default constructor */
	public LocationAttributeValue() {
	}

	
	public Integer getLocationAttributeValueId()
	{
		return this.locationAttributeValueId;
	}


	public void setLocationAttributeValueId(Integer locationAttributeValueId)
	{
		this.locationAttributeValueId = locationAttributeValueId;
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