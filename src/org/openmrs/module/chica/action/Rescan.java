/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.HashMap;
import java.util.List;

import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan
 *
 */
public class Rescan extends org.openmrs.module.atd.action.Rescan
{

	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{	
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		ATDService atdService = Context.getService(ATDService.class);
		Integer sessionId = patientState.getSessionId();
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		Session session = chirdlutilbackportsService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formInstance.getFormId());
		String formName = form.getName();
		
		//only void non-question related obs for PSF
		if (formName != null && formName.equals("PSF"))
		{
			//make sure the processAction from the super
			//class does not void any Obs since we already
			//voided them
			parameters.put("voidObs", new Boolean(false));
		} 
		
		super.processAction(stateAction,patient,patientState,parameters);
	}
}
