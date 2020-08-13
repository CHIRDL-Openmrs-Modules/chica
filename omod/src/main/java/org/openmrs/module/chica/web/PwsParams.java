package org.openmrs.module.chica.web;

import java.io.Serializable;

import org.openmrs.module.chirdlutil.util.Util;

/**
 * Contains the parameters needed for PWS processing.
 * 
 * @author Steve McKee
 */
public class PwsParams implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String mrn;
	private String providerId;
	private String formName;
	private String formPage;
	private String startState;
	private String endState;
	private String formInstance;
	private Integer patientId;
	private Integer sessionId;
	private Integer encounterId;
	
	/**
	 * @return the mrn
	 */
	public String getMrn() {
		return this.mrn;
	}
	
	/**
	 * @param mrn the mrn to set
	 */
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}
	
	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return this.providerId;
	}
	
	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	/**
	 * @return the formName
	 */
	public String getFormName() {
		return this.formName;
	}
	
	/**
	 * @param formName the formName to set
	 */
	public void setFormName(String formName) {
		this.formName = formName;
	}
	
	/**
	 * @return the formPage
	 */
	public String getFormPage() {
		return this.formPage;
	}
	
	/**
	 * @param formPage the formPage to set
	 */
	public void setFormPage(String formPage) {
		this.formPage = formPage;
	}
	
	/**
	 * @return the startState
	 */
	public String getStartState() {
		return this.startState;
	}
	
	/**
	 * @param startState the startState to set
	 */
	public void setStartState(String startState) {
		this.startState = startState;
	}
	
	/**
	 * @return the endState
	 */
	public String getEndState() {
		return this.endState;
	}
	
	/**
	 * @param endState the endState to set
	 */
	public void setEndState(String endState) {
		this.endState = endState;
	}
	
	/**
	 * @return the formInstance
	 */
	public String getFormInstance() {
		return this.formInstance;
	}
	
	/**
	 * @param formInstance the formInstance to set
	 */
	public void setFormInstance(String formInstance) {
		this.formInstance = formInstance;
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
	
	/**
	 * @return the sessionId
	 */
	public Integer getSessionId() {
		return this.sessionId;
	}
	
	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}
	
	/**
	 * @return the encounterId
	 */
	public Integer getEncounterId() {
		return this.encounterId;
	}
	
	/**
	 * @param encounterId the encounterId to set
	 */
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.encounterId == null) ? 0 : this.encounterId.hashCode());
		result = prime * result + ((this.endState == null) ? 0 : this.endState.hashCode());
		result = prime * result + ((this.formInstance == null) ? 0 : this.formInstance.hashCode());
		result = prime * result + ((this.formName == null) ? 0 : this.formName.hashCode());
		result = prime * result + ((this.formPage == null) ? 0 : this.formPage.hashCode());
		result = prime * result + ((this.mrn == null) ? 0 : this.mrn.hashCode());
		result = prime * result + ((this.patientId == null) ? 0 : this.patientId.hashCode());
		result = prime * result + ((this.providerId == null) ? 0 : this.providerId.hashCode());
		result = prime * result + ((this.sessionId == null) ? 0 : this.sessionId.hashCode());
		result = prime * result + ((this.startState == null) ? 0 : this.startState.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
        
        if (!super.equals(obj)) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
		PwsParams other = (PwsParams) obj;
		if (!Util.compareObjectEquality(this.encounterId, other.encounterId)) {
            return false;
        }
		
		if (!Util.compareObjectEquality(this.endState, other.endState)) {
            return false;
        }
		
		if (!Util.compareObjectEquality(this.formInstance, other.formInstance)) {
            return false;
        }
		
		if (!Util.compareObjectEquality(this.formName, other.formName)) {
            return false;
        }
		
		if (!Util.compareObjectEquality(this.formPage, other.formPage)) {
            return false;
        }
		
		if (!Util.compareObjectEquality(this.mrn, other.mrn)) {
            return false;
        }
		
		if (!Util.compareObjectEquality(this.patientId, other.patientId)) {
            return false;
        }
		
		if (!Util.compareObjectEquality(this.providerId, other.providerId)) {
            return false;
        }
		
		if (!Util.compareObjectEquality(this.sessionId, other.sessionId)) {
            return false;
        }
		
		return Util.compareObjectEquality(this.startState, other.startState);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PwsParams [mrn=" + this.mrn + ", providerId=" + this.providerId + ", formName=" + this.formName 
				+ ", formPage=" + this.formPage + ", startState=" + this.startState + ", endState=" + this.endState 
				+ ", formInstance=" + this.formInstance + ", patientId=" + this.patientId + ", sessionId=" 
				+ this.sessionId + ", encounterId=" + this.encounterId + "]";
	}
}
