package org.openmrs.module.chica.study.dp3.reading.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.openmrs.module.chica.study.dp3.reading.GenericReading;
import org.openmrs.module.chica.study.dp3.reading.Readings;

public class BGMeterReadingsImpl implements Readings
{
	private List<Object> readings;
	private List<GenericReading> genericReadingsList;
	
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
	public List<GenericReading> getGenericReadingList() {
		if(genericReadingsList == null)
		{
			genericReadingsList = new ArrayList<GenericReading>();
			
			for(Object obj : readings)
			{
				try
				{
					@SuppressWarnings({ "rawtypes", "unchecked" })
					LinkedHashMap<String, Object> map = (LinkedHashMap)obj;
					String timestamp = map.get("timestamp") == null ? null : (String)map.get("timestamp");
					Integer timeOffset = map.get("timeOffset") == null ? null : (Integer) map.get("timeOffset");
					String syncTimestamp = map.get("syncTimestamp") == null ? null : (String) map.get("syncTimestamp");
					String guid = map.get("guid") == null ? null : (String) map.get("guid");
					String updatedAt = map.get("updatedAt") == null ? null : (String) map.get("updatedAt");
					Integer value = map.get("value") == null ? null : (Integer) map.get("value");
					String units = map.get("units") == null ? null : (String) map.get("units");
					String mealTagSource = map.get("mealTagSource") == null ? null : (String) map.get("mealTagSource");
					String mealTag = map.get("mealTag") == null ? null : (String) map.get("mealTag");
					
					genericReadingsList.add(new GenericReading(timestamp, timeOffset, syncTimestamp, guid, updatedAt, value, units, mealTagSource, mealTag));
				}
				catch(ClassCastException e)
				{
					// Log error
					// Do not return a partial list as this could affect logic in the decision support
					return null;
				}
			}
		}
		
		return genericReadingsList;
	}
}
