package org.openmrs.module.chica.study.dp3.reading;

import org.openmrs.module.chica.study.dp3.GlookoConstants;
import org.openmrs.module.chica.study.dp3.reading.impl.BGMeterReadingsImpl;
import org.openmrs.module.chica.study.dp3.reading.impl.CGMReadingsImpl;
import org.openmrs.module.chica.study.dp3.reading.impl.PumpReadingsImpl;

/**
 * @author davely
 * Factory used to create an instance of the class that implements the Readings interface
 */
public class ReadingsFactory 
{	
	/**
	 * Return the appropriate implementation based on the dataType
	 * @param dataType
	 * @return
	 */
	public static Readings getReadingsImpl(String dataType)
	{
		switch(dataType)
		{
			case GlookoConstants.READINGS_DATATYPE:
				return new BGMeterReadingsImpl();
			case GlookoConstants.PUMP_READINGS_DATATYPE:
				return new PumpReadingsImpl();
			case GlookoConstants.CGM_READINGS_DATATYPE:
				return new CGMReadingsImpl();
			default:
				return null;
		}	
	}
}
