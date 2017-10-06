package org.openmrs.module.chica.study.dp3.reading;

import java.util.List;

/**
 * @author davely
 */
public interface Readings 
{
	/**
	 * Get a list of GenericReading objects
	 * The Glooko API returns a different type of object for each endpoint
	 * This method should convert the list of objects that is returned into a list of GenericReading objects
	 * @return
	 */
	public List<GenericReading> getGenericReadingList();
}
