package org.openmrs.module.chica.hibernateBeans;

import java.util.Date;

/**
 * Holds information to store in the chica_study_attribute_value table
 * 
 * @author Tammy Dugan
 */
public class PatientFamily implements java.io.Serializable {

	// Fields
	private Integer patientFamilyId=null;
	private Integer patientId=null;
	private Integer familyId=null;
	private Date creationTime=null;
	private String flag=null;
	private String streetAddress=null;
	private String phoneNum=null;

	// Constructors

	/** default constructor */
	public PatientFamily() {
	}

	public Integer getPatientFamilyId()
	{
		return this.patientFamilyId;
	}

	public void setPatientFamilyId(Integer patientFamilyId)
	{
		this.patientFamilyId = patientFamilyId;
	}

	public Integer getPatientId()
	{
		return this.patientId;
	}

	public void setPatientId(Integer patientId)
	{
		this.patientId = patientId;
	}

	public Integer getFamilyId()
	{
		return this.familyId;
	}

	public void setFamilyId(Integer familyId)
	{
		this.familyId = familyId;
	}

	public Date getCreationTime()
	{
		return this.creationTime;
	}

	public void setCreationTime(Date creationTime)
	{
		this.creationTime = creationTime;
	}

	public String getFlag()
	{
		return this.flag;
	}

	public void setFlag(String flag)
	{
		this.flag = flag;
	}

	public String getStreetAddress()
	{
		return this.streetAddress;
	}

	public void setStreetAddress(String streetAddress)
	{
		this.streetAddress = streetAddress;
	}

	public String getPhoneNum()
	{
		return this.phoneNum;
	}

	public void setPhoneNum(String phoneNum)
	{
		this.phoneNum = phoneNum;
	}
}