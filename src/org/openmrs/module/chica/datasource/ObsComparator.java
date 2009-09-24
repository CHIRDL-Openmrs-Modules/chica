/**
 * 
 */
package org.openmrs.module.chica.datasource;

import java.util.Comparator;

import org.openmrs.Obs;

/**
 * @author tmdugan
 *
 */
public class ObsComparator implements Comparator
{
	//sort obs in ascending order by obs datetime
	public int compare(Object obj1, Object obj2)
	{
		Obs obs1 = (Obs) obj1;
		Obs obs2 = (Obs) obj2;
		
		return obs1.getObsDatetime().compareTo(obs2.getObsDatetime());
	}

}
