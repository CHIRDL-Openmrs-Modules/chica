/**
 * 
 */
package org.openmrs.module.chica.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.XMLUtil;
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
		String encounterIdString = request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
		Integer encounterId = null;
		String strOutput = null;
		try {
			encounterId = Integer.parseInt(encounterIdString);
		} catch (NumberFormatException e){
			log.error("Error Parsing encounter Id: "+encounterIdString, e);
		}
		
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
		
		Integer locationTagId = Util.getLocationTagId(encounterId);
		// Transform the left XML
		if (leftLocationId != null && leftFormId != null && leftFormInstanceId != null) {
			Form form = Context.getFormService().getForm(leftFormId);
			if (form != null) {
				map.put("leftImageFormname", form.getName());
			}
			map.put("leftImageForminstance", leftFormInstanceId);
			strOutput = Util.displayStylesheet(leftFormId, locationTagId, leftLocationId, leftFormInstanceId, 
											   leftStylesheet, getLocationAttributeDirectoryName());
		}
		map.put("leftOutput", strOutput);
		
		// Transform the right XML
		if (rightLocationId != null && rightFormId != null && rightFormInstanceId != null) {
			Form form = Context.getFormService().getForm(rightFormId);
			if (form != null) {
				map.put("rightImageFormname", form.getName());
			}
			map.put("rightImageForminstance", rightFormInstanceId);
			strOutput = Util.displayStylesheet(rightFormId, locationTagId, rightLocationId, rightFormInstanceId, 
											   rightStylesheet, getLocationAttributeDirectoryName());
		}
		map.put("rightOutput", strOutput);
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
}
