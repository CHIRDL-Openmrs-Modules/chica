package org.openmrs.module.chica.advice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
					org.openmrs.Encounter encounter = (org.openmrs.Encounter) args[0];
					
					//spawn the checkin thread
					Runnable checkin = new CheckinPatient(encounter.getEncounterId());
					Daemon.runInDaemonThread(checkin, org.openmrs.module.chica.util.Util.getDaemonToken());
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
						this.log.info("!!!! FOUND TF STATES to PROCESS!!!");
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
            this.log.info("clear regenObs and medicationList");
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