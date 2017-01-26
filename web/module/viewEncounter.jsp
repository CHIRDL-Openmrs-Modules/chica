<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require
	allPrivileges="View Encounters, View Patients, View Concept Classes"
	otherwise="/login.htm" redirect="/module/chica/viewEncounter.form" />

<%@ page import="org.openmrs.web.WebConstants"%>
<%
	pageContext.setAttribute("msg",
			session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	pageContext.setAttribute("msgArgs",
			session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
	pageContext.setAttribute("err",
			session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	pageContext.setAttribute("errArgs",
			session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
	session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
%>

<html>
<head>
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
<script src="${pageContext.request.contextPath}/moduleResources/chica/viewEncounter.js"></script>
<!--<meta http-equiv="refresh" content="${refreshPeriod}" /> -->
<openmrs:htmlInclude file="/openmrs.css" />
<openmrs:htmlInclude file="/style.css" />
<openmrs:htmlInclude file="/openmrs.js" />
<title>Encounters</title>
<style>
#submitWaitText {
    text-align:center;
    color:#000;
}

#loadingDialog {
    border: 4px solid #FA8258;
    padding: 0,0,0,0;
}

.form-loading {
    text-align: center;
    margin: 0 auto;
    width: 100%;
    color: #000;
}

.ui-dialog-shadow { 
    box-shadow: 10px 10px 5px #2E2E2E;
}

.ui-dialog { 
    z-index: 1002 !important ;
}

.ui-selectmenu { 
    z-index: 2 !important ;
}
</style>
</head>

<body>
	<c:if test="${msg != null}">
		<div id="openmrs_msg">
			<spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" />
		</div>
	</c:if>
	<c:if test="${err != null}">
		<div id="openmrs_error">
			<spring:message code="${err}" text="${err}" arguments="${errArgs}" />
		</div>
	</c:if>
	<div class="viewEncounterHeaderArea" id="top">
		<table width="100%">
			<tr>
				<td width="100%" class="formTitleStyle"><b>Encounters:
						${titleMRN} <br>${titleLastName}, ${titleFirstName}
						&nbsp;&nbsp;&nbsp; DOB: ${titleDOB}
				</b></td>
			</tr>
		</table>
	</div>
	<div class="encounterarea" id="middle">
		<table style="width: 100%; overflow: auto;" class="chicaBackground" id="encountersTable">
		    <thead id="encounterHeader">
		        <tr>
                    <th class="viewEncounterDate chicaTableHeader"><b>Encounter
                            Date</b></th>
                    <th class="viewEncounterStation chicaTableHeader"><b>Station</b></th>
                    <th class="viewEncounterAge chicaTableHeader"><b>Age at
                            visit</b></th>
                    <th class="viewEncounterWeight chicaTableHeader"><b>Weight<BR>Percentile
                    </b></th>
                    <th class="viewEncounterHeight chicaTableHeader"><b>Height<BR>Percentile
                    </b></th>
                    <th class="viewEncounterDoctor chicaTableHeader"><b>Doctor</b></th>
                    <th class="viewEncounterPSFID chicaTableHeader"><b>PSF ID</b></th>
                    <th class="viewEncounterPWSID chicaTableHeader"><b>PWS ID</b></th>
                    <th class="viewEncounterAction chicaTableHeader"><b>Action</b></th>
               </tr>
		    </thead>
			<tbody>
				<c:forEach items="${patientRows}" var="row" varStatus="status">
					<c:choose>
						<c:when test='${(status.index)%2 eq 0}'>
							<c:set var="rowColor" value="tableRowOdd" scope="page" />
						</c:when>
						<c:otherwise>
							<c:set var="rowColor" value="tableRowEven" scope="page" />
						</c:otherwise>
					</c:choose>
					<tr style="text-align: left">
						<td class="viewEncounterDate ${rowColor}">${row.checkin}</td>
						<td class="viewEncounterStation ${rowColor}">${row.station}</td>
						<td class="viewEncounterAge ${rowColor}">${row.ageAtVisit}</td>
						<td class="viewEncounterWeight ${rowColor}">${row.weightPercentile}</td>
						<td class="viewEncounterHeight ${rowColor}">${row.heightPercentile}</td>
						<td class="viewEncounterDoctor ${rowColor}">${row.mdName}</td>
						<td class="viewEncounterPSFID ${rowColor}"><c:if
								test="${empty row.psfId.formInstanceId}">N/A</c:if> <c:if
								test="${!empty row.psfId.formInstanceId}">${row.psfId.formInstanceId}</c:if>
						</td>
						<td class="viewEncounterPWSID ${rowColor}"><c:if
								test="${empty row.pwsId.formInstanceId}">N/A</c:if> <c:if
								test="${!empty row.pwsId.formInstanceId}">${row.pwsId.formInstanceId}<c:if
									test="${row.pwsScanned}">
									<span style="color: #6699FF; text-shadow: 1px 1px #000000;"><b>*</b></span>
								</c:if>
							</c:if></td>
						<td class="viewEncounterAction ${rowColor}">
							<form method="post" STYLE="margin: 0px; padding: 0px" action="">
								<fieldset class="tableFieldset">
									<select name="options" class="view-forms tableSelect">
										<option value="unselected" selected="selected">Select
											a form</option>
										<c:forEach items="${row.formInstances}" var="formInstance">
											<option
												value="${formInstance.locationId}_${formInstance.formId}_${formInstance.formInstanceId}">
												${formNameMap[formInstance.formId] }</option>
										</c:forEach>
									</select>
									<input type="hidden" value="${row.patientId}" name="patientId" />
                                    <input type="hidden" value="${row.encounter.encounterId}" name="encounterId" />
								</fieldset>
							</form>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div style="height:60px;" id="bottom">
		<table style="width: 100%"
			class="chicaBackground viewEncounterFooter">
			<tr>
				<td align="center" style="padding-top:10px;">
					<div>
						<button id="viewPatientButton" class="icon-button-large ui-state-default ui-corner-all">View Patient</button>
						<button id="exitButton" class="icon-button-large ui-state-default ui-corner-all" onclick="javascript:window.close();">Exit</button>
					</div>
				</td>
			</tr>
			<tr>
				<td><span style="color: #6699FF; text-shadow: 1px 1px #000000;"><b>*</b></span>&nbsp;<span
					style="color: white; text-shadow: 1px 1px #000000;">Indicates
						that the PWS was scanned for the given encounter</span></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>
	</div>
	<div id="loadingDialog" class="noTitle">
        <div id="submitWaitText">
            <span>Loading...</span>
        </div>
    </div>
    <div id="viewEncountersMRNDialog" title="View Encounters" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div style="margin: 0 auto;text-align: center;">
            <div style="color:#000000;"><p><b>Type the MRN #. Press OK to display the patient's encounters.</b></p></div>
            <div id="encounterMrnLoading" class="form-loading">
                 <span><img src="${pageContext.request.contextPath}/moduleResources/chica/images/ajax-loader.gif"/>Verifying MRN...</span>
              </div>
            <div id="encounterMrnError" style="text-align:center;"><span id="encounterMrnMessage" class="alertText"></span></div>
            <div style="padding-bottom:10px;"><input type="text" size="20" id="encounterMrnLookup" tabindex="1"/></div>
        </div>
    </div>
</body>
</html>