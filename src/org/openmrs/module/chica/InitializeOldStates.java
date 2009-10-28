/**
 * 
 */
package org.openmrs.module.chica;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
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
			Integer processedStates = 0;

			LocationService locationService = Context.getLocationService();

			List<Location> locations = locationService.getAllLocations();
			
			for (Location location : locations)
			{
				Calendar todaysDate = Calendar.getInstance();
				todaysDate.set(Calendar.HOUR_OF_DAY, 0);
				todaysDate.set(Calendar.MINUTE, 0);
				todaysDate.set(Calendar.SECOND, 0);
				Date currDate = todaysDate.getTime();
				ATDService atdService = Context.getService(ATDService.class);

				Set<LocationTag> tags = location.getTags();
				
				if(tags != null){
				
					for(LocationTag tag:tags){
						
						Integer locationTagId = tag.getLocationTagId();
						Integer locationId = location.getLocationId();
						
				List<PatientState> unfinishedStatesToday = atdService
						.getUnfinishedPatientStatesAllPatients(null,locationTagId,locationId);

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
						if (stateAction!=null&&(stateAction.getActionName().equalsIgnoreCase(
								"CONSUME FORM INSTANCE")))
						{
								TeleformFileState teleformFileState = TeleformFileMonitor
										.addToPendingStatesWithoutFilename(
												currPatientState.getFormInstance());
								teleformFileState.addParameter("patientState",
									currPatientState);
						}
						HashMap<String,Object> parameters = new HashMap<String,Object>();
						parameters.put("formInstance", currPatientState.getFormInstance());
						handler.processAction(stateAction, patient,
								currPatientState, parameters);
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
			}
				}
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
