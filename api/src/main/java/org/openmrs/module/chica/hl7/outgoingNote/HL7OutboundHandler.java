package org.openmrs.module.chica.hl7.outgoingNote;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.sockethl7listener.hibernateBeans.HL7Outbound;
import org.openmrs.module.sockethl7listener.service.SocketHL7ListenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DWE CHICA-636
 * Scheduled task to send sockethl7listener_hl7_out_queue records
 * Task will query for records for the specified host and port (destination)
 */
public class HL7OutboundHandler implements Runnable
{
	private static final Logger log = LoggerFactory.getLogger(HL7OutboundHandler.class);
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
		try
		{	
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
				
				Thread.sleep(this.sleepTime * 1000L);
			}
		}
		catch(ContextAuthenticationException e)
		{
			log.error("Error authenticating context.", e);
		}
		catch (InterruptedException e) {
			log.error("HL7OutboundHandler thread interrupted.", e);
			Thread.currentThread().interrupt();
		}
		catch(Exception e)
		{
			log.error("Error in {}.", this.getClass().getName(), e);
		}
		finally
		{
			log.error("Shutting down {}.", this.getClass().getName());
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
			return socketHL7ListenerService.getPendingHL7OutboundByHostAndPort(this.host, this.port);
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
			if (this.os != null)
			{
				this.os.write(ChirdlUtilConstants.HL7_START_OF_MESSAGE.getBytes() );
				this.os.write(hl7Outbound.getHl7Message().getBytes());
				this.os.write(ChirdlUtilConstants.HL7_END_OF_MESSGAE.getBytes() );
				this.os.write(13);
				this.os.flush();
			}

			this.socket.setSoTimeout(this.socketReadTimeout * 1000);
		}
		catch(Exception e)
		{
			log.error("Error sending HL7Outbound message HL7OutQueueId: {}", hl7Outbound.getHL7OutQueueId(), e);
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
		return this.keepRunning;
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
			this.socket = new Socket(this.host, this.port);
			this.socket.setSoLinger(true, 10000);

			this.os = this.socket.getOutputStream();
			this.is = this.socket.getInputStream();

		} 
		catch (Exception e) 
		{
			log.error("Open socket failed: ", e);
		}
	} 
	
	/**
	 * Close the socket
	 */
	 private void closeSocket() 
	 {
		 try 
		 {
			 Socket sckt = this.socket;
			 this.socket = null;
			 this.os.close();
			 this.is.close();

			 if (sckt != null)
			 {
				 sckt.close();
			 }
		 }
		 catch (Exception e) 
		 {
			 log.error("Error closing socket: ", e);
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
			 StringBuilder stringbuffer = new StringBuilder();
			 int i = 0;
			 while(i != 28)
			 {
				 i = this.is.read();
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
