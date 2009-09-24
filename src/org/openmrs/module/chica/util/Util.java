/**
 * 
 */
package org.openmrs.module.chica.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hibernateBeans.Statistics;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;

/**
 * @author Tammy Dugan
 * 
 */
public class Util
{
	public static final String YEAR_ABBR = "yo";
	public static final String MONTH_ABBR = "mo";
	public static final String WEEK_ABBR = "wk";
	public static final String DAY_ABBR = "do";
	
	private static Log log = LogFactory.getLog( Util.class );
	public static final Random GENERATOR = new Random();
	
	public synchronized static String getDssType(String dssString)
	{
		if(dssString == null)
		{
			return null;
		}
		String dssType = null;
		
		if (dssString.startsWith("PSF"))
		{
			dssType = "PSF";
		}
		if (dssString.startsWith("PWS"))
		{
			dssType = "PWS";
		}
		return dssType;
	}
	
	public synchronized static int getMaxDssElements(Integer formId)
	{
		String propertyValue = null;
		int maxDssElements = 0;
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		String formName = form.getName();
		
		if (formName.equals("PSF"))
		{
			propertyValue = org.openmrs.module.atd.util.Util
					.getFormAttributeValue(formId, "numQuestions");
		}
		if (formName.equals("PWS"))
		{
			propertyValue = org.openmrs.module.atd.util.Util
				.getFormAttributeValue(formId, "numPrompts");
		}
		
		try
		{
			maxDssElements = Integer.parseInt(propertyValue);
		} catch (NumberFormatException e)
		{
		}
		
		return maxDssElements;
	}
	
	public synchronized static void saveObs(Patient patient, Concept currConcept,
			int encounterId, String value, Integer formInstanceId,
			Integer ruleId,Integer formId)
	{
		if (value == null || value.length() == 0)
		{
			return;
		}
		
		String formName = null;
		
		if (formId != null)
		{
			FormService formService = Context.getFormService();
			Form form = formService.getForm(formId);
			formName = form.getName();
		}

		ObsService obsService = Context.getObsService();
		Obs obs = new Obs();
		String datatypeName = currConcept.getDatatype().getName();

		if (datatypeName.equalsIgnoreCase("Numeric"))
		{
			try
			{
				obs.setValueNumeric(Double.parseDouble(value));
			} catch (NumberFormatException e)
			{
				log.error("Could not save value: " + value
						+ " to the database for concept "+currConcept.getName().getName());
			}
		} else if (datatypeName.equalsIgnoreCase("Coded"))
		{
			ConceptService conceptService = Context.getConceptService();
			Concept answer = conceptService.getConceptByName(value);
			obs.setValueCoded(answer);
		} else
		{
			obs.setValueText(value);
		}

		EncounterService encounterService = Context
				.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService
				.getEncounter(encounterId);

		Location location = encounter.getLocation();
		ChicaService chicaService = Context.getService(ChicaService.class);
		Date obsDate = null;
		
		//Since PWS forms get scanned much later, sometimes the next day, 
		//set the observation time as the time the form is printed
		if(formName != null && formName.equalsIgnoreCase("PWS")){
			List<Statistics> stats = chicaService.getStatByFormInstance(formInstanceId, formName);
			if(stats != null&&stats.size()>0){
				obsDate = stats.get(0).getPrintedTimestamp(); 
			}
		}
		if(obsDate == null){
			obsDate = new Date();
		}
		obs.setObsDatetime(obsDate);
		obs.setPerson(patient);
		obs.setConcept(currConcept);
		obs.setLocation(location);
		obs.setEncounter(encounter);
		obsService.saveObs(obs, null);

		if (formInstanceId != null)
		{
			if (ruleId != null)
			{
				List<Statistics> statistics = chicaService.getStatByIdAndRule(
						formInstanceId, ruleId, formName);

				if (statistics != null)
				{
					Statistics stat = statistics.get(0);

					if (stat.getObsvId() == null)
					{
						stat.setObsvId(obs.getObsId());
						chicaService.updateStatistics(stat);
					} else
					{
						stat = new Statistics(stat);
						stat.setObsvId(obs.getObsId());
						chicaService.createStatistics(stat);
					}
				}
			} else
			{
				List<Statistics> statistics = chicaService.getStatByFormInstance(formInstanceId, formName);
				Statistics stat = new Statistics();

				stat.setAgeAtVisit(org.openmrs.module.chica.util.Util
						.adjustAgeUnits(patient.getBirthdate(), null));
				stat.setEncounterId(encounterId);
				stat.setFormInstanceId(formInstanceId);
				stat.setFormName(formName);
				stat.setObsvId(obs.getObsId());
				stat.setPatientId(patient.getPatientId());
				stat.setRuleId(ruleId);
				if(statistics != null&&statistics.size()>0){
					Statistics oldStat = statistics.get(0);
					stat.setPrintedTimestamp(oldStat.getPrintedTimestamp());
					stat.setScannedTimestamp(oldStat.getScannedTimestamp());
				}
				chicaService.createStatistics(stat);
			}
		}
	}
	
	public synchronized static String removeLeadingZeros(String mrn)
	{

		char[] chars = mrn.toCharArray();
		int index = 0;
		for (; index < chars.length; index++)
		{
			if (chars[index] != '0')
			{
				break;
			}
		}
		if (index > -1)
		{
			return mrn.substring(index);
		}
		return mrn;
	}
	
	/**
	 * Calculates age to a precision of days, weeks, months, or years based on a
	 * set of rules
	 * 
	 * @param birthdate patient's birth date
	 * @param cutoff date to calculate age from
	 * @return String age with units 
	 */
	public synchronized static String adjustAgeUnits(Date birthdate, Date cutoff)
	{
		int years = org.openmrs.module.dss.util.Util.getAgeInUnits(birthdate, cutoff, YEAR_ABBR);
		int months = org.openmrs.module.dss.util.Util.getAgeInUnits(birthdate, cutoff, MONTH_ABBR);
		int weeks = org.openmrs.module.dss.util.Util.getAgeInUnits(birthdate, cutoff, WEEK_ABBR);
		int days = org.openmrs.module.dss.util.Util.getAgeInUnits(birthdate, cutoff, DAY_ABBR);

		if (years >= 2)
		{
			return years + " " + YEAR_ABBR;
		}

		if (months >= 2)
		{
			return months + " " + MONTH_ABBR;
		}

		if (days > 30)
		{
			return weeks + " " + WEEK_ABBR;
		}

		return days + " " + DAY_ABBR;
	}
	
	public synchronized static boolean isValidSSN( String ssnFull){
		boolean valid = true;
		String ssn = null;
		
		if(ssnFull != null){
			ssn = ssnFull.replace("-", "");
		}
		
		if (ssn  == null||ssn.length()==0
				|| ssn.equals("000000000")
				|| ssn.equals("111111111")
				|| ssn.equals("222222222")
				|| ssn.equals("333333333")
				|| ssn.equals("444444444")
				|| ssn.equals("555555555")
				|| ssn.equals("666666666")
				|| ssn.equals("777777777")
				|| ssn.equals("888888888")
				|| ssn.equals("999999999")){
			valid = false;
		}
		return valid;
	}
	
	public static Properties getProps(String filename)
	{
		try
		{

			Properties prop = new Properties();
			InputStream propInputStream = new FileInputStream(filename);
			prop.loadFromXML(propInputStream);
			return prop;

		} catch (FileNotFoundException e)
		{

		} catch (InvalidPropertiesFormatException e)
		{

		} catch (IOException e)
		{
			// TODO Auto-generated catch block

		}
		return null;
	}
}
