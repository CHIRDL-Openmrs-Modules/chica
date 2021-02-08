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
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;

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
			this.patientState = (PatientState) parameters.get("patientState");
			Hibernate.initialize(this.patientState);
			Hibernate.initialize(this.patientState.getState());
			Hibernate.initialize(this.patientState.getState().getAction());
			Hibernate.initialize(this.patientState.getPatient());
			this.patientId = this.patientState.getPatientId();
			if (this.patientState.getState() != null) {
				this.stateName = this.patientState.getState().getName();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */

	@Override
	public void run()
	{
		this.log.info("Started execution of " + getName() + "("+ Thread.currentThread().getName() + ", " + 
			new Timestamp(new Date().getTime()) + ")");
		try
		{
			HashMap<String, Object> stateParameters = this.patientState
					.getParameters();
			if (stateParameters == null)
			{
				stateParameters = new HashMap<>();
			}
			stateParameters.put("filename", this.filename);
			stateParameters.put("formInstance", this.formInstance);
	
			BaseStateActionHandler.getInstance().changeState(this.patientState,
					stateParameters);
		} 
		catch (Exception e)
		{
			this.log.error("Error processing file", e);
		} 
		finally
		{
			this.log.info("Finished execution of " + getName() + "("+ Thread.currentThread().getName() + ", " + 
				new Timestamp(new Date().getTime()) + ")");
		}
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
	 */
    @Override
	public String getName() {
	    return "Process File (State: " + this.stateName + " Patient: " + this.patientId + " Patient State: " + 
	    	this.patientState.getPatientStateId() + ")";
    }

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
	 */
    @Override
	public int getPriority() {
    	if ("PWS_wait_to_scan".equalsIgnoreCase(this.stateName) || 
    			"PWS_process".equalsIgnoreCase(this.stateName) || 
    			"PWS_rescan".equalsIgnoreCase(this.stateName)) {
    		return ChirdlRunnable.PRIORITY_FOUR;
    	}
    	
	    return ChirdlRunnable.PRIORITY_ONE;
    }
}
