/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.gis;

/**
 * Contains information about a geocoded location.
 *
 * @author Steve McKee
 */
public class Placemark {

	public int rank;
	public int ID;
	public String OrgName;
	public Float Mi;
	public String stAdd;
	public String City;
	public String Zip;
	public String state;
	public String address;
	public String Category;
	public String Type;
	public String Cost;
	public String CostDetails;
	public Float Latitude;
	public Float Longitude;
	public String Phone;
	public String Description;
	public String Ages;
	public String Times;
	public String WebAdd;
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(rank + ":");
		buffer.append("\tName: " + OrgName);
		buffer.append("\n\tDistance: " + Mi + " miles");
		buffer.append("\n\tAddress: " + address);
		buffer.append("\n\tPhone: " + Phone);
		buffer.append("\n\tCategory: " + Category);
		buffer.append("\n\tType: " + Type);
		buffer.append("\n\tCost: " + Cost);
		buffer.append("\n\tCost Details: " + CostDetails);
		buffer.append("\n\tAges: " + Ages);
		buffer.append("\n\tTimes; " + Times);
		buffer.append("\n\tWebsite: " + WebAdd);
		buffer.append("\n\tDescription: " + Description);
		
		return buffer.toString();
	}
}
