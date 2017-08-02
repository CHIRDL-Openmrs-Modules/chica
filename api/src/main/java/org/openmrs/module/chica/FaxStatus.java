
package org.openmrs.module.chica;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
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
	private FormInstance formInstance;
	private Integer locationId;
	private Integer locationTagId;
	private String locationName;
	private Location location;
	private String imageFileLocation;



	private Short faxStateFlags;

	public FaxStatus(String idTag) {

		LocationService locationService = Context.getLocationService();
		this.idTag = idTag;
		if ("".equalsIgnoreCase(idTag)){
			return;
		}
		setFormInstance(idTag);
		if (formInstance == null) return;
		setFormNameByFormInstance(formInstance);
		setLocation(formInstance);

		PatientService patientService = Context.getPatientService();
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
			locationTagId = states.get(0).getLocationTagId();
			if (patientId != null){
				Patient patient = patientService.getPatient(patientId);
				if (patient != null) {
					patientFirstName = patient.getGivenName();
					patientLastName = patient.getFamilyName();
					setPatientMRN(patient);
				}
			}

			Location location = locationService.getLocation(formInstance.getLocationId());
			if (location != null){
				setLocationName(location.getName());
			}


		} catch (Exception e) {
			log.error("Error setting fax patient information from form instance " + formInstance.toString() + ".", e);
		}
	}

	/**
	 * @return the assignedID
	 */
	public short getAssignedID() {
		return assignedID;
	}

	/**
	 * @return the attachmentCount
	 */
	public int getAttachmentCount() {
		return attachmentCount;
	}
	/**
	 * @return the connectTime
	 */
	public int getConnectTime() {
		return connectTime;
	}

	/**
	 * @return the csi
	 */
	public String getCsi() {
		return csi;
	}
	/**
	 * @return the envelopeName
	 */
	public String getEnvelopeName() {
		return envelopeName;
	}
	/**
	 * @return the faxNumber
	 */
	public String getFaxNumber() {
		return faxNumber;
	}
	/**
	 * @return the faxStateFlags
	 */
	public Short getFaxStateFlags() {
		return faxStateFlags;
	}
	/**
	 * @return the formId
	 */
	public Integer getFormId() {
		return formId;
	}
	/**
	 * @return the formInstance
	 */
	public FormInstance getFormInstance() {
		return formInstance;
	}
	/**
	 * @return the formName
	 */
	public String getFormName() {
		return formName;
	}
	/**
	 * @return the id
	 */
	public short getId() {
		return id;
	}
	/**
	 * @return the idTag
	 */
	public String getIdTag() {
		return idTag;
	}
	/**
	 * @return the imageFileLocation
	 */
	public String getImageFileLocation() {
		return imageFileLocation;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}
	/**
	 * @return the locationId
	 */
	public Integer getLocationId() {
		return locationId;
	}
	/**
	 * @return the locationTagId
	 */
	public Integer getLocationTagId() {
		return locationTagId;
	}
	/**
	 * @return the numberOfAttempts
	 */
	public short getNumberOfAttempts() {
		return numberOfAttempts;
	}

	/**
	 * @return the pagesTransmitted
	 */
	public int getPagesTransmitted() {
		return pagesTransmitted;
	}
	/**
	 * @return the patientFirstName
	 */
	public String getPatientFirstName() {
		return patientFirstName;
	}



	/**
	 * @return the patient_id
	 */
	public Integer getPatientId() {
		return patientId;
	}
	/**
	 * @return the patientLastName
	 */
	public String getPatientLastName() {
		return patientLastName;
	}
	/**
	 * @return the patientMRN
	 */
	public String getPatientMRN() {
		return patientMRN;
	}
	/**
	 * @return the portNumber
	 */
	public int getPortNumber() {
		return portNumber;
	}
	/**
	 * @return the recipientName
	 */
	public String getRecipientName() {
		return recipientName;
	}
	/**
	 * @return the renderedImagePath
	 */
	public String getRenderedImagePath() {
		return renderedImagePath;
	}
	/**
	 * @return the statusName
	 */
	public String getStatusName() {
		return statusName;
	}

	/**
	 * @return the statusText
	 */
	public String getStatusText() {
		return statusText;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @return the totalPages
	 */
	public int getTotalPages() {
		return totalPages;
	}
	/**
	 * @return the transferRate
	 */
	public int getTransferRate() {
		return transferRate;
	}
	/**
	 * @return the transmissionStatus
	 */
	public int getTransmissionStatus() {
		return transmissionStatus;
	}
	/**
	 * @return the transmitTime
	 */
	public XMLGregorianCalendar getTransmitTime() {
		return transmitTime;
	}
	/**
	 * @return the transmitTimeAsString
	 */
	public String getTransmitTimeAsString() {
		return transmitTimeAsString;
	}
	/**
	 * @return the uniqueJobID
	 */
	public String getUniqueJobID() {
		return uniqueJobID;
	}
	/**
	 * @return the fallBack
	 */
	public boolean isFallBack() {
		return fallBack;
	}
	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}
	/**
	 * @param assignedID the assignedID to set
	 */
	public void setAssignedID(short assignedID) {
		this.assignedID = assignedID;
	}
	/**
	 * @param attachmentCount the attachmentCount to set
	 */
	public void setAttachmentCount(int attachmentCount) {
		this.attachmentCount = attachmentCount;
	}
	/**
	 * @param connectTime the connectTime to set
	 */
	public void setConnectTime(int connectTime) {
		this.connectTime = connectTime;
	}
	/**
	 * @param csi the csi to set
	 */
	public void setCsi(String csi) {
		this.csi = csi;
	}
	/**
	 * @param envelopeName the envelopeName to set
	 */
	public void setEnvelopeName(String envelopeName) {
		this.envelopeName = envelopeName;
	}
	/**
	 * @param fallBack the fallBack to set
	 */
	public void setFallBack(boolean fallBack) {
		this.fallBack = fallBack;
	}
	/**
	 * @param faxNumber the faxNumber to set
	 */
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
	/**
	 * @param faxStateFlags the faxStateFlags to set
	 */
	public void setFaxStateFlags(Short faxStateFlags) {
		this.faxStateFlags = faxStateFlags;
	}
	/**
	 * @param formId the formId to set
	 */
	public void setFormId(Integer formId) {
		this.formId = formId;
	}
	/**
	 * @param formInstance the formInstance to set
	 */
	public void setFormInstance(FormInstance formInstance) {
		this.formInstance = formInstance;
	}
	
	
	/**Create a form instance based on the fax idtag consisting of location id, form id, and form instance.
	 * @param idTag
	 * @return
	 */
	public FormInstance setFormInstance(String idTag){

		try {
			// Exit if the form instance already exists.  
			if (formInstance != null || StringUtils.isBlank(idTag)){
				return null;
			}

			String [] formInstanceSubstrings = idTag.split("[^a-zA-Z0-9']+");
			if (formInstanceSubstrings != null && formInstanceSubstrings.length >= 3){

				Integer locationId = Integer.valueOf(formInstanceSubstrings[0]);
				Integer formId = Integer.valueOf(formInstanceSubstrings[1]);
				Integer formInstanceId = Integer.valueOf(formInstanceSubstrings[2]);
				this.formInstance = new FormInstance(locationId, formId, formInstanceId);	
			}

		} catch (NumberFormatException e) {
			//The fax id tag was not a correct form instance id format.  No need for stack trace.
			//There are past faxes that have the FAXCOM unique id in that field.  
			//Do not print to log.
			this.idTag = ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING;
		} catch (Exception e2){
			log.error("Unable to determine fax status form instance from idTag " + idTag, e2);
		}

		return formInstance;

	}
	/**
	 * @param formName the formName to set
	 */
	public void setFormName(String formName) {
		this.formName = formName;
	}
	
	/**Parse form_id from form instance and get form name
	 * @param formInstance
	 */
	public void setFormNameByFormInstance(FormInstance formInstance){
		FormService formService = Context.getFormService();
		if (formInstance != null ){
			formId = formInstance.getFormId();
			Form form = formService.getForm(formId);
			if (form != null ){
				formName = form.getName();
			}
		}
	}
	/**
	 * @param id the id to set
	 */
	public void setId(short id) {
		this.id = id;
	}
	/**
	 * @param idTag the idTag to set
	 */
	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}
	/**
	 * @param imageFileLocation the imageFileLocation to set
	 */
	public void setImageFileLocation(String imageFileLocation) {
		this.imageFileLocation = imageFileLocation;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(FormInstance formInstance) {

		if (formInstance == null ){
			return;
		}

		LocationService locationService = Context.getLocationService();
		try {

			if ((locationId =formInstance.getLocationId())!= null){
				Location location = locationService.getLocation(locationId);
				if (location != null){
					this.setLocationName(location.getName());
				}
			}

		} catch (Exception e) {
			log.error("Unable to extract fax location (clinic) from form instance  " + formInstance.toString() + ".", e);
		}


	}
	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	/**
	 * @param locationTagId the locationTagId to set
	 */
	public void setLocationTagId(Integer locationTagId) {
		this.locationTagId = locationTagId;
	}
	/**
	 * @param numberOfAttempts the numberOfAttempts to set
	 */
	public void setNumberOfAttempts(short numberOfAttempts) {
		this.numberOfAttempts = numberOfAttempts;
	}
	/**
	 * @param pagesTransmitted the pagesTransmitted to set
	 */
	public void setPagesTransmitted(int pagesTransmitted) {
		this.pagesTransmitted = pagesTransmitted;
	}
	/**Get the patient from the patient state table using location, form, and form instance
	 * @param idTag
	 */
	public void setPatient(String idTag) {
		if (StringUtils.isBlank(idTag)){
			return;
		}
		setFormInstance(idTag);
		if (formInstance == null) return;
		PatientService patientService = Context.getPatientService();
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
			locationTagId = states.get(0).getLocationTagId();
			if (patientId != null){
				Patient patient = patientService.getPatient(patientId);
				if (patient != null) {
					patientFirstName = patient.getGivenName();
					patientLastName = patient.getFamilyName();
					setPatientMRN(patient);
				}
			}

		} catch (Exception e) {
			log.error("Error setting fax patient information from form instance " + formInstance.toString() + "." , e);
		}

	}
	/**
	 * @param patientFirstName the patientFirstName to set
	 */
	public void setPatientFirstName(String patientFirstName) {
		this.patientFirstName = patientFirstName;
	}
	/**
	 * @param patient_id the patient_id to set
	 */
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	/**
	 * @param patientLastName the patientLastName to set
	 */
	public void setPatientLastName(String patientLastName) {
		this.patientLastName = patientLastName;
	}
	/**
	 * @param patientMRN
	 */
	public void setPatientMRN(String patientMRN) {
		this.patientMRN = patientMRN;
	}
	/**
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	/**
	 * @param recipientName the recipientName to set
	 */
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	/**
	 * @param renderedImagePath the renderedImagePath to set
	 */
	public void setRenderedImagePath(String renderedImagePath) {
		this.renderedImagePath = renderedImagePath;
	}
	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	/**
	 * @param statusName the statusName to set
	 */
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	/**
	 * @param statusText the statusText to set
	 */
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @param totalPages the totalPages to set
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	/**
	 * @param transferRate the transferRate to set
	 */
	public void setTransferRate(int transferRate) {
		this.transferRate = transferRate;
	}
	/**
	 * @param transmissionStatus the transmissionStatus to set
	 */
	public void setTransmissionStatus(int transmissionStatus) {
		this.transmissionStatus = transmissionStatus;
	}


	/**
	 * @param transmitTime the transmitTime to set
	 */
	public void setTransmitTime(XMLGregorianCalendar transmitTime) {
		this.transmitTime = transmitTime;
	}

	/**
	 * @param transmitTimeAsString the transmitTimeAsString to set
	 */
	public void setTransmitTimeAsString(String transmitTimeAsString) {
		this.transmitTimeAsString = transmitTimeAsString;
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
	 * @param uniqueJobID the uniqueJobID to set
	 */
	public void setUniqueJobID(String uniqueJobID) {
		this.uniqueJobID = uniqueJobID;
	}
	
	/**
	 * @param patientMRN the patientMRN to set
	 */
	private void setPatientMRN(Patient patient) {

		patientMRN = ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING;

		if (patient != null) {
			PatientIdentifier patientIdentifier = patient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
			if (patientIdentifier != null) {
				patientMRN = patientIdentifier.getIdentifier();
			}
		}
	}

	/**
	 * @return
	 */
	public String getLocationName() {
		return locationName;
	}

	/**
	 * @param locationName
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}



}
