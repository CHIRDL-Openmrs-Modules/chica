package org.openmrs.module.chica.study.dp3.reading;

import org.openmrs.module.chica.study.dp3.reading.impl.BGMeterReadingsImpl;
import org.openmrs.module.chica.study.dp3.reading.impl.CGMReadingsImpl;
import org.openmrs.module.chica.study.dp3.reading.impl.PumpReadingsImpl;

/**
 * @author davely
 * Factory used to create an instance of the class that implements the Readings interface
 */
public class ReadingsFactory 
{
	// TODO CHICA-1029 These are duplicated in QueryGlooko
	// Data types
	private static final String READINGS_DATATYPE = "readings";
	private static final String PUMP_READINGS_DATATYPE = "pumps_readings";
	private static final String CGM_READINGS_DATATYPE = "cgm_readings";
		
	/**
	 * Return the appropriate implementation based on the dataType
	 * @param dataType
	 * @return
	 */
	public static Readings getReadingsImpl(String dataType)
	{
		switch(dataType)
		{
			case READINGS_DATATYPE:
				return new BGMeterReadingsImpl();
			case PUMP_READINGS_DATATYPE:
				return new PumpReadingsImpl();
			case CGM_READINGS_DATATYPE:
				return new CGMReadingsImpl();
			default:
				return null;
		}	
	}
	
	/**
	 * TODO CHICA-1029 I don't think this method will work - get rid of it if needed
	 * Return the appropriate class based on the dataType
	 * @param dataType
	 * @return
	 */
	public static Class<?> getReadingClass(String dataType)
	{
		switch(dataType)
		{
			case READINGS_DATATYPE:
				return BGMeterReadingsImpl.class;
			case PUMP_READINGS_DATATYPE:
				return PumpReadingsImpl.class;
			case CGM_READINGS_DATATYPE:
				return CGMReadingsImpl.class;
			default:
				return null;
		}	
	}
}
