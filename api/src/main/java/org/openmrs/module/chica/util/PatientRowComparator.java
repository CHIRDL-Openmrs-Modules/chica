/**
 * 
 */
package org.openmrs.module.chica.util;

import java.util.Comparator;

import org.openmrs.Encounter;
import org.openmrs.Obs;

/**
 * @author tmdugan
 *
 */
public class PatientRowComparator implements Comparator
{
	//sort row in descending order by encounter datetime
	public int compare(Object obj1, Object obj2)
	{
		PatientRow row1 = (PatientRow) obj1;
		PatientRow row2 = (PatientRow) obj2;
		
		Encounter encRow1 = row1.getEncounter();
		Encounter encRow2 = row2.getEncounter();
		
		return encRow2.getEncounterDatetime().
			compareTo(encRow1.getEncounterDatetime());
	}

}
