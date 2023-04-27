<%@ include file="/WEB-INF/template/include.jsp"%>

<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/diabetesHistory.form" />
<html xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:openmrs="urn:jsptld:/WEB-INF/view/module/legacyui/taglibs/openmrs.tld"
   >
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/diabetesHistory.css">
        <openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />        
		<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
		<script>var ctx = "${pageContext.request.contextPath}";</script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/diabetesHistory.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
	</head>
	
	<%@ include file="specialCharacters.jsp" %>
	
	<c:set var="search" value="'" />
	<c:set var="replace" value="\\'" />
	<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
	<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>

	<!-- Titles/Headers/Footers/Buttons/Copyright  -->
	<c:set var="formName" value="Diabetes History Questionnaire"/>
	<c:set var="formName_sp" value="Cuestionario sobre historia de diabetes"/>
	<c:set var="formNameHeader" value="${formName}${colon}"/>
	<c:set var="formNameHeader_sp" value="${formName_sp}${colon}"/>
	<input type="hidden" name="formNameHeader" id="formNameHeader" value="${formNameHeader}" />
	<input type="hidden" name="formNameHeader_sp" id="formNameHeader_sp" value="${formNameHeader_sp}" />
	<c:set var="headerInterimDiabetesHistory" value='INTERIM DIABETES HISTORY${colon}'/>
	<c:set var="headerInterimDiabetesHistory_sp" value='HISTORIA DE DIABETES EN EL ENTRETANTO${colon}'/>
	<c:set var="headerHypoglycemia" value='HYPOGLYCEMIA${colon}'/>
	<c:set var="headerHypoglycemia_sp" value='HIPOGLICEMIA${colon}'/>
	<c:set var="headerHyperglycemia" value='HYPERGLYCEMIA${colon}'/>
	<c:set var="headerHyperglycemia_sp" value='HIPERGLICEMIA${colon}'/>
	<c:set var="prefix" value="DiabetesHistory_" />
	
	<c:set var="instructions_additionalQuestions" value="The following are some additional questions about your diabetes history${period}" />
	<c:set var="instructions_additionalQuestions_sp" value="Las siguientes son algunas preguntas adicionales sobre su historia de diabetes${period}" />

	<!-- Questions (English) -->
	<c:set var="question1"  value='Have you been hospitalized or in the Emergency Room since your last clinic visit${questionMark}' scope="request"/>
	<c:set var="question2"  value='How do you give your insulin${questionMark}' scope="request"/>
	<c:set var="question10"  value='Who gives your insulin injections${questionMark}' scope="request"/>
	<c:set var="question11"  value='How often do you forget${slash}miss insulin injections${questionMark}' scope="request"/>
	<c:set var="question12"  value='Can you make changes in your insulin pump on your own${questionMark}' scope="request"/>
	<c:set var="question13"  value='How often do you forget${slash}miss bolus insulin when you eat${questionMark}' scope="request"/>
	<c:set var="question3"  value='Do you take your insulin before you start eating${questionMark}' scope="request"/>
	<c:set var="question4"  value='Do you have symptoms of low blood sugar when your blood sugar is between 60-70${questionMark}' scope="request"/>
	<c:set var="question5"  value='Since your last clinic visit${comma} have you been confused and unable to treat a low blood sugar without help${questionMark}' scope="request"/>
	<c:set var="question6"  value='Since your last clinic visit${comma} have you had a seizure${comma} or been unconscious${comma} when you had a low blood sugar${questionMark}' scope="request"/>
	<c:set var="question7"  value='Do you have a glucagon emergency kit${questionMark}' scope="request"/>
	<c:set var="question8" value='Do you have ketone strips${questionMark}' scope="request"/>
	<c:set var="question9" value='Do you check for ketones every time you feel sick${comma} throw up or have an illness${questionMark}' scope="request"/>

	<!-- Questions (Spanish) -->
	<c:set var="question1_2" value='${invQuestionMark}Ha estado usted hospitalizado o en la sala de emergencia desde su ${uAcute}ltima visita a la cl${iAcute}nica${questionMark}' scope="request"/>
	<c:set var="question2_2" value='${invQuestionMark}C${oAcute}mo se aplica usted la insulina${questionMark}' scope="request"/>
	<c:set var="question10_2" value='${invQuestionMark}Qui${eAcute}n le aplica a usted las inyecciones de insulina${questionMark}' scope="request"/>
	<c:set var="question11_2" value='${invQuestionMark}Con cu${aAcute}nta frecuencia olvida${slash}deja usted de aplicarse las inyecciones de insulina${questionMark}' scope="request"/>
	<c:set var="question12_2"  value='${invQuestionMark}Puede usted hacer cambios en su bomba de insulina por s${iAcute} mismo${questionMark}' scope="request"/>
	<c:set var="question13_2"  value='${invQuestionMark}Con cu${aAcute}nta frecuencia olvida${slash}deja usted de aplicarse la insulina embolada cuando come${questionMark}' scope="request"/>
	<c:set var="question3_2" value='${invQuestionMark}Toma usted la insulina antes de comenzar a comer${questionMark}' scope="request"/>
	<c:set var="question4_2" value='${invQuestionMark}Tiene usted s${iAcute}ntomas de baja az${uAcute}car en la sangre cuando el az${uAcute}car en la sangre est${aAcute} entre 60-70${questionMark}' scope="request"/>
	<c:set var="question5_2" value='Desde su ${uAcute}ltima visita a la cl${iAcute}nica, ${invQuestionMark}se ha sentido usted confundido e incapaz de tratar una baja de az${uAcute}car sin ayuda${questionMark}' scope="request"/>
	<c:set var="question6_2" value='Desde su ${uAcute}ltima visita a la cl${iAcute}nica, ${invQuestionMark}ha tenido usted una convulsi${oAcute}n o ha estado inconsciente cuando tuvo una baja de az${uAcute}car en la sangre${questionMark}' scope="request"/>
	<c:set var="question7_2" value='${invQuestionMark}Cuenta usted con un kit de emergencia de glucagon${questionMark}' scope="request"/>
	<c:set var="question8_2" value='${invQuestionMark}Tiene usted bandas de cetona${questionMark}' scope="request"/>
	<c:set var="question9_2" value='${invQuestionMark}Realiza usted el control de las cetonas cada vez que se siente enfermo, vomita o tiene una enfermedad${questionMark}' scope="request"/>
	
	<input type="hidden" name="instructions_additionalQuestions" id="instructions_additionalQuestions" value="${instructions_additionalQuestions}" />
	<input type="hidden" name="instructions_additionalQuestions_sp" id="instructions_additionalQuestions_sp" value="${instructions_additionalQuestions_sp}" />

<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}', 'DiabetesHistoryForm', 'DiabHistQuestion_', 'DiabetesHistory_')">
<form id="DiabetesHistoryForm" method="POST" action="diabetesHistory.form" method="post" enctype="multipart/form-data">
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

<!-- Instructions/Start page -->
<div data-role="page" id="instruction_page" data-theme="b">
    <div data-role="header" >
        <h1 id="formTitle">${formNameHeader}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Quit</a>
    </div>
	<div data-role="content" >
		<strong><span id="additionalQuestions">${instructions_additionalQuestions}</span></strong>
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
        <strong><span>The ${formName} form has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-inline="true" data-theme="b" data-role="button" data-rel="back" style="width: 150px;">Back</a>
    </div>
</div>

<%@ include file="mobileQuitConfirmDialog.jsp" %>

<c:set var="copyright" value=''/>
<!-- Form pages (English)-->
	<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formNameHeader}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Quit</a>
	    </div>
	    
	    <div id="content_1" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory}</h3><hr/></div>

			<input id="DiabHistQuestion_1" name="DiabHistQuestion_1" type="hidden" value="${question1}" />
			<strong>${question1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question1}")'></a>
			<div class="choice_1" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "1", false);</script></div>

			<input id="DiabHistQuestion_2" name="DiabHistQuestion_2" type="hidden" value="${question2}" />
	        <strong>${question2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question2}")'></a>
	        <div class="choice_2" data-role="fieldcontain" style="margin-top:0px;">
				<fieldset data-role="controlgroup" data-type="vertical">
					<input type="radio" name="DiabetesHistory_2" id="DiabetesHistory_2_NOT_ON_INSULIN" value="1" data-theme="b" />
					<label for="DiabetesHistory_2_NOT_ON_INSULIN">I am not on insulin</label>
					<input type="radio" name="DiabetesHistory_2" id="DiabetesHistory_2_SHOTS" value="2" data-theme="b" />
					<label for="DiabetesHistory_2_SHOTS">Shots</label>
					<input type="radio" name="DiabetesHistory_2" id="DiabetesHistory_2_INSULIN_PUMP" value="3" data-theme="b" />
					<label for="DiabetesHistory_2_INSULIN_PUMP">Insulin pump</label>
				</fieldset>
			</div>
			
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
			
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
	        <a id="Next" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Next</a>
			<a id="NextNoInsulin" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Next</a>
			<a id="NextShotsPump" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Next</a>
	    </div>
	</div>

	<div id="question_page_2" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formNameHeader}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage2Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Quit</a>
	    </div>
	    
	    <div id="content_2" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory}</h3><hr/></div>
	            
			<div id="question_shots_container">
				<input id="DiabHistQuestion_10" name="DiabHistQuestion_10" type="hidden" value="${question10}" />
				<strong>${question10}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question10}")'></a>
				<div class="choice_10" data-role="fieldcontain" style="margin-top:0px;">
					<fieldset data-role="controlgroup" data-type="vertical">
						<input type="radio" name="DiabetesHistory_10" id="DiabetesHistory_10_ME" value="1" data-theme="b" />
						<label for="DiabetesHistory_10_ME">Me</label>
						<input type="radio" name="DiabetesHistory_10" id="DiabetesHistory_10_PARENT_OTHER" value="2" data-theme="b" />
						<label for="DiabetesHistory_10_PARENT_OTHER">A parent / someone else</label>
						<input type="radio" name="DiabetesHistory_10" id="DiabetesHistory_10_ALL" value="3" data-theme="b" />
						<label for="DiabetesHistory_10_ALL">Both me and a parent/someone else</label>
					</fieldset>
				</div>

				<input id="DiabHistQuestion_11" name="DiabHistQuestion_11" type="hidden" value="${question11}" />
				<strong>${question11}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question11}")'></a>
				<div class="choice_11" data-role="fieldcontain" style="margin-top:0px;">
					<fieldset data-role="controlgroup" data-type="vertical">
						<input type="radio" name="DiabetesHistory_11" id="DiabetesHistory_11_MORE_THAN_ONCE_WEEK" value="1" data-theme="b" />
						<label for="DiabetesHistory_11_MORE_THAN_ONCE_WEEK">More than once a week</label>
						<input type="radio" name="DiabetesHistory_11" id="DiabetesHistory_11_ONCE_A_WEEK" value="2" data-theme="b" />
						<label for="DiabetesHistory_11_ONCE_A_WEEK">About once a week</label>
						<input type="radio" name="DiabetesHistory_11" id="DiabetesHistory_11_MORE_THAN_ONCE_MONTH" value="3" data-theme="b" />
						<label for="DiabetesHistory_11_MORE_THAN_ONCE_MONTH">More than once a month but less than once a week</label>
						<input type="radio" name="DiabetesHistory_11" id="DiabetesHistory_11_ONCE_A_MONTH" value="4" data-theme="b" />
						<label for="DiabetesHistory_11_ONCE_A_MONTH">About once a month</label>
						<input type="radio" name="DiabetesHistory_11" id="DiabetesHistory_11_LESS_THAN_ONCE_MONTH" value="5" data-theme="b" />
						<label for="DiabetesHistory_11_LESS_THAN_ONCE_MONTH">Less than once a month</label>
					</fieldset>
				</div>
			</div>
			<div id="question_insulin_pump_container">
				<input id="DiabHistQuestion_12" name="DiabHistQuestion_12" type="hidden" value="${question12}" />
				<strong>${question12}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question12}")'></a>
				<div class="choice_12" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "12", false);</script></div>

				<input id="DiabHistQuestion_13" name="DiabHistQuestion_13" type="hidden" value="${question13}" />
				<strong>${question13}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question13}")'></a>
				<div class="choice_13" data-role="fieldcontain" style="margin-top:0px;">
					<fieldset data-role="controlgroup" data-type="vertical">
						<input type="radio" name="DiabetesHistory_13" id="DiabetesHistory_13_MORE_THAN_ONCE_WEEK" value="1" data-theme="b" />
						<label for="DiabetesHistory_13_MORE_THAN_ONCE_WEEK">More than once a week</label>
						<input type="radio" name="DiabetesHistory_13" id="DiabetesHistory_13_ONCE_A_WEEK" value="2" data-theme="b" />
						<label for="DiabetesHistory_13_ONCE_A_WEEK">About once a week</label>
						<input type="radio" name="DiabetesHistory_13" id="DiabetesHistory_13_MORE_THAN_ONCE_MONTH" value="3" data-theme="b" />
						<label for="DiabetesHistory_13_MORE_THAN_ONCE_MONTH">More than once a month but less than once a week</label>
						<input type="radio" name="DiabetesHistory_13" id="DiabetesHistory_13_ONCE_A_MONTH" value="4" data-theme="b" />
						<label for="DiabetesHistory_13_ONCE_A_MONTH">About once a month</label>
						<input type="radio" name="DiabetesHistory_13" id="DiabetesHistory_13_LESS_THAN_ONCE_MONTH" value="5" data-theme="b" />
						<label for="DiabetesHistory_13_LESS_THAN_ONCE_MONTH">Less than once a month</label>
					</fieldset>
				</div>
			</div>
        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Previous</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Next</a>
	    </div>
	</div>

	<div id="question_page_3" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formNameHeader}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage3Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Quit</a>
	    </div>
	    
	    <div id="content_3" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory}</h3><hr/></div>
	            
	        <input id="DiabHistQuestion_3" name="DiabHistQuestion_3" type="hidden" value="${question3}" />
	        <strong>${question3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question3}")'></a>
			<div class="choice_3" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "3", false);</script></div>
			
			</br>
			<div><h3>${headerHypoglycemia}</h3><hr/></div>
	            
	        <input id="DiabHistQuestion_4" name="DiabHistQuestion_4" type="hidden" value="${question4}" />
	        <strong>${question4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question4}")'></a>
			<div class="choice_4" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "4", false);</script></div>
			
			<input id="DiabHistQuestion_5" name="DiabHistQuestion_5" type="hidden" value="${question5}" />
	        <strong>${question5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question5}")'></a>
	        <div class="choice_5" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "5", false);</script></div>
			
			<input id="DiabHistQuestion_6" name="DiabHistQuestion_6" type="hidden" value="${question6}" />
	        <strong>${question6}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question6}")'></a>
	        <div class="choice_6" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "6", false);</script></div>
	        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>

	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a id="PreviousFirstPage" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Previous</a>
	        <a id="PreviousShotsPump" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Previous</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Next</a>
	    </div>
	</div>
	
	<div id="question_page_4" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formNameHeader}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage4Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Quit</a>
	    </div>
	    
	    <div id="content_4" data-role="content">
			<div id="question_hypoglycemia_container">
				<div><h3>${headerHypoglycemia}</h3><hr/></div>

				<input id="DiabHistQuestion_7" name="DiabHistQuestion_7" type="hidden" value="${question7}" />
				<strong>${question7}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question7}")'></a>
				<div class="choice_7" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "7", false);</script></div>
				
				</br>
			</div>
			<div><h3>${headerHyperglycemia}</h3><hr/></div>

			<input id="DiabHistQuestion_8" name="DiabHistQuestion_8" type="hidden" value="${question8}" />
			<strong>${question8}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question8}")'></a>
			<div class="choice_8" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "8", false);</script></div>

			<input id="DiabHistQuestion_9" name="DiabHistQuestion_9" type="hidden" value="${question9}" />
			<strong>${question9}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question9}")'></a>
			<div class="choice_9" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "9", false);</script></div>
	        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
			<%@ include file="mobileFinishDialogs.jsp" %>

	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
	        <a id="Previous" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Previous</a>
			<a id="PreviousNoInsulin" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Previous</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Continue</a>
	    </div>
	</div>

	<!-- Form pages (Spanish) -->
	
	<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formNameHeader_sp}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
	        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Dejar</a>
	    </div>
	    
	    <div id="content_1_sp" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory_sp}</h3><hr/></div>
	            
	        <input id="DiabHistQuestion_1_2" name="DiabHistQuestion_1_2" type="hidden" value="${question1_2}" />
	        <strong>${question1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question1_2}")'></a>
	        <div class="choice_1_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "1", true);</script></div>
	        
	        <input id="DiabHistQuestion_2_2" name="DiabHistQuestion_2_2" type="hidden" value="${question2_2}" />
	        <strong>${question2_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question2_2}")'></a>
	        <div class="choice_2_2" data-role="fieldcontain" style="margin-top:0px;">
				<fieldset data-role="controlgroup" data-type="vertical">
					<input type="radio" name="DiabetesHistory_2_2" id="DiabetesHistory_2_2_NOT_ON_INSULIN" value="1" data-theme="b" />
					<label for="DiabetesHistory_2_2_NOT_ON_INSULIN">No estoy en tratamiento de insulina</label>
					<input type="radio" name="DiabetesHistory_2_2" id="DiabetesHistory_2_2_SHOTS" value="2" data-theme="b" />
					<label for="DiabetesHistory_2_2_SHOTS">Inyecciones</label>
					<input type="radio" name="DiabetesHistory_2_2" id="DiabetesHistory_2_2_INSULIN_PUMP" value="3" data-theme="b" />
					<label for="DiabetesHistory_2_2_INSULIN_PUMP">Bomba de insulina</label>
				</fieldset>
			</div>
			
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a id="Next_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Proximo</a>
			<a id="NextNoInsulin_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Proximo</a>
			<a id="NextShotsPump_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Proximo</a>
	    </div>
	</div>

	<div id="question_page_2_sp" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formNameHeader_sp}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage2SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
	        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Dejar</a>
	    </div>
	    
	    <div id="content_2_sp" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory_sp}</h3><hr/></div>
	            
			<div id="question_shots_container_sp">
				<input id="DiabHistQuestion_10_2" name="DiabHistQuestion_10_2" type="hidden" value="${question10_2}" />
				<strong>${question10_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question10_2}")'></a>
				<div class="choice_10_2" data-role="fieldcontain" style="margin-top:0px;">
					<fieldset data-role="controlgroup" data-type="vertical">
						<input type="radio" name="DiabetesHistory_10_2" id="DiabetesHistory_10_2_ME" value="1" data-theme="b" />
						<label for="DiabetesHistory_10_2_ME">Yo</label>
						<input type="radio" name="DiabetesHistory_10_2" id="DiabetesHistory_10_2_PARENT_OTHER" value="2" data-theme="b" />
						<label for="DiabetesHistory_10_2_PARENT_OTHER">Uno de los padres/alguien m${aAcute}s</label>
						<input type="radio" name="DiabetesHistory_10_2" id="DiabetesHistory_10_2_ALL" value="3" data-theme="b" />
						<label for="DiabetesHistory_10_2_ALL">Uno de los padres/alguien m${aAcute}s y yo</label>
					</fieldset>
				</div>

				<input id="DiabHistQuestion_11_2" name="DiabHistQuestion_11_2" type="hidden" value="${question11_2}" />
				<strong>${question11_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question11_2}")'></a>
				<div class="choice_11_2" data-role="fieldcontain" style="margin-top:0px;">
					<fieldset data-role="controlgroup" data-type="vertical">
						<input type="radio" name="DiabetesHistory_11_2" id="DiabetesHistory_11_2_MORE_THAN_ONCE_WEEK" value="1" data-theme="b" />
						<label for="DiabetesHistory_11_2_MORE_THAN_ONCE_WEEK">M${aAcute}s de una vez a la semana</label>
						<input type="radio" name="DiabetesHistory_11_2" id="DiabetesHistory_11_2_ONCE_A_WEEK" value="2" data-theme="b" />
						<label for="DiabetesHistory_11_2_ONCE_A_WEEK">Aproximadamente una vez a la semana</label>
						<input type="radio" name="DiabetesHistory_11_2" id="DiabetesHistory_11_2_MORE_THAN_ONCE_MONTH" value="3" data-theme="b" />
						<label for="DiabetesHistory_11_2_MORE_THAN_ONCE_MONTH">M${aAcute}s de una vez al mes pero menos de una vez a la semana</label>
						<input type="radio" name="DiabetesHistory_11_2" id="DiabetesHistory_11_2_ONCE_A_MONTH" value="4" data-theme="b" />
						<label for="DiabetesHistory_11_2_ONCE_A_MONTH">Aproximadamente una vez al mes</label>
						<input type="radio" name="DiabetesHistory_11_2" id="DiabetesHistory_11_2_LESS_THAN_ONCE_MONTH" value="5" data-theme="b" />
						<label for="DiabetesHistory_11_2_LESS_THAN_ONCE_MONTH">Menos de una vez al mes</label>
					</fieldset>
				</div>
			</div>
			<div id="question_insulin_pump_container_sp">
				<input id="DiabHistQuestion_12_2" name="DiabHistQuestion_12_2" type="hidden" value="${question12_2}" />
				<strong>${question12_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question12_2}")'></a>
				<div class="choice_12_2" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "12", true);</script></div>

				<input id="DiabHistQuestion_13_2" name="DiabHistQuestion_13_2" type="hidden" value="${question13_2}" />
				<strong>${question13_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question13_2}")'></a>
				<div class="choice_13_2" data-role="fieldcontain" style="margin-top:0px;">
					<fieldset data-role="controlgroup" data-type="vertical">
						<input type="radio" name="DiabetesHistory_13_2" id="DiabetesHistory_13_2_MORE_THAN_ONCE_WEEK" value="1" data-theme="b" />
						<label for="DiabetesHistory_13_2_MORE_THAN_ONCE_WEEK">M${aAcute}s de una vez a la semana</label>
						<input type="radio" name="DiabetesHistory_13_2" id="DiabetesHistory_13_2_ONCE_A_WEEK" value="2" data-theme="b" />
						<label for="DiabetesHistory_13_2_ONCE_A_WEEK">Aproximadamente una vez a la semana</label>
						<input type="radio" name="DiabetesHistory_13_2" id="DiabetesHistory_13_2_MORE_THAN_ONCE_MONTH" value="3" data-theme="b" />
						<label for="DiabetesHistory_13_2_MORE_THAN_ONCE_MONTH">M${aAcute}s de una vez al mes pero menos de una vez a la semana</label>
						<input type="radio" name="DiabetesHistory_13_2" id="DiabetesHistory_13_2_ONCE_A_MONTH" value="4" data-theme="b" />
						<label for="DiabetesHistory_13_2_ONCE_A_MONTH">Aproximadamente una vez al mes</label>
						<input type="radio" name="DiabetesHistory_13_2" id="DiabetesHistory_13_2_LESS_THAN_ONCE_MONTH" value="5" data-theme="b" />
						<label for="DiabetesHistory_13_2_LESS_THAN_ONCE_MONTH">Menos de una vez al mes</label>
					</fieldset>
				</div>
			</div>
	        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Anterior</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Proximo</a>
	    </div>
	</div>

	<div id="question_page_3_sp" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formNameHeader_sp}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage3SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
	        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Dejar</a>
	    </div>
	    
	    <div id="content_3_sp" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory_sp}</h3><hr/></div>
	            
			<input id="DiabHistQuestion_3_2" name="DiabHistQuestion_3_2" type="hidden" value="${question3_2}" />
	        <strong>${question3_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question3_2}")'></a>
	        <div class="choice_3_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "3", true);</script></div>
			
			</br>
			<div><h3>${headerHypoglycemia_sp}</h3><hr/></div>
	            
			<input id="DiabHistQuestion_4_2" name="DiabHistQuestion_4_2" type="hidden" value="${question4_2}" />
	        <strong>${question4_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question4_2}")'></a>
	        <div class="choice_4_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "4", true);</script></div>
			
			<input id="DiabHistQuestion_5_2" name="DiabHistQuestion_5_2" type="hidden" value="${question5_2}" />
	        <strong>${question5_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question5_2}")'></a>
	        <div class="choice_5_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "5", true);</script></div>

			<input id="DiabHistQuestion_6_2" name="DiabHistQuestion_6_2" type="hidden" value="${question6_2}" />
	        <strong>${question6_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question6_2}")'></a>
	        <div class="choice_6_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "6", true);</script></div>
	        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a id="PreviousFirstPage_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Anterior</a>
	        <a id="PreviousShotsPump_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Anterior</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Proximo</a>
	    </div>
	</div>
	
	<div id="question_page_4_sp" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formNameHeader_sp}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage4SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
	        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Dejar</a>
	    </div>
	    
	    <div id="content_4_sp" data-role="content">
			<div id="question_hypoglycemia_container_sp">
				<div><h3>${headerHypoglycemia_sp}</h3><hr/></div>

				<input id="DiabHistQuestion_7_2" name="DiabHistQuestion_7_2" type="hidden" value="${question7_2}" />
				<strong>${question7_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question7_2}")'></a>
				<div class="choice_7_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "7", true);</script></div>
				
				</br>
			</div>
			<div><h3>${headerHyperglycemia_sp}</h3><hr/></div>

			<input id="DiabHistQuestion_8_2" name="DiabHistQuestion_8_2" type="hidden" value="${question8_2}" />
			<strong>${question8_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question8_2}")'></a>
			<div class="choice_8_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "8", true);</script></div>

			<input id="DiabHistQuestion_9_2" name="DiabHistQuestion_9_2" type="hidden" value="${question9_2}" />
			<strong>${question9_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question9_2}")'></a>
			<div class="choice_9_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "9", true);</script></div>
	        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
			<%@ include file="mobileFinishDialogs_SP.jsp" %>

	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a id="Previous_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Anterior</a>
			<a id="PreviousNoInsulin_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Anterior</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Continuar</a>
	    </div>
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
	<input id="DiabetesInterpretation" name="DiabetesInterpretation" type="hidden"/>
	
</form>
</body>
</html>
