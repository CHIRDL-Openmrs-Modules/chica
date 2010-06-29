package org.openmrs.module.chica.web;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.FileDateComparator;
import org.openmrs.module.chirdlutil.util.FileListFilter;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
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
	
	private void setImageLocation(String defaultImageDirectory,String imageFormIdString,
	                      String imageLocationIdString,String imageFormInstanceIdString,
	                      Integer locationTagId, String na,Map map,
	                      String filenameParameterName,Integer encounterId){
		String imageDir = null;
		String imageFilename = null;
		
		Integer imageFormId = parseString(imageFormIdString);
		Integer imageLocationId = parseString(imageLocationIdString);
		Integer imageFormInstanceId = parseString(imageFormInstanceIdString);
		if (imageFormId != null && locationTagId != null && imageLocationId != null) {
			imageDir = IOUtil.formatDirectoryName(org.openmrs.module.atd.util.Util.getFormAttributeValue(imageFormId,
			    "imageDirectory", locationTagId, imageLocationId));
		}
						
		if (imageDir != null && !imageDir.equals("") && imageFormInstanceId != null) {
			
			ChicaService chicaService = Context.getService(ChicaService.class);
			
			//see if this is a chica1 form
			File imagefile = null;
			Chica1Appointment chica1Appt = chicaService.getChica1AppointmentByEncounterId(encounterId);
			if (chica1Appt != null) {
				imageFilename = imageFormInstanceId.toString();
				
				imagefile = searchForFile(imageFilename,imageDir);
				
				if (!imagefile.exists()) {
					imageFilename = null;
				}else{
					imageFilename = imageDir+imageFilename+".tif";
				}
			}else{
			
				// check if dir and file exists
				imageFilename = imageLocationId + "-" + imageFormId + "-" + imageFormInstanceId;
				
				imagefile = searchForFile(imageFilename,imageDir);
				
				LocationService locationService = Context.getLocationService();
				Location location = locationService.getLocation(imageLocationId);
				String locationName = location.getName();
				
				//check for formInstance.tif format if from Pecar
				if (!imagefile.exists()) {
					
					if (locationName.equals("PEPS")) {
						imageFilename = imageFormInstanceId.toString();
						
						imagefile = searchForFile(imageFilename, imageDir);
						
						if (!imagefile.exists()) {
							imageFilename = null;
						} else {
							imageFilename = imageDir + imageFilename + ".tif";
						}
					}else{
						imageFilename = null;
					}
				}else{
					imageFilename = imageDir+imageFilename+".tif";
				}
			}
		}
		
		if(imageFilename == null){
			imageFilename = defaultImageDirectory + na + ".tif";
		}

		map.put(filenameParameterName, imageFilename);
	}
	
	private static File searchForFile(String imageFilename,String imageDir){
		//This FilenameFilter will get ALL tifs starting with the filename
		//including of rescan versions nnn_1.tif, nnn_2.tif, etc
		FilenameFilter filtered = new FileListFilter(imageFilename, "tif");
		File dir = new File(imageDir);
		File[] files = dir.listFiles(filtered);
		if (!(files == null || files.length == 0)) {
			//This FileDateComparator will list in order
			//with newest file first.
			Arrays.sort(files, new FileDateComparator());
			imageFilename = files[0].getPath();
		}
		
		File imagefile = new File(imageFilename);
		
		return imagefile;
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
			String na = "notavailable";
			String encounterIdString = request.getParameter("encounterId");

			String leftImageFormInstanceIdString = request
					.getParameter("leftImageFormInstanceId");
			String leftImageFormIdString = request.getParameter("leftImageFormId");
			String leftImageLocationIdString = request.getParameter("leftImageLocationId");
			Integer leftImageFormId = parseString(leftImageFormIdString);
			FormService formService = Context.getFormService();
			Form form = formService.getForm(leftImageFormId);
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

			setImageLocation(defaultImageDirectory,leftImageFormIdString,leftImageLocationIdString,
				leftImageFormInstanceIdString,locationTagId,na,map,"leftImagefilename",encounterId);
			
			setImageLocation(defaultImageDirectory,rightImageFormIdString,rightImageLocationIdString,
				rightImageFormInstanceIdString,locationTagId,na,map,"rightImagefilename",encounterId);

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
