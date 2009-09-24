package org.openmrs.module.chica.hibernateBeans;

import java.io.Serializable;
import java.util.Date;

/**
 * @author msheley
 * 
 */
public class ChicaHL7Export implements java.io.Serializable {

	private Integer queueId = null;
	private Integer encounterId = null;
	private Integer status = null;
	private Date dateProcessed = null;
	private Date dateInserted = null;
	private Boolean voided = null;
	private Date dateVoided = null;

	public ChicaHL7Export() {

	}

	public Integer getQueueId() {
		return queueId;
	}

	public void setQueueId(Integer queueId) {
		this.queueId = queueId;
	}

	public Boolean getVoided() {
		return voided;
	}

	public void setVoided(Boolean voided) {
		this.voided = voided;
	}

	public Date getDateVoided() {
		return dateVoided;
	}

	public void setDateVoided(Date dateVoided) {
		this.dateVoided = dateVoided;
	}
	
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Date getDateInserted() {
		return dateInserted;
	}
	
	public void setDateInserted(Date dateInserted) {
		this.dateInserted = dateInserted;
	}

	
	public Integer getEncounterId() {
		return encounterId;
	}

	
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}

	public Date getDateProcessed() {
		return dateProcessed;
	}

	public void setDateProcessed(Date dateProcessed) {
		this.dateProcessed = dateProcessed;
	}

		

}
