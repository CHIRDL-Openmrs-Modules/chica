<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp" %>
<!DOCTYPE html>
  <openmrs:require allPrivileges="View Encounters, View Patients" otherwise="/login.htm" redirect="/module/chica/viewPatient.form" />

<%@ page import="org.openmrs.web.WebConstants" %>
<%
	pageContext.setAttribute("msg", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	pageContext.setAttribute("msgArgs", session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
	pageContext.setAttribute("err", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	pageContext.setAttribute("errArgs", session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
	session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
	
%>
  
<style>
	#content { font-size: inherit }
</style>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<link
    href="${pageContext.request.contextPath}/moduleResources/chica/chica.css"
    type="text/css" rel="stylesheet" />
<link rel="stylesheet" type="text/css"
    href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css" />
<link rel="stylesheet" type="text/css"
    href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css" />
<link rel="stylesheet" type="text/css"
    href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css" />
<script
	src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script
	src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.floatThead.min.js"></script>

<script language="javascript">
    function backToAdminPage() {
		window.location = ctx + "/admin/index.htm";
	}
</script>
<html>
	<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>View Encounters</title>
		<style>
			td{
				vertical-align: middle;
			}
		</style>
	</head>
	<body>
		<div id="pageBody">
			<div id="contentMinimal">
				<c:if test="${msg != null}">
					<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" /></div>
				</c:if>
				<c:if test="${err != null}">
					<div id="openmrs_error"><spring:message code="${err}" text="${err}" arguments="${errArgs}"/></div>
				</c:if>
				<form id="viewPatient" name="viewPatient" method="post" >
					<table width = "100%" class="formTitleStyle" >
						<tr>
							<td>
								<h3>Search For Patient Encounters</h3>
							</td>
						</tr>
					</table>

					<div id="encounterMrnMessage" >
						<p><h4>Enter the patient MRN to display all encounters for that patient</h4><p>
						<p><span id="encounterMrnError" ></span></p>
					</div>
					<div id= "viewEncounter">
						<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MRN <span style="font-size:8pt;" ></span></b>&nbsp;&nbsp;<input type="text"  size="8" id="mrn" name="mrn" style="width:150px;"tabindex="1"/>
					
						<input id="Enter" type="submit" name="viewPatientFromEncounterPage" value="Enter" tabindex="2"/> 
						<input type="button" value="Cancel" onClick="backToAdminPage();" tabindex="3"/>
					</div>
				</form>
				<script language="javascript">
						document.viewPatient.mrn.focus();
				</script>
				<script src="${pageContext.request.contextPath}/moduleResources/chica/viewPatient.js"></script>
			</div>
		</div>
	</body>
</html>