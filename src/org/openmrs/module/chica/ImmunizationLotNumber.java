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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 */
public class ImmunizationLotNumber {
	private Log log = LogFactory.getLog(this.getClass());

	private Integer remainingDoses = null;
	private String lotNumber = null;
	private String  source = null;
	private String vaccineName = null;
	private String vaccineASIISCode = null;
	private Date expirationDate = null;
	private Integer locationId = null;
	
	
	public ImmunizationLotNumber(){
		
	}

	public Integer getRemainingDoses() {
		return remainingDoses;
	}

	public void setRemainingDoses(Integer remainingDoses) {
		this.remainingDoses = remainingDoses;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getVaccineName() {
		return vaccineName;
	}

	public void setVaccineName(String vaccineName) {
		this.vaccineName = vaccineName;
	}

	public String getVaccineASIISCode() {
		return vaccineASIISCode;
	}

	public void setVaccineASIISCode(String vaccineASIISCode) {
		this.vaccineASIISCode = vaccineASIISCode;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	
	
    
}
