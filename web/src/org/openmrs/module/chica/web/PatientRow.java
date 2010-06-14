/**
 * 
 */
package org.openmrs.module.chica.web;

import java.util.ArrayList;

import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.chica.hibernateBeans.Encounter;

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
	private FormInstance psfId	= null;
	private FormInstance pwsId = null;
	private FormInstance jitID = null;
	private String ageAtVisit = null;
	private String station = null;
	private String weightPercentile = null;
	private String heightPercentile = null;
	private Encounter encounter = null;
	private boolean reprintStatus = false;
	private ArrayList<String> printableJits;
	
	
    /**
     * @return the printableJits
     */
    public ArrayList<String> getPrintableJits() {
    	return this.printableJits;
    }
	public Encounter getEncounter()
	{
		return this.encounter;
	}
	public void setEncounter(Encounter encounter)
	{
		this.encounter = encounter;
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
	public FormInstance getPsfId()
	{
		return this.psfId;
	}
	public void setPsfId(FormInstance psfId)
	{
		this.psfId = psfId;
	}
	public FormInstance getPwsId()
	{
		return this.pwsId;
	}
	public void setPwsId(FormInstance pwsId)
	{
		this.pwsId = pwsId;
	}
	public FormInstance getJitID()
	{
		return this.jitID;
	}
	public void setJitID(FormInstance jitID)
	{
		this.jitID = jitID;
	}
	/**
     * Auto generated method comment
     * 
     * @param printableJits
     */
    public void setPrintableJits(ArrayList<String> printableJits) {
	  this.printableJits = printableJits;
	    
    }
}
