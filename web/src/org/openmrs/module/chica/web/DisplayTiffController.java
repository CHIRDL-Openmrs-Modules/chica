package org.openmrs.module.chica.web;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
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
	
	private void setImageLocation(String defaultImageDirectory,String imageFormIdString, String imageLocationIdString, 
	                              String imageFormInstanceIdString, Integer locationTagId, String na,Map map,
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
					imageFilename = defaultImageDirectory + "NotAvailableTablet.tif";
					File scanXmlFile = XMLUtil.getXmlFile(imageLocationId, imageFormId, imageFormInstanceId, 
						XMLUtil.DEFAULT_EXPORT_DIRECTORY);
					File stylesheetFile=null;
					FormAttributeValue formAttributeValue = service.getFormAttributeValue(imageFormId, ChirdlUtilConstants.FORM_ATTR_STYLESHEET, locationTagId, imageLocationId);
					if (formAttributeValue != null && formAttributeValue.getValue() != null && !formAttributeValue.getValue().isEmpty()) {
						try{
							stylesheetFile = new File(formAttributeValue.getValue());
						}catch (Exception e){
							log.error("The file path in the form attribute is not defined correctly. "+ e);
						}				
					} else {
						stylesheetFile = XMLUtil.findStylesheet(stylesheet);
					}
					if (stylesheetFile == null) {
						log.error("Error finding stylesheet to format the form: " + stylesheet);
					}
					if (scanXmlFile != null  && stylesheetFile != null) {
						try {
							String output = XMLUtil.transformFile(scanXmlFile, stylesheetFile);
							map.put(htmlOutputParameterName, output);
						} catch (Exception e) {
							log.error("Error transforming xml: " + scanXmlFile.getAbsolutePath() + " xslt: " + 
								stylesheetFile.getAbsolutePath(), e);
						}
					}
				}
			}
			
			if (imageFilename == null) {
				imageFilename = defaultImageDirectory + na + ".tif";
			}
		}else{
			imageFilename = imagefile.getPath();
		}

		map.put(filenameParameterName, imageFilename);
	}
	
	/* @param request 
	 * @should return the form id for existing file
	 * @return
	 */
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		AdministrationService adminService = Context.getAdministrationService();
		String defaultImageDirectory = adminService.getGlobalProperty("atd.defaultTifImageDirectory");

		try {
			// default 
			String na = "notavailable";
			String encounterIdString = request.getParameter("encounterId");

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
			} catch (Exception e){
			}
			Integer locationTagId = org.openmrs.module.chica.util.Util.getLocationTagId(encounterId);
			setImageLocation(defaultImageDirectory,leftImageFormIdString,leftImageLocationIdString,
				leftImageFormInstanceIdString,locationTagId,na,map,"leftImagefilename",encounterId,leftStylesheet,
				"leftHtmlOutput");
			
			setImageLocation(defaultImageDirectory,rightImageFormIdString,rightImageLocationIdString,
				rightImageFormInstanceIdString,locationTagId,na,map,"rightImagefilename",encounterId,rightStylesheet,
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

		return map;
	}
}
