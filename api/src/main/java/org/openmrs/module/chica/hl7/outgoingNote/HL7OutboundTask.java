package org.openmrs.module.chica.hl7.outgoingNote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.atd.util.Util;
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
			this.host = this.taskDefinition.getProperty(ChirdlUtilConstants.TASK_PROPERTY_HOST);
			String socketReadTimeoutString = this.taskDefinition.getProperty(ChirdlUtilConstants.TASK_PROPERTY_SOCKET_READ_TIMEOUT);
			String sleepTimeString = this.taskDefinition.getProperty(ChirdlUtilConstants.TASK_PROPERTY_THREAD_SLEEP_TIME);
			
			if (this.host == null)
			{
				this.log.error("Could not start " + this.getClass().getName() + ". Host has not been set.");
				return;
			}
			
			if(portString == null)
			{
				this.log.error("Could not start " + this.getClass().getName() + ". Port has not been set.");
				return;
			}

			if (portString != null)
			{
				this.port = Integer.valueOf(portString);
			} 
			
			if (socketReadTimeoutString != null && socketReadTimeoutString.length() > 0)
			{
				this.socketReadTimeout = Integer.valueOf(socketReadTimeoutString);
			} 
			else 
			{
				this.socketReadTimeout = DEFAULT_SOCKET_READ_TIMEOUT;
			}
			
			if (sleepTimeString != null && sleepTimeString.length() > 0)
			{
				this.sleepTime = Integer.valueOf(sleepTimeString);
			} 
			else 
			{
				this.sleepTime = DEFAULT_THREAD_SLEEP_TIME;
			}
			
		} 
		catch(Exception e)
		{
			this.log.error("Error starting " + this.getClass().getName() + "...", e);
			this.host = null;
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
			if(this.host != null && this.port != null)
			{
				this.log.error("Starting HL7OutboundHandler...");
				
				this.hl7OutboundHandler = new HL7OutboundHandler(this.host, this.port, this.socketReadTimeout, this.sleepTime);
				this.hl7OutboundHandlerThread = Daemon.runInDaemonThread(this.hl7OutboundHandler, Util.getDaemonToken());
				
				this.log.error("Finished starting HL7OutboundHandler.");
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
		if(this.hl7OutboundHandler != null)
		{
			this.hl7OutboundHandler.setKeepRunning(false);
			try 
			{
				this.hl7OutboundHandlerThread.join(10000);
				
				if(this.hl7OutboundHandlerThread.isAlive())
				{
					this.log.error("Unable to stop HL7OutboundHandler.");
				}
			} 
			catch (InterruptedException e) 
			{
				this.log.error("Error occurred while stopping HL7OutboundHandler thread.", e);
				Thread.currentThread().interrupt();
			}
		}
		
		super.shutdown();
	}
}
