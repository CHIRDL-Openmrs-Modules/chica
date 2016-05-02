package org.openmrs.module.chica.hl7.outgoingNote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.sockethl7listener.HL7SocketHandler;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * DWE CHICA-636
 * Scheduled task to send sockethl7listener_hl7_out_queue records
 * Task will start a new thread that is used to query for records 
 * for the specified host and port (destination)
 */
public class HL7OutboundTask extends AbstractTask
{
	private Log log = LogFactory.getLog(this.getClass());
	private String host;
	private Integer port;
	private Integer socketReadTimeout;
	private HL7OutboundHandler hl7OutboundHandler;
	
	@Override
	public void initialize(TaskDefinition config) 
	{
		this.log.info("Initializing initializing " + this.getClass().getName() + "...");
		super.initialize(config);

		try 
		{
			String portString = this.taskDefinition.getProperty(ChirdlUtilConstants.TASK_PROPERTY_PORT);
			host = this.taskDefinition.getProperty(ChirdlUtilConstants.TASK_PROPERTY_HOST);
			String socketReadTimeoutString = this.taskDefinition.getProperty(ChirdlUtilConstants.TASK_PROPERTY_SOCKET_READ_TIMEOUT);

			if (host == null)
			{
				log.error("Could not start " + this.getClass().getName() + ". Host has not been set.");
				
				// TODO DWE CHICA-636 What happens here
				// call shutdown here?
				return;
			}
			
			if(portString == null)
			{
				log.error("Could not start " + this.getClass().getName() + ". Port has not been set.");
				
				// TODO DWE CHICA-636 What happens here
				// call shutdown here?
				return;
			}

			if (portString != null)
			{
				port = Integer.parseInt(portString);
			} 
			
			if (socketReadTimeoutString != null)
			{
				socketReadTimeout = Integer.parseInt(socketReadTimeoutString);
			} 
			else 
			{
				socketReadTimeout = 5; // seconds
			}
		} 
		catch(Exception e)
		{
			log.error("Error starting " + this.getClass().getName() + "...", e);
			
			// TODO DWE CHICA-636 What happens here
			// call shutdown here?
			return;
		}
		
		this.log.info("Finished initializing " + this.getClass().getName() + ".");
	}
	
	@Override
	public void execute() 
	{
		Context.openSession();
		try
		{
			log.error("Starting HL7OutboundHandler...");
			
			hl7OutboundHandler = new HL7OutboundHandler(host, port, socketReadTimeout);
			new Thread(hl7OutboundHandler).start();
			//Thread hl7OutboundThread = new Thread(hl7OutboundHandler);
			//hl7OutboundThread.start();
			
			log.error("Finished starting HL7OutboundHandler.");
		} 
		catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		} 
		finally
		{
			Context.closeSession();
		}
	}
	
	/**
	 * Close the socket
	 */
	@Override 
	public void shutdown()
	{
		//socketHandler.closeSocket();
		hl7OutboundHandler.setKeepRunning(false);
		super.shutdown();
	}
}