<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ page import="org.openmrs.module.chirdlutil.util.Util"%>
<%@ page import="org.openmrs.Patient" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/TRAQMobile.form" />
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
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/TRAQMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
<form id="TRAQForm" method="POST" action="TRAQMobile.form" method="post" enctype="multipart/form-data">
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
        <h1 id="formTitle">Transition Readiness Assessment Questionnaire(TRAQ)</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>

    <div data-role="content" >
        <strong><span id="additionalQuestions">Please check the box that best describes your skill level in the following areas that are important for transition to adult health care.</span></span></strong>
        <div><br/></div>
        <strong><span id="information">There is no right or wrong answer and your answers will remain confidential and private.</span></strong>
        <div><br/></div>
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage(1)" style="width: 150px;">Start</a>
    </div>
    
</div>

<div data-role="page" id="form_completed_page" data-theme="b">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire Completed</h1>
    </div>
    <div data-role="content" style="margin: 0 auto;text-align: center;" >
        <strong><span>The Transition Readiness Assessment Questionnaire has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-inline="true" data-theme="b" data-role="button" data-rel="back" style="width: 150px;">Back</a>
    </div>
</div>

<div id="quit_confirm_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Confirm Quit</h1>
    </div>
    <div data-role="content">
        <span>Are you sure you want to quit?</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#quit_dialog" data-role="button" data-rel="dialog" data-inline="true" data-theme="b" style="width: 150px;">Yes</a>
            <a href="#" data-role="button" data-rel="back" data-inline="true" data-theme="b" style="width: 150px;">No</a>
        </div>
    </div>
</div>

<div id="quit_confirm_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Confirmar Salir</h1>
    </div>
    <div data-role="content">
        <span>&#191;Est&#225; seguro de que desea salir?</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#quit_dialog_sp" data-role="button" data-rel="dialog" data-inline="true" data-theme="b" style="width: 150px;">Si</a>
            <a href="#" data-role="button" data-rel="back" data-inline="true" data-theme="b" style="width: 150px;">No</a>
        </div>
    </div>
</div>

<div id="quit_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Finished</h1>
    </div>
    <div data-role="content">
        <span>Thank you for filling out the form.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a onclick="submitForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">OK</a>
        </div>
    </div>
</div>

<div id="quit_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Acabado</h1>
    </div>
    <div data-role="content">
        <span>Gracias por rellenar el formulario.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a onclick="submitForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Aceptar</a>
        </div>
    </div>
</div>

<c:set var="copyright" value='Copyright &#169; Wood, Sawicki, Reiss, Livingood &#38 Kraemer, 2014'/>
<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_1" data-role="content">
        <div><h3>Managing Medications:</h3><hr/><br/></div>
        <c:set var="quest1" value='Do you fill a prescription if you need to?'/>
        <input id="TRAQQuestion_1" name="TRAQQuestion_1" type="hidden" value="${quest1}"/>
	    <strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest1}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            <input type="radio" name="TRAQQuestionEntry_1" id="TRAQQuestionEntry_1_DO_NOT_KNOW" value="1" data-theme="b" />
	            <label for="TRAQQuestionEntry_1_DO_NOT_KNOW">No, I do not know how.</label>
	            <input type="radio" name="TRAQQuestionEntry_1" id="TRAQQuestionEntry_1_WANT_TO_LEARN" value="2" data-theme="b" />
	            <label for=TRAQQuestionEntry_1_WANT_TO_LEARN>No, but I want to learn.</label>
	            <input type="radio" name="TRAQQuestionEntry_1" id="TRAQQuestionEntry_1_LEARNING" value="3" data-theme="b" />
                <label for="TRAQQuestionEntry_1_LEARNING">No, but I am learning to do this.</label>
                <input type="radio" name="TRAQQuestionEntry_1" id="TRAQQuestionEntry_1_STARTED" value="4" data-theme="b" />
                <label for="TRAQQuestionEntry_1_STARTED">Yes, I have started doing this.</label>
                <input type="radio" name="TRAQQuestionEntry_1" id="TRAQQuestionEntry_1_ALWAYS" value="5" data-theme="b" />
                <label for="TRAQQuestionEntry_1_ALWAYS">Yes, I always do this when I need to.</label>
	        </fieldset>
	    </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Next</a>
    </div>
</div>


<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_1_sp" data-role="content">
        <div><h3>Acostarlo/ponerlo a dormir:</h3><hr/><br/></div>
        <c:set var="quest1_2" value='&iquest;Cu&aacute;nto tiempo en promedio tarda generalmente su beb&eacute; en disponerse a dormir?'/>
        <input id="TRAQQuestion_1_2" name="TRAQQuestion_1_2" type="hidden" value="${quest1_2}"/>
        <strong>${quest1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="TRAQQuestionEntry_1_2" id="TRAQQuestionEntry_1_2_LT10" value="0" data-theme="b" />
                <label for="TRAQQuestionEntry_1_2_LT10">menos de 10 minutos</label>
                <input type="radio" name="TRAQQuestionEntry_1_2" id="TRAQQuestionEntry_1_2_10_20" value="1" data-theme="b" />
                <label for="TRAQQuestionEntry_1_2_10_20">de 10 a 20 minutos </label>
                <input type="radio" name="TRAQQuestionEntry_1_2" id="TRAQQuestionEntry_1_2_20_30" value="2" data-theme="b" />
                <label for="TRAQQuestionEntry_1_2_20_30">de 20 a 30 minutos</label>
                <input type="radio" name="TRAQQuestionEntry_1_2" id="TRAQQuestionEntry_1_2_30_40" value="3" data-theme="b" />
                <label for="TRAQQuestionEntry_1_2_30_40">de 30 a 40 minutos</label>
                <input type="radio" name="TRAQQuestionEntry_1_2" id="TRAQQuestionEntry_1_2_40_50" value="4" data-theme="b" />
                <label for="TRAQQuestionEntry_1_2_40_50">de 40 a 50 minutos</label>
                <input type="radio" name="TRAQQuestionEntry_1_2" id="TRAQQuestionEntry_1_2_50_60" value="5" data-theme="b" />
                <label for="TRAQQuestionEntry_1_2_50_60">de 50 a 60 minutos</label>
                <input type="radio" name="TRAQQuestionEntry_1_2" id="TRAQQuestionEntry_1_2_1HRORLONGER" value="6" data-theme="b" />
                <label for="TRAQQuestionEntry_1_2_1HRORLONGER">1 hora o más</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Proximo</a>      
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
<input id="TRAQScore" name="TRAQScore" type="hidden"/>
<input id="TRAQProb" name="TRAQProb" type="hidden"/>
<input id="TRAQSevere" name="TRAQSevere" type="hidden"/>
<input id="TRAQResearch" name="TRAQResearch" type="hidden"/>
<input id="language" name="language" type="hidden" value="${language}"/>
</form>
</body>
</html>
