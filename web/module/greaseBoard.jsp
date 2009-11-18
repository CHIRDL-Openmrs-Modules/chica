<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/greaseBoard.form" />

<%@ page  import="org.openmrs.web.WebConstants" %>
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

<html  style="height:100%;"  xmlns="http://www.w3.org/1999/xhtml">
	<head >
	    <meta http-equiv="refresh" content="${refreshPeriod}"/>
		<openmrs:htmlInclude file="/openmrs.css" />
		<openmrs:htmlInclude file="/style.css" />
		<openmrs:htmlInclude file="/openmrs.js" />
			<title>CHICA Greaseboard</title>
		
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
		function popUp(URL) {
			day = new Date();
			id = day.getTime();
			eval("page" + id + " = window.open(URL, '" + id + "', 'toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=0,width=250,height=250,left = 312,top = 284');");
		}

		function pagerPopUp(URL) {
			window.open(URL, '', 'toolbar=0, scrollbars=0,location=0,locationbar=0,statusbar=0,menubar=0,resizable=0,width=400,height=100,left = 312,top = 284');
		}
		
		function lookupPatient(){
			document.location.href = "viewPatient.form";
			return false;
		}

                function popupfull(url) 
                {
 		params  = 'width='+screen.width;
 		params += ', height='+screen.height;
 		params += ', top=0, left=0'
 		params += ', fullscreen=no';

 		newwin=window.open(url,'windowname4', params);
 		if (window.focus) {newwin.focus()}
 		return false;
 		}
		</script>
 
</head>

<body  style="height:100%;" >
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<div  class="greaseBoardPageBorder headerarea" style="overflow:hidden;">
<table width="100%"  style="height:100%;border:0;frame:void; cellspacing:0px;border-width:1px;border-bottom-width:3px;border-style:solid;border-color: black">
	<tr  class="chicaBackground" style=>
	<td  width = "100%" class="formTitleStyle" ><b>Arrivals&nbsp;&nbsp;&nbsp;&nbsp;${today}</b></td>
	
	</tr>
</table>
</div>
<div style="height:3%;font-size:15px; "  >
<table  cellpadding="1" style="height:100%; overflow: hidden;  cellpadding:0; border:0;" class="greaseBoardHeader" >
<tr class="chicaBackground" text-align:left>
<td class="ln"><b>Last</b></td>
<td class="fn"><b>First</b></td>
<td class="mrn"><b>MRN</b></td>
<td class="dob"><b>DOB</b></td>
<td class="sex"><b>Sex</b></td>
<td class="MD"><b>MD</b></td>
<td class="aptTime"><b>Appt</b></td>
<td class="chkTime"><b>Check-in</b></td>
<td class="reprint"><b>Rpnt</b></td>
<td class="status"><b>Status</b></td>
<td class="action"><b>Action</b></td>
</tr>
</table>
</div>
<div class="area" width="100%">
<table width="100%">
<c:forEach items="${patientRows}" var="row"> 
<tr style="text-align:left">
<td class="ln ${row.rowColor}">${row.lastName}</td>
<td class="fn ${row.rowColor}">${row.firstName}</td>
<td class="mrn ${row.rowColor}">${row.mrn}</td>
<td class="dob ${row.rowColor}">${row.dob}</td>
<td class="sex ${row.rowColor}">${row.sex}</td>
<td class="MD ${row.rowColor}">${row.mdName}</td>
<td class="aptTime ${row.rowColor}">${row.appointment}</td>
<td class="chkTime ${row.rowColor}">${row.checkin}</td>
<td class="reprint ${row.rowColor}"><c:if test="${row.reprintStatus}"><span style="color:red"><b>*</b></c:if></td>
<td class="status ${row.statusColor}">${row.status}</td>
<td class="action ${row.rowColor}" >
<form method="post" STYLE="margin: 0px; padding: 0px; action="">
<select name="options"  onchange="this.form.submit();">
<option>&lt;Options&gt;</option>
<option>Encounters</option>
<option>Print PSF</option>
<option>Print PWS</option>
</select>
<input type="hidden" value="${row.patientId}" name="patientId" />
<input type="hidden" value="${row.sessionId}" name="sessionId" />
</form>
</td>
</tr>
</c:forEach>
</table>
</div>
<div style="height:23%;">
<table  class="chicaBackground greaseBoardFooter" >
<tr>
<td width="27%">
<table width="100%">
<tr>
<td><b>Need Vitals:</b></td>
<td>${needVitals}</td>
</tr>
<tr>
<td><b>Waiting for MD:</b></td>
<td>${waitingForMD}</td>
</tr>
</table>
</td>
<td width="40%">
<table width="100%">
<tr>
<td align="center"><button style="width:250px;height:27px;font-size:15px;" onClick="javascript:popUp('manualCheckinSSNMRN.form')" tabindex="1"><b>Check-in Patient</b></button></td>
</tr>
<tr>
<td align="center">
<input type="button" value="View Encounters" 
name="viewEncountersButton" onClick="javascript:popupfull('viewPatient.form')"  style="width:250px;height:27px;font-size:15px;font-weight:bold;" ></a>

</tr>
<tr>
<td align="center">
<form method="post" name="pageForm" action="">
<input type="hidden" name="page" value="sendPage"/>
<button class="pagerButtonStyle" name="selectPager"  onClick="javascript:pagerPopUp('pager.form')" >"Get Help Now!" </button>
</form></td>
</tr>
</table>
</td>
<td width="33%">
<table width="100%" style="font-size: 8pt">
<tr>
<td><span class="waitTextStyle">__</span>&nbsp;Wait, Please STAY until GREEN</td>
</tr>
<tr>
<td><span class="inProcessTextStyle">__</span>&nbsp;Transaction in Process</td>
</tr>
<tr>
<td><span class="formReadyTextStyle">__</span>&nbsp;Form Ready to Pickup</td>
</tr>
</table>
</td>
</tr>
</table>
</div>
</body>
</html>