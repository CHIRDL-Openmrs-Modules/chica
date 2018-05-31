package org.openmrs.module.chica.hl7.outgoingNote;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.sockethl7listener.hibernateBeans.HL7Outbound;
import org.openmrs.module.sockethl7listener.service.SocketHL7ListenerService;


/**
 * DWE CHICA-636
 * Scheduled task to send sockethl7listener_hl7_out_queue records
 * Task will query for records for the specified host and port (destination)
 */
public class HL7OutboundHandler implements Runnable
{
	private Log log = LogFactory.getLog(this.getClass());
	private String host;
	private Integer port;
	private Integer socketReadTimeout;
	private Integer sleepTime;
	private boolean keepRunning = true;
	private Socket socket;
	private OutputStream os = null;
	private InputStream is = null;
	
	public HL7OutboundHandler(String host, Integer port, Integer socketReadTimeout, Integer sleepTime)
	{
		this.host = host;
		this.port = port;
		this.socketReadTimeout = socketReadTimeout;
		this.sleepTime = sleepTime;
	}
	
	@Override
	public void run()
	{
		Context.openSession();
		try
		{	
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME),
					adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSPHRASE));

			SocketHL7ListenerService socketHL7ListenerService = Context.getService(SocketHL7ListenerService.class);

			while (keepRunning())
			{
				// Get the list of pending records to send
				List<HL7Outbound> hl7OutboundList = getPendingHL7OutboundRecords(socketHL7ListenerService);
				
				// Send the records
				if(hl7OutboundList != null && hl7OutboundList.size() > 0)
				{
					openSocket();
					for(HL7Outbound hl7Outbound : hl7OutboundList)
					{
						sendMessage(hl7Outbound, socketHL7ListenerService);
					}
					closeSocket();
				}
				
				Thread.sleep(sleepTime * 1000);
			}
		}
		catch(ContextAuthenticationException e)
		{
			log.error("Error authenticating context.", e);
		}
		catch(Exception e)
		{
			log.error("Error in " + this.getClass().getName() + ".", e);
		}
		finally
		{
			Context.closeSession();
			log.error("Shutting down " + this.getClass().getName() + ".");
		}	
	}
	
	/**
	 * Get the list of pending HL7Outbound records
	 * @param socketHL7ListenerService
	 * @return
	 */
	private List<HL7Outbound> getPendingHL7OutboundRecords(SocketHL7ListenerService socketHL7ListenerService)
	{
		try
		{
			return socketHL7ListenerService.getPendingHL7OutboundByHostAndPort(host, port);
		}
		catch(Exception e)
		{
			log.error("Error getting pending HL7Outbound records.", e);
		}
		
		return null;
	}
	
	/**
	 * Sends the message and updates the ack_received field if it was successfully sent
	 * @param hl7Outbound
	 * @param socketHL7ListenerService
	 */
	private void sendMessage(HL7Outbound hl7Outbound, SocketHL7ListenerService socketHL7ListenerService)
	{
		try
		{
			if (os != null)
			{
				os.write(ChirdlUtilConstants.HL7_START_OF_MESSAGE.getBytes() );
				os.write(hl7Outbound.getHl7Message().getBytes());
				os.write(ChirdlUtilConstants.HL7_END_OF_MESSGAE.getBytes() );
				os.write(13);
				os.flush();
			}

			socket.setSoTimeout(socketReadTimeout * 1000);
		}
		catch(Exception e)
		{
			log.error("Error sending HL7Outbound message HL7OutQueueId: " + hl7Outbound.getHL7OutQueueId(), e);
			return;
		}

		String ack = readAck();
		if (ack != null)
		{ 
			Date ackDate = null;
			ackDate = new Date();
			hl7Outbound.setAckReceived(ackDate);
			socketHL7ListenerService.saveMessageToDatabase(hl7Outbound);
		}
	}
	
	/**
	 * @return keepRunning
	 */
	private boolean keepRunning()
	{
		return keepRunning;
	}
	
	/**
	 * @param keepRunning
	 */
	protected void setKeepRunning(boolean keepRunning)
	{
		this.keepRunning = keepRunning;
	}
	
	/**
	 * Open socket for host and port
	 */
	private void openSocket()
	{
		try 
		{
			socket = new Socket(host, port);
			socket.setSoLinger(true, 10000);

			os = socket.getOutputStream();
			is = socket.getInputStream();

		} 
		catch (Exception e) 
		{
			log.error("Open socket failed: " + e.getMessage());
		}
	} 
	
	/**
	 * Close the socket
	 */
	 private void closeSocket() 
	 {
		 try 
		 {
			 Socket sckt = socket;
			 socket = null;
			 os.close();
			 is.close();

			 if (sckt != null)
			 {
				 sckt.close();
			 }
		 }
		 catch (Exception e) 
		 {
			 log.error("Error closing socket: " + e.getMessage());
		 }
	 }
	 
	 /**
	  * Read ACK from the input stream
	  * @return
	  */
	 private String readAck()
	 {
		 try
		 {
			 StringBuffer stringbuffer = new StringBuffer();
			 int i = 0;
			 while(i != 28)
			 {
				 i = is.read();
				 if (i == -1)
				 {
					 return null;
				 }

				 stringbuffer.append((char) i);
			 }       
			 return stringbuffer.toString();
		 } 
		 catch (Exception e) 
		 {
			 log.error("Error during readAck", e);
		 }
		 
		 return null;
	 }
}
