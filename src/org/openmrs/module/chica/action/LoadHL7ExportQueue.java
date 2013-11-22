/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportMap;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceAttributeValue;
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

	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		
		
		ChicaService chicaService = Context
				.getService(ChicaService.class);
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		
		Session session = chirdlutilbackportsService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		
		try {
			if (patientState.getState().getName().equals("Export POC")) {
				//TODO:add encounter to queue
				ChicaHL7Export pocExport = new ChicaHL7Export();
				pocExport.setDateInserted(new Date());
				pocExport.setEncounterId(encounterId);
				pocExport.setSessionId(sessionId);
				pocExport.setVoided(false);
				pocExport.setStatus(1);
				ChicaHL7ExportMap POCmap = new ChicaHL7ExportMap();
				LocationTagAttributeValue  POCTagValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, "POCConceptMapLocation", 
						locationId);
				if (POCTagValue != null && !POCTagValue.equals("")) {
					POCTagValue.getValue();
					ChicaHL7Export insertedExport = chicaService.insertEncounterToHL7ExportQueue(pocExport);
					POCmap.setValue(String.valueOf(POCTagValue.getLocationTagAttributeValueId()));
					POCmap.setHl7ExportQueueId(insertedExport.getQueueId());
					POCmap.setDateInserted(new Date());
					POCmap.setVoided(false);
					chicaService.saveHL7ExportMap(POCmap);
				}
				
				LocationTagAttributeValue  psftiffTagValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, "PSFTiffConceptMapLocation", 
						locationId);
				psftiffTagValue.getValue();
				
				if (psftiffTagValue != null && !psftiffTagValue.equals("")){
					// Find the form instance for the PSF for the encounter to see if it was populated using paper or not.  
					// If it was populated through some medium other than paper, there will be no tiff to export.
					boolean paperImage = true;
					List<PatientState> patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(
						"PSF", encounterId);
					FormInstance formInstance = null;
					if (patientStates != null) {
						Iterator<PatientState> psIterator = patientStates.iterator();
						while (psIterator.hasNext()) {
							formInstance = psIterator.next().getFormInstance();
							if (formInstance != null) {
								break;
							}
						}
					}
					if (formInstance != null) {
						Integer formId = formInstance.getFormId();
						Integer formInstanceId = formInstance.getFormInstanceId();
						Integer formLocationId = formInstance.getLocationId();
						FormInstanceAttributeValue fiav = chirdlutilbackportsService.getFormInstanceAttributeValue(
							formId, formInstanceId, formLocationId, "medium");
						if (fiav != null && !"paper".equalsIgnoreCase(fiav.getValue())) {
							paperImage = false;
						}
					}
					
					if (paperImage) {
						ChicaHL7ExportMap psftiffMap = new ChicaHL7ExportMap();
						ChicaHL7Export exportpsf = new ChicaHL7Export();
						exportpsf.setDateInserted(new Date());
						exportpsf.setEncounterId(encounterId);
						exportpsf.setSessionId(sessionId);
						exportpsf.setVoided(false);
						exportpsf.setStatus(1);
						ChicaHL7Export insertedExport = chicaService.insertEncounterToHL7ExportQueue(exportpsf);
						psftiffMap.setValue(String.valueOf(psftiffTagValue.getLocationTagAttributeValueId()));
						psftiffMap.setHl7ExportQueueId(insertedExport.getQueueId());
						psftiffMap.setDateInserted(new Date());
						psftiffMap.setVoided(false);
						chicaService.saveHL7ExportMap(psftiffMap);
					}
					
				}
				
				ChicaHL7ExportMap pwstiffMap = new ChicaHL7ExportMap();
				LocationTagAttributeValue  pwstiffTagValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, "PWSTiffConceptMapLocation", 
						locationId);
				pwstiffTagValue.getValue();
				
				if (pwstiffTagValue != null && !pwstiffTagValue.equals("")){
					ChicaHL7Export exportpws = new ChicaHL7Export();
					exportpws.setDateInserted(new Date());
					exportpws.setEncounterId(encounterId);
					exportpws.setSessionId(sessionId);
					exportpws.setVoided(false);
					exportpws.setStatus(1);
					ChicaHL7Export insertedExport = chicaService.insertEncounterToHL7ExportQueue(exportpws);
					pwstiffMap.setValue(String.valueOf(pwstiffTagValue.getLocationTagAttributeValueId()));
					pwstiffMap.setHl7ExportQueueId(insertedExport.getQueueId());
					pwstiffMap.setDateInserted(new Date());
					pwstiffMap.setVoided(false);
					chicaService.saveHL7ExportMap(pwstiffMap);
					
				}
				
			
				
				
			}
			if (patientState.getState().getName().equals("Export Vitals")){
				
				
				ChicaHL7ExportMap vitalsMap = new ChicaHL7ExportMap();
				LocationTagAttributeValue  tagValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, "VitalsConceptMapLocation", 
						locationId);
				if (tagValue != null && !tagValue.equals("")){
					//TODO:add encounter to queue
					ChicaHL7Export vitalsExport = new ChicaHL7Export();
					vitalsExport.setDateInserted(new Date());
					vitalsExport.setEncounterId(encounterId);
					vitalsExport.setSessionId(sessionId);
					vitalsExport.setVoided(false);
					vitalsExport.setStatus(1);
					ChicaHL7Export insertedExport = chicaService.insertEncounterToHL7ExportQueue(vitalsExport);
					tagValue.getValue();
					vitalsMap.setValue(String.valueOf(tagValue.getLocationTagAttributeValueId()));
					vitalsMap.setHl7ExportQueueId(insertedExport.getQueueId());
					vitalsMap.setDateInserted(new Date());
					vitalsMap.setVoided(false);
					chicaService.saveHL7ExportMap(vitalsMap);
					
				}
				
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

}
