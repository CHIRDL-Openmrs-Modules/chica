package org.openmrs.module.chica.hl7.immunization;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.IOUtil;


public class SSLPost {
	
	@Test
	public void testSSLPost() throws Exception {
		//pass in vxqHL7 to method
		String vxqHL7 = "MSH|^~\\&|DBO^QSInsight^L|QS4444|5.0^QSInsight^L||20030828104856+0000||VXQ^V01|QS444437861000000042|P|2.3.1|||NE|AL|\n"
		        + "QRD|20030828104856+0000|R|I|QueryID01|||5|000000001^Bucket^Hyacinth^^^^^^^^^^MR|VXI|SIIS|\n"
		        + "QRF|QS4444|20030828104856+0000|20030828104856+0000||100000001~19460401~~~~~~~~~~111 East Lansing Bouldvard^Indianapolis^IN~10000|";
		String url = "https://chirp.in.gov/HL7Server"; //need to be global property
		String userId = "WISHARDHL7"; //needs to be global property
		String password = "WELCOME101"; //needs to be global property
		
		String data = URLEncoder.encode("USERID", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
		data += "&" + URLEncoder.encode("PASSWORD", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
		data += "&" + URLEncoder.encode("MESSAGEDATA", "UTF-8") + "=" + URLEncoder.encode(vxqHL7, "UTF-8");
		data += "&" + URLEncoder.encode("debug", "UTF-8");
		data += "&" + URLEncoder.encode("deduplicate", "UTF-8");
		
		String response = postSSLMessage(url,data);
		System.out.println("response is: "+response);
	}
	
	public static String postSSLMessage(String url,String postData) throws Exception{
		URL aURL = new java.net.URL(url);
		AdministrationService adminService = Context.getAdministrationService();
		Integer timeout = Integer.parseInt(adminService.getGlobalProperty("chica.SSLConnectionTimeout"));
		timeout = timeout * 1000; // convert seconds to milliseconds
		// Make the connection
		HttpURLConnection aConnection = (java.net.HttpURLConnection)aURL.openConnection();
		aConnection.setConnectTimeout(timeout); //needs to be global property
		aConnection.setReadTimeout(timeout); //needs to be global property
		aConnection.setDoOutput(true);
		aConnection.setDoInput(true);
		aConnection.setRequestMethod("POST");
		aConnection.setAllowUserInteraction(false);
		// POST the data
		OutputStreamWriter streamToAuthorize = new java.io.OutputStreamWriter(aConnection.getOutputStream());
		streamToAuthorize.write(postData);
		streamToAuthorize.flush();
		streamToAuthorize.close();
		
		InputStream resultStream = aConnection.getInputStream();
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		IOUtil.bufferedReadWrite(resultStream, responseStream);
		return responseStream.toString();
	}
}
