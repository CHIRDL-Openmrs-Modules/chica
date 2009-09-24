package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_insurance_category table
 * 
 * @author Tammy Dugan
 */
public class InsuranceCategory implements java.io.Serializable
{	
	private Integer insuranceId;

	private String smsCode;
	private String category;
	private String smsInsName;
	private String mckessonInsName;
	private String starCarrierCode;
	private String starPlanCode;
	private String insCode;

	
	public InsuranceCategory()
	{
		
	}
	
	public Integer getInsuranceId()
	{
		return this.insuranceId;
	}
	public void setInsuranceId(Integer insuranceId)
	{
		this.insuranceId = insuranceId;
	}
	public String getSmsCode()
	{
		return this.smsCode;
	}
	public void setSmsCode(String smsCode)
	{
		this.smsCode = smsCode;
	}
	
	public String getCategory()
	{
		return this.category;
	}
	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getSmsInsName()
	{
		return this.smsInsName;
	}

	public void setSmsInsName(String smsInsName)
	{
		this.smsInsName = smsInsName;
	}

	public String getMckessonInsName()
	{
		return this.mckessonInsName;
	}

	public void setMckessonInsName(String mckessonInsName)
	{
		this.mckessonInsName = mckessonInsName;
	}

	public String getStarCarrierCode()
	{
		return this.starCarrierCode;
	}

	public void setStarCarrierCode(String starCarrierCode)
	{
		this.starCarrierCode = starCarrierCode;
	}

	public String getStarPlanCode()
	{
		return this.starPlanCode;
	}

	public void setStarPlanCode(String starPlanCode)
	{
		this.starPlanCode = starPlanCode;
	}

	public String getInsCode()
	{
		return this.insCode;
	}

	public void setInsCode(String insCode)
	{
		this.insCode = insCode;
	}
	
}