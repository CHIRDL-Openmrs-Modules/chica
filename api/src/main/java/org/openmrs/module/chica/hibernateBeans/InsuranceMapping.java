package org.openmrs.module.chica.hibernateBeans;

import org.openmrs.module.chirdlutilbackports.BaseChirdlMetadata;

/**
 * Holds information to store in the chica_insurance_mapping table
 * 
 * @author Tammy Dugan
 */
public class InsuranceMapping extends BaseChirdlMetadata implements java.io.Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer insuranceId;

	private String category;
	private String insName;
	private String carrierCode;
	private String planCode;
	private String insCode;
	private String sendingApplication;
	private String sendingFacility;
	private String name = null;
	private String description = null;

	
	public InsuranceMapping()
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
		
	public String getCategory()
	{
		return this.category;
	}
	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getInsCode()
	{
		return this.insCode;
	}

	public void setInsCode(String insCode)
	{
		this.insCode = insCode;
	}

	
    /**
     * @return the insName
     */
    public String getInsName() {
    	return insName;
    }

	
    /**
     * @param insName the insName to set
     */
    public void setInsName(String insName) {
    	this.insName = insName;
    }

	
    /**
     * @return the carrierCode
     */
    public String getCarrierCode() {
    	return carrierCode;
    }

	
    /**
     * @param carrierCode the carrierCode to set
     */
    public void setCarrierCode(String carrierCode) {
    	this.carrierCode = carrierCode;
    }

	
    /**
     * @return the planCode
     */
    public String getPlanCode() {
    	return planCode;
    }

	
    /**
     * @param planCode the planCode to set
     */
    public void setPlanCode(String planCode) {
    	this.planCode = planCode;
    }

	
    /**
     * @return the sendingApplication
     */
    public String getSendingApplication() {
    	return sendingApplication;
    }

	
    /**
     * @param sendingApplication the sendingApplication to set
     */
    public void setSendingApplication(String sendingApplication) {
    	this.sendingApplication = sendingApplication;
    }

	
    /**
     * @return the sendingFacility
     */
    public String getSendingFacility() {
    	return sendingFacility;
    }

	
    /**
     * @param sendingFacility the sendingFacility to set
     */
    public void setSendingFacility(String sendingFacility) {
    	this.sendingFacility = sendingFacility;
    }

	@Override
	public Integer getId() {
		return getInsuranceId();
	}

	@Override
	public void setId(Integer id) {
		setInsuranceId(id);
		
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