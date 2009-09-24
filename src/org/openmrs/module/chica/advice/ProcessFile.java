/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.ChicaStateActionHandler;

/**
 * @author tmdugan
 * 
 */
public class ProcessFile implements Runnable
{
	private Log log = LogFactory.getLog(this.getClass());

	private Integer patientStateId = null;
	private String filename = null;

	public ProcessFile(TeleformFileState tfState)
	{
		//set these values in the constructor instead of the 
		//run method of the thread to prevent crossthreading
		//from corrupting the patientState
		this.filename = tfState.getFullFilePath();
		Map<String, Object> parameters = tfState.getParameters();
		if (parameters != null)
		{
			PatientState patientState = (PatientState) parameters.get("patientState");
			this.patientStateId = patientState.getPatientStateId();
		}
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
			ATDService atdService = Context.getService(ATDService.class);
			//lookup the patient state to get a fresh copy (passing by reference
			//seems to be causing crossthreading errors)
			//this also prevents lazy initialization exceptions from the
			//object being passed across the aop call
			PatientState patientState = atdService.getPatientState(this.patientStateId);
			HashMap<String, Object> stateParameters = patientState
					.getParameters();
			if (stateParameters == null)
			{
				stateParameters = new HashMap<String, Object>();
			}
			stateParameters.put("filename", this.filename);
			ChicaStateActionHandler.getInstance().processState(patientState,
					stateParameters);
		} catch (Exception e)
		{
		}finally{
			Context.closeSession();
		}
	}
}
