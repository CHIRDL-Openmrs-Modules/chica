package org.openmrs.module.chica;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.ChicaError;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.dss.util.IOUtil;

public class KiteMessageHandler
{

	private static final String HL7_START_OF_MESSAGE = "\u000b";
	private static final String HL7_END_OF_MESSAGE = "\u001c";

	protected final Log log = LogFactory.getLog(getClass());
	
	private String host = null;
	private int port = 0;
	private Socket socket = null;
	private OutputStream os = null;
	private InputStream is = null;
	private int timeout = 0;

	public KiteMessageHandler(String host, int port, int timeout)
	{
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	/*
	 * 
	 */
	public void openSocket() throws IOException
	{
		this.socket = new Socket(this.host, this.port);
		this.socket.setSoTimeout(this.timeout);

		this.os = this.socket.getOutputStream();
		this.is = this.socket.getInputStream();
	}

	public void closeSocket()
	{
		try
		{
			Socket sckt = this.socket;
			this.socket = null;
			if (sckt != null)
				sckt.close();
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
		}
	}

	public void sendMessage(String theMessage) throws IOException
	{
		this.os.write(theMessage.getBytes());
		this.os.write(13);
		this.os.flush();
	}

	public String getMessage() throws Exception
	{
		ChicaService chicaService = Context.getService(ChicaService.class);
		ByteArrayOutputStream outputString = new ByteArrayOutputStream();

		try
		{
			IOUtil.bufferedReadWrite(this.is, outputString,1);
			outputString.flush();
			outputString.close();
		} catch (Exception e)
		{	
			outputString.flush();
			outputString.close();
			ChicaError error = new ChicaError("Error","Query Kite Connection"
					, "Message dropped"
					, "Partial message: "+outputString.toString(), new Date(), null);
			chicaService.saveError(error);
			return null;
		}
		return outputString.toString();
	}

}
