package org.openmrs.module.chica.web.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.util.AtdConstants;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chirdlutilbackports.cache.ApplicationCacheManager;
import org.openmrs.module.chirdlutilbackports.cache.CacheStatistic;
import org.openmrs.module.chirdlutilbackports.util.ChirdlUtilBackportsConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for handling the functionality of making changes to application caches.
 *
 * @author Steve McKee
 */
@Controller
@RequestMapping(value = "module/chica/cacheConfiguration.form")
public class CacheConfigurationController {
	
	private final Log log = LogFactory.getLog(getClass());
	
	/** Form view */
    private static final String FORM_VIEW = "/module/chica/cacheConfiguration";
    
    /** Success view */
    private static final String SUCCESS_VIEW = "cacheConfiguration.form";
	
    /** Parameters */
	private static final String PARAM_ERROR_MESSAGE = "errorMessage";
	private static final String PARAM_CACHE_CONFIG_LOCATION = "cacheConfigurationLocation";
	private static final String PARAM_EHR_CACHE_HEAP_SIZE = "EHRCacheHeapSize";
	private static final String PARAM_EHR_CACHE_HEAP_SIZE_UNIT = "EHRCacheHeapSizeUnit";
	private static final String PARAM_EHR_CACHE_DISK_SIZE = "EHRCacheDiskSize";
	private static final String PARAM_EHR_CACHE_DISK_SIZE_UNIT = "EHRCacheDiskSizeUnit";
	private static final String PARAM_EHR_CACHE_EXPIRY = "EHRCacheExpiry";
	private static final String PARAM_EHR_CACHE_EXPIRY_UNIT = "EHRCacheExpiryUnit";
	private static final String PARAM_EHR_CACHE_STATISTICS = "EHRCacheStatistics";
	private static final String PARAM_FORM_DRAFT_CACHE_HEAP_SIZE = "formDraftCacheHeapSize";
	private static final String PARAM_FORM_DRAFT_CACHE_HEAP_SIZE_UNIT = "formDraftCacheHeapSizeUnit";
	private static final String PARAM_FORM_DRAFT_CACHE_DISK_SIZE = "formDraftCacheDiskSize";
	private static final String PARAM_FORM_DRAFT_CACHE_DISK_SIZE_UNIT = "formDraftCacheDiskSizeUnit";
	private static final String PARAM_FORM_DRAFT_CACHE_EXPIRY = "formDraftCacheExpiry";
	private static final String PARAM_FORM_DRAFT_CACHE_EXPIRY_UNIT = "formDraftCacheExpiryUnit";
	private static final String PARAM_FORM_DRAFT_CACHE_STATISTICS = "formDraftCacheStatistics";
	
	/**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(method = RequestMethod.POST)
    protected ModelAndView processSubmit(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<>();
		String ehrCacheHeapSizeStr = request.getParameter(PARAM_EHR_CACHE_HEAP_SIZE);
		String formDraftCacheHeapSizeStr = request.getParameter(PARAM_FORM_DRAFT_CACHE_HEAP_SIZE);
		
		// Update the EHR Medical Record Cache heap size
		updateCacheHeapSize(ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
							ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
							ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS, 
							ehrCacheHeapSizeStr, model);
		
		// A non-empty model reflects errors occurred updating the heap size
		if (!model.isEmpty()) {
			return new ModelAndView(new RedirectView(SUCCESS_VIEW), model);
		}
		
		// A non-empty model reflects errors occurred updating the heap size
		if (!model.isEmpty()) {
			return new ModelAndView(new RedirectView(SUCCESS_VIEW), model);
		}
		
		// Update the Form Draft Cache heap size
		updateCacheHeapSize(AtdConstants.CACHE_FORM_DRAFT, 
							AtdConstants.CACHE_FORM_DRAFT_KEY_CLASS, 
							AtdConstants.CACHE_FORM_DRAFT_VALUE_CLASS, 
							formDraftCacheHeapSizeStr, model);
		
		// A non-empty model reflects errors occurred updating the heap size
		if (!model.isEmpty()) {
			return new ModelAndView(new RedirectView(SUCCESS_VIEW), model);
		}
		
		return new ModelAndView(new RedirectView(SUCCESS_VIEW));
	}
	
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    protected String initForm(HttpServletRequest request, ModelMap map) {
		User user = Context.getUserContext().getAuthenticatedUser();
		if (user == null) {
			return null;
		}
		
		String errorMessage = request.getParameter(PARAM_ERROR_MESSAGE);
		map.put(PARAM_ERROR_MESSAGE, errorMessage);
		ApplicationCacheManager cacheManager = ApplicationCacheManager.getInstance();
		
		URI cacheLocationURI = cacheManager.getCacheConfigurationFileLocation();
		if (cacheLocationURI != null) {
		    map.put(PARAM_CACHE_CONFIG_LOCATION, cacheLocationURI.toString());
		}
		
		// Retrieve data for the EHR cache
		loadEHRMedicalRecordCacheInfo(cacheManager, map);
		
		// Retrieve data for the form draft cache
		loadFormDraftCacheInfo(cacheManager, map);
		
		return FORM_VIEW;
	}
	
	/**
	 * Loads all the specific information about the EHR Medical Record Cache.
	 * 
	 * @param cacheManager The Application Cache Manger to access the cache information
	 * @param model Map containing the HTTP information to display to the client
	 */
	private void loadEHRMedicalRecordCacheInfo(ApplicationCacheManager cacheManager, Map<String, Object> model) {
		Long ehrCacheHeapSize = cacheManager.getCacheHeapSize(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_HEAP_SIZE, ehrCacheHeapSize);
		
		String ehrCacheHeapSizeUnit = cacheManager.getCacheHeapSizeUnit(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_HEAP_SIZE_UNIT, ehrCacheHeapSizeUnit);
		
		Long ehrCacheDiskSize = cacheManager.getCacheDiskSize(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_DISK_SIZE, ehrCacheDiskSize);
		
		String ehrCacheDiskSizeUnit = cacheManager.getCacheDiskSizeUnit(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_DISK_SIZE_UNIT, ehrCacheDiskSizeUnit);
		
		Long ehrCacheExpiry = cacheManager.getCacheExpiry(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_EXPIRY, ehrCacheExpiry);
		
		String ehrCacheExpiryUnit = cacheManager.getCacheExpiryUnit(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_EXPIRY_UNIT, ehrCacheExpiryUnit);
		
		// load the cache statistics
		List<CacheStatistic> stats = cacheManager.getCacheStatistics(
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
			ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS);
		model.put(PARAM_EHR_CACHE_STATISTICS, stats);
	}
	
	
	/**
	 * Loads all the specific information about the Form Draft.
	 * 
	 * @param cacheManager The Application Cache Manger to access the cache information
	 * @param model Map containing the HTTP information to display to the client
	 */
	private void loadFormDraftCacheInfo(ApplicationCacheManager cacheManager, Map<String, Object> model) {
		Long formDraftCacheHeapSize = cacheManager.getCacheHeapSize(
			AtdConstants.CACHE_FORM_DRAFT, 
			AtdConstants.CACHE_FORM_DRAFT_KEY_CLASS, 
			AtdConstants.CACHE_FORM_DRAFT_VALUE_CLASS);
		model.put(PARAM_FORM_DRAFT_CACHE_HEAP_SIZE, formDraftCacheHeapSize);
		
		String formDraftCacheHeapSizeUnit = cacheManager.getCacheHeapSizeUnit(
			AtdConstants.CACHE_FORM_DRAFT, 
			AtdConstants.CACHE_FORM_DRAFT_KEY_CLASS, 
			AtdConstants.CACHE_FORM_DRAFT_VALUE_CLASS);
		model.put(PARAM_FORM_DRAFT_CACHE_HEAP_SIZE_UNIT, formDraftCacheHeapSizeUnit);
		
		Long formDraftCacheDiskSize = cacheManager.getCacheDiskSize(
			AtdConstants.CACHE_FORM_DRAFT, 
			AtdConstants.CACHE_FORM_DRAFT_KEY_CLASS, 
			AtdConstants.CACHE_FORM_DRAFT_VALUE_CLASS);
		model.put(PARAM_FORM_DRAFT_CACHE_DISK_SIZE, formDraftCacheDiskSize);
		
		String formDraftCacheDiskSizeUnit = cacheManager.getCacheDiskSizeUnit(
			AtdConstants.CACHE_FORM_DRAFT, 
			AtdConstants.CACHE_FORM_DRAFT_KEY_CLASS, 
			AtdConstants.CACHE_FORM_DRAFT_VALUE_CLASS);
		model.put(PARAM_FORM_DRAFT_CACHE_DISK_SIZE_UNIT, formDraftCacheDiskSizeUnit);
		
		Long formDraftCacheExpiry = cacheManager.getCacheExpiry(
			AtdConstants.CACHE_FORM_DRAFT, 
			AtdConstants.CACHE_FORM_DRAFT_KEY_CLASS, 
			AtdConstants.CACHE_FORM_DRAFT_VALUE_CLASS);
		model.put(PARAM_FORM_DRAFT_CACHE_EXPIRY, formDraftCacheExpiry);
		
		String formDraftCacheExpiryUnit = cacheManager.getCacheExpiryUnit(
			AtdConstants.CACHE_FORM_DRAFT, 
			AtdConstants.CACHE_FORM_DRAFT_KEY_CLASS, 
			AtdConstants.CACHE_FORM_DRAFT_VALUE_CLASS);
		model.put(PARAM_FORM_DRAFT_CACHE_EXPIRY_UNIT, formDraftCacheExpiryUnit);
		
		// load the cache statistics
		List<CacheStatistic> stats = cacheManager.getCacheStatistics(
			AtdConstants.CACHE_FORM_DRAFT, 
			AtdConstants.CACHE_FORM_DRAFT_KEY_CLASS, 
			AtdConstants.CACHE_FORM_DRAFT_VALUE_CLASS);
		model.put(PARAM_FORM_DRAFT_CACHE_STATISTICS, stats);
	}
	
	/**
	 * Updates a cache's heap size.  It will only update the heap size if the provided value is different than the current 
	 * value.
	 * 
	 * @param cacheName The name of the cache
	 * @param keyClass The key class of the cache
	 * @param valueClass The value class of the cache
	 * @param newCacheHeapSizeStr The new heap size value
	 * @param model Map used for error handling
	 */
	private void updateCacheHeapSize(String cacheName, Class<?> keyClass, Class<?> valueClass, String newCacheHeapSizeStr, 
	        Map<String, Object> model) {
		Long newCacheHeapSize = null;
		try {
			newCacheHeapSize = Long.parseLong(newCacheHeapSizeStr);
		} catch (NumberFormatException e) {
		    String message = "The " + cacheName + " cache heap size specified is not a valid value.";
		    log.error(message, e);
			model.put(PARAM_ERROR_MESSAGE, message);
			return;
		}
		
		ApplicationCacheManager cacheManager = ApplicationCacheManager.getInstance();
		
		// Look at the old setting to see if anything changed
		Long currentCacheHeapSize = cacheManager.getCacheHeapSize(cacheName, keyClass, valueClass);
		if (currentCacheHeapSize == null || (currentCacheHeapSize.longValue() != newCacheHeapSize)) {
			try {
				cacheManager.updateCacheHeapSize(cacheName, keyClass, valueClass, newCacheHeapSize);
			} catch (Exception e) {
				log.error("Error updating the " + cacheName + " cache heap size.", e);
				model.put(PARAM_ERROR_MESSAGE, "An error occurred saving the " + cacheName + " cache heap size: " + 
				        e.getMessage());
			}
		}
	}
}
