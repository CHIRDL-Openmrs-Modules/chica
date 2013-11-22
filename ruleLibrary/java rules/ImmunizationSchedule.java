/********************************************************************
 Translated from - ImmunizationSchedule.mlm on Fri Jul 12 16:20:17 EDT 2013

 Title:  ImmunizationSchedule
 Filename:  ImmunizationSchedule
 Version: 1.0
 Institution:  Indiana University School of Medicine
 Author:  Meena Sheley
 Specialist:  Pediatrics
 Date: 303-10-16T11:38:57-0400
 Validation :
 Purpose:  Fill in ImmunizationSchedule.
 Explanation: 
 Keywords:  Developmental
 Citations: 
 Links: 
********************************************************************/
package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.logic.rule.provider.RuleProvider;
import org.openmrs.module.dss.DssRule;
import org.openmrs.logic.Duration;
import java.util.StringTokenizer;

import org.openmrs.api.ConceptService;
import java.text.SimpleDateFormat;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
public class ImmunizationSchedule implements Rule, DssRule{

	private Log log = LogFactory.getLog(this.getClass());

	/*** @see org.openmrs.logic.rule.Rule#getDuration()*/
	public int getDuration() {
		return 60*30;   // 30 minutes
	}

	/*** @see org.openmrs.logic.rule.Rule#getDatatype(String)*/
	public Datatype getDatatype(String token) {
		return Datatype.TEXT;
	}

	/*** @see org.openmrs.logic.rule.Rule#getParameterList()*/
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	/*** @see org.openmrs.logic.rule.Rule#getDependencies()*/
	public String[] getDependencies() {
		return new String[] { };
	}

	/*** @see org.openmrs.logic.rule.Rule#getTTL()*/
	public int getTTL() {
		return 0; //60 * 30; // 30 minutes
	}

	/*** @see org.openmrs.logic.rule.Rule#getDatatype(String)*/
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAuthor()*/
	public String getAuthor(){
		return "Meena Sheley";
	}

	/*** @see org.openmrs.module.dss.DssRule#getCitations()*/
	public String getCitations(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getDate()*/
	public String getDate(){
		return "303-10-16T11:38:57-0400";
	}

	/*** @see org.openmrs.module.dss.DssRule#getExplanation()*/
	public String getExplanation(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getInstitution()*/
	public String getInstitution(){
		return "Indiana University School of Medicine";
	}

	/*** @see org.openmrs.module.dss.DssRule#getKeywords()*/
	public String getKeywords(){
		return "Developmental";
	}

	/*** @see org.openmrs.module.dss.DssRule#getLinks()*/
	public String getLinks(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getPurpose()*/
	public String getPurpose(){
		return "Fill in ImmunizationSchedule.";
	}

	/*** @see org.openmrs.module.dss.DssRule#getSpecialist()*/
	public String getSpecialist(){
		return "Pediatrics";
	}

	/*** @see org.openmrs.module.dss.DssRule#getTitle()*/
	public String getTitle(){
		return "ImmunizationSchedule";
	}

	/*** @see org.openmrs.module.dss.DssRule#getVersion()*/
	public Double getVersion(){
		return 1.0;
	}

	/*** @see org.openmrs.module.dss.DssRule#getType()*/
	public String getType(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getPriority()*/
	public Integer getPriority(){
		return 0;
	}

	/*** @see org.openmrs.module.dss.DssRule#getData()*/
	public String getData(){
		return "read If endif If";
	}

	/*** @see org.openmrs.module.dss.DssRule#getLogic()*/
	public String getLogic(){
		return "If CALL CALL endif If if || conclude endif conclude endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAction()*/
	public String getAction(){
		return "write write write";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMin()*/
	public Integer getAgeMin(){
		return 0;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMinUnits()*/
	public String getAgeMinUnits(){
		return "years";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMax()*/
	public Integer getAgeMax(){
		return 21;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMaxUnits()*/
	public String getAgeMaxUnits(){
		return "years";
	}

private static boolean containsIgnoreCase(Result key,List<Result> lst){
if(key == null){
return false;
}
String keyString = "";
if(key.getDatatype() == Result.Datatype.CODED) {
	Concept keyConcept = key.toConcept();
	if(keyConcept != null) {
		keyString = ((ConceptName) keyConcept.getNames().toArray()[0]).getName();
	}
} else {
	keyString = key.toString();
}
for(Result element:lst){
Concept concept = element.toConcept();
if(concept == null){
continue;
}
String elementString = ((ConceptName) concept.getNames().toArray()[0]).getName();
if(keyString.equalsIgnoreCase(elementString)){
return true;
}
}
return false;
}
	private static String toProperCase(String str){

		if(str == null || str.length()<1){
			return str;
		}

		StringBuffer resultString = new StringBuffer();
		String delimiter = " ";
		StringTokenizer tokenizer = new StringTokenizer(str,delimiter,true);
		String currToken = null;

		while(tokenizer.hasMoreTokens()){
			currToken = tokenizer.nextToken();
			if(!currToken.equals(delimiter)){
				if(currToken.length()>0){
					currToken = currToken.substring(0, 1).toUpperCase()
					+ currToken.substring(1).toLowerCase();
				}
			}
			resultString.append(currToken);
		}
		return resultString.toString();
	}

	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException {

		String actionStr = "";
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		HashMap<String, Result> resultLookup = new HashMap <String, Result>();
		Boolean ageOK = null;
		try {
			RuleProvider ruleProvider = (RuleProvider)parameters.get("ruleProvider");
			HashMap<String, String> userVarMap = new HashMap <String, String>();
			String firstname = patient.getPersonName().getGivenName();
			userVarMap.put("firstname", toProperCase(firstname));
			String lastName = patient.getFamilyName();
			userVarMap.put("lastName", lastName);
			String gender = patient.getGender();
			userVarMap.put("Gender", gender);
			if(gender.equalsIgnoreCase("M")){
				userVarMap.put("gender","his");
				userVarMap.put("hisher","his");
			}else{
				userVarMap.put("gender","her");
				userVarMap.put("hisher","her");
			}
			ArrayList<String> actions = initAction();

			Result mode=new Result((String) parameters.get("mode"));
			resultLookup.put("mode",mode);		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){

			Result chirpStatus=context.read(
				patient.getPatientId(),context.getLogicDataSource("obs"),
				new LogicCriteriaImpl("CHIRP_Status").within(Duration.days(-2)).last());
			resultLookup.put("chirpStatus",chirpStatus);}		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("CONSUME"))){
}

			if(evaluate_logic(parameters, context, ruleProvider, patient, userVarMap, resultLookup)){
				Result ruleResult = new Result();
		Result chirpStatus = (Result) resultLookup.get("chirpStatus");

				for(String currAction:actions){
					currAction = doAction(currAction, userVarMap, resultLookup);
					ruleResult.add(new Result(currAction));
				}
				return ruleResult;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return Result.emptyResult();
		}
		return Result.emptyResult();
	}

	private boolean evaluate_logic(Map<String, Object> parameters, LogicContext context, RuleProvider ruleProvider, Patient patient, HashMap<String, String> userVarMap, HashMap<String, Result> resultLookup) throws LogicException {

		Result Gender = new Result(userVarMap.get("Gender"));
		Result chirpStatus = (Result) resultLookup.get("chirpStatus");
		Result mode = (Result) resultLookup.get("mode");

		Object value = null;
		String variable = null;
		int varLen = 0;
		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("CONSUME"))){
				if (ruleProvider != null) {
					ruleProvider.getRule("createVXUMessage");
				}
				Result VXUResult = context.eval(patient.getPatientId(), "createVXUMessage",parameters);
				resultLookup.put("VXUResult",VXUResult);
				varLen = "VXUResult".length();
				value=userVarMap.get("VXUResult");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("VXUResult".endsWith("_value"))
				{
					variable = "VXUResult".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("VXUResult".endsWith("_date"))
				{
					variable = "VXUResult".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("VXUResult".endsWith("_object"))
				{
					variable = "VXUResult".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("VXUResult") != null){
						value = resultLookup.get("VXUResult").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","VXUResult");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("sendCHIRPUpdate");
				}
				context.eval(patient.getPatientId(), "sendCHIRPUpdate",parameters);
				}		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){
		if((!chirpStatus.isNull()&&chirpStatus.toString().equalsIgnoreCase("CHIRP_patient_created"))){
			//preprocess any || operator ;
			String val = doAction("This child does not exist in CHIRP.  Please fill out any immunization information you have. A new CHIRP record can be created when scanned.", userVarMap, resultLookup);
			userVarMap.put("Text",  val);
		}
			return true;
		}
			return false;
	}

	public ArrayList<String> initAction() {
		ArrayList<String> actions = new ArrayList<String>();
		actions.add("");
		actions.add("|| Text ||@CHIRP_Status");
		actions.add("I have read or had explained to me the information contained in the Vaccine Information Sheets about the vaccines checked above.  I have had my questions answered to my satisfaction and believe I understand the benefits & risks of the vaccines that my child will receive.  I also understand this immunization record will be in the electronic medical record the Indiana registry and available to other providers and schools.@Note");


		return actions;
	}

private String substituteString(String variable,String outStr, HashMap<String, String> userVarMap, HashMap<String, Result> resultLookup){
//see if the variable is in the user map
String value = userVarMap.get(variable);
if (value != null)
{
}
// It must be a result value or date
else if (variable.contains("_value"))
{
	variable = variable.replace("_value","").trim();
if(resultLookup.get(variable) != null){value = resultLookup.get(variable).toString();
}}
// It must be a result date
else if (variable.contains("_date"))
{
String pattern = "MM/dd/yy";
SimpleDateFormat dateForm = new SimpleDateFormat(pattern);
variable = variable.replace("_date","").trim();
if(resultLookup.get(variable) != null){value = dateForm.format(resultLookup.get(variable).getResultDate());
}}
else
{
if(resultLookup.get(variable) != null){value = resultLookup.get(variable).toString();
}}
if (value != null)
{
	outStr += value;
}
return outStr;
}
public String doAction(String inStr, HashMap<String, String> userVarMap, HashMap<String, Result> resultLookup)
{
int startindex = -1;
int endindex = -1;
int index = -1;
String outStr = "";
while((index = inStr.indexOf("||"))>-1)
{
if(startindex == -1){
startindex = 0;
outStr+=inStr.substring(0,index);
}else if(endindex == -1){
endindex = index-1;
String variable = inStr.substring(startindex, endindex).trim();
outStr = substituteString(variable,outStr,userVarMap,resultLookup);
startindex = -1;
endindex = -1;
}
inStr = inStr.substring(index+2);
}
outStr+=inStr;
return outStr;
}
}