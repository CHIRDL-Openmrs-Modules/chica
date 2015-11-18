/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.Date;
import java.util.HashMap;

import org.jfree.util.Log;
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

	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		
		PatientService patientService = Context.getPatientService();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);	
		FormService formService = Context.getFormService();
		
		//lookup the patient again to avoid lazy initialization errors
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		Session session = chirdlutilbackportsService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		
		try {
			
			if (currState.getName().equals(ChirdlUtilConstants.STATE_EXPORT_VITALS)){
				
				//Concept map files will be eliminated in a future update
				LocationTagAttributeValue  conceptMapLocationTagAttrValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, VITALS_CONCEPT_MAP_LOCATION, locationId);
				if (conceptMapLocationTagAttrValue == null) {
					Log.error("Location tag attribute for vitals concept map location is null. " + 
							"Check location tag attribute " + VITALS_CONCEPT_MAP_LOCATION + " for location: " +
							locationId);
					return;
				}
				saveExport(encounterId, sessionId, conceptMapLocationTagAttrValue );
				return;	
				
			}
			
			//export the observations scanned from PWS 
			if (currState.getName().equals(ChirdlUtilConstants.STATE_EXPORT_POC)) {
				
				//Concept map files will be eliminated in a future update
				LocationTagAttributeValue  conceptMapLocationTagAttrValue = 
						chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, POC_CONCEPT_MAP_LOCATION, 
							locationId);
				if (conceptMapLocationTagAttrValue == null) {
					Log.error("Location tag attribute for POC observation concept map location is null. " + 
							"Check location tag attribute " + POC_CONCEPT_MAP_LOCATION + " for location: " +
							locationId);
					return;
				};
				saveExport(encounterId, sessionId, conceptMapLocationTagAttrValue );
	
				//Do not return yet, because a PWS may need to be exported next.
				
			}
			
			//Export form image (PWS or JIT)
			Integer formId = patientState.getFormId();
			Form form = formService.getForm(formId);
			
			//Check form attribute to determine if form should be exported
			FormAttributeValue exportAttrValue = 
					chirdlutilbackportsService.getFormAttributeValue(formId, FORM_ATTRIBUTE_EXPORTABLE, locationTagId, locationId);
			
			if (exportAttrValue == null){
				//do not export
				return;
			}
			
			String exportable = exportAttrValue.getValue();
			
			if (exportable!= null && exportable.trim().equalsIgnoreCase(ChirdlUtilConstants.GENERAL_INFO_TRUE)){						
				
				//Concept map files will be eliminated in a future update
				LocationTagAttributeValue  tiffLocationTagConceptMapLocation = 
						chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, form.getName() + TIFF_CONCEPT_MAP_LOCATION, 
								locationId);
				if (tiffLocationTagConceptMapLocation == null){
					Log.error("Location tag attribute for tiff concept map location is null. " + 
							"Check location tag attribute " + form.getName() + TIFF_CONCEPT_MAP_LOCATION + " for location: " +
							locationId);
					return;
				}
				
				saveExport(encounterId, sessionId, tiffLocationTagConceptMapLocation );
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
	
	private void saveExport(Integer encounterId, Integer sessionId, LocationTagAttributeValue tagValue) {
		
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
