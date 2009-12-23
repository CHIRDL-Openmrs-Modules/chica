package org.openmrs.module.chica.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.Util;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class ViewEncounterController extends SimpleFormController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject
	 * (javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		return "testing";
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		String optionsString = request.getParameter("options");
		String patientIdString = request.getParameter("patientId");

		Integer patientId = null;

		try {
			if (patientIdString != null) {
				patientId = Integer.parseInt(patientIdString);
			}
		} catch (Exception e) {
		}

		String sessionIdString = request.getParameter("sessionId");
		String encounterIdString = request.getParameter("encounterId");
		Integer sessionId = null;
		Integer encounterId = null;
		try {
			if (sessionIdString != null && !sessionIdString.equals("")) {
				sessionId = Integer.parseInt(sessionIdString);
			}
			if (encounterIdString != null && !encounterIdString.equals("")) {
				encounterId = Integer.parseInt(encounterIdString);
			}
		} catch (Exception e) {
		}

		if (optionsString != null) {

			if (patientId != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("encounterId", encounterId);
				map.put("sessionId", sessionId);
				map.put("psfFormInstanceId", request.getParameter("psfFormInstanceId"));
				map.put("pwsFormInstanceId", request.getParameter("pwsFormInstanceId"));
				map.put("psfFormId", request.getParameter("psfFormId"));
				map.put("pwsFormId", request.getParameter("pwsFormId"));
				map.put("psfLocationId", request.getParameter("psfLocationId"));
				map.put("pwsLocationId", request.getParameter("pwsLocationId"));
				map.put("patientId", patientIdString);
				return new ModelAndView(new RedirectView("displayTiff.form"),
						map);
			}

		}
		return new ModelAndView(new RedirectView("viewEncounter.form"));

	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		ATDService atdService = Context.getService(ATDService.class);
		ChicaService chicaService = Context.getService(ChicaService.class);
		PatientService patientService = Context
				.getService(PatientService.class);
		Map<String, Object> map = new HashMap<String, Object>();

		try {

			String pidparam = request.getParameter("patientId");

			if (pidparam == null || pidparam.length()==0) {
				return map;
			}
			Patient patient = patientService.getPatient(Integer
					.valueOf(pidparam));
			if (patient == null) {
				return map;
			}

			// title name, mrn, and dob
			String dobString = "";
			Date dob = patient.getBirthdate();
			if (dob != null) {
				dobString = new SimpleDateFormat("yyyy-MM-dd").format(dob);
			}
			map.put("titleMRN", patient.getPatientIdentifier());
			map.put("titleLastName", patient.getFamilyName());
			map.put("titleFirstName", patient.getGivenName());
			map.put("titleDOB", dobString);

			// encounter rows
			EncounterService encounterService = Context
					.getService(EncounterService.class);
			List<org.openmrs.Encounter> list = encounterService
					.getEncountersByPatientId(Integer.valueOf(pidparam));
			List<PatientRow> rows = new ArrayList<PatientRow>();

			for (org.openmrs.Encounter enc : list) {
				Integer encounterId = enc.getEncounterId();
				PatientRow row = new PatientRow();
				Encounter enct = (Encounter) encounterService
						.getEncounter(encounterId);
				if (enct == null)
					continue;

				String apptDateString = "";
				Date appt = enct.getScheduledTime();
				if (appt != null) {
					apptDateString = new SimpleDateFormat("MMM dd, yyyy")
							.format(appt);
				}

				// Set checkin date text
				Date checkin = enct.getEncounterDatetime();

				String checkinDateString = "";

				if (checkin != null) {
					if (Util.isToday(checkin)) {
						checkinDateString = "Today";
					} else {
						checkinDateString = new SimpleDateFormat("MMM dd, yyyy")
								.format(checkin);
					}
				}

				// Calculate age at visit
				User provider = enct.getProvider();
				String providerName = getProviderName(provider);
				String station = enct.getPrinterLocation();
				String firstName = enct.getPatient().getGivenName();
				Patient pat = enct.getPatient();
				if (pat != null) {
					String lastName = pat.getFamilyName();
					row.setLastName(lastName);
					PatientIdentifier pi = pat.getPatientIdentifier();
					if (pi != null) {
						String mrn = pi.getIdentifier();
						row.setMrn(mrn);
					}
				}

				row.setFirstName(firstName);
				row.setMdName(providerName);
				row.setAppointment(apptDateString);
				row.setCheckin(checkinDateString);
				row.setEncounter(enct);
				row.setStation(station);
				row.setAgeAtVisit(org.openmrs.module.chica.util.Util
						.adjustAgeUnits(dob, checkin));

				// psf, pws form ids
				
				
				List<PatientState> psfPatientStates = atdService.getPatientStatesWithFormInstances("PSF", encounterId);
				if (psfPatientStates != null && !psfPatientStates.isEmpty()){
					
					for(PatientState currState:psfPatientStates){
						if(currState.getEndTime()!=null){
							row.setPsfId(currState.getFormInstance());
							break;
						}
					}
				}
				
				List<PatientState> pwsPatientStates = atdService.getPatientStatesWithFormInstances("PWS", encounterId);
				if (pwsPatientStates != null && !pwsPatientStates.isEmpty()){
					
					for(PatientState currState:pwsPatientStates){
						if(currState.getEndTime()!=null){
							row.setPwsId(currState.getFormInstance());
							break;
						}
					}
				}
				
				Chica1Appointment chica1Appt = chicaService
					.getChica1AppointmentByEncounterId(encounterId);
				
				if (chica1Appt != null)
				{
					Integer psfId = chica1Appt.getApptPsfId();
					Integer pwsId = chica1Appt.getApptPwsId();
					Integer locationId = enct.getLocation().getLocationId();
					
					FormService formService = Context.getFormService();
					
					if(psfId != null){
						Form form = formService.getForms("PSF",null,null,false,null,null,null).get(0);
						Integer formId = null;
						if (form != null)
						{
							formId = form.getFormId();
						}
						FormInstance psfFormInstance = new FormInstance();
						psfFormInstance.setFormInstanceId(psfId);
						psfFormInstance.setFormId(formId);
						psfFormInstance.setLocationId(locationId);
						row.setPsfId(psfFormInstance);
					}
					
					if(pwsId != null){
						Form form = formService.getForms("PWS",null,null,false,null,null,null).get(0);
						Integer formId = null;
						if (form != null)
						{
							formId = form.getFormId();
						}
						FormInstance pwsFormInstance = new FormInstance();
						pwsFormInstance.setFormInstanceId(pwsId);
						pwsFormInstance.setFormId(formId);
						pwsFormInstance.setLocationId(locationId);
						row.setPwsId(pwsFormInstance);
					}
				}

				// From the encounter, get the obs for weight percentile

				String result = searchEncounterForObs(enct, "WTCENTILE");
				row.setWeightPercentile(result);

				result = searchEncounterForObs(enct, "HTCENTILE");
				row.setHeightPercentile(result);

				rows.add(row);

			}

			map.put("patientRows", rows);

		} catch (UnexpectedRollbackException ex) {
			// ignore this exception since it happens with an
			// APIAuthenticationException
		} catch (APIAuthenticationException ex2) {
			// ignore this exception. It happens during the redirect to the
			// login page
		}

		return map;
	}

	private String getProviderName(User prov) {
		String mdName = "";
		if (prov != null) {
			String firstInit = Util.toProperCase(prov.getGivenName());
			if (firstInit != null && firstInit.length() > 0) {
				firstInit = firstInit.substring(0, 1);
			} else {
				firstInit = "";
			}

			String middleInit = Util.toProperCase(prov.getMiddleName());
			if (middleInit != null && middleInit.length() > 0) {
				middleInit = middleInit.substring(0, 1);
			} else {
				middleInit = "";
			}
			if (firstInit != null && firstInit.length() > 0) {
				mdName += firstInit + ".";
				if (middleInit != null && middleInit.length() > 0) {
					mdName += " " + middleInit + ".";
				}
			}
			if (mdName.length() > 0) {
				mdName += " ";
			}
			String familyName = Util.toProperCase(prov.getFamilyName());
			if (familyName == null) {
				familyName = "";
			}
			mdName += familyName;

		}

		return mdName;

	}

	private String searchEncounterForObs(Encounter enc, String conceptName) {
		String name = "N/A";

		Set<Obs> allObs = enc.getObs();
		ConceptService conceptService = Context.getConceptService();
		if (allObs != null) {
			for (Obs obs : allObs) {
				Concept obsConcept = obs.getConcept();
				Concept wtcentileConcept = conceptService
						.getConceptByName(conceptName);
				if (obsConcept != null
						&& wtcentileConcept != null
						&& wtcentileConcept.getConceptId().equals(obsConcept
								.getConceptId())) {
					Double wtcentile = obs.getValueNumeric();
					if (wtcentile != null) {
						name = wtcentile.toString();
					}
				}
			}
		}
		return name;
	}

}
