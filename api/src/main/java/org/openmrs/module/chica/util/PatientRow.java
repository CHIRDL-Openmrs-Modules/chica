/**
 * 
 */
package org.openmrs.module.chica.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;


/**
 * @author tmdugan
 *
 */
public class PatientRow implements Serializable
{
    private static final long serialVersionUID = 1L;
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
	private boolean isManualCheckin = false;
	private FormInstance psfId	= null;
	private FormInstance pwsId = null;
	private String ageAtVisit = null;
	private String station = null;
	private String weightPercentile = null;
	private String heightPercentile = null;
	private Encounter encounter = null;
	private boolean reprintStatus = false;
	private ArrayList<String> printableJits;
	private LinkedHashSet<FormInstance> formInstances; //CHICA-815 Ensure correct order of eJITs - MSHELEY
	private boolean pwsScanned = false;
	private Integer locationId = null;
	private Integer locationTagId = null;
	private Integer ageInYears = null;
	
	
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
		return this.heightPercentile;
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
		return this.weightPercentile;
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
		return this.station;
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
		return this.ageAtVisit;
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
		return this.reprintStatus;
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

	/**
     * Auto generated method comment
     * 
     * @param printableJits
     */
    public void setPrintableJits(ArrayList<String> printableJits) {
	  this.printableJits = printableJits;
	    
    }
    
    //CHICA-815 Ensure correct order of mobile eJITs - MSHELEY
    public void addFormInstance(FormInstance formInstance){
    	if(this.formInstances == null){
    		this.formInstances = new LinkedHashSet<>();
    	}
    	this.formInstances.add(formInstance);
    }
    
    public Set<FormInstance> getFormInstances(){
    	return this.formInstances;
    }
    
    /**
	 * @return the pwsScanned
	 */
	public boolean isPwsScanned() {
		return this.pwsScanned;
	}
	/**
	 * @param pwsScanned the pwsScanned to set
	 */
	public void setPwsScanned(boolean pwsScanned) {
		this.pwsScanned = pwsScanned;
	}
		
	/**
     * @return the locationId
     */
    public Integer getLocationId() {
    	return this.locationId;
    }
	
    /**
     * @param locationId the locationId to set
     */
    public void setLocationId(Integer locationId) {
    	this.locationId = locationId;
    }
	
    /**
     * @return the locationTagId
     */
    public Integer getLocationTagId() {
    	return this.locationTagId;
    }
	
    /**
     * @param locationTagId the locationTagId to set
     */
    public void setLocationTagId(Integer locationTagId) {
    	this.locationTagId = locationTagId;
    }
    
    /**
     * @return the isManualCheckin
     */
    public boolean getIsManualCheckin() {
    	return this.isManualCheckin;
    }
	
    /**
     * @param isManualCheckin the isManualCheckin to set
     */
    public void setIsManualCheckin(boolean isManualCheckin) {
    	this.isManualCheckin = isManualCheckin;
    }
    
    /**
     * Gets Integer age in years, no units (yo, mo, wk, do)
     * @return
     */
    public Integer getAgeInYears()
    {
    	return this.ageInYears;
    }
    
    /**
     * @param ageInYears
     */
    public void setAgeInYears(Integer ageInYears)
    {
    	this.ageInYears = ageInYears;
    }
    
    /**
     * 
     * Creates an XML representation of this PatientRow object.
     * 
     * @return String containing the XML representation of this PatientRow object.
     */
    public String toXml() {
    	StringBuilder xmlWriter = new StringBuilder();
    	xmlWriter.append("<patientRow>");
    	xmlWriter.append("<firstName>" + this.firstName + "</firstName>");
    	xmlWriter.append("<lastName>" + this.lastName + "</lastName>");
    	xmlWriter.append("<mrn>" + this.mrn + "</mrn>");
    	xmlWriter.append("<dob>" + this.dob + "</dob>");
    	xmlWriter.append("<sex>" + this.sex + "</sex>");
    	xmlWriter.append("<mdName>" + this.mdName + "</mdName>");
    	xmlWriter.append("<appointment>" + this.appointment + "</appointment>");
    	xmlWriter.append("<checkin>" + this.checkin + "</checkin>");
    	xmlWriter.append("<status>" + this.status + "</status>");
    	xmlWriter.append("<statusColor>" + this.statusColor + "</statusColor>");
    	xmlWriter.append("<patientId>" + this.patientId + "</patientId>");
    	xmlWriter.append("<sessionId>" + this.sessionId + "</sessionId>");
    	xmlWriter.append("<isManualCheckin>" + this.isManualCheckin + "</isManualCheckin>");
    	xmlWriter.append("<psfId>");
    	if (psfId != null) {
    		xmlWriter.append("<formId>" + this.psfId.getFormId() + "</formId>");
    		xmlWriter.append("<formInstanceId>" + this.psfId.getFormInstanceId() + "</formInstanceId>");
    		xmlWriter.append("<locationId>" + this.psfId.getLocationId() + "</locationId>");
    	}
    	xmlWriter.append("</psfId>");
    	xmlWriter.append("<pwsId>");
    	if (pwsId != null) {
    		xmlWriter.append("<formId>" + this.pwsId.getFormId() + "</formId>");
    		xmlWriter.append("<formInstanceId>" + this.pwsId.getFormInstanceId() + "</formInstanceId>");
    		xmlWriter.append("<locationId>" + this.pwsId.getLocationId() + "</locationId>");
    	}
    	xmlWriter.append("</pwsId>");
    	xmlWriter.append("<ageAtVisit>" + this.ageAtVisit + "</ageAtVisit>");
    	xmlWriter.append("<ageInYears>" + this.ageInYears + "</ageInYears>");
    	xmlWriter.append("<station>" + this.station + "</station>");
    	xmlWriter.append("<weightPercentile>" + this.weightPercentile + "</weightPercentile>");
    	xmlWriter.append("<heightPercentile>" + this.heightPercentile + "</heightPercentile>");
    	xmlWriter.append("<reprintStatus>" + this.reprintStatus + "</reprintStatus>");
    	xmlWriter.append("<printableJits>");
    	if (printableJits != null) {
    		for (String jit : this.printableJits) {
    			xmlWriter.append("<printableJit>" + jit + "</printableJit>");
    		}
    	}
    	xmlWriter.append("</printableJits>");
    	xmlWriter.append("<formInstances>");
    	if (this.formInstances != null) {
    		Iterator<FormInstance> iter = this.formInstances.iterator();
    		while (iter.hasNext()) {
    			FormInstance formInstance = iter.next();
    			xmlWriter.append("<formInstance>");
    			xmlWriter.append("<formId>" + formInstance.getFormId() + "</formId>");
        		xmlWriter.append("<formInstanceId>" + formInstance.getFormInstanceId() + "</formInstanceId>");
        		xmlWriter.append("<locationId>" + formInstance.getLocationId() + "</locationId>");
        		xmlWriter.append("</formInstance>");
    		}
    	}
    	xmlWriter.append("</formInstances>");
    	xmlWriter.append("<pwsScanned>" + this.pwsScanned + "</pwsScanned>");
    	xmlWriter.append("<locationId>" + this.locationId + "</locationId>");
    	xmlWriter.append("<locationTagId>" + this.locationTagId + "</locationTagId>");
    	xmlWriter.append("<encounter>");
    	if (encounter != null) {
    		//TODO: fill in encounter information.  This isn't needed for the immediate implementation for the grease board.
    	}
    	xmlWriter.append("</encounter>");
    	xmlWriter.append("</patientRow>");
    	
    	return xmlWriter.toString();
    }
}
