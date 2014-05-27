package org.openmrs.module.chica.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;

/**
 * Servlet to handle mobile ajax communications
 *
 * @author Steve McKee
 */
public class ChicaMobileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(this.getClass());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("patientsWithForms".equals(action)) {
			getPatientsWithForms(response);
		} else if ("verifyPasscode".equals(action)) {
			verifyPasscode(request, response);
		} else if ("isAuthenticated".equals(action)) {
			isUserAuthenticated(response);
		} else if ("authenticateUser".equals(action)) {
			authenticateUser(request, response);
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private void getPatientsWithForms(HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<patientsWithForms>");
		ArrayList<PatientRow> rows = new ArrayList<PatientRow>();
		String result;
		StringWriter stringWriter = new StringWriter();
	    PrintWriter printWriter = new PrintWriter(stringWriter);
	    if (Context.getAuthenticatedUser() == null) {
	    	pw.write("<error>Please log in.</error>");
	    	pw.write("</patientsWithForms>");
	    	return;
	    }
	    
        try {
	        result = org.openmrs.module.chica.util.Util.getPatientsWithForms(rows, null);
	        if (result == null) {
				for (PatientRow row : rows) {
					printWriter.write("<patient>");
					writeTag("id", row.getPatientId(), printWriter);
					writeTag("mrn", row.getMrn(), printWriter);
					writeTag("firstName", escapeXML(row.getFirstName()), printWriter);
					writeTag("lastName", escapeXML(row.getLastName()), printWriter);
					writeTag("appointment", escapeXML(row.getAppointment()), printWriter);
					writeTag("checkin", escapeXML(row.getCheckin()), printWriter);
					writeTag("dob", escapeXML(row.getDob()), printWriter);
					writeTag("age", escapeXML(row.getAgeAtVisit()), printWriter);
					writeTag("mdName", escapeXML(row.getMdName()), printWriter);
					writeTag("sex", row.getSex(), printWriter);
					writeTag("station", escapeXML(row.getStation()), printWriter);
					writeTag("status", escapeXML(row.getStatus()), printWriter);
					writeTag("sessionId", row.getSessionId(), printWriter);
					printWriter.write("<formInstances>");
					List<FormInstance> formInstances = row.getFormInstances();
					if (formInstances != null) {
						for (FormInstance formInstance : formInstances) {
							printWriter.write("<formInstance>");
							writeTag("formId", formInstance.getFormId(), printWriter);
							writeTag("formInstanceId", formInstance.getFormInstanceId(), printWriter);
							writeTag("locationId", formInstance.getLocationId(), printWriter);
							printWriter.write("</formInstance>");
						}
					}
					
					printWriter.write("</formInstances>");
					printWriter.write("</patient>");
				}
			}
	        
	        pw.write(stringWriter.toString());
        }
        catch (Exception e) {
	        log.error("Error generating patients with forms", e);
	        pw.write("<error>An error occurred retrieving the patient list</error>");
        }
		
		pw.write("</patientsWithForms>");
	}
	
	private void verifyPasscode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String passcode = request.getParameter("passcode");
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<passcodeResult>");
		pw.write("<result>");
		if (passcode == null || passcode.trim().length() == 0) {
			pw.write("Please enter a passcode.");
		} else if (Context.getAuthenticatedUser() == null) {
			pw.write("Please log in.");
		} else {
			String systemPasscode = Context.getAdministrationService().getGlobalProperty("chica.passcode");
			if (systemPasscode == null) {
				log.error("Please specify global propery chica.passcode");
				pw.write("Passcode not properly set on server.");
			} else {
				if (systemPasscode.equals(passcode)) {
					pw.write("success");
				} else {
					pw.write("Invalid passcode.");
				}
			}
		}
		
		pw.write("</result>");
		pw.write("</passcodeResult>");
	}
	
	private String escapeXML(String str) {
    	if (str == null) {
    		return str;
    	}
    	
        return str.replaceAll("&","&amp;")
                  .replaceAll("<", "&lt;")
                  .replaceAll(">", "&gt;")
                  .replaceAll("\"", "&quot;")
                  .replaceAll("'", "&apos;");
    }
	
	private void writeTag(String tagName, Object value, PrintWriter pw) {
		pw.write("<" + tagName + ">");
		if (value != null) {
			pw.write(value.toString());
		}
		
		pw.write("</" + tagName + ">");
	}
	
	private void isUserAuthenticated(HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<userAuthenticated>");
		pw.write("<result>");
		if (Context.getAuthenticatedUser() == null) {
			pw.write("false");
		} else {
			pw.write("true");
		}
		
		pw.write("</result>");
		pw.write("</userAuthenticated>");
	}
	
	private void authenticateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<userAuthenticated>");
		pw.write("<result>");
		try {
			Context.authenticate(username, password);
			pw.write("true");
		} catch (Exception e) {
			log.error("Error authenticating user", e);
			pw.write("false");
		}
		
		pw.write("</result>");
		pw.write("</userAuthenticated>");
	}
}
