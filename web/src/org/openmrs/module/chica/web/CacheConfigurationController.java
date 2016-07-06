package org.openmrs.module.chica.web;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutilbackports.cache.ApplicationCacheManager;
import org.openmrs.module.chirdlutilbackports.cache.CacheStatistic;
import org.openmrs.module.chirdlutilbackports.util.ChirdlUtilBackportsConstants;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for handling the functionality of making changes to application caches.
 *
 * @author Steve McKee
 */
public class CacheConfigurationController extends SimpleFormController {
	
	private final Log log = LogFactory.getLog(getClass());
	
	private static final String PARAM_ERROR_MESSAGE = "errorMessage";
	private static final String PARAM_CACHE_CONFIG_LOCATION = "cacheConfigurationLocation";
	private static final String PARAM_EHR_CACHE_HEAP_SIZE = "EHRCacheHeapSize";
	private static final String PARAM_EHR_CACHE_HEAP_SIZE_UNIT = "EHRCacheHeapSizeUnit";
	private static final String PARAM_EHR_CACHE_DISK_SIZE = "EHRCacheDiskSize";
	private static final String PARAM_EHR_CACHE_DISK_SIZE_UNIT = "EHRCacheDiskSizeUnit";
	private static final String PARAM_EHR_CACHE_EXPIRY = "EHRCacheExpiry";
	private static final String PARAM_EHR_CACHE_EXPIRY_UNIT = "EHRCacheExpiryUnit";
	private static final String PARAM_EHR_CACHE_STATISTICS = "EHRCacheStatistics";
	
	/**
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
	                                BindException errors) throws Exception {
		String EHRCacheHeapSizeStr = request.getParameter(PARAM_EHR_CACHE_HEAP_SIZE);
		String EHRCacheHeapSizeUnit = request.getParameter(PARAM_EHR_CACHE_HEAP_SIZE_UNIT);
		Integer EHRCacheHeapSize = null;
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			EHRCacheHeapSize = Integer.parseInt(EHRCacheHeapSizeStr);
		} catch (NumberFormatException e) {
			map.put(PARAM_ERROR_MESSAGE, "The EHR Medical Record Cache heap size specified is not a valid integer.");
			return new ModelAndView(new RedirectView(getSuccessView()), map);
		}
		
		ApplicationCacheManager cacheManager = ApplicationCacheManager.getInstance();
		try {
			cacheManager.updateCacheHeapSize(
				ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
				ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
				ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS, 
				EHRCacheHeapSize, EHRCacheHeapSizeUnit);
		} catch (Exception e) {
			log.error("Error updating the EHR Medical Record Cache heap size.", e);
			map.put(PARAM_ERROR_MESSAGE, "An error occurred saving the EHR Medical Record Cache heap size: " + e.getMessage());
			return new ModelAndView(new RedirectView(getSuccessView()), map);
		}
		
		return new ModelAndView(new RedirectView(getSuccessView()));
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		User user = Context.getUserContext().getAuthenticatedUser();
		if (user == null) {
			return null;
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		String errorMessage = request.getParameter(PARAM_ERROR_MESSAGE);
		model.put(PARAM_ERROR_MESSAGE, errorMessage);
		ApplicationCacheManager cacheManager = ApplicationCacheManager.getInstance();
		
		URI cacheLocationURI = cacheManager.getCacheConfigurationFileLocation();
		if (cacheLocationURI != null) {
			model.put(PARAM_CACHE_CONFIG_LOCATION, cacheLocationURI.toString());
		}
		
		// Retrieve data for the EHR cache
		loadEHRMedicalRecordCacheInfo(cacheManager, model);
		
		return model;
	}
	
	/**
	 * Loads all the specific information about the EHR Medical Record Cache.
	 * 
	 * @param cacheManager The Application Cache Manger to access the cache information
	 * @param model Map containing the HTTP information to display to the client
	 */
	private void loadEHRMedicalRecordCacheInfo(ApplicationCacheManager cacheManager, Map<String, Object> model) {
		Long EHRCacheHeapSize = cacheManager.getCacheHeapSize(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_HEAP_SIZE, EHRCacheHeapSize);
		
		String EHRCacheHeapSizeUnit = cacheManager.getCacheHeapSizeUnit(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_HEAP_SIZE_UNIT, EHRCacheHeapSizeUnit);
		
		Long EHRCacheDiskSize = cacheManager.getCacheDiskSize(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_DISK_SIZE, EHRCacheDiskSize);
		
		String EHRCacheDiskSizeUnit = cacheManager.getCacheDiskSizeUnit(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_DISK_SIZE_UNIT, EHRCacheDiskSizeUnit);
		
		Long EHRCacheExpiry = cacheManager.getCacheExpiry(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_EXPIRY, EHRCacheExpiry);
		
		String EHRCacheExpiryUnit = cacheManager.getCacheExpiryUnit(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_EXPIRY_UNIT, EHRCacheExpiryUnit);
		
		// load the cache statistics
		List<CacheStatistic> stats = cacheManager.getCacheStatistics(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_STATISTICS, stats);
	}
}
