/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.HashMap;

import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;

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
		FormInstance formInstance = (FormInstance) parameters.get(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formInstance.getFormId());
		String formName = form.getName();
		
		//only void non-question related obs for PSF
		String formType = org.openmrs.module.chica.util.Util.getFormType(formInstance.getFormId(), (Integer) parameters.get(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID), formInstance.getLocationId());
		if (formName != null && ChirdlUtilConstants.PATIENT_FORM_TYPE.equalsIgnoreCase(formType))
		{
			//make sure the processAction from the super
			//class does not void any Obs since we already
			//voided them
			parameters.put("voidObs", new Boolean(false));
		} 
		
		super.processAction(stateAction,patient,patientState,parameters);
	}
}
