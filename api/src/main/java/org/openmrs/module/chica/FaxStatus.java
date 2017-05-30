
package org.openmrs.module.chica;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * Fax status object for faxes sent via web service.
 *
 * @author Meena Sheley
 */
public class FaxStatus {

	private Log log = LogFactory.getLog(this.getClass());
	public short id;
    private XMLGregorianCalendar transmitTime;
    private String transmitTimeAsString;
    /**
	 * @return the transmitTimeAsString
	 */
	public String getTransmitTimeAsString() {
		return transmitTimeAsString;
	}
	/**
	 * @param transmitTimeAsString the transmitTimeAsString to set
	 */
	public void setTransmitTimeAsString(String transmitTimeAsString) {
		this.transmitTimeAsString = transmitTimeAsString;
	}
	private String faxNumber;

	private short numberOfAttempts;
    private String recipientName;
    private String subject;
    private int pagesTransmitted;
    private int transferRate;  
    private boolean fallBack;
    private int transmissionStatus;
    private String statusText;
    private String statusName;
    private int connectTime;
    private String csi;
    private int portNumber;
    private int totalPages;
    private int attachmentCount;
    private String envelopeName;
    private String renderedImagePath;
    private int index;
    private String idTag;
    private short assignedID;
    private String uniqueJobID;
    private boolean isSelected;
    private Integer patientId;
    private String patientMRN;
    private Integer formId;
    private String formName;
    private String patientFirstName;
    private String patientLastName;
    private String formInstanceString;
    private FormInstance formInstance;
    private Integer locationId;
	private String location;
    /**
	 * @return the locationId
	 */
	public Integer getLocationId() {
		return locationId;
	}
	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	/**
	 * @return the formId
	 */
	public Integer getFormId() {
		return formId;
	}
	/**
	 * @param formId the formId to set
	 */
	public void setFormId(Integer formId) {
		this.formId = formId;
	}
	/**
	 * @return the formName
	 */
	public String getFormName() {
		return formName;
	}
	/**
	 * @param formName the formName to set
	 */
	public void setFormName(String formName) {
		this.formName = formName;
	}
	/**
	 * @return the patientFirstName
	 */
	public String getPatientFirstName() {
		return patientFirstName;
	}
	/**
	 * @param patientFirstName the patientFirstName to set
	 */
	public void setPatientFirstName(String patientFirstName) {
		this.patientFirstName = patientFirstName;
	}
	/**
	 * @return the patientLastName
	 */
	public String getPatientLastName() {
		return patientLastName;
	}
	/**
	 * @param patientLastName the patientLastName to set
	 */
	public void setPatientLastName(String patientLastName) {
		this.patientLastName = patientLastName;
	}
	/**
	 * @return the patientMRN
	 */
	public String getPatientMRN() {
		return patientMRN;
	}
	/**
	 * @param patientMRN
	 */
	public void setPatientMRN(String patientMRN) {
		this.patientMRN = patientMRN;
	}
	/**
	 * @param patientMRN the patientMRN to set
	 */
	private void setPatientMRN(Integer patientId) {

		patientMRN = ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING;

		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		if (patient != null) {
			PatientIdentifier patientIdentifier = patient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
			if (patientIdentifier != null) {
				patientMRN = patientIdentifier.getIdentifier();
			}
		}
	}
	/**
	 * @return the patient_id
	 */
	public Integer getPatientId() {
		return patientId;
	}
	/**
	 * @param patient_id the patient_id to set
	 */
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
	/**
	 * @return the formInstance
	 */
	public FormInstance getFormInstance() {
		return formInstance;
	}
	/**
	 * @param formInstance the formInstance to set
	 */
	public void setFormInstance(FormInstance formInstance) {
		this.formInstance = formInstance;
	}
	
	
	
	/**
	 * @return the id
	 */
	public short getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(short id) {
		this.id = id;
	}
	/**
	 * @return the transmitTime
	 */
	public XMLGregorianCalendar getTransmitTime() {
		return transmitTime;
	}
	/**
	 * @param transmitTime the transmitTime to set
	 */
	public void setTransmitTime(XMLGregorianCalendar transmitTime) {
		this.transmitTime = transmitTime;
	}
	 /**
	 * @param transmitTime
	 * @param format
	 */
	public void setTransmitTimeAsString(XMLGregorianCalendar transmitTime, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date date = transmitTime.toGregorianCalendar().getTime();
		this.transmitTimeAsString = dateFormat.format(date);
	}
	/**
	 * @return the faxNumber
	 */
	public String getFaxNumber() {
		return faxNumber;
	}
	/**
	 * @param faxNumber the faxNumber to set
	 */
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
	/**
	 * @return the formInstance
	 */
	public String getFormInstanceString() {
		return formInstanceString;
	}
	/**
	 * @param formInstance the formInstance to set
	 */
	public void setFormInstanceString(String formInstanceString) {
		this.formInstanceString = formInstanceString;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the numberOfAttempts
	 */
	public short getNumberOfAttempts() {
		return numberOfAttempts;
	}
	/**
	 * @param numberOfAttempts the numberOfAttempts to set
	 */
	public void setNumberOfAttempts(short numberOfAttempts) {
		this.numberOfAttempts = numberOfAttempts;
	}
	/**
	 * @return the recipientName
	 */
	public String getRecipientName() {
		return recipientName;
	}
	/**
	 * @param recipientName the recipientName to set
	 */
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the pagesTransmitted
	 */
	public int getPagesTransmitted() {
		return pagesTransmitted;
	}
	/**
	 * @param pagesTransmitted the pagesTransmitted to set
	 */
	public void setPagesTransmitted(int pagesTransmitted) {
		this.pagesTransmitted = pagesTransmitted;
	}
	/**
	 * @return the transferRate
	 */
	public int getTransferRate() {
		return transferRate;
	}
	/**
	 * @param transferRate the transferRate to set
	 */
	public void setTransferRate(int transferRate) {
		this.transferRate = transferRate;
	}
	/**
	 * @return the fallBack
	 */
	public boolean isFallBack() {
		return fallBack;
	}
	/**
	 * @param fallBack the fallBack to set
	 */
	public void setFallBack(boolean fallBack) {
		this.fallBack = fallBack;
	}
	/**
	 * @return the transmissionStatus
	 */
	public int getTransmissionStatus() {
		return transmissionStatus;
	}
	/**
	 * @param transmissionStatus the transmissionStatus to set
	 */
	public void setTransmissionStatus(int transmissionStatus) {
		this.transmissionStatus = transmissionStatus;
	}
	/**
	 * @return the statusText
	 */
	public String getStatusText() {
		return statusText;
	}
	/**
	 * @param statusText the statusText to set
	 */
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	/**
	 * @return the statusName
	 */
	public String getStatusName() {
		return statusName;
	}
	/**
	 * @param statusName the statusName to set
	 */
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	/**
	 * @return the connectTime
	 */
	public int getConnectTime() {
		return connectTime;
	}
	/**
	 * @param connectTime the connectTime to set
	 */
	public void setConnectTime(int connectTime) {
		this.connectTime = connectTime;
	}
	/**
	 * @return the csi
	 */
	public String getCsi() {
		return csi;
	}
	/**
	 * @param csi the csi to set
	 */
	public void setCsi(String csi) {
		this.csi = csi;
	}
	/**
	 * @return the portNumber
	 */
	public int getPortNumber() {
		return portNumber;
	}
	/**
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	/**
	 * @return the totalPages
	 */
	public int getTotalPages() {
		return totalPages;
	}
	/**
	 * @param totalPages the totalPages to set
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	/**
	 * @return the attachmentCount
	 */
	public int getAttachmentCount() {
		return attachmentCount;
	}
	/**
	 * @param attachmentCount the attachmentCount to set
	 */
	public void setAttachmentCount(int attachmentCount) {
		this.attachmentCount = attachmentCount;
	}
	/**
	 * @return the envelopeName
	 */
	public String getEnvelopeName() {
		return envelopeName;
	}
	/**
	 * @param envelopeName the envelopeName to set
	 */
	public void setEnvelopeName(String envelopeName) {
		this.envelopeName = envelopeName;
	}
	/**
	 * @return the renderedImagePath
	 */
	public String getRenderedImagePath() {
		return renderedImagePath;
	}
	/**
	 * @param renderedImagePath the renderedImagePath to set
	 */
	public void setRenderedImagePath(String renderedImagePath) {
		this.renderedImagePath = renderedImagePath;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * @return the idTag
	 */
	public String getIdTag() {
		return idTag;
	}
	/**
	 * @param idTag the idTag to set
	 */
	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}
	/**
	 * @return the assignedID
	 */
	public short getAssignedID() {
		return assignedID;
	}
	/**
	 * @param assignedID the assignedID to set
	 */
	public void setAssignedID(short assignedID) {
		this.assignedID = assignedID;
	}
	/**
	 * @return the uniqueJobID
	 */
	public String getUniqueJobID() {
		return uniqueJobID;
	}
	/**
	 * @param uniqueJobID the uniqueJobID to set
	 */
	public void setUniqueJobID(String uniqueJobID) {
		this.uniqueJobID = uniqueJobID;
	}
	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}
	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	/**
	 * @return the faxStateFlags
	 */
	public Short getFaxStateFlags() {
		return faxStateFlags;
	}
	/**
	 * @param faxStateFlags the faxStateFlags to set
	 */
	public void setFaxStateFlags(Short faxStateFlags) {
		this.faxStateFlags = faxStateFlags;
	}
	private Short faxStateFlags;


	public void setLocationByFormInstance(FormInstance formInstance){
		
		try {
			if (formInstance != null){
				if ((locationId =formInstance.getLocationId())!= null){
					Location location = Context.getLocationService().getLocation(locationId);
					if (location != null){
						this.location = location.getName();
					}
				}
			}
		} catch (Exception e) {
			log.error("Unable to extract fax location (clinic) from fax id tag", e);
		}
		
	}
	
	public void setFormNameByFormInstance(FormInstance formInstance){
		
		if (formInstance != null ){
			formId = formInstance.getFormId();
			Form form = Context.getFormService().getForm(formId);
			if (form != null ){
				formName = form.getName();
			}
		}
	}
	
	public void setPatientByByFormInstance(FormInstance formInstance) {

		ChirdlUtilBackportsService chirdlUtilBackportsService = Context.getService(ChirdlUtilBackportsService.class);
		List<PatientState> states = new ArrayList<PatientState>();
		try {
			if (formInstance != null){
				states = chirdlUtilBackportsService.getPatientStatesByFormInstance(formInstance, false);
			}

			if (states == null || states.isEmpty()) {
				return;
			}
			patientId = states.get(0).getPatientId();
			Patient patient = Context.getPatientService().getPatient(patientId);
			if (patient != null) {
				patientFirstName = patient.getGivenName();
				patientLastName = patient.getFamilyName();
				setPatientMRN(patientId);
			}
		} catch (Exception e) {
			log.error("Error setting fax patient information from form instance.", e);
		}

	}
	
	public void  setFormInstanceByIdTag(String idTag){
		
		
		try {
			if (idTag == null){
				return;

			}
			String [] formInstanceSubstrings = idTag.split("[^a-zA-Z0-9']+");
			if (formInstanceSubstrings != null && formInstanceSubstrings.length >= 3){
				
				Integer locationId = Integer.valueOf(formInstanceSubstrings[0]);
				Integer formId = Integer.valueOf(formInstanceSubstrings[1]);
				Integer formInstanceId = Integer.valueOf(formInstanceSubstrings[2]);
				this.formInstance = new FormInstance(locationId, formId, formInstanceId);	
				this.formInstanceString = formInstance.toString();
				setLocationByFormInstance(formInstance);
				setPatientByByFormInstance(formInstance);
				setFormNameByFormInstance(formInstance);
			}
			
			
		} catch (Exception e) {
			//The fax id tag was not a correct form instance id format.  No need for stack trace.
			log.info("Unable to determine form instance from fax status because " + idTag + " is not a valid form instance format");
		}
		
		return;
				
	}

	public void setImageLocation(String idTag){
		//get the image directory from attributes
		// get the form file based on directory and idTag
	}
	
	
}
