
package org.openmrs.module.chica.rule;

import java.io.IOException;

/**
 * @author msheley This rule updates the immunization registry (CHIRP) via hl7 messages
 * real-time (upon Physician Worksheet scanning)  with
 *  vaccines given during the office visit.
 */
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
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
	private Log log = LogFactory.getLog(this.getClass());
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
		
		if (parameters != null){
			AdministrationService adminService = Context.getAdministrationService();
			EncounterService encounterService = Context.getEncounterService();
			String dir = adminService.getGlobalProperty("chica.ImmunizationOutputDirectory");
			String url = adminService.getGlobalProperty("chica.ImmunizationQueryURL");
			String activateVXU = adminService.getGlobalProperty("chica.activateVXU");
			Integer timeout = Integer.parseInt(adminService.getGlobalProperty("chica.immunizationListTimeout"));
			boolean sendVXU = false;
			if (activateVXU != null && 
				(activateVXU.equalsIgnoreCase("true") || activateVXU.equalsIgnoreCase("yes") 
						|| activateVXU.equalsIgnoreCase("T"))){
				sendVXU = true;
			
			}
			
			//Param1 contains the hl7 VXU message string
			String vxu = (String) parameters.get("param1");
			if (vxu == null || vxu.trim().equals("")){
				log.error("Immunization: VXU is an empty string or is null. " +
						"Do not send to CHIRP. Patient id = " + patientId);
				return Result.emptyResult();
			}
			
			PatientService patientService = Context.getPatientService();
			patientService.getPatient(patientId);
			Integer encounterId = (Integer) parameters.get("encounterId");
			Encounter encounter = encounterService.getEncounter(encounterId);
			ImmunizationQueryConstructor.saveFile(dir, vxu, "vxu", encounter );
			log.info("Immunization: vxu = " + vxu);
			log.info("Immunization: VXU activated = " + sendVXU);
			log.info("Immunization: URL = " + url);
			
			//Use chirdlutil module post method to post to CHIRP.
			
			String postData = getData(vxu);
			String queryResponse = "";
			try {
				if (sendVXU){
					log.info("Immunizaton: Sending VXU with timout =  " + timeout * 1000);
					if (vxu != null && !vxu.trim().equals("")){
						 queryResponse = HttpUtil.post(url, postData,timeout * 1000 , timeout * 1000);
					}
					log.info("Immunization: VXU response =  " + queryResponse);
				}else {
					log.info("Immunization: VXU not sent.");
				}
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
	System.out.println("url string: " + data);
	
	return data;
	
	
}
}