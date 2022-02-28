/**
 * 
 */
package org.openmrs.module.chica.hl7.mrfdump;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hibernate.Hibernate;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hl7.mckesson.PatientHandler;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7ObsHandler;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.HL7SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v23.datatype.CE;
import ca.uhn.hl7v2.model.v23.datatype.NM;
import ca.uhn.hl7v2.model.v23.datatype.ST;
import ca.uhn.hl7v2.model.v23.datatype.TS;
import ca.uhn.hl7v2.model.v23.datatype.TX;
import ca.uhn.hl7v2.model.v23.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v23.message.ADT_A01;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.OBX;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * @author tmdugan
 * 
 */
public class HL7ObsHandler23 implements HL7ObsHandler
{

	private static final Logger log = LoggerFactory.getLogger(HL7ObsHandler23.class);

	public static MSH getMSH(ORU_R01 oru)
	{
		return oru.getMSH();
	}

	public static PID getPID(ORU_R01 oru)
	{
		return oru.getRESPONSE().getPATIENT().getPID();
	}

	private OBX getOBX(Message message, int orderRe, int obRep)
	{
		if (message instanceof ORU_R01)
		{
			return getOBX((ORU_R01) message, orderRe, obRep);
		}
		return null;
	}

	private OBX getOBX(ORU_R01 oru, int orderRep, int obRep)
	{

		OBX obx = null;
		try
		{
			obx = oru.getRESPONSE().getORDER_OBSERVATION(orderRep)
					.getOBSERVATION(obRep).getOBX();
		} catch (Exception e)
		{
			log.error("Error getting OBX segment from HL7 for orderRep: {}  obRep: {}.", orderRep, obRep, e);
		}

		return obx;
	}

	public static OBR getOBR(ORU_R01 oru, int orderRep)
	{

		OBR obr = null;
		try
		{
			obr = oru.getRESPONSE().getORDER_OBSERVATION(orderRep).getOBR();
		} catch (Exception e)
		{
			log.error("Error getting OBX segment from HL7 for orderRep: {}.", orderRep, e);
		}

		return obr;
	}

	public static Date TranslateDate(TS ts)
	{
		int day = 0;
		int month = 0;
		int year = 0;
		int hour = 12;
		int minute = 0;
		int second = 0;

		String dateString = ts.getTimeOfAnEvent().getValue();

		if (dateString == null)
		{
			return null;
		}

		char[] dateChars = dateString.toCharArray();
		int length = dateChars.length;

		String yearString = "";
		String monthString = "";
		String dayString = "";
		String hourString = "";
		String minuteString = "";
		String secondString = "";

		for (int i = 0; i < length; i++)
		{
			if (i <= 3)
			{
				yearString += dateChars[i];
			}
			if (i >= 4 && i <= 5)
			{
				monthString += dateChars[i];
			}
			if (i >= 6 && i <= 7)
			{
				dayString += dateChars[i];
			}
			if (i >= 8 && i <= 9)
			{
				hourString += dateChars[i];
			}
			if (i >= 10 && i <= 11)
			{
				minuteString += dateChars[i];
			}
			if (i >= 12 && i <= 13)
			{
				secondString += dateChars[i];
			}
		}

		if (yearString.length() > 0)
		{
			try
			{
				year = Integer.parseInt(yearString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (monthString.length() > 0)
		{
			try
			{
				month = Integer.parseInt(monthString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (dayString.length() > 0)
		{
			try
			{
				day = Integer.parseInt(dayString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (hourString.length() > 0)
		{
			try
			{
				hour = Integer.parseInt(hourString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (minuteString.length() > 0)
		{
			try
			{
				minute = Integer.parseInt(minuteString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (secondString.length() > 0)
		{
			try
			{
				second = Integer.parseInt(secondString);
			} catch (NumberFormatException e)
			{
			}
		}

		Calendar cal = Calendar.getInstance();
		if(year == 0){
			return null;
		}
		cal.set(year, month - 1, day, hour, minute, second);

		return cal.getTime();

	}

	public String getSendingFacility(Message message)
	{
		if (!(message instanceof ORU_R01))
		{
			return null;
		}

		MSH msh = getMSH((ORU_R01) message);
		return msh.getSendingFacility().getNamespaceID().getValue();
	}

	public Date getDateStarted(Message message)
	{
		if (!(message instanceof ORU_R01))
		{
			return null;
		}
		// OBR segment --Observation start time
		OBR obr = getOBR((ORU_R01) message, 0);
		if(obr != null){
		    TS tsObsvStartDateTime = obr.getObservationDateTime();
	        if (tsObsvStartDateTime.getTimeOfAnEvent().getValue() == null)
	        {
	            MSH msh = getMSH((ORU_R01) message);
	            if(msh != null){
	                tsObsvStartDateTime = msh.getDateTimeOfMessage();      
	            }      
	        }
	        return TranslateDate(tsObsvStartDateTime);
		}
		
		return null;
	}

	public Date getDateStopped(Message message)
	{
		if (!(message instanceof ORU_R01))
		{
			return null;
		}

		// OBR Segment Observation stop time - usually not present
		Date edt = null;
		OBR obr = getOBR((ORU_R01) message, 0);
		if(obr != null){
		    TS tsObsvEndDateTime = obr.getObservationEndDateTime();
	        if (tsObsvEndDateTime.getTimeOfAnEvent().getValue() != null)
	        {
	            edt = TranslateDate(tsObsvEndDateTime);
	        }
		}
		
		return edt;
	}

	public String getObsValueType(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        return obx.getValueType().toString();
	    }
		return null;
	}

	public Date getObsDateTime(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        TS tsObsDateTime = obx.getDateTimeOfTheObservation();
	        return TranslateDate(tsObsDateTime);
	    }	
		return null;
	}

	public String getConceptId(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        CE ceObsIdentifier = obx.getObservationIdentifier();
	        return ceObsIdentifier.getIdentifier().toString();
	    }
		return null;
	}

	public String getConceptName(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        CE ceObsIdentifier = obx.getObservationIdentifier();
	        return ceObsIdentifier.getText().toString();
	    }
		return null;
	}

	public String getTextResult(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        Varies[] values = obx.getObservationValue();
	        Varies value = null;
	        String dataString = null;

	        if (values.length > 0)
	        {
	            value = values[0];

	            if (value.getData() instanceof TX)
	            {
	                TX data = (TX) value.getData();
	                dataString = data.getValue();
	            }

	            if (value.getData() instanceof ST)
	            {
	                ST data = (ST) value.getData();
	                dataString = data.getValue();
	            }

	            return dataString;
	        }
	    }
		return null;
	}

	public Date getDateResult(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        Varies[] values = obx.getObservationValue();
	        Varies value = null;
	        if (values.length > 0)
	        {
	            value = values[0];
	            TS ts = (TS) value.getData();
	            Date date = TranslateDate(ts);
	            return date;
	        }
	    }
		
		return null;
	}

	public Double getNumericResult(Message message, int orderRep, int obxRep)
	{
		double dVal = 0;
		OBX obx = getOBX(message, orderRep, obxRep);
		if(obx != null){
		    Varies[] values = obx.getObservationValue();
	        Varies value = null;
	        if (values.length > 0)
	        {
	            value = values[0];
	            String nmvalue = ((NM) value.getData()).getValue();

	            if (nmvalue != null)
	            {
	                try
	                {
	                    dVal = Double.parseDouble(nmvalue);
	                } catch (NumberFormatException ex)
	                {
	                    dVal = 0;
	                }
	            }
	        }
		}
		
		return dVal;
	}

	private Concept processCEType(Varies value,
			String pIdentifierString, String conceptQuestionId)
	{
		String conceptName = ((CE) value.getData()).getText().toString();
		String stConceptId = ((CE) value.getData()).getIdentifier().toString();
		Integer intObxValueID = null;

		try
		{
			intObxValueID = Integer.parseInt(stConceptId);
		} catch (NumberFormatException ne)
		{
			intObxValueID = 1;
			conceptName = stConceptId;
		}

		// Success conversion to int
		if (intObxValueID != null)
		{
			try
			{
				Concept answer = new Concept();
				answer.setConceptId(intObxValueID);
				ConceptName name = new ConceptName();
				name.setName( conceptName);
				name.setLocale(new Locale("en_US"));
				answer.addName(name);

				return answer;
			} catch (RuntimeException e)
			{
				log.error("Processing coded observation type failed. MRN: {}; Invalid OBX value: {}; concept question id: {}; concept name: {}." 
						,pIdentifierString, stConceptId, conceptQuestionId, conceptName,e);
			}

		}
		return null;
	}

	public Concept getCodedResult(Message message, int orderRep, int obxRep,
			 String pIdentifierString, String obsvID,
			String obsValueType)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        Varies[] values = obx.getObservationValue();
	        Varies value = null;

	        if (values.length > 0)
	        {
	            value = values[0];

	            if (obsValueType.equals("CE"))
	            {
	                return processCEType(value, pIdentifierString, obsvID);
	            }
	        }
	    }
		
		return null;
	}
	
	public ArrayList<Obs> getObs(Message message,Patient patient) throws HL7Exception
	{
		ConceptService conceptService = Context.getConceptService();

		PipeParser parser = new PipeParser();
		parser.setValidationContext(new NoValidation());
		PatientHandler patientHandler = new PatientHandler();
		HL7PatientHandler hl7PatientHandler = new HL7PatientHandler23();
		HL7EncounterHandler hl7EncounterHandler = new HL7EncounterHandler23();
		HL7SocketHandler hl7SocketHandler = new HL7SocketHandler(parser,
				patientHandler, this, hl7EncounterHandler,
				hl7PatientHandler,null);
		ArrayList<Obs> allObs = new ArrayList<Obs>();
		LocationService locationService = Context.getLocationService();
		String sendingFacility = getSendingFacility(message);
		Location existingLoc = locationService.getLocation(sendingFacility);
		
			if (message instanceof ORU_R01)
			{
				ORU_R01 oru = (ORU_R01) message;

				int numOrders = oru.getRESPONSE().getORDER_OBSERVATIONReps();
				ConceptDatatype codedDatatype = conceptService.getConceptDatatypeByName("Coded");
				ConceptDatatype numericDatatype = conceptService.getConceptDatatypeByName("Numeric");
				ConceptDatatype dateTimeDatatype = conceptService.getConceptDatatypeByName("Datetime");
				ConceptDatatype textDatatype = conceptService.getConceptDatatypeByName("Text");
				
				// Initialize the objects in case they go to a caching mechanism.
				Hibernate.initialize(codedDatatype);
				Hibernate.initialize(numericDatatype);
				Hibernate.initialize(dateTimeDatatype);
				Hibernate.initialize(textDatatype);
				for (int i = 0; i < numOrders; i++)
				{
					ORU_R01_ORDER_OBSERVATION order = oru.getRESPONSE()
							.getORDER_OBSERVATION(i);

					int numObs = order.getOBSERVATIONReps();
					for (int j = 0; j < numObs; j++)
					{
						Obs obs = hl7SocketHandler.CreateObservation(null,
								false, message, i, j, existingLoc,
								patient);
						
						Concept obsConcept = obs.getConcept();
						//if the concept datatype is null,
						//infer the type from the data
						if(obsConcept.getDatatype() == null){
							if(obs.getValueCoded() != null){
								obsConcept.setDatatype(codedDatatype);
							}
							
							if(obs.getValueNumeric() != null){
								obsConcept.setDatatype(numericDatatype);
							}
							
							if(obs.getValueDatetime() != null){
								obsConcept.setDatatype(dateTimeDatatype);
							}
							
							if(obs.getValueText() != null){
								obsConcept.setDatatype(textDatatype);
							}
						}
						
						if(obs.getObsDatetime()==null){
							//do not add obs without a date
						}else{
							allObs.add(obs);
						}
					}
				}
			}
		return allObs;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.sockethl7listener.HL7ObsHandler#getReps(ca.uhn.hl7v2.model.Message)
	 */
	public int getReps(Message message)
	{
		int reps = 0;
		if (message instanceof ORU_R01)
		{
			reps = ((ORU_R01) message).getRESPONSE()
					.getORDER_OBSERVATION().getOBSERVATIONReps();
		} else if ((message instanceof ADT_A01))
		{
			reps = ((ADT_A01) message).getOBXReps();
		}
		return reps;
	}
	
	/**
     * @see org.openmrs.module.sockethl7listener.HL7ObsHandler#getUnits(Message, int, int)
     * DWE CHICA-635
     */
    public String getUnits(Message message, int orderRep, int obxRep)
    {
    	OBX obx = getOBX(message, orderRep, obxRep);
    	if(obx != null){
    		CE units = obx.getUnits();
    		return units.getIdentifier().getValue();
    	}
    				
		return "";
    }
}
