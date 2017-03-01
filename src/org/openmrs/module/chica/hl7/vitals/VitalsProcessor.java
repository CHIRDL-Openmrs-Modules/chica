/**
 * 
 */
package org.openmrs.module.chica.hl7.vitals;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

import ca.uhn.hl7v2.llp.LowerLayerProtocol;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * @author Tammy Dugan
 */
public class VitalsProcessor extends AbstractTask {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private VitalsHL7ListenerServer server = null;
	
	@Override
	public void initialize(TaskDefinition config) {
		this.log.info("Initializing VitalsHL7ListenerServer processor...");
		super.initialize(config);
		
		String portString = this.taskDefinition.getProperty("port");
		String source = this.taskDefinition.getProperty("source"); // DWE CHICA-635 Adding source so that this can be used to look up concepts using the concept_source and concept_map tables
		
		try {
			Integer port = null;
			
			try {
				port = Integer.parseInt(portString);
			}
			catch (NumberFormatException e) {
				this.log.error("Could not start VitalsHL7ListenerServer. Port " + portString + " could not be parsed");
				return;
			}
			
			if(source == null || source.isEmpty())
			{
				this.log.error("Could not start VitalsHL7ListenerServer. Source must be set.");
				return;
			}
			
			PipeParser parser = new PipeParser();
			parser.setValidationContext(new NoValidation());
			HL7SocketHandler socketHandler = new HL7SocketHandler(parser);
			socketHandler.setPort(Integer.valueOf(portString));
			socketHandler.setSource(source);
			this.server = new VitalsHL7ListenerServer(port, LowerLayerProtocol.makeLLP(), parser, socketHandler);
			log.info("Starting VitalsHL7ListenerServer...");
		}
		catch (Exception e) {
			log.error("Error starting VitalsHL7ListenerServer...");
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		this.log.info("Finished initializing VitalsHL7ListenerServer processor.");
	}
	
	@Override
	public void execute() {
		Context.openSession();
		try {
			this.server.start();
		}
		catch (Exception e) {
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		finally {
			Context.closeSession();
		}
	}
	
	@Override
	public void shutdown() {
		super.shutdown();
		try {
			if (this.server != null) {
				// CHICA-221 Use stopAndWait() instead of stop()
				// Openmrs added multiple calls to SchedulerUtil.startUp() which could potentially
				// allow multiple instances of this task to run at the same time or causes errors when starting
				// the second instance which would cause zero instances to be running since the first one will eventually shutdown
				// CHICA-961 Add shutdownNow() to allow the application to shutdown faster
				server.getExecutorService().shutdownNow();
				this.server.stopAndWait();
				
				// Adding this delay due to limitations with Hapi. Hapi does not call AcceptorThread.stopAndWait().
				// AcceptorThread.stop() is used which does not guarantee that the socket has had time to close
				Thread.sleep(3000);
			}
		}
		catch (Exception e) {
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}
}
