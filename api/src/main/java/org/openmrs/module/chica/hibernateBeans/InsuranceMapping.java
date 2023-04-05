package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica_insurance_mapping table
 * 
 * @author Tammy Dugan
 */
public class InsuranceMapping implements java.io.Serializable
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

}