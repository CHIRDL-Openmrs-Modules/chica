package org.openmrs.module.chica.hibernateBeans;

import java.util.Date;

/**
 * @author msheley
 * 
 */
public class ChicaHL7ExportMap implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer hl7ExportMapId = null;
	private Integer hl7ExportQueueId = null;
	private String value = null;
	private Date dateInserted = null;
	private Boolean voided = null;
	private Date dateVoided = null;
	

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

	

	public ChicaHL7ExportMap() {

	}

	public Integer getHl7ExportMapId() {
		return hl7ExportMapId;
	}

	public void setHl7ExportMapId(Integer hl7ExportMapId) {
		this.hl7ExportMapId = hl7ExportMapId;
	}

	public Integer getHl7ExportQueueId() {
		return hl7ExportQueueId;
	}

	public void setHl7ExportQueueId(Integer hl7ExportQueueId) {
		this.hl7ExportQueueId = hl7ExportQueueId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getDateInserted() {
		return dateInserted;
	}

	public void setDateInserted(Date dateInserted) {
		this.dateInserted = dateInserted;
	}

}
