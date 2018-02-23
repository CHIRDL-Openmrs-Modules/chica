package org.openmrs.module.chica.web.controller;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.openmrs.Location;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chica.web.ChicaServlet;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(value = "module/chica/displayViewEncounterForm.form")
public class DisplayViewEncounterFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final String FORM_VIEW = "/module/chica/displayViewEncounterForm";
	private static final String PARAMETER_RIGHT_FORM_NAME = "rightFormName";
	private static final String PARAMETER_LEFT_FORM_NAME = "leftFormName";
	private static final String PARAMETER_LEFT_IMAGE_FILENAME = "leftImagefilename";
	private static final String PARAMETER_RIGHT_IMAGE_FILENAME = "rightImagefilename";
	private static final String PARAMETER_LEFT_HTML_OUTPUT = "leftHtmlOutput";
	private static final String PARAMETER_RIGHT_HTML_OUTPUT = "rightHtmlOutput";
	private static final String LOCATION_PEPS = "PEPS";

	@RequestMapping(method = RequestMethod.POST)
	protected ModelAndView processSubmit(HttpServletRequest request,HttpServletResponse response, Object command) throws Exception {
		return new ModelAndView(new RedirectView(FORM_VIEW));
	}
	
	/**
	 * Sets the path to the file (image, merge, or scan)
	 * Looks in the image directory first since ADHD form are in the image directory, but PWSs and PSFs can also be in the image directory
	 * If an image is not found, a url is created with the directory set based on what was found in the config file
	 * 
	 * @param formId
	 * @param locationId
	 * @param formInstanceId
	 * @param locationTagId
	 * @param map
	 * @param filenameParameterName
	 * @param encounterId
	 * @param stylesheet
	 * @param htmlOutputParameterName
	 * @param formDirectory
	 */
	private void setFormFilePath(Integer formId, Integer locationId, Integer formInstanceId, Integer locationTagId, Map<String,Object> map,
	                              String filenameParameterName,Integer encounterId, String stylesheet, String htmlOutputParameterName, String formDirectory){
		String imageDir = null;
		File imagefile = null;
		String imageFilename = null;

		if (formId != null && locationTagId != null && locationId != null && formInstanceId != null) 
		{
			imageDir = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util.getFormAttributeValue(formId,
					ChirdlUtilConstants.FORM_ATTRIBUTE_IMAGE_DIRECTORY, locationTagId, locationId));

			if (StringUtils.isNotEmpty(imageDir)) 
			{
				// check if dir and file exists
				imageFilename = locationId + ChirdlUtilConstants.GENERAL_INFO_DASH + formId + ChirdlUtilConstants.GENERAL_INFO_DASH + formInstanceId;

				imagefile = IOUtil.searchForImageFile(imageFilename,imageDir);

				//check for formInstance.tif format if from Pecar
				if (!imagefile.exists()) {
					imageFilename = formInstanceId.toString();
					imagefile = IOUtil.searchForImageFile(imageFilename, imageDir);

					if (!imagefile.exists()) {
						imagefile = null;
					} 
				}
			}

			if(imagefile == null && !ChirdlUtilConstants.FORM_ATTRIBUTE_IMAGE_DIRECTORY.equalsIgnoreCase(formDirectory))
			{
					String transformUrl = null;
					try {
						URIBuilder uriBuilder = new URIBuilder(ChicaServlet.CHICA_SERVLET_URL);
						uriBuilder.addParameter(ChicaServlet.PARAM_ACTION, ChicaServlet.TRANSFORM_FORM_XML);
						uriBuilder.addParameter(ChirdlUtilConstants.PARAMETER_FORM_ID, String.valueOf(formId));
						uriBuilder.addParameter(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, String.valueOf(locationTagId));
						uriBuilder.addParameter(ChirdlUtilConstants.PARAMETER_LOCATION_ID, String.valueOf(locationId));
						uriBuilder.addParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID, String.valueOf(formInstanceId));
						uriBuilder.addParameter(ChicaServlet.STYLESHEET, stylesheet);
						uriBuilder.addParameter(ChicaServlet.FORM_DIRECTORY, formDirectory);

						transformUrl = uriBuilder.toString();
					}
					catch (URISyntaxException e) {
						log.error("Error generating URI for form image location for action: " + ChicaServlet.TRANSFORM_FORM_XML + 
								" form ID: " + formId + " location tag ID: " + locationTagId + " location ID " + locationId + 
								" form instance ID: " + formInstanceId + " stylesheet: " + stylesheet, e);
					}

					map.put(htmlOutputParameterName, transformUrl);
			}
			else
			{
				try {
					URIBuilder uriBuilder = new URIBuilder(ChicaServlet.CHICA_SERVLET_URL);
					uriBuilder.addParameter(ChicaServlet.PARAM_ACTION, ChicaServlet.CONVERT_TIFF_TO_PDF);
					uriBuilder.addParameter(ChicaServlet.PARAM_TIFF_FILE_LOCATION, imagefile != null ? imagefile.getPath() : ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING);

					imageFilename = uriBuilder.toString() + ChicaServlet.CHICA_SERVLET_PDF_PARAMS;
					map.put(filenameParameterName, imageFilename);
				}
				catch (URISyntaxException e) {
					log.error("Error generating URI form image filename for action: " + ChicaServlet.CONVERT_TIFF_TO_PDF + 
							" tiff file location: " + imagefile.getPath(), e);
				}
			}
		}
		
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
	
	/**
	 * Parse the parameter and returns an Integer
	 * Checks to make sure the parameter isn't null before parsing
	 * Catches NumberFormatException and logs the error
	 * 
	 * @param request
	 * @param paramName
	 * @return
	 */
	private Integer parseParameter(HttpServletRequest request, String paramName)
	{
		Integer intValue = null;
		
		String stringValue = request.getParameter(paramName);
		if(stringValue != null)
		{
			try
			{
				intValue = Integer.parseInt(stringValue);
			}
			catch(NumberFormatException nfe)
			{
				log.error("Error in " + getClass().getName() + ". Error parsing parameter: " + paramName + " stringValue: " + stringValue + ".", nfe);
			}
		}
		
		return intValue;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	protected String initForm(HttpServletRequest request, ModelMap map) throws Exception {
		try { 
			
			// Parse required parameters
			String encounterIdString = request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
			Integer encounterId = null;
			
			try
			{
				// Note - not using the parseParameter method here so that we can return from here if any type of parsing error occurs
				encounterId = Integer.parseInt(encounterIdString);
			}
			catch(Exception e)
			{
				log.error("Error in " + getClass().getName() + ". Error parsing encounter Id: "+encounterIdString, e);
				return FORM_VIEW;
			}
			
			Integer locationTagId = org.openmrs.module.chica.util.Util.getLocationTagId(encounterId);
			
			// Left form parameters - these parameters could be null if the left form isn't set
			Integer leftFormFormInstanceId = parseParameter(request, ChicaConstants.PARAMETER_LEFT_FORM_FORM_INSTANCE_ID);
			Integer leftFormFormId = parseParameter(request, ChicaConstants.PARAMETER_LEFT_FORM_FORM_ID);
			Integer leftFormLocationId = parseParameter(request, ChicaConstants.PARAMETER_LEFT_FORM_LOCATION_ID);
			String leftStylesheet = request.getParameter(ChicaConstants.PARAMETER_LEFT_FORM_STYLESHEET);
			String leftFormDirectory = request.getParameter(ChicaConstants.PARAMETER_LEFT_FORM_DIRECTORY);
			
			// Right form parameters - these parameters could be null if the right form isn't set
			Integer rightFormFormInstanceId = parseParameter(request, ChicaConstants.PARAMETER_RIGHT_FORM_FORM_INSTANCE_ID);
			Integer rightFormFormId = parseParameter(request, ChicaConstants.PARAMETER_RIGHT_FORM_FORM_ID);
			Integer rightFormLocationId = parseParameter(request, ChicaConstants.PARAMETER_RIGHT_FORM_LOCATION_ID);
			String rightStylesheet = request.getParameter(ChicaConstants.PARAMETER_RIGHT_FORM_STYLESHEET);
			String rightFormDirectory = request.getParameter(ChicaConstants.PARAMETER_RIGHT_FORM_DIRECTORY);
				
			// Use the display name from the form attribute
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			FormAttributeValue fav = null;
			if(leftFormFormId != null && locationTagId != null && leftFormLocationId != null)
			{
				fav = chirdlutilbackportsService.getFormAttributeValue(leftFormFormId, ChirdlUtilConstants.FORM_ATTR_DISPLAY_NAME, locationTagId, leftFormLocationId);
				if(fav != null && StringUtils.isNotEmpty(fav.getValue()))
				{
					map.put(PARAMETER_LEFT_FORM_NAME, fav.getValue());
				}
			}
			
			map.put(ChicaConstants.PARAMETER_LEFT_FORM_FORM_INSTANCE_ID, leftFormFormInstanceId);
			
			// Use the display name from the form attribute
			if(rightFormFormId != null && locationTagId != null && rightFormLocationId != null)
			{
				fav = chirdlutilbackportsService.getFormAttributeValue(rightFormFormId, ChirdlUtilConstants.FORM_ATTR_DISPLAY_NAME, locationTagId, rightFormLocationId);
				if(fav != null && StringUtils.isNotEmpty(fav.getValue()))
				{
					map.put(PARAMETER_RIGHT_FORM_NAME, fav.getValue());
				}
			}
			
			map.put(ChicaConstants.PARAMETER_RIGHT_FORM_FORM_INSTANCE_ID, rightFormFormInstanceId);
			
			setFormFilePath(leftFormFormId, leftFormLocationId, leftFormFormInstanceId, locationTagId, map, 
					PARAMETER_LEFT_IMAGE_FILENAME, encounterId, leftStylesheet, PARAMETER_LEFT_HTML_OUTPUT, leftFormDirectory);
			
			setFormFilePath(rightFormFormId, rightFormLocationId, rightFormFormInstanceId, locationTagId, map, 
					PARAMETER_RIGHT_IMAGE_FILENAME, encounterId, rightStylesheet, PARAMETER_RIGHT_HTML_OUTPUT, rightFormDirectory); 

			map.put(ChirdlUtilConstants.PARAMETER_PATIENT_ID, request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID));

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
