<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ page import="org.openmrs.module.chirdlutil.util.Util"%>
<%@ page import="org.openmrs.Patient" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/additionalInfomationMobile.form" />
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/additionalInfomationMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<c:set var="formName" value="Additional Information Form:"/>
<c:set var="formName_sp" value=""/>
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
<form id="AdditionalInformantionForm" method="POST" action="additionalInfomationMobile.form" method="post" enctype="multipart/form-data">
<c:if test="${errorMessage != null}">
    <div id="error_dialog" class="extended-header" data-role="dialog" data-close-btn="none" data-dismissible="false" data-theme="b" data-overlay-theme="c">
        <div data-role="header" data-theme="b">
            <h1>Error</h1>
        </div>
        <div data-role="content">
            <span>${errorMessage}</span>
            <div style="margin: 0 auto;text-align: center;">
                <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="submitEmptyForm()" style="width: 150px;">OK</a>
            </div>
        </div>
    </div>
</c:if>
<div data-role="page" id="instruction_page" data-theme="b">
    <div data-role="header" >
        <h1 id="formTitle">${formName}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
	<div data-role="content" >
        <strong><span id="additionalQuestions">Please complete some additional information about this visit.</span></strong>
        <div><br/></div>
    </div>

    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage(1)" style="width: 150px;">Start</a>
    </div>
    
</div>

<div data-role="page" id="form_completed_page" data-theme="b">
    <div data-role="header" >
        <h1>${formName} Form Completed</h1>
    </div>
    <div data-role="content" style="margin: 0 auto;text-align: center;" >
        <strong><span>The Additional Information form has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-inline="true" data-theme="b" data-role="button" data-rel="back" style="width: 150px;">Back</a>
    </div>
</div>

<%@ include file="mobileQuitConfirmDialog.jsp" %>
<c:set var="copyright" value=''/>
<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formName}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_1" data-role="content">
        <c:set var="quest1" value='What is your relationship to ${patient.givenName}&nbsp;${patient.familyName}?'/>
        <input id="Question_1" name="Question_1" type="hidden" value="${quest1}"/>
	    <strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest1}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
				<select name="Informant_1" id="Informant_1" data-native-menu="false">
					<option name="Informant_1" id="Informant_1_0" value=""/>
					<label for="Informant_1_0">Select One</label>
					<option name="Informant_1" id="Informant_1_1" value="self" data-theme="b" />
					<label for="Informant_1_1">Self</label>
					<option name="Informant_1" id="Informant_1_2" value="mother" data-theme="b" />
					<label for="Informant_1_2">Mother</label>
					<option name="Informant_1" id="Informant_1_3" value="father" data-theme="b" />
					<label for="Informant_1_3">Father</label>
					<option name="Informant_1" id="Informant_1_4" value="grandmother" data-theme="b" />
					<label for="Informant_1_4">Grandmother</label>
					<option name="Informant_1" id="Informant_1_5" value="grandfather" data-theme="b" />
					<label for="Informant_1_5">Grandfather</label>
					<option name="Informant_1" id="Informant_1_6" value="aunt" data-theme="b" />
					<label for="Informant_1_6">Aunt</label>
					<option name="Informant_1" id="Informant_1_7" value="uncle" data-theme="b" />
					<label for="Informant_1_7">Uncle</label>
					<option name="Informant_1" id="Informant_1_8" value="foster parent" data-theme="b" />
					<label for="Informant_1_8">Foster parent</label>
					<option name="Informant_1" id="Informant_1_9" value="sibling" data-theme="b" />
					<label for="Informant_1_9">Sibling</label>
					<option name="Informant_1" id="Informant_1_10" value="Other" data-theme="b" />
					<label for="Informant_1_10">Other</label>
				</select>
	        </fieldset>
	    </div>
    </div>
    <div id="content_2" data-role="content">
    <c:set var="quest2" value='Who else is at this visit?'/>
        <input id="Question_2" name="Question_2" type="hidden" value="${quest2}"/>
        <strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
			<fieldset data-role="controlgroup" data-type="vertical">
				<select name="Informant_2" id="Informant_2" multiple="multiple" data-native-menu="false">
				  <option name="Informant_2" id="Informant_2_0"/>
				  <label for="Informant_2_0">Select all that apply</label>
				  <option name="Informant_2" value="self" id="Informant_2_1" data-theme="b" />
				  <label for="Informant_2_1">Self</label>
				  <option name="Informant_2" value="mother" id="Informant_2_2" data-theme="b" />
				  <label for="Informant_2_2">Mother</label>
				  <option name="Informant_2" value="father" id="Informant_2_3" data-theme="b" />
				  <label for="Informant_2_3">Father</label>
				  <option name="Informant_2" value="grandmother" id="Informant_2_4" data-theme="b" />
				  <label for="Informant_2_4">Grandmother</label>
				  <option name="Informant_2" value="grandfather" id="Informant_2_5" data-theme="b" />
				  <label for="Informant_2_5">Grandfather</label>
				  <option name="Informant_2" value="aunt" id="Informant_2_6" data-theme="b" />
				  <label for="Informant_2_6">Aunt</label>
				  <option name="Informant_2" value="uncle" id="Informant_2_7" data-theme="b" />
				  <label for="Informant_2_7">Uncle</label>
				  <option name="Informant_2" value="foster parent" id="Informant_2_8" data-theme="b" />
				  <label for="Informant_2_8">Foster parent</label>
				  <option name="Informant_2" value="sibling" id="Informant_2_9" data-theme="b" />
				  <label for="Informant_2_9">Sibling</label>
				  <option name="Informant_2" value="Other" id="Informant_2_10" data-theme="b" />
				  <label for="Informant_2_10">Other</label>
				  <option name="Informant_2" value="No One" id="Informant_2_11" data-theme="b" />
				  <label for="Informant_2_11">No one (I'm here alone)</label>
				</select>
			</fieldset>
		</div>
		<input id="VisitAttendee" name="VisitAttendee" type="hidden" value=""/>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
        		
        <%@ include file="mobileFinishDialogs.jsp" %>
        
        </div>
        
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a  href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Finish</a>
    </div>
</div>

<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
   <div data-role="header" >
        <h1>${formName_sp}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_1_sp" data-role="content">
        <c:set var="quest1_2" value='(Spanish) What is your relationship to ${patient.givenName}&nbsp;${patient.familyName}?'/>
        <input id="Question_1_2" name="Question_1_2" type="hidden" value="${quest1_2}"/>
	    <strong>${quest1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
				<select name="Informant_1_2" id="Informant_1_2" data-native-menu="false">
					<option name="Informant_1_2" id="Informant_1_2_0" />
					<label for="Informant_1_2_0">Select One</label>
					<option name="Informant_1_2" id="Informant_1_2_1" value="self" data-theme="b" />
					<label for="Informant_1_2_1">Self</label>
					<option name="Informant_1_2" id="Informant_1_2_2" value="mother" data-theme="b" />
					<label for="Informant_1_2_2">Mother</label>
					<option name="Informant_1_2" id="Informant_1_2_3" value="father" data-theme="b" />
					<label for="Informant_1_2_3">Father</label>
					<option name="Informant_1_2" id="Informant_1_2_4" value="grandmother" data-theme="b" />
					<label for="Informant_1_2_4">Grandmother</label>
					<option name="Informant_1_2" id="Informant_1_2_5" value="grandfather" data-theme="b" />
					<label for="Informant_1_2_5">Grandfather</label>
					<option name="Informant_1_2" id="Informant_1_2_6" value="aunt" data-theme="b" />
					<label for="Informant_1_2_6">Aunt</label>
					<option name="Informant_1_2" id="Informant_1_2_7" value="uncle" data-theme="b" />
					<label for="Informant_1_2_7">Uncle</label>
					<option name="Informant_1_2" id="Informant_1_2_8" value="foster parent" data-theme="b" />
					<label for="Informant_1_2_8">Foster parent</label>
					<option name="Informant_1_2" id="Informant_1_2_9" value="sibling" data-theme="b" />
					<label for="Informant_1_2_9">Sibling</label>
					<option name="Informant_1_2" id="Informant_1_2_10" value="Other" data-theme="b" />
					<label for="Informant_1_2_10">Other</label>
				</select>
	        </fieldset>
	    </div>
    </div>
    <div id="content_2_sp" data-role="content">
    <c:set var="quest2_2" value='(Spanish) Who else is at this visit?'/>
        <input id="Question_2_2" name="Question_2_2" type="hidden" value="${quest2_2}"/>
        <strong>${quest2_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
			<fieldset data-role="controlgroup" data-type="vertical">
				<select name="Informant_2_2" id="Informant_2_2" multiple="multiple" data-native-menu="false">
				  <option name="Informant_2_2" id="Informant_2_2_0"/>
				  <label for="Informant_2_2_0">Select all that apply</label>
				  <option name="Informant_2_2" value="self" id="Informant_2_2_1" data-theme="b" />
				  <label for="Informant_2_2_1">Self</label>
				  <option name="Informant_2_2" value="mother" id="Informant_2_2_2" data-theme="b" />
				  <label for="Informant_2_2_2">Mother</label>
				  <option name="Informant_2_2" value="father" id="Informant_2_2_3" data-theme="b" />
				  <label for="Informant_2_2_3">Father</label>
				  <option name="Informant_2_2" value="grandmother" id="Informant_2_2_4" data-theme="b" />
				  <label for="Informant_2_2_4">Grandmother</label>
				  <option name="Informant_2_2" value="grandfather" id="Informant_2_2_5" data-theme="b" />
				  <label for="Informant_2_2_5">Grandfather</label>
				  <option name="Informant_2_2" value="aunt" id="Informant_2_2_6" data-theme="b" />
				  <label for="Informant_2_2_6">Aunt</label>
				  <option name="Informant_2_2" value="uncle" id="Informant_2_2_7" data-theme="b" />
				  <label for="Informant_2_2_7">Uncle</label>
				  <option name="Informant_2_2" value="foster parent" id="Informant_2_2_8" data-theme="b" />
				  <label for="Informant_2_2_8">Foster parent</label>
				  <option name="Informant_2_2" value="sibling" id="Informant_2_2_9" data-theme="b" />
				  <label for="Informant_2_2_9">Sibling</label>
				  <option name="Informant_2_2" value="Other" id="Informant_2_2_10" data-theme="b" />
				  <label for="Informant_2_2_10">Other</label>
				  <option name="Informant_2_2" value="No One" id="Informant_2_2_11" data-theme="b" />
				  <label for="Informant_2_2_11">No one (I'm here alone)</label>
				</select>
			</fieldset>
		</div>
		<input id="VisitAttendee" name="VisitAttendee" type="hidden" value=""/>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
        		
        <%@ include file="mobileFinishDialogs_SP.jsp" %>
        
	</div>
        
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a  href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Acabado</a>
    </div>
</div>

    
<div id="empty_page" data-role="page" data-theme="b">
</div>

<input id="formInstances" name="formInstances" type="hidden" value="${formInstances }"/>
<input id="patientId" name="patientId" type="hidden" value="${patient.patientId}"/>
<input id="encounterId" name="encounterId" type="hidden" value="${encounterId}"/>
<input id="formId" name="formId" type="hidden" value="${formId}"/>
<input id="formInstanceId" name="formInstanceId" type="hidden" value="${formInstanceId}"/>
<input id="locationId" name="locationId" type="hidden" value="${locationId}"/>
<input id="locationTagId" name="locationTagId" type="hidden" value="${locationTagId}"/>
<input id="sessionId" name="sessionId" type="hidden" value="${sessionId}"/>
<input id="language" name="language" type="hidden" value="${language}"/>
</form>
</body>
</html>
