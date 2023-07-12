<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ page import="org.openmrs.module.chirdlutil.util.Util"%>
<%@ page import="org.openmrs.Patient" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/additionalInformationMobile.form" />
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/additionalInformationMobile.css">
<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/additionalInformationMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<c:set var="formName" value="Additional Information Form:"/>
<c:set var="formName_sp" value="El formulario de informaci&oacute;n adicional"/>
<c:set var="quitButtonText" value="Quit"/>
<c:set var="quitButtonText_sp" value="Dejar"/>

<c:set var="possessiveFirstName" value="${patient.givenName.endsWith(\"s\") ? patient.givenName.concat(\"\'\") : patient.givenName.concat(\"\'s\")}"/>

<c:set var="answerTextMother" value="${possessiveFirstName} mother"/>
<c:set var="answerTextFather" value="${possessiveFirstName} father"/>
<c:set var="answerTextGrandmother" value="${possessiveFirstName} grandmother"/>
<c:set var="answerTextGrandfather" value="${possessiveFirstName} grandfather"/>
<c:set var="answerTextAunt" value="${possessiveFirstName} aunt"/>
<c:set var="answerTextUncle" value="${possessiveFirstName} uncle"/>
<c:set var="answerTextFosterParent" value="${possessiveFirstName} foster parent"/>
<c:set var="answerTextSibling" value="${possessiveFirstName} sibling"/>
<c:set var="answerTextSiblings" value="${possessiveFirstName} siblings"/>
<c:set var="answerTextOther" value="Other"/>
<c:set var="answerTextHereAlone" value="No one (${patient.givenName} is here alone)"/>

<c:set var="answerTextMother_SP" value="La madre de ${patient.givenName}"/>
<c:set var="answerTextFather_SP" value="El padre de ${patient.givenName}"/>
<c:set var="answerTextGrandmother_SP" value="La abuela de ${patient.givenName}"/>
<c:set var="answerTextGrandfather_SP" value="El abuelo de ${patient.givenName}"/>
<c:set var="answerTextAunt_SP" value="La t&iacute;a de ${patient.givenName}"/>
<c:set var="answerTextUncle_SP" value="El t&iacute;o de ${patient.givenName}"/>
<c:set var="answerTextFosterParent_SP" value="Un padre sustituto de ${patient.givenName}"/>
<c:set var="answerTextSibling_SP" value="Los hermano de ${patient.givenName}"/>
<c:set var="answerTextSiblings_SP" value="Los hermanos de ${patient.givenName}"/>
<c:set var="answerTextOther_SP" value="Otro"/>
<c:set var="answerTextHereAlone_SP" value="Nadie (${patient.givenName} est&aacute; aqu&iacute; solo/a)"/>

<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
<form id="AdditionalInformationForm" method="POST" action="additionalInformationMobile.form" method="post" enctype="multipart/form-data">
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
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
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
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
    </div>
    <div id="content_1" data-role="content">
        <c:set var="quest1" value='Who answered questions on the tablet today?'/>
        <input id="Question_1" name="Question_1" type="hidden" value="${quest1}"/>
	    <strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest1}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
				<label for="Informant_1">Select all that apply</label>
				<select name="Informant_1" id="Informant_1" multiple="multiple" data-native-menu="false">
					<option name="Informant_1" id="Informant_1_0" value=""/>
					<label for="Informant_1_0">Select all that apply</label>
					
					<c:if test="${AgeInYears>=12.0}">
				  		<option name="Informant_1" id="Informant_1_Self" value="self" data-theme="b" />
						<label for="Informant_1_Self">${patient.givenName}</label>
				  	</c:if>
				  	
					<option name="Informant_1" id="Informant_1_mother" value="mother" data-theme="b" />
					<label for="Informant_1_mother">${answerTextMother}</label>
					<option name="Informant_1" id="Informant_1_father" value="father" data-theme="b" />
					<label for="Informant_1_father">${answerTextFather}</label>
					<option name="Informant_1" id="Informant_1_gm" value="grandmother" data-theme="b" />
					<label for="Informant_1_gm">${answerTextGrandmother}</label>
					<option name="Informant_1" id="Informant_1_gf" value="grandfather" data-theme="b" />
					<label for="Informant_1_gf">${answerTextGrandfather}</label>
					<option name="Informant_1" id="Informant_1_aunt" value="aunt" data-theme="b" />
					<label for="Informant_1_aunt">${answerTextAunt}</label>
					<option name="Informant_1" id="Informant_1_uncle" value="uncle" data-theme="b" />
					<label for="Informant_1_uncle">${answerTextUncle}</label>
					<option name="Informant_1" id="Informant_1_fp" value="foster parent" data-theme="b" />
					<label for="Informant_1_fp">${answerTextFosterParent}</label>
					<option name="Informant_1" id="Informant_1_sibling" value="sibling" data-theme="b" />
					<label for="Informant_1_sibling">${answerTextSibling}</label>
					
					<c:if test="${AgeInYears<12.0}">
				  		<option name="Informant_1" id="Informant_1_Self" value="self" data-theme="b" />
						<label for="Informant_1_Self">${patient.givenName}</label>
				  	</c:if>
				  	
					<option name="Informant_1" id="Informant_1_other" value="Other" data-theme="b" />
					<label for="Informant_1_other">${answerTextOther}</label>
				</select>
	        </fieldset>
	    </div>
	    <input id="Informant_1_MultiSelect" name="QuestionnaireInformants" type="hidden" value=""/>
    </div>
    <div id="content_2" data-role="content">
    <c:set var="quest2" value='Who is at the visit with ${patient.givenName}?'/>
        <input id="Question_2" name="Question_2" type="hidden" value="${quest2}"/>
        <strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
			<fieldset data-role="controlgroup" data-type="vertical">
				<label for="Informant_2">Select all that apply</label>
				<select name="Informant_2" id="Informant_2" multiple="multiple" data-native-menu="false">
				  <option name="Informant_2" id="Informant_2_0"/>
				  <label for="Informant_2_0">Select all that apply</label>
				  
				 <c:if test="${AgeInYears>=12.0}">
				  	<option name="Informant_2" value="No One" id="Informant_2_noone" data-theme="b" />
				  	<label for="Informant_2_noone">${answerTextHereAlone}</label>
				  </c:if>
				  
				  <option name="Informant_2" value="mother" id="Informant_2_mother" data-theme="b" />
				  <label for="Informant_2_mother">${answerTextMother}</label>
				  <option name="Informant_2" value="father" id="Informant_2_father" data-theme="b" />
				  <label for="Informant_2_father">${answerTextFather}</label>
				  <option name="Informant_2" value="grandmother" id="Informant_2_gm" data-theme="b" />
				  <label for="Informant_2_gm">${answerTextGrandmother}</label>
				  <option name="Informant_2" value="grandfather" id="Informant_2_gf" data-theme="b" />
				  <label for="Informant_2_gf">${answerTextGrandfather}</label>
				  <option name="Informant_2" value="aunt" id="Informant_2_aunt" data-theme="b" />
				  <label for="Informant_2_aunt">${answerTextAunt}</label>
				  <option name="Informant_2" value="uncle" id="Informant_2_uncle" data-theme="b" />
				  <label for="Informant_2_uncle">${answerTextUncle}</label>
				  <option name="Informant_2" value="foster parent" id="Informant_2_fp" data-theme="b" />
				  <label for="Informant_2_fp">${answerTextFosterParent}</label>
				  <option name="Informant_2" value="sibling" id="Informant_2_sibling" data-theme="b" />
				  <label for="Informant_2_sibling">${answerTextSiblings}</label>
				  <option name="Informant_2" value="Other" id="Informant_2_other" data-theme="b" />
				  <label for="Informant_2_other">${answerTextOther}</label>
				  	  
				  <c:if test="${AgeInYears<12.0}">
				  	<option name="Informant_2" value="No One" id="Informant_2_noone" data-theme="b" />
				  	<label for="Informant_2_noone">${answerTextHereAlone}</label>
				  </c:if>
				  
				</select>
			</fieldset>
		</div>
		<input id="Informant_2_MultiSelect" name="Visit_Attendee" type="hidden" value=""/>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
        		
        <%@ include file="mobileFinishDialogs.jsp" %>
        
        </div>
        
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a  href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Continue</a>
    </div>
</div>

<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
   <div data-role="header" >
        <h1>${formName_sp}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
    </div>
    <div id="content_1_sp" data-role="content">
        <c:set var="quest1_2" value='&iquest;Qui&eacute;n respondi&oacute; a las preguntas en la tableta hoy?'/>
        <input id="Question_1_2" name="Question_1_2" type="hidden" value="${quest1_2}"/>
	    <strong>${quest1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	        	<label for="Informant_1_2">Elija todos que aplican</label>
				<select name="Informant_1_2" id="Informant_1_2" multiple="multiple" data-native-menu="false">
					<option name="Informant_1_2" id="Informant_1_2_0" value=""/>
					<label for="Informant_1_2_0">Elija todos que aplican</label>
					
					<c:if test="${AgeInYears>=12.0}">
				  		<option name="Informant_1_2" id="Informant_1_2_self" value="self" data-theme="b" />
						<label for="Informant_1_2_self">${patient.givenName}</label>
				  	</c:if>
				  				
					<option name="Informant_1_2" id="Informant_1_2_mother" value="mother" data-theme="b" />
					<label for="Informant_1_2_mother">${answerTextMother_SP}</label>
					<option name="Informant_1_2" id="Informant_1_2_father" value="father" data-theme="b" />
					<label for="Informant_1_2_father">${answerTextFather_SP}</label>
					<option name="Informant_1_2" id="Informant_1_2_gm" value="grandmother" data-theme="b" />
					<label for="Informant_1_2_gm">${answerTextGrandmother_SP}</label>
					<option name="Informant_1_2" id="Informant_1_2_gf" value="grandfather" data-theme="b" />
					<label for="Informant_1_2_gf">${answerTextGrandfather_SP}</label>
					<option name="Informant_1_2" id="Informant_1_2_aunt" value="aunt" data-theme="b" />
					<label for="Informant_1_2_aunt">${answerTextAunt_SP}</label>
					<option name="Informant_1_2" id="Informant_1_2_uncle" value="uncle" data-theme="b" />
					<label for="Informant_1_2_uncle">${answerTextUncle_SP}</label>
					<option name="Informant_1_2" id="Informant_1_2_fp" value="foster parent" data-theme="b" />
					<label for="Informant_1_2_fp">${answerTextFosterParent_SP}</label>
					<option name="Informant_1_2" id="Informant_1_2_sibling" value="sibling" data-theme="b" />
					<label for="Informant_1_2_sibling">${answerTextSibling_SP}</label>
					
					<c:if test="${AgeInYears<12.0}">
				  		<option name="Informant_1_2" id="Informant_1_2_self" value="self" data-theme="b" />
						<label for="Informant_1_2_self">${patient.givenName}</label>
				  	</c:if>
				  	
					<option name="Informant_1_2" id="Informant_1_2_other" value="Other" data-theme="b" />
					<label for="Informant_1_2_other">${answerTextOther_SP}</label>
				</select>
	        </fieldset>
	    </div>
    </div>
    <div id="content_2_sp" data-role="content">
    <c:set var="quest2_2" value='&iquest;Qui&eacute;n esta en la cita con ${patient.givenName}?'/>
        <input id="Question_2_2" name="Question_2_2" type="hidden" value="${quest2_2}"/>
        <strong>${quest2_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
			<fieldset data-role="controlgroup" data-type="vertical">
				<label for="Informant_2_2">Elija todos que aplican</label>
				<select name="Informant_2_2" id="Informant_2_2" multiple="multiple" data-native-menu="false">
				  <option name="Informant_2_2" id="Informant_2_2_0"/>
				  <label for="Informant_2_2_0">Elija todos que aplican</label>
				  
				  <c:if test="${AgeInYears>=12.0}">
				  	<option name="Informant_2_2" value="No One" id="Informant_2_2_noone" data-theme="b" />
				  	<label for="Informant_2_2_noone">${answerTextHereAlone_SP}</label>
				  </c:if>
				  
				  <option name="Informant_2_2" value="mother" id="Informant_2_2_mother" data-theme="b" />
				  <label for="Informant_2_2_mother">${answerTextMother_SP}</label>
				  <option name="Informant_2_2" value="father" id="Informant_2_2_father" data-theme="b" />
				  <label for="Informant_2_2_father">${answerTextFather_SP}</label>
				  <option name="Informant_2_2" value="grandmother" id="Informant_2_2_gm" data-theme="b" />
				  <label for="Informant_2_2_gm">${answerTextGrandmother_SP}</label>
				  <option name="Informant_2_2" value="grandfather" id="Informant_2_2_gf" data-theme="b" />
				  <label for="Informant_2_2_gf">${answerTextGrandfather_SP}</label>
				  <option name="Informant_2_2" value="aunt" id="Informant_2_2_aunt" data-theme="b" />
				  <label for="Informant_2_2_aunt">${answerTextAunt_SP}</label>
				  <option name="Informant_2_2" value="uncle" id="Informant_2_2_uncle" data-theme="b" />
				  <label for="Informant_2_2_uncle">${answerTextUncle_SP}</label>
				  <option name="Informant_2_2" value="foster parent" id="Informant_2_2_fp" data-theme="b" />
				  <label for="Informant_2_2_fp">${answerTextFosterParent_SP}</label>
				  <option name="Informant_2_2" value="sibling" id="Informant_2_2_sibling" data-theme="b" />
				  <label for="Informant_2_2_sibling">${answerTextSiblings_SP}</label>
				  <option name="Informant_2_2" value="Other" id="Informant_2_2_other" data-theme="b" />
				  <label for="Informant_2_2_other">${answerTextOther_SP}</label>
				  
				  <c:if test="${AgeInYears<12.0}">
				  	<option name="Informant_2_2" value="No One" id="Informant_2_2_noone" data-theme="b" />
				  	<label for="Informant_2_2_noone">${answerTextHereAlone_SP}</label>
				  </c:if>
				  
				</select>
			</fieldset>
		</div>
		<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
        		
        <%@ include file="mobileFinishDialogs_SP.jsp" %>
        
	</div>
        
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a  href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Continuar</a>
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
<input type="hidden" name="ageInYears" id="ageInYears" value="${AgeInYears}" />
</form>
</body>
</html>
