/********************************************************************
 Translated from - R_HPV_Next_Dose_Needed_PWS.mlm on Mon Apr 20 11:12:14 EDT 2015

 Title:  Research HPV, next dose needed Scripted PWS
 Filename:  R_HPV_Next_Dose_Needed_PWS
 Version: 1.0
 Institution:  Indiana University School of Medicine
 Author:  Steve Downs
 Specialist:  Pediatrics
 Date: 
 Validation :
 Purpose:  Alert the MD that teen is due for a second or third HPV vaccine.
 Explanation:  If there is a record of one or two shots, the doc is alerted to provide the booster, language.
 Keywords:  HPV, meningococcal, tetanus, vaccine, research, Merck
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
public class R_HPV_Next_Dose_Needed_PWS implements Rule, DssRule{

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
		return "Steve Downs";
	}

	/*** @see org.openmrs.module.dss.DssRule#getCitations()*/
	public String getCitations(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getDate()*/
	public String getDate(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getExplanation()*/
	public String getExplanation(){
		return "If there is a record of one or two shots, the doc is alerted to provide the booster, language.";
	}

	/*** @see org.openmrs.module.dss.DssRule#getInstitution()*/
	public String getInstitution(){
		return "Indiana University School of Medicine";
	}

	/*** @see org.openmrs.module.dss.DssRule#getKeywords()*/
	public String getKeywords(){
		return "HPV, meningococcal, tetanus, vaccine, research, Merck";
	}

	/*** @see org.openmrs.module.dss.DssRule#getLinks()*/
	public String getLinks(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getPurpose()*/
	public String getPurpose(){
		return "Alert the MD that teen is due for a second or third HPV vaccine.";
	}

	/*** @see org.openmrs.module.dss.DssRule#getSpecialist()*/
	public String getSpecialist(){
		return "Pediatrics";
	}

	/*** @see org.openmrs.module.dss.DssRule#getTitle()*/
	public String getTitle(){
		return "Research HPV, next dose needed Scripted PWS";
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
		return 10;
	}

	/*** @see org.openmrs.module.dss.DssRule#getData()*/
	public String getData(){
		return "read read read read read read read If endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getLogic()*/
	public String getLogic(){
		return "call If conclude If If conclude call call If conclude If conclude If || If || If || conclude endif If CALL If CALL CALL endif If CALL CALL endif If CALL CALL endif If CALL CALL endif If CALL CALL endif endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAction()*/
	public String getAction(){
		return "write write write write write write write";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMin()*/
	public Integer getAgeMin(){
		return 11;
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
			resultLookup.put("mode",mode);
			Result Box1=new Result((String) parameters.get("box1"));
			resultLookup.put("Box1",Box1);
			Result Box2=new Result((String) parameters.get("box2"));
			resultLookup.put("Box2",Box2);
			Result Box3=new Result((String) parameters.get("box3"));
			resultLookup.put("Box3",Box3);
			Result Box4=new Result((String) parameters.get("box4"));
			resultLookup.put("Box4",Box4);
			Result Box5=new Result((String) parameters.get("box5"));
			resultLookup.put("Box5",Box5);
			Result Box6=new Result((String) parameters.get("box6"));
			resultLookup.put("Box6",Box6);		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){

			Result VisitType=context.read(
				patient.getPatientId(),context.getLogicDataSource("obs"),
				new LogicCriteriaImpl("VisitType").within(Duration.days(-1)).last());
			resultLookup.put("VisitType",VisitType);}

			if(evaluate_logic(parameters, context, ruleProvider, patient, userVarMap, resultLookup)){
				Result ruleResult = new Result();
		Result VisitType = (Result) resultLookup.get("VisitType");

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
		Result Box1 = (Result) resultLookup.get("Box1");
		Result VisitType = (Result) resultLookup.get("VisitType");
		Result Box2 = (Result) resultLookup.get("Box2");
		Result Box5 = (Result) resultLookup.get("Box5");
		Result Box6 = (Result) resultLookup.get("Box6");
		Result Box3 = (Result) resultLookup.get("Box3");
		Result Box4 = (Result) resultLookup.get("Box4");
		Result mode = (Result) resultLookup.get("mode");

		Object value = null;
		String variable = null;
		int varLen = 0;
				varLen = "hpvStudyArm".length();
				value=userVarMap.get("hpvStudyArm");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("hpvStudyArm".endsWith("_value"))
				{
					variable = "hpvStudyArm".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hpvStudyArm".endsWith("_date"))
				{
					variable = "hpvStudyArm".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hpvStudyArm".endsWith("_object"))
				{
					variable = "hpvStudyArm".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hpvStudyArm") != null){
						value = resultLookup.get("hpvStudyArm").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","hpvStudyArm");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("providerAttributeLookup");
				}
				Result hpvStudyArm = context.eval(patient.getPatientId(), "providerAttributeLookup",parameters);
				resultLookup.put("hpvStudyArm",hpvStudyArm);
		if((hpvStudyArm.isNull())||
			!(!hpvStudyArm.isNull()&&hpvStudyArm.toString().equalsIgnoreCase("prompt only arm"))){
			return false;
		}
		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){
		if((!VisitType.isNull()&&VisitType.toString().equalsIgnoreCase("SickVisit"))){
			return false;
		}
				varLen = "HPV".length();
				value=userVarMap.get("HPV");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("HPV".endsWith("_value"))
				{
					variable = "HPV".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("HPV".endsWith("_date"))
				{
					variable = "HPV".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("HPV".endsWith("_object"))
				{
					variable = "HPV".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("HPV") != null){
						value = resultLookup.get("HPV").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","HPV");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("LookupVaccineGiven");
				}
				Result hpv_doses = context.eval(patient.getPatientId(), "LookupVaccineGiven",parameters);
				resultLookup.put("hpv_doses",hpv_doses);
				varLen = "HPV".length();
				value=userVarMap.get("HPV");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("HPV".endsWith("_value"))
				{
					variable = "HPV".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("HPV".endsWith("_date"))
				{
					variable = "HPV".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("HPV".endsWith("_object"))
				{
					variable = "HPV".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("HPV") != null){
						value = resultLookup.get("HPV").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","HPV");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("LookupVaccineDue");
				}
				Result hpv_due = context.eval(patient.getPatientId(), "LookupVaccineDue",parameters);
				resultLookup.put("hpv_due",hpv_due);
		if((hpv_due.isNull())){
			return false;
		}
		if((!hpv_due.isNull()&&(hpv_due.toNumber()!= null&&hpv_due.toNumber() ==  1))){
			return false;
		}
		if((!hpv_due.isNull()&&(hpv_due.toNumber()!= null&&hpv_due.toNumber() ==  2))){
			//preprocess any || operator ;
			String val = doAction("second", userVarMap, resultLookup);
			userVarMap.put("dose_Text",  val);
		}
		if((!hpv_due.isNull()&&(hpv_due.toNumber()!= null&&hpv_due.toNumber() ==  2))){
			//preprocess any || operator ;
			String val = doAction("scheduled for third dose", userVarMap, resultLookup);
			userVarMap.put("followUpText",  val);
		}
		if((!hpv_due.isNull()&&(hpv_due.toNumber()!= null&&hpv_due.toNumber() ==  3))){
			//preprocess any || operator ;
			String val = doAction("third", userVarMap, resultLookup);
			userVarMap.put("doseText",  val);
		}
			return true;
		}
		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("CONSUME"))){
				varLen = "Patient is due for an HPV vaccine boster today.".length();
				value=userVarMap.get("Patient is due for an HPV vaccine boster today.");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("Patient is due for an HPV vaccine boster today.".endsWith("_value"))
				{
					variable = "Patient is due for an HPV vaccine boster today.".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Patient is due for an HPV vaccine boster today.".endsWith("_date"))
				{
					variable = "Patient is due for an HPV vaccine boster today.".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("Patient is due for an HPV vaccine boster today.".endsWith("_object"))
				{
					variable = "Patient is due for an HPV vaccine boster today.".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("Patient is due for an HPV vaccine boster today.") != null){
						value = resultLookup.get("Patient is due for an HPV vaccine boster today.").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","Patient is due for an HPV vaccine boster today.");
				}
				varLen = "VACCINES".length();
				value=userVarMap.get("VACCINES");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("VACCINES".endsWith("_value"))
				{
					variable = "VACCINES".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("VACCINES".endsWith("_date"))
				{
					variable = "VACCINES".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("VACCINES".endsWith("_object"))
				{
					variable = "VACCINES".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("VACCINES") != null){
						value = resultLookup.get("VACCINES").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","VACCINES");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeNote");
				}
				context.eval(patient.getPatientId(), "storeNote",parameters);
						if((!Box1.isNull()&&Box1.toString().equalsIgnoreCase("true"))){
				varLen = "Vaccine_given".length();
				value=userVarMap.get("Vaccine_given");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("Vaccine_given".endsWith("_value"))
				{
					variable = "Vaccine_given".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Vaccine_given".endsWith("_date"))
				{
					variable = "Vaccine_given".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("Vaccine_given".endsWith("_object"))
				{
					variable = "Vaccine_given".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("Vaccine_given") != null){
						value = resultLookup.get("Vaccine_given").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","Vaccine_given");
				}
				varLen = "HPV".length();
				value=userVarMap.get("HPV");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("HPV".endsWith("_value"))
				{
					variable = "HPV".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("HPV".endsWith("_date"))
				{
					variable = "HPV".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("HPV".endsWith("_object"))
				{
					variable = "HPV".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("HPV") != null){
						value = resultLookup.get("HPV").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","HPV");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeObs");
				}
				context.eval(patient.getPatientId(), "storeObs",parameters);
								varLen = "I ordered the HPV vaccine today.".length();
				value=userVarMap.get("I ordered the HPV vaccine today.");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("I ordered the HPV vaccine today.".endsWith("_value"))
				{
					variable = "I ordered the HPV vaccine today.".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("I ordered the HPV vaccine today.".endsWith("_date"))
				{
					variable = "I ordered the HPV vaccine today.".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("I ordered the HPV vaccine today.".endsWith("_object"))
				{
					variable = "I ordered the HPV vaccine today.".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("I ordered the HPV vaccine today.") != null){
						value = resultLookup.get("I ordered the HPV vaccine today.").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","I ordered the HPV vaccine today.");
				}
				varLen = "VACCINES".length();
				value=userVarMap.get("VACCINES");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("VACCINES".endsWith("_value"))
				{
					variable = "VACCINES".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("VACCINES".endsWith("_date"))
				{
					variable = "VACCINES".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("VACCINES".endsWith("_object"))
				{
					variable = "VACCINES".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("VACCINES") != null){
						value = resultLookup.get("VACCINES").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","VACCINES");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeNote");
				}
				context.eval(patient.getPatientId(), "storeNote",parameters);
				}		if((!Box2.isNull()&&Box2.toString().equalsIgnoreCase("true"))){
				varLen = "Follow-up_Scheduled".length();
				value=userVarMap.get("Follow-up_Scheduled");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("Follow-up_Scheduled".endsWith("_value"))
				{
					variable = "Follow-up_Scheduled".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Follow-up_Scheduled".endsWith("_date"))
				{
					variable = "Follow-up_Scheduled".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("Follow-up_Scheduled".endsWith("_object"))
				{
					variable = "Follow-up_Scheduled".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("Follow-up_Scheduled") != null){
						value = resultLookup.get("Follow-up_Scheduled").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","Follow-up_Scheduled");
				}
				varLen = "HPV".length();
				value=userVarMap.get("HPV");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("HPV".endsWith("_value"))
				{
					variable = "HPV".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("HPV".endsWith("_date"))
				{
					variable = "HPV".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("HPV".endsWith("_object"))
				{
					variable = "HPV".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("HPV") != null){
						value = resultLookup.get("HPV").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","HPV");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeObs");
				}
				context.eval(patient.getPatientId(), "storeObs",parameters);
								varLen = "Will schedule for next dose.".length();
				value=userVarMap.get("Will schedule for next dose.");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("Will schedule for next dose.".endsWith("_value"))
				{
					variable = "Will schedule for next dose.".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Will schedule for next dose.".endsWith("_date"))
				{
					variable = "Will schedule for next dose.".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("Will schedule for next dose.".endsWith("_object"))
				{
					variable = "Will schedule for next dose.".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("Will schedule for next dose.") != null){
						value = resultLookup.get("Will schedule for next dose.").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","Will schedule for next dose.");
				}
				varLen = "VACCINES".length();
				value=userVarMap.get("VACCINES");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("VACCINES".endsWith("_value"))
				{
					variable = "VACCINES".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("VACCINES".endsWith("_date"))
				{
					variable = "VACCINES".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("VACCINES".endsWith("_object"))
				{
					variable = "VACCINES".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("VACCINES") != null){
						value = resultLookup.get("VACCINES").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","VACCINES");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeNote");
				}
				context.eval(patient.getPatientId(), "storeNote",parameters);
				}		if((!Box3.isNull()&&Box3.toString().equalsIgnoreCase("true"))){
				varLen = "Vaccine_deferred".length();
				value=userVarMap.get("Vaccine_deferred");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("Vaccine_deferred".endsWith("_value"))
				{
					variable = "Vaccine_deferred".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Vaccine_deferred".endsWith("_date"))
				{
					variable = "Vaccine_deferred".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("Vaccine_deferred".endsWith("_object"))
				{
					variable = "Vaccine_deferred".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("Vaccine_deferred") != null){
						value = resultLookup.get("Vaccine_deferred").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","Vaccine_deferred");
				}
				varLen = "HPV".length();
				value=userVarMap.get("HPV");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("HPV".endsWith("_value"))
				{
					variable = "HPV".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("HPV".endsWith("_date"))
				{
					variable = "HPV".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("HPV".endsWith("_object"))
				{
					variable = "HPV".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("HPV") != null){
						value = resultLookup.get("HPV").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","HPV");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeObs");
				}
				context.eval(patient.getPatientId(), "storeObs",parameters);
								varLen = "We deferred the HPV vaccine today.".length();
				value=userVarMap.get("We deferred the HPV vaccine today.");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("We deferred the HPV vaccine today.".endsWith("_value"))
				{
					variable = "We deferred the HPV vaccine today.".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("We deferred the HPV vaccine today.".endsWith("_date"))
				{
					variable = "We deferred the HPV vaccine today.".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("We deferred the HPV vaccine today.".endsWith("_object"))
				{
					variable = "We deferred the HPV vaccine today.".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("We deferred the HPV vaccine today.") != null){
						value = resultLookup.get("We deferred the HPV vaccine today.").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","We deferred the HPV vaccine today.");
				}
				varLen = "VACCINES".length();
				value=userVarMap.get("VACCINES");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("VACCINES".endsWith("_value"))
				{
					variable = "VACCINES".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("VACCINES".endsWith("_date"))
				{
					variable = "VACCINES".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("VACCINES".endsWith("_object"))
				{
					variable = "VACCINES".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("VACCINES") != null){
						value = resultLookup.get("VACCINES").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","VACCINES");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeNote");
				}
				context.eval(patient.getPatientId(), "storeNote",parameters);
				}		if((!Box4.isNull()&&Box4.toString().equalsIgnoreCase("true"))){
				varLen = "Vaccine_deferred_by".length();
				value=userVarMap.get("Vaccine_deferred_by");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("Vaccine_deferred_by".endsWith("_value"))
				{
					variable = "Vaccine_deferred_by".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Vaccine_deferred_by".endsWith("_date"))
				{
					variable = "Vaccine_deferred_by".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("Vaccine_deferred_by".endsWith("_object"))
				{
					variable = "Vaccine_deferred_by".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("Vaccine_deferred_by") != null){
						value = resultLookup.get("Vaccine_deferred_by").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","Vaccine_deferred_by");
				}
				varLen = "patient/family".length();
				value=userVarMap.get("patient/family");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("patient/family".endsWith("_value"))
				{
					variable = "patient/family".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("patient/family".endsWith("_date"))
				{
					variable = "patient/family".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("patient/family".endsWith("_object"))
				{
					variable = "patient/family".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("patient/family") != null){
						value = resultLookup.get("patient/family").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","patient/family");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeObs");
				}
				context.eval(patient.getPatientId(), "storeObs",parameters);
								varLen = "The patient/family chose to defer.".length();
				value=userVarMap.get("The patient/family chose to defer.");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("The patient/family chose to defer.".endsWith("_value"))
				{
					variable = "The patient/family chose to defer.".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("The patient/family chose to defer.".endsWith("_date"))
				{
					variable = "The patient/family chose to defer.".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("The patient/family chose to defer.".endsWith("_object"))
				{
					variable = "The patient/family chose to defer.".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("The patient/family chose to defer.") != null){
						value = resultLookup.get("The patient/family chose to defer.").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","The patient/family chose to defer.");
				}
				varLen = "VACCINES".length();
				value=userVarMap.get("VACCINES");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("VACCINES".endsWith("_value"))
				{
					variable = "VACCINES".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("VACCINES".endsWith("_date"))
				{
					variable = "VACCINES".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("VACCINES".endsWith("_object"))
				{
					variable = "VACCINES".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("VACCINES") != null){
						value = resultLookup.get("VACCINES").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","VACCINES");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeNote");
				}
				context.eval(patient.getPatientId(), "storeNote",parameters);
				}		if((!Box5.isNull()&&Box5.toString().equalsIgnoreCase("true"))){
				varLen = "Vaccine_deferred_by".length();
				value=userVarMap.get("Vaccine_deferred_by");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("Vaccine_deferred_by".endsWith("_value"))
				{
					variable = "Vaccine_deferred_by".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Vaccine_deferred_by".endsWith("_date"))
				{
					variable = "Vaccine_deferred_by".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("Vaccine_deferred_by".endsWith("_object"))
				{
					variable = "Vaccine_deferred_by".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("Vaccine_deferred_by") != null){
						value = resultLookup.get("Vaccine_deferred_by").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","Vaccine_deferred_by");
				}
				varLen = "physician".length();
				value=userVarMap.get("physician");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("physician".endsWith("_value"))
				{
					variable = "physician".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("physician".endsWith("_date"))
				{
					variable = "physician".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("physician".endsWith("_object"))
				{
					variable = "physician".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("physician") != null){
						value = resultLookup.get("physician").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","physician");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeObs");
				}
				context.eval(patient.getPatientId(), "storeObs",parameters);
								varLen = "I chose to defer.".length();
				value=userVarMap.get("I chose to defer.");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("I chose to defer.".endsWith("_value"))
				{
					variable = "I chose to defer.".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("I chose to defer.".endsWith("_date"))
				{
					variable = "I chose to defer.".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("I chose to defer.".endsWith("_object"))
				{
					variable = "I chose to defer.".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("I chose to defer.") != null){
						value = resultLookup.get("I chose to defer.").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","I chose to defer.");
				}
				varLen = "VACCINES".length();
				value=userVarMap.get("VACCINES");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("VACCINES".endsWith("_value"))
				{
					variable = "VACCINES".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("VACCINES".endsWith("_date"))
				{
					variable = "VACCINES".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("VACCINES".endsWith("_object"))
				{
					variable = "VACCINES".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("VACCINES") != null){
						value = resultLookup.get("VACCINES").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","VACCINES");
				}
				if (ruleProvider != null) {
					ruleProvider.getRule("storeNote");
				}
				context.eval(patient.getPatientId(), "storeNote",parameters);
				}}	return false;	}

	public ArrayList<String> initAction() {
		ArrayList<String> actions = new ArrayList<String>();
		actions.add("|| firstname || has begun the HPV vaccine series and must finish it to get full protection. Today || firstname ||is due for the || dose_Text || dose of HPV vaccine.");
		actions.add("HPV given today ---->");
		actions.add("|| followUpText ||");
		actions.add("Deferred --->");
		actions.add("by patient/parent");
		actions.add("");
		actions.add("by physician");


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