<%@ include file="/WEB-INF/template/include.jsp"%>

<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/SUDEPMobile.form" />

<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/SUDEPMobile.css">
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
		<script>var ctx = "${pageContext.request.contextPath}";</script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/SUDEPMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
	</head>
	
	<%@ include file="specialCharacters.jsp" %>
	
	<c:set var="search" value="'" />
	<c:set var="replace" value="\\'" />
	<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
	<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
	
	<!-- Use gender to determine his/her  -->
	<c:set var="hisHer" value="his/her"/>
	<c:choose>
		<c:when test="${patient.gender == 'M'}">
			<c:set var="hisHer" value="his"/>
		</c:when>
		<c:when test="${patient.gender == 'F'}">
			<c:set var="hisHer" value="her"/>
		</c:when>
		<c:otherwise>
			<c:set var="hisHer" value="his/her"/>
		</c:otherwise>
	</c:choose>
	
	<c:set var="formName" value="Epilepsy Questions"/>
	<c:set var="formName_sp" value="Preguntas acerca de la epilepsia"/>
	<c:set var="formNameHeader" value="${formName}:"/>
	<c:set var="formNameHeader_sp" value="${formName_sp}:"/>
	<input type="hidden" name="formNameHeader" id="formNameHeader" value="${formNameHeader}" />
	<input type="hidden" name="formNameHeader_sp" id="formNameHeader_sp" value="${formNameHeader_sp}" />
	<c:set var="prefix" value="SUDEPQuestionEntry_" />
	
	<c:set var="instructions_additionalQuestions" value="The following are some additional questions about ${PossessiveFirstName} epilepsy or seizures." />
	
	<!-- Escape using special characters defined in the specialCharacters.jsp so that the hidden inputs can be used in the .js file to toggle between English/Spanish -->
	<c:set var="instructions_additionalQuestions_sp" value="Las siguientes son algunas preguntas adicionales sobre la epilepsia o convulsiones de ${patient.givenName}." />
	
	<!--  Questions (English) -->
	<c:set var="quest1" value='How many seizures with stiffness or jerking has ${patient.givenName} had in the last 12 months?'/>
	<c:set var="quest2" value='In the past week, how many doses of the anti-epileptic medication has ${patient.givenName} missed?'/>
	<c:set var="quest3" value='Does ${patient.givenName} receive ${hisHer} medication most of the time?'/>
	<c:set var="quest4" value='Do you sometimes forget to give ${patient.givenName} ${hisHer} medications?'/>
	<c:set var="quest5" value='Have you sometimes run out of ${PossessiveFirstName} medication?'/>
	<c:set var="quest6" value='Is it difficult to afford ${PossessiveFirstName} medication?'/>
	<c:set var="quest7" value='Has ${patient.givenName} seen the neurologist in the last 12 months?'/>
	<c:set var="quest8" value='Do you have difficulty bringing ${patient.givenName} to the neurologist for appointments?'/>
	
	<!--  Questions (Spanish) -->
	<c:set var="quest1_2" value='${invQuestionMark}Cu${aAcute}ntas convulsiones con rigidez o espasmos ha tenido ${patient.givenName} en los ${uAcute}ltimos 12 meses${questionMark}'/>
	<c:set var="quest2_2" value='En la semana pasada, ${invQuestionMark}cu${aAcute}ntas dosis del medicamento antiepil${eAcute}ptico ha omitido ${patient.givenName}${questionMark}'/>
	<c:set var="quest3_2" value='${invQuestionMark}Toma ${patient.givenName} su medicamento la mayor${iAcute}a de las veces${questionMark}'/>
	<c:set var="quest4_2" value='${invQuestionMark}Olvida usted darle a ${patient.givenName} sus medicamentos${questionMark}'/>
	<c:set var="quest5_2" value='${invQuestionMark}Se le ha terminado el medicamento de ${patient.givenName} alguna vez${questionMark}'/>
	<c:set var="quest6_2" value='${invQuestionMark}Le resulta dif${iAcute}cil costear el medicamento de ${patient.givenName}${questionMark}'/>
	<c:set var="quest7_2" value='${invQuestionMark}Ha visitado ${patient.givenName} al neur${oAcute}logo en los ${uAcute}ltimos 12 meses${questionMark}'/>
	<c:set var="quest8_2" value='${invQuestionMark}Tiene usted dificultad para llevar a ${patient.givenName} a las citas m${eAcute}dicas del neur${oAcute}logo${questionMark}'/>
	
	<!-- Adolescent version of questions along with the instructions page text -->
	<c:if test="${AgeInYears>=12.0}">
			<c:set var="quest1" value='How many seizures with stiffness or jerking have you had in the last 12 months?'/>
			<c:set var="quest2" value='In the past week, how many doses of the anti-epileptic medication have you missed?'/>
			<c:set var="quest3" value='Do you take your medication most of the time?'/>
			<c:set var="quest4" value='Do you sometimes forget to take your medications?'/>
			<c:set var="quest5" value='Have you sometimes run out of your medication?'/>
			<c:set var="quest6" value='Is it difficult for your family to afford your medication?'/>
			<c:set var="quest7" value='Have you seen the neurologist in the last 12 months?'/>
			<c:set var="quest8" value='Do you have difficulty going to the neurologist for your appointments?'/>
			<c:set var="instructions_additionalQuestions" value="The following are some additional questions about your epilepsy or seizures." />
			
			<!-- Escape using special characters defined in the specialCharacters.jsp so that the hidden inputs can be used in the .js file to toggle between English/Spanish -->
			<c:set var="quest1_2" value='${invQuestionMark}Cu${aAcute}ntas convulsiones con rigidez o espasmos ha tenido usted en los ${uAcute}ltimos 12 meses${questionMark}'/>
			<c:set var="quest2_2" value='En la semana pasada, ${invQuestionMark}cu${aAcute}ntas dosis del medicamento antiepil${eAcute}ptico ha omitido usted${questionMark}'/>
			<c:set var="quest3_2" value='${invQuestionMark}Toma usted su medicamento la mayor${iAcute}a de las veces${questionMark}'/>
			<c:set var="quest4_2" value='${invQuestionMark}Olvida usted tomar sus medicamentos algunas veces${questionMark}'/>
			<c:set var="quest5_2" value='${invQuestionMark}Se le ha terminado su medicamento alguna vez${questionMark}'/>
			<c:set var="quest6_2" value='${invQuestionMark}Es dif${iAcute}cil para su familia costear su medicamento${questionMark}'/>
			<c:set var="quest7_2" value='${invQuestionMark}Ha visitado usted al neur${oAcute}logo en los ${uAcute}ltimos 12 meses${questionMark}'/>
			<c:set var="quest8_2" value='${invQuestionMark}Tiene usted dificultad para ir a las citas m${eAcute}dicas del neur${oAcute}logo${questionMark}'/>
			
			<!-- Escape using special characters defined in the specialCharacters.jsp so that the hidden inputs can be used in the .js file to toggle between English/Spanish -->
			<c:set var="instructions_additionalQuestions_sp" value="Las siguientes son algunas preguntas adicionales sobre su epilepsia o convulsiones." />
	</c:if>
	<input type="hidden" name="instructions_additionalQuestions" id="instructions_additionalQuestions" value="${instructions_additionalQuestions}" />
	<input type="hidden" name="instructions_additionalQuestions_sp" id="instructions_additionalQuestions_sp" value="${instructions_additionalQuestions_sp}" />
	
	<!--  Leaving this as an example since the new chicaMobile.js file has been created 
	<c:set var="instructions_part2" value="This is just an example &lt;span style='text-decoration: underline;'&gt; with some underlined text. &lt;/span&gt;" />
	<c:set var="instructions_part2_sp" value="(SPANISH) This is just an example &lt;span style='text-decoration: underline;'&gt; with some underlined text. &lt;/span&gt;" />
	<input type="hidden" name="instructions_part2" id="instructions_part2" value="${instructions_part2}" />
	<input type="hidden" name="instructions_part2_sp" id="instructions_part2_sp" value="${instructions_part2_sp}" />
	-->
	
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}', 'SUDEPForm', 'SUDEPQuestion_', 'SUDEPQuestionEntry_')">
<form id="SUDEPForm" method="POST" action="SUDEPMobile.form" method="post" enctype="multipart/form-data">
<c:if test="${errorMessage != null}">
    <div id="error_dialog" class="extended-header" data-role="dialog" data-close-btn="none" data-dismissible="false" data-theme="b" data-overlay-theme="c">
        <div data-role="header" data-theme="b">
            <h1>Error</h1>
        </div>
        <div data-role="content">
            <span>${errorMessage}</span>
            <div style="margin: 0 auto;text-align: center;">
                <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="submitEmptyForm();" style="width: 150px;">OK</a>
            </div>
        </div>
    </div>
</c:if>
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
        
        <!-- We typically put more instructions here 
        <strong><span id="instructions">${instructions_part2}</span></strong>
        <div><br/></div>
        -->
    </div><!-- /content -->
    
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

<!-- Copyright is unused but leaving it here in case this eJIT is copied as a starting point for another eJIT -->
<c:set var="copyright" value=''/>
<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formNameHeader}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Quit</a>
    </div>
    <div id="content_1" data-role="content">
        <input id="SUDEPQuestion_1" name="SUDEPQuestion_1" type="hidden" value="${quest1}"/>
        <strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest1}")'></a>
        <div class="choice_1" data-role="fieldcontain" style="margin-top:0px;">
            <script>insertChoices("1", false);</script>
        </div>
        
        <input id="SUDEPQuestion_2" name="SUDEPQuestion_2" type="hidden" value="${quest2}"/>
        <strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2}")'></a>
        <div class="choice_2" data-role="fieldcontain" style="margin-top:0px;">
            <script>insertChoices("2", false);</script>
        </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_2" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formNameHeader}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Quit</a>
    </div>
    <div id="content_2" data-role="content">
        <input id="SUDEPQuestion_3" name="SUDEPQuestion_3" type="hidden" value="${quest3}"/>
        <strong>${quest3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest3}")'></a>
        <div class="choice_3" data-role="fieldcontain" style="margin-top:0px;">
            <script>insertYesNo("${prefix}", "3", false);</script>
        </div>
        
        <input id="SUDEPQuestion_4" name="SUDEPQuestion_4" type="hidden" value="${quest4}"/>
        <strong>${quest4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest4}")'></a>
        <div class="choice_4" data-role="fieldcontain" style="margin-top:0px;">
             <script>insertYesNo("${prefix}", "4", false);</script>
        </div>
        
        <input id="SUDEPQuestion_5" name="SUDEPQuestion_5" type="hidden" value="${quest5}"/>
        <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest5}")'></a>
        <div class="choice_5" data-role="fieldcontain" style="margin-top:0px;">
             <script>insertYesNo("${prefix}", "5", false);</script>
        </div>
    
        <input id="SUDEPQuestion_6" name="SUDEPQuestion_6" type="hidden" value="${quest6}"/>
        <strong>${quest6}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest6}")'></a>
        <div class="choice_6" data-role="fieldcontain" style="margin-top:0px;">
             <script>insertYesNo("${prefix}", "6", false);</script>
        </div>
    
        <input id="SUDEPQuestion_7" name="SUDEPQuestion_7" type="hidden" value="${quest7}"/>
        <strong>${quest7}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest7}")'></a>
        <div class="choice_7" data-role="fieldcontain" style="margin-top:0px;">
             <script>insertYesNo("${prefix}", "7", false);</script>
        </div>
    
        <input id="SUDEPQuestion_8" name="SUDEPQuestion_8" type="hidden" value="${quest8}"/>
        <strong>${quest8}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest8}")'></a>
        <div class="choice_8" data-role="fieldcontain" style="margin-top:0px;">
             <script>insertYesNo("${prefix}", "8", false);</script>
        </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
        
        <%@ include file="mobileFinishDialogs.jsp" %>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Continue</a>
    </div>
</div>

<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formNameHeader_sp}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Dejar</a>
    </div>
    <div id="content_1_sp" data-role="content">      
        <input id="SUDEPQuestion_1_2" name="SUDEPQuestion_1_2" type="hidden" value="${quest1_2}"/>
        <strong>${quest1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2}")'></a>
        <div class="choice_1_2" data-role="fieldcontain" style="margin-top:0px;">
            <script>insertChoices("1", true);</script>
        </div>
               
        <input id="SUDEPQuestion_2_2" name="SUDEPQuestion_2_2" type="hidden" value="${quest2_2}"/>
        <strong>${quest2_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest2_2}")'></a>
        <div class="choice_2_2" data-role="fieldcontain" style="margin-top:0px;">
            <script>insertChoices("2", true);</script>
        </div>
        
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Proximo</a>      
    </div>
</div> 

<div id="question_page_2_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formNameHeader_sp}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Dejar</a>
    </div>
    <div id="content_2_sp" data-role="content">        
        <input id="SUDEPQuestion_3_2" name="SUDEPQuestion_3_2" type="hidden" value="${quest3_2}"/>
        <strong>${quest3_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest3_2}")'></a>
        <div class="choice_3_2" data-role="fieldcontain" style="margin-top:0px;">
            <script>insertYesNo("${prefix}", "3", true);</script>
        </div>
        
        <input id="SUDEPQuestion_4_2" name="SUDEPQuestion_4_2" type="hidden" value="${quest4_2}"/>
        <strong>${quest4_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest4_2}")'></a>
        <div class="choice_4_2" data-role="fieldcontain" style="margin-top:0px;">
            <script>insertYesNo("${prefix}", "4", true);</script>
        </div>
        
        <input id="SUDEPQuestion_5_2" name="SUDEPQuestion_5_2" type="hidden" value="${quest5_2}"/>
        <strong>${quest5_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest5_2}")'></a>
        <div class="choice_5_2" data-role="fieldcontain" style="margin-top:0px;">
        	<script>insertYesNo("${prefix}", "5", true);</script>
        </div>
        
        <input id="SUDEPQuestion_6_2" name="SUDEPQuestion_6_2" type="hidden" value="${quest6_2}"/>
        <strong>${quest6_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest6_2}")'></a>
        <div class="choice_6_2" data-role="fieldcontain" style="margin-top:0px;">
        	<script>insertYesNo("${prefix}", "6", true);</script>
        </div>
        
        <input id="SUDEPQuestion_7_2" name="SUDEPQuestion_7_2" type="hidden" value="${quest7_2}"/>
        <strong>${quest7_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest7_2}")'></a>
        <div class="choice_7_2" data-role="fieldcontain" style="margin-top:0px;">
        	<script>insertYesNo("${prefix}", "7", true);</script>
        </div>
        
        <input id="SUDEPQuestion_8_2" name="SUDEPQuestion_8_2" type="hidden" value="${quest8_2}"/>
        <strong>${quest8_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest8_2}")'></a>
        <div class="choice_8_2" data-role="fieldcontain" style="margin-top:0px;">
        	<script>insertYesNo("${prefix}", "8", true);</script>
        </div>
        
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
        
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
<input type="hidden" name="ageInYears" id="ageInYears" value="${AgeInYears}" />
</form>
</body>
</html>
