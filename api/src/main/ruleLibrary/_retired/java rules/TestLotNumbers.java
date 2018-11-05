/********************************************************************
 Translated from - mcad.mlm on Fri Dec 28 15:10:34 EST 2007

 Title : MCAD Reminder
 Filename:  mcad
 Version : 0 . 2
 Institution : Indiana University School of Medicine
 Author : Steve Downs
 Specialist : Pediatrics
 Date : 05 - 22 - 2007
 Validation :
 Purpose : Provides a specific reminder, tailored to the patient who identified one or more fatty acid disorders
 Explanation : Based on AAP screening recommendations
 Keywords : fatty, acid, fatty acid disorder
 Citations : Screening for fatty acid disorder AAP
 Links :

 ********************************************************************/
package org.openmrs.module.chica.rule;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.hl7.immunization.ImmunizationQueryConstructor;
import org.openmrs.module.chirdlutil.util.HttpUtil;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

public class TestLotNumbers implements Rule
{

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}

	public Result eval(LogicContext context, Integer patientId,
	       			Map<String, Object> parameters) throws LogicException
	{
		
		String vxu = null;
		AdministrationService adminService = Context.getAdministrationService();
		//String dir = adminService.getGlobalProperty("chica.ImmunizationOutputDirectory");
		String url = "https://chirp.in.gov";
		Integer timeout = Integer.parseInt(adminService.getGlobalProperty("chica.SSLConnectionTimeout"));
		//Result result = (Result) parameters.get("param1");
		//if (result != null){
		String data = getData("");	
		System.out.println( "test" + data );
		
		String queryResponse = "";
		try {
			queryResponse = HttpUtil.post(url, data,120000 ,120000 );
			System.out.println( "response" + queryResponse );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println( "lot number query error " + queryResponse );
			e.printStackTrace();
		}
		
		
		System.out.println( "lot number query" + queryResponse );
		
		
		return Result.emptyResult();
	}
	
	private String getData(String message){
		String data = "";
		try {
			AdministrationService adminService = Context.getAdministrationService();
			String userId = "wishardhl7";
			String password = "chica101";
			String url = "https://chirp.in.gov/reportLotNumberSummary.do";
			

			data = URLEncoder.encode("USERID", "UTF-8") + "="
			+ URLEncoder.encode(userId, "UTF-8");
			data += "&" + URLEncoder.encode("PASSWORD", "UTF-8") + "="
			+ URLEncoder.encode(password, "UTF-8");
			
			data += "&" + URLEncoder.encode("selectedVaccinesOptions", "UTF-8") + "="
			 +  URLEncoder.encode("3", "UTF-8");
			data += "&" + URLEncoder.encode("siisFacilityId", "UTF-8") + "="
			 +  URLEncoder.encode("10561", "UTF-8");
			 data += "&" + URLEncoder.encode("irms", "UTF-8") + "="
			 +  URLEncoder.encode("494044", "UTF-8");
			 data += "&" + URLEncoder.encode("limitByFacilityOrGroup", "UTF-8") + "="
			 +  URLEncoder.encode("facility", "UTF-8");
			
			System.out.println(data);
			/*data += "&" + "alimitByIrmsSystemId=Y&reportFromDate="
				 + "&selectedVaccinesOptions=3%5EMMR&reportToDate=&displaySubtotalByVaccine=Y"
				 + "&lotNumber=&registryViewIRMS=494044&siisFacilityId=10561&irms=494044"
				 + "&expirationFromDate=&limitByVaccineName=true&expirationToDate="
				 + "&isProvider=&homeSiisFacilityId=&manufacturer=-1" 
				 + "&limitByFacilityOrGroup=facility";*/
			
			
		} catch (APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("data is " + data);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println("data is " + data);
			e.printStackTrace();
		}
		
		return data;
	}
}