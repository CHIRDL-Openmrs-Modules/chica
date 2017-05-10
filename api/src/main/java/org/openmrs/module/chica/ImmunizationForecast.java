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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class ImmunizationForecast implements Serializable {

    private static final long serialVersionUID = 1L;

	private String vaccineName = null;
	private String vaccineCode = null;
	private Integer dose = null;
	private Integer patientId = null;
	private Date dateDue = null;
	private Date earliestDate = null;
	private Date pastDue = null;;
	
	public ImmunizationForecast(){
		
	}
	
	public ImmunizationForecast(ImmunizationForecast immunization){
		this.vaccineName = immunization.getVaccineName();
		this.vaccineCode = immunization.getVaccineCode();
		this.dose=immunization.getDose();
		this.patientId=immunization.getPatientId();
		this.dateDue = immunization.getDateDue();
		this.earliestDate = immunization.getEarliestDate();
		this.pastDue = immunization.getPastDue();
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
     * @return the dateDue
     */
    public Date getDateDue() {
    	return this.dateDue;
    }

    /**
     * @param dateString the dateString to set
     */
    public void setDateDue(String dateString) {
    	try {
    		this.dateDue = getDate(dateString);
        }
        catch (ParseException e) {
        }
    }

    /**
     * @return the earliestDate
     */
    public Date getEarliestDate() {
    	return this.earliestDate;
    }

    /**
     * @param dateString the dateString to set
     */
    public void setEarliestDate(String dateString) {
    	try {
    		this.earliestDate = getDate(dateString);
        }
        catch (ParseException e) {
        }
    }

	public Date getPastDue() {
		return pastDue;
	}

	public void setPastDue(Date pastDue) {
		this.pastDue = pastDue;
	}
}
