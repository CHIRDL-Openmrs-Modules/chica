package org.openmrs.module.chica.datasource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicExpression;
import org.openmrs.logic.LogicExpressionBinary;
import org.openmrs.logic.LogicTransform;
import org.openmrs.logic.db.LogicObsDAO;
import org.openmrs.logic.op.Operator;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hl7.mrfdump.HL7ObsHandler23;
import org.openmrs.module.dss.util.IOUtil;
import org.openmrs.module.dss.util.Util;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * 
 */
public class LogicChicaObsDAO implements LogicObsDAO
{
	protected final Log log = LogFactory.getLog(getClass());

	//I don't want duplicate obs stored if the same message is processed more 
	//than once so Obs are stored in a Set instead of a list
	private HashMap<Integer, HashMap<String, Set<Obs>>> regenObs = null; 

	public LogicChicaObsDAO()
	{
		this.regenObs = new HashMap<Integer, HashMap<String, Set<Obs>>>();
	}

	public List<Obs> getObservations(Cohort who, LogicCriteria logicCriteria)
	{
		List<Obs> results = new ArrayList<Obs>();
		HashMap<String, Set<Obs>> obsByConceptName = null;

		// look up the obs for each patient in the set
		for (Integer patientId : who.getMemberIds())
		{
			obsByConceptName = this.regenObs.get(patientId);
			if (obsByConceptName != null)
			{
				List<Obs> patientResults = evaluateLogicCriteria(obsByConceptName,
						logicCriteria.getExpression());
				if(patientResults != null)
				{
					results.addAll(patientResults);
				}
			}
		}

		return results;
	}

	private List<Obs> evaluateLogicCriteria(
			HashMap<String, Set<Obs>> obsByConceptName,
			LogicExpression expression)
	{
		Date indexDate = Calendar.getInstance().getTime();
		Operator transformOperator = null;
		LogicTransform transform = expression.getTransform();
		Integer numResults = null;
		
		if(transform!= null){
			transformOperator = transform.getTransformOperator();
			numResults = transform.getNumResults();
		}
		
		if(numResults == null){
			numResults = 1;
		}
		List<Obs> resultObs = this.getCriterion(obsByConceptName, expression,
				indexDate);

		// Apply First/Last Transform to results
		if (transformOperator == Operator.LAST)
		{
			if(resultObs.size()>0)
			{
				Collections.sort(resultObs, Collections
						.reverseOrder(new ObsComparator()));
			}
		} else if (transformOperator == Operator.FIRST)
		{
			Collections.sort(resultObs, new ObsComparator());
		}else if (transformOperator == Operator.DISTINCT) {
			Set<Obs> distinctElements = new HashSet<Obs>(resultObs);
			resultObs = new ArrayList<Obs>(distinctElements);			
		} 
		
		//make the default sort order in reverse
		//this is specifically needed for the dx and complaints rule
		if(resultObs != null)
		{
			Collections.sort(resultObs, Collections
					.reverseOrder(new ObsComparator()));
		}else
		{
			resultObs = new ArrayList<Obs>();
		}
		
		//return a single result per patient for these operators
		//I don't see an easy way to do this in hibernate so I am
		//doing some postprocessing
		if(transformOperator == Operator.FIRST || transformOperator == Operator.LAST){
			HashMap<Integer,ArrayList<Obs>> nResultMap = new HashMap<Integer,ArrayList<Obs>>();
			
			for(Obs currResult:resultObs){
				Integer currPersonId = currResult.getPersonId();
				ArrayList<Obs> prevResults = nResultMap.get(currPersonId);
				if(prevResults == null){
					prevResults = new ArrayList<Obs>();
					nResultMap.put(currPersonId, prevResults);
				}
				
				if(prevResults.size()<numResults){
					prevResults.add(currResult);
				}
			}
			
			if(nResultMap.values().size()>0){
				resultObs.clear();
				
				for(ArrayList<Obs> currPatientObs:nResultMap.values()){
					resultObs.addAll(currPatientObs);
				}
			}
		}
		return resultObs;
	}

	private ArrayList<Obs> getCriterion(
			HashMap<String, Set<Obs>> obsByConceptName,
			LogicExpression expression, Date indexDate)
	{

		Operator operator = expression.getOperator();
		Object leftOperand = null;
		if(expression instanceof LogicExpressionBinary){
			leftOperand = ((LogicExpressionBinary) expression).
					getLeftOperand();
		}
		Object rightOperand = expression.getRightOperand();
		ArrayList<Obs> results = new ArrayList<Obs>();
		ArrayList<Obs> leftOperandResults = new ArrayList<Obs>();

		String rootToken = expression.getRootToken();
	
		if(rootToken != null){

			Set<Obs> conceptObs = obsByConceptName.get(rootToken);
			if(conceptObs != null){
				leftOperandResults.addAll(conceptObs);
				results.addAll(conceptObs);
			}
		}
		
		if (operator == Operator.BEFORE)
		{
			results = compare("obsDatetime", rightOperand, leftOperandResults, "LT");

		} else if (operator == Operator.AFTER)
		{
			results = compare("obsDatetime", rightOperand, leftOperandResults, "GT");
		} else if (operator == Operator.AND || operator == Operator.OR)
		{
			ArrayList<Obs> leftResults = null;
			ArrayList<Obs> rightResults = null;

			if (leftOperand instanceof LogicExpression)
			{
				leftResults = this.getCriterion(obsByConceptName,
						(LogicExpression) leftOperand, indexDate);
			}
			if (rightOperand instanceof LogicExpression)
			{
				rightResults = this.getCriterion(obsByConceptName,
						(LogicExpression) rightOperand, indexDate);
			}

			if (leftResults != null && rightResults != null)
			{
				if (operator == Operator.AND)
				{
					results = and(leftResults, rightResults);
				}
				if (operator == Operator.OR)
				{
					results = or(leftResults, rightResults);
				}
			}

		} else if (operator == Operator.NOT)
		{
			// ignore this one for now
		} else if (operator == Operator.CONTAINS) {
			if (rightOperand instanceof Concept) {
				results = compare("valueCoded", rightOperand, leftOperandResults, "EQ");
			} else if (rightOperand instanceof String) {
				Concept concept = new Concept();
				ConceptName conceptName = new ConceptName();
				conceptName.setName((String) rightOperand);
				conceptName.setLocale(new Locale("en_US"));
				concept.addName(conceptName);
				results = compare("valueCoded", concept, leftOperandResults, "EQ");
			} else
				this.log.error("Invalid operand value for CONTAINS operation");
		} else if (operator == Operator.EQUALS)
		{
			if (rightOperand instanceof Float
					|| rightOperand instanceof Integer
					|| rightOperand instanceof Double){
				results = compare("valueNumeric", rightOperand, leftOperandResults, "EQ");
			}
			else if (rightOperand instanceof String){
				results = compare("valueText", rightOperand, leftOperandResults, "EQ");
			}
			else if (rightOperand instanceof Date){
				results = compare("valueDatetime", rightOperand, leftOperandResults, "EQ");
			}
			else if (rightOperand instanceof Concept){
				results = compare("valueCoded", rightOperand, leftOperandResults, "EQ");
			}
			else
				this.log.error("Invalid operand value for EQUALS operation");

		} else if (operator == Operator.LTE)
		{
			if (rightOperand instanceof Float
					|| rightOperand instanceof Integer
					|| rightOperand instanceof Double){
				results = compare("valueNumeric", rightOperand, leftOperandResults,
						"LTE");
			}
			else if (rightOperand instanceof Date){
				results = compare("valueDatetime", rightOperand, leftOperandResults,
						"LTE");
			}
			else
				this.log
						.error("Invalid operand value for LESS THAN EQUAL operation");

		} else if (operator == Operator.GTE)
		{
			if (rightOperand instanceof Float
					|| rightOperand instanceof Integer
					|| rightOperand instanceof Double){
				results = compare("valueNumeric", rightOperand, leftOperandResults,
						"GTE");
			}
			else if (rightOperand instanceof Date){
				results = compare("valueDatetime", rightOperand, leftOperandResults,
						"GTE");
			}
			else
				this.log
						.error("Invalid operand value for GREATER THAN EQUAL operation");
		} else  if (operator == Operator.LT)
		{
			if (rightOperand instanceof Float
					|| rightOperand instanceof Integer
					|| rightOperand instanceof Double){
				results = compare("valueNumeric", rightOperand, leftOperandResults,
						"LT");
			}
			else if (rightOperand instanceof Date){
				results = compare("valueDatetime", rightOperand, leftOperandResults,
						"LT");
			}
			else
				this.log.error("Invalid operand value for LESS THAN operation");

		}else if (operator == Operator.GT)
		{
			if (rightOperand instanceof Float
					|| rightOperand instanceof Integer
					|| rightOperand instanceof Double){
				results = compare("valueNumeric", rightOperand, leftOperandResults,
						"GT");
			}
			else if (rightOperand instanceof Date){
				results = compare("valueDatetime", rightOperand, leftOperandResults,
						"GT");
			}
			else
				this.log.error("Invalid operand value for GREATER THAN operation");

		} else if (operator == Operator.EXISTS)
		{
			// EXISTS can be handled on the higher level (above
			// LogicService, even) by coercing the Result into a Boolean for
			// each patient
		} else if (operator == Operator.ASOF
		        && rightOperand instanceof Date) {
			indexDate = (Date) rightOperand;
			results = compare("obsDatetime", indexDate, leftOperandResults, "LT");

		} else if (operator == Operator.WITHIN
		        && rightOperand instanceof Duration) {
			
			Duration duration = (Duration) rightOperand;
			Calendar within = Calendar.getInstance();
			within.setTime(indexDate);

			if (duration.getUnits() == Duration.Units.YEARS) {
				within.add(Calendar.YEAR, duration.getDuration().intValue());
			} else if (duration.getUnits() == Duration.Units.MONTHS) {
				within.add(Calendar.MONTH, duration.getDuration().intValue());
			} else if (duration.getUnits() == Duration.Units.WEEKS) {
				within.add(Calendar.WEEK_OF_YEAR, duration.getDuration()
				                                            .intValue());
			} else if (duration.getUnits() == Duration.Units.DAYS) {
				within.add(Calendar.DAY_OF_YEAR, duration.getDuration()
				                                           .intValue());
			} else if (duration.getUnits() == Duration.Units.MINUTES) {
				within.add(Calendar.MINUTE, duration.getDuration().intValue());
			} else if (duration.getUnits() == Duration.Units.SECONDS) {
				within.add(Calendar.SECOND, duration.getDuration().intValue());
			}

			if(indexDate.compareTo(within.getTime())>0){
				results = compareBetween("obsDatetime",within.getTime(),indexDate, leftOperandResults);
			}else{
				results = compareBetween("obsDatetime",indexDate, within.getTime(),leftOperandResults);
			}
		}
		
		return results;
	}
	
	private ArrayList<Obs> compareBetweenObsDateTime(Date firstDate,
			Date lastDate, ArrayList<Obs> prevResults)
	{
		ArrayList<Obs> results = new ArrayList<Obs>();

		for (Obs currObs : prevResults)
		{
			Date currObsDate = currObs.getObsDatetime();

			if (currObsDate != null
					&& currObsDate.compareTo(firstDate) >= 0&&
					currObsDate.compareTo(lastDate) <= 0)
			{
				results.add(currObs);
			}
		}

		return results;
	}

	private ArrayList<Obs> compareBetween(String component, Object comparisonOperand1,
			Object comparisonOperand2,
			ArrayList<Obs> prevResults)
	{
		if (component.equalsIgnoreCase("obsDatetime")
				&& comparisonOperand1 instanceof Date&&comparisonOperand2 instanceof Date)
		{
			return compareBetweenObsDateTime((Date) comparisonOperand1,(Date) comparisonOperand2,
					prevResults);
		}
		
		return prevResults;
	}
	
	private ArrayList<Obs> compare(String component, Object comparisonOperand,
			ArrayList<Obs> prevResults, String comparator)
	{
		if (component.equalsIgnoreCase("obsDatetime")
				&& comparisonOperand instanceof Date)
		{
			return compareObsDateTime((Date) comparisonOperand, prevResults,
					comparator);
		}
		if (component.equalsIgnoreCase("valueNumeric"))

		{
			if (comparisonOperand instanceof Double)
			{
				return compareValueNumeric((Double) comparisonOperand,
						prevResults, comparator);
			}
			if (comparisonOperand instanceof Integer)
			{
				return compareValueNumeric((Integer) comparisonOperand,
						prevResults, comparator);
			}

			if (comparisonOperand instanceof Float)
			{
				return compareValueNumeric((Float) comparisonOperand,
						prevResults, comparator);
			}
		}
		if (component.equalsIgnoreCase("valueDatetime")
				&& comparisonOperand instanceof Date)
		{
			return compareValueDate((Date) comparisonOperand, prevResults,
					comparator);
		}

		if (component.equalsIgnoreCase("valueCoded")
				&& comparisonOperand instanceof Concept)
		{
			return compareValueConcept((Concept) comparisonOperand,
					prevResults, comparator);
		}
		
		if (component.equalsIgnoreCase("valueText")
				&& comparisonOperand instanceof String)
		{
			return compareValueText((String) comparisonOperand,
					prevResults, comparator);
		}
		return prevResults;
	}

	private ArrayList<Obs> compareValueConcept(Concept comparisonOperand,
			ArrayList<Obs> prevResults, String comparator)
	{
		ArrayList<Obs> results = new ArrayList<Obs>();

		for (Obs currObs : prevResults)
		{
			if (comparator.equalsIgnoreCase("EQ"))
			{
				Concept codedAnswer = currObs.getValueCoded();
				
				if (codedAnswer != null && codedAnswer.getName().getName().equals(
						comparisonOperand.getName().getName()))
				{
					results.add(currObs);
				}
			}
		}

		return results;
	}

	private ArrayList<Obs> compareObsDateTime(Date comparisonOperand,
			ArrayList<Obs> prevResults, String comparator)
	{
		ArrayList<Obs> results = new ArrayList<Obs>();

		for (Obs currObs : prevResults)
		{
			Date currObsDate = currObs.getObsDatetime();

			if (comparator.equalsIgnoreCase("LT"))
			{
				if (currObsDate!=null&&currObsDate.compareTo(comparisonOperand) < 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("LTE"))
			{
				if (currObsDate!=null&&currObsDate.compareTo(comparisonOperand) <= 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("GT"))
			{
				if (currObsDate!=null&&currObsDate.compareTo(comparisonOperand) > 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("GTE"))
			{
				if (currObsDate!=null&&currObsDate.compareTo(comparisonOperand) >= 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("EQ"))
			{
				if (currObsDate!=null&&currObsDate.compareTo(comparisonOperand) == 0)
				{
					results.add(currObs);
				}
			}
		}

		return results;
	}

	private ArrayList<Obs> compareValueNumeric(Integer comparisonOperand,
			ArrayList<Obs> prevResults, String comparator)
	{
		return compareValueNumeric(comparisonOperand.doubleValue(),
				prevResults, comparator);
	}

	private ArrayList<Obs> compareValueNumeric(Float comparisonOperand,
			ArrayList<Obs> prevResults, String comparator)
	{
		return compareValueNumeric(comparisonOperand.doubleValue(),
				prevResults, comparator);
	}

	private ArrayList<Obs> compareValueNumeric(Double comparisonOperand,
			ArrayList<Obs> prevResults, String comparator)
	{
		ArrayList<Obs> results = new ArrayList<Obs>();

		for (Obs currObs : prevResults)
		{
			Double currObsNumeric = currObs.getValueNumeric();

			if (comparator.equalsIgnoreCase("LT"))
			{
				if (currObsNumeric!=null&&currObsNumeric.compareTo(comparisonOperand) < 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("LTE"))
			{
				if (currObsNumeric!=null&&currObsNumeric.compareTo(comparisonOperand) <= 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("GT"))
			{
				if (currObsNumeric!=null&&currObsNumeric.compareTo(comparisonOperand) > 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("GTE"))
			{
				if (currObsNumeric!=null&&currObsNumeric.compareTo(comparisonOperand) >= 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("EQ"))
			{
				if (currObsNumeric!=null&&currObsNumeric.compareTo(comparisonOperand) == 0)
				{
					results.add(currObs);
				}
			}
		}

		return results;
	}
	
	private ArrayList<Obs> compareValueText(String comparisonOperand,
			ArrayList<Obs> prevResults, String comparator)
	{
		ArrayList<Obs> results = new ArrayList<Obs>();

		for (Obs currObs : prevResults)
		{
			if (comparator.equalsIgnoreCase("EQ"))
			{
				String currObsText = currObs.getValueText();
				if (currObsText!=null&&currObsText.compareTo(comparisonOperand) == 0)
				{
					results.add(currObs);
				}
			}
		}

		return results;
	}

	private ArrayList<Obs> compareValueDate(Date comparisonOperand,
			ArrayList<Obs> prevResults, String comparator)
	{
		ArrayList<Obs> results = new ArrayList<Obs>();

		for (Obs currObs : prevResults)
		{
			Date currValueDatetime = currObs.getValueDatetime();

			if (comparator.equalsIgnoreCase("LT"))
			{
				if (currValueDatetime!=null&&currValueDatetime.compareTo(comparisonOperand) < 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("LTE"))
			{
				if (currValueDatetime!=null&&currValueDatetime.compareTo(comparisonOperand) <= 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("GT"))
			{
				if (currValueDatetime!=null&&currValueDatetime.compareTo(comparisonOperand) > 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("GTE"))
			{
				if (currValueDatetime!=null&&currValueDatetime.compareTo(comparisonOperand) >= 0)
				{
					results.add(currObs);
				}
			}
			if (comparator.equalsIgnoreCase("EQ"))
			{
				if (currValueDatetime!=null&&currValueDatetime.compareTo(comparisonOperand) == 0)
				{
					results.add(currObs);
				}
			}
		}

		return results;
	}

	private ArrayList<Obs> and(ArrayList<Obs> leftResults,
			ArrayList<Obs> rightResults)
	{
		ArrayList<Obs> results = new ArrayList<Obs>();
		results.addAll(leftResults);
		results.retainAll(rightResults);

		return results;
	}

	private ArrayList<Obs> or(ArrayList<Obs> leftResults,
			ArrayList<Obs> rightResults)
	{
		ArrayList<Obs> results = new ArrayList<Obs>();
		results.addAll(leftResults);
		results.addAll(rightResults);

		return results;
	}

	public Set<Obs> getRegenObsByConceptName(Integer patientId,
			String conceptName)
	{
		if (patientId == null)
		{
			return new HashSet<Obs>();
		}

		HashMap<String, Set<Obs>> regenObsById = this.regenObs
				.get(patientId);

		if (regenObsById == null)
		{
			// TODO call query kite
			return new HashSet<Obs>();
		}

		return regenObsById.get(conceptName);
	}

	public void deleteRegenObsByPatientId(Integer patientId)
	{
		this.regenObs.remove(patientId);
	}

	public void parseHL7ToObs(String hl7Message, Integer patientId, String mrn)
	{
		ATDService atdService = Context.getService(ATDService.class);
		try
		{
			BufferedReader reader = new BufferedReader(new StringReader(
					hl7Message));
			String line = null;

			// skip lines before hl7 message begins
			while ((line = reader.readLine()) != null
					&& !line.startsWith("MSH"))
			{
				if(line.contains("FAILED")){
					ATDError error = new ATDError("Error", "Query Kite Connection"
							, "MRF query returned FAILED for mrn: "+mrn
							, null, new Date(), null);
					atdService.saveError(error);
					return;
				}
			}

			StringWriter output = new StringWriter();
			PrintWriter writer = new PrintWriter(output);
			if(line != null){
				writer.println(line); // write out the MSH line
			}

			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("MSH"))
				{
					writer.flush();
					writer.close();
					try
					{
						processMessage(output.toString(), patientId);
					} catch (Exception e)
					{
						//error is logged in processMessage
						//catch this error so other MSH's are processed
					}

					// process the next message
					output = new StringWriter();
					writer = new PrintWriter(output);
				}
				writer.println(line);
			}

			writer.flush();
			writer.close();
			try
			{
				processMessage(output.toString(), patientId);
			} catch (Exception e)
			{
				//error is logged in processMessage
				//catch this error so other MSH's are processed
			}

		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
		}
	}
	
	public String renameDxAndComplaints(String message)
	{
		message = message.replaceAll("DX & COMPLAINTS", "DX and COMPLAINTS");
		return message;
	}
	
	public String replaceVersion(String message)
	{
		StringBuffer newMessage = new StringBuffer();
		BufferedReader reader = new BufferedReader(new StringReader(message));
		try {
			String firstLine = reader.readLine();
			
			if(firstLine == null)
			{
				return message;
			}
			
			String[] fields = PipeParser.split(firstLine, "|");
			if (fields != null)
			{
				int length = fields.length;
							
				for(int i = 0; i < length; i++)
				{
					if(fields[i]==null)
					{
						fields[i]="";
					}
					if(i>0)
					{
						newMessage.append("|");
					}
					if(i==11)
					{
						newMessage.append("2.3");
					}else
					{
						newMessage.append(fields[i]);
					}
				}
			}
			String line = null;
			
			while((line = reader.readLine())!=null)
			{
				newMessage.append("\r\n");
				newMessage.append(line);
			}
		} catch (IOException e) {
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
		}
		
		
		return newMessage.toString();
	}

	private void processMessage(String messageString, Integer patientId)
	{			
		ATDService atdService = Context.getService(ATDService.class);
		AdministrationService adminService = Context.getAdministrationService();
		
		if(messageString != null){
			messageString = messageString.trim();
		}
		
		if (messageString == null||messageString.length()==0)
		{
			return;
		}
		String newMessageString = messageString;
		PipeParser pipeParser = new PipeParser();
		pipeParser.setValidationContext(new NoValidation());
		newMessageString = replaceVersion(newMessageString);
		newMessageString = renameDxAndComplaints(newMessageString);
		Message message = null;
		try
		{
			message = pipeParser.parse(newMessageString);
		} catch (Exception e)
		{
			ATDError error = new ATDError("Error", "Hl7 Parsing",
					"Error parsing the MRF dump " + e.getMessage(),
					messageString,
					new Date(), null);
			atdService.saveError(error);
			String mrfParseErrorDirectory = IOUtil
					.formatDirectoryName(adminService
							.getGlobalProperty("chica.mrfParseErrorDirectory"));
			if (mrfParseErrorDirectory != null)
			{
				String filename = "r" + Util.archiveStamp() + ".hl7";

				FileOutputStream outputFile = null;

				try
				{
					outputFile = new FileOutputStream(mrfParseErrorDirectory
							+ "/" + filename);
				} catch (FileNotFoundException e1)
				{
					this.log.error("Could not find file: "
							+ mrfParseErrorDirectory + "/" + filename);
				}
				if (outputFile != null)
				{
					try
					{
						ByteArrayInputStream input = new ByteArrayInputStream(
								newMessageString.getBytes());
						IOUtil.bufferedReadWrite(input, outputFile);
						outputFile.flush();
						outputFile.close();
					} catch (Exception e1)
					{
						try
						{
							outputFile.flush();
							outputFile.close();
						} catch (Exception e2)
						{
						}
						this.log
								.error("There was an error writing the dump file");
						this.log.error(e1.getMessage());
						this.log.error(Util.getStackTrace(e));
					}
				}
			}
			return;
		}
		HL7ObsHandler23 obsHandler = new HL7ObsHandler23();
		try
		{
			ArrayList<Obs> allObs = obsHandler.getObs(message);

			HashMap<String, Set<Obs>> obsByConcept = this.regenObs
					.get(patientId);

			if (obsByConcept == null)
			{
				obsByConcept = new HashMap<String, Set<Obs>>();
				this.regenObs.put(patientId, obsByConcept);
			}

			for (Obs currObs : allObs)
			{
				String currConceptName = currObs.getConcept().getName()
						.getName();
				Set<Obs> obs = obsByConcept.get(currConceptName);
				if (obs == null)
				{
					obs = new HashSet<Obs>();
					obsByConcept.put(currConceptName, obs);
				}
				obs.add(currObs);
			}
		} catch (Exception e)
		{
			this.log.error("Error processing MRF dump obs.");
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
		}
	}
}
