package org.openmrs.module.chica.web.controller;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.web.ChicaServlet;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(value = "module/chica/displayTiff.form")
public class DisplayTiffController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final String FORM_VIEW = "/module/chica/displayTiff";

	@RequestMapping(method = RequestMethod.POST)
	protected ModelAndView processSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command) throws Exception {
		return new ModelAndView(new RedirectView(FORM_VIEW));
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
	
	private void setImageLocation(String defaultImageDirectory,String imageFormIdString, String imageLocationIdString, 
	                              String imageFormInstanceIdString, Integer locationTagId, Map<String,Object> map,
	                              String filenameParameterName,Integer encounterId, String stylesheet, 
	                              String htmlOutputParameterName){
		String imageDir = null;
		
		Integer imageFormId = parseString(imageFormIdString);
		Integer imageLocationId = parseString(imageLocationIdString);
		Integer imageFormInstanceId = parseString(imageFormInstanceIdString);
		if (imageFormId != null && locationTagId != null && imageLocationId != null) {
			imageDir = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util.getFormAttributeValue(imageFormId,
			    "imageDirectory", locationTagId, imageLocationId));
		}
		
		File imagefile = null;
		
		if (imageDir != null && !imageDir.equals("") && imageFormInstanceId != null) {
			
			ChicaService chicaService = Context.getService(ChicaService.class);
			
			//see if this is a chica1 form
			Chica1Appointment chica1Appt = chicaService.getChica1AppointmentByEncounterId(encounterId);
			if (chica1Appt != null) {
				String imageFilename = imageFormInstanceId.toString();
				
				imagefile = IOUtil.searchForImageFile(imageFilename,imageDir);
				
				if (!imagefile.exists()) {
					imagefile = null;
				}
			}else{
			
				// check if dir and file exists
				String imageFilename = imageLocationId + "-" + imageFormId + "-" + imageFormInstanceId;
				
				imagefile = IOUtil.searchForImageFile(imageFilename,imageDir);
				
				LocationService locationService = Context.getLocationService();
				Location location = locationService.getLocation(imageLocationId);
				String locationName = location.getName();
				
				//check for formInstance.tif format if from Pecar
				if (!imagefile.exists()) {
					
					if (locationName.equals("PEPS")) {
						imageFilename = imageFormInstanceId.toString();
						
						imagefile = IOUtil.searchForImageFile(imageFilename, imageDir);
						
						if (!imagefile.exists()) {
							imagefile = null;
						} 
					}else{
						imagefile = null;
					}
				}
			}
		}
		
		String imageFilename = null;
		
		if(imagefile == null){
			if (imageFormId != null && imageFormInstanceId != null && imageLocationId != null) {
				// Check to see if it was populated using electronic means
				ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
				FormInstanceAttributeValue fiav = service.getFormInstanceAttributeValue(
					imageFormId, imageFormInstanceId, imageLocationId, "medium");
				if (fiav != null && "electronic".equals(fiav.getValue())) {
					String transformUrl = null;
					try {
						URIBuilder uriBuilder = new URIBuilder(ChicaServlet.CHICA_SERVLET_URL);
						uriBuilder.addParameter(ChicaServlet.PARAM_ACTION, ChicaServlet.TRANSFORM_FORM_XML);
						uriBuilder.addParameter(ChirdlUtilConstants.PARAMETER_FORM_ID, String.valueOf(imageFormId));
						uriBuilder.addParameter(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, String.valueOf(locationTagId));
						uriBuilder.addParameter(ChirdlUtilConstants.PARAMETER_LOCATION_ID, String.valueOf(imageLocationId));
						uriBuilder.addParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID, String.valueOf(imageFormInstanceId));
						uriBuilder.addParameter(ChicaServlet.STYLESHEET, stylesheet);
						
						transformUrl = uriBuilder.toString();
					}
					catch (URISyntaxException e) {
						log.error("Error generating URI for form image location for action: " + ChicaServlet.TRANSFORM_FORM_XML + 
							" form ID: " + imageFormId + " location tag ID: " + locationTagId + " location ID " + imageLocationId + 
							" form instance ID: " + imageFormInstanceId + " stylesheet: " + stylesheet, e);
					}
					
					map.put(htmlOutputParameterName, transformUrl);
				}
			}
		}else{
			try {
				URIBuilder uriBuilder = new URIBuilder(ChicaServlet.CHICA_SERVLET_URL);
				uriBuilder.addParameter(ChicaServlet.PARAM_ACTION, ChicaServlet.CONVERT_TIFF_TO_PDF);
				uriBuilder.addParameter(ChicaServlet.PARAM_TIFF_FILE_LOCATION, imagefile.getPath());
				
				imageFilename = uriBuilder.toString() + ChicaServlet.CHICA_SERVLET_PDF_PARAMS;
			}
			catch (URISyntaxException e) {
				log.error("Error generating URI form image filename for action: " + ChicaServlet.CONVERT_TIFF_TO_PDF + 
					" tiff file location: " + imagefile.getPath(), e);
			}
		}

		map.put(filenameParameterName, imageFilename);
		if (map.get(filenameParameterName) == null && map.get(htmlOutputParameterName) == null) {
			// We weren't able to locate the form. We still need to return something so an error page gets displayed.
			try {
				URIBuilder uriBuilder = new URIBuilder(ChicaServlet.CHICA_SERVLET_URL);
				uriBuilder.addParameter(ChicaServlet.PARAM_ACTION, ChicaServlet.CONVERT_TIFF_TO_PDF);
				imageFilename = uriBuilder.toString() + ChicaServlet.CHICA_SERVLET_PDF_PARAMS;
				map.put(filenameParameterName, imageFilename);
			}
			catch (URISyntaxException e) {
				log.error("Error generating URI form image filename for action: " + ChicaServlet.CONVERT_TIFF_TO_PDF, e);
			}
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	protected String initForm(HttpServletRequest request, ModelMap map) throws Exception {
		AdministrationService adminService = Context.getAdministrationService();
		String defaultImageDirectory = adminService.getGlobalProperty("atd.defaultTifImageDirectory");

		try {
			// default 
			String encounterIdString = request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);

			String leftImageFormInstanceIdString = request
					.getParameter("leftImageFormInstanceId");
			String leftImageFormIdString = request.getParameter("leftImageFormId");
			String leftImageLocationIdString = request.getParameter("leftImageLocationId");
			Integer leftImageFormId = parseString(leftImageFormIdString);
			String leftStylesheet = request.getParameter("leftImageStylesheet");
			String rightStylesheet = request.getParameter("rightImageStylesheet");
			FormService formService = Context.getFormService();
			Form form = null;
			if(leftImageFormId != null){
				form = formService.getForm(leftImageFormId);
			}
			map.put("leftImageForminstance", leftImageFormInstanceIdString);
			if(form != null){
				map.put("leftImageFormname", form.getName());
			}
			
			String rightImageFormInstanceIdString = request
					.getParameter("rightImageFormInstanceId");
			String rightImageFormIdString = request.getParameter("rightImageFormId");
			String rightImageLocationIdString = request.getParameter("rightImageLocationId");
			Integer rightImageFormId = parseString(rightImageFormIdString);
			form = null;
			if(rightImageFormId != null){
				form = formService.getForm(rightImageFormId);
			}
			map.put("rightImageForminstance", rightImageFormInstanceIdString);
			if(form != null){
				map.put("rightImageFormname", form.getName());
			}
			Integer encounterId = null;
			try {
				encounterId = Integer.parseInt(encounterIdString);
			} catch (NumberFormatException e){
				log.error("Error Parsing encounter Id: "+encounterIdString, e);
			}
			Integer locationTagId = org.openmrs.module.chica.util.Util.getLocationTagId(encounterId);
			setImageLocation(defaultImageDirectory,leftImageFormIdString,leftImageLocationIdString,
				leftImageFormInstanceIdString,locationTagId,map,"leftImagefilename",encounterId,leftStylesheet,
				"leftHtmlOutput");
			
			setImageLocation(defaultImageDirectory,rightImageFormIdString,rightImageLocationIdString,
				rightImageFormInstanceIdString,locationTagId,map,"rightImagefilename",encounterId,rightStylesheet,
				"rightHtmlOutput");

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

		return FORM_VIEW;
	}
}
