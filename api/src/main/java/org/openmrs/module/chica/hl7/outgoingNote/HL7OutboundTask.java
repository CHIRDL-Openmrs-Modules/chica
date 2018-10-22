package org.openmrs.module.chica.hl7.outgoingNote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
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
	private Integer sleepTime;
	private HL7OutboundHandler hl7OutboundHandler;
	private Thread hl7OutboundHandlerThread;
	private static final Integer DEFAULT_SOCKET_READ_TIMEOUT = 5; // seconds
	private static final Integer DEFAULT_THREAD_SLEEP_TIME = 1; // seconds
	
	@Override
	public void initialize(TaskDefinition config) 
	{
		this.log.info("Initializing " + this.getClass().getName() + "...");
		super.initialize(config);

		try 
		{
			String portString = this.taskDefinition.getProperty(ChirdlUtilConstants.TASK_PROPERTY_PORT);
			host = this.taskDefinition.getProperty(ChirdlUtilConstants.TASK_PROPERTY_HOST);
			String socketReadTimeoutString = this.taskDefinition.getProperty(ChirdlUtilConstants.TASK_PROPERTY_SOCKET_READ_TIMEOUT);
			String sleepTimeString = this.taskDefinition.getProperty(ChirdlUtilConstants.TASK_PROPERTY_THREAD_SLEEP_TIME);
			
			if (host == null)
			{
				log.error("Could not start " + this.getClass().getName() + ". Host has not been set.");
				return;
			}
			
			if(portString == null)
			{
				log.error("Could not start " + this.getClass().getName() + ". Port has not been set.");
				return;
			}

			if (portString != null)
			{
				port = Integer.parseInt(portString);
			} 
			
			if (socketReadTimeoutString != null && socketReadTimeoutString.length() > 0)
			{
				socketReadTimeout = Integer.parseInt(socketReadTimeoutString);
			} 
			else 
			{
				socketReadTimeout = DEFAULT_SOCKET_READ_TIMEOUT;
			}
			
			if (sleepTimeString != null && sleepTimeString.length() > 0)
			{
				sleepTime = Integer.parseInt(sleepTimeString);
			} 
			else 
			{
				sleepTime = DEFAULT_THREAD_SLEEP_TIME;
			}
			
		} 
		catch(Exception e)
		{
			log.error("Error starting " + this.getClass().getName() + "...", e);
			host = null;
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
			if(host != null && port != null)
			{
				log.error("Starting HL7OutboundHandler...");
				
				hl7OutboundHandler = new HL7OutboundHandler(host, port, socketReadTimeout, sleepTime);
				hl7OutboundHandlerThread = new Thread(hl7OutboundHandler);
				hl7OutboundHandlerThread.start();
				
				log.error("Finished starting HL7OutboundHandler.");
			}
			else
			{
				shutdown();
			}
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
		if(hl7OutboundHandler != null)
		{
			hl7OutboundHandler.setKeepRunning(false);
			try 
			{
				hl7OutboundHandlerThread.join(10000);
				
				if(hl7OutboundHandlerThread.isAlive())
				{
					log.error("Unable to stop HL7OutboundHandler.");
				}
			} 
			catch (InterruptedException e) 
			{
				log.error("Error occurred while stopping HL7OutboundHandler thread.", e);
				Thread.currentThread().interrupt();
			}
		}
		
		super.shutdown();
	}
}
