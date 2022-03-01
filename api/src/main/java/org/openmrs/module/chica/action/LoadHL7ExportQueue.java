/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.Date;
import java.util.HashMap;

import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportMap;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmdugan
 *
 */
public class LoadHL7ExportQueue implements ProcessStateAction
{

	private static final String EMPTY_STRING = "";
	private static final int EXPORT_STATUS_PENDING = 1;
	private static final String VITALS_CONCEPT_MAP_LOCATION = "VitalsConceptMapLocation";
	private static final String TIFF_CONCEPT_MAP_LOCATION = "TiffConceptMapLocation";
	private static final String POC_CONCEPT_MAP_LOCATION = "POCConceptMapLocation";
	private static final String FORM_ATTRIBUTE_EXPORTABLE = "exportForm";
	
	/** Logger for this class and subclasses */
	private static final Logger log = LoggerFactory.getLogger(LoadHL7ExportQueue.class);

	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		
		PatientService patientService = Context.getPatientService();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);	
		FormService formService = Context.getFormService();
		
		//Lookup the patient again to avoid lazy initialization errors
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		//Lookup the form to avoid lazy initialization errors
		Integer formId = patientState.getFormId();
		Form form = formService.getForm(formId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		Session session = chirdlutilbackportsService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		
		try {
			
			if (currState == null){
				return;
			}
			String currStateName = currState.getName();
			if (ChirdlUtilConstants.STATE_EXPORT_VITALS.equals(currStateName)){
				
				//Concept map files will be eliminated in a future update
				LocationTagAttributeValue  conceptMapLocationTagAttrValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, VITALS_CONCEPT_MAP_LOCATION, locationId);
				if (conceptMapLocationTagAttrValue == null || conceptMapLocationTagAttrValue.getValue() == null
						|| conceptMapLocationTagAttrValue.getValue().trim().equals("")) {
					log.error("Location tag attribute for vitals concept map location for location id = {} is null or empty.", locationId);
					return;
				}
				addExportToQueue(encounterId, sessionId, conceptMapLocationTagAttrValue );
				return;	
				
			}
			
			//Export the observations scanned from PWS 
			if (ChirdlUtilConstants.STATE_EXPORT_POC.equals(currStateName)) {
				
				//Concept map files will be eliminated in a future update
				LocationTagAttributeValue  conceptMapLocationTagAttrValue = 
						chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, POC_CONCEPT_MAP_LOCATION, 
							locationId);
				if (conceptMapLocationTagAttrValue == null || conceptMapLocationTagAttrValue.getValue() == null
						|| conceptMapLocationTagAttrValue.getValue().trim().equals("")) {
					log.error("Location tag attribute for location of POC observation concept map for location id ={} is null or empty.", locationId);
					return;
				};
				addExportToQueue(encounterId, sessionId, conceptMapLocationTagAttrValue );
	
				//Do not return yet, because both observations AND scanned PWS forms are exported.
				
			}
			
			//Export form image (PWS or JIT)
			
			/*  CHICA-597 MES 
			 * Check form attribute to determine if a form should be exported.
			 * 	We will no longer check medium of electronic versus paper. The form attribute, that defines
			 * need for export, can be configured by location.
			 * For the time when any clinics have both paper and electronic PWS, we will set PWS as an exportable form. 
			 * The paper PWS will get exported, and we will still get exporter errors for electronic forms temporarily.
			 * The PWS export errors from the Operations Dashboard have been disabled.  
			 * When each clinic moves completely to ePWS, we will set the form attribute for those locations
			 * to disable exporting the PWS.
			 */
			
			FormAttributeValue exportAttrValue = 
					chirdlutilbackportsService.getFormAttributeValue(formId, FORM_ATTRIBUTE_EXPORTABLE, locationTagId, locationId);
			
			if (exportAttrValue == null ){
				//do not export
				return;
			}
			
			if (ChirdlUtilConstants.GENERAL_INFO_TRUE.equals(exportAttrValue.getValue())){						
				
				//Concept map files will be eliminated in a future update
				LocationTagAttributeValue  tiffLocationTagConceptMapLocation = 
						chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, form.getName() + TIFF_CONCEPT_MAP_LOCATION, 
								locationId);
				if (tiffLocationTagConceptMapLocation == null || tiffLocationTagConceptMapLocation.getValue() == null
						|| tiffLocationTagConceptMapLocation.getValue().trim().equals("")){
					log.error("Location tag attribute for location of tiff concept map is null or empty for location id = {}.", locationId);
					return;
				}
				
				addExportToQueue(encounterId, sessionId, tiffLocationTagConceptMapLocation );
			}
			
		} finally{
			
		StateManager.endState(patientState);
		BaseStateActionHandler.changeState(patient, sessionId, currState,stateAction,
				parameters,locationTagId,locationId);
		}
	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}
	
	private void addExportToQueue(Integer encounterId, Integer sessionId, LocationTagAttributeValue tagValue) {
		
		ChicaService chicaService = Context.getService(ChicaService.class);
		String conceptMapLocation = tagValue.getValue();
		
		if (conceptMapLocation != null && !conceptMapLocation.equals(EMPTY_STRING)){
			ChicaHL7Export export = new ChicaHL7Export();
			export.setDateInserted(new Date());
			export.setEncounterId(encounterId);
			export.setSessionId(sessionId);
			export.setVoided(false);
			export.setStatus(EXPORT_STATUS_PENDING);
			chicaService.saveChicaHL7Export(export);
			ChicaHL7ExportMap tiffMap = new ChicaHL7ExportMap();
			ChicaHL7Export insertedExport = chicaService.insertEncounterToHL7ExportQueue(export);
			tiffMap.setValue(String.valueOf(tagValue.getLocationTagAttributeValueId()));
			tiffMap.setHl7ExportQueueId(insertedExport.getQueueId());
			tiffMap.setDateInserted(new Date());
			tiffMap.setVoided(false);
			chicaService.saveHL7ExportMap(tiffMap);
		}

	}

}
