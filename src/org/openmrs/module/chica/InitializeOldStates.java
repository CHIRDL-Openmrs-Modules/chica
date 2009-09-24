/**
 * 
 */
package org.openmrs.module.chica;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;

/**
 * @author tmdugan
 * 
 */
public class InitializeOldStates implements Runnable
{
	private Log log = LogFactory.getLog(this.getClass());

	public InitializeOldStates()
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		Context.openSession();
		try
		{
			AdministrationService adminService = Context
					.getAdministrationService();
			Context.authenticate(adminService
					.getGlobalProperty("scheduler.username"), adminService
					.getGlobalProperty("scheduler.password"));
			Calendar todaysDate = Calendar.getInstance();
			todaysDate.set(Calendar.HOUR_OF_DAY, 0);
			todaysDate.set(Calendar.MINUTE, 0);
			todaysDate.set(Calendar.SECOND, 0);
			Date currDate = todaysDate.getTime();
			ATDService atdService = Context.getService(ATDService.class);

			List<PatientState> unfinishedStatesToday = atdService
					.getUnfinishedPatientStatesAllPatients(null);

			Integer processedStates = 0;
			ChicaStateActionHandler handler = ChicaStateActionHandler
					.getInstance();

			for (PatientState currPatientState : unfinishedStatesToday)
			{
				if (currPatientState.getStartTime().compareTo(currDate) >= 0)
				{
					continue;
				}
				State state = currPatientState.getState();
				if (state != null)
				{
					StateAction stateAction = state.getAction();
					Patient patient = currPatientState.getPatient();
					
					try
					{
						if (stateAction!=null&&stateAction.getActionName().equalsIgnoreCase(
								"CONSUME FORM INSTANCE"))
						{
							PatientState prevProduceState = atdService
									.getPrevPatientStateByAction(
											currPatientState.getSessionId(),
											currPatientState
													.getPatientStateId(),
											"PRODUCE FORM INSTANCE");
							if (prevProduceState != null)
							{
								Integer formId = prevProduceState.getFormId();
								Integer formInstanceId = prevProduceState
										.getFormInstanceId();
								TeleformFileState teleformFileState = TeleformFileMonitor
										.addToPendingStatesWithoutFilename(
												formId, formInstanceId);
								teleformFileState.addParameter("patientState",
										currPatientState);
							} else
							{
								log
										.error("Patient State: "
												+ currPatientState
														.getPatientStateId()
												+ " with action: "
												+ stateAction.getActionName()
												+ " could not be processed. No previous produce state.");
							}
						}
						handler.processAction(stateAction, patient,
								currPatientState, null);
					} catch (Exception e)
					{
						log.error(e.getMessage());
						log.error(org.openmrs.module.dss.util.Util
								.getStackTrace(e));
					}
				}
				if (processedStates % 100 == 0)
				{
					this.log.info("Old states loaded: " + processedStates);
				}
				processedStates++;
			}
			this.log.info("Final number old states loaded: " + processedStates);
		} catch (Exception e)
		{
		} finally
		{
			Context.closeSession();
		}
	}

}
