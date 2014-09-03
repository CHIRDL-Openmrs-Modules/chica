/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.gis;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;


/**
 * Queries the Polis center for maps and map metadata for tutoring, exercise, and dental programs.  The maps will be stored 
 * on the file system as a JPG file, and the map metadata will be stored in a JSON file on the file system.
 * 
 * @author Steve McKee
 */
public class QueryGIS implements ChirdlRunnable{
	
	private Log log = LogFactory.getLog(this.getClass());
	private Integer encounterId;
	private Integer patientId;
	private Integer locationId;
	
	/**
	 * Constructor method
	 * 
	 * @param encounter The patient's Encounter
	 */
	public QueryGIS(Encounter encounter) {
		this.encounterId = encounter.getEncounterId();
		this.patientId = encounter.getPatientId();
		this.locationId = encounter.getLocation().getLocationId();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
    public void run() {
		Context.openSession();
		try {
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService.getGlobalProperty("scheduler.username"), 
				adminService.getGlobalProperty("scheduler.password"));
			
			// Check to see if it's an intervention site
			ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
			LocationAttributeValue lav = service.getLocationAttributeValue(locationId, "isGISInterventionLocation");
			if (lav == null || !"true".equalsIgnoreCase(lav.getValue())) {
				return;
			}
			
			Integer minAge = 10;
			String minAgeStr = adminService.getGlobalProperty("chica.gisMinAge");
			if (minAgeStr == null || minAgeStr.trim().length() == 0) {
				log.error("Please specify a value for global property chica.gisMinAge.  A value of 10 will be used by " +
					"default");
				minAgeStr = "2";
			}
			
			try {
				minAge = Integer.parseInt(minAgeStr);
			} catch (NumberFormatException e) {
				log.error("Please specify a valid value for global property chica.gisMinAge.  A value of 2 will be " +
						"used by default");
			}
			
			// Get the child's age range and pass as a filter.
			PatientService patientService = Context.getPatientService();
			Patient patient = patientService.getPatient(patientId);
			DssService dssService = Context.getService(DssService.class);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("param1", "years");
			Rule ageRule = new Rule();
			ageRule.setTokenName("ChicaAgeRule");
			ageRule.setParameters(parameters);
			Result result = dssService.runRule(patient, ageRule);
			String spanishFilter = "";
			String insuranceFilter = "";
			String ageFilter = "";
			if ((result != null) && !(result == Result.emptyResult())) {
				Double age = result.toNumber();
				if (age < minAge) {
					// No use to query since the child is below the study age.
					return;
				} else if ((age >= 2) && (age < 6)) {
					ageFilter = "," + GISConstants.PRESCHOOL;
				} else if ((age >= 6) && (age < 12)) {
					ageFilter = "," + GISConstants.ELEMENTARY;
				} else if ((age >= 12) && (age < 19)) {
					ageFilter = "," + GISConstants.ADOLESCENT;
				} else if (age >= 19) {
					ageFilter = "," + GISConstants.ADULT_ONLY;
				}
			}
			
			String mapUrl = adminService.getGlobalProperty("chica.gisMapUrl");
			if (mapUrl == null || mapUrl.trim().length() == 0) {
				log.error("Cannot query the GIS service.  Please specify a value for global property chica.gisMapUrl");
				return;
			}
			
			String dataUrl = adminService.getGlobalProperty("chica.gisDataUrl");
			if (dataUrl == null || dataUrl.trim().length() == 0) {
				log.error("Cannot query the GIS service.  Please specify a value for global property chica.gisDataUrl");
				return;
			}
			
			String toppingFilter = adminService.getGlobalProperty("chica.gisToppingFilter");
			if (toppingFilter == null || toppingFilter.trim().length() == 0) {
				log.error("Cannot query the GIS service.  Please specify a value for global property " +
						"chica.gisToppingFilter");
				return;
			}
			
			Integer timeout = 10;
			String timeoutStr = adminService.getGlobalProperty("chica.gisTimeout");
			if (timeoutStr == null || timeoutStr.trim().length() == 0) {
				log.error("Please specify a value for global property chica.gisTimeout.  A value of 10 will be used by " +
					"default");
				timeoutStr = "10";
			}
			
			try {
				timeout = Integer.parseInt(timeoutStr);
			} catch (NumberFormatException e) {
				log.error("Please specify a valid value for global property chica.gisTimeout.  A value of 10 will be " +
						"used by default");
			}
			
			// Convert to ms
			timeout = timeout * 1000;
			
			Integer connectionTimeout = 3;
			String connectionTimeoutStr = adminService.getGlobalProperty("chica.gisConnectionTimeout");
			if (connectionTimeoutStr == null || connectionTimeoutStr.trim().length() == 0) {
				log.error("Please specify a value for global property chica.gisConnectionTimeout.  A value of 3 will be " +
						"used by default");
				connectionTimeoutStr = "3";
			}
			
			try {
				connectionTimeout = Integer.parseInt(connectionTimeoutStr);
			} catch (NumberFormatException e) {
				log.error("Please specify a valid value for global property chica.gisConnectionTimeout.  A value of 3 " +
						"will be used by default");
			}
			
			// Convert to ms
			connectionTimeout = connectionTimeout * 1000;
			
			ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
			LocationAttributeValue attrVal = backportsService.getLocationAttributeValue(locationId, "GISDirectory");
			if (attrVal == null || attrVal.getValue().trim().length() == 0) {
				log.error("Please specify a directory for the GISDirectory location attribute value for location: " + 
					locationId);
				return;
			}
			
			String gisDirStr = attrVal.getValue();
			
			// Find out if patient is Spanish speaking and/or on Medicaid
			LogicCriteria conceptCriteria = new LogicCriteriaImpl("preferred_language");
			LogicContext context = new LogicContextImpl(patientId);
			Result languageResult = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria.last());
			if (languageResult != null && languageResult.toString().equalsIgnoreCase("Spanish")) {
				spanishFilter += "," + GISConstants.SPANISH_SPEAKING;
			}
			
			conceptCriteria = new LogicCriteriaImpl("Insurance");
			Result insuranceResult = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria.last());
			if (insuranceResult != null && insuranceResult.toString().equalsIgnoreCase(GISConstants.MEDICAID)) {
				insuranceFilter += "," + GISConstants.MEDICAID;
			} else if (insuranceResult != null && insuranceResult.toString().equalsIgnoreCase(GISConstants.ADVANTAGE)) {
				insuranceFilter += "," + GISConstants.ADVANTAGE;
			}
			
			String radius = adminService.getGlobalProperty("chica.gisTutorRadius");
			if (radius == null || radius.trim().length() == 0) {
				radius = "3";
				log.error("Please specify a value for global property chica.gisTutorRadius.  A radius of 3 will " +
						"be used by default.");
			}
			
			// Get the minimum search result number
			Integer minResults = 3;
			String minResultsStr = adminService.getGlobalProperty("chica.gisMinimumSearchResults");
			if (minResultsStr == null || minResultsStr.trim().length() == 0) {
				log.error("Please specify a value for global property chica.gisMinimumSearchResults.  A value of 3 will " +
						"be used by default");
				minResultsStr = "3";
			}
			
			try {
				minResults = Integer.parseInt(minResultsStr);
			} catch (NumberFormatException e) {
				log.error("Please specify a valid value for global property chica.gisMinimumSearchResults.  A value of 3 " +
						"will be used by default");
			}
			
			PersonAddress address = patient.getPersonAddress();
			String street = null;
			String city = null;
			String state = null;
			String zip = null;
			if (address != null) {
				street = address.getAddress1();
				city = address.getCityVillage();
				state = address.getStateProvince();
				zip = address.getPostalCode();
			}
				
			long startTime = System.currentTimeMillis();
			boolean found = fetchGISInformation(patient, gisDirStr, mapUrl, dataUrl, radius, toppingFilter, 
				GISConstants.TUTORING, spanishFilter, timeout, connectionTimeout, street, city, state, zip, false, 
				minResults);
			if (!found) {
				// Look for resources not Spanish Speaking.
				found = fetchGISInformation(patient, gisDirStr, mapUrl, dataUrl, radius, toppingFilter, 
					GISConstants.TUTORING, "", timeout, connectionTimeout, street, city, state, zip, false, minResults);
				if (!found) {
					// Try to find map data based on the clinic location address.
					found = fetchClinicGISInformation(patient, gisDirStr, mapUrl, dataUrl, radius, toppingFilter, 
						GISConstants.TUTORING, spanishFilter, timeout, connectionTimeout, minResults);
					if (!found) {
						// Try to find map data based on the clinic location address not Spanish Speaking.
						fetchClinicGISInformation(patient, gisDirStr, mapUrl, dataUrl, radius, toppingFilter, 
							GISConstants.TUTORING, "", timeout, connectionTimeout, minResults);
					}
				}
			}
			
			radius = adminService.getGlobalProperty("chica.gisExerciseRadius");
			if (radius == null || radius.trim().length() == 0) {
				radius = "3";
				log.error("Please specify a value for global property chica.gisExerciseRadius.  A radius of 3 will " +
						"be used by default.");
			}
				
			found = fetchGISInformation(patient, gisDirStr, mapUrl, dataUrl, radius, toppingFilter, GISConstants.EXERCISE, 
				ageFilter, timeout, connectionTimeout, street, city, state, zip, false, minResults);
			if (!found) {
				// Try to find map data based on the clinic location address.
				fetchClinicGISInformation(patient, gisDirStr, mapUrl, dataUrl, radius, toppingFilter, 
					GISConstants.EXERCISE, ageFilter, timeout, connectionTimeout, minResults);
			}
			
			radius = adminService.getGlobalProperty("chica.gisDentalRadius");
			if (radius == null || radius.trim().length() == 0) {
				radius = "3";
				log.error("Please specify a value for global property chica.gisDentalRadius.  A radius of 3 will " +
						"be used by default.");
			}
				
			found = fetchGISInformation(patient, gisDirStr, mapUrl, dataUrl, radius, toppingFilter, GISConstants.DENTAL, 
				spanishFilter + insuranceFilter, timeout, connectionTimeout, street, city, state, zip, false, minResults);
			if (!found) {
				// Try to find map data based on the clinic location address.
				fetchClinicGISInformation(patient, gisDirStr, mapUrl, dataUrl, radius, toppingFilter, 
					GISConstants.DENTAL, spanishFilter + insuranceFilter, timeout, connectionTimeout, minResults);
			}
			
			System.out.println("Total GIS query time: " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			log.error("Error retrieving GIS data", e);
		} finally {
			Context.closeSession();
		}
    }

    /**
     * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
     */
    public String getName() {
		return "Query GIS (Encounter: " + encounterId + " Patient: " + patientId + ")";
    }

    /**
     * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
     */
    public int getPriority() {
		return ChirdlRunnable.PRIORITY_FIVE;
    }
    
    /**
     * Retrieves and stores all the GIS information for the patient.
     * 
     * @param patient The patient to retrieve the GIS data for.
     * @param gisDirStr The directory where the data will be stored.
     * @param mapUrl The base URL for the GIS map service.
     * @param dataUrl The base URL for the GIS map data service.
     * @param radius The radius (in miles) for the GIS search.
     * @param toppingFilter The maximum number of results to be returned by the GIS service.
     * @param attributeFilter The type of resource being requested.
     * @param additionalAttributeFilters Additional filters for the query.  Must begin with a "," if a value exists and be 
     * delimited by the same character for each filter.
     * @param timeout The read timeout.
     * @param connectionTimeout The connection timeout.
     * @param street The street part of the address.
     * @param city The city part of the address.
     * @param state The state part of the address.
     * @param zip The zip part of the address.
     * @param clinicAddress boolean to tell whether or not the clinic address is being used for the query.
     * @param minimumSearchResults The minimum number of results allowed.
     * @return Boolean telling if resources were found.
     */
	private boolean fetchGISInformation(Patient patient, String gisDirStr, String mapUrl, String dataUrl, String radius,
	                                 String toppingFilter, String attributeFilter, String additionalAttributeFilters, 
	                                 int timeout, int connectionTimeout, String street, String city, String state, 
	                                 String zip, boolean clinicAddress, int minimumSearchResults) {
		if (street == null || city == null || state == null || zip == null) {
			// Can't use the patient's address if it's not complete.
			log.info("Insufficient address information for GIS lookup for patient " + patient.getPatientId());
			return false;
		}
		
		String allFilters = attributeFilter + additionalAttributeFilters;
		String filters = "a=" + encode(street) + "&c=" + encode(city) + "&s=" + encode(state) + "&z=" + encode(zip) + 
			"&r=" + encode(radius) + "&af=" +  encode(allFilters) + "&tf=" + encode(toppingFilter);
		String imageDataUrl = dataUrl + filters;
		
		GeocodeLocations locations = null;
		try {
			locations = parseJson(new URL(imageDataUrl), timeout, connectionTimeout);
		}
		catch (MalformedURLException e) {
			log.error("Malformed URL retrieving GIS map data. No GIS data will be stored: " + imageDataUrl, e);
			return false;
		}
		catch (SocketTimeoutException e) {
			log.error("Timeout retrieving GIS map data: " + imageDataUrl, e);
			// Do not return for a timeout exception.  We'll try again.
		}
		catch (IOException e) {
			log.error("Exception encountered downloading GIS map data:: " + imageDataUrl, e);
			// Do not return for a timeout exception.  We'll try again.
		}
		
		int radiusVal = Integer.parseInt(radius);
		radiusVal++;
		String maxRadiusStr = Context.getAdministrationService().getGlobalProperty("chica.gisMaxRadius");
		int maxRadius = 10;
		try {
			maxRadius = Integer.parseInt(maxRadiusStr);
		}
		catch (NumberFormatException e) {
			maxRadius = 10;
			log.error("Please specify a value for global property chica.gisMaxRadius.  A value of 10 is being used " +
					"by default");
		}
		
		// If we didn't get any results or the results are lower than the minimum specified, try querying again, increase 
		// the radius by a mile until we find results or reach the maximum radius.
		if (locations == null || locations.Placemark == null || locations.Placemark.size() < minimumSearchResults) {
			if (radiusVal <= maxRadius) {
				// Recursively call until we find some data or we reach the max radius.
				return fetchGISInformation(patient, gisDirStr, mapUrl, dataUrl, String.valueOf(radiusVal), 
					toppingFilter, attributeFilter, additionalAttributeFilters, timeout, connectionTimeout, street, city, 
					state, zip, clinicAddress, minimumSearchResults);
			} else {
				return false;
			}
		}
		
		filters = "a=" + encode(street) + "&c=" + encode(city) + "&s=" + encode(state) + "&z=" + encode(zip) + "&r=" + 
			encode(radius) + "&af=" + encode(allFilters) + "&tf=" + encode(toppingFilter);
		String imageMapUrl = mapUrl + filters;
		
		String resource = attributeFilter;
		resource = resource.replaceAll(" ", "_");
		resource = resource.replaceAll("/", "_");
		String mrn = patient.getPatientIdentifier().getIdentifier();
		String filename = gisDirStr + File.separator + Util.archiveStamp() + "_" + resource + "_" + mrn + ".jpg";
		File file = new File(filename);
		try {
			downloadImage(new URL(imageMapUrl), file, timeout, connectionTimeout);
		}
		catch (MalformedURLException e) {
			log.error("Malformed URL retrieving GIS map image.  No GIS data will be stored: " + imageMapUrl, e);
			return false;
		}
		catch (SocketTimeoutException e) {
			log.error("Timeout retrieving GIS map image: " + imageMapUrl, e);
			if (radiusVal <= maxRadius) {
				// Recursively call until we find some data or we reach the max radius.
				return fetchGISInformation(patient, gisDirStr, mapUrl, dataUrl, String.valueOf(radiusVal), 
					toppingFilter, attributeFilter, additionalAttributeFilters, timeout, connectionTimeout, street, city, 
					state, zip, clinicAddress, minimumSearchResults);
			} else {
				return false;
			}
		}
		catch (IOException e) {
			log.error("Exception encountered downloading GIS map image: " + imageMapUrl, e);
			if (radiusVal <= maxRadius) {
				// Recursively call until we find some data or we reach the max radius.
				return fetchGISInformation(patient, gisDirStr, mapUrl, dataUrl, String.valueOf(radiusVal), 
					toppingFilter, attributeFilter, additionalAttributeFilters, timeout, connectionTimeout, street, city, 
					state, zip, clinicAddress, minimumSearchResults);
			} else {
				return false;
			}
		}
		
		String dataLocation = storeGISData(gisDirStr, locations, mrn, attributeFilter);
		if (dataLocation != null) {
			PatientGISData data = new PatientGISData(filename, dataLocation, clinicAddress);
			PatientGISDataStorage.storePatientGISData(patient.getPatientId(), attributeFilter, data);
			if (clinicAddress) {
				// We want a record if the clinic address was used for the query.
				ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
				Error error = new Error("Warning", "GIS Clinic Address Used", "Clinic address used for GIS (" + 
					attributeFilter + ") for patient " + patient.getPatientId(), null, new Date(), null);
				service.saveError(error);
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
     * Retrieves and stores all the GIS information for the clinic location.
     * 
     * @param patient The patient to retrieve the GIS data for.
     * @param gisDirStr The directory where the data will be stored.
     * @param mapUrl The base URL for the GIS map service.
     * @param dataUrl The base URL for the GIS map data service.
     * @param radius The radius (in miles) for the GIS search.
     * @param toppingFilter The maximum number of results to be returned by the GIS service.
     * @param attributeFilter The type of resource being requested.
     * @param additionalAttributeFilters Additional filters for the query.  Must begin with a "," if a value exists and be 
     * delimited by the same character for each filter.
     * @param timeout The read timeout.
     * @param connectionTimeout The connection timeout.
     * @param minimumSearchResults The minimum number of search results allowed.
     * @return Boolean true if the search was successful, false otherwise.
     */
	private boolean fetchClinicGISInformation(Patient patient, String gisDirStr, String mapUrl, String dataUrl, String radius,
	  	                                 String toppingFilter, String attributeFilter, String additionalAttributeFilters, 
	  	                                 int timeout, int connectionTimeout, int minimumSearchResults) {
		Location location = Context.getLocationService().getLocation(locationId);
		if (location == null) {
			return false;
		}
		
		String street = location.getAddress1();
		String city = location.getCityVillage();
		String state = location.getStateProvince();
		String zip = location.getPostalCode();
		return fetchGISInformation(patient, gisDirStr, mapUrl, dataUrl, radius, toppingFilter, attributeFilter, 
			additionalAttributeFilters, timeout, connectionTimeout, street, city, state, zip, true, minimumSearchResults);
	}
    
    /**
     * Stores the GIS data to a JSON file.
     * 
     * @param gisDirStr The directory where the file is to be stored.
     * @param locations The GeocodeLocations to store in the JSON file.
     * @param mrn The MRN of the patient for the data is being stored.
     * @param attributeFilter The category requested.
     * @return The location of the JSON file or null if an error occurs.
     */
    private String storeGISData(String gisDirStr, GeocodeLocations locations, String mrn, 
                                String attributeFilter) {
    	String resource = attributeFilter;
    	resource = resource.replaceAll(" ", "_");
    	resource = resource.replaceAll("/", "_");
    	String filename = gisDirStr + File.separator + Util.archiveStamp() + "_" + resource + "_" + mrn + ".json";
    	File file = new File(filename);
    	
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.getDeserializationConfig().set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.writeValue(file, locations);
        }
        catch (JsonMappingException e) {
	        log.error("Error serializing GIS JSON to file due to mapping exception", e);
	        return null;
        }
        catch (JsonGenerationException e) {
	        log.error("Error serializing GIS JSON generation exception", e);
	        return null;
        }
        catch (IOException e) {
	        log.error("Error serializing GIS JSON i/o exception", e);
	        return null;
        }
        
        return filename;
    }

    /**
     * Downloads an image from the URL provided to the File provided.
     * 
     * @param url The URL to the GIS map service.
     * @param targetFile The destination of the downloaded file.
     * @param timeout The connection timeout for reading from GIS server.
     * @param connectionTimeout The connection timeout for the GIS server.
     * @throws IOException, SocketTimeoutException
     */
	private void downloadImage(URL url, File targetFile, int timeout, int connectionTimeout) 
	throws IOException, SocketTimeoutException {
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(connectionTimeout);
		connection.setReadTimeout(timeout);
		connection.connect();
		FileOutputStream fos = null;
		ReadableByteChannel rbc = null;
		
		try {
			rbc = Channels.newChannel(new BufferedInputStream(connection.getInputStream())); 
		    fos = new FileOutputStream(targetFile); 
		    fos.getChannel().transferFrom(rbc, 0, 1 << 24); 	
		} finally {
			if (fos != null) {
				fos.close();
			}
			
			if (rbc != null) {
				rbc.close();
			}
		}
	}
	
	/**
	 * Parses the JSON data provided by the GIS map data service.
	 * 
	 * @param url The URL to the GIS map data service.
	 * @param timeout The timeout for reading from the GIS server.
	 * @param connectionTimeout The timeout for connecting to the GIS server
	 * @return GeocodeLocations object containing the map data.
	 * @throws IOException
	 */
	private GeocodeLocations parseJson(URL url, int timeout, int connectionTimeout) 
	throws IOException, SocketTimeoutException {
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(connectionTimeout);
		connection.setReadTimeout(timeout);
		connection.connect();
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		GeocodeLocations locations = null;
		InputStream in = null;
		try {
			in = connection.getInputStream();
			locations = mapper.readValue(new BufferedInputStream(in), GeocodeLocations.class);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		return locations;
	}
	
	/**
	 * Encodes a value using the URL encoder with UTF-8 encoding.
	 *  
	 * @param value The value to encode.
	 * @return The encoded value or the original value if an encoding exception occurs.
	 */
	private String encode(String value) {
		try {
	        return URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
	        log.error("Error encoding value: " + value, e);
	        return value;
        }
	}
}
