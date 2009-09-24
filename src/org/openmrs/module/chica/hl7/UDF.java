package org.openmrs.module.chica.hl7;

import ca.uhn.hl7v2.parser.PipeParser;

public class UDF
{

	private String printerLocation = null;

	public UDF()
	{
	}

	/**
	 * Parses the udf segment string into fields
	 * 
	 * @param udfString
	 */
	public void parseFields(String udfString)
	{

		String[] fields = PipeParser.split(udfString, "|");
		if (fields != null)
		{
			int length = fields.length;
			this.printerLocation = (length >= 2) ? fields[1] : "";

		}
	}

	/**
	 * Gets the UDF segment string from the incoming hl7 message string
	 * 
	 * @param mstring
	 * @return
	 */
	private String getUDFSegmentString(String mstring)
	{
		String ret = "";

		String[] segments = PipeParser.split(mstring, "\r");
		for (String s : segments)
		{
			if (s != null && s.startsWith("UDF"))
			{
				ret = s;
			}
		}

		return ret;
	}

	/**
	 * Parses the UDF segment string from the incoming message string, and
	 * inserts the information as UDF segment into message object.
	 * 
	 * @param mstring
	 */
	public void loadUDFSegment(String mstring)
	{
		parseFields(getUDFSegmentString(mstring));
	}

	/**
	 * @return the printerLocation
	 */
	public String getPrinterLocation()
	{
		return this.printerLocation;
	}

	/**
	 * @param printerLocation the printerLocation to set
	 */
	public void setPrinterLocation(String printerLocation)
	{
		this.printerLocation = printerLocation;
	}
	
}
