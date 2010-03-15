/**
 * 
 */
package org.openmrs.module.chica.datasource;

import java.util.Set;

import org.openmrs.Obs;
import org.openmrs.logic.datasource.ObsDataSource;

/**
 * @author Tammy Dugan
 * 
 */
public class ObsChicaDatasource extends ObsDataSource
{
	public void parseHL7ToObs(String hl7Message,Integer patientId,String mrn)
	{
		((LogicChicaObsDAO) this.getLogicObsDAO()).parseHL7ToObs(hl7Message,
				patientId,mrn);
	}

	public void deleteRegenObsByPatientId(Integer patientId)
	{
		((LogicChicaObsDAO) this.getLogicObsDAO())
				.deleteRegenObsByPatientId(patientId);
	}

	public Set<Obs> getRegenObsByConceptName(Integer patientId,
			String conceptName)
	{
		return ((LogicChicaObsDAO) this.getLogicObsDAO())
				.getRegenObsByConceptName(patientId, conceptName);
	}
	
	public void clearRegenObs() {
	    ((LogicChicaObsDAO) this.getLogicObsDAO()).clearRegenObs();
	}

}
