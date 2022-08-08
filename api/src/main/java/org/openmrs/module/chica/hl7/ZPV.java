package org.openmrs.module.chica.hl7;

import ca.uhn.hl7v2.parser.PipeParser;

public class ZPV
{

	private String printerLocation = null;

	public ZPV()
	{
        // This constructor is intentionally left empty.
	}

	/**
	 * Parses the zpv segment string into fields
	 * 
	 * @param zpvString
	 */
	public void parseFields(String zpvString)
	{

		String[] fields = PipeParser.split(zpvString, "|");
		if (fields != null)
		{
			int length = fields.length;
			this.printerLocation = (length >= 2) ? fields[1] : "";

		}
	}

	/**
	 * Gets the ZPV segment string from the incoming hl7 message string
	 * 
	 * @param mstring
	 * @return
	 */
	private String getZPVSegmentString(String mstring)
	{
		String ret = "";

		String[] segments = PipeParser.split(mstring, "\r");
		for (String s : segments)
		{
			if (s != null && s.startsWith("ZPV"))
			{
				ret = s;
			}
		}

		return ret;
	}

	/**
	 * Parses the ZPV segment string from the incoming message string, and
	 * inserts the information as ZPV segment into message object.
	 * 
	 * @param mstring
	 */
	public void loadZPVSegment(String mstring)
	{
		parseFields(getZPVSegmentString(mstring));
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
