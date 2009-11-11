/**
 * 
 */
package org.openmrs.module.chica.hl7.mckesson;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hl7.PrinterLocationHL7Filter;
import org.openmrs.module.sockethl7listener.HL7Filter;
import org.openmrs.module.sockethl7listener.HL7ObsHandler25;
import org.openmrs.module.sockethl7listener.SimpleServer;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

import ca.uhn.hl7v2.llp.LowerLayerProtocol;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * @author Tammy Dugan
 * 
 */
public class CheckinProcessor extends AbstractTask
{
	private Log log = LogFactory.getLog(this.getClass());
	private TaskDefinition taskConfig;
	private SimpleServer server = null;
	
	@Override
	public void initialize(TaskDefinition config)
	{
		this.log.info("Initializing McKesson checkin processor...");
		AdministrationService adminService = Context.getAdministrationService();

		this.taskConfig = config;

		String portString = this.taskConfig.getProperty("port");

		if (portString == null)
		{
			portString = adminService
					.getGlobalProperty("sockethl7listener.port");
		}

		try
		{
			Integer port = null;
			
			try
			{
				port = Integer.parseInt(portString);
			} catch (NumberFormatException e)
			{
				this.log.error("Could not start SimpleServer. Port "+portString+" could not be parsed");
				return;
			}
			PipeParser parser = new PipeParser();
			parser.setValidationContext(new NoValidation());
			PatientHandler patientHandler = new PatientHandler();
			PrinterLocationHL7Filter printerLocationHL7Filter = new PrinterLocationHL7Filter();
			ArrayList<HL7Filter> filters = new ArrayList<HL7Filter>();
			filters.add(printerLocationHL7Filter);
			HL7SocketHandler socketHandler = new HL7SocketHandler(parser,
					patientHandler, new HL7ObsHandler25(), new HL7EncounterHandler25(),
					new HL7PatientHandler25(),filters);
			socketHandler.setPort(Integer.valueOf(portString));
			this.server = new SimpleServer(port, LowerLayerProtocol
					.makeLLP(),parser,patientHandler, socketHandler);
			log.info("Starting SimpleServer...");
		} catch (Exception e)
		{
			log.error("Error starting SimpleServer...");
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		this.log.info("Finished initializing McKesson checkin processor.");
	}

	@Override
	public void execute()
	{
		Context.openSession();
		try
		{
			if (Context.isAuthenticated() == false)
				authenticate();
			this.server.start();
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		} finally
		{
			Context.closeSession();
		}
	}

	@Override
	public void shutdown()
	{
		super.shutdown();
		try
		{
			if(this.server != null){
				this.server.stop();
			}
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}
}
