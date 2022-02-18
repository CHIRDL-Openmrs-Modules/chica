package org.openmrs.module.chica.hibernateBeans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DiscriminatorOptions;
import org.openmrs.module.chica.ChicaActivator;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chica.util.Util;

/**
 * Holds information to store in the encounter table
 * 
 * @author Tammy Dugan
 * @version 1.0
 */

@Entity(name = "org.openmrs.module.chica.hibernateBeans.Encounter")
@Table (name = "encounter")
@DiscriminatorValue(ChicaConstants.ENCOUNTER_TYPE_VALUE_CHICA) 
//@DiscriminatorValue("5") 
public class Encounter extends org.openmrs.Encounter implements
		java.io.Serializable 
{	

	@Column(name = "scheduled_datetime", nullable = true, length = 19)
	private Date scheduledTime = null;
	
	@Column(name = "insurance_sms_code", nullable = true, length = 255)
	private String insuranceSmsCode = null;
	
	@Column(name = "printer_location", nullable = true, length = 255)
	private String printerLocation = null;
	
	@Column(name = "insurance_carrier_code", nullable = true, length = 255)
	private String insuranceCarrierCode = null;
	
	@Column(name = "insurance_plan_code", nullable = true, length = 255)
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