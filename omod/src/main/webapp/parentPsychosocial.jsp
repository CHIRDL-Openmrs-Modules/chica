<%@ include file="/WEB-INF/template/include.jsp"%>

<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/parentPsychosocial.form" />
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/parentPsychosocial.css">
		<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" /> 
		<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
		<script>var ctx = "${pageContext.request.contextPath}";</script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/parentPsychosocial.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
	</head>
	
	<%@ include file="specialCharacters.jsp" %>
		
	<c:set var="search" value="'" />
	<c:set var="replace" value="\\'" />
	<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
	<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>

	<!-- Titles/Headers/Footers/Buttons/Copyright  -->
	<c:set var="formName" value="Parent Psychosocial Questionnaire"/>
	<c:set var="formName_sp" value="Cuestionario psicosocial para los padres"/>
	<c:set var="formNameHeader" value="${formName}${colon}"/>
	<c:set var="formNameHeader_sp" value="${formName_sp}${colon}"/>
	<input type="hidden" name="formNameHeader" id="formNameHeader" value="${formNameHeader}" />
	<input type="hidden" name="formNameHeader_sp" id="formNameHeader_sp" value="${formNameHeader_sp}" />
	<c:set var="prefix" value="ParentPsychosocialQuestionEntry_" />

	<c:set var="instructions_additionalQuestions" value="The following are some additional questions for your parent. Your parents will not be able to look back at your previous answers${period} Please hand tablet to the parent${slash}caregiver${period}" />
	<c:set var="instructions_additionalQuestions_sp" value="Las siguientes son algunas preguntas adicionales para su padre${slash}madre${slash}cuidador. Sus padres no podr${aAcute}n ver sus respuestas anteriores${period} Entregue la tableta a su padre/madre/cuidador${period}" />

	<!-- Questions (English) -->
	<c:set var="question1"  value='In the past four weeks, did you worry that your household would not have enough food${questionMark}' scope="request"/>
	<c:set var="question2"  value='Do you ever have trouble getting to the clinic due to not having a ride or someone to drive${questionMark}' scope="request"/>
	<c:set var="question3"  value='Have you changed or lost your job since the last visit${questionMark}' scope="request"/>
	<c:set var="question4"  value='Have you lost or changed insurance coverage since the last visit${questionMark}' scope="request"/>
	<c:set var="question5"  value='Are there any concerns or problems our social worker can help you with${questionMark}' scope="request"/>
	<c:set var="question6"  value='Are there any concerns or problems our dietician can help you with${questionMark}' scope="request"/>
	<c:set var="question7"  value='Are there any concerns or problems a diabetes educator can help you with${questionMark}' scope="request"/>

	<!-- Questions (Spanish) -->
	<c:set var="question1_2"  value='En las ${uAcute}ltimas cuatro semanas ${invQuestionMark}se preocup${oAcute} usted de que no hubiese suficiente alimento en su casa${questionMark}' scope="request"/>
	<c:set var="question2_2"  value='${invQuestionMark}Tiene usted problemas para llegar a la cl${iAcute}nica debido a que no tiene transporte o alguien que lo lleve${questionMark}' scope="request"/>
	<c:set var="question3_2"  value='${invQuestionMark}Ha cambiado o perdido usted su empleo desde la ${uAcute}ltima visita${questionMark}' scope="request"/>
	<c:set var="question4_2"  value='${invQuestionMark}Ha cambiado o perdido usted su cobertura de seguro desde la ${uAcute}ltima visita${questionMark}' scope="request"/>
	<c:set var="question5_2"  value='${invQuestionMark}Tiene usted preocupaciones o problemas sobre los cuales nuestro trabajador social pueda ayudarle${questionMark}' scope="request"/>
	<c:set var="question6_2"  value='${invQuestionMark}Tiene usted preocupaciones o problemas sobre los cuales nuestro dietista pueda ayudarle${questionMark}' scope="request"/>
	<c:set var="question7_2"  value='${invQuestionMark}Tiene usted preocupaciones o problemas sobre los cuales un educador de diabetes pueda ayudarle${questionMark}' scope="request"/>
	
	<input type="hidden" name="instructions_additionalQuestions" id="instructions_additionalQuestions" value="${instructions_additionalQuestions}" />
	<input type="hidden" name="instructions_additionalQuestions_sp" id="instructions_additionalQuestions_sp" value="${instructions_additionalQuestions_sp}" />
	
	<c:set var="instructions_part2" value="If your parent${slash}caregiver is not present${comma} please skip the questions by clicking the &lt;span style='text-decoration: underline;'&gt; No Parent&lt;/span&gt;&nbsp;button below${period}" />
	<c:set var="instructions_part2_sp" value="Si su padre${slash}madre${slash}cuidador no est${aAcute} presente${comma} omita las preguntas haciendo clic en el bot${oAcute}n &lt;span style='text-decoration: underline;'&gt; No Padre&lt;/span&gt;&nbsp;m${aAcute}s adelante${period}" />
	<input type="hidden" name="instructions_part2" id="instructions_part2" value="${instructions_part2}" />
	<input type="hidden" name="instructions_part2_sp" id="instructions_part2_sp" value="${instructions_part2_sp}" />

<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}', 'ParentPsychosocialForm', 'ParentPsycoSocialQuestion_', 'ParentPsychosocialQuestionEntry_')">
<form id="ParentPsychosocialForm" method="POST" action="parentPsychosocial.form" method="post" enctype="multipart/form-data">
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
		
		<strong><span id="instructions">${instructions_part2}</span></strong>
        <div><br/></div>
	</div>

    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage(1)" style="width: 150px;">Start</a>
        <a id="skipButton" href="#" data-role="button" data-theme="b" onclick="confirmSkipForm()" style="width: 150px;">No Parent</a>
        <%@ include file="mobileSkipFormDialog.jsp" %>
    </div>
    
</div>

<div data-role="page" id="form_completed_page" data-theme="b">
    <div data-role="header" >
        <h1>${formName} Form Completed</h1>
    </div>
    <div data-role="content" style="margin: 0 auto;text-align: center;" >
        <strong><span>The ${formName} form has already been completed and successfully submitted. It cannot be accessed again.</span></strong>
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

	        <input id="ParentPsycoSocialQuestion_1" name="ParentPsycoSocialQuestion_1" type="hidden" value="${question1}" />
	        <strong>${question1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question1}")'></a>
	        <div class="choice_1" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "1", false);</script></div>
	        
			<input id="ParentPsycoSocialQuestion_2" name="ParentPsycoSocialQuestion_2" type="hidden" value="${question2}" />
	        <strong>${question2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question2}")'></a>
	        <div class="choice_2" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "2", false);</script></div>
			
			<input id="ParentPsycoSocialQuestion_3" name="ParentPsycoSocialQuestion_3" type="hidden" value="${question3}" />
	        <strong>${question3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question3}")'></a>
	        <div class="choice_3" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "3", false);</script></div>
			
			<input id="ParentPsycoSocialQuestion_4" name="ParentPsycoSocialQuestion_4" type="hidden" value="${question4}" />
	        <strong>${question4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question4}")'></a>
	        <div class="choice_4" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "4", false);</script></div>
			
			<input id="ParentPsycoSocialQuestion_5" name="ParentPsycoSocialQuestion_5" type="hidden" value="${question5}" />
	        <strong>${question5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question5}")'></a>
	        <div class="choice_5" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "5", false);</script></div>
			
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
			
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Next</a>
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

			<input id="ParentPsycoSocialQuestion_6" name="ParentPsycoSocialQuestion_6" type="hidden" value="${question6}" />
	        <strong>${question6}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question6}")'></a>
	        <div class="choice_6" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "6", false);</script></div>
			
			<input id="ParentPsycoSocialQuestion_7" name="ParentPsycoSocialQuestion_7" type="hidden" value="${question7}" />
	        <strong>${question7}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question7}")'></a>
	        <div class="choice_7" data-role="fieldcontain" style="margin-top:0px;"><script>insertYesNo("${prefix}", "7", false);</script></div>
			
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
			<%@ include file="mobileFinishDialogs.jsp" %>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Previous</a>
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

			<input id="ParentPsycoSocialQuestion_1_2" name="ParentPsycoSocialQuestion_1_2" type="hidden" value="${question1_2}" />
	        <strong>${question1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question1_2}")'></a>
	        <div class="choice_1_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "1", true);</script></div>
	        
	        <input id="ParentPsycoSocialQuestion_2_2" name="ParentPsycoSocialQuestion_2_2" type="hidden" value="${question2_2}" />
	        <strong>${question2_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question2_2}")'></a>
	        <div class="choice_2_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "2", true);</script></div>
			
	        <input id="ParentPsycoSocialQuestion_3_2" name="ParentPsycoSocialQuestion_3_2" type="hidden" value="${question3_2}" />
	        <strong>${question3_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question3_2}")'></a>
	        <div class="choice_3_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "3", true);</script></div>
			
	        <input id="ParentPsycoSocialQuestion_4_2" name="ParentPsycoSocialQuestion_4_2" type="hidden" value="${question4_2}" />
	        <strong>${question4_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question4_2}")'></a>
	        <div class="choice_4_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "4", true);</script></div>

	        <input id="ParentPsycoSocialQuestion_5_2" name="ParentPsycoSocialQuestion_5_2" type="hidden" value="${question5_2}" />
	        <strong>${question5_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question5_2}")'></a>
	        <div class="choice_5_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "5", true);</script></div>
			
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
			
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
			<a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Proximo</a>
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

	        <input id="ParentPsycoSocialQuestion_6_2" name="ParentPsycoSocialQuestion_6_2" type="hidden" value="${question6_2}" />
	        <strong>${question6_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question6_2}")'></a>
	        <div class="choice_6_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "6", true);</script></div>

	        <input id="ParentPsycoSocialQuestion_7_2" name="ParentPsycoSocialQuestion_7_2" type="hidden" value="${question7_2}" />
			<strong>${question7_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question7_2}")'></a>
			<div class="choice_7_2" data-role="fieldcontain" style="margin-top:0px;"><script> insertYesNo("${prefix}", "7", true);</script></div>
        
	        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
			
			<%@ include file="mobileFinishDialogs_SP.jsp" %>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Anterior</a>
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
	<input id="SocialWorker" name="SocialWorker" type="hidden"/>
	<input id="Dietician" name="Dietician" type="hidden"/>
	<input id="DiabetesEducator" name="DiabetesEducator" type="hidden"/>
	
</form>
</body>
</html>
