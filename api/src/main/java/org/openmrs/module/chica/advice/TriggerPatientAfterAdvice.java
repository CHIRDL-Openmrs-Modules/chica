package org.openmrs.module.chica.advice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.datasource.ObsInMemoryDatasource;
import org.springframework.aop.AfterReturningAdvice;

/**
 * Triggers code in this module when certain other methods are called.
 * 
 * @author Tammy Dugan
 * 
 */
public class TriggerPatientAfterAdvice implements AfterReturningAdvice
{
	private static final Logger log = LoggerFactory.getLogger(TriggerPatientAfterAdvice.class);

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
					org.openmrs.Encounter encounter = (org.openmrs.Encounter) args[0];
					
					//spawn the checkin thread
					Runnable checkin = new CheckinPatient(encounter.getEncounterId());
					Daemon.runInDaemonThread(checkin, org.openmrs.module.chica.util.Util.getDaemonToken());
				}
			} catch (Exception e)
			{
				log.error("Exception during patient checkin after hl7 message processing.", e);
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
				log.error("Exception after processing teleform fil.e",e);
			}
		}
		else if(method.getName().equals("cleanCache")) 
		{
            log.info("Clear regenObs and medicationList.");
            ((ObsInMemoryDatasource) Context.getLogicService().getLogicDataSource(ChirdlUtilConstants.DATA_SOURCE_IN_MEMORY)).clearObs();

        }
	}

	private void processState(TeleformFileState tfState) 
	{
		if (tfState == null) 
		{
			return;
		}
		
		// Flush the session so the contents will be available for the thread.
		Context.flushSession();
		Daemon.runInDaemonThread(new ProcessFile(tfState), org.openmrs.module.chica.util.Util.getDaemonToken());
	}
}