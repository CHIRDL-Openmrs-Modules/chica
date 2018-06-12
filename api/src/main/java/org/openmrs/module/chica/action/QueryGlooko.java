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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.SslConfigurator;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.study.dp3.GlookoConstants;
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
import org.springframework.http.HttpStatus;

/**
 * @author Dave Ely
 * Action class used to query Glooko for device readings
 */
public class QueryGlooko implements ProcessStateAction
{
	private Log log = LogFactory.getLog(this.getClass());
	
	// This is expensive to create 
	// I'm not sure that we'll need to initialize with SSLContext if the Glooko server doesn't need our certificate
	private static Client client = ClientBuilder.newClient(); // initializeClient(); // Leave this commented out.
	
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
	private static final String PARAMETER_PAGE_NUMBER = "pageNumber";
	
	// HTTP headers
	private static final String CUSTOM_HTTP_HEADER_API_KEY = "x-api-key";
	private static final String CUSTOM_HTTP_HEADER_TOTAL_PAGES = "X-Total-Pages";
	private static final String CUSTOM_HTTP_HEADER_CURRENT_PAGE = "X-Current-Page";
	private static final String HTTP_AUTHORIZATION_TYPE = "Bearer ";
	
	// Global properties
	private static final String GLOBAL_PROP_ENABLE_GLOOKO_QUERY = "chica.enableGlookoQuery";
	private static final String GLOBAL_PROP_GLOOKO_TARGET_ENDPOINT = "chica.GlookoQueryTargetEndpoint";
	private static final String GLOBAL_PROP_GLOOKO_API_KEY = "chica.GlookoAPIKey";
	private static final String GLOBAL_PROP_GLOOKO_USERNAME = "chica.GlookoUsername";
	private static final String GLOBAL_PROP_GLOOKO_PASS = "chica.GlookoPassword";
		
	/**
	 * TODO CHICA-1029 This method is not currently used. I'm not sure yet if it is needed.
	 * The client will use SSL without this method, but won't include the certificate
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
		
		boolean changeState = false; // Use this flag to determine if the state should be changed, we only want to change the state if no errors occurred so that we don't create a PWS for no reason
		try
		{
			AdministrationService adminService = Context.getAdministrationService();
			String enableQuery = adminService.getGlobalProperty(GLOBAL_PROP_ENABLE_GLOOKO_QUERY);
			if(StringUtils.isBlank(enableQuery) || ChirdlUtilConstants.GENERAL_INFO_FALSE.equalsIgnoreCase(enableQuery))
			{
				log.error("Glooko query is not enabled and will not be completed for patient: " + patient.getPatientId());
			}
			
			String rootWebTargetString = adminService.getGlobalProperty(GLOBAL_PROP_GLOOKO_TARGET_ENDPOINT); // Read global property to get rootWebTarget
			if(StringUtils.isBlank(rootWebTargetString))
			{
				log.error(GLOBAL_PROP_GLOOKO_TARGET_ENDPOINT + " is not valid. Glooko query will not be performed for patient: " + patient.getPatientId());
				enableQuery = ChirdlUtilConstants.GENERAL_INFO_FALSE;
			}
			
			String apiKey = adminService.getGlobalProperty(GLOBAL_PROP_GLOOKO_API_KEY); // Read global property to get apiKey
			if(StringUtils.isBlank(apiKey))
			{
				log.error(GLOBAL_PROP_GLOOKO_API_KEY + " is not valid. Glooko query will not be performed for patient: " + patient.getPatientId());
				enableQuery = ChirdlUtilConstants.GENERAL_INFO_FALSE;
			}
			
			String username = adminService.getGlobalProperty(GLOBAL_PROP_GLOOKO_USERNAME); // Read global property to get username
			if(StringUtils.isBlank(username))
			{
				log.error(GLOBAL_PROP_GLOOKO_USERNAME + " is not valid. Glooko query will not be performed for patient: " + patient.getPatientId());
				enableQuery = ChirdlUtilConstants.GENERAL_INFO_FALSE;
			}
			
			String password = adminService.getGlobalProperty(GLOBAL_PROP_GLOOKO_PASS); // Read global property to get password
			if(StringUtils.isBlank(password))
			{
				log.error(GLOBAL_PROP_GLOOKO_PASS + " is not valid. Glooko query will not be performed for patient: " + patient.getPatientId());
				enableQuery = ChirdlUtilConstants.GENERAL_INFO_FALSE;
			}
			
			// Check the global property to make sure the service is enabled
			if(ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(enableQuery))
			{
				String dataType = (String)parameters.get(GlookoConstants.PARAMETER_DATA_TYPE); // Get the data type from the parameters map
				
				// Get the implementation based on the dataType
				Readings readingsImpl = ReadingsFactory.getReadingsImpl(dataType);
				if(readingsImpl != null) // If readingsImpl is null, we received a device sync notification for an unsupported dataType
				{
					String syncTimestamp = (String)parameters.get(GlookoConstants.PARAMETER_SYNC_TIMESTAMP); // Get the sync timestamp from the parameters map
					String glookoCode = (String)parameters.get(GlookoConstants.PARAMETER_GLOOKO_CODE); // Get the glookoCode from the parameters map
					
					WebTarget rootWebTarget = client.target(rootWebTargetString);
					
					// TODO CHICA-1029 Can we login with each request
					// or do we need to keep track of the token expiration
					// as mentioned in the documentation (expiration is 1 hour)
					String token = loginAndGetToken(rootWebTarget, username, password);
					
					// Get the correct resource target based on the dataType
					WebTarget readingsTarget = getReadingsTargetByDataType(rootWebTarget, dataType);
					
					// Call the rest service
					Response response = updateQueryParametersGetResponse(glookoCode, syncTimestamp, 1, token, apiKey, readingsTarget);
					
					if(response.getStatus() == HttpStatus.OK.value())
					{
						String totalPagesStr = response.getHeaderString(CUSTOM_HTTP_HEADER_TOTAL_PAGES);
						
						List<GenericReading> genericReadings = response.readEntity(readingsImpl.getClass())
																	.getGenericReadingList();
					
						List<GenericReading> allReadings = new ArrayList<GenericReading>(); // Combined list of readings from all pages
						allReadings.addAll(genericReadings);
						
						int totalPages = Integer.parseInt(totalPagesStr);
						if(totalPages > 1)
						{
							for(int i = 2; i <= totalPages; i++) // We already have page 1, start at page 2
							{
								genericReadings.clear();
								
								// TODO CHICA-1029 If we need to handle token expiration, 
								// we'll need to check that it isn't about to expire before making this call
								response = updateQueryParametersGetResponse(glookoCode, syncTimestamp, i, token, apiKey, readingsTarget);
								
								if(response.getStatus() == HttpStatus.OK.value())
								{
									// Get current page from response just to be sure we are on the correct page
									int responsePageNum = Integer.parseInt(response.getHeaderString(CUSTOM_HTTP_HEADER_CURRENT_PAGE)); 
									if(i == responsePageNum)
									{
										genericReadings = response.readEntity(readingsImpl.getClass())
																	.getGenericReadingList();
										allReadings.addAll(genericReadings);
									}
								}
								else
								{
									// Don't try to read anymore pages. We don't want to end up with a partial list of readings
									log.error("An error occurred while querying for device data. ResponseStatus: " + response.getStatus());
									allReadings.clear();
									break;
								}
							}
						}
						
						// TODO CHICA-1029 What do we do from here?
						// Store in the cache? Or store in the DB?
						changeState = true;
					}
					else
					{
						log.error("An error occurred while querying for device data for patient: " + patient.getPatientId() + ". ResponseStatus: " + response.getStatus());
					}
				}	
			}
		}
		catch(Exception e)
		{
			// Catch any errors that may occur including parseInt, parsing errors with the response, or service call errors
			// Don't do anything with the list if an error occurs at any point in time
			// Running rules against a partial list of readings could result in 
			// incorrect logic, just log it and change the patient's state
			log.error("An error occurred while querying for device data for patient: " + patient.getPatientId(), e);
			
		}
		finally
		{
			// End the state even if something fails during the query but don't change the state so a PWS doesn't get created, the PWS can still get created by submitting the PSF or vitals
			StateManager.endState(patientState);
			
			if(changeState)
			{
				BaseStateActionHandler
		        .changeState(patient, sessionId, currState, stateAction, parameters, locationTagId, locationId);
			}
		}
	}
	
	/**
	 * Login and get the token from Glooko
	 * @param rootWebTarget
	 * @return
	 */
	private String loginAndGetToken(WebTarget rootWebTarget, String username, String password)
	{
		WebTarget loginTarget = rootWebTarget.path(LOGIN_WEB_TARGET);
		
		LoginCredentials loginCredentials = new LoginCredentials(username, password);
		
		LoginResponse loginResponse = loginTarget.request(MediaType.APPLICATION_JSON)
											.post(Entity.json(loginCredentials), LoginResponse.class);
		
		return loginResponse.getToken();
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
		WebTarget readingsTarget = rootWebTarget.path(ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING);
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
													.queryParam(GlookoConstants.PARAMETER_SYNC_TIMESTAMP, syncTimestamp) // Sync timestamp received in the device synce notification
													.queryParam(PARAMETER_PAGE_NUMBER, pageNumber);
					
		// Call the rest service
		Response response = readingsTargetWithParams.request(MediaType.APPLICATION_JSON)
							.header(ChirdlUtilConstants.HTTP_AUTHORIZATION_HEADER, HTTP_AUTHORIZATION_TYPE + token) // Token from login
							.header(CUSTOM_HTTP_HEADER_API_KEY, apiKey)
							.get();
		
		return response;
	}
}
