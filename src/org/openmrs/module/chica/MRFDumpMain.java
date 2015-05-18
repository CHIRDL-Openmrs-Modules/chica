package org.openmrs.module.chica;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.soap.Detail;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.axis2.AxisFault;
import org.openmrs.module.chica.mrfservices.DumpServiceStub;
import org.openmrs.module.chica.mrfservices.DumpServiceStub.GetDumpE;
import org.openmrs.module.chica.mrfservices.DumpServiceStub.GetDumpResponseE;


public class MRFDumpMain {
	
	public void getMrfDump() {
//		GetDump dump = new GetDump();
//		
//		try {
//			DumpService service = new DumpService(new URL("https://172.31.80.31:8443/NHIN/services/MRNRequestForDump?wsdl"), 
//				new QName("http://www.regenstrief.org/services", "DumpService"));
//		} catch (SOAPFaultException e) {
//        	processSoapFault(e);
//        	System.exit(1);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
		
		try {
		DumpServiceStub service = new DumpServiceStub();
		GetDumpE dumpE = new GetDumpE();
		DumpServiceStub.GetDump dump = new DumpServiceStub.GetDump();
		dumpE.setGetDump(dump);
		
		DumpServiceStub.EntityIdentifier login = new DumpServiceStub.EntityIdentifier();
		login.setId("CHICA_PCC");
		login.setSystem("SPIN Users");
		dump.setUser(login);
		DumpServiceStub.EntityIdentifier patient = new DumpServiceStub.EntityIdentifier();
		patient.setId("1613721-8");
		patient.setSystem("Wishard Memorial Hospital Medical Record Numbers");
		dump.setPatient(patient);
		
		dump.setPassword("92ReIlAbOuQ");
		dump.setClassesToExclude("org.regenstrief.location.EncounterDiagnosis;org.regenstrief.location.EncounterProcedure;org.regenstrief.location.EncounterInsurance;org.regenstrief.multimedia.TextReport;org.regenstrief.location.EncounterAccount;org.regenstrief.pharmacy.PharmacyOrder;org.regenstrief.order.Order;org.regenstrief.order.Participation");
		long start = Calendar.getInstance().getTimeInMillis();
		GetDumpResponseE response = service.getDump(dumpE);
		long stop = Calendar.getInstance().getTimeInMillis();
		String res = response.getGetDumpResponse().getHl7();
		final byte[] utf16Bytes= res.getBytes("UTF-16BE");
		
		
		String hl7String = response.getGetDumpResponse().getHl7();
		System.out.println(hl7String);
		byte[] utf8Bytes = hl7String.getBytes("UTF-8");
		System.out.println("Size: " + utf8Bytes.length);
		System.out.println("query time: " + (stop - start));
		String[] timings = response.getGetDumpResponse().getTiming();
		for (String timing : timings){
			System.out.println(timing);
		}
		
		} catch (AxisFault e) {
			e.printStackTrace();
			System.exit(1);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.exit(0);
	}
	
	/**
	 * Processes a SOAPFaultException.
	 * 
	 * @param e The SOAPFaultException to process.
	 */
	private void processSoapFault(SOAPFaultException e) {
		SOAPFault fault = e.getFault();
    	System.err.println("Fault code: " + fault.getFaultCode());
    	System.err.println("Fault string: " + fault.getFaultString());
    	Detail detail = fault.getDetail();
    	if (detail != null) {
    		String content = detail.getTextContent();
    		if (content != null) {
    			System.err.println("Fault detail: " + content);
    		}
    	}
	}
	
	public static void main(String[] args) {
		new MRFDumpMain().getMrfDump();
	} 
	
}
