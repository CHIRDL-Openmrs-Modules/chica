/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.util.Util;

/**
 * @author tmdugan
 * 
 */
public class ProcessFile implements ChirdlRunnable
{
	private Log log = LogFactory.getLog(this.getClass());

	private PatientState patientState = null;
	private String filename = null;
	private FormInstance formInstance = null;
	private Integer patientId = null;
	private String stateName = null;

	public ProcessFile(TeleformFileState tfState)
	{
		//set these values in the constructor instead of the 
		//run method of the thread to prevent crossthreading
		//from corrupting the patientState
		this.filename = tfState.getFullFilePath();
		this.formInstance = tfState.getFormInstance();
		Map<String, Object> parameters = tfState.getParameters();
		if (parameters != null)
		{
			patientState = (PatientState) parameters.get("patientState");
			Hibernate.initialize(patientState);
			Hibernate.initialize(patientState.getState());
			Hibernate.initialize(patientState.getState().getAction());
			Hibernate.initialize(patientState.getPatient());
			this.patientId = patientState.getPatientId();
			if (patientState.getState() != null) {
				this.stateName = patientState.getState().getName();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */

	public void run()
	{
		log.info("Started execution of " + getName() + "("+ Thread.currentThread().getName() + ", " + 
			new Timestamp(new Date().getTime()) + ")");
		Context.openSession();
		try
		{
            
            Context.authenticate(Util.decryptGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME),
                    Util.decryptGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSPHRASE));

			HashMap<String, Object> stateParameters = patientState
					.getParameters();
			if (stateParameters == null)
			{
				stateParameters = new HashMap<String, Object>();
			}
			stateParameters.put("filename", this.filename);
			stateParameters.put("formInstance", this.formInstance);
	
			BaseStateActionHandler.getInstance().changeState(patientState,
					stateParameters);
		} 
		catch (Exception e)
		{
			log.error("Error processing file", e);
		} 
		finally
		{
			Context.closeSession();
			log.info("Finished execution of " + getName() + "("+ Thread.currentThread().getName() + ", " + 
				new Timestamp(new Date().getTime()) + ")");
		}
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
	 */
    public String getName() {
	    return "Process File (State: " + stateName + " Patient: " + patientId + " Patient State: " + 
	    	patientState.getPatientStateId() + ")";
    }

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
	 */
    public int getPriority() {
    	if ("PWS_wait_to_scan".equalsIgnoreCase(stateName) || 
    			"PWS_process".equalsIgnoreCase(stateName) || 
    			"PWS_rescan".equalsIgnoreCase(stateName)) {
    		return ChirdlRunnable.PRIORITY_FOUR;
    	}
    	
	    return ChirdlRunnable.PRIORITY_ONE;
    }
}
