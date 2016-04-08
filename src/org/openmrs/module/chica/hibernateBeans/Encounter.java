package org.openmrs.module.chica.hibernateBeans;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.FormField;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlutilbackportsEncounterAttributeValue;

/**
 * Holds information to store in the encounter table
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
public class Encounter extends org.openmrs.Encounter implements
		java.io.Serializable
{	
	private Date scheduledTime = null;
	private String insuranceSmsCode = null;
	private String printerLocation = null;
	private String insuranceCarrierCode = null;
	private String insurancePlanCode = null;
	
	/**
	 * 
	 */
	public Encounter()
	{
		super();
	}

	/**
	 * @param encounterId
	 */
	public Encounter(Integer encounterId)
	{
		super(encounterId);
	}

	/**
	 * @return
	 */
	public Date getScheduledTime()
	{
		return this.scheduledTime;
	}

	/**
	 * @param scheduledTime
	 */
	public void setScheduledTime(Date scheduledTime)
	{
		this.scheduledTime = scheduledTime;
	}

	/**
	 * @return the printerLocation
	 */
	public String getPrinterLocation()
	{
		return this.printerLocation;
	}

	/**
	 * @param printerLocation the printerLocation to set
	 */
	public void setPrinterLocation(String printerLocation)
	{
		this.printerLocation = printerLocation;
	}

	public String getInsuranceSmsCode()
	{
		return this.insuranceSmsCode;
	}

	public void setInsuranceSmsCode(String insuranceSmsCode)
	{
		this.insuranceSmsCode = insuranceSmsCode;
	}

	public String getInsuranceCarrierCode()
	{
		return this.insuranceCarrierCode;
	}

	public void setInsuranceCarrierCode(String insuranceCarrierCode)
	{
		this.insuranceCarrierCode = insuranceCarrierCode;
	}

	public String getInsurancePlanCode()
	{
		return this.insurancePlanCode;
	}

	public void setInsurancePlanCode(String insurancePlanCode)
	{
		this.insurancePlanCode = insurancePlanCode;
	}
}