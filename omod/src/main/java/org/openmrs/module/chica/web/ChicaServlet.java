package org.openmrs.module.chica.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.cache.Cache;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.util.AtdConstants;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.cache.ApplicationCacheManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Servlet giving access to CHICA information.
 *
 * @author Steve McKee
 */
public class ChicaServlet extends HttpServlet {
	
	public static final String CHICA_SERVLET_URL = "/moduleServlet/chica/chica";
	public static final String CHICA_SERVLET_PDF_PARAMS = "#view=fit&navpanes=0";
	
	private static final long serialVersionUID = 1L;
	
	private static final String WILL_KEEP_ALIVE = "OK";
	
	private static final Log LOG = LogFactory.getLog(ChicaServlet.class);
	
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
    				ChirdlUtilConstants.HTTP_HEADER_CONTENT_DISPOSITION, ServletUtil.CONTENT_DISPOSITION_PDF);
    		} else if (ServletUtil.FORCE_PRINT_FORMS.equals(action)) {
    			ServletUtil.getForcePrintFormHeader(request, response);
    		}
	    }catch(IOException e){
	        LOG.error("IOException in ChicaServlet.", e);
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
		    	getPatientJITs(request, response);
		    } else if (ServletUtil.GET_AVAILABLE_PATIENT_JITS.equals(action)) {
		    	ServletUtil.getAvailablePatientJITs(request, response);
		    } else if (ServletUtil.GET_FORCE_PRINT_FORMS.equals(action)) {
		    	ServletUtil.getForcePrintForms(request, response);
		    } else if (ServletUtil.FORCE_PRINT_FORMS.equals(action)) {
		    	ServletUtil.forcePrintForms(request, response);
		    } else if (ServletUtil.DISPLAY_FORCE_PRINT_FORMS.equals(action)) {
		    	getPatientJITs(request, response);
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
		    LOG.error("IOException in ChicaServlet.", ioe);
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
            LOG.error("ServletException in ChicaServlet", e);
        }
	}
	
	/**
	 * Retrieves the patient's JITs based on form instances provided.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void getPatientJITs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String formInstances = request.getParameter(ServletUtil.PARAM_FORM_INSTANCES);
		locatePatientJITs(response, formInstances);
	}
	
	/**
	 * Locates and loads the patient JITs based on the formInstances provided.
	 * 
	 * @param response HttpServletResponse
	 * @param formInstances The form instances to load.  This should be a comma delimited list.
	 * @throws IOException
	 */
	private void locatePatientJITs(HttpServletResponse response, String formInstances) 
			throws IOException {	
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_APPLICATION_PDF);
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CONTENT_DISPOSITION, ServletUtil.CONTENT_DISPOSITION_PDF);
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_CACHE_CONTROL_PUBLIC + ", " + 
				ChirdlUtilConstants.HTTP_CACHE_CONTROL_MAX_AGE + "=" + ServletUtil.MAX_CACHE_AGE);
		
		if (formInstances == null) {
			try {
	            writePdfTextToResponse("There are no forms to display.", response);
            }
            catch (DocumentException e) {
                LOG.error("Error creating error message PDF", e);
	            return;
            }
			
			return;
		}
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		List<String> pdfFiles = new ArrayList<>();
		List<FormInstanceTag> teleformFiles = new ArrayList<>();
		List<FormInstanceTag> failedFiles = new ArrayList<>();
		for (String formInstance : formInstances.split(ChirdlUtilConstants.GENERAL_INFO_COMMA)) {
			FormInstanceTag formInstanceTag = FormInstanceTag.parseFormInstanceTag(formInstance);
			if (formInstanceTag == null) {
				continue;
			}
			
			File mergeFile = locatePatientJIT(formInstanceTag, backportsService);
			if (mergeFile == null) {
				failedFiles.add(formInstanceTag);
			} else if (mergeFile.getAbsolutePath().toLowerCase().endsWith(ChirdlUtilConstants.FILE_PDF)) {
				pdfFiles.add(mergeFile.getAbsolutePath());
			} else {
				teleformFiles.add(formInstanceTag);
			}
		}
		
		int pdfListSize = pdfFiles.size();
		if (pdfListSize == 0 && teleformFiles.size() > 0) {
			loadPatientTeleformJITs(response, teleformFiles);
			return;
		} else if (pdfListSize == 0 && failedFiles.size() > 0) {
			loadPatientErrorJITs(response, failedFiles);
			return;
		} else if (pdfListSize == 0) {
			String message = "An error occurred locating the file(s) to display.";
			try {
	            writePdfTextToResponse(message, response);
            }
            catch (DocumentException e) {
                LOG.error("Error creating error PDF document", e);
            }
			
			return;
		} 
		
		loadPatientPdfJITs(response, pdfFiles);
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
    		LOG.error(message);
    		pw.write(message);
    		return;
    	}
    	
    	String cacheKeyType = request.getParameter(ServletUtil.PARAM_CACHE_KEY_TYPE);
    	if (cacheKeyType == null || cacheKeyType.isEmpty()) {
    		String message = "Please specify a " + ServletUtil.PARAM_CACHE_KEY_TYPE + " parameter";
    		LOG.error(message);
    		pw.write(message);
    		return;
    	}
    	
    	String cacheValueType = request.getParameter(ServletUtil.PARAM_CACHE_VALUE_TYPE);
    	if (cacheValueType == null || cacheValueType.isEmpty()) {
    		String message = "Please specify a " + ServletUtil.PARAM_CACHE_VALUE_TYPE + " parameter";
    		LOG.error(message);
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
    		LOG.error(message, e);
    		pw.write(message);
    		return;
    	}
    	
    	try {
    		valueType = Class.forName(cacheValueType);
    	}
    	catch (LinkageError | ClassNotFoundException e) {
    		String message = "Error creating class from reflection using parameter " + 
    				ServletUtil.PARAM_CACHE_VALUE_TYPE + " " + valueType + " for cache " + cacheName;
    		LOG.error(message, e);
    		pw.write(message);
    		return;
    	}
    	
    	ApplicationCacheManager cacheManager = ApplicationCacheManager.getInstance();
    	try {
    		cacheManager.clearCache(cacheName, keyType, valueType);
    	}
    	catch (Exception e) {
    		String message = "Error clearing cache " + cacheName;
    		LOG.error(message, e);
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
	    		LOG.error(message, e);
	    		pw.write(message);
			}
		} else {
			String messagePart1 = "Error clearing form instance from the " + AtdConstants.CACHE_FORM_DRAFT + 
					" cache: form instance tag parameter not found.";
			String messagePart2 = "Please contact support.";
			ServletUtil.writeHtmlErrorMessage(pw, null, LOG, messagePart1, messagePart2);
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
				LOG.error(message);
	    		pw.write(message);
			}
		} catch (Exception e) {
			String message = "Error clearing form instance " + formInstance + " from the " + AtdConstants.CACHE_FORM_DRAFT + " cache.";
    		LOG.error(message, e);
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
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CONTENT_DISPOSITION, ServletUtil.CONTENT_DISPOSITION_PDF);
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_CACHE_CONTROL_PUBLIC + ", " + 
				ChirdlUtilConstants.HTTP_CACHE_CONTROL_MAX_AGE + "=" + ServletUtil.MAX_CACHE_AGE);
		
		String tiffFileLocation = request.getParameter(ServletUtil.PARAM_TIFF_FILE_LOCATION);
		if (StringUtils.isBlank(tiffFileLocation)) {
			try {
				IOUtil.createFormNotAvailablePDF(response.getOutputStream(), null);
			}
			catch (Exception e) {
				LOG.error("Error creating Form Not Available PDF", e);
			}
			
			return;
		}
		
		try {
			IOUtil.convertTifToPDF(tiffFileLocation, response.getOutputStream());
		}
		catch (Exception e) {
			LOG.error("Error converting tiff to PDF: " + tiffFileLocation, e);
			try {
				IOUtil.createFormNotAvailablePDF(response.getOutputStream(), tiffFileLocation);
			}
			catch (Exception e1) {
				LOG.error("Error creating Form Not Available PDF", e1);
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
			LOG.error("Parameter " + ChirdlUtilConstants.PARAMETER_FORM_ID + " is invalid: " + formIdStr, e);
			pw.write(errorHtml);
			return;
		}
		
		String locationTagIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID);
		try {
			locationTagId = Integer.valueOf(locationTagIdStr);
		} catch (NumberFormatException e) {
			LOG.error("Parameter " + ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID + " is invalid: " + locationTagIdStr, e);
			pw.write(errorHtml);
			return;
		}
		
		String locationIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_ID);
		try {
			locationId = Integer.valueOf(locationIdStr);
		} catch (NumberFormatException e) {
			LOG.error("Parameter " + ChirdlUtilConstants.PARAMETER_LOCATION_ID + " is invalid: " + locationIdStr, e);
			pw.write(errorHtml);
			return;
		}
		
		String formInstanceIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID);
		try {
			formInstanceId = Integer.valueOf(formInstanceIdStr);
		} catch (NumberFormatException e) {
			LOG.error("Parameter " + ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID + " is invalid: " + formInstanceIdStr, e);
			pw.write(errorHtml);
			return;
		}
		
		stylesheet = request.getParameter(ServletUtil.STYLESHEET);
		if (StringUtils.isBlank(stylesheet)) {
			LOG.error("Parameter " + ServletUtil.STYLESHEET + " is invalid: " + stylesheet);
			pw.write(errorHtml);
			return;
		}
		
		String formDirectory = request.getParameter(ServletUtil.FORM_DIRECTORY);
		if(StringUtils.isBlank(formDirectory))
		{
			LOG.error("Parameter " + ServletUtil.FORM_DIRECTORY + " is invalid: " + ServletUtil.FORM_DIRECTORY);
			pw.write(errorHtml);
			return;
		}
		
		String output = org.openmrs.module.chica.util.Util.displayStylesheet(formId, locationTagId, locationId, formInstanceId, 
			stylesheet, formDirectory); // CHICA-1125 Changed to use the directory specified in the config file
		if (StringUtils.isBlank(output)) {
			LOG.info("Transformation is empty for form ID: " + formIdStr + " location tag ID: " + locationTagIdStr + 
				" location ID: " + locationIdStr + " form instance ID: " + formInstanceIdStr + " stylesheet: " + stylesheet);
			pw.write(errorHtml);
			return;
		}
		
		pw.write(output);
	}
	
	/**
	 * Creates a PDF with an error message for the forms the failed and writes it to the HTTP response.
	 * 
	 * @param response The HttpServletResponse where the PDF will be written.
	 * @param errorFiles List of FormInstanceTag objects that failed.
	 * @throws IOException
	 */
	private void loadPatientErrorJITs(HttpServletResponse response, List<FormInstanceTag> errorFiles) 
			throws IOException {
		String subject = "form";
		if (errorFiles.size() > 1) {
			subject = "forms";
		}
		
		StringBuffer message = new StringBuffer("An error occurred creating the following ").append(subject).append(": ");
		for (int i = 0; i < errorFiles.size(); i++) {
			FormInstanceTag formInstanceTag = errorFiles.get(i);
			if (i != 0) {
				message.append(", ");
			}
			
			String formName = ServletUtil.getFormName(
				formInstanceTag.getFormId(), formInstanceTag.getLocationId(), formInstanceTag.getLocationTagId());
			message.append(formName);
		}
		
		message.append(".");
		try {
            writePdfTextToResponse(message.toString(), response);
        }
        catch (DocumentException e) {
            LOG.error("Error creating error PDF document", e);
        }
	}
	
	/**
	 * Creates a PDF with an informational message about the Teleform files that were processed 
	 * and writes it to the HTTP response.
	 * 
	 * @param response The HttpServletResponse where the PDF will be written.
	 * @param teleformFiles List of FormInstanceTag objects that used for Teleform.
	 * @throws IOException
	 */
	private void loadPatientTeleformJITs(HttpServletResponse response, List<FormInstanceTag> teleformFiles) 
			throws IOException {
		String subject = "form";
		String verb = "has";
		if (teleformFiles.size() > 1) {
			subject = "forms";
			verb = "have";
		}
		
		StringBuffer message = new StringBuffer("The following ").append(subject).append(" ").append(verb).append(
			" been successfully sent to the printer: ");
		for (int i = 0; i < teleformFiles.size(); i++) {
			FormInstanceTag formInstanceTag = teleformFiles.get(i);
			if (i != 0) {
				message.append(", ");
			}
			
			String formName = ServletUtil.getFormName(
				formInstanceTag.getFormId(), formInstanceTag.getLocationId(), formInstanceTag.getLocationTagId());
			message.append(formName);
		}
		
		message.append(".");
		
		// CHICA-962
		message.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED)
		.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED)
		.append("Please pick the ").append(subject).append(" up at the printer. DO NOT print this page.");
		
		try {
            writePdfTextToResponse(message.toString(), response);
        }
        catch (DocumentException e) {
            LOG.error("Error creating error PDF document", e);
        }
	}
	
	/**
	 * Creates a new PDF with the provided text and writes it to the HttpServletResponse object
	 * 
	 * @param text The text to insert into the PDF
	 * @param response The HttpServletResponse object where the PDF will be written
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void writePdfTextToResponse(String text, HttpServletResponse response) 
			throws DocumentException, IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Document document = new Document();
		try {
	        PdfWriter.getInstance(document, output);
	        document.open();
	        document.add(new Paragraph(text));
	        document.close();
		    response.setContentLength(output.size());
		    response.getOutputStream().write(output.toByteArray());
		} finally {
			output.flush();
		    output.close();
		}
	}
	
	/**
	 * Locates the merge file for the JIT whether it be PDF or Teleform XML.
	 * 
	 * @param formInstanceTag The form instance tag information needed to locate the merge file.
	 * @param backportsService ChirdlUtilBackportsService object used to find the form's merge directory.
	 * @return File containing the merge file location or null if it could not be found.
	 */
	private File locatePatientJIT(FormInstanceTag formInstanceTag, ChirdlUtilBackportsService backportsService) {
		Integer locationId = formInstanceTag.getLocationId();
		Integer formId = formInstanceTag.getFormId();
		Integer formInstanceId = formInstanceTag.getFormInstanceId();
		Integer locationTagId = formInstanceTag.getLocationTagId();
		
		// Get the merge directory for the form.
		FormAttributeValue fav = 
				backportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, 
					locationTagId, locationId);
		if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
			return null;
		}
		
		// Attempt to find the merge PDF file.
		String mergeDirectory = fav.getValue();
		File pdfDir = new File(mergeDirectory, ChirdlUtilConstants.FILE_PDF);
		File mergeFile = new File(pdfDir, locationId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formId + 
			ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formInstanceId + ChirdlUtilConstants.FILE_EXTENSION_PDF);
		if (!mergeFile.exists() || mergeFile.length() == 0) {
			mergeFile = new File(pdfDir, ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + locationId + 
				ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + 
				formInstanceId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + ChirdlUtilConstants.FILE_EXTENSION_PDF);
			if (mergeFile.exists() && mergeFile.length() > 0) {
				return mergeFile;
			}
		} else {
			return mergeFile;
		}
		
		// Attempt to find the XML file.
		mergeFile = XMLUtil.findMergeXmlFile(mergeDirectory, locationId, formId, formInstanceId);
		if (mergeFile != null && mergeFile.exists() && mergeFile.length() > 0) {
			return mergeFile;
		}
		
		return null;
	}
	
	/**
	 * Combines the provided PDF files into one PDF document and writes it to the HTTP response.
	 * 
	 * @param response The HttpServletResponse where the PDF will be written.
	 * @param pdfFiles List of files locations for PDF forms to combine.
	 * @throws IOException
	 */
	private void loadPatientPdfJITs(HttpServletResponse response, List<String> pdfFiles) throws IOException {
		if (pdfFiles.size() == 1) {
			String filePath = null;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try {
				Document document = new Document();
		        PdfCopy copy = new PdfCopy(document, output);
		        document.open();
		        PdfReader reader;
		        int n;
	        	filePath = pdfFiles.get(0);
	            reader = new PdfReader(filePath);
	            // loop over the pages in that document
	            n = reader.getNumberOfPages();
	            for (int page = 0; page < n; ) {
	            	try {
	            		copy.addPage(copy.getImportedPage(reader, ++page));
	            	} catch (Exception e) {
	            		LOG.error("Error adding page", e);
	            	}
	            }
	            
	            copy.freeReader(reader);
	            reader.close();
	
		        document.close();
		        copy.close();
		        response.setContentLength(output.size());
		        response.getOutputStream().write(output.toByteArray());
			} catch (BadPdfFormatException e) {
				LOG.error("Bad PDF found: " + filePath, e);
				throw new IOException(e);
			} catch (DocumentException e) {
				LOG.error("Error handling PDF document: " + filePath, e);
				throw new IOException(e);
			} finally {
				output.flush();
		        output.close();
			}
		} else {
			// DWE CHICA-500 Allow multiple PDFs to be selected/combined into 
			// a single document for printing. If the document has an odd number of pages,
			// a blank page will be added so that the next document will not be printed on the back of the 
			// previous document when printing duplex
			String filePath = "";
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				Document doc = new Document();
		        PdfCopy copy = new PdfCopy(doc, baos);
		        doc.open();
		        PdfReader reader = null;
		        
		        for (int i = 0; i < pdfFiles.size(); i++) {
		        	filePath = pdfFiles.get(i);
		        	reader = new PdfReader(renamePdfFields(filePath, i));
		        	
		        	// Loop over the pages in the document
		            int numOfPages = reader.getNumberOfPages();
		            
		            for (int page = 1; page <= numOfPages; page++) {
		            	try {		            		
		            		// When forms are combined, we need to check to see if the view is landscape
		            		// If it is, we need to rotate it otherwise Firefox will shrink the pages to fit in portrait
		            		// which causes pages that should be printed in portrait to be shrunk as well
		            		int rot = reader.getPageRotation(page);
		            		if(rot == 90 || rot == 270)
		            		{
		            			PdfDictionary pageDict = reader.getPageN(page);
			            		pageDict.put(PdfName.ROTATE, new PdfNumber(0));
		            		}
		            					            	
		                copy.addPage(copy.getImportedPage(reader, page));
		            	} catch (Exception e) {
		            		LOG.error("Error adding page", e);
		            	}
		            }
		            
		            // Add blank page if the document has an odd number of pages
		            if(numOfPages % 2 != 0)
		            {		            	
		            	copy.addPage(reader.getPageSize(1), reader.getPageRotation(1));
		            }
		        }
		        		        
		        copy.freeReader(reader);
	            reader.close();
		        doc.close();		        
		        copy.close();
		       
		        response.setContentLength(baos.size());
		        response.getOutputStream().write(baos.toByteArray());
			} catch (BadPdfFormatException e) {
				LOG.error("Bad PDF found: " + filePath, e);
				throw new IOException(e);
			} catch (DocumentException e) {
				LOG.error("Error handling PDF document", e);
				throw new IOException(e);
			} finally {
				baos.flush();
		        baos.close();
			}
		}
	}
	
	/**
	 * Renames the fields in the PDF.  This needs to be done due to fields with the same name in the document.
	 * 
	 * @param pdfFile The PDF file that will have its fields renamed.
	 * @param instance The instance of the field in the PDF document.
	 * @return Array of bytes of the PDF document after the fields are renamed.
	 * @throws IOException
	 * @throws DocumentException
	 */
	private byte[] renamePdfFields(String pdfFile, int instance) throws IOException, DocumentException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Create the stamper
		PdfStamper stamper = new PdfStamper(new PdfReader(pdfFile), baos);
		// Get the fields
		AcroFields form = stamper.getAcroFields();
		// Loop over the fields
		Set<String> keys = new HashSet<>(form.getFields().keySet());
		for (String key : keys) {
			// rename the fields
			form.renameField(key, String.format("%s_%d", key, instance));
		}
		// close the stamper
		stamper.close();
		return baos.toByteArray();
	}
}
