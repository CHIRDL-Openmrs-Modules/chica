package org.openmrs.module.chica.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.dss.util.Util;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class DisplayTiffController extends SimpleFormController {
	public  static final String DEFAULT_IMG_DIR = "c:/chica/images/";
	
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

	
	/* @param request 
	 * @should return the form id for existing file
	 * @return
	 */
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			// default 
			String pwsDir = DEFAULT_IMG_DIR;
			String psfDir = DEFAULT_IMG_DIR;
			String na = "notavailable";
			
			String encounterIdString = request.getParameter("encounterId");
			
			String pwsIdString = request.getParameter("pwsId");
			if (pwsIdString == null || pwsIdString.equals("")) {
				pwsIdString = na;
			}
			String psfIdString = request.getParameter("psfId");
			if (psfIdString == null || psfIdString.equals("")) {
				psfIdString = na;
			}
			
			if (encounterIdString == null || encounterIdString.equals("")) {

				pwsIdString = na;
				psfIdString = na;


			} else {
				psfDir = getImagesDirectory(
						Integer.parseInt(encounterIdString), "PSF");
				pwsDir = getImagesDirectory(
						Integer.parseInt(encounterIdString), "PWS");
				
				if (psfDir!= null && !psfDir.equals("")){
					//check if dir and file exists 
					File psffile = new File(psfDir + psfIdString + ".tif");
					if (!psffile.exists()){
						psfDir = DEFAULT_IMG_DIR;
						psfIdString = na;
					}
				}
				
				if (pwsDir!= null && !pwsDir.equals("")){
					//check if dir and file exists 
					File pwsfile = new File(pwsDir + pwsIdString + ".tif");
					if (!pwsfile.exists()){
						pwsDir = DEFAULT_IMG_DIR;
						pwsIdString = na;
					}
				}
				
			}

			map.put("psfdir", psfDir);
			map.put("pwsdir", pwsDir);
			map.put("pwsfilename", pwsIdString);
			map.put("psffilename", psfIdString);
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

	private String getImagesDirectory(Integer encounterId, String type) {

		ChicaService chicaService = Context.getService(ChicaService.class);
		String name = null;
		String result = null;
		EncounterService encService = Context
				.getService(EncounterService.class);
		Encounter enc = (Encounter) encService.getEncounter(encounterId);

		if (enc != null) {
			Location loc = enc.getLocation();
			if (loc != null) {
				name = loc.getName();

			}
		}

		if (name != null) {
			ArrayList<String> dirs = chicaService.getImagesDirectory(name);

			for (String dir : dirs) {
				if (dir.contains(type)) {
					result = dir;
					char lastChar = result.charAt(result.length() - 1);
					result.replace('\\', '/');
					if (lastChar != '\\') {
						result += "/";
					}
					return result;
				}
			}

		}
		return null;

	}

}
