package org.openmrs.module.chica.hibernateBeans;

import java.util.Date;

/**
 * Holds information to store in the chica_hl7_export_status table
 * 
 * @author msheley
 * 
 */
public class ChicaHL7ExportStatus implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer hl7ExportStatusId = null;
	private String name = null;
	private String description = null;
	private Date dateCreated = null;
	
	
	public Integer getHl7ExportStatusId() {
		return hl7ExportStatusId;
	}
	public void setHl7ExportStatusId(Integer hl7ExportStatusId) {
		this.hl7ExportStatusId = hl7ExportStatusId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
}
