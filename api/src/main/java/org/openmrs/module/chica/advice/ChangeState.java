/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.hibernate.Hibernate;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tmdugan
 * 
 */
public class ChangeState implements ChirdlRunnable
{
	private static final Logger log = LoggerFactory.getLogger(ChangeState.class);

	private PatientState patientState = null;
	private HashMap<String, Object> parameters = null;
	private Integer patientId = null;
	private String stateName = null;

	public ChangeState(PatientState patientState, HashMap<String, Object> parameters)
	{
		//set these values in the constructor instead of the 
		//run method of the thread to prevent crossthreading
		//from corrupting the patientState
		this.patientState = patientState;
		this.parameters = parameters;
		Hibernate.initialize(this.patientState);
		Hibernate.initialize(this.patientState.getState());
		Hibernate.initialize(this.patientState.getState().getAction());
		Hibernate.initialize(this.patientState.getPatient());
		this.patientId = this.patientState.getPatientId();
		if (this.patientState.getState() != null) {
			this.stateName = this.patientState.getState().getName();
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
		log.info("Started execution of {} ({}, {})", getName(), Thread.currentThread().getName(), new Timestamp(new Date().getTime()));
		try
		{
			BaseStateActionHandler.getInstance().changeState(this.patientState,
					this.parameters);
		} 
		catch (Exception e)
		{
			log.error("Exception processing changeState", e);
		} 
		finally
		{
			log.info("Finished execution of {} ({}, {})", getName(), Thread.currentThread().getName(), new Timestamp(new Date().getTime()));
		}
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
	 */
    @Override
	public String getName() {
	    return "Change State (State: " + this.stateName + " Patient: " + this.patientId + " Patient State: " + 
	    	this.patientState.getPatientStateId() + ")";
    }

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
	 */
    @Override
	public int getPriority() {
    	return ChirdlRunnable.PRIORITY_ONE;
    }
}
