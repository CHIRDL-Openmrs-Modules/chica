package org.openmrs.module.chica.study.dp3.reading.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmrs.module.chica.study.dp3.GlookoConstants;
import org.openmrs.module.chica.study.dp3.reading.GenericReading;
import org.openmrs.module.chica.study.dp3.reading.Readings;

/**
 * @author davely
 * CHICA-1029 Class used to represent the "readings" object that is returned by Glooko
 * This is for blood glucose meters
 */
public class BGMeterReadingsImpl implements Readings
{
	private List<Object> readings;
	private List<GenericReading> genericReadingsList;
	
	/**
	 * Default constructor
	 */
	public BGMeterReadingsImpl()
	{
		
	}
	
	/**
	 * Only here to make the JSON parser happy
	 * @return - list of objects
	 */
	public List<Object> getReadings()
	{
		return readings;
	}
	
	@Override
	public List<GenericReading> getGenericReadingList() throws Exception{
		if(genericReadingsList == null)
		{
			genericReadingsList = new ArrayList<GenericReading>();
			
			for(Object obj : readings)
			{
				@SuppressWarnings({ "rawtypes", "unchecked" })
				HashMap<String, Object> map = (HashMap)obj;
				String timestamp = map.get(GlookoConstants.PARAMETER_TIMESTAMP) == null ? null : (String)map.get(GlookoConstants.PARAMETER_TIMESTAMP);
				Integer timeOffset = map.get(GlookoConstants.PARAMETER_TIME_OFFSET) == null ? null : (Integer) map.get(GlookoConstants.PARAMETER_TIME_OFFSET);
				String syncTimestamp = map.get(GlookoConstants.PARAMETER_SYNC_TIMESTAMP) == null ? null : (String) map.get(GlookoConstants.PARAMETER_SYNC_TIMESTAMP);
				String guid = map.get(GlookoConstants.PARAMETER_GUID) == null ? null : (String) map.get(GlookoConstants.PARAMETER_GUID);
				String updatedAt = map.get(GlookoConstants.PARAMETER_UPDATED_AT) == null ? null : (String) map.get(GlookoConstants.PARAMETER_UPDATED_AT);
				Integer value = map.get(GlookoConstants.PARAMETER_VALUE) == null ? null : (Integer) map.get(GlookoConstants.PARAMETER_VALUE);
				String units = map.get(GlookoConstants.PARAMETER_UNITS) == null ? null : (String) map.get(GlookoConstants.PARAMETER_UNITS);
				String mealTagSource = map.get(GlookoConstants.PARAMETER_MEAL_TAG_SOURCE) == null ? null : (String) map.get(GlookoConstants.PARAMETER_MEAL_TAG_SOURCE);
				String mealTag = map.get(GlookoConstants.PARAMETER_MEAL_TAG) == null ? null : (String) map.get(GlookoConstants.PARAMETER_MEAL_TAG);

				genericReadingsList.add(new GenericReading(timestamp, timeOffset, syncTimestamp, guid, updatedAt, value, units, mealTagSource, mealTag));
			}
		}
		
		return genericReadingsList;
	}
}
