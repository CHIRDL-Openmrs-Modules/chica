<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ page import="org.openmrs.module.chirdlutil.util.Util"%>
<%@ page import="org.openmrs.Patient" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/diabetesHistory.form" />
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/diabetesHistory.css">
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/diabetesHistory.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>

<%@ include file="specialCharacters.jsp" %>

<!-- Titles/Headers/Footers/Buttons/Copyright  -->
<c:set var="formName" value="Diabetes History Questionnaire:"/>
<c:set var="formName_sp" value="Diabetes History Questionnaire:(SPANISH)"/>
<c:set var="headerInterimDiabetesHistory" value='INTERIM DIABETES HISTORY${colon}'/>
<c:set var="headerInterimDiabetesHistory_sp" value='INTERIM DIABETES HISTORY (SPANISH)'/>
<c:set var="headerHypoglycemia" value='HYPOGLYCEMIA${colon}'/>
<c:set var="headerHypoglycemia_sp" value='HYPOGLYCEMIA(SPANISH)'/>
<c:set var="headerHyperglycemia" value='HYPERGLYCEMIA${colon}'/>
<c:set var="headerHyperglycemia_sp" value='HYPERGLYCEMIA(SPANISH)'/>
<c:set var="headerFormCompleted" value='Diabetes History Questionnaire Complete'/>
<c:set var="staffButtonText" value="Staff"/>
<c:set var="staffButtonText_sp" value="Personal"/>


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
<c:set var="question9" value='Do you check for ketones every time you feel sick, throw up, or have an illness${questionMark}' scope="request"/>



<!-- Questions (Spanish) -->
<c:set var="question1_2" value='${invQuestionMark} ${questionMark} one' scope="request"/>
<c:set var="question2_2" value='${invQuestionMark} ${questionMark} two' scope="request"/>
<c:set var="question10_2" value='${invQuestionMark} ${questionMark} ten' scope="request"/>
<c:set var="question11_2" value='${invQuestionMark} ${questionMark} eleven' scope="request"/>
<c:set var="question12_2"  value='${invQuestionMark} ${questionMark} twelve' scope="request"/>
<c:set var="question13_2"  value='${invQuestionMark} ${questionMark} thirteen' scope="request"/>
<c:set var="question3_2" value='${invQuestionMark} ${questionMark} three' scope="request"/>
<c:set var="question4_2" value='${invQuestionMark} ${questionMark} four' scope="request"/>
<c:set var="question5_2" value='${invQuestionMark} ${questionMark} five' scope="request"/>
<c:set var="question6_2" value='${invQuestionMark} ${questionMark} six' scope="request"/>
<c:set var="question7_2" value='${invQuestionMark} ${questionMark} seven' scope="request"/>
<c:set var="question8_2" value='${invQuestionMark} ${questionMark} eight' scope="request"/>
<c:set var="question9_2" value='${invQuestionMark} ${questionMark} nine' scope="request"/>




<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
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
        <h1 id="formTitle">${formName}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">${staffButtonText}</a>
    </div>
	<div data-role="content" id="informationContent">
		<strong><span id="instructions"></span></strong>
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
        <strong><span>The Diabetes History form has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-inline="true" data-theme="b" data-role="button" data-rel="back" style="width: 150px;">Back</a>
    </div>
</div>


<c:set var="copyright" value=''/>
<!-- Form pages (English)-->
	<c:set var="PNumber" value="1" />
	<div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">${staffButtonText}</a>
	    </div>
	    
	    <div id="content_${PNumber}" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory}</h3><hr/></div>
	            
	        <c:set var="QNumber" value="1" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script> insertYESNO("${QNumber}");</script></div>
	        
			<c:set var="QNumber" value="2" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertInsulinMethodChoices("${QNumber}");</script></div>
			
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
			
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
	        <a id="Next" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber} + 2)" style="width: 150px;">Next</a>
			<a id="NextNoInsulin" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+3)" style="width: 150px;">Next</a>
			<a id="NextShotsPump" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Next</a>
	    </div>
	</div>

	<c:set var="PNumber" value="2" />
	<div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">${staffButtonText}</a>
	    </div>
	    
	    <div id="content_${PNumber}" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory}</h3><hr/></div>
	            
			<div id="question_shots_container">
				<c:set var="QNumber" value="10" />
				<input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
				<c:set var="questionName" value="question${QNumber}" />
				<strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
				<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertWhoGivesInsulinChoices("${QNumber}");</script></div>

				<c:set var="QNumber" value="11" />
				<input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
				<c:set var="questionName" value="question${QNumber}" />
				<strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
				<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertMissInsulinChoices("${QNumber}");</script></div>
			</div>
			<div id="question_insulin_pump_container">
				<c:set var="QNumber" value="12" />
				<input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
				<c:set var="questionName" value="question${QNumber}" />
				<strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
				<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>

				<c:set var="QNumber" value="13" />
				<input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
				<c:set var="questionName" value="question${QNumber}" />
				<strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
				<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertMissInsulinChoices("${QNumber}");</script></div>
			</div>
        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Previous</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Next</a>
	    </div>
	</div>

	<c:set var="PNumber" value="3" />
	<div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">${staffButtonText}</a>
	    </div>
	    
	    <div id="content_${PNumber}" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory}</h3><hr/></div>
	            
			<c:set var="QNumber" value="3" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>
			
			</br>
			<div><h3>${headerHypoglycemia}</h3><hr/></div>
	            
			<c:set var="QNumber" value="4" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
			<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>
			
			<c:set var="QNumber" value="5" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>

			<c:set var="QNumber" value="6" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>
	        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>

	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a id="PreviousFirstPage" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-2)" style="width: 150px;">Previous</a>
	        <a id="PreviousShotsPump" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Previous</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Next</a>
	    </div>
	</div>
	
	<c:set var="PNumber" value="4" />
	<div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">${staffButtonText}</a>
	    </div>
	    
	    <div id="content_${PNumber}" data-role="content">
			<div id="question_hypoglycemia_container">
				<div><h3>${headerHypoglycemia}</h3><hr/></div>

				<c:set var="QNumber" value="7" />
				<input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
				<c:set var="questionName" value="question${QNumber}" />
				<strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
				<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>
				
				</br>
			</div>
			<div><h3>${headerHyperglycemia}</h3><hr/></div>

			<c:set var="QNumber" value="8" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>

			<c:set var="QNumber" value="9" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>
	        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
			<%@ include file="mobileFinishDialogs.jsp" %>

	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
	        <a id="Previous" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Previous</a>
			<a id="PreviousNoInsulin" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-3)" style="width: 150px;">Previous</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Continue</a>
	    </div>
	</div>

	<!-- Form pages (Spanish) -->
	
	<c:set var="PNumber" value="1" />
	<div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formName_sp}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage${PNumber}SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">${staffButtonText_sp}</a>
	    </div>
	    
	    <div id="content_${PNumber}_sp" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory_sp}</h3><hr/></div>
	            
	        <c:set var="QNumber" value="1_2" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script> insertYESNO("${QNumber}");</script></div>
	        
			<c:set var="QNumber" value="2_2" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertInsulinMethodChoices("${QNumber}");</script></div>
			
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a id="Next_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber} + 2)" style="width: 150px;">Proximo</a>
			<a id="NextNoInsulin_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+3)" style="width: 150px;">Proximo</a>
			<a id="NextShotsPump_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Proximo</a>
	    </div>
	</div>

	<c:set var="PNumber" value="2" />
	<div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formName_sp}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage${PNumber}SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">${staffButtonText_sp}</a>
	    </div>
	    
	    <div id="content_${PNumber_sp}" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory_sp}</h3><hr/></div>
	            
			<div id="question_shots_container_sp">
				<c:set var="QNumber" value="10_2" />
				<input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
				<c:set var="questionName" value="question${QNumber}" />
				<strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
				<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertWhoGivesInsulinChoices("${QNumber}");</script></div>

				<c:set var="QNumber" value="11_2" />
				<input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
				<c:set var="questionName" value="question${QNumber}" />
				<strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
				<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertMissInsulinChoices("${QNumber}");</script></div>
			</div>
			<div id="question_insulin_pump_container_sp">
				<c:set var="QNumber" value="12_2" />
				<input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
				<c:set var="questionName" value="question${QNumber}" />
				<strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
				<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>

				<c:set var="QNumber" value="13_2" />
				<input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
				<c:set var="questionName" value="question${QNumber}" />
				<strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
				<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertMissInsulinChoices("${QNumber}");</script></div>
			</div>
	        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Anterior</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Proximo</a>
	    </div>
	</div>

	<c:set var="PNumber" value="3" />
	<div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formName_sp}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage${PNumber}SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">${staffButtonText_sp}</a>
	    </div>
	    
	    <div id="content_${PNumber}" data-role="content">
	    
	        <div><h3>${headerInterimDiabetesHistory_sp}</h3><hr/></div>
	            
			<c:set var="QNumber" value="3_2" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>
			
			</br>
			<div><h3>${headerHypoglycemia_sp}</h3><hr/></div>
	            
			<c:set var="QNumber" value="4_2" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
			<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>
			
			<c:set var="QNumber" value="5_2" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>

			<c:set var="QNumber" value="6_2" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>
	        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a id="PreviousFirstPage_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-2)" style="width: 150px;">Anterior</a>
	        <a id="PreviousShotsPump_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Anterior</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Proximo</a>
	    </div>
	</div>
	
	<c:set var="PNumber" value="4" />
	<div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header">
	        <h1>${formName_sp}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage${PNumber}SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">${staffButtonText_sp}</a>
	    </div>
	    
	    <div id="content_${PNumber}" data-role="content">
			<div id="question_hypoglycemia_container_sp">
				<div><h3>${headerHypoglycemia_sp}</h3><hr/></div>

				<c:set var="QNumber" value="7_2" />
				<input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
				<c:set var="questionName" value="question${QNumber}" />
				<strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
				<div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>
				
				</br>
			</div>
			<div><h3>${headerHyperglycemia_sp}</h3><hr/></div>

			<c:set var="QNumber" value="8_2" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>

			<c:set var="QNumber" value="9_2" />
	        <input id="DiabHistQuestion_${QNumber}" name="DiabHistQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
	        <c:set var="questionName" value="question${QNumber}" />
	        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertYESNO("${QNumber}");</script></div>
	        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
			<%@ include file="mobileFinishDialogs_SP.jsp" %>

	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a id="Previous_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Anterior</a>
			<a id="PreviousNoInsulin_sp" href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-3)" style="width: 150px;">Anterior</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Continuar</a>
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
	<input id="DiabetesInterpretation" name="DiabetesInterpretation" type="hidden"/>
	<input id="DiabetesHistory" name="DiabetesHistory" type="hidden"/>
	<input id="DiabetesHistoryAction" name="DiabetesHistoryAction" type="hidden"/>
	
</form>
</body>
</html>
