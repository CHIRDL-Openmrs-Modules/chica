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
import org.openmrs.module.atd.util.AtdConstants;
import org.openmrs.module.chica.util.ChicaConstants;
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
	private static final String PARAM_IMMUNIZATION_CACHE_HEAP_SIZE = "immunizationCacheHeapSize";
	private static final String PARAM_IMMUNIZATION_CACHE_HEAP_SIZE_UNIT = "immunizationCacheHeapSizeUnit";
	private static final String PARAM_IMMUNIZATION_CACHE_DISK_SIZE = "immunizationCacheDiskSize";
	private static final String PARAM_IMMUNIZATION_CACHE_DISK_SIZE_UNIT = "immunizationCacheDiskSizeUnit";
	private static final String PARAM_IMMUNIZATION_CACHE_EXPIRY = "immunizationCacheExpiry";
	private static final String PARAM_IMMUNIZATION_CACHE_EXPIRY_UNIT = "immunizationCacheExpiryUnit";
	private static final String PARAM_IMMUNIZATION_CACHE_STATISTICS = "immunizationCacheStatistics";
	private static final String PARAM_FORM_DRAFT_CACHE_HEAP_SIZE = "formDraftCacheHeapSize";
	private static final String PARAM_FORM_DRAFT_CACHE_HEAP_SIZE_UNIT = "formDraftCacheHeapSizeUnit";
	private static final String PARAM_FORM_DRAFT_CACHE_DISK_SIZE = "formDraftCacheDiskSize";
	private static final String PARAM_FORM_DRAFT_CACHE_DISK_SIZE_UNIT = "formDraftCacheDiskSizeUnit";
	private static final String PARAM_FORM_DRAFT_CACHE_EXPIRY = "formDraftCacheExpiry";
	private static final String PARAM_FORM_DRAFT_CACHE_EXPIRY_UNIT = "formDraftCacheExpiryUnit";
	private static final String PARAM_FORM_DRAFT_CACHE_STATISTICS = "formDraftCacheStatistics";
	
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
		Map<String, Object> model = new HashMap<String, Object>();
		String EHRCacheHeapSizeStr = request.getParameter(PARAM_EHR_CACHE_HEAP_SIZE);
		String immunizationCacheHeapSizeStr = request.getParameter(PARAM_IMMUNIZATION_CACHE_HEAP_SIZE);
		
		// Update the EHR Medical Record Cache heap size
		updateCacheHeapSize(ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD, 
							ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_KEY_CLASS, 
							ChirdlUtilBackportsConstants.CACHE_EHR_MEDICAL_RECORD_VALUE_CLASS, 
							EHRCacheHeapSizeStr, model);
		
		// A non-empty model reflects errors occurred updating the heap size
		if (!model.isEmpty()) {
			return new ModelAndView(new RedirectView(getSuccessView()), model);
		}
		
		// Update the Immunization Cache heap size
		updateCacheHeapSize(ChicaConstants.CACHE_IMMUNIZATION, 
							ChicaConstants.CACHE_IMMUNIZATION_KEY_CLASS, 
							ChicaConstants.CACHE_IMMUNIZATION_VALUE_CLASS, 
							immunizationCacheHeapSizeStr, model);
		
		// A non-empty model reflects errors occurred updating the heap size
		if (!model.isEmpty()) {
			return new ModelAndView(new RedirectView(getSuccessView()), model);
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
		
		// Retrieve data for the immunization cache
		loadImmunizationCacheInfo(cacheManager, model);
		
		// Retrieve data for the form draft cache
		loadFormDraftCacheInfo(cacheManager, model);
		
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
	
	/**
	 * Loads all the specific information about the Immunization Cache.
	 * 
	 * @param cacheManager The Application Cache Manger to access the cache information
	 * @param model Map containing the HTTP information to display to the client
	 */
	private void loadImmunizationCacheInfo(ApplicationCacheManager cacheManager, Map<String, Object> model) {
		Long immunizationCacheHeapSize = cacheManager.getCacheHeapSize(
			ChicaConstants.CACHE_IMMUNIZATION, 
			ChicaConstants.CACHE_IMMUNIZATION_KEY_CLASS, 
			ChicaConstants.CACHE_IMMUNIZATION_VALUE_CLASS);
		model.put(PARAM_IMMUNIZATION_CACHE_HEAP_SIZE, immunizationCacheHeapSize);
		
		String immunizationCacheHeapSizeUnit = cacheManager.getCacheHeapSizeUnit(
			ChicaConstants.CACHE_IMMUNIZATION, 
			ChicaConstants.CACHE_IMMUNIZATION_KEY_CLASS, 
			ChicaConstants.CACHE_IMMUNIZATION_VALUE_CLASS);
		model.put(PARAM_IMMUNIZATION_CACHE_HEAP_SIZE_UNIT, immunizationCacheHeapSizeUnit);
		
		Long immunizationCacheDiskSize = cacheManager.getCacheDiskSize(
			ChicaConstants.CACHE_IMMUNIZATION, 
			ChicaConstants.CACHE_IMMUNIZATION_KEY_CLASS, 
			ChicaConstants.CACHE_IMMUNIZATION_VALUE_CLASS);
		model.put(PARAM_IMMUNIZATION_CACHE_DISK_SIZE, immunizationCacheDiskSize);
		
		String immunizationCacheDiskSizeUnit = cacheManager.getCacheDiskSizeUnit(
			ChicaConstants.CACHE_IMMUNIZATION, 
			ChicaConstants.CACHE_IMMUNIZATION_KEY_CLASS, 
			ChicaConstants.CACHE_IMMUNIZATION_VALUE_CLASS);
		model.put(PARAM_IMMUNIZATION_CACHE_DISK_SIZE_UNIT, immunizationCacheDiskSizeUnit);
		
		Long immunizationCacheExpiry = cacheManager.getCacheExpiry(
			ChicaConstants.CACHE_IMMUNIZATION, 
			ChicaConstants.CACHE_IMMUNIZATION_KEY_CLASS, 
			ChicaConstants.CACHE_IMMUNIZATION_VALUE_CLASS);
		model.put(PARAM_IMMUNIZATION_CACHE_EXPIRY, immunizationCacheExpiry);
		
		String immunizationCacheExpiryUnit = cacheManager.getCacheExpiryUnit(
			ChicaConstants.CACHE_IMMUNIZATION, 
			ChicaConstants.CACHE_IMMUNIZATION_KEY_CLASS, 
			ChicaConstants.CACHE_IMMUNIZATION_VALUE_CLASS);
		model.put(PARAM_IMMUNIZATION_CACHE_EXPIRY_UNIT, immunizationCacheExpiryUnit);
		
		// load the cache statistics
		List<CacheStatistic> stats = cacheManager.getCacheStatistics(
			ChicaConstants.CACHE_IMMUNIZATION, 
			ChicaConstants.CACHE_IMMUNIZATION_KEY_CLASS, 
			ChicaConstants.CACHE_IMMUNIZATION_VALUE_CLASS);
		model.put(PARAM_IMMUNIZATION_CACHE_STATISTICS, stats);
	}
	
	/**
	 * Loads all the specific information about the Immunization Cache.
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
	 * Updates a cache's heap size.  It will only update the heap size if the provided value is different than the current value.
	 * 
	 * @param cacheName The name of the cache
	 * @param keyClass The key class of the cache
	 * @param valueClass The value class of the cache
	 * @param newCacheHeapSizeStr The new heap size value
	 * @param model Map used for error handling
	 */
	private void updateCacheHeapSize(String cacheName, Class<?> keyClass, Class<?> valueClass, String newCacheHeapSizeStr, Map<String, Object> model) {
		Long newCacheHeapSize = null;
		try {
			newCacheHeapSize = Long.parseLong(newCacheHeapSizeStr);
		} catch (NumberFormatException e) {
			model.put(PARAM_ERROR_MESSAGE, "The " + cacheName + " cache heap size specified is not a valid value.");
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
				model.put(PARAM_ERROR_MESSAGE, "An error occurred saving the " + cacheName + " cache heap size: " + e.getMessage());
			}
		}
	}
}
