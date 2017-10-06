package org.openmrs.module.chica.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.SslConfigurator;
import org.openmrs.Patient;
import org.openmrs.module.chica.study.dp3.login.LoginCredentials;
import org.openmrs.module.chica.study.dp3.login.LoginResponse;
import org.openmrs.module.chica.study.dp3.reading.GenericReading;
import org.openmrs.module.chica.study.dp3.reading.Readings;
import org.openmrs.module.chica.study.dp3.reading.ReadingsFactory;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;

/**
 * @author Dave Ely
 * Action class used to query Glooko for device readings
 */
public class QueryGlooko implements ProcessStateAction
{
	// This is expensive to create 
	// I'm not sure that we'll need to initialize with SSLContext if the Glooko server doesn't need our certificate
	private static Client client = initializeClient(); // ClientBuilder.newClient(); // Leave this commented out.
	
	// Resource targets
	private static final String LOGIN_WEB_TARGET = "/auth/login";
	private static final String READINGS_WEB_TARGET = "/external/readings";
	private static final String PUMP_READINGS_WEB_TARGET = "/pumps/readings";
	private static final String CGM_READINGS_WEB_TARGET = "/cgm/readings";
	
	// Data types
	private static final String READINGS_DATATYPE = "readings";
	private static final String PUMP_READINGS_DATATYPE = "pumps_readings";
	private static final String CGM_READINGS_DATATYPE = "cgm_readings";
	
	// Query parameter names
	private static final String PARAMETER_SYNC_TIMESTAMP = "";
	private static final String PARAMETER_PAGE_NUMBER = "";
	
	// HTTP headers
	private static final String CUSTOM_HTTP_HEADER_API_KEY = "x-api-key";
	private static final String CUSTOM_HTTP_HEADER_TOTAL_PAGES = "X-Total-Pages";
	private static final String CUSTOM_HTTP_HEADER_CURRENT_PAGE = "X-Current-Page";
	private static final String HTTP_AUTHORIZATION_TYPE = "Bearer ";
		
	/**
	 * Configure SSL and initialize the client
	 * @return
	 */
	private static Client initializeClient()
	{
		// Per documentation, clients are expensive to create and should only be created once
		// They are thread safe
		SslConfigurator sslConfig = SslConfigurator.newInstance()			        
										.keyStoreFile("pathToKeystore") // Global property
										.keyPassword("password") // Global property
										.securityProtocol("TLSv1.2"); // Global property
		 
		SSLContext sslContext = sslConfig.createSSLContext();
		Client client = ClientBuilder.newBuilder().sslContext(sslContext).build();
		return client;
	}
	
	/**
	 * @see org.openmrs.module.chirdlutilbackports.action.ProcessStateAction#changeState(org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	public void changeState(PatientState patientState, HashMap<String, Object> parameters) 
	{
		// Deliberately empty because processAction changes the state
	}

	@Override
	public void processAction(StateAction stateAction, Patient patient, PatientState patientState, HashMap<String, Object> parameters) 
	{
		Integer sessionId = patientState.getSessionId();
		State currState = patientState.getState();
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
			
		try
		{
			
			// Read global property to make sure the service is enabled
			
			
			
			String dataType = ""; // Get the data type from the parameters map
			
			// Get the implementation based on the dataType
			Readings readingsImpl = ReadingsFactory.getReadingsImpl(dataType);
			if(readingsImpl == null) // We received a device sync notification for an unsupported dataType
			{
				return;
			}
			
			// We could read this once when the class is initialized,
			// but might be better off to read each time 
			String rootWebTargetString = "http://localhost:10997"; // Read global property to get rootWebTarget
			
			String apiKey = ""; // Read global property to get apiKey
			
			String syncTimestamp = ""; // Get the sync timestamp from the parameters map
			String glookoCode = ""; // Get the glookoCode from the parameters map
			
			WebTarget rootWebTarget = client.target(rootWebTargetString);
			
			// TODO CHICA-1029 Can we login with each request
			// or do we need to keep track of the token expiration
			// as mentioned in the documentation (expiration is 1 hour)
			String token = loginAndGetToken(rootWebTarget);
			
			// Get the correct resource target based on the dataType
			WebTarget readingsTarget = getReadingsTargetByDataType(rootWebTarget, dataType);
			
			// Call the rest service
			Response response = updateQueryParametersGetResponse(glookoCode, syncTimestamp, 1, token, apiKey, readingsTarget);
			
			String totalPagesStr = response.getHeaderString(CUSTOM_HTTP_HEADER_TOTAL_PAGES);
			
			List<GenericReading> genericReadings = response.readEntity(readingsImpl.getClass())
														.getGenericReadingList();
			
			List<GenericReading> allReadings = new ArrayList<GenericReading>(); // Combined list from all pages
			allReadings.addAll(genericReadings);
			
			int totalPages = Integer.parseInt(totalPagesStr);
			if(totalPages > 1)
			{
				for(int i = 2; i <= totalPages; i++) // We already have page 1, start at page 2
				{
					genericReadings.clear();
					
					response = updateQueryParametersGetResponse(glookoCode, syncTimestamp, i, token, apiKey, readingsTarget);
					
					// Get current page from response just to be sure we are on the correct page
					int responsePageNum = Integer.parseInt(response.getHeaderString(CUSTOM_HTTP_HEADER_CURRENT_PAGE)); 
					if(i == responsePageNum)
					{
						genericReadings = response.readEntity(readingsImpl.getClass())
													.getGenericReadingList();
						allReadings.addAll(genericReadings);
					}
				}
			}
			
			
			// TODO CHICA-1029 What do we do from here?
			// Store in the cache? Or store in the DB?
		}
		catch(Exception e)
		{
			// Catch any errors that may occur including parseInt or service call
			// Don't do anything with the list if an error occurs at any point in time
			// Running rules against a partial list of readings could result in 
			// incorrect logic, just log it and change the patient's state
			// TODO CHICA-1029 Log the error
		}
		finally
		{
			StateManager.endState(patientState);
			
			BaseStateActionHandler
			        .changeState(patient, sessionId, currState, stateAction, parameters, locationTagId, locationId);
		}
	}
	
	/**
	 * Login and get the token from Glooko
	 * @param rootWebTarget
	 * @return
	 */
	private String loginAndGetToken(WebTarget rootWebTarget)
	{
		String username = ""; // Read global property to get username
		String password = ""; // Read global property to get password
		
		WebTarget loginTarget = rootWebTarget.path(LOGIN_WEB_TARGET);
		
		LoginCredentials loginCredentials = new LoginCredentials(username, password);
		
		LoginResponse loginResponse = loginTarget.request(MediaType.APPLICATION_JSON)
											.post(Entity.json(loginCredentials), LoginResponse.class);
		
		return loginResponse.getToken(); // TODO CHICA-1029 determine if this can be set at class level with a 1 hour expiration
	}
	
	/**
	 * Create the WebTarget for the readings endpoint based on the dataType from the sync notification
	 * @param rootWebTarget
	 * @param dataType
	 * @return WebTarget
	 */
	private WebTarget getReadingsTargetByDataType(WebTarget rootWebTarget, String dataType)
	{
		// Determine the resource target based on the data type from the sync notification
		WebTarget readingsTarget = rootWebTarget.path("");
		switch(dataType)
		{
			case READINGS_DATATYPE:
				readingsTarget = rootWebTarget.path(READINGS_WEB_TARGET);
				break;
			case PUMP_READINGS_DATATYPE:
				readingsTarget = rootWebTarget.path(PUMP_READINGS_WEB_TARGET);
				break;
			case CGM_READINGS_DATATYPE:
				readingsTarget = rootWebTarget.path(CGM_READINGS_WEB_TARGET);
				break;
			default:
				// leave it empty
				// We shouldn't even get to this method call unless a new class has been added
				// that implements the Readings interface, in which case the dataType needs to be
				// added above
				break;
		}
		
		return readingsTarget;
	}
	
	/**
	 * Calls the rest service - updates parameters before the call
	 * @param glookoCode
	 * @param syncTimestamp
	 * @param pageNumber
	 * @param token
	 * @param apiKey
	 * @param readingsTarget
	 * @return Response object
	 */
	private Response updateQueryParametersGetResponse(String glookoCode, String syncTimestamp, int pageNumber, String token, String apiKey, WebTarget readingsTarget)
	{
		// Add query parameters
		WebTarget readingsTargetWithParams = readingsTarget.queryParam(ChirdlUtilConstants.PARAMETER_PATIENT, glookoCode) // patient's glooko code
													.queryParam(PARAMETER_SYNC_TIMESTAMP, syncTimestamp) // Sync timestamp received in the device synce notification
													.queryParam(PARAMETER_PAGE_NUMBER, pageNumber);
					
		// Call the rest service
		Response response = readingsTargetWithParams.request(MediaType.APPLICATION_JSON)
							.header(ChirdlUtilConstants.HTTP_AUTHORIZATION_HEADER, HTTP_AUTHORIZATION_TYPE + token) // Token from login
							.header(CUSTOM_HTTP_HEADER_API_KEY, apiKey)
							.get();
		
		return response;
	}
	
	public static void main(String[] args)
	{
		String apiKey = "testAPIKey1234"; // TODO CHICA-1029  this will likely be a global property. It is provided by Glooko after we create an account
		
		Client client = ClientBuilder.newClient();
		
		// TODO CHICA-1029  Meter readings base target - real world would be the same base target as above
		WebTarget baseTarget2 = client.target("http://localhost:10997");
		WebTarget meterReadingsTarget = baseTarget2.path("/external/readings");
		
		// Add the patient and the syncTimestamp as url query parameters
		WebTarget meterReadingsTargetWithParams = meterReadingsTarget.queryParam("patient", "Jenny")
													.queryParam("syncTimestamp", "2017-09-28T14:07:54:472Z")
													.queryParam("pageNumber", 1);
		
		
		Response response = meterReadingsTargetWithParams.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + "")
				.header("x-api-key", apiKey)
				.get();
		
		Readings readingsImpl = ReadingsFactory.getReadingsImpl(READINGS_DATATYPE);
		if(readingsImpl == null) // We received a device sync notification for an unsupported dataType
		{
			return;
		}
		
		
		List<GenericReading> genericReadings = response.readEntity(readingsImpl.getClass())
													.getGenericReadingList();
		
		System.out.println("Success");
	}
}
