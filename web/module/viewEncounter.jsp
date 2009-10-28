<%@ include file="/WEB-INF/template/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/viewEncounter.form" />

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

<html style="height:100%;" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	    <!--<meta http-equiv="refresh" content="${refreshPeriod}" /> -->
		<openmrs:htmlInclude file="/openmrs.css" />
		<openmrs:htmlInclude file="/style.css" />
		<openmrs:htmlInclude file="/openmrs.js" />
		
		<script type="text/javascript">
			/* variable used in js to know the context path */
			var openmrsContextPath = '${pageContext.request.contextPath}';
		</script>
		
		<!--  Page Title : '${pageTitle}' 
			OpenMRS Title: <spring:message code="openmrs.title"/>
		-->
		<c:choose>
			<c:when test="${!empty pageTitle}">
				<title>${pageTitle}</title>
			</c:when>
			<c:otherwise>
				<title><spring:message code="openmrs.title"/></title>
			</c:otherwise>
		</c:choose>		
		<SCRIPT LANGUAGE="JavaScript">
<!-- Idea by:  Nic Wolfe -->
<!-- This script and many more are available free online at -->
<!-- The JavaScript Source!! http://javascript.internet.com -->



function lookupPatient(){
	document.location.href = "viewPatient.form";
	return false;
}

function exitForm(){
	 document.location.href = "greaseBoard.form";
	 return false;
}
// End -->
</script>

	</head>

<body  style="height:100%;" >
	<div id="pageBody" style="height:100%;">
		<div id="contentMinimal"style="height:100%;">
			<c:if test="${msg != null}">
				<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" /></div>
			</c:if>
			<c:if test="${err != null}">
				<div id="openmrs_error"><spring:message code="${err}" text="${err}" arguments="${errArgs}"/></div>
			</c:if>
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<div class="viewEncounterHeaderArea">
<table width="100%" class="viewEncounterHeaderText">
	<tr  class="chicaBackground" >
	<td  width = "100%" class="formTitleStyle" ><b>Encounters: ${titleMRN}</b>
	<br><b>${titleLastName}, ${titleFirstName} &nbsp&nbsp&nbsp DOB: ${titleDOB}</b></td>
	</tr>
</table>
</div>
<div style="height:5%">
<table width="100%" class="greaseBoardHeader">
	<tr  class="chicaBackground" style="text-align:left">
	<td class="viewEncounterDate"><b>Encounter<br>Date</b></td>
	<td class="viewEncounterStation"><b>Station</b></td>
	<td class="viewEncounterAge"><b>Age <BR>at visit</b></td>
	<td class="viewEncounterWeight"><b>Weight<BR>Percentile</b></td>
	<td class="viewEncounterHeight"><b>Height<BR>Percentile</b></td>
	<td class="viewEncounterDoctor"><b>Doctor</b></td>
	<td class="viewEncounterPSFID"><b>PSF ID</b></td>
	<td class="viewEncounterPWSID"><b>PWS ID</b></td>
	<td class="viewEncounterJITID"><b>JIT ID</b></td>
	<td class="viewEncounterAction"><b>Action</b></td>
	</tr>
   </table>
 </div>
<div class="encounterarea">
<table  style="width:100%; overflow:auto;" >
	<c:forEach items="${patientRows}" var="row">
		<tr style="text-align:left">
			<td class="viewEncounterDate">${row.checkin}</td>
			<td class="viewEncounterStation">${row.station}</td>
			<td class="viewEncounterAge">${row.ageAtVisit}</td>
			<td class="viewEncounterWeight">${row.weightPercentile}</td>
			<td class="viewEncounterHeight">${row.heightPercentile}</td>
			<td class="viewEncounterDoctor">${row.mdName}</td>
			<td class="viewEncounterPSFID"><c:if test = "${empty row.psfId.formInstanceId}">N/A</c:if>
				<c:if test= "${!empty row.psfId.formInstanceId}">${row.psfId.formInstanceId}</c:if>
			</td>
			<td class="viewEncounterPWSID"><c:if test = "${empty row.pwsId.formInstanceId}">N/A</c:if>
				<c:if test= "${!empty row.pwsId.formInstanceId}">${row.pwsId.formInstanceId}</c:if></td>
			<td class="viewEncounterJITID"><c:if test = "${empty row.jitID.formInstanceId}">N/A</c:if>
				<c:if test= "${!empty row.jitID.formInstanceId}">${row.jitID.formInstanceId}</c:if></td>
			<td class="viewEncounterAction">
				<form method="post" STYLE="margin: 0px; padding: 0px" action="">
				<select name="options" onchange="this.form.submit();">
				<option>&lt;Forms&gt;</option>
				<c:if test= "${!empty row.psfId.formInstanceId}"><option>PSF</option></c:if>
				<c:if test= "${!empty row.pwsId.formInstanceId}"><option>PWS</option></c:if>
				</select>
				<input type="hidden" value="${row.patientId}" name="patientId" />
				<input type="hidden" value="${row.sessionId}" name="sessionId" />
				<input type="hidden" value="${row.encounter.encounterId}" name="encounterId" />
				<input type="hidden" value="${row.psfId.formInstanceId}" name="psfFormInstanceId" />
				<input type="hidden" value="${row.pwsId.formInstanceId}" name="pwsFormInstanceId" />
				<input type="hidden" value="${row.psfId.formId}" name="psfFormId" />
				<input type="hidden" value="${row.pwsId.formId}" name="pwsFormId" />
				<input type="hidden" value="${row.psfId.locationId}" name="psfLocationId" />
				<input type="hidden" value="${row.pwsId.locationId}" name="pwsLocationId" />
			</form>
			</td>
		</tr>
		</c:forEach>
	</table>
   </div>
<div style="height:10%">
<table style="height:10%;width:100%" class="chicaBackground viewEncounterFooter"  valign="bottom">
<tr>
<td align="center">
<a href="" onclick="return lookupPatient();"><input type="button" value="View Patient" name="viewPatientButton" style="width:100px; height:50px"></a>
<a href="" onclick="return exitForm();"><input type="button" value="Exit" name="exitButton" style="width:100px; height:50px"></a>
</td>
</tr>
<tr>
<td>&nbsp</td>
</tr>
<tr>
<td>&nbsp</td>
</tr>
</table>
</div>
		<br/>
		</div>
	</div>
</body>
</html>