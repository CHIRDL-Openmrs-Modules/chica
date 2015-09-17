package org.openmrs.module.chica.rule;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

/**
 * DWE CLINREQ-90
 */
public class PhysicianNoteTextNote implements Rule
{
	public static final String HISTORY_PHYSICAL_NOTE = "History and Physical Note";
	public static final String ASSESSMENT_PLAN_NOTE = "Assessment and Plan Note";
	
	public static final String[] TEXT_NOTES_CONCEPTS = {HISTORY_PHYSICAL_NOTE,ASSESSMENT_PLAN_NOTE};
	
	/*
	 * Enumeration of characters that must be replaced before sending them in HL7 or XML
	 * If we need to add more message formats, they can be added here as well
	 */
	public enum SpecialCharacters{
		TILDE("~","\\R\\","~"),
		AMPERSAND("&", "\\T\\", "&amp;"),
		PIPE("|", "\\F\\", "|"),
		GREATER_THAN(">", ">", "&gt;"),
		LESS_THAN("<", "<", "&lt;"),
		APOSTROPHE("'", "'", "&apos;"),
		QUOTE("\"", "\"", "&quot;"),
		ESCAPE("\\","\\E\\","\\"),
		CARET("^","\\S\\","^"),
		LINE_FEED("\n", "\\X0A\\", "\n"),
		CARRIAGE_RETURN("\r", "\\X0D\\", "\r"),
		;
		
		private String valueToReplace;
		private String hl7Value;
		private String xmlValue;
		
		/**
		 * @param valueToReplace - string to replace
		 * @param hl7Value - string to use in HL7 messages
		 * @param xmlValue - string to use in XML
		 */
		private SpecialCharacters(String valueToReplace, String hl7Value, String xmlValue)
		{
			this.valueToReplace = valueToReplace;
			this.hl7Value = hl7Value;
			this.xmlValue = xmlValue;
		}
		
		public String getValueToReplace()
		{
			return valueToReplace;
		}
		
		public String getHL7Value()
		{
			return hl7Value;
		}
		
		public String getXMLValue()
		{
			return xmlValue;
		}
	}

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		long startTime = System.currentTimeMillis();
		String examNote = buildTextNote(patientId, parameters);
		if (examNote.trim().length() > 0) {
			System.out.println("chicaNoteTextNote: " + (System.currentTimeMillis() - startTime) + "ms");
			return new Result(examNote);
		}

		System.out.println("chicaNoteTextNote: " + (System.currentTimeMillis() - startTime) + "ms");
		return Result.emptyResult();
	}

	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[]{};
	}

	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * Builds the text note portion of the physician note.
	 * 
	 * @param patientId The ID of the patient used to lookup text note observations for the current day.
	 * @param parameters
	 * @return String containing the text note portion of the physician note.  This will not return null.
	 */
	private static String buildTextNote(Integer patientId, Map<String, Object> parameters) {
		StringBuffer noteBuffer = new StringBuffer();
		Patient patient = Context.getPatientService().getPatient(patientId);
		LogicContext context = new LogicContextImpl(patientId);
		LogicDataSource obsDataSource = context.getLogicDataSource("obs");

		Encounter encounter = getLastEncounter(patient);
		if (encounter == null) {
			return noteBuffer.toString();
		}

		Integer encounterId = encounter.getEncounterId();
		
		for(String conceptName : TEXT_NOTES_CONCEPTS)
		{
			Result textNote = context.read(patientId, obsDataSource, 
					new LogicCriteriaImpl(conceptName).within(Duration.days(-3)).last());
			if (textNote != null && !textNote.isEmpty() && equalEncounters(encounterId, textNote)) {
				noteBuffer.append(conceptName.toUpperCase());
				noteBuffer.append(":\n");
				noteBuffer.append(replaceSpecialCharacters(textNote.toString(), parameters));
				noteBuffer.append("\n");
			}
		}
		
		return noteBuffer.toString();
	}
	
	/**
	 * Replace special characters such as &, ~, or ^ based on how they should be encoded for HL7 or XML messages
	 * 
	 * @param originalString
	 * @param parameters
	 * @return
	 */
	public static String replaceSpecialCharacters(String originalString, Map<String, Object> parameters)
	{
		String newTextNote = originalString;
		try
		{
			String messageFormat = parameters.get("messageFormat") != null ? parameters.get("messageFormat").toString() : "";
			
				switch(messageFormat)
				{
					case ChirdlUtilConstants.MESSAGE_XML:
						// Loop over the special characters list in the enumeration 
						// and replace them with the XML value
						for(SpecialCharacters character : SpecialCharacters.values())
						{
							newTextNote = newTextNote.replace(character.getValueToReplace(), character.getXMLValue());
						}
						break;
					case ChirdlUtilConstants.MESSAGE_HL7:
						// Loop over the special characters list in the enumeration 
						// and replace them with the HL7 value
						for(SpecialCharacters character : SpecialCharacters.values())
						{
							newTextNote = newTextNote.replace(character.getValueToReplace(), character.getHL7Value());
						}
						break;
					case ChirdlUtilConstants.MESSAGE_CDATA: 
						// There currently isn't anything that needs to be replaced since the string is in the CDATA section
						// The one exception (]]>) is already handled before the user saves the note 
						break;
					default: 
							break;
				}
		}
		catch(Exception e)
		{
			return originalString;
		}
		
		return newTextNote;
	}

	private static Encounter getLastEncounter(Patient patient) {
		// Get last encounter with last day
		Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.get(GregorianCalendar.DAY_OF_MONTH) - 3);
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, null, 
				null, null, false);
		if (encounters == null || encounters.size() == 0) {
			return null;
		} else if (encounters.size() == 1) {
			return encounters.get(0);
		}

		// Do a check to find the latest encounters with observations with a scanned timestamp for the PSF.
		ATDService atdService = Context.getService(ATDService.class);
		for (int i = encounters.size() - 1; i >= 0; i--) {
			Encounter encounter = encounters.get(i);
			List<Statistics> stats = atdService.getStatsByEncounterForm(encounter.getEncounterId(), "PSF");
			if (stats == null || stats.size() == 0) {
				continue;
			}

			for (Statistics stat : stats) {
				if (stat.getScannedTimestamp() != null) {
					return encounter;
				}
			}
		}

		return null;
	}

	private static boolean equalEncounters(Integer encounterId, Result result) {
		if (encounterId == null || result == null) {
			return false;
		}

		Result latestResult = result.latest();
		if (latestResult == null) {
			return false;
		}

		if (latestResult.getResultObject() == null || !(latestResult.getResultObject() instanceof Obs)) {
			return false;
		}

		Obs obs = (Obs)latestResult.getResultObject();
		if (obs == null) {
			return false;
		}

		Encounter obsEncounter = obs.getEncounter();
		if (obsEncounter == null) {
			return false;
		}

		if (encounterId == obsEncounter.getEncounterId()) {
			return true;
		}

		return false;
	}

}
