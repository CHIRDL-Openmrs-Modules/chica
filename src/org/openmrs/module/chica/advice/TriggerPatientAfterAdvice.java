package org.openmrs.module.chica.advice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.chica.MedicationListLookup;
import org.openmrs.module.sockethl7listener.ProcessedMessagesManager;
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
					Thread thread = new Thread(new CheckinPatient(encounter));
					ThreadManager.startThread(thread);
					MedicationListLookup.queryMedicationList(encounter, false);
					ProcessedMessagesManager.encountersProcessed();
				}
			} catch (Exception e)
			{
				this.log.error(e.getMessage());
				this.log.error(org.openmrs.module.chirdlutil.util.Util
						.getStackTrace(e));
			}
		}

		if (method.getName().equals("fileProcessed"))
		{
			try
			{
				if (method.getParameterTypes()[0].getName().compareTo("java.util.ArrayList") == 0)
				{
					ArrayList<TeleformFileState> tfStates = (ArrayList<TeleformFileState>) args[0];
					
					TeleformFileState tfState = null;
					ArrayList<Thread> activeThreads = new ArrayList<Thread>();
					Iterator<TeleformFileState> iter = tfStates.iterator();
					if(tfStates.size() > 0)
					{
						log.info("!!!! FOUND TF STATES to PROCESS!!!");
						while(iter.hasNext())
						{
							tfState = iter.next();
							Thread thread = new Thread(new ProcessFile(tfState));
							thread.start();
							activeThreads.add(thread);
						}
						log.info("Waiting on threads to finish...");
						for(Thread activeThread:activeThreads){
							log.info("Waiting on thread: "+activeThread.getName()+" to finish...");
							while(activeThread.isAlive()){
								Thread.sleep(100);
							}
							log.info("Thread: "+activeThread.getName()+" finished.");
						}
						log.info("!!!! PROCESSED TF STATES DONE: " + tfStates.size());
						TeleformFileMonitor.statesProcessed();
						
					}
				}
			} catch (Exception e)
			{
				this.log.error(e.getMessage());
				this.log.error(org.openmrs.module.chirdlutil.util.Util
						.getStackTrace(e));
			}
		}
	}

}