package org.openmrs.module.chica.study.dp3.reading.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmrs.module.chica.study.dp3.GlookoConstants;
import org.openmrs.module.chica.study.dp3.reading.GenericReading;
import org.openmrs.module.chica.study.dp3.reading.Readings;

/**
 * @author davely
 * CHICA-1029 Class used to represent the "pumpReadings" object that is returned by Glooko
 * This is for pumps
 */
public class PumpReadingsImpl implements Readings
{
	private List<Object> pumpsReadings;
	private List<GenericReading> genericReadingsList;
	
	/**
	 * Default constructor
	 */
	public PumpReadingsImpl()
	{
		// This constructor is intentionally left empty.
	}
	
	/**
	 * Only here to make the JSON parser happy
	 * @return - list of objects
	 */
	public List<Object> getPumpsReadings()
	{
		return this.pumpsReadings;
	}
	
	@Override
	public List<GenericReading> getGenericReadingList() throws Exception{
		if(this.genericReadingsList == null)
		{
			this.genericReadingsList = new ArrayList<>();
			
			for(Object obj : this.pumpsReadings)
			{

				@SuppressWarnings({ "rawtypes", "unchecked" })
				HashMap<String, Object> map = (HashMap)obj;
				String timestamp = map.get(GlookoConstants.PARAMETER_TIMESTAMP) == null ? null : (String)map.get(GlookoConstants.PARAMETER_TIMESTAMP);
				String utcOffset = map.get(GlookoConstants.PARAMETER_UTCOFFSET) == null ? null : (String) map.get(GlookoConstants.PARAMETER_UTCOFFSET);
				String syncTimestamp = map.get(GlookoConstants.PARAMETER_SYNC_TIMESTAMP) == null ? null : (String) map.get(GlookoConstants.PARAMETER_SYNC_TIMESTAMP);
				String guid = map.get(GlookoConstants.PARAMETER_GUID) == null ? null : (String) map.get(GlookoConstants.PARAMETER_GUID);
				String updatedAt = map.get(GlookoConstants.PARAMETER_UPDATED_AT) == null ? null : (String) map.get(GlookoConstants.PARAMETER_UPDATED_AT);
				Integer value = map.get(GlookoConstants.PARAMETER_GLUCOSE_VALUE) == null ? null : (Integer) map.get(GlookoConstants.PARAMETER_GLUCOSE_VALUE);
				String units = map.get(GlookoConstants.PARAMETER_UNITS) == null ? null : (String) map.get(GlookoConstants.PARAMETER_UNITS);
				String mealTagSource = map.get(GlookoConstants.PARAMETER_MEAL_TAG_SOURCE) == null ? null : (String) map.get(GlookoConstants.PARAMETER_MEAL_TAG_SOURCE);
				String mealTag = map.get(GlookoConstants.PARAMETER_MEAL_TAG) == null ? null : (String) map.get(GlookoConstants.PARAMETER_MEAL_TAG);

				// It would be nice if all Glooko "readings" objects would return this, but instead
				// we have an inconsistent interface which creates the need for the different classes
				// that implement the Readings interface

				this.genericReadingsList.add(new GenericReading(timestamp, utcOffset, syncTimestamp, guid, updatedAt, value, units, mealTagSource, mealTag));
			}
		}
		
		return this.genericReadingsList;
	}

}
