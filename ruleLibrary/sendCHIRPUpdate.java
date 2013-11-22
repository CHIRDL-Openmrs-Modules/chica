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

public class sendCHIRPUpdate implements Rule
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
		AdministrationService adminService = Context.getAdministrationService();
		String dir = adminService.getGlobalProperty("chica.ImmunizationOutputDirectory");
		String url = adminService.getGlobalProperty("chica.ImmunizationQueryURL");
		String activateVXU = adminService.getGlobalProperty("chica.activateVXU");
		Integer timeout = Integer.parseInt(adminService.getGlobalProperty("chica.SSLConnectionTimeout"));
		boolean sendVXU = false;
		
		String vxu = (String) parameters.get("param0");
		PatientService patientService = Context.getPatientService();
		patientService.getPatient(patientId);
		
		if (activateVXU != null && 
				(activateVXU.equal("true") || activateVXU.equalsIgnoreCase("yes") 
						|| activateVXU.equalsIgnoreCase("T"))){
			sendVXU = true;
			
		}
		
		
		//Probably need to add a state for sending the vxu
		if (sendVXU){
			String data = "";
			try {
				String queryResponse = HttpUtil.post(url, vxu,0 ,0 );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
		return Result.emptyResult();
	}
	
	private String getData(String message){
		String data = "";
		try {
			AdministrationService adminService = Context.getAdministrationService();
			String userId = adminService
			.getGlobalProperty("chica.ImmunizationQueryUserId");
			String password = adminService
			.getGlobalProperty("chica.ImmunizationQueryPassword");
			String url = adminService
			.getGlobalProperty("chica.ImmunizationQueryURL");

			data = URLEncoder.encode("USERID", "UTF-8") + "="
			+ URLEncoder.encode(userId, "UTF-8");
			data += "&" + URLEncoder.encode("PASSWORD", "UTF-8") + "="
			+ URLEncoder.encode(password, "UTF-8");
			
			if (message != null && message.contains("VXU")){
				data += "&" + URLEncoder.encode("debug", "UTF-8") + "="
				+ URLEncoder.encode("debug", "UTF-8");
				data += "&" + URLEncoder.encode("deduplication", "UTF-8") + "="
				+ URLEncoder.encode("deduplication", "UTF-8");
			}
			data += "&" + URLEncoder.encode("MESSAGEDATA", "UTF-8") + "="
			+ URLEncoder.encode(message, "UTF-8");
		} catch (APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}
}