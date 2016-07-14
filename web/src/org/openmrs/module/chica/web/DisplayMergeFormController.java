/**
 * 
 */
package org.openmrs.module.chica.web;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * @author Steve McKee
 * 
 * This controller is setup to handle the display of one or two merge forms.  It will take the parameters it is given, find 
 * the applicable merge files, and transform them against stylesheets.  The two outputs of the transformation will be 
 * returned to the view model.
 */
public class DisplayMergeFormController extends SimpleFormController
{

	protected final Log log = LogFactory.getLog(getClass());

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
	protected Map referenceData(HttpServletRequest request) throws Exception
	{
		Map<String, Object> map = new HashMap<String, Object>();
		Integer leftLocationId = null;
		Integer leftFormId = null;
		Integer leftFormInstanceId = null;
		String leftLocationIdStr = request.getParameter("leftImageLocationId");
		String leftFormIdStr = request.getParameter("leftImageFormId");
		String leftFormInstanceIdStr = request.getParameter("leftImageFormInstanceId");
		String leftStylesheet = request.getParameter("leftImageStylesheet");
		Integer rightLocationId = null;
		Integer rightFormId = null;
		Integer rightFormInstanceId = null;
		String rightLocationIdStr = request.getParameter("rightImageLocationId");
		String rightFormIdStr = request.getParameter("rightImageFormId");
		String rightFormInstanceIdStr = request.getParameter("rightImageFormInstanceId");
		String rightStylesheet = request.getParameter("rightImageStylesheet");
		
		try {
			leftLocationId = Integer.parseInt(leftLocationIdStr);
		} catch (NumberFormatException e) {
		}
		
		try {
			leftFormId = Integer.parseInt(leftFormIdStr);
		} catch (NumberFormatException e) {
		}
		
		try {
			leftFormInstanceId = Integer.parseInt(leftFormInstanceIdStr);
		} catch (NumberFormatException e) {
		}
		
		try {
			rightLocationId = Integer.parseInt(rightLocationIdStr);
		} catch (NumberFormatException e) {
		}
		
		try {
			rightFormId = Integer.parseInt(rightFormIdStr);
		} catch (NumberFormatException e) {
		}
		
		try {
			rightFormInstanceId = Integer.parseInt(rightFormInstanceIdStr);
		} catch (NumberFormatException e) {
		}
		
		// Transform the left XML
		String leftOutput = null;
		if (leftLocationId != null && leftFormId != null && leftFormInstanceId != null) {
			Form form = Context.getFormService().getForm(leftFormId);
			if (form != null) {
				map.put("leftImageFormname", form.getName());
			}
			
			map.put("leftImageForminstance", leftFormInstanceId);
			File leftXmlFile = XMLUtil.getXmlFile(leftLocationId, leftFormId, leftFormInstanceId, 
				getLocationAttributeDirectoryName());
			File stylesheetFile = XMLUtil.findStylesheet(leftStylesheet);
			if (stylesheetFile == null) {
				log.error("Error finding stylesheet to format the form: " + leftStylesheet);
			}
			
			if (leftXmlFile != null  && stylesheetFile != null) {
				try {
					leftOutput = XMLUtil.transformFile(leftXmlFile, stylesheetFile);
				} catch (Exception e) {
					log.error("Error transforming xml: " + leftXmlFile.getAbsolutePath() + " xslt: " + 
						stylesheetFile.getAbsolutePath(), e);
				}
			}
		}
		
		map.put("leftOutput", leftOutput);
		
		// Transform the right XML
		String rightOutput = null;
		if (rightLocationId != null && rightFormId != null && rightFormInstanceId != null) {
			Form form = Context.getFormService().getForm(rightFormId);
			if (form != null) {
				map.put("rightImageFormname", form.getName());
			}
			
			map.put("rightImageForminstance", rightFormInstanceId);
			File rightXmlFile = XMLUtil.getXmlFile(rightLocationId, rightFormId, rightFormInstanceId, 
				XMLUtil.DEFAULT_MERGE_DIRECTORYY);
			
			String encounterIdString = request.getParameter("encounterId");
			Integer locationTagId = getLocationTagId(encounterIdString);
			ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
			File stylesheetFile=null;
			FormAttributeValue formAttributeValue = service.getFormAttributeValue(rightFormId, "stylesheet", locationTagId, rightLocationId);
			if (formAttributeValue!=null) {
				try{
					stylesheetFile = new File(formAttributeValue.getValue());
				}catch (Exception e){
					log.error("The file path in the form attribute is not defined correctly. "+ e);
				}				
			} else {
				stylesheetFile = XMLUtil.findStylesheet(rightStylesheet);
			}
			if (stylesheetFile == null) {
				log.error("Error finding stylesheet to format the form: " + rightStylesheet);
			}
			
			if (rightXmlFile != null && stylesheetFile != null) {
				try {
					rightOutput = XMLUtil.transformFile(rightXmlFile, stylesheetFile);
				} catch (Exception e) {
					log.error("Error transforming xml: " + rightXmlFile.getAbsolutePath() + " xslt: " + 
						stylesheetFile.getAbsolutePath(), e);
				}
			}
		}
		
		map.put("rightOutput", rightOutput);

		return map;
	}
	
	/**
	 * Returns the directory used to find the XML file.
	 * 
	 * @return String containing the location attribute name associated with the directory where the XML resides.
	 */
	protected String getLocationAttributeDirectoryName() {
		return XMLUtil.DEFAULT_MERGE_DIRECTORYY;
	}
	
	/**
	 * Returns the Location Tag ID
	 * Auto generated method comment
	 * 
	 * @param encounterIdString encounter ID
	 * @return LocationTagId
	 */
	private Integer getLocationTagId(String encounterIdString){
		
		Integer encounterId = null;
		try {
			encounterId = Integer.parseInt(encounterIdString);
		} catch (Exception e){
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
	return locationTagId;
	}

}
