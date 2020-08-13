/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.randomizer.Randomizer;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan
 *
 */
public class Randomize implements ProcessStateAction
{
	private static Log log = LogFactory.getLog(Randomize.class);

	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		ChicaService chicaService = Context
				.getService(ChicaService.class);
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		
		Session session = chirdlutilbackportsService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		List<Study> activeStudies = chicaService.getActiveStudies();

		for (Study currActiveStudy : activeStudies)
		{
			StudyAttributeValue studyAttributeValue = chicaService
					.getStudyAttributeValue(currActiveStudy,
							"Custom Randomizer");

			Randomizer randomizer = null;

			if (studyAttributeValue != null)
			{
				String randomizerClassName = "org.openmrs.module.chica.randomizer."+
					studyAttributeValue.getValue();

				try
				{
					Class theClass = Class.forName(randomizerClassName);
					randomizer = (Randomizer) theClass.newInstance();
				} catch (Exception e)
				{
					log.error("Error creating custom randomizer: "
							+ randomizerClassName);
					log.error(e.getMessage());
					log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
				}
			} else
			{
				continue;
			}

			if (randomizer != null)
			{
				EncounterService encounterService = Context.getService(EncounterService.class);
				randomizer.randomize(currActiveStudy, patient, encounterService.getEncounter(encounterId));
			}
		}
		StateManager.endState(patientState);
		BaseStateActionHandler.changeState(patient, sessionId, currState,
				stateAction,parameters,locationTagId,locationId);

	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
