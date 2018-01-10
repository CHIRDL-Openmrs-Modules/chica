package org.openmrs.module.chica.advice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.chica.ImmunizationForecastLookup;
import org.openmrs.module.chirdlutil.threadmgmt.ThreadManager;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.datasource.ObsInMemoryDatasource;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.springframework.aop.AfterReturningAdvice;

/**
 * Triggers code in this module when certain other methods are called.
 * 
 * @author Tammy Dugan
 * 
 */
public class TriggerPatientAfterAdvice implements AfterReturningAdvice
{
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable
	{
		if (method.getName().equals("messageProcessed"))
		{
			try
			{
				if (method.getParameterTypes()[0].getName().compareTo(
						"org.openmrs.Encounter") == 0)
				{
					AdministrationService adminService = Context.getAdministrationService();
					org.openmrs.Encounter encounter = (org.openmrs.Encounter) args[0];
					
					ThreadManager threadManager = ThreadManager.getInstance();
					Location location = encounter.getLocation();
					//spawn the checkin thread
					threadManager.execute(new CheckinPatient(encounter.getEncounterId()), location.getLocationId());
					
					String executeImmunization = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_IMMUNIZATION_QUERY_ACTIVATED);
					if (ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(executeImmunization)) {
						//spawn the immunization query thread
						Thread immunThread = new Thread(new QueryImmunizationForecast(encounter.getEncounterId()));
						immunThread.start();
						//ImmunizationForecastLookup.queryImmunizationList(encounter, true);
					}
				}
			} catch (Exception e)
			{
				this.log.error(e.getMessage());
				this.log.error(org.openmrs.module.chirdlutil.util.Util
						.getStackTrace(e));
			}
		}
		else if (method.getName().equals("fileProcessed"))
		{
			try
			{
				if (method.getParameterTypes()[0].getName().compareTo("org.openmrs.module.atd.TeleformFileState") == 0) 
				{
					TeleformFileState tfState = (TeleformFileState) args[0];
					processState(tfState);
				}
				else if (method.getParameterTypes()[0].getName().compareTo("java.util.ArrayList") == 0)
				{
					ArrayList<TeleformFileState> tfStates = (ArrayList<TeleformFileState>) args[0];
					
					TeleformFileState tfState = null;
					Iterator<TeleformFileState> iter = tfStates.iterator();
					if(tfStates.size() > 0)
					{
						log.info("!!!! FOUND TF STATES to PROCESS!!!");
						while(iter.hasNext())
						{
							tfState = iter.next();
							processState(tfState);
						}
					}
				} 
			} catch (Exception e)
			{
				this.log.error(e.getMessage());
				this.log.error(org.openmrs.module.chirdlutil.util.Util
						.getStackTrace(e));
			}
		}
		else if(method.getName().equals("cleanCache")) 
		{
            log.info("clear regenObs and medicationList");
            ((ObsInMemoryDatasource) Context.getLogicService().getLogicDataSource(ChirdlUtilConstants.DATA_SOURCE_IN_MEMORY)).clearObs();
            ImmunizationForecastLookup.clearimmunizationLists();
        }
	}

	private void processState(TeleformFileState tfState) 
	{
		if (tfState == null) 
		{
			return;
		}
		
		ThreadManager threadManager = ThreadManager.getInstance();
		Map<String, Object> parameters = tfState.getParameters();
		Integer locationId = -1;
		if (parameters != null) 
		{
			PatientState patientState = (PatientState) parameters.get("patientState");
			locationId = patientState.getLocationId();
		}
		
		// Flush the session so the contents will be available for the thread.
		Context.flushSession();
		threadManager.execute(new ProcessFile(tfState), locationId);
	}
}