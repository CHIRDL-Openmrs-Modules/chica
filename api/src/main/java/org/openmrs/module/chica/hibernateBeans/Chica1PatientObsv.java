package org.openmrs.module.chica.hibernateBeans;

/**
 * Holds information to store in the chica1_patient_obsv table
 * 
 * @author Tammy Dugan
 */
public class Chica1PatientObsv implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Fields
	private Integer idNum = null;

	private Integer patientId = null;
	private String dateStamp = null;
	private String obsvVal = null;
	private String obsvId = null;
	private String obsvSource = null;
	private Integer openmrsObsId = null;
	private String skipLoadReason = null;
	
	// Constructors

	/** default constructor */
	public Chica1PatientObsv() {
	}

	public Integer getIdNum() {
		return this.idNum;
	}

	public void setIdNum(Integer idNum) {
		this.idNum = idNum;
	}

	public Integer getPatientId() {
		return this.patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getDateStamp() {
		return this.dateStamp;
	}

	public void setDateStamp(String dateStamp) {
		this.dateStamp = dateStamp;
	}

	public String getObsvVal() {
		return this.obsvVal;
	}

	public void setObsvVal(String obsvVal) {
		this.obsvVal = obsvVal;
	}

	public String getObsvSource() {
		return this.obsvSource;
	}

	public void setObsvSource(String obsvSource) {
		this.obsvSource = obsvSource;
	}

	public Integer getOpenmrsObsId() {
		return this.openmrsObsId;
	}

	public void setOpenmrsObsId(Integer openmrsObsId) {
		this.openmrsObsId = openmrsObsId;
	}

	public String getObsvId() {
		return this.obsvId;
	}

	public void setObsvId(String obsvId) {
		this.obsvId = obsvId;
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