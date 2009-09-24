/**
 * 
 */
package org.openmrs.module.chica.web;

/**
 * @author tmdugan
 *
 */
public class PatientRow
{
	private String lastName = null;
	private String firstName = null;
	private String mrn = null;
	private String dob = null;
	private String sex = null;
	private String mdName = null;
	private String appointment = null;
	private String checkin = null;
	private String status = null;
	private String statusColor = null;
	private Integer patientId = null;
	private Integer sessionId = null;
	private String rowColor = null;
	private Integer psfId	= null;
	private Integer pwsId = null;
	private Integer jitID = null;
	private String ageAtVisit = null;
	private String station = null;
	private String weightPercentile = null;
	private String heightPercentile = null;
	private Integer encounterId = null;
	/**
	 * @return the encounterId
	 */
	public Integer getEncounterId() {
		return encounterId;
	}
	/**
	 * @param encounterId the encounterId to set
	 */
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}
	/**
	 * @return the heightPercentile
	 */
	public String getHeightPercentile() {
		return heightPercentile;
	}
	/**
	 * @param heightPercentile the heightPercentile to set
	 */
	public void setHeightPercentile(String heightPercentile) {
		this.heightPercentile = heightPercentile;
	}
	/**
	 * @return the weightPercentile
	 */
	public String getWeightPercentile() {
		return weightPercentile;
	}
	/**
	 * @param weightPercentile the weightPercentile to set
	 */
	public void setWeightPercentile(String weightPercentile) {
		this.weightPercentile = weightPercentile;
	}
	/**
	 * @return the station
	 */
	public String getStation() {
		return station;
	}
	/**
	 * @param station the station to set
	 */
	public void setStation(String station) {
		this.station = station;
	}
	/**
	 * @return the ageAtVisit
	 */
	public String getAgeAtVisit() {
		return ageAtVisit;
	}
	/**
	 * @param ageAtVisit the ageAtVisit to set
	 */
	public void setAgeAtVisit(String ageAtVisit) {
		this.ageAtVisit = ageAtVisit;
	}
	/**
	 * @return the jitID
	 */
	public Integer getJitID() {
		return jitID;
	}
	/**
	 * @param jitID the jitID to set
	 */
	public void setJitID(Integer jitID) {
		this.jitID = jitID;
	}

	private boolean reprintStatus = false;
	
	
	/**
	 * @return the reprintStatus
	 */
	public boolean isReprintStatus() {
		return reprintStatus;
	}
	/**
	 * @param reprintStatus the reprintStatus to set
	 */
	public void setReprintStatus(boolean reprintStatus) {
		this.reprintStatus = reprintStatus;
	}
	public String getLastName()
	{
		return this.lastName;
	}
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	public String getFirstName()
	{
		return this.firstName;
	}
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	public String getMrn()
	{
		return this.mrn;
	}
	public void setMrn(String mrn)
	{
		this.mrn = mrn;
	}
	public String getDob()
	{
		return this.dob;
	}
	public void setDob(String dob)
	{
		this.dob = dob;
	}
	public String getSex()
	{
		return this.sex;
	}
	public void setSex(String sex)
	{
		this.sex = sex;
	}
	public String getMdName()
	{
		return this.mdName;
	}
	public void setMdName(String mdName)
	{
		this.mdName = mdName;
	}
	public String getAppointment()
	{
		return this.appointment;
	}
	public void setAppointment(String appointment)
	{
		this.appointment = appointment;
	}
	public String getCheckin()
	{
		return this.checkin;
	}
	public void setCheckin(String checkin)
	{
		this.checkin = checkin;
	}
	public String getStatus()
	{
		return this.status;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	public String getStatusColor()
	{
		return this.statusColor;
	}
	public void setStatusColor(String statusColor)
	{
		this.statusColor = statusColor;
	}
	
	public Integer getPatientId()
	{
		return this.patientId;
	}
	public void setPatientId(Integer patientId)
	{
		this.patientId = patientId;
	}
	public Integer getSessionId()
	{
		return this.sessionId;
	}
	public void setSessionId(Integer sessionId)
	{
		this.sessionId = sessionId;
	}
	
	public String getRowColor() {
		return rowColor;
	}
	
	public void setRowColor(String rowColor) {
		this.rowColor = rowColor;
	}
	
	public Integer getPsfId() {
		return psfId;
	}
	
	public void setPsfId(Integer psfId) {
		this.psfId = psfId;
	}
	public Integer getPwsId() {
		return pwsId;
	}
	
	public void setPwsId(Integer pwsId) {
		this.pwsId = pwsId;
	}

}
