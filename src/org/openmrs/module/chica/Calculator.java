/**
 * 
 */
package org.openmrs.module.chica;

import java.util.Calendar;
import java.util.Date;

import org.openmrs.api.context.Context;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.dss.util.Util;

/**
 * @author Tammy Dugan
 * 
 */
public class Calculator
{
	private static final String MALE_STRING = "M";
	private static final String FEMALE_STRING = "F";

	private static final int MALE_INT = 1;
	private static final int FEMALE_INT = 2;

	private static final String PERCENTILE_HC = "hc";
	private static final String PERCENTILE_LEN = "length";
	private static final String PERCENTILE_BMI = "bmi";
	private static final String PERCENTILE_WT = "weight";

	/**
	 * 
	 */
	public Calculator()
	{
	}

	/**
	 * @param measurement
	 * @param gender
	 * @param birthdate
	 * @param type
	 * @param measurementUnits
	 * @return
	 */
	public String calculatePercentileAsString(double measurement, String gender,
			Date birthdate, String type, String measurementUnits)
	{
		Double percentile = calculatePercentile(measurement, gender, birthdate,
				type, measurementUnits);

		if (percentile == null)
		{
			return null;
		}

		if (percentile > 99.0)
		{
			return ">99";
		}

		if (percentile < 1.0)
		{
			return "<1";
		}
		return String.valueOf(Util.round(percentile, 0).intValue());
	}

	private Percentile lookupPercentile(String type,double agemos,int sex)
	{
		ChicaService chicaService = Context
			.getService(ChicaService.class);
		Percentile percentileTable = null;

		if (type.equalsIgnoreCase(PERCENTILE_BMI))
		{
			percentileTable = chicaService.getBmiage(agemos, sex);
		}

		if (type.equalsIgnoreCase(PERCENTILE_WT))
		{
			percentileTable = chicaService.getWtageinf(agemos, sex);
		}

		if (type.equalsIgnoreCase(PERCENTILE_LEN))
		{
			percentileTable = chicaService.getLenageinf(agemos, sex);
		}

		if (type.equalsIgnoreCase(PERCENTILE_HC))
		{
			percentileTable = chicaService.getHcageinf(agemos, sex);
		}

		return percentileTable;
	}
	
	/**
	 * Calculates the percentile of the measurement
	 * 
	 * @param measurement measurement for the patient
	 * @param gender gender of the patient
	 * @param birthdate birthdate of the patient used to compute age
	 * @param type type of percentile (weight, hc, height, BMI)
	 * @param measurementUnits units of the measurement indicating whether it
	 *        needs converted before computing the percentile
	 * @return
	 */
	public Double calculatePercentile(double measurement, String gender,
			Date birthdate, String type, String measurementUnits)
	{		
		double ageInMonths = Util.getFractionalAgeInUnits(birthdate, Calendar.getInstance()
				.getTime(),Util.MONTH_ABBR);
		int sex = translateGender(gender);
		double s = 0;
		double l = 0;
		double m = 0;

		//look up the percentile for the age in months
		Percentile percentileTable = lookupPercentile(type,ageInMonths,sex);
		
		if(percentileTable != null)
		{
			s = percentileTable.getS();
			l = percentileTable.getL();
			m = percentileTable.getM();
			
			return calculatePercentile(measurement, m, l, s, measurementUnits);
		}
		
		//if the percentile does not exist for that age,
		//pull the value for .5 months lower and .5 months higher
		//then interpolate by age to the precision of days
		double roundedAge = Math.round(ageInMonths);
		Percentile percentileTableLeft = lookupPercentile(type,roundedAge-0.5,sex);
		Percentile percentileTableRight = lookupPercentile(type,roundedAge+0.5,sex);

		if(percentileTableLeft != null && percentileTableRight != null)
		{
			s = percentileTableLeft.getS();
			l = percentileTableLeft.getL();
			m = percentileTableLeft.getM();
			
			double leftPercentile =  calculatePercentile(measurement, m, l, s, measurementUnits);
			
			s = percentileTableRight.getS();
			l = percentileTableRight.getL();
			m = percentileTableRight.getM();
			
			double rightPercentile =  calculatePercentile(measurement, m, l, s, measurementUnits);
			
			double interpolation = ageInMonths - Math.floor(ageInMonths);
			return (interpolation*Math.abs(rightPercentile-leftPercentile))+Math.min(leftPercentile,rightPercentile);
		}
		
		//if we get here, we can't interpolate because one side is null
		//so just use the non-null side
		if(percentileTableLeft != null)
		{
			percentileTable = percentileTableLeft;
		}
		if(percentileTableRight != null)
		{
			percentileTable = percentileTableRight;
		}

		if(percentileTable != null)
		{
			s = percentileTable.getS();
			l = percentileTable.getL();
			m = percentileTable.getM();
			
			return calculatePercentile(measurement, m, l, s, measurementUnits);
		}
		//return null if we didn't find any percentiles
		return null;
	}

	/**
	 * Calculates a z-score for a given measurement and converts the z-score
	 * into a percentile
	 * 
	 * @param measurement measurement to compute the percentile for
	 * @param m median
	 * @param l power in the Box-Cox transformation
	 * @param s generalized coefficient of variation
	 * @param measurementUnits units of the measurement indicating whether it
	 *        needs converted before computing the percentile
	 * @return
	 */
	private double calculatePercentile(double measurement, double m, double l,
			double s, String measurementUnits)
	{
		measurement = Util.convertUnitsToMetric(measurement, measurementUnits);
		double percentile = 0;
		double zScore = (Math.pow((measurement / m), l) - 1) / (l * s);

		// convert z-score into percentile
		double a = 1 / Math.sqrt(2 * 3.14159625);
		double b = Math.exp(-(Math.pow(Math.abs(zScore), 2) / 2));
		double d = (1 / (1 + 0.33267 * (Math.abs(zScore))));
		double c = ((0.4361836 * d) - (0.1201676 * (Math.pow(d, 2))) + (0.937298 * (Math
				.pow(d, 3))));

		double p = 1 - (a * b * c);

		if (zScore > 0)
		{
			percentile = p * 100;
		} else
		{
			percentile = 100 - (p * 100);
		}

		return percentile;
	}

	/**
	 * Translates string gender into integer sex
	 * 
	 * @param gender string gender ("M" or "F")
	 * @return
	 */
	public int translateGender(String gender)
	{
		int sex = 0;

		if (gender == null)
		{
			return sex;
		}

		if (gender.equalsIgnoreCase(MALE_STRING))
		{
			sex = MALE_INT;
		}

		if (gender.equalsIgnoreCase(FEMALE_STRING))
		{
			sex = FEMALE_INT;
		}

		return sex;
	}
}
