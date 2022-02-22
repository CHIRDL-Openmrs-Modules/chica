package org.openmrs.module.chica.hl7.vitals;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.chica.hl7.mrfdump.HL7ObsHandler23;
import org.openmrs.module.chirdlutil.threadmgmt.RunnableResult;
import org.openmrs.module.sockethl7listener.HL7ObsHandler25;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;

/**
 * Runnable to process vitals HL7 message.
 * 
 * @author Steve McKee
 */
public class ProcessMessageRunnable implements RunnableResult<Message> {
	
	private static final Logger log = LoggerFactory.getLogger(ProcessMessageRunnable.class);
	private Message message;
	private Message response;
	private HL7SocketHandler socketHandler;
	private Parser parser;
	private Exception exception;
	
	/**
	 * Constructor method
	 * 
	 * @param message The HL7 message to process
	 * @param socketHandler The socket handler used to process the message
	 * @param parser The parser used to parse the message
	 */
	public ProcessMessageRunnable(Message message, HL7SocketHandler socketHandler, Parser parser) {
		this.message = message;
		this.socketHandler = socketHandler;
		this.parser = parser;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		boolean error = false;
		try {
			Date startTime = Calendar.getInstance().getTime();
			if (this.socketHandler.canProcess(this.message)) {
				String incomingMessageString = "";
				
				incomingMessageString = this.parser.encode(this.message);
				error = this.socketHandler.processMessageSegments(this.message, incomingMessageString, startTime);
			}
			try {
				if (this.message instanceof ca.uhn.hl7v2.model.v25.message.ORU_R01
				        || this.message instanceof ca.uhn.hl7v2.model.v25.message.ADT_A01) {
					ca.uhn.hl7v2.model.v25.segment.MSH msh = HL7ObsHandler25.getMSH(this.message);
					this.response = org.openmrs.module.sockethl7listener.util.Util.makeACK(msh, error, null, null);
				} else if (this.message instanceof ca.uhn.hl7v2.model.v23.message.ORU_R01) {
					ca.uhn.hl7v2.model.v23.segment.MSH msh = HL7ObsHandler23
					        .getMSH((ca.uhn.hl7v2.model.v23.message.ORU_R01) this.message);
					this.response = org.openmrs.module.sockethl7listener.util.Util.makeACK(msh, error, null, null);
				} else {
					this.response = null;
				}
				
			}
			catch (IOException e) {
				log.error("Error creating ACK message." + e.getMessage());
				this.exception = e;
			}
			catch (HL7Exception e) {
				log.error("Parser error constructing ACK.", e);
				this.exception = e;
			}
			catch (Exception e) {
				log.error("Exception processing inbound vitals HL7 message.", e);
				this.exception = e;
			}
			
			Context.clearSession();
		}
		catch (ContextAuthenticationException e) {
			log.error("Context Authentication exception: ", e);
			this.exception = e;
		}
		catch (ClassCastException e) {
			log.error("Error casting to " + this.message.getClass().getName() + " ", e);
			this.exception = new ApplicationException("Invalid message type for handler");
		}
		catch (HL7Exception e) {
			log.error("Error while processing hl7 message", e);
			this.exception = new ApplicationException(e);
		}
		finally {
			if (this.response == null) {
				try {
					error = true;
					ca.uhn.hl7v2.model.v25.segment.MSH msh = HL7ObsHandler25.getMSH(this.message);
					this.response = org.openmrs.module.sockethl7listener.util.Util.makeACK(msh, error, null, null);
				}
				catch (Exception e) {
					log.error("Could not send acknowledgement", e);
					this.exception = e;
				}
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.RunnableResult#getResult()
	 */
	@Override
	public Message getResult() {
		return this.response;
	}
	
	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.RunnableResult#getException()
	 */
	@Override
	public Exception getException() {
		return this.exception;
	}
}
