package org.openmrs.module.chica.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.cache.Cache;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.atd.util.AtdConstants;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutilbackports.cache.ApplicationCacheManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet giving access to CHICA information.
 *
 * @author Steve McKee
 */
public class ChicaServlet extends HttpServlet {
	
	public static final String CHICA_SERVLET_URL = "/moduleServlet/chica/chica";
	public static final String CHICA_SERVLET_PDF_PARAMS = "#view=fit&navpanes=0";
	public static final String CONTENT_DISPOSITION_PDF = "inline;filename=patientJITS.pdf";
	
	private static final long serialVersionUID = 1L;
	
	private static final String WILL_KEEP_ALIVE = "OK";
	
	private static final Logger log = LoggerFactory.getLogger(ChicaServlet.class);
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	    try{
    		boolean authenticated = ServletUtil.authenticateUser(request);
    		if (!authenticated) {
    			response.setHeader(
    				ChirdlUtilConstants.HTTP_HEADER_AUTHENTICATE, ChirdlUtilConstants.HTTP_HEADER_AUTHENTICATE_BASIC_CHICA);  
    			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    		}
    		
    		String action = request.getParameter(ServletUtil.PARAM_ACTION);
    		if (ServletUtil.GET_PATIENT_JITS.equals(action) || ServletUtil.DISPLAY_FORCE_PRINT_FORMS.equals(action)) {
    			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_APPLICATION_PDF);
    			response.addHeader(
    				ChirdlUtilConstants.HTTP_HEADER_CONTENT_DISPOSITION, CONTENT_DISPOSITION_PDF);
    		} else if (ServletUtil.FORCE_PRINT_FORMS.equals(action)) {
    			ServletUtil.getForcePrintFormHeader(request, response);
    		}
	    }catch(IOException e){
	        log.error("IOException in ChicaServlet.", e);
	    }
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try{
		    boolean authenticated = ServletUtil.authenticateUser(request);
		    if (!authenticated) {
		        response.setHeader(
		            ChirdlUtilConstants.HTTP_HEADER_AUTHENTICATE, ChirdlUtilConstants.HTTP_HEADER_AUTHENTICATE_BASIC_CHICA);  
		        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		    }
		    
		    String action = request.getParameter(ServletUtil.PARAM_ACTION);
		    if (ServletUtil.IS_AUTHENTICATED.equals(action)) {
		        ServletUtil.isUserAuthenticated(response);
		    } else if (ServletUtil.AUTHENTICATE_USER.equals(action)) {
		        ServletUtil.authenticateUser(request, response);
		    } else if (ServletUtil.GET_PATIENT_JITS.equals(action)) {
		    	ServletUtil.getPatientJITs(request, response);
		    } else if (ServletUtil.GET_AVAILABLE_PATIENT_JITS.equals(action)) {
		    	ServletUtil.getAvailablePatientJITs(request, response);
		    } else if (ServletUtil.GET_FORCE_PRINT_FORMS.equals(action)) {
		    	ServletUtil.getForcePrintForms(request, response);
		    } else if (ServletUtil.FORCE_PRINT_FORMS.equals(action)) {
		    	ServletUtil.forcePrintForms(request, response);
		    } else if (ServletUtil.DISPLAY_FORCE_PRINT_FORMS.equals(action)) {
		    	ServletUtil.getPatientJITs(request, response);
		    } else if (ServletUtil.GET_GREASEBOARD_PATIENTS.equals(action)) {
		        getGreaseboardPatients(request, response);
		    } else if (ServletUtil.VERIFY_MRN.equals(action)) {
		        ManualCheckinSSNMRN.verifyMRN(request, response);
		    } else if (ServletUtil.GET_MANUAL_CHECKIN.equals(action)) {
		        ManualCheckin.getManualCheckinPatient(request, response);
		    } else if (ServletUtil.SAVE_MANUAL_CHECKIN.equals(action)) {
		        ManualCheckin.saveManualCheckinPatient(request, response);
		    } else if (ServletUtil.SEND_PAGE_REQUEST.equals(action)) {
		        Pager.sendPage(request, response);
		    } else if (ServletUtil.KEEP_ALIVE.equals(action)) {
		        keepAlive(response);
		    } else if (ServletUtil.CLEAR_CACHE.equals(action)) {
		        clearCache(request, response);
		    } else if (ServletUtil.SAVE_FORM_DRAFT.equals(action)) {
		        ServletUtil.saveFormDraft(request, response);
		    } else if (ServletUtil.CLEAR_FORM_INSTANCE_FROM_FORM_CACHE.equals(action)) {
		        clearFormInstaceFromFormDraftCache(request, response);
		    } else if (ServletUtil.CONVERT_TIFF_TO_PDF.equals(action)) {
		        convertTiffToPDF(request, response);
		    } else if (ServletUtil.TRANSFORM_FORM_XML.equals(action)) {
		        transformFormXMLToHTML(request, response);
		    }
		}catch(IOException ioe){
		    log.error("IOException in ChicaServlet.", ioe);
		}
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try{
		    doGet(request, response);
		}catch(ServletException e){
            log.error("ServletException in ChicaServlet", e);
        }
	}
	
	/**
	 * Retrieves the greaseboard patient information for the day..
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
    private void getGreaseboardPatients(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> map = new HashMap<>();
		GreaseBoardBuilder.generatePatientRows(map);
		List<PatientRow> patientRows = (List<PatientRow>)map.get(ServletUtil.PARAM_PATIENT_ROWS);
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(ServletUtil.XML_GREASEBOARD_START);
		pw.write(ServletUtil.XML_NEED_VITALS_START + map.get(ServletUtil.PARAM_NEED_VITALS) + 
			ServletUtil.XML_NEED_VITALS_END);
		pw.write(ServletUtil.XML_WAITING_FOR_MD_START + map.get(ServletUtil.PARAM_WAITING_FOR_MD) + 
			ServletUtil.XML_WAITING_FOR_MD_END);
		pw.write(ServletUtil.XML_PATIENT_ROWS_START);
		if (patientRows != null) {
			for (PatientRow row : patientRows) {
				pw.write(row.toXml());
			}
		}
		pw.write(ServletUtil.XML_PATIENT_ROWS_END);
		pw.write(ServletUtil.XML_BAD_SCANS_START);
		List<URL> badScans = (List<URL>)map.get(ServletUtil.PARAM_BAD_SCANS);
		if (badScans != null) {
			for (URL badScan : badScans) {
				pw.write(ServletUtil.XML_URL_START + badScan + ServletUtil.XML_URL_END);
			}
		}
		pw.write(ServletUtil.XML_BAD_SCANS_END);
		pw.write(ServletUtil.XML_GREASEBOARD_END);
	}
		
	/**
	 * Method to keep the session alive.
	 * 
	 * @param response The HttpServletResponse where the response will be written.
	 * @throws IOException
	 */
	private void keepAlive(HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(WILL_KEEP_ALIVE);
	}
	
	/**
	 * Clears the given cache in the request
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
    private void clearCache(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		
    	PrintWriter pw = response.getWriter();
    	String cacheName = request.getParameter(ServletUtil.PARAM_CACHE_NAME);
    	if (cacheName == null || cacheName.isEmpty()) {
    		String message = "Please specify a " + ServletUtil.PARAM_CACHE_NAME + " parameter";
    		log.error(message);
    		pw.write(message);
    		return;
    	}
    	
    	String cacheKeyType = request.getParameter(ServletUtil.PARAM_CACHE_KEY_TYPE);
    	if (cacheKeyType == null || cacheKeyType.isEmpty()) {
    		String message = "Please specify a " + ServletUtil.PARAM_CACHE_KEY_TYPE + " parameter";
    		log.error(message);
    		pw.write(message);
    		return;
    	}
    	
    	String cacheValueType = request.getParameter(ServletUtil.PARAM_CACHE_VALUE_TYPE);
    	if (cacheValueType == null || cacheValueType.isEmpty()) {
    		String message = "Please specify a " + ServletUtil.PARAM_CACHE_VALUE_TYPE + " parameter";
    		log.error(message);
    		pw.write(message);
    		return;
    	}
    	
    	Class<?> keyType = null;
    	Class<?> valueType = null;
    	try {
    		keyType = Class.forName(cacheKeyType);
    	}
    	catch (LinkageError | ClassNotFoundException e) {
    		String message = "Error creating class from reflection using parameter " + 
    				ServletUtil.PARAM_CACHE_KEY_TYPE + " " + keyType + " for cache " + cacheName;
    		log.error(message, e);
    		pw.write(message);
    		return;
    	}
    	
    	try {
    		valueType = Class.forName(cacheValueType);
    	}
    	catch (LinkageError | ClassNotFoundException e) {
    		String message = "Error creating class from reflection using parameter " + 
    				ServletUtil.PARAM_CACHE_VALUE_TYPE + " " + valueType + " for cache " + cacheName;
    		log.error(message, e);
    		pw.write(message);
    		return;
    	}
    	
    	ApplicationCacheManager cacheManager = ApplicationCacheManager.getInstance();
    	try {
    		cacheManager.clearCache(cacheName, keyType, valueType);
    	}
    	catch (Exception e) {
    		String message = "Error clearing cache " + cacheName;
    		log.error(message, e);
    		pw.write(message);
    		return;
    	}
    	
    	pw.write(ServletUtil.RESULT_SUCCESS);
	}
	
	/**
	 * Clears an individual form instance from the form draft cache.
	 * 
	 * @param request The request from the client
     * @param response The response that will be sent back to the client
	 * @throws IOException
	 */
	private void clearFormInstaceFromFormDraftCache(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		
    	PrintWriter pw = response.getWriter();
		
		// parse out the location_id, form_id, location_tag_id, and form_instance_id from the selected form
		String formInstance = request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);
		FormInstanceTag formInstTag = null;
		if (formInstance != null && formInstance.trim().length() > 0) {
			try {
				formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
			} catch (Exception e) {
				String message = "Error clearing form instance " + formInstance + " from the " + AtdConstants.CACHE_FORM_DRAFT + 
						" cache.  Cannot successfully parse the form instance provided.";
	    		log.error(message, e);
	    		pw.write(message);
			}
		} else {
			String messagePart1 = "Error clearing form instance from the " + AtdConstants.CACHE_FORM_DRAFT + 
					" cache: form instance tag parameter not found.";
			String messagePart2 = "Please contact support.";
			ServletUtil.writeHtmlErrorMessage(pw, null, log, messagePart1, messagePart2);
    		return;
		}
		
		ApplicationCacheManager cacheManager = ApplicationCacheManager.getInstance();
		try {
			Cache<FormInstanceTag, Records> formCache = cacheManager.getCache(AtdConstants.CACHE_FORM_DRAFT, 
																			  AtdConstants.CACHE_FORM_DRAFT_KEY_CLASS, 
																			  AtdConstants.CACHE_FORM_DRAFT_VALUE_CLASS);
			if (formCache != null) {
				boolean existed = formCache.remove(formInstTag);
				pw.write(String.valueOf(existed));
			} else {
				String message = "The " + AtdConstants.CACHE_FORM_DRAFT + " cache cannot be located.";
				log.error(message);
	    		pw.write(message);
			}
		} catch (Exception e) {
			String message = "Error clearing form instance " + formInstance + " from the " + AtdConstants.CACHE_FORM_DRAFT + " cache.";
    		log.error(message, e);
    		pw.write(message);
		}
	}
	
	/**
	 * Converts a Tiff image into a PDF document and writes it to the response's output stream.
	 * 
	 * @param request The request object containing the parameter with the location of the Tiff image.
	 * @param response The response object where the PDF will be written.
	 * @throws IOException
	 */
	private void convertTiffToPDF(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_APPLICATION_PDF);
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CONTENT_DISPOSITION, CONTENT_DISPOSITION_PDF);
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_CACHE_CONTROL_PUBLIC + ", " + 
				ChirdlUtilConstants.HTTP_CACHE_CONTROL_MAX_AGE + "=" + ServletUtil.MAX_CACHE_AGE);
		
		String tiffFileLocation = request.getParameter(ServletUtil.PARAM_TIFF_FILE_LOCATION);
		if (StringUtils.isBlank(tiffFileLocation)) {
			try {
				IOUtil.createFormNotAvailablePDF(response.getOutputStream(), null);
			}
			catch (Exception e) {
				log.error("Error creating Form Not Available PDF", e);
			}
			
			return;
		}
		
		try {
			IOUtil.convertTifToPDF(tiffFileLocation, response.getOutputStream());
		}
		catch (Exception e) {
			log.error("Error converting tiff to PDF: " + tiffFileLocation, e);
			try {
				IOUtil.createFormNotAvailablePDF(response.getOutputStream(), tiffFileLocation);
			}
			catch (Exception e1) {
				log.error("Error creating Form Not Available PDF", e1);
			}
		}
	}
	
	/**
	 * Transforms a form XML into HTML.
	 * 
	 * @param request The request object containing the parameters to perform the transformation.
	 * @param response The response object where the HTML will be written.
	 * @throws IOException
	 */
	private void transformFormXMLToHTML(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		
		PrintWriter pw = response.getWriter();
		Integer formId = null;
		Integer locationTagId = null;
		Integer locationId = null;
		Integer formInstanceId = null;
		String stylesheet = null;
		String errorHtml = "<!DOCTYPE html><html><body style=\"font-size:6em;font-weight:bold;text-align:center;\"><p>Form</p><p>Not</p><p>Available</p></body></html>";
		
		String formIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_ID);
		try {
			formId = Integer.valueOf(formIdStr);
		} catch (NumberFormatException e) {
			log.error("Parameter {} is invalid: {}",ChirdlUtilConstants.PARAMETER_FORM_ID,formIdStr,e);
			pw.write(errorHtml);
			return;
		}
		
		String locationTagIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID);
		try {
			locationTagId = Integer.valueOf(locationTagIdStr);
		} catch (NumberFormatException e) {
			log.error("Parameter {} is invalid: {}",ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID,locationTagIdStr,  e);
			pw.write(errorHtml);
			return;
		}
		
		String locationIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_ID);
		try {
			locationId = Integer.valueOf(locationIdStr);
		} catch (NumberFormatException e) {
			log.error("Parameter {} is invalid: {}",ChirdlUtilConstants.PARAMETER_LOCATION_ID,locationIdStr, e);
			pw.write(errorHtml);
			return;
		}
		
		String formInstanceIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID);
		try {
			formInstanceId = Integer.valueOf(formInstanceIdStr);
		} catch (NumberFormatException e) {
			log.error("Parameter{} is invalid: {}", ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID,formInstanceIdStr,e);
			pw.write(errorHtml);
			return;
		}
		
		stylesheet = request.getParameter(ServletUtil.STYLESHEET);
		if (StringUtils.isBlank(stylesheet)) {
			log.error("Parameter {} is invalid: {}", ServletUtil.STYLESHEET,stylesheet);
			pw.write(errorHtml);
			return;
		}
		
		String formDirectory = request.getParameter(ServletUtil.FORM_DIRECTORY);
		if(StringUtils.isBlank(formDirectory))
		{
			log.error("Parameter {} is invalid: {}",ServletUtil.FORM_DIRECTORY,ServletUtil.FORM_DIRECTORY);
			pw.write(errorHtml);
			return;
		}
		
		String output = org.openmrs.module.chica.util.Util.displayStylesheet(formId, locationTagId, locationId, formInstanceId, 
			stylesheet, formDirectory); // CHICA-1125 Changed to use the directory specified in the config file
		if (StringUtils.isBlank(output)) {
			log.info("Transformation is empty for form ID: {} location tag ID: {} location ID: {} form instance ID: {} stylesheet: {}",
				formIdStr,locationTagIdStr,locationIdStr,formInstanceIdStr,stylesheet);
			pw.write(errorHtml);
			return;
		}
		
		pw.write(output);
	}
}
