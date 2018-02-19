package org.openmrs.module.chica.hl7.immunization;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.action.ProduceFormInstance;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.CX;
import ca.uhn.hl7v2.model.v231.datatype.ST;
import ca.uhn.hl7v2.model.v231.group.VXX_V02_PIDNK1;
import ca.uhn.hl7v2.model.v231.message.VXQ_V01;
import ca.uhn.hl7v2.model.v231.message.VXU_V04;
import ca.uhn.hl7v2.model.v231.message.VXX_V02;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.model.v231.segment.QRD;
import ca.uhn.hl7v2.model.v231.segment.QRF;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Constructs the hl7 message resulting from an abnormal newborn screen
 * @author msheley
 *
 */
/**
 * @author msheley
 * 
 */
public class ImmunizationQueryConstructor extends
		org.openmrs.module.sockethl7listener.HL7MessageConstructor {

	private static Log log = LogFactory.getLog(ProduceFormInstance.class);

	String formInstance = null;
	private static Properties props;
	private VXQ_V01 vxq;
	private VXU_V04 vxu;
	private String port;
	private String host;
	private static String SITE_CODESET231 = "HL70163l";
	private static String ROUTE_CODESET231 = "HL701621";
	private static String CVX_CODING_SYSTEM = "CVX";
	private static String CPT_CODING_SYSTEM = "CPT";
	private String ourFacility = "";
	private String ourApplication = "";
	private String version = "2.3.1";
	private String attributeRace = "Race";
	private String messageType = ""; // VXQ or VXU
	private String triggerEvent = ""; // V01 or V04
	private String codeSys = "";
	private String receivingApp = "";
	private String receivingFacility = "";
	private String resultStatus = "";
	private String ackType = "";
	private Socket socket = null;
	private String checkDigitScheme = "";
	private String pid2Required = "";
	private String assignAuthority = "";
	private String identifierTypeCode = "";
	private String app_acknowledgement_type = "";
	private String processing_id = "";
	private String obsLocation = "";
	private String patientClass = "";
	private String timeout = null;
	private String inboundPort = null;
	private String url = "";
	private String useNK = "";

	public ImmunizationQueryConstructor() {
		vxq = new VXQ_V01();
		vxu = new VXU_V04();
		// super();

	}

	public ImmunizationQueryConstructor(String vxu) {
		vxq = new VXQ_V01();
		PipeParser parser = new PipeParser();
		try {
			Message vxuMessage = parser.parse(vxu);
			if (vxuMessage instanceof ca.uhn.hl7v2.model.v25.message.VXU_V04
					|| vxuMessage instanceof ca.uhn.hl7v2.model.v231.message.VXU_V04) {
				this.vxu = (VXU_V04) vxuMessage;
			} else {
				this.vxu = new VXU_V04();
			}
		} catch (EncodingNotSupportedException e) {
			log.error("Error parsing vxu string to VXU_V04 - encoding not supported", e);
		} catch (Exception e) {
			log.error("Error parsing vxu string to VXU_V04", e);
		}

	}

	/**
	 * Set the properties from xml in hl7configFileLoaction
	 * 
	 * @param hl7configFileLocation
	 */

	private void setProperties(Properties props) {
		if (props != null) {
			codeSys = props.getProperty("coding_system");
			checkDigitScheme = props.getProperty("check_digit_algorithm");
			pid2Required = props.getProperty("pid_2_required");
			assignAuthority = props.getProperty("assigning_authority");
			identifierTypeCode = props.getProperty("identifier_type");
			ourFacility = props.getProperty("our_facility");
			ourApplication = props.getProperty("our_app");
			receivingApp = props.getProperty("receiving_app");
			receivingFacility = props.getProperty("receiving_facility");
			version = props.getProperty("version");
			messageType = props.getProperty("message_type");
			triggerEvent = props.getProperty("event_type_code");
			// Acknowlegment Type AL=always; NE=never, ER= Error only, and
			// SU=successful
			ackType = props.getProperty("acknowledgement_type");
			codeSys = props.getProperty("coding_system");
			resultStatus = props.getProperty("result_status");
			app_acknowledgement_type = props
					.getProperty("app_acknowledgement_type");
			processing_id = props.getProperty("msh_processing_id");
			obsLocation = props.getProperty("obs_location");
			patientClass = props.getProperty("patient_class");
			useNK = props.getProperty("useNextOfKin");

		}

	}

	public static String constructVXQ(VXQ_V01 vxq, Encounter encounter) {

		MSH msh = vxq.getMSH();
		QRD qrd = vxq.getQRD();
		QRF qrf = vxq.getQRF();

		String vxqString = null;
		try {
			msh.getMessageType().getMessageType().setValue("VXQ");
			msh.getMessageType().getTriggerEvent().setValue("V01");
			AddSegmentMSHByEncounter(encounter, msh);
			AddSegmentQRD(qrd, encounter);
			AddSegmentQRF(qrf, encounter);
			vxqString = convertVXQMessageToString(vxq);
		} catch (Exception e) {
			log.error("Error in constructVXQ().", e);
		}

		return vxqString;

	}

	public static String constructVXU(VXU_V04 vxu, Encounter encounter) {

		String vxuString = null;
		try {
			MSH msh = vxu.getMSH();
			PID pid = vxu.getPID();
			msh.getMessageType().getMessageType().setValue("VXU");
			msh.getMessageType().getTriggerEvent().setValue("V04");
			AddSegmentMSHByEncounter(encounter, msh);
			Patient patient = new Patient();
			patient = encounter.getPatient();
			AddSegmentPID(pid, patient.getPatientId());
			vxu = AddSegmentNK1(vxu, patient.getPatientId());
			vxuString = getVXUMessageString(vxu);
		} catch (Exception e) {
			log.error("Error in constructVXU().", e);
		}

		return vxuString;

	}

	// Update vxq name and id for requery

	public String updateVXQ(String vxq, Patient patient) {

		PipeParser parser = new PipeParser();
		PersonName name = patient.getPersonName();
		String id = "";
		PatientIdentifier identifier = patient
				.getPatientIdentifier("Immunization Registry");
		if (identifier != null) {
			id = identifier.getIdentifier();
		}

		try {

			VXQ_V01 vxqMessage = (VXQ_V01) parser.parse(vxq);

			if (name != null) {
				vxqMessage.getQRD().getWhoSubjectFilter(0).getIDNumber()
						.setValue(id);
				vxqMessage.getQRD().getWhoSubjectFilter(0)
						.getIdentifierTypeCode().setValue("SR");
				vxqMessage.getQRD().getWhoSubjectFilter(0).getFamilyLastName()
						.getFamilyName().setValue(name.getFamilyName());
				vxqMessage.getQRD().getWhoSubjectFilter(0).getGivenName()
						.setValue(name.getGivenName());
				vxqMessage.getQRD().getWhoSubjectFilter(0)
						.getMiddleInitialOrName()
						.setValue(name.getMiddleName());

				PipeParser pipeParser = new PipeParser();
				vxq = pipeParser.encode(vxqMessage);

			}
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		}

		return vxq;

	}

	private static QRD AddSegmentQRD(QRD qrd, Encounter encounter) {
		AdministrationService adminService = Context.getAdministrationService();

		String configFileName = adminService
				.getGlobalProperty("chica.ImmunizationQueryConfigFile");
		Properties props = IOUtil.getProps(configFileName);
		if (props == null) {
			return null;
		}
		
		String dateFormat = "yyyyMMdd";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		String date = formatter.format(new Date());
		
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(encounter.getPatient().getPatientId());

		if (patient == null)
			return null;
		//Always use CHICA MRN for initial query and not CHIRP SIIS (CHICA-756 MSHELEY)
		PatientIdentifierType pidtype = patientService
				.getPatientIdentifierTypeByName("MRN_OTHER");
		PatientIdentifier pid = patient.getPatientIdentifier(pidtype);
		String identifier = pid.getIdentifier();
		String idType = "MR";
	
		try {
			qrd.getQueryDateTime().getTimeOfAnEvent().setValue(date);
			qrd.getQueryFormatCode().setValue(
					props.getProperty("response_format_code"));
			qrd.getQueryPriority()
					.setValue(props.getProperty("query_priority"));
			qrd.getQueryID().setValue(String.valueOf(encounter.getPatient().getPatientId())); // CHICA-1151 replace getPatientId() with getPatient().getPatientId()
			qrd.getDeferredResponseType().setValue(
					props.getProperty("deferred_response_type"));
			qrd.getQuantityLimitedRequest().getQuantity().setValue(
					props.getProperty("quantity_limited_request"));
			qrd
					.getQuantityLimitedRequest()
					.getUnits()
					.getIdentifier()
					.setValue(
							props.getProperty("quantity_limited_request_units"));
			qrd.getWhoSubjectFilter(0).getIDNumber().setValue(identifier);
			qrd.getWhoSubjectFilter(0).getFamilyLastName().getFamilyName()
					.setValue(patient.getFamilyName());
			qrd.getWhoSubjectFilter(0).getGivenName().setValue(
					patient.getGivenName());
			qrd.getWhoSubjectFilter(0).getMiddleInitialOrName().setValue(
					patient.getMiddleName());
			qrd.getWhatSubjectFilter(0).getIdentifier().setValue(
					props.getProperty("what_subject_filter"));
			qrd.getWhatDepartmentDataCode(0).getIdentifier().setValue(
					props.getProperty("what_department_data_code"));
			qrd.getQueryResultsLevel().setValue(
					props.getProperty("query_results_level"));
			qrd.getWhoSubjectFilter();
			qrd.getWhoSubjectFilter(0).getIdentifierTypeCode().setValue(idType);

		} catch (DataTypeException e1) {
			log.error(Util.getStackTrace(e1));
		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		}

		return qrd;

	}

	public static QRF AddSegmentQRF(QRF qrf, Encounter encounter) {

		PatientService patientService = Context.getPatientService();
		PersonService personService = Context.getPersonService();

		Patient patient = encounter.getPatient();
		patient = patientService.getPatient(patient.getPatientId());// lookup to
		AdministrationService adminService = Context.getAdministrationService();

		String configFileName = adminService
				.getGlobalProperty("chica.ImmunizationQueryConfigFile");
		
		Properties props = IOUtil.getProps(configFileName);
		if (props == null) {
			return null;
		}

		try {
			if (patient == null)
				return null;
			String useNK = props.getProperty("useNextOfKin");
			String useBirthdate = props.getProperty("useDOB");
			String ssn = "";
			String dateFormat = "yyyyMMdd";
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			String birthDate = formatter.format(patient.getBirthdate());
			String birthState = "";
			String BirthRegistrationNumber = "";
			String motherLastName = "";
			String motherFirstName = "";
			String motherMaidenName = "";
			String motherSSN = "";
			String fatherSSN = "";
			String medicareNumber = "";
			boolean useNextOfKin = false;
			boolean useDOB = false;
			if (useNK != null
					&& (useNK.equalsIgnoreCase("true")
							|| useNK.equalsIgnoreCase("yes") || useNK
							.equalsIgnoreCase("1"))) {
				useNextOfKin = true;
			}
			if (useBirthdate == null
					|| (useBirthdate.equalsIgnoreCase("false")
							|| useBirthdate.equalsIgnoreCase("no") || useNK
							.equalsIgnoreCase("0"))) {
				birthDate = "";
			}

			ST motherFirstNameST = new ST(qrf.getMessage());
			PersonAttributeType pat = personService
					.getPersonAttributeTypeByName("Next of Kin");
			Person person = personService.getPerson(patient.getPatientId());
			PersonAttribute nextOfKinAttribute = person.getAttribute(pat);

			if (nextOfKinAttribute != null && useNextOfKin) {
				String name = nextOfKinAttribute.getValue();
				StringTokenizer names = new StringTokenizer(name, "|", false);
				if (names.hasMoreTokens()) {
					motherFirstName = names.nextToken();
				}
				if (names.hasMoreTokens()) {
					motherLastName = names.nextToken();
				}

			}

			PatientIdentifierType SSN = patientService
					.getPatientIdentifierTypeByName("SSN");
			PatientIdentifier ssnId = patient.getPatientIdentifier(SSN);
			if (ssnId != null)
				ssn = ssnId.getIdentifier();
			qrf.getWhereSubjectFilter(0).setValue(
					props.getProperty("where_subject_filter"));
			qrf.getOtherQRYSubjectFilter(0).setValue(ssn);
			qrf.getOtherQRYSubjectFilter(1).setValue(birthDate);
			qrf.getOtherQRYSubjectFilter(2).setValue(birthState);
			qrf.getOtherQRYSubjectFilter(3).setValue(BirthRegistrationNumber);
			qrf.getOtherQRYSubjectFilter(4).setValue(medicareNumber);
			if (useNextOfKin) {
				qrf.getOtherQRYSubjectFilter(5).setValue(motherLastName);
				qrf.getOtherQRYSubjectFilter(5).getExtraComponents()
						.getComponent(0).setData(motherFirstNameST);
				qrf.getOtherQRYSubjectFilter(6).setValue(motherMaidenName);
				qrf.getOtherQRYSubjectFilter(7).setValue(motherSSN);
				qrf.getOtherQRYSubjectFilter(8).setValue("");
				qrf.getOtherQRYSubjectFilter(9).setValue(fatherSSN);

			}

		} catch (DataTypeException e1) {
			log.error("Datetype error when creating qrf segment", e1);
		} catch (Exception e) {
			log.error("Error creating qrf segment", e);
		}

		return qrf;

	}

	public static VXQ_V01 AddSegmentMSH(VXQ_V01 vxq) {

		MSH msh = vxq.getMSH();
		AdministrationService adminService = Context.getAdministrationService();
		String configFileName = adminService
				.getGlobalProperty("chica.ImmunizationQueryConfigFile");

		Properties props = IOUtil.getProps(configFileName);
		String messageType = props.getProperty("message_type");
		String ourFacility = props.getProperty("our_facility");
		String ourApplication = props.getProperty("our_app");
		String receivingApp = props.getProperty("receiving_app");
		String receivingFacility = props.getProperty("receiving_facility");
		String version = props.getProperty("version");
		String triggerEvent = props.getProperty("event_type_code");
		// Acknowlegment Type AL=always; NE=never, ER= Error only, and
		// SU=successful
		String ackType = props.getProperty("acknowledgement_type");
		String app_acknowledgement_type = props
				.getProperty("app_acknowledgement_type");
		String processing_id = props.getProperty("msh_processing_id");

		// Get current date
		String dateFormat = "yyyyMMdd";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		String formattedDate = formatter.format(new Date());

		try {
			if (props != null) {
				msh.getFieldSeparator().setValue("|");
				msh.getEncodingCharacters().setValue("^~\\&");
				msh.getDateTimeOfMessage().getTimeOfAnEvent().setValue(
						formattedDate);
				msh.getSendingApplication().getNamespaceID().setValue(
						ourApplication);
				msh.getSendingFacility().getNamespaceID().setValue(ourFacility);
				msh.getMessageType().getMessageType().setValue(messageType);
				msh.getMessageType().getTriggerEvent().setValue(triggerEvent);
				msh.getMessageControlID().setValue("");
				msh.getVersionID().getVersionID().setValue(version);
				msh.getReceivingApplication().getNamespaceID().setValue(
						receivingApp);
				msh.getReceivingFacility().getNamespaceID().setValue(
						receivingFacility);
				msh.getAcceptAcknowledgmentType().setValue(ackType);
				msh.getApplicationAcknowledgmentType().setValue(
						app_acknowledgement_type);
				msh.getProcessingID().getProcessingID().setValue(processing_id);
				msh.getMessageControlID().setValue(
						ourApplication + "-" + formattedDate);
			}
		} catch (DataTypeException e) {
			log.error("Error in AddSegmentMSH().", e);
		}

		return vxq;

	}

	public static MSH AddSegmentMSHByEncounter(Encounter enc, MSH msh) {

		AdministrationService adminService = Context.getAdministrationService();
		String configFileName = adminService
				.getGlobalProperty("chica.ImmunizationQueryConfigFile");
		Properties props = IOUtil.getProps(configFileName);

		// MSH msh = vxq.getMSH();
		if (props != null) {
			String ourFacility = props.getProperty("our_facility");
			String ourApplication = props.getProperty("our_app");
			String receivingApp = props.getProperty("receiving_app");
			String receivingFacility = props.getProperty("receiving_facility");
			String version = props.getProperty("version");
			// Acknowlegment Type AL=always; NE=never, ER= Error only, and
			// SU=successful
			String ackType = props.getProperty("acknowledgement_type");
			String app_acknowledgement_type = props
					.getProperty("app_acknowledgement_type");
			String processing_id = props.getProperty("msh_processing_id");

			// Get current date
			String dateFormat = "yyyyMMddHHmmss";
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			String formattedDate = formatter.format(new Date());
			Integer patientId = enc.getPatient().getPatientId(); // CHICA-1151 replace getPatientId() with getPatient().getPatientId()

			try {
				msh.getFieldSeparator().setValue("|");
				msh.getEncodingCharacters().setValue("^~\\&");
				msh.getDateTimeOfMessage().getTimeOfAnEvent().setValue(
						formattedDate);
				msh.getSendingApplication().getNamespaceID().setValue(
						ourApplication);
				msh.getSendingFacility().getNamespaceID().setValue(ourFacility);
				msh.getMessageControlID().setValue("");
				msh.getVersionID().getVersionID().setValue(version);
				msh.getReceivingApplication().getNamespaceID().setValue(
						receivingApp);
				msh.getReceivingFacility().getNamespaceID().setValue(
						receivingFacility);
				msh.getAcceptAcknowledgmentType().setValue(ackType);
				msh.getApplicationAcknowledgmentType().setValue(
						app_acknowledgement_type);
				msh.getProcessingID().getProcessingID().setValue(processing_id);
				msh.getMessageControlID().setValue(
						ourApplication + "-" + patientId);
			} catch (DataTypeException e) {
				log.error("Error in AddSegmentMSHByEncounter().", e);
			}
		}

		return msh;

	}

	public String getFormInstance() {
		return formInstance;
	}

	public void setFormInstance(String formInstance) {
		this.formInstance = formInstance;
	}

	public void setAssignAuthority(PatientIdentifier pi) {

		super.setAssignAuthority(pi);
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public static String convertVXQMessageToString(VXQ_V01 vxq) {
		PipeParser pipeParser = new PipeParser();
		String msg = null;
		try {
			msg = pipeParser.encode(vxq);
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		}
		return msg;
	}

	public String getVXUMessageString() {
		PipeParser pipeParser = new PipeParser();
		String msg = null;
		try {
			msg = pipeParser.encode(vxu);
		} catch (Exception e) {
			log.error("Exception parsing constructed message.");
		}
		return msg;

	}

	public static String getVXUMessageString(VXU_V04 vxu) {
		PipeParser pipeParser = new PipeParser();
		String msg = null;
		try {
			msg = pipeParser.encode(vxu);
		} catch (Exception e) {
			log.error("Exception parsing VXU message.");
		}
		return msg;

	}

	public String getPort() {
		return port;
	}

	public Integer getPortAsInteger() {
		Integer portInt = null;
		if (port != null) {
			portInt = Integer.valueOf(port);
		}
		return portInt;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Integer getTimeout() {
		// in ms
		Integer to = null;
		if (timeout != null) {
			to = Integer.valueOf(timeout);
		}
		return to;
	}

	public Integer getInboundPort() {
		if (inboundPort != null) {
			return Integer.valueOf(inboundPort);
		}
		return null;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public VXQ_V01 getVxq() {
		return vxq;
	}

	public void setVxq(VXQ_V01 vxq) {
		this.vxq = vxq;
	}

	public VXU_V04 getVxu() {
		return vxu;
	}

	public void setVxu(VXU_V04 vxu) {
		this.vxu = vxu;
	}

	public String getStateId(String vxx) {
		String stateIdString = null;
		PipeParser parser = new PipeParser();
		try {
			Message message = parser.parse(vxx);
			VXX_V02 vxxMessage = (VXX_V02) message;
			if (vxxMessage == null || vxxMessage.getPIDNK1Reps() == 0) {
				return null;
			}
			VXX_V02_PIDNK1 pidNk1 = vxxMessage.getPIDNK1();
			PID pid = pidNk1.getPID();
			CX id = pid.getPatientIdentifierList(0);
			ST stateId = id.getID();
			stateIdString = stateId.getValue();

		} catch (EncodingNotSupportedException e) {
			log.error(Util.getStackTrace(e));
		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		}
		return stateIdString;
	}

	public ca.uhn.hl7v2.model.v231.segment.PID selectPatientFromVXX(String vxx) {
		ca.uhn.hl7v2.model.v231.segment.PID PID = null;
		return PID;
	}

	public static void saveFile(String file, String text, String filetype,
			Encounter encounter) {
		String encounterId = "";
		String locationId = "";
		try {
			if (encounter != null && encounter.getEncounterId() != null){
				encounterId = encounter.getEncounterId().toString();
				locationId = encounter.getLocation().getLocationId().toString();
			}
			FileOutputStream immunFileOutput = new FileOutputStream(file + "\\"
					+ encounterId + "_" + locationId + "_"
					+ Util.archiveStamp() + "." + filetype);
			ByteArrayInputStream responseInput = new ByteArrayInputStream(text
					.getBytes());
			IOUtil.bufferedReadWrite(responseInput, immunFileOutput);
		} catch (Exception e) {
			log.error(" vxq output stream error", e);
		}

	}

	
	public static VXU_V04 AddSegmentNK1(VXU_V04 vxu, Integer patientId) {
		
		PatientService patientService = Context.getPatientService();
		
		try {
			// If a next of kin exists, send nk information
			
			String fullName = null;
			Patient patient = patientService.getPatient(patientId);
			PersonAttribute nkAttr = patient.getAttribute("Next of Kin");
			if (nkAttr == null || (fullName = nkAttr.getValue()) == null 
					|| fullName.trim().equalsIgnoreCase("") 
					|| fullName.trim().equalsIgnoreCase("|")){
				return vxu;
			}
			
			String firstName = "";
			String lastName = "";
			if ( fullName.indexOf("|")<0 || fullName.indexOf("|") == (fullName.length() - 1)){
				//field only contains the first name
				firstName = fullName;
			}
			else {
				firstName = fullName.substring(0,fullName.indexOf("|")-1 );
				lastName = fullName.substring(fullName.indexOf("|") + 1, fullName.length() - 1 );
			}
			
			vxu.getNK1(0).getNKName(0).getFamilyLastName().getFamilyName().setValue(lastName);
			vxu.getNK1(0).getNKName(0).getGivenName().setValue(firstName);
			

		} catch (DataTypeException e) {
			log.error(Util.getStackTrace(e));
		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		}
		return vxu;
	}

	public static PID AddSegmentPID(PID pid, Integer patientId) {
		PersonService personService = Context.getPersonService();
		PatientService patientService = Context.getPatientService();
		AdministrationService adminService = Context.getAdministrationService();
		String configFileName = adminService
				.getGlobalProperty("chica.ImmunizationQueryConfigFile");

		Properties props = IOUtil.getProps(configFileName);
		String pid2Required = props.getProperty("pid_2_required");
		String checkDigitScheme = props.getProperty("check_digit_algorithm");
		String assignAuthority = props.getProperty("assigning_authority");
		String identifierTypeCode = props.getProperty("identifier_type");
		String identifierTypeCodeRegistry = "SR";
		String assignAuthorityRegistry = "";
		String identifierStringRegistry = "";
		
		Patient patient = patientService.getPatient(patientId);

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date dob = patient.getBirthdate();
		Date dod = patient.getDeathDate();
		String dobStr = "";
		String dodStr = "";
		if (dob != null)
			dobStr = df.format(dob);
		if (dod != null)
			dodStr = df.format(dod);

		try {
			// Name
			pid.getPatientName(0).getFamilyLastName().getFamilyName().setValue(
					patient.getFamilyName());
			pid.getPatientName(0).getMiddleInitialOrName().setValue(
					patient.getMiddleName());
			pid.getPatientName(0).getGivenName().setValue(patient.getGivenName());

			// Identifiers
			PatientIdentifier piRegistry = patient
					.getPatientIdentifier("Immunization Registry");
			PatientIdentifier piMRN = patient.getPatientIdentifier("MRN_OTHER");
		
			if (piMRN != null) {
				// Identifier PID-2 not required
				if (pid2Required != null && Boolean.valueOf(pid2Required)) {
					String assignAuthFromIdentifierType = getAssigningAuthorityFromPatientIDType(piMRN);
					String addon = "-" + assignAuthFromIdentifierType;
					pid.getPatientID().getID().setValue(
							piMRN.getIdentifier() + addon);
				}
			}

			// Identifier PID-3
			// MRN
			if (piMRN != null) {
				String identString = piMRN.getIdentifier();
				if (identString != null) {
					Integer dash = identString.indexOf("-");
					if (dash >= 0) {
						identString = identString.substring(0, dash)
								+ identString.substring(dash + 1);
					}
				}
				
				pid.getPatientIdentifierList(0).getID().setValue(identString);
				pid.getPatientIdentifierList(0)
						.getCodeIdentifyingTheCheckDigitSchemeEmployed()
						.setValue(checkDigitScheme);
			}
			
	
			pid.getPatientIdentifierList(0).getAssigningAuthority()
					.getNamespaceID().setValue(assignAuthority);
			pid.getPatientIdentifierList(0).getIdentifierTypeCode().setValue(
					identifierTypeCode);
			
			if (piRegistry != null ){
				identifierStringRegistry = piRegistry.getIdentifier();
				pid.getPatientIdentifierList(1).getID().setValue(identifierStringRegistry);
				pid.getPatientIdentifierList(1).getIdentifierTypeCode().setValue(
						identifierTypeCodeRegistry);
				pid.getPatientIdentifierList(1).getAssigningAuthority()
				.getNamespaceID().setValue(assignAuthorityRegistry);
			}

			// Address
			pid.getPatientAddress(0).getStreetAddress().setValue(
					patient.getPersonAddress().getAddress1());
			pid.getPatientAddress(0).getOtherDesignation().setValue(
					patient.getPersonAddress().getAddress2());
			pid.getPatientAddress(0).getCity().setValue(
					patient.getPersonAddress().getCityVillage());
			pid.getPatientAddress(0).getStateOrProvince().setValue(
					patient.getPersonAddress().getStateProvince());
			pid.getPatientAddress(0).getZipOrPostalCode().setValue(
					patient.getPersonAddress().getPostalCode());

			// Telephone
			PersonAttributeType personAttrTypeId = personService
					.getPersonAttributeTypeByName("Telephone Number");
			PersonAttribute telephoneNumberAttribute = patient
					.getAttribute(personAttrTypeId);
			if (telephoneNumberAttribute != null) {
				String tn = telephoneNumberAttribute.getValue();
				if (tn != null) {
					tn = tn.replace("(", "");
					tn = tn.replace(")", "");
					tn = tn.replace("-", "");
				}
				pid.getPhoneNumberHome(0).getPhoneNumber().setValue(tn);
			}

			// gender
			pid.getSex().setValue(patient.getGender());

			// dob
			pid.getDateTimeOfBirth().getTimeOfAnEvent().setValue(dobStr);

			// Race identifier -
			personAttrTypeId = personService
					.getPersonAttributeTypeByName("Race");
			PersonAttribute raceAttribute = patient.getAttribute(personAttrTypeId);
			String race = null;
			if (raceAttribute != null) {
				race = raceAttribute.getValue();
			}
			
			pid.getRace(0).getIdentifier().setValue(race);

			// Death
			pid.getPatientDeathIndicator().setValue(patient.getDead().toString());
			pid.getPatientDeathDateAndTime().getTimeOfAnEvent()
					.setValue(dodStr);

			return pid;

		} catch (DataTypeException e) {
			log.error(Util.getStackTrace(e));
		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public static VXU_V04 addVaccine(VXU_V04 vxu, Vaccine vaccine) {
		// get obs for vaccine name. - name, code, date
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		AdministrationService adminService = Context.getAdministrationService();
		String configFileName = adminService
				.getGlobalProperty("chica.ImmunizationQueryConfigFile");
		Properties props = IOUtil.getProps(configFileName);
		if (props == null) {
			return null;
		}

		try {

			String lotnumber = vaccine.getLotNumber();
			String providerFN = vaccine.getProviderFN();
			String providerLN = vaccine.getProviderLN();
			String code = vaccine.getVaccineCode();
			String name = vaccine.getVaccineName();
			String address1 = vaccine.getAddress1();
			String facility = vaccine.getFacility();
			String city = vaccine.getCity();
			String state = vaccine.getState();
			String zipcode = vaccine.getZipcode();
			String combined = vaccine.getSiteCombined();
			String siteCode = vaccine.getSiteCode();
			String route = vaccine.getRoute();
			String routeCode = vaccine.getRouteCode();
			String dateGivenString = "";
			Date dateGiven = vaccine.getDateGiven();
			if (dateGiven != null) {
				dateGivenString = dateformat.format(dateGiven);
			}
			int rep = vxu.getORCRXARXROBXNTEReps();
			vxu.getORCRXARXROBXNTE(rep).getRXA().getGiveSubIDCounter()
					.setValue("0");
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getAdministrationSubIDCounter().setValue("999");
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getDateTimeStartOfAdministration().getTimeOfAnEvent()
					.setValue(dateGivenString);
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getDateTimeEndOfAdministration().getTimeOfAnEvent()
					.setValue(dateGivenString);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getIdentifier().setValue(code);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getText().setValue(name);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getNameOfCodingSystem().setValue(CVX_CODING_SYSTEM);
			vxu.getORCRXARXROBXNTE(rep).getRXR().getSite()
					.getNameOfCodingSystem().setValue(SITE_CODESET231);
			vxu.getORCRXARXROBXNTE(rep).getRXR().getRoute()
					.getNameOfCodingSystem().setValue(ROUTE_CODESET231);
			vxu.getORCRXARXROBXNTE(rep).getRXR().getRoute().getIdentifier()
					.setValue(routeCode);
			vxu.getORCRXARXROBXNTE(rep).getRXR().getRoute().getText().setValue(
					route);
			vxu.getORCRXARXROBXNTE(rep).getRXR().getSite().getIdentifier()
					.setValue(siteCode);
			vxu.getORCRXARXROBXNTE(rep).getRXR().getSite().getText().setValue(
					combined);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeringProvider(0)
					.getFamilyLastName().getFamilyName().setValue(providerLN);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeringProvider(0)
					.getGivenName().setValue(providerFN);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
					.getStreetAddress().setValue(address1);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
					.getCity().setValue(city);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
					.getStateOrProvince().setValue(state);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
					.getZipOrPostalCode().setValue(zipcode);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
					.getFacility().getNamespaceID().setValue(facility);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getSystemEntryDateTime()
					.getTimeOfAnEvent().setValue(dateformat.format(new Date()));
			vxu.getORCRXARXROBXNTE(rep).getRXA().getSubstanceLotNumber(0)
					.setValue(lotnumber);

		} catch (DataTypeException e) {
			log.error(Util.getStackTrace(e));
		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		}

		return vxu;
	}

	public static String addVaccine(VXU_V04 vxu, Obs vaccine, String cvxName,
			String cvxCode, String armThigh, String leftRight, String lotNumber) {
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		ChirdlUtilBackportsService service = Context
				.getService(ChirdlUtilBackportsService.class);
		String vxuString = "";
		String siteCode = "";
		String routeCode = "OTH";
		String routeName = "OTHER/MISCELLANEOUS";
		String site = "";
		String facility = "";
		String facilityId = "";
		String date = "";
		if (vaccine == null) {
			return null;
		}

		try {

			// provider
			Encounter encounter = vaccine.getEncounter();
			
			// CHICA-1151 Use the provider that has the "Attending Provider" role for the encounter
			org.openmrs.Provider provider = org.openmrs.module.chirdlutil.util.Util.getProviderByAttendingProviderEncounterRole(encounter);
			String providerFN = "";
			String providerLN = "";
			
			if(provider != null)
			{
				Person person = provider.getPerson();
				providerFN = person.getGivenName();
				providerLN = person.getFamilyName();
			}
			

			// location
			Location location = vaccine.getLocation();
			String address1 = location.getAddress1();
			String city = location.getCityVillage();
			String state = location.getStateProvince();
			String zipcode = location.getPostalCode();
			ChirdlLocationAttributeValue attrvalue = service
					.getLocationAttributeValue(location.getLocationId(),
							"clinicDisplayName");
			if (attrvalue != null) {
				facility = attrvalue.getValue();
			}
			
			ChirdlLocationAttributeValue facCodeAttrVal = service
			.getLocationAttributeValue(location.getLocationId(),
					"facilityCode");
			if (facCodeAttrVal != null) {
				facilityId = facCodeAttrVal.getValue();
			}

			// date
			Date dateGiven = vaccine.getObsDatetime();
			date = dateformat.format(dateGiven);

			if (armThigh != null && armThigh.length() > 0 && leftRight != null
					&& leftRight.length() > 0) {
				site = leftRight + " " + armThigh;
				siteCode = (leftRight.substring(0, 0) + armThigh
						.substring(0, 0)).toUpperCase();
				routeName = "INTRAMUSCULAR";
				routeCode = "IM";
			}

			// Construct the vxu
			int rep = vxu.getORCRXARXROBXNTEReps();
			vxu.getORCRXARXROBXNTE(rep).getRXA().getGiveSubIDCounter()
					.setValue("0");
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getAdministrationSubIDCounter().setValue("999");
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getDateTimeStartOfAdministration().getTimeOfAnEvent()
					.setValue(date);
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getDateTimeEndOfAdministration().getTimeOfAnEvent()
					.setValue(date);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getIdentifier().setValue(cvxCode);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getText().setValue(cvxName);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getNameOfCodingSystem().setValue(CVX_CODING_SYSTEM);

			if (site != null && !site.equals("")) {
				vxu.getORCRXARXROBXNTE(rep).getRXR().getSite()
						.getNameOfCodingSystem().setValue(SITE_CODESET231);
				vxu.getORCRXARXROBXNTE(rep).getRXR().getRoute()
						.getNameOfCodingSystem().setValue(ROUTE_CODESET231);
				vxu.getORCRXARXROBXNTE(rep).getRXR().getRoute().getIdentifier()
						.setValue(routeCode);
				vxu.getORCRXARXROBXNTE(rep).getRXR().getRoute().getText()
						.setValue(routeName);
				vxu.getORCRXARXROBXNTE(rep).getRXR().getSite().getIdentifier()
						.setValue(siteCode);
				vxu.getORCRXARXROBXNTE(rep).getRXR().getSite().getText()
						.setValue(site);
			}

			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeringProvider(0)
					.getFamilyLastName().getFamilyName().setValue(providerLN);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeringProvider(0)
					.getGivenName().setValue(providerFN);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
					.getStreetAddress().setValue(address1);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
					.getCity().setValue(city);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
					.getStateOrProvince().setValue(state);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
					.getZipOrPostalCode().setValue(zipcode);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
					.getFacility().getNamespaceID().setValue(facility);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
			.getFacility().getUniversalID().setValue(facilityId);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getSystemEntryDateTime()
					.getTimeOfAnEvent().setValue(dateformat.format(new Date()));
			vxu.getORCRXARXROBXNTE(rep).getRXA().getSubstanceLotNumber(0)
					.setValue(lotNumber);
			vxuString = getVXUMessageString(vxu);

		} catch (DataTypeException e) {
			log.error("DataTypeException in addVaccine().", e);
		} catch (Exception e) {
			log.error("Error in addVaccine().", e);
		}

		return vxuString;
	}

	public static String addVaccine(String vxuString, Obs vaccine,
			String cvxName, String cvxCode, String cptName, String cptCode,
			boolean includeProvider, boolean includeLocation) {
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		ChirdlUtilBackportsService service = Context
				.getService(ChirdlUtilBackportsService.class);
		ObsService obsService = Context.getObsService();
		String facility = "";
		String lotNumber = "";
		String date = "";
		String facilityId = "";

		if (vaccine == null) {
			return null;
		}
		
		try {

			// provider
			Encounter encounter = vaccine.getEncounter();
			
			// CHICA-1151 Use the provider that has the "Attending Provider" role for the encounter
			org.openmrs.Provider provider = org.openmrs.module.chirdlutil.util.Util.getProviderByAttendingProviderEncounterRole(encounter);
			String providerFN = "";
			String providerLN = "";

			if(provider != null)
			{
				Person person = provider.getPerson();
				providerFN = person.getGivenName();
				providerLN = person.getFamilyName();
			}

			// location
			Location location = vaccine.getLocation();
			String address1 = location.getAddress1();
			String city = location.getCityVillage();
			String state = location.getStateProvince();
			String zipcode = location.getPostalCode();
			ChirdlLocationAttributeValue attrvalue = service
					.getLocationAttributeValue(location.getLocationId(),
							"clinicDisplayName");
			if (attrvalue != null) {
				facility = attrvalue.getValue();
			}
			
			if (location != null){
				ChirdlLocationAttributeValue facCodeAttrVal = service
				.getLocationAttributeValue(location.getLocationId(),
						"facilityCode");
				if (facCodeAttrVal != null) {
					facilityId = facCodeAttrVal.getValue();
				}
			}

			// date
			Date dateGiven = vaccine.getObsDatetime();
			date = dateformat.format(dateGiven);

			PipeParser parser = new PipeParser();
			VXU_V04 vxu = (VXU_V04) parser.parse(vxuString);
			
			// Construct the vxu
			int rep = vxu.getORCRXARXROBXNTEReps();
			vxu.getORCRXARXROBXNTE(rep).getRXA().getGiveSubIDCounter()
					.setValue("0");
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getAdministrationSubIDCounter().setValue("999");
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getDateTimeStartOfAdministration().getTimeOfAnEvent()
					.setValue(date);
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getDateTimeEndOfAdministration().getTimeOfAnEvent()
					.setValue(date);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getIdentifier().setValue(cvxCode);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getText().setValue(cvxName);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getNameOfCodingSystem().setValue(CVX_CODING_SYSTEM);

			if (includeProvider) {
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeringProvider(0).getFamilyLastName()
						.getFamilyName().setValue(providerLN);
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeringProvider(0).getGivenName().setValue(
								providerFN);
				
				// CHICA-925 Use provider.identifier instead of obs for PROVIDER_ID concept
				// Use provider.identifier
				org.openmrs.Provider openmrsProvider = org.openmrs.module.chirdlutil.util.Util.getProviderByAttendingProviderEncounterRole(encounter);
				if(openmrsProvider != null)
				{
					vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getAdministeringProvider(0).getIDNumber().setValue(openmrsProvider.getIdentifier());
				}
			}

			if (includeLocation) {
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeredAtLocation().getStreetAddress()
						.setValue(address1);
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeredAtLocation().getCity().setValue(city);
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeredAtLocation().getStateOrProvince()
						.setValue(state);
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeredAtLocation().getZipOrPostalCode()
						.setValue(zipcode);
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeredAtLocation().getFacility()
						.getNamespaceID().setValue(facility);
				vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
				.getFacility().getUniversalID().setValue(facilityId);
				vxu.getORCRXARXROBXNTE(rep).getRXA()
				.getAdministeredAtLocation().getFacility().getUniversalID().setValue(facilityId);
			}

			vxu.getORCRXARXROBXNTE(rep).getRXA().getSystemEntryDateTime()
					.getTimeOfAnEvent().setValue(dateformat.format(new Date()));
			vxu.getORCRXARXROBXNTE(rep).getRXA().getSubstanceLotNumber(0)
					.setValue(lotNumber);
			vxuString = getVXUMessageString(vxu);

		} catch (DataTypeException e) {
			log.error(Util.getStackTrace(e));
		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		}

		return vxuString;
	}

	// Physician vaccine history entries are saved as a datetime obs.
	public static String addVaccineHistory(String vxuString, Obs vaccine,
			String cvxName, String cvxCode, String cptName, String cptCode, String action,
			boolean includeProvider, boolean includeLocation) {
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
	
		if (vaccine == null) {
			return null;
		}

		try {

			// Do not include provider, because this is historical. 
			// The vaccine may not have been administered or ordered by the encounter provider.
			// Do not include location, because this may not have been 
			// administered at this location.
		
			// date vaccination was administered
			Date dateGiven = vaccine.getValueDatetime();
			String date = dateformat.format(dateGiven);

			PipeParser parser = new PipeParser();
			VXU_V04 vxu = (VXU_V04) parser.parse(vxuString);
			
			// Construct the RXA/RXR/OBX
			int rep = vxu.getORCRXARXROBXNTEReps();
			vxu.getORCRXARXROBXNTE(rep).getRXA().getGiveSubIDCounter()
					.setValue("0");
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getAdministrationSubIDCounter().setValue("999");
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getDateTimeStartOfAdministration().getTimeOfAnEvent()
					.setValue(date);
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getDateTimeEndOfAdministration().getTimeOfAnEvent()
					.setValue(date);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getIdentifier().setValue(cvxCode);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getText().setValue(cvxName);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getNameOfCodingSystem().setValue("CVX");
			vxu.getORCRXARXROBXNTE(rep).getRXA().getSystemEntryDateTime()
					.getTimeOfAnEvent().setValue(dateformat.format(new Date()));
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
			.getAlternateIdentifier().setValue(cptCode);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
				.getAlternateText().setValue(cptName);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
				.getNameOfAlternateCodingSystem().setValue(CPT_CODING_SYSTEM);
		
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministrationNotes(0)
					.getIdentifier().setValue("01");
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministrationNotes(0)
					.getText().setValue("Historical information - source unspecified");
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministrationNotes(0)
					.getNameOfCodingSystem().setValue(
							"NIP001");
			
			//action -- add, update, delete
			vxu.getORCRXARXROBXNTE(rep).getRXA().getActionCodeRXA().setValue(
					action);
			
			vxuString = getVXUMessageString(vxu);

		} catch (DataTypeException e) {
			log.error(Util.getStackTrace(e));
		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		}

		return vxuString;
	}

	public String getSITE_CODESET231() {
		return SITE_CODESET231;
	}

	public void setSITE_CODESET231(String sITECODESET231) {
		SITE_CODESET231 = sITECODESET231;
	}

	public String getROUTE_CODESET231() {
		return ROUTE_CODESET231;
	}

	public void setROUTE_CODESET231(String rOUTECODESET231) {
		ROUTE_CODESET231 = rOUTECODESET231;
	}

	public static String addVaccine(String vxuString, Obs vaccine,
			String cvxName, String cvxCode, String cptName, String cptCode,
			String armThigh, String leftRight, String lotNumber, String routeCode, String routeName, String action,
			boolean includeProvider, boolean includeLocation) {
		// get obs for vaccine name. - name, code, date
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		ChirdlUtilBackportsService service = Context
				.getService(ChirdlUtilBackportsService.class);
		ObsService obsService = Context.getObsService();
		ConceptService conceptService = Context.getConceptService();
		String siteCode = ""; // arm, thigh, left, right
		String site = "";
		String facility = "";
		String date = "";
		String facilityCode = "";
		if (vaccine == null) {
			return null;
		}

		try {
			PipeParser parser = new PipeParser();
			VXU_V04 vxu = (VXU_V04) parser.parse(vxuString);
			// provider
			Encounter encounter = vaccine.getEncounter();
			
			// CHICA-1151 Use the provider that has the "Attending Provider" role for the encounter
			org.openmrs.Provider provider = org.openmrs.module.chirdlutil.util.Util.getProviderByAttendingProviderEncounterRole(encounter);
			String providerFN = "";
			String providerLN = "";

			if(provider != null)
			{
				Person person = provider.getPerson();
				providerFN = person.getGivenName();
				providerLN = person.getFamilyName();
			}
			
			Location location = vaccine.getLocation();
			
			// date
			Date dateGiven = vaccine.getObsDatetime();
			date = dateformat.format(dateGiven);

			if (armThigh != null && armThigh.length() > 0 && leftRight != null
					&& leftRight.length() > 0) {
				site = leftRight + " " + armThigh;
				siteCode = (leftRight.substring(0, 1) + armThigh
						.substring(0, 1)).toUpperCase();
				
			}
			
			if (routeCode == null || routeCode.length() <= 0 ) {
				routeCode = "OTH";
				routeName = "OTHER/MISCELLANEOUS";
			}

			if (action == null || action.equalsIgnoreCase("")) {
				// Default
				action = "A";
			}

			// Construct the vxu
			int rep = vxu.getORCRXARXROBXNTEReps();
			vxu.getORCRXARXROBXNTE(rep).getRXA().getGiveSubIDCounter()
					.setValue("0");
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getAdministrationSubIDCounter().setValue("999");
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getDateTimeStartOfAdministration().getTimeOfAnEvent()
					.setValue(date);
			vxu.getORCRXARXROBXNTE(rep).getRXA()
					.getDateTimeEndOfAdministration().getTimeOfAnEvent()
					.setValue(date);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getIdentifier().setValue(cvxCode);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getText().setValue(cvxName);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getNameOfCodingSystem().setValue("CVX");
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getAlternateIdentifier().setValue(cptCode);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getAlternateText().setValue(cptName);
			vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredCode()
					.getNameOfAlternateCodingSystem().setValue(CPT_CODING_SYSTEM);

			if (site != null && !site.equals("")) {
				vxu.getORCRXARXROBXNTE(rep).getRXR().getSite()
						.getNameOfCodingSystem().setValue(SITE_CODESET231);
				vxu.getORCRXARXROBXNTE(rep).getRXR().getRoute()
						.getNameOfCodingSystem().setValue(ROUTE_CODESET231);
				vxu.getORCRXARXROBXNTE(rep).getRXR().getRoute().getIdentifier()
						.setValue(routeCode);
				vxu.getORCRXARXROBXNTE(rep).getRXR().getRoute().getText()
						.setValue(routeName);
				vxu.getORCRXARXROBXNTE(rep).getRXR().getSite().getIdentifier()
						.setValue(siteCode);
				vxu.getORCRXARXROBXNTE(rep).getRXR().getSite().getText()
						.setValue(site);
			}

			if (includeProvider) {
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeringProvider(0).getFamilyLastName()
						.getFamilyName().setValue(providerLN);
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeringProvider(0).getGivenName().setValue(
								providerFN);
				
				//Get the ADT message Provider id from the provider_id observation
				List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
				encounters.add(encounter);
				List<Person> persons = new ArrayList<Person>();
				persons.add(new Person(encounter.getPatient().getPatientId()));  // CHICA-1151 replace getPatientId() with getPatient().getPatientId()
				List<Concept> concepts = new ArrayList<Concept>();
				Concept concept = conceptService.getConceptByName("PROVIDER_ID");
				concepts.add(concept);
				
				List<Obs> providerIdObs = obsService.getObservations(persons, encounters, 
						concepts, null, null, null, null, null, null, null, null, false);
				
				if (providerIdObs != null && providerIdObs.size() > 0 
						&& providerIdObs.get(0) != null && providerIdObs.get(0) != null){
					vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeringProvider(0).getIDNumber().setValue(providerIdObs.get(0).getValueText());
				}
			}
			
			//Get facility name and code from location attributes
			if (includeLocation && location != null) {
				
				
				ChirdlLocationAttributeValue attrvalue = service
						.getLocationAttributeValue(location.getLocationId(),
								"clinicDisplayName");
				ChirdlLocationAttributeValue facilityCodeAttrValue = 
					service.getLocationAttributeValue(location.getLocationId(), "facilityCode");
				if (attrvalue != null) {
					facility = attrvalue.getValue();
				}
				if (facilityCodeAttrValue != null){
					facilityCode = facilityCodeAttrValue.getValue();
				}
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeredAtLocation().getStreetAddress()
						.setValue(location.getAddress1());
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeredAtLocation().getCity().setValue(location.getCityVillage());
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeredAtLocation().getStateOrProvince()
						.setValue(location.getStateProvince());
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeredAtLocation().getZipOrPostalCode()
						.setValue(location.getPostalCode());
				vxu.getORCRXARXROBXNTE(rep).getRXA()
						.getAdministeredAtLocation().getFacility()
						.getNamespaceID().setValue(facility);
				vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministeredAtLocation()
						.getPointOfCare().setValue(facilityCode);
			}
			
			
			//Enter current date as the system entry date
			vxu.getORCRXARXROBXNTE(rep).getRXA().getSystemEntryDateTime()
					.getTimeOfAnEvent().setValue(dateformat.format(new Date()));
			vxu.getORCRXARXROBXNTE(rep).getRXA().getSubstanceLotNumber(0)
					.setValue(lotNumber);

			if (lotNumber == null || lotNumber.equals("")) {
				// if no lot number it needs to be "historical" instead of
				// administered.
				vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministrationNotes(0)
						.getIdentifier().setValue("01");
				vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministrationNotes(0)
						.getText().setValue("Historical information - source unspecified");
				vxu.getORCRXARXROBXNTE(rep).getRXA().getAdministrationNotes(0)
						.getNameOfCodingSystem().setValue(
								"NIP001");
			}

			//action -- add, update, delete
			vxu.getORCRXARXROBXNTE(rep).getRXA().getActionCodeRXA().setValue(
					action);
			
			vxuString = getVXUMessageString(vxu);

			// vxu.getORCRXARXROBXNTE(rep).getRXA().get
		} catch (DataTypeException e) {
			log.error(Util.getStackTrace(e));
		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		}

		return vxuString;
	}

	public static String getAssigningAuthorityFromPatientIDType(
			PatientIdentifier pi) {
		String assignAuth = "";
		if (pi != null && pi.getIdentifierType() != null) {
			assignAuth = pi.getIdentifierType().getName();
			int underscore = assignAuth.indexOf('_');
			assignAuth = assignAuth.substring(underscore + 1);
		}
		return assignAuth;

	}

	public String getOurFacility() {
		return ourFacility;
	}

	public void setOurFacility(String ourFacility) {
		this.ourFacility = ourFacility;
	}

	public String getOurApplication() {
		return ourApplication;
	}

	public void setOurApplication(String ourApplication) {
		this.ourApplication = ourApplication;
	}

	public String getAttributeRace() {
		return attributeRace;
	}

	public void setAttributeRace(String attributeRace) {
		this.attributeRace = attributeRace;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getTriggerEvent() {
		return triggerEvent;
	}

	public void setTriggerEvent(String triggerEvent) {
		this.triggerEvent = triggerEvent;
	}

	public String getCodeSys() {
		return codeSys;
	}

	public void setCodeSys(String codeSys) {
		this.codeSys = codeSys;
	}

	public String getReceivingApp() {
		return receivingApp;
	}

	public void setReceivingApp(String receivingApp) {
		this.receivingApp = receivingApp;
	}

	public String getReceivingFacility() {
		return receivingFacility;
	}

	public void setReceivingFacility(String receivingFacility) {
		this.receivingFacility = receivingFacility;
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getAckType() {
		return ackType;
	}

	public void setAckType(String ackType) {
		this.ackType = ackType;
	}

	public String getCheckDigitScheme() {
		return checkDigitScheme;
	}

	public void setCheckDigitScheme(String checkDigitScheme) {
		this.checkDigitScheme = checkDigitScheme;
	}

	public String getPid2Required() {
		return pid2Required;
	}

	public void setPid2Required(String pid2Required) {
		this.pid2Required = pid2Required;
	}

	public String getAssignAuthority() {
		return assignAuthority;
	}

	public void setAssignAuthority(String assignAuthority) {
		this.assignAuthority = assignAuthority;
	}

	public String getIdentifierTypeCode() {
		return identifierTypeCode;
	}

	public void setIdentifierTypeCode(String identifierTypeCode) {
		this.identifierTypeCode = identifierTypeCode;
	}

	public String getApp_acknowledgement_type() {
		return app_acknowledgement_type;
	}

	public void setApp_acknowledgement_type(String appAcknowledgementType) {
		app_acknowledgement_type = appAcknowledgementType;
	}

	public String getProcessing_id() {
		return processing_id;
	}

	public void setProcessing_id(String processingId) {
		processing_id = processingId;
	}

	public String getObsLocation() {
		return obsLocation;
	}

	public void setObsLocation(String obsLocation) {
		this.obsLocation = obsLocation;
	}

	public String getPatientClass() {
		return patientClass;
	}

	public void setPatientClass(String patientClass) {
		this.patientClass = patientClass;
	}

	public String getUseNK() {
		return useNK;
	}

	public void setUseNK(String useNK) {
		this.useNK = useNK;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public void setInboundPort(String inboundPort) {
		this.inboundPort = inboundPort;
	}

}
