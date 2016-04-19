package org.openmrs.module.chica.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class GreaseBoardController extends SimpleFormController
{
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private static int numRefreshes = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception
	{
		return "testing";
	}

	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception
	{
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
	
		String optionsString = request.getParameter("options");
		
		String patientIdString = request.getParameter("greaseBoardPatientId");
		Integer patientId = null;
		try
		{
			if(patientIdString != null){
				patientId = Integer.parseInt(patientIdString);
			}
		} catch (Exception e)
		{
		}
		String sessionIdString = request.getParameter("greaseBoardSessionId");
		Integer sessionId = null;
		try
		{
			if(sessionIdString != null){
				sessionId = Integer.parseInt(sessionIdString);
			}
		} catch (Exception e)
		{
		}
		
		//Initiate an ADHD WU for the patient
		if (optionsString != null && optionsString.equalsIgnoreCase("ADHD WU")) {
			PatientService patientService = Context.getPatientService();
			Patient patient = patientService.getPatient(patientId);
			ConceptService conceptService = Context.getConceptService();
			Concept currConcept = conceptService.getConceptByName("CHICA_ADHD_SX");
			Session session = chirdlutilbackportsService.getSession(sessionId);
			Integer encounterId = session.getEncounterId();
			org.openmrs.module.chirdlutil.util.Util.saveObs(patient, currConcept, encounterId, 
				"workup_initiated",new Date());
			
			LogicService logicService = Context.getLogicService();
			
			//print the ADHD parent form
			User user = Context.getUserContext().getAuthenticatedUser();
			String locationString = user.getUserProperty("location");
			String locationTags = user.getUserProperty("locationTags");
			LocationService locationService = Context.getLocationService();
			
			Integer locationId = null;
			Location location = null;
			Integer locationTagId = null;
			if (locationString != null) {
				location = locationService.getLocation(locationString);
				if (location != null) {
					locationId = location.getLocationId();
					
					if (locationTags != null) {
						StringTokenizer tokenizer = new StringTokenizer(locationTags, ",");
						while (tokenizer.hasMoreTokens()) {
							String locationTagName = tokenizer.nextToken();
							locationTagName = locationTagName.trim();
							Set<LocationTag> tags = location.getTags();
							for (LocationTag tag : tags) {
								if (tag.getName().equalsIgnoreCase(locationTagName)) {
									locationTagId = tag.getLocationTagId();
								}
							}
						}
					}
					
				}
			}
			
			//print the ADHD parent form
			Map<String, Object> parameters = new HashMap<String,Object>();
			parameters.put("sessionId",sessionId);
			parameters.put("locationTagId",locationTagId); 
			FormInstance formInstance = new FormInstance();
			formInstance.setLocationId(locationId);
			parameters.put("formInstance",formInstance);
			parameters.put("param1","ADHD P");
			logicService.eval(patientId, "CREATE_JIT",parameters);
			
			//print the ADHD Spanish parent form
			parameters = new HashMap<String,Object>();
			parameters.put("sessionId",sessionId);
			parameters.put("locationTagId",locationTagId); 
			formInstance = new FormInstance();
			formInstance.setLocationId(locationId);
			parameters.put("formInstance",formInstance);
			parameters.put("param1","ADHD PS");
			logicService.eval(patientId, "CREATE_JIT",parameters);
			
			//print the ADHD teacher form
			parameters = new HashMap<String,Object>();
			parameters.put("sessionId",sessionId);
			parameters.put("locationTagId",locationTagId); 
			formInstance = new FormInstance();
			formInstance.setLocationId(locationId);
			parameters.put("formInstance",formInstance);
			parameters.put("param1","ADHD T");
			logicService.eval(patientId, "CREATE_JIT",parameters);
		}

		if (optionsString != null && (optionsString.equalsIgnoreCase("Print PSF")||
				optionsString.equalsIgnoreCase("Print PWS")))
		{
			String formName = optionsString.replaceAll("Print", "");
			formName = formName.trim();
			

			if (patientId != null && sessionId != null && formName != null)
			{
				FormService formService = Context.getFormService();
				Session session = chirdlutilbackportsService.getSession(sessionId);
				Integer encounterId = session.getEncounterId();
				
				Form form = formService.getForm(formName);
				Integer formId = null;
				if (form != null)
				{
					formId = form.getFormId();
				}
				
				PatientState patientStateProduce = 
					org.openmrs.module.atd.util.Util.getProducePatientStateByEncounterFormAction(encounterId, formId);

				State currState = null;

				
				//Don't generate a PSF with the print button on the greaseboard
				//We don't ever want more than one unique PSF
				
				String stateName = null;
				
				if(formName.equalsIgnoreCase("PSF")||
						formName.equalsIgnoreCase("PWS"))
				{
					stateName = formName+"_reprint";
				}
				
				if (formName.equalsIgnoreCase("PSF"))
				{
					currState = chirdlutilbackportsService
							.getStateByName(stateName);
				} else
				{
					// reprint if the state exists
					if (patientStateProduce != null)
					{
						currState = chirdlutilbackportsService.getStateByName(stateName);
					} else
					{
						// create for the first time if it does not exist
						currState = chirdlutilbackportsService.getStateByName("QUERY KITE "
								+ formName);
					}
				}
				
				HashMap<String,Object> actionParameters = new HashMap<String,Object>();
				actionParameters.put("formName", formName);

				if (currState != null)
				{
					PatientState patientState = chirdlutilbackportsService.getLastPatientState(sessionId);
					PatientService patientService = Context.getPatientService();
					Patient patient = patientService.getPatient(patientId);

					StateManager.runState(patient, sessionId, currState,actionParameters,
							patientState.getLocationTagId(),
							patientState.getLocationId(),
							BaseStateActionHandler.getInstance());
				}
			}
			
			return new ModelAndView(new RedirectView("greaseBoard.form"));
		}
		
		return new ModelAndView(new RedirectView("greaseBoard.form"));
		
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception
	{
		User user = Context.getUserContext().getAuthenticatedUser();
		if(user == null) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("refreshPeriod", Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_GREASEBOARD_REFRESH));
		map.put("showManualCheckin", Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_ENABLE_MANUAL_CHECKIN));
		map.put("currentUser", user.getUsername());
		
		numRefreshes++;
		if(numRefreshes%100==0){
			Context.clearSession();
		}
		
		return map;
	}
}
