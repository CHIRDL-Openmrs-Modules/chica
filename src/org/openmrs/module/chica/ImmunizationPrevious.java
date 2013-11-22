/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class ImmunizationPrevious {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private String vaccineName = null;
	private String vaccineCode = null;
	private Integer dose = null;
	private Date date = null;
	private Integer patientId = null;
	
	public ImmunizationPrevious() {
		
	}
	
	public ImmunizationPrevious(ImmunizationPrevious immunizationPrevious) {
		this.vaccineName = immunizationPrevious.getVaccineName();
		this.vaccineCode = immunizationPrevious.getVaccineCode();
		this.dose = immunizationPrevious.getDose();
		this.patientId = immunizationPrevious.getPatientId();
		this.date = immunizationPrevious.getDate();
	}
	
    /**
     * @return the vaccineName
     */
    public String getVaccineName() {
    	return this.vaccineName;
    }

	
    /**
     * @param vaccineName the vaccineName to set
     */
    public void setVaccineName(String vaccineName) {
    	this.vaccineName = vaccineName;
    }

	
    /**
     * @return the vaccineCode
     */
    public String getVaccineCode() {
    	return this.vaccineCode;
    }

	
    /**
     * @param vaccineCode the vaccineCode to set
     */
    public void setVaccineCode(String vaccineCode) {
    	this.vaccineCode = vaccineCode;
    }

	/**
	 * @return the dose
	 */
	public Integer getDose() {
		return this.dose;
	}
	
	/**
	 * @param dose the dose to set
	 */
	public void setDose(Integer dose) {
		this.dose = dose;
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(String dateString) {
		try {
	        this.date = getDate(dateString);
        }
        catch (ParseException e) {
        }
	}
	
	/**
	 * @return the patientId
	 */
	public Integer getPatientId() {
		return this.patientId;
	}
	
	/**
	 * @param patientId the patientId to set
	 */
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
	public static Date getDate(String dateString) throws java.text.ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date localDate = formatter.parse(dateString);
		
		return localDate;
	}
	
}
