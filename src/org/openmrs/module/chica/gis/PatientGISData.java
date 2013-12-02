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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


/**
 * Contains the map file and data file for the GIS information.
 * 
 * @author Steve McKee
 */
public class PatientGISData {

	private Log log = LogFactory.getLog(this.getClass());
	private String mapLocation;
	private String dataLocation;
	private GeocodeLocations locations = null;
	private boolean clinicAddress = false;
	
	/**
	 * Constructor method
	 * 
	 * @param mapLocation Filename where the GIS map data is located.
	 * @param dataLocation Filename where the GIS JSON data file is located.
	 * @param clincAddress Boolean whether or not the resource data is based on the clinic address.
	 */
	public PatientGISData(String mapLocation, String dataLocation, boolean clinicAddress) {
		this.mapLocation = mapLocation;
		this.dataLocation = dataLocation;
		this.clinicAddress = clinicAddress;
	}

	/**
     * @return the mapLocation
     */
    public String getMapLocation() {
    	return mapLocation;
    }
	
    /**
     * @return the dataLocation
     */
    public GeocodeLocations getGeocodeLocations() {
    	if (dataLocation == null) {
    		return null;
    	} else if (locations != null) {
    		return locations;
    	}
    	
        try {
        	ObjectMapper mapper = new ObjectMapper();
    		mapper.getDeserializationConfig().set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    		File dataFile = new File(dataLocation);
    		locations = mapper.readValue(dataFile, GeocodeLocations.class);
    		orderPlacemarks();
        }
        catch (JsonMappingException e) {
	        log.error("Error serializing GIS JSON to file due to mapping exception", e);
        }
        catch (JsonGenerationException e) {
	        log.error("Error serializing GIS JSON generation exception", e);
        }
        catch (IOException e) {
	        log.error("Error serializing GIS JSON i/o exception", e);
        }
        
        return locations;
    }
    
    /**
     * @return the clinicAddress
     */
    public boolean isClinicAddress() {
    	return clinicAddress;
    }
    
    /**
     * Orders the Placemarks based on rank.
     */
    private void orderPlacemarks() {
    	if (locations == null) {
    		return;
    	}
    	
    	Collection<Placemark> placemarks = locations.Placemark;
    	ArrayList<Placemark> newList = new ArrayList<Placemark>();
    	for (Placemark placemark : placemarks) {
    		int rank = placemark.rank;
    		// Rank is 1 based, so we need to make it 0 based for list purposes.
    		int index = rank - 1;
    		newList.add(index, placemark);
    	}
    	
    	locations.Placemark = newList;
    }
}
