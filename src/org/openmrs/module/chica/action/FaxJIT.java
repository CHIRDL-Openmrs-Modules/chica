/**
 * 
 */
package org.openmrs.module.chica.action;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.ChicaStateActionHandler;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chirdlutil.util.IOUtil;

/**
 * @author Steve McKee
 */
public class FaxJIT implements ProcessStateAction {
	
	private static Log log = LogFactory.getLog(FaxJIT.class);
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient, PatientState patientState,
	                          HashMap<String, Object> parameters) {
		// lookup the patient again to avoid lazy initialization errors
		ATDService atdService = Context.getService(ATDService.class);
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		Integer formId = formInstance.getFormId();
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		try {
			// see if the form needs to be faxed
			FormAttributeValue formAttrVal = atdService.getFormAttributeValue(formId, "auto-fax", locationTagId, locationId);
			if (formAttrVal != null && "true".equals(formAttrVal.getValue())) {
				// get the fax directory
				AdministrationService adminService = Context.getAdministrationService();
				String faxDirectory = adminService.getGlobalProperty("chica.outgoingFaxDirectory");
				if (faxDirectory == null || faxDirectory.trim().length() == 0) {
					String message = "Location: " + locationId + " Form: " + formId
					        + " is set to auto-fax, but the chica.outgoingFaxDirectory global property is not set.";
					logError(atdService, sessionId, message, null);
				} else if (!(new File(faxDirectory).exists())) {
					String message = "Location: " + locationId + " Form: " + formId
					        + " is set to auto-fax, but the chica.outgoingFaxDirectory cannot be found: " + faxDirectory;
					logError(atdService, sessionId, message, null);
				} else {
					// get the clinic fax number
					ChirdlUtilService chirdlService = Context.getService(ChirdlUtilService.class);
					LocationAttributeValue locAttrVal = chirdlService.getLocationAttributeValue(locationId,
					    "clinicFaxNumber");
					if (locAttrVal != null && locAttrVal.getValue() != null && locAttrVal.getValue().trim().length() > 0) {
						String clinicFaxNumber = locAttrVal.getValue();
						FormAttributeValue tiffLocVal = atdService.getFormAttributeValue(formId, "imageDirectory",
						    locationTagId, locationId);
						if (tiffLocVal != null && tiffLocVal.getValue() != null && tiffLocVal.getValue().trim().length() > 0) {
							Integer formInstId = formInstance.getFormInstanceId();
							String filename = locationId + "-" + formId + "-" + formInstId;
							String imageLocDir = tiffLocVal.getValue();
							File imageFile = IOUtil.searchForImageFile(filename, imageLocDir);
							// check to see if the file exists
							if (imageFile.exists()) {
								String from = adminService.getGlobalProperty("chica.outgoingFaxFrom");
								LocationAttributeValue faxReceiverVal = chirdlService.getLocationAttributeValue(locationId,
								    "clinicFaxReceiver");
								String to = "Clinical Staff";
								if (faxReceiverVal != null && faxReceiverVal.getValue() != null
								        && faxReceiverVal.getValue().trim().length() > 0) {
									to = faxReceiverVal.getValue();
								}
								
								createFaxControlFile(faxDirectory, imageFile, from, to, clinicFaxNumber, filename);
							} else {
								String message = "Error locating form to auto-fax - Location: " + locationId + " Form: "
								        + formId + " File: " + imageFile.getAbsolutePath();
								logError(atdService, sessionId, message, null);
							}
						} else {
							String message = "Location: " + locationId + " Form: " + formId
							        + " is set to auto-fax, but the image directory cannot be found for the form.";
							logError(atdService, sessionId, message, null);
						}
					} else {
						String message = "Location: " + locationId + " Form: " + formId
						        + " is set to auto-fax, but no clinicFaxNumber exists.";
						logError(atdService, sessionId, message, null);
					}
				}
			}
		}
		catch (Exception e) {
			String message = "Error auto-faxing form - Location: " + locationId + " Form: " + formId;
			logError(atdService, sessionId, message, e);
		}
		finally {
			StateManager.endState(patientState);
			ChicaStateActionHandler.changeState(patient, sessionId, currState,
					stateAction,parameters,locationTagId,locationId);
		}
	}
	
	private void createFaxControlFile(String faxDirectory, File fileToFax, String from, String to, String faxNumber,
	                                  String controlFilename) throws Exception {
		// copy the image file to the fax directory
		String name = fileToFax.getName();
		String destination = faxDirectory + File.separator + name;
		IOUtil.copyFile(fileToFax.getAbsolutePath(), destination);
		
		// create the control file
		File controlFile = new File(faxDirectory, controlFilename + ".col");
		FileWriter writer = new FileWriter(controlFile);
		String lineSeparator = System.getProperty("line.separator");
		StringBuffer data = new StringBuffer("##filename ");
		data.append(name);
		data.append(lineSeparator);
		data.append("##covername Generic.doc");
		data.append(lineSeparator);
		data.append("##cover");
		data.append(lineSeparator);
		data.append("##from ");
		data.append(from);
		data.append(lineSeparator);
		data.append("##to ");
		data.append(to);
		data.append(lineSeparator);
		data.append("##dial ");
		data.append(faxNumber);
		
		try {
			writer.write(data.toString());
		}
		finally {
			writer.flush();
			writer.close();
		}
	}
	
	private void logError(ATDService atdService, Integer sessionId, String message, Throwable e) {
		log.error("Error auto-faxing form");
		log.error(message);
		ATDError atdError = new ATDError("Error", "General Error", message, null, new Date(), sessionId);
		atdService.saveError(atdError);
	}
	
	public void changeState(PatientState patientState, HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}
	
}
