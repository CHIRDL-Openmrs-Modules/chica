package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica1_appointments table
 * 
 * @author Tammy Dugan
 */
public class Chica1Appointment implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Fields
	private Integer apptId = null;

	private String dateOfAppt = null;
	private Integer apptPsfId = null;
	private Integer apptPwsId = null;
	private Integer openmrsEncounterId = null;
	private String skipLoadReason = null;
	
	// Constructors
	/** default constructor */
	public Chica1Appointment() {
	}

	public Integer getApptId() {
		return this.apptId;
	}

	public void setApptId(Integer apptId) {
		this.apptId = apptId;
	}

	public String getDateOfAppt() {
		return this.dateOfAppt;
	}

	public void setDateOfAppt(String dateOfAppt) {
		this.dateOfAppt = dateOfAppt;
	}

	public Integer getApptPsfId() {
		return this.apptPsfId;
	}

	public void setApptPsfId(Integer apptPsfId) {
		this.apptPsfId = apptPsfId;
	}

	public Integer getApptPwsId() {
		return this.apptPwsId;
	}

	public void setApptPwsId(Integer apptPwsId) {
		this.apptPwsId = apptPwsId;
	}

	public Integer getOpenmrsEncounterId() {
		return this.openmrsEncounterId;
	}

	public void setOpenmrsEncounterId(Integer openmrsEncounterId) {
		this.openmrsEncounterId = openmrsEncounterId;
	}

	public String getSkipLoadReason()
	{
		return this.skipLoadReason;
	}

	public void setSkipLoadReason(String skipLoadReason)
	{
		this.skipLoadReason = skipLoadReason;
	}
	
}