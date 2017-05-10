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
package org.openmrs.module.chica.hibernateBeans;

import java.io.Serializable;

import org.openmrs.Patient;


/**
 * Hibernate mapping to the chica_subject table.
 * 
 * @author Steve McKee
 */
public class StudySubject implements Serializable {
	
    private static final long serialVersionUID = 1L;
	private Integer subjectId;
	private Patient patient;
	private Study study;
    
    /**
     * @return the subjectId
     */
    public Integer getSubjectId() {
    	return subjectId;
    }
	
    /**
     * @param subjectId the subjectId to set
     */
    public void setSubjectId(Integer subjectId) {
    	this.subjectId = subjectId;
    }
	
    /**
     * @return the patient
     */
    public Patient getPatient() {
    	return patient;
    }
	
    /**
     * @param patient the patient to set
     */
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }
	
    /**
     * @return the study
     */
    public Study getStudy() {
    	return study;
    }
	
    /**
     * @param study the study to set
     */
    public void setStudy(Study study) {
    	this.study = study;
    }

	/**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer buffer = new StringBuffer("Subject:\n");
    	buffer.append("\tsubject_id: " + subjectId + "\n");
    	buffer.append("\tpatient_id: " + (patient == null ? "unknown" : patient.getPatientId()) + "\n");
    	buffer.append("\tstudy_id: " + (study == null ? "unknown" : study.getStudyId()) + "\n");
    	
    	return buffer.toString();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (subjectId == null ? 0 : subjectId.hashCode());
        hash = hash * 31 + (patient == null ? 0 : patient.hashCode());
        hash = hash * 46 + (study == null ? 0 : study.hashCode());
        
        return hash;
    }
}
