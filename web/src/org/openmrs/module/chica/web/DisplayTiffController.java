package org.openmrs.module.chica.web;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.dss.util.IOUtil;
import org.openmrs.module.dss.util.Util;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class DisplayTiffController extends SimpleFormController {
	
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

		return new ModelAndView(new RedirectView("displayTiff.form"));

	}

	private Integer parseString(String idString){
		
		Integer id = null;
		
		if (idString != null && idString.length() > 0)
		{
			try
			{
				id =  Integer.parseInt(idString);
			} catch (Exception e)
			{
			}
		}
		return id;
	}
	
	/* @param request 
	 * @should return the form id for existing file
	 * @return
	 */
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		AdministrationService adminService = Context.getAdministrationService();
		String defaultImageDirectory = adminService.getGlobalProperty("chica.defaultTifImageDirectory");

		try {
			// default 
			String pwsDir = defaultImageDirectory;
			String psfDir = defaultImageDirectory;
			String na = "notavailable";
			String psfFilename = null;
			String pwsFilename = null;
			
			String encounterIdString = request.getParameter("encounterId");

			String psfFormInstanceIdString = request
					.getParameter("psfFormInstanceId");
			String psfFormIdString = request.getParameter("psfFormId");
			String psfLocationIdString = request.getParameter("psfLocationId");

			String pwsFormInstanceIdString = request
					.getParameter("pwsFormInstanceId");
			String pwsFormIdString = request.getParameter("pwsFormId");
			String pwsLocationIdString = request.getParameter("pwsLocationId");

			Integer psfFormId = parseString(psfFormIdString);
			Integer psfLocationId = parseString(psfLocationIdString);
			Integer psfFormInstanceId = parseString(psfFormInstanceIdString);
			
			Integer encounterId = null;

			try
			{
				encounterId = Integer.parseInt(encounterIdString);
			} catch (Exception e)
			{
			}

			String printerLocation = null;
			Integer locationTagId = null;

			if (encounterId != null)
			{
				EncounterService encounterService = Context
						.getService(EncounterService.class);
				Encounter encounter = (Encounter) encounterService
						.getEncounter(encounterId);

				if (encounter != null)
				{
					// see if the encounter has a printer location
					// this will give us the location tag id
					printerLocation = encounter.getPrinterLocation();

					// if the printer location is null, pick
					// any location tag id for the given location
					if (printerLocation == null)
					{
						Location location = encounter.getLocation();
						if (location != null)
						{
							Set<LocationTag> tags = location.getTags();

							if (tags != null && tags.size() > 0)
							{
								printerLocation = ((LocationTag) tags.toArray()[0])
										.getTag();
							}
						}
					}
					if (printerLocation != null)
					{
						LocationService locationService = Context
								.getLocationService();
						LocationTag tag = locationService
								.getLocationTagByName(printerLocation);
						if (tag != null)
						{
							locationTagId = tag.getLocationTagId();
						}
					}
				}
			}

			if (psfFormId != null && locationTagId != null && psfLocationId != null)
			{
				psfDir = IOUtil
						.formatDirectoryName(org.openmrs.module.atd.util.Util
								.getFormAttributeValue(psfFormId,
										"imageDirectory", locationTagId,
										psfLocationId));
			}

			Integer pwsFormId = parseString(pwsFormIdString);
			Integer pwsLocationId = parseString(pwsLocationIdString);
			Integer pwsFormInstanceId = parseString(pwsFormInstanceIdString);
			
			if (pwsFormId != null && locationTagId != null && pwsLocationId != null)
			{
				pwsDir = IOUtil
						.formatDirectoryName(org.openmrs.module.atd.util.Util
								.getFormAttributeValue(pwsFormId,
										"imageDirectory", locationTagId,
										pwsLocationId));
			}

			psfFilename = na;
			pwsFilename = na;
			
			ATDService atdService = Context.getService(ATDService.class);
			State state = atdService.getStateByName("PSF_wait_to_scan");
			List<PatientState> scanStates = 
				atdService.getPatientStateByEncounterState(encounterId,
					state.getStateId());
			PatientState scanState = null;
			
			if(scanStates != null && scanStates.size()>0){
				scanState = scanStates.get(0);
			}
			
			ChicaService chicaService = Context.getService(ChicaService.class);
			Chica1Appointment chica1Appt = chicaService
				.getChica1AppointmentByEncounterId(encounterId);
			
			//there are two different possible file name formats
			//if a completed scan state exists
			if (scanState!=null&&scanState.getEndTime() != null) {
				
				if (psfDir != null && !psfDir.equals("") && psfFormInstanceId != null) {
					// check if dir and file exists
					psfFilename = "_" + psfLocationId + "-" + psfFormId + "-" + psfFormInstanceId + "_";
					File psffile = new File(psfDir + psfFilename + ".tif");
					if (!psffile.exists()) {
						psfFilename = psfFormInstanceId.toString();
						psffile = new File(psfDir + psfFilename + ".tif");
						
						if (!psffile.exists()) {
							psfDir = defaultImageDirectory;
							psfFilename = na;
						}
					}
				}
			}else{
				//if there is no completed scanned state but a chica1Appt row
				//exists, then this is archival chica1 data and there is only
				//one possible file format
				if (chica1Appt != null) {
					if (psfDir != null && !psfDir.equals("") && psfFormInstanceId != null) {
						// check if dir and file exists
						psfFilename = psfFormInstanceId.toString();
						File psffile = new File(psfDir + psfFilename + ".tif");
						
						if (!psffile.exists()) {
							psfDir = defaultImageDirectory;
							psfFilename = na;
						}
					}
				}else{
					psfDir = defaultImageDirectory;
					psfFilename = na;
				}
			}
			
			state = atdService.getStateByName("PWS_wait_to_scan");
			scanStates = 
				atdService.getPatientStateByEncounterState(encounterId,
					state.getStateId());
			scanState = null;
			
			if(scanStates != null && scanStates.size()>0){
				scanState = scanStates.get(0);
			}
			//there are two different possible file name formats
			//if a completed scan state exists
			if (scanState!=null&&scanState.getEndTime() != null) {
				
				if (pwsDir != null && !pwsDir.equals("") && pwsFormInstanceId != null) {
					// check if dir and file exists
					pwsFilename = "_" + pwsLocationId + "-" + pwsFormId + "-" + pwsFormInstanceId + "_";
					File pwsfile = new File(pwsDir + pwsFilename + ".tif");
					if (!pwsfile.exists()) {
						pwsFilename = pwsFormInstanceId.toString();
						pwsfile = new File(pwsDir + pwsFilename + ".tif");
						
						if (!pwsfile.exists()) {
							pwsDir = defaultImageDirectory;
							pwsFilename = na;
						}
					}
				}
			}else{
				//if there is no completed scanned state but a chica1Appt row
				//exists, then this is archival chica1 data and there is only
				//one possible file format
				if (chica1Appt != null) {
					if (pwsDir != null && !pwsDir.equals("") && pwsFormInstanceId != null) {
						// check if dir and file exists
						pwsFilename = pwsFormInstanceId.toString();
						File pwsfile = new File(pwsDir + pwsFilename + ".tif");
						
						if (!pwsfile.exists()) {
							pwsDir = defaultImageDirectory;
							pwsFilename = na;
						}
					}
				}else{
					pwsDir = defaultImageDirectory;
					pwsFilename = na;
				}
			}

			map.put("psfdir", psfDir);
			map.put("pwsdir", pwsDir);
			map.put("pwsfilename", pwsFilename);
			map.put("psffilename", psfFilename);
			map.put("patientId", request.getParameter("patientId"));

		} catch (UnexpectedRollbackException ex) {
			// ignore this exception since it happens with an
			// APIAuthenticationException
		} catch (APIAuthenticationException ex2) {
			// ignore this exception. It happens during the redirect to the
			// login page
		}catch (Exception e){

			this.log.error(Util.getStackTrace(e));
		}

		return map;
	}
}
