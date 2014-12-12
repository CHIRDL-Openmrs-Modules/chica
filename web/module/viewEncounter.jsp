<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
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
	    <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
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

	$(function() {
	    $("#viewPatientButton").button();
	    $("#exitButton").button();
	    //$("select").selectmenu();
	    //$("select").selectmenu( "refresh" );
	    /*$(".view-forms").selectmenu().selectmenu("menuWidget").css({"max-height":($(window).height() * 0.60) + "px"});
	    $(".view-forms").append(options.join("")).selectmenu({
	          select: function( event, ui ) {
	              var formInstance = this.val();
	              if (formInstance == "unselected") {
	                  // A valid form was not selected
	              } else {
	            	  var formId = this.val();
	              }
	          }
	    }).selectmenu("menuWidget").css({"max-height":($(window).height() * 0.60) + "px"});*/
	    $( ".view-forms" ).selectmenu({
    	  select: function( event, ui ) {
    		  var formInstance = ui.item.value;
              if (formInstance == "unselected") {
                  // A valid form was not selected
              } else {
            	  var form = $(this).closest('form');
            	  form.submit();
              }
    	  }
    	});
	});

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
<style>
    fieldset {
      border: 0;
      font-size: 10px;
    }

    label {
      display: block;
      margin: 30px 0 0 0;
    }

    select, .ui-selectmenu {
      width: 200px;
      height: 20px;
    }

    .overflow {
      height: 200px;
    }
</style>
	</head>

<body  style="height:100%;" >
	<div id="pageBody" style="height:100%;">
		<div id="contentMinimal" style="height:100%;">
			<c:if test="${msg != null}">
				<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" /></div>
			</c:if>
			<c:if test="${err != null}">
				<div id="openmrs_error"><spring:message code="${err}" text="${err}" arguments="${errArgs}"/></div>
			</c:if>
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
<div class="viewEncounterHeaderArea">
<table width="100%" class="viewEncounterHeaderText">
	<tr>
	<td  width = "100%" class="formTitleStyle" ><b>Encounters: ${titleMRN}
	<br>${titleLastName}, ${titleFirstName} &nbsp;&nbsp;&nbsp; DOB: ${titleDOB}</b></td>
	</tr>
</table>
</div>
<div class="encounterarea">
<table  style="width:100%; overflow:auto;" class="table1" >
    <tbody>
    <tr>
	    <th class="viewEncounterDate chicaTableHeader"><b>Encounter Date</b></th>
	    <th class="viewEncounterStation chicaTableHeader"><b>Station</b></th>
	    <th class="viewEncounterAge chicaTableHeader"><b>Age at visit</b></th>
	    <th class="viewEncounterWeight chicaTableHeader"><b>Weight<BR>Percentile</b></th>
	    <th class="viewEncounterHeight chicaTableHeader"><b>Height<BR>Percentile</b></th>
	    <th class="viewEncounterDoctor chicaTableHeader"><b>Doctor</b></th>
	    <th class="viewEncounterPSFID chicaTableHeader"><b>PSF ID</b></th>
	    <th class="viewEncounterPWSID chicaTableHeader"><b>PWS ID</b></th>
	    <th class="viewEncounterAction chicaTableHeader"><b>Action</b></th>
    </tr>
	<c:forEach items="${patientRows}" var="row" varStatus="status">
	   <c:choose>
          <c:when test='${(status.index)%2 eq 0}'>
            <c:set var="rowColor" value="tableRowOdd" scope="page"/>
          </c:when>
          <c:otherwise>
            <c:set var="rowColor" value="tableRowEven" scope="page"/>
          </c:otherwise>
       </c:choose>
		<tr style="text-align:left">
			<td class="viewEncounterDate ${rowColor}">${row.checkin}</td>
			<td class="viewEncounterStation ${rowColor}">${row.station}</td>
			<td class="viewEncounterAge ${rowColor}">${row.ageAtVisit}</td>
			<td class="viewEncounterWeight ${rowColor}">${row.weightPercentile}</td>
			<td class="viewEncounterHeight ${rowColor}">${row.heightPercentile}</td>
			<td class="viewEncounterDoctor ${rowColor}">${row.mdName}</td>
			<td class="viewEncounterPSFID ${rowColor}"><c:if test = "${empty row.psfId.formInstanceId}">N/A</c:if>
				<c:if test= "${!empty row.psfId.formInstanceId}">${row.psfId.formInstanceId}</c:if>
			</td>
			<td class="viewEncounterPWSID ${rowColor}"><c:if test = "${empty row.pwsId.formInstanceId}">N/A</c:if>
				<c:if test= "${!empty row.pwsId.formInstanceId}">${row.pwsId.formInstanceId}<c:if test="${row.pwsScanned}"><span style="color:#6699FF;text-shadow: 1px 1px #000000;"><b>*</b></span></c:if></c:if></td>
			<td class="viewEncounterAction ${rowColor}">
				<form method="post" STYLE="margin: 0px; padding: 0px" action="">
				    <fieldset>
						<select name="options" class="view-forms">
							<option value="unselected" selected="selected">Select a form</option>
							<c:forEach items="${row.formInstances}" var="formInstance">
								<option value="${formInstance.locationId}_${formInstance.formId}_${formInstance.formInstanceId}">
								     ${formNameMap[formInstance.formId] }
								</option>
							</c:forEach>
						</select>
					</fieldset>
					<input type="hidden" value="${row.patientId}" name="patientId" />
					<input type="hidden" value="${row.encounter.encounterId}" name="encounterId" />
			    </form>
			</td>
		</tr>
		</c:forEach>
		</tbody>
	</table>
   </div>
<div style="height:10%">
<table style="height:10%;width:100%" class="chicaBackground viewEncounterFooter"  valign="bottom">
<tr>
<td align="center" style="padding-top:10px;">
<a href="#" name="viewPatientButton" onclick="return lookupPatient();" id="viewPatientButton" class="icon-button-large ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>View Patient</a>
<a href="#" name="exitButton" onclick="javascript:window.close();" id="exitButton" class="icon-button-large ui-state-default ui-corner-all">Exit</a>
</td>
</tr>
<tr>
<td><span style="color:#6699FF;text-shadow: 1px 1px #000000;"><b>*</b></span>&nbsp;<span style="color:white;text-shadow: 1px 1px #000000;">Indicates that the PWS was scanned for the given encounter</span></td>
</tr>
<tr>
<td>&nbsp;</td>
</tr>
<tr>
<td>&nbsp;</td>
</tr>
</table>
</div>
		<br/>
		</div>
	</div>
</body>
</html>