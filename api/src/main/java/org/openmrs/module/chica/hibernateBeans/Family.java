package org.openmrs.module.chica.hibernateBeans;

import java.util.Date;

/**
 * Holds information to store in the chica_family table
 * 
 * @author Tammy Dugan
 */
public class Family implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	private Integer familyId = null;
	private String streetAddress = null;
	private String city=null;
	private String state=null;
	private String phoneNum=null;
	private Date creationTime =null;
	private Integer numKids=null;

	// Constructors

	/** default constructor */
	public Family() {
	}

	public Integer getFamilyId()
	{
		return this.familyId;
	}

	public void setFamilyId(Integer familyId)
	{
		this.familyId = familyId;
	}

	public String getStreetAddress()
	{
		return this.streetAddress;
	}

	public void setStreetAddress(String streetAddress)
	{
		this.streetAddress = streetAddress;
	}

	public String getCity()
	{
		return this.city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return this.state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getPhoneNum()
	{
		return this.phoneNum;
	}

	public void setPhoneNum(String phoneNum)
	{
		this.phoneNum = phoneNum;
	}

	public Date getCreationTime()
	{
		return this.creationTime;
	}

	public void setCreationTime(Date creationTime)
	{
		this.creationTime = creationTime;
	}

	public Integer getNumKids()
	{
		return this.numKids;
	}

	public void setNumKids(Integer numKids)
	{
		this.numKids = numKids;
	}

}