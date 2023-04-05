package org.openmrs.module.chica.study.dp3.reading.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmrs.module.chica.study.dp3.GlookoConstants;
import org.openmrs.module.chica.study.dp3.reading.GenericReading;
import org.openmrs.module.chica.study.dp3.reading.Readings;

/**
 * @author davely
 * CHICA-1029 Class used to represent the "cgmReadings" object that is returned by Glooko
 * This is for continuous glucose monitors
 */
public class CGMReadingsImpl implements Readings
{
	private List<Object> cgmReadings;
	private List<GenericReading> genericReadingsList;
	
	/**
	 * Default constructor
	 */
	public CGMReadingsImpl()
	{
		// This constructor is intentionally left empty.
	}
	
	/**
	 * Only here to make the JSON parser happy
	 * @return - list of objects
	 */
	public List<Object> getCgmReadings()
	{
		return this.cgmReadings;
	}
	
	@Override
	public List<GenericReading> getGenericReadingList() throws Exception{
		if(this.genericReadingsList == null)
		{
			this.genericReadingsList = new ArrayList<>();
			
			for(Object obj : this.cgmReadings)
			{	
				@SuppressWarnings({ "rawtypes", "unchecked" })
				HashMap<String, Object> map = (HashMap)obj;
				String timestamp = map.get(GlookoConstants.PARAMETER_SYSTEM_TIME) == null ? null : (String)map.get(GlookoConstants.PARAMETER_SYSTEM_TIME);
				String displayTime = map.get(GlookoConstants.PARAMETER_DISPLAY_TIME) == null ? null : (String) map.get(GlookoConstants.PARAMETER_DISPLAY_TIME);
				String syncTimestamp = map.get(GlookoConstants.PARAMETER_SYNC_TIMESTAMP) == null ? null : (String) map.get(GlookoConstants.PARAMETER_SYNC_TIMESTAMP);
				String guid = map.get(GlookoConstants.PARAMETER_GUID) == null ? null : (String) map.get(GlookoConstants.PARAMETER_GUID);
				String updatedAt = map.get(GlookoConstants.PARAMETER_UPDATED_AT) == null ? null : (String) map.get(GlookoConstants.PARAMETER_UPDATED_AT);
				Integer value = map.get(GlookoConstants.PARAMETER_GLUCOSE_VALUE) == null ? null : (Integer) map.get(GlookoConstants.PARAMETER_GLUCOSE_VALUE);
				String units = map.get(GlookoConstants.PARAMETER_UNITS) == null ? null : (String) map.get(GlookoConstants.PARAMETER_UNITS);
				String trendArrow = map.get(GlookoConstants.PARAMETER_TREND_ARROW) == null ? null : (String) map.get(GlookoConstants.PARAMETER_TREND_ARROW);

				this.genericReadingsList.add(new GenericReading(timestamp, displayTime, syncTimestamp, guid, updatedAt, value, units, trendArrow));
			}
		}
		
		return this.genericReadingsList;
	}

}
