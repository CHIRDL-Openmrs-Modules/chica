package org.openmrs.module.chica.web;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class ForcePrintJITsController extends SimpleFormController {
	
	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		PatientService patientService = Context.getPatientService();
		String patientIdString = request.getParameter("patientId");
		String resultMessage = request.getParameter("resultMessage");
		Integer patientId = null;
		try {
			if (patientIdString != null) {
				patientId = Integer.parseInt(patientIdString);
			}
		}
		catch (Exception e) {}
		String sessionIdString = request.getParameter("sessionId");
		Integer sessionId = null;
		try {
			if (sessionIdString != null) {
				sessionId = Integer.parseInt(sessionIdString);
			}
		}
		catch (Exception e) {}
		
		Map<String, Object> map = new HashMap<String, Object>();
		
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
							if (tag.getTag().equalsIgnoreCase(locationTagName)) {
								locationTagId = tag.getLocationTagId();
							}
						}
					}
				}
				
			}
		}
		
		
		Patient patient = patientService.getPatient(patientId);
		
		FormService formService = Context.getFormService();
		Set<FormDisplay> printableJits = new TreeSet<FormDisplay>();
		List<FormAttributeValue> attributes = chirdlutilbackportsService.getFormAttributesByName("forcePrintable");
		
		Date birthdate = patient.getBirthdate();
		Date today = new Date();
		
		for (FormAttributeValue attribute : attributes) {
			if (attribute.getValue().equalsIgnoreCase("true") && attribute.getLocationId().equals(locationId)) {
				Form form = formService.getForm(attribute.getFormId());
				Integer formId = form.getFormId();
				if (!form.getRetired()) {
					FormDisplay formDisplay = new FormDisplay();
					formDisplay.setFormName(form.getName());
					formDisplay.setFormId(form.getFormId());
					FormAttributeValue attributeValue = chirdlutilbackportsService.getFormAttributeValue(formId, "displayName",
					    locationTagId, locationId);
					if (attributeValue == null || attributeValue.getValue() == null) {
						formDisplay.setDisplayName(form.getName());
					} else {
						formDisplay.setDisplayName(attributeValue.getValue());
					}
					
					FormAttributeValue ageMin = chirdlutilbackportsService.getFormAttributeValue(formId, "ageMin",
							locationTagId, locationId);
					FormAttributeValue ageMinUnits = chirdlutilbackportsService.getFormAttributeValue(formId, "ageMinUnits",
							locationTagId, locationId);
					FormAttributeValue ageMax = chirdlutilbackportsService.getFormAttributeValue(formId, "ageMax",
							locationTagId, locationId);
					FormAttributeValue ageMaxUnits = chirdlutilbackportsService.getFormAttributeValue(formId, "ageMaxUnits",
							locationTagId, locationId);

					if(ageMin!=null && ageMin.getValue()!=null && ageMinUnits!=null && ageMinUnits.getValue()!=null &&
							ageMax!=null && ageMax.getValue()!=null && ageMaxUnits!=null && ageMaxUnits.getValue()!=null){
						Integer ageWithMinUnits = Util.getAgeInUnits(birthdate, today, ageMinUnits.getValue());
						Integer ageWithMaxUnits = Util.getAgeInUnits(birthdate, today, ageMaxUnits.getValue());
						try{

							if(ageWithMinUnits.intValue()<Integer.parseInt(ageMin.getValue())){
								continue;
							}
							if(ageWithMaxUnits.intValue()>= Integer.parseInt(ageMax.getValue())){
								continue;
							}
						}
						catch(NumberFormatException e){
							log.error("Error with age restrictions for form: "+form.getName()+
								". Values are ageMin "+ageWithMinUnits+" and ageMax "+ageWithMaxUnits);
							//if error in age check, display form in printable JITs
						}
					}
					printableJits.add(formDisplay);
				}
			}
		}
		
		String familyName = patient.getPersonName().getFamilyName();
		String givenName = patient.getPersonName().getGivenName();
		String patientName = givenName + " " + familyName;
		
		map.put("printableJits", printableJits);
		map.put("patientId", patientId);
		map.put("sessionId", sessionId);
		map.put("resultMessage", resultMessage);
		map.put("patientName", patientName);
		return map;
	}
	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
	                                BindException errors) throws Exception {
		String optionsString = request.getParameter("options");

		String patientIdString = request.getParameter("patientId");
		Integer patientId = null;
		try {
			if (patientIdString != null) {
				patientId = Integer.parseInt(patientIdString);
			}
		}
		catch (Exception e) {}
		String sessionIdString = request.getParameter("sessionId");
		Integer sessionId = null;
		try {
			if (sessionIdString != null) {
				sessionId = Integer.parseInt(sessionIdString);
			}
		}
		catch (Exception e) {}
		
		LogicService logicService = Context.getLogicService();
		
		//print the form
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
							if (tag.getTag().equalsIgnoreCase(locationTagName)) {
								locationTagId = tag.getLocationTagId();
							}
						}
					}
				}
				
			}
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		FormService formService = Context.getFormService();
		
		parameters = new HashMap<String, Object>();
		parameters.put("sessionId", sessionId);
		parameters.put("locationTagId", locationTagId);
		FormInstance formInstance = new FormInstance();
		formInstance.setLocationId(locationId);
		parameters.put("formInstance", formInstance);
		String formName = null;
		Form form = null;
		
		//print the form
		String formIdString = optionsString;
		Integer formId = null;
		try {
			if (formIdString != null) {
				formId = Integer.parseInt(formIdString);
			}
		}
		catch (Exception e) {}
		form = formService.getForm(formId);
		formName = form.getName();
		parameters.put("param1", formName);
		parameters.put("param2", "forcePrint");
		logicService.eval(patientId, "CREATE_JIT", parameters);

		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("patientId", patientId);
		map.put("sessionId", sessionId);

		FormAttributeValue attributeValue = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(),
		    "displayName", locationTagId, locationId);
		if (attributeValue != null && attributeValue.getValue() != null && attributeValue.getValue().length() > 0) {
			formName = attributeValue.getValue();
		}
		
		String resultMessage = formName + " successfully sent to the printer.";
		map.put("resultMessage", resultMessage);
		
		return new ModelAndView(new RedirectView("forcePrintJITs.form"), map);
		
	}
}
