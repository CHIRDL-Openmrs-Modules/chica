/**
 * 
 */
package org.openmrs.module.chica;

import java.util.Calendar;
import java.util.Date;

import org.openmrs.api.context.Context;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.Util;

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
	public String calculatePercentileAsString(Double measurement, String gender, 
	                                          Date birthdate, String type, String measurementUnits)
	{
		if (measurement == null || gender == null || gender.equalsIgnoreCase("U") || birthdate == null || type == null) {
			return null;
		}
		
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
	
	private Percentile lookupPercentile(String type, Double agemos, Integer sex) 
	{
		if (type == null || agemos == null || sex == null) {
			return null;
		}
		
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
	
	public Double calculatePercentile(Double measurement, String gender, Date birthdate, String type,
	                                  String measurementUnits, Date currDate) 
	{
		
		if (measurement == null || gender == null || gender.equalsIgnoreCase("U") || birthdate == null || type == null) {
			return null;
		}
		
		Double ageInMonths = Util.getFractionalAgeInUnits(birthdate, currDate, Util.MONTH_ABBR);
		Integer sex = translateGender(gender);
		Double s = 0D;
		Double l = 0D;
		Double m = 0D;
		
		//look up the percentile for the age in months
		Percentile percentileTable = lookupPercentile(type, ageInMonths, sex);
		
		if (percentileTable != null) 
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
		
		double leftAge = roundedAge;
		
		if(leftAge >0){
			leftAge= roundedAge - 0.5;
		}
		double rightAge = roundedAge + 0.5;
		Percentile percentileTableLeft = lookupPercentile(type,leftAge, sex);
		Percentile percentileTableRight = lookupPercentile(type, rightAge, sex);
		
		if (percentileTableLeft != null && percentileTableRight != null) {
			s = percentileTableLeft.getS();
			l = percentileTableLeft.getL();
			m = percentileTableLeft.getM();
			
			double leftPercentile = calculatePercentile(measurement, m, l, s, measurementUnits);
			
			s = percentileTableRight.getS();
			l = percentileTableRight.getL();
			m = percentileTableRight.getM();
			
			double rightPercentile = calculatePercentile(measurement, m, l, s, measurementUnits);
			
			return interpolate(leftAge, rightAge, leftPercentile, rightPercentile, ageInMonths);	
		}
		
		//if we get here, we can't interpolate because one side is null
		//so just use the non-null side
		if (percentileTableLeft != null) 
		{
			percentileTable = percentileTableLeft;
		}
		if (percentileTableRight != null) 
		{
			percentileTable = percentileTableRight;
		}
		
		if (percentileTable != null) 
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
	 * Calculates the percentile of the measurement
	 * 
	 * @param measurement measurement for the patient
	 * @param gender gender of the patient
	 * @param birthdate birthdate of the patient used to compute age
	 * @param type type of percentile (weight, hc, height, BMI)
	 * @param measurementUnits units of the measurement indicating whether it needs converted before
	 *            computing the percentile
	 * @return
	 */
	public Double calculatePercentile(Double measurement, String gender, Date birthdate, String type, String measurementUnits) {
		return calculatePercentile(measurement, gender, birthdate, type, measurementUnits, Calendar.getInstance().getTime());
	}
	
	private Double interpolate(Double leftX, Double rightX, Double leftY, Double rightY, Double xValue){
		double result = (Math.abs(xValue-leftX)/(Math.abs(rightX-leftX)))*Math.abs(rightY-leftY);
		
		if(leftY <= rightY){
			return leftY+result;
		}else{
			return leftY-result;
		}
	}
	
	public Double calculateZscore(Double measurement, String gender, Date birthdate, String type, Date currDate) {
		
		if (measurement == null || gender == null || gender.equalsIgnoreCase("U") || birthdate == null || type == null) {
			return null;
		}
		
		Double ageInMonths = Util.getFractionalAgeInUnits(birthdate, currDate, Util.MONTH_ABBR);
		Integer sex = translateGender(gender);
		Double s = 0D;
		Double l = 0D;
		Double m = 0D;
		
		//look up the percentile for the age in months
		Percentile percentileTable = lookupPercentile(type, ageInMonths, sex);
		
		if (percentileTable != null) {
			s = percentileTable.getS();
			l = percentileTable.getL();
			m = percentileTable.getM();
			
			return computeZscore(measurement, m, l, s);
		}
		
		//if the percentile does not exist for that age,
		//pull the value for .5 months lower and .5 months higher
		//then interpolate by age to the precision of days
		double roundedAge = Math.round(ageInMonths);
		double leftAge = roundedAge;
		
		if(leftAge >0){
			leftAge= roundedAge - 0.5;
		}
		double rightAge = roundedAge + 0.5;
		Percentile percentileTableLeft = lookupPercentile(type,leftAge, sex);
		Percentile percentileTableRight = lookupPercentile(type, rightAge, sex);
		
		if (percentileTableLeft != null && percentileTableRight != null) {
			s = percentileTableLeft.getS();
			l = percentileTableLeft.getL();
			m = percentileTableLeft.getM();
			
			double leftZscore = computeZscore(measurement, m, l, s);
			
			s = percentileTableRight.getS();
			l = percentileTableRight.getL();
			m = percentileTableRight.getM();
			
			double rightZscore = computeZscore(measurement, m, l, s);
			
			return interpolate(leftAge, rightAge, leftZscore, rightZscore, ageInMonths);
		}
		
		//if we get here, we can't interpolate because one side is null
		//so just use the non-null side
		if (percentileTableLeft != null) {
			percentileTable = percentileTableLeft;
		}
		if (percentileTableRight != null) {
			percentileTable = percentileTableRight;
		}
		
		if (percentileTable != null) {
			s = percentileTable.getS();
			l = percentileTable.getL();
			m = percentileTable.getM();
			
			return computeZscore(measurement, m, l, s);
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
	 * needs converted before computing the percentile
	 * @return
	 */
	private Double calculatePercentile(Double measurement, Double m, Double l,
	                                   Double s, String measurementUnits) 
	{
		if (measurement == null || m == null || l == null || s == null) {
			return null;
		}
		measurement = Util.convertUnitsToMetric(measurement, measurementUnits);
		
		Double zScore = computeZscore(measurement, m, l, s);
		
		return convertZscoreToPercentile(zScore);
	}
	
	private Double computeZscore(Double measurement, Double m, Double l, Double s) {
		
		if (measurement == null || m == null || l == null || s == null) {
			return null;
		}
		return (Math.pow((measurement / m), l) - 1) / (l * s);
	}
	
	public Double computeBloodPressurePercentile(Date birthdate, String gender, Double height, Double observedBloodPressure,
	                                             String bloodPressureType, String measurementUnits) {
		return computeBloodPressurePercentile(birthdate, gender, height, observedBloodPressure, bloodPressureType,
		    measurementUnits, Calendar.getInstance().getTime());
	}
	
	/**
	 * Compute the blood pressure percentile based on page 47 and 48 of
	 * https://www.nhlbi.nih.gov/files/docs/resources/heart/hbp_ped.pdf
	 * THE FOURTH REPORT ON THE Diagnosis, Evaluation, and Treatment of High Blood Pressure in Children and Adolescents
	 * 
	 * @param birthdate
	 * @param gender
	 * @param height
	 * @param observedBloodPressure
	 * @param bloodPressureType
	 * @param measurementUnits
	 * @param currDate
	 * @return
	 */
	public Double computeBloodPressurePercentile(Date birthdate, String gender, Double height, Double observedBloodPressure,
	                                             String bloodPressureType, String measurementUnits, Date currDate) {
		
		if (birthdate == null || gender == null || gender.equalsIgnoreCase("U") || height == null ||
				observedBloodPressure == null || bloodPressureType == null) {
			return null;
		}
		
		//regression coefficients
		final double A1_MALE_SYSTOLIC = 102.19768;
		final double A1_FEMALE_SYSTOLIC = 102.01027;
		final double A1_MALE_DIASTOLIC = 61.01217;
		final double A1_FEMALE_DIASTOLIC = 60.50510;
		
		final double B1_MALE_SYSTOLIC = 1.82416;
		final double B1_FEMALE_SYSTOLIC = 1.94397;
		final double B1_MALE_DIASTOLIC = 0.68314;
		final double B1_FEMALE_DIASTOLIC = 1.01301;
		
		final double B2_MALE_SYSTOLIC = 0.12766;
		final double B2_FEMALE_SYSTOLIC = 0.00598;
		final double B2_MALE_DIASTOLIC = -0.09835;
		final double B2_FEMALE_DIASTOLIC = 0.01157;
		
		final double B3_MALE_SYSTOLIC = 0.00249;
		final double B3_FEMALE_SYSTOLIC = -0.00789;
		final double B3_MALE_DIASTOLIC = 0.01711;
		final double B3_FEMALE_DIASTOLIC = 0.00424;
		
		final double B4_MALE_SYSTOLIC = -0.00135;
		final double B4_FEMALE_SYSTOLIC = -0.00059;
		final double B4_MALE_DIASTOLIC = -0.00045;
		final double B4_FEMALE_DIASTOLIC = -0.00137;
		
		final double G1_MALE_SYSTOLIC = 2.73157;
		final double G1_FEMALE_SYSTOLIC = 2.03526;
		final double G1_MALE_DIASTOLIC = 1.46993;
		final double G1_FEMALE_DIASTOLIC = 1.16641;
		
		final double G2_MALE_SYSTOLIC = -0.19618;
		final double G2_FEMALE_SYSTOLIC = 0.02534;
		final double G2_MALE_DIASTOLIC = -0.07849;
		final double G2_FEMALE_DIASTOLIC = 0.12795;
		
		final double G3_MALE_SYSTOLIC = -0.04659;
		final double G3_FEMALE_SYSTOLIC = -0.01884;
		final double G3_MALE_DIASTOLIC = -0.03144;
		final double G3_FEMALE_DIASTOLIC = -0.03869;
		
		final double G4_MALE_SYSTOLIC = 0.00947;
		final double G4_FEMALE_SYSTOLIC = 0.00121;
		final double G4_MALE_DIASTOLIC = 0.00967;
		final double G4_FEMALE_DIASTOLIC = -0.00079;
		
		final double STANDARD_DEV_MALE_SYSTOLIC = 10.7128;
		final double STANDARD_DEV_FEMALE_SYSTOLIC = 10.4855;
		final double STANDARD_DEV_MALE_DIASTOLIC = 11.6032;
		final double STANDARD_DEV_FEMALE_DIASTOLIC = 10.9573;
		
		height = Util.convertUnitsToMetric(height, measurementUnits);
		Double heightZscore = calculateZscore(height, gender, birthdate, "length", currDate);
		if (heightZscore == null) {
			return null;
		}
		
		double age = Util.getAgeInUnits(birthdate, new Date(), Util.YEAR_ABBR);
		
		double a1 = 0;
		double b1 = 0;
		double b2 = 0;
		double b3 = 0;
		double b4 = 0;
		double g1 = 0;
		double g2 = 0;
		double g3 = 0;
		double g4 = 0;
		double standardDeviation = 0;
		
		if (gender.equalsIgnoreCase("F")) {
			if (bloodPressureType.equalsIgnoreCase("diastolic")) {
				a1 = A1_FEMALE_DIASTOLIC;
				b1 = B1_FEMALE_DIASTOLIC;
				b2 = B2_FEMALE_DIASTOLIC;
				b3 = B3_FEMALE_DIASTOLIC;
				b4 = B4_FEMALE_DIASTOLIC;
				g1 = G1_FEMALE_DIASTOLIC;
				g2 = G2_FEMALE_DIASTOLIC;
				g3 = G3_FEMALE_DIASTOLIC;
				g4 = G4_FEMALE_DIASTOLIC;
				standardDeviation = STANDARD_DEV_FEMALE_DIASTOLIC;
			} else {
				a1 = A1_FEMALE_SYSTOLIC;
				b1 = B1_FEMALE_SYSTOLIC;
				b2 = B2_FEMALE_SYSTOLIC;
				b3 = B3_FEMALE_SYSTOLIC;
				b4 = B4_FEMALE_SYSTOLIC;
				g1 = G1_FEMALE_SYSTOLIC;
				g2 = G2_FEMALE_SYSTOLIC;
				g3 = G3_FEMALE_SYSTOLIC;
				g4 = G4_FEMALE_SYSTOLIC;
				standardDeviation = STANDARD_DEV_FEMALE_SYSTOLIC;
			}
			
		} else {
			if (bloodPressureType.equalsIgnoreCase("diastolic")) {
				a1 = A1_MALE_DIASTOLIC;
				b1 = B1_MALE_DIASTOLIC;
				b2 = B2_MALE_DIASTOLIC;
				b3 = B3_MALE_DIASTOLIC;
				b4 = B4_MALE_DIASTOLIC;
				g1 = G1_MALE_DIASTOLIC;
				g2 = G2_MALE_DIASTOLIC;
				g3 = G3_MALE_DIASTOLIC;
				g4 = G4_MALE_DIASTOLIC;
				standardDeviation = STANDARD_DEV_MALE_DIASTOLIC;
			} else {
				a1 = A1_MALE_SYSTOLIC;
				b1 = B1_MALE_SYSTOLIC;
				b2 = B2_MALE_SYSTOLIC;
				b3 = B3_MALE_SYSTOLIC;
				b4 = B4_MALE_SYSTOLIC;
				g1 = G1_MALE_SYSTOLIC;
				g2 = G2_MALE_SYSTOLIC;
				g3 = G3_MALE_SYSTOLIC;
				g4 = G4_MALE_SYSTOLIC;
				standardDeviation = STANDARD_DEV_MALE_SYSTOLIC;
			}
		}
		
		//compute the expected blood pressure based on regression models
		double expectedBloodPressure = a1 + b1*(age-10)+b2*(age-10)*(age-10)+
			b3*(age-10)*(age-10)*(age-10)+b4*(age-10)*(age-10)*(age-10)*(age-10)+
			g1*heightZscore+g2*heightZscore*heightZscore+g3*heightZscore*heightZscore*heightZscore+
			g4*heightZscore*heightZscore*heightZscore*heightZscore;
		
		//compute the z-score for the observed blood pressure
		Double bloodPressureZscore = (observedBloodPressure - expectedBloodPressure) / standardDeviation;
		
		//convert the z-score to a percentile
		return convertZscoreToPercentile(bloodPressureZscore);
	}
	
	private Double convertZscoreToPercentile(Double zScore) {
		
		if (zScore == null) {
			return null;
		}
		double percentile = 0;
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
	public Integer translateGender(String gender) 
	{
		if (gender == null) 
		{
			return null;
		}
		
		if (gender.equalsIgnoreCase(MALE_STRING)) 
		{
			return MALE_INT;
		}
		
		if (gender.equalsIgnoreCase(FEMALE_STRING)) 
		{
			return FEMALE_INT;
		}
		
		return null;
	}
}
