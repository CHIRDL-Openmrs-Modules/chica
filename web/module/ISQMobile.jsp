<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ page import="org.openmrs.module.chirdlutil.util.Util"%>
<%@ page import="org.openmrs.Patient" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/ISQMobile.form" />
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
<script src="${pageContext.request.contextPath}/moduleResources/chica/ISQMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
<form id="ISQForm" method="POST" action="ISQMobile.form" method="post" enctype="multipart/form-data">
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
        <h1 id="formTitle">Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>

    <div data-role="content" >
        <strong><span id="additionalQuestions">Here are a number of questions about your baby's sleeping habits.</span></strong>
        <div><br/></div>
        <strong><span id="instructions">Please base your answers on what you have noticed over the <span style="text-decoration: underline;">last MONTH.</span></span></strong>
        <div><br/></div>
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage(1)" style="width: 150px;">Start</a>
    </div>
    
</div>

<div data-role="page" id="form_completed_page" data-theme="b">
    <div data-role="header" >
        <h1>ISQ Form Completed</h1>
    </div>
    <div data-role="content" style="margin: 0 auto;text-align: center;" >
        <strong><span>The ISQ form has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
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

<c:set var="copyright" value=''/>
<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_1" data-role="content">
        <div><h3>Going to bed/to sleep:</h3><hr/><br/></div>
        <c:set var="quest1" value='How long does it usually take to settle your baby off to sleep on average?'/>
        <input id="ISQQuestion_1" name="ISQQuestion_1" type="hidden" value="${quest1}"/>
	    <strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest1}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_LT10" value="0" data-theme="b" />
	            <label for="ISQQuestionEntry_1_LT10">Less than 10 minutes</label>
	            <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_10_20" value="1" data-theme="b" />
	            <label for=ISQQuestionEntry_1_10_20>10 to 20 minutes</label>
	            <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_20_30" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_1_20_30">20 to 30 minutes</label>
                <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_30_40" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_1_30_40">30 to 40 minutes</label>
                <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_40_50" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_1_40_50">40 to 50 minutes</label>
                <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_50_60" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_1_50_60">50 to 60 minutes</label>
                <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_1HRORLONGER" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_1_1HRORLONGER">1 hour or longer</label>
	        </fieldset>
	    </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_2" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_2" data-role="content">
    <div><h3>Going to bed/to sleep:</h3><hr/><br/></div>
    <c:set var="quest2" value='How many times a week do you have problems settling him/her on average?'/>
        <input id="ISQQuestion_2" name="ISQQuestion_2" type="hidden" value="${quest2}"/>
	    <strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_2" id="ISQQuestionEntry_2_LT1PW" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_2_LT1PW">Problems less than once a week</label>
                <input type="radio" name="ISQQuestionEntry_2" id="ISQQuestionEntry_2_1PW" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_2_1PW">Problems 1 night a week</label>
                <input type="radio" name="ISQQuestionEntry_2" id="ISQQuestionEntry_2_2PW" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_2_2PW">Problems 2 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_2" id="ISQQuestionEntry_2_3PW" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_2_3PW">Problems 3 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_2" id="ISQQuestionEntry_2_4PW" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_2_4PW">Problems 4 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_2" id="ISQQuestionEntry_2_5PW" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_2_5PW">Problems 5 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_2" id="ISQQuestionEntry_2_6PW" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_2_6PW">Problems 6 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_2" id="ISQQuestionEntry_2_EN" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_2_EN">Problems every night of the week</label>
            </fieldset>
        </div>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_3" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_3" data-role="content">
    <div><h3>Going to bed/to sleep:</h3><hr/><br/></div>
    <c:set var="quest3" value='How long has the settling problem been going on?'/>
        <input id="ISQQuestion_3" name="ISQQuestion_3" type="hidden" value="${quest3}"/>
        <strong>${quest3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_NA" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_3_NA">Not applicable/No settling problem</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_LT1M" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_3_LT1M">Less than 1 month</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_1M" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_3_1M">1 month</label>
                <input type="radio" name="ISQQuestionEntry_3" id=ISQQuestionEntry_3_2M value="3" data-theme="b" />
                <label for="ISQQuestionEntry_3_2M">2 months</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_3M" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_3_3M">3 months</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_4M" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_3_4M">4 months</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_5M" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_3_5M">5 months</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_6MOM" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_3_6MOM">6 months or more</label>
            </fieldset>
        </div>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_4" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_4" data-role="content">
    <div><h3>Waking at night (between midnight and 6:00 a.m.):</h3><hr/><br/></div>
    <c:set var="quest4" value='How many nights a week does your baby wake on average?'/>
        <input id="ISQQuestion_4" name="ISQQuestion_4" type="hidden" value="${quest4}"/>
        <strong>${quest4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_NLT1" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_4_NLT1">None or less than once a week</label>
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_1N" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_4_1N">1 night a week</label>
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_2N" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_4_2N">2 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_3N" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_4_3N">3 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_4N" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_4_4N">4 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_5N" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_4_5N">5 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_6N" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_4_6N">6 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_EN" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_4_EN">Every night of the week</label>
            </fieldset>
        </div>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(5)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_5" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_5" data-role="content">
    <div><h3>Waking at night (between midnight and 6:00 a.m.):</h3><hr/><br/></div>
    <c:set var="quest5" value='How many times does your baby wake each night and need resettling on average?'/>
        <input id="ISQQuestion_5" name="ISQQuestion_5" type="hidden" value="${quest5}"/>
        <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest5}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_DNW" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_5_DNW">Does not wake</label>
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_1PN" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_5_1PN">Once a night</label>
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_2PN" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_5_2PN">Twice a night</label>
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_3PN" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_5_3PN">3 times a night</label>
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_4PN" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_5_4PN">4 times a night</label>
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_5NOM" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_5_5NOM">5 or more times a night</label>
            </fieldset>
        </div>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(6)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_6" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_6" data-role="content">
    <div><h3>Waking at night (between midnight and 6:00 a.m.):</h3><hr/><br/></div>
    <c:set var="quest6" value='If your baby wakes, how long does it take for your baby to go back to sleep on average?'/>
        <input id="ISQQuestion_6" name="ISQQuestion_6" type="hidden" value="${quest6}"/>
        <strong>${quest6}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest6}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_6" id="ISQQuestionEntry_6_LT10" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_6_LT10">Less than 10 minutes</label>
                <input type="radio" name="ISQQuestionEntry_6" id="ISQQuestionEntry_6_10_20" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_6_10_20">10 to 20 minutes</label>
                <input type="radio" name="ISQQuestionEntry_6" id="ISQQuestionEntry_6_20_30" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_6_20_30">20 to 30 minutes</label>
                <input type="radio" name="ISQQuestionEntry_6" id="ISQQuestionEntry_6_30_40" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_6_30_40">30 to 40 minutes</label>
                <input type="radio" name="ISQQuestionEntry_6" id="ISQQuestionEntry_6_40_50" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_6_40_50">40 to 50 minutes</label>
                <input type="radio" name="ISQQuestionEntry_6" id="ISQQuestionEntry_6_50_60" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_6_50_60">50 to 60 minutes</label>
                <input type="radio" name="ISQQuestionEntry_6" id="ISQQuestionEntry_6_1HOL" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_6_1HOL">1 hour or longer</label>
            </fieldset>
        </div>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(5)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(7)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_7" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_7" data-role="content">
    <div><h3>Waking at night (between midnight and 6:00 a.m.):</h3><hr/><br/></div>
        <c:set var="quest7" value='How long has the waking problem been going on?'/>
        <input id="ISQQuestion_7" name="ISQQuestion_7" type="hidden" value="${quest7}"/>
        <strong>${quest7}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest7}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_NA" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_7_NA">Not applicable/No waking problem</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_LT1M" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_7_LT1M">Less than 1 month</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_1M" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_7_1M">1 month</label>
                <input type="radio" name="ISQQuestionEntry_7" id=ISQQuestionEntry_7_2M value="3" data-theme="b" />
                <label for="ISQQuestionEntry_7_2M">2 months</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_3M" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_7_3M">3 months</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_4M" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_7_4M">4 months</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_5M" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_7_5M">5 months</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_6MOM" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_7_6MOM">6 months or more</label>
            </fieldset>
        </div>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(6)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(8)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_8" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_8" data-role="content">
    <div><h3>Sleeping in parents&#39; bed:</h3><hr/><br/></div>
    <c:set var="quest8" value='How often do you end up taking your baby into your bed because he/she is upset and won&rsquo;t sleep?'/>
        <input id="ISQQuestion_8" name="ISQQuestion_8" type="hidden" value="${quest8}"/>
        <strong>${quest8}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest8}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_8" id="ISQQuestionEntry_8_NLT1" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_8_NLT1">Never, or less than once a week</label>
                <input type="radio" name="ISQQuestionEntry_8" id="ISQQuestionEntry_8_1N" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_8_1N">1 night a week</label>
                <input type="radio" name="ISQQuestionEntry_8" id="ISQQuestionEntry_8_2N" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_8_2N">2 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_8" id="ISQQuestionEntry_8_3N" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_8_3N">3 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_8" id="ISQQuestionEntry_8_4N" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_8_4N">4 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_8" id="ISQQuestionEntry_8_5N" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_8_5N">5 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_8" id="ISQQuestionEntry_8_6N" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_8_6N">6 nights a week</label>
                <input type="radio" name="ISQQuestionEntry_8" id="ISQQuestionEntry_8_EN" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_8_EN">Every night of the week</label>
            </fieldset>
        </div>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(7)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_9" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_9" data-role="content">
    <div><h3>Sleeping in parents&#39; bed:</h3><hr/><br/></div>
    <c:set var="quest9" value='How long has the problem been going on?'/>
        <input id="ISQQuestion_9" name="ISQQuestion_9" type="hidden" value="${quest9}"/>
        <strong>${quest9}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest9}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_NA" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_9_NA">Not applicable/There is no problem</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_LT1M" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_9_LT1M">Less than 1 month</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_1M" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_9_1M">1 month</label>
                <input type="radio" name="ISQQuestionEntry_9" id=ISQQuestionEntry_9_2M value="3" data-theme="b" />
                <label for="ISQQuestionEntry_9_2M">2 months</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_3M" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_9_3M">3 months</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_4M" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_9_4M">4 months</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_5M" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_9_5M">5 months</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_6MOM" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_9_6MOM">6 months or more</label>
            </fieldset>
        </div>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(8)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(10)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_10" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_10" data-role="content">
    <div><h3>Your views:</h3><hr/><br/></div>
    <c:set var="quest10" value='Do you think that your baby has sleeping difficulties?'/>
        <input id="ISQQuestion_10" name="ISQQuestion_9" type="hidden" value="${quest10}"/>
        <strong>${quest10}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest10}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_10" id="ISQQuestionEntry_10_NO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_10_NO">No</label>
                <input type="radio" name="ISQQuestionEntry_10" id="ISQQuestionEntry_10_MILD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_10_MILD">Yes, mild</label>
                <input type="radio" name="ISQQuestionEntry_10" id="ISQQuestionEntry_10_MOD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_10_MOD">Yes, moderate</label>
                <input type="radio" name="ISQQuestionEntry_10" id="ISQQuestionEntry_10_SEVERE" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_10_SEVERE">Yes, severe</label>
            </fieldset>
        </div>
    	<div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
        
        <%@ include file="mobileFinishDialogs.jsp" %>
        
        </div>
        
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Previous</a>
        <a  href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Finish</a>
    </div>
</div>
    
<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_1_sp" data-role="content">
        <div><h3>Acostarlo/ponerlo a dormir:</h3><hr/><br/></div>
        <c:set var="quest1_2" value='&iquest;Cu&aacute;nto tiempo en promedio tarda generalmente su beb&eacute; en disponerse a dormir?'/>
        <input id="ISQQuestion_1_2" name="ISQQuestion_1_2" type="hidden" value="${quest1_2}"/>
        <strong>${quest1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_LT10" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_LT10">menos de 10 minutos</label>
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_10_20" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_10_20">de 10 a 20 minutos </label>
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_20_30" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_20_30">de 20 a 30 minutos</label>
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_30_40" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_30_40">de 30 a 40 minutos</label>
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_40_50" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_40_50">de 40 a 50 minutos</label>
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_50_60" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_50_60">de 50 a 60 minutos</label>
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_1HRORLONGER" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_1HRORLONGER">1 hora o más</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Proximo</a>      
    </div>
</div> 

<div id="question_page_2_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_2_sp" data-role="content">
        <div><h3>Acostarlo/ponerlo a dormir:</h3><hr/><br/></div>
        <c:set var="quest2_2" value='&iquest;Cu&aacute;ntas veces a la semana en promedio tiene problemas para ponerlo a dormir?'/>
        <input id="ISQQuestion_2_2" name="ISQQuestion_2_2" type="hidden" value="${quest2_2}"/>
        <strong>${quest2_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest2_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_LT1PW" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_LT1PW">Problemas menos de una vez a la semana</label>
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_1PW" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_1PW">Problemas 1 noche a la semana</label>
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_2PW" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_2PW">Problemas 2 noche a la semana</label>
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_3PW" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_3PW">Problemas 3 noche a la semana</label>
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_4PW" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_4PW">Problemas 4 noche a la semana</label>
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_5PW" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_5PW">Problemas 5 noche a la semana</label>
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_6PW" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_6PW">Problemas 6 noche a la semana</label>
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_EN" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_EN">Problemas todas las noches de la semana</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Proximo</a>   
    </div>
</div>

<div id="question_page_3_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_3_sp" data-role="content">
        <div><h3>Acostarlo/ponerlo a dormir:</h3><hr/><br/></div>
        <c:set var="quest3_2" value='&iquest;Por cu&aacute;nto tiempo ha persistido el problema de ponerlo a dormir?'/>
        <input id="ISQQuestion_3_2" name="ISQQuestion_3_2" type="hidden" value="${quest3_2}"/>
        <strong>${quest3_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest3_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_NA" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_NA">No aplica/No hay problema de ponerlo a dormir</label>
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_LT1M" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_LT1M">menos de 1 mes</label>
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_1M" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_1M">1 mes</label>
                <input type="radio" name="ISQQuestionEntry_3_2" id=ISQQuestionEntry_3_2_2M value="3" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_2M">2 meses</label>
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_3M" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_3M">3 meses</label>
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_4M" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_4M">4 meses</label>
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_5M" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_5M">5 meses</label>
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_6MOM" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_6MOM">6 meses o m&aacute;s</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Proximo</a>   
    </div>
</div> 

<div id="question_page_4_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_4_sp" data-role="content">
        <div><h3>Despertarse en la noche (entre la medianoche y las 6:00 a.m.):</h3><hr/><br/></div>
        <c:set var="quest4_2" value='&iquest;Cu&aacute;ntas veces a la semana su beb&eacute; se despierta en promedio?'/>
        <input id="ISQQuestion_4_2" name="ISQQuestion_4_2" type="hidden" value="${quest4_2}"/>
        <strong>${quest4_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest4_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_NLT1" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_NLT1">Ninguna e menos de una vez a la semana</label>
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_1N" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_1N">1 noche a la semana</label>
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_2N" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_2N">2 noches a la semana</label>
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_3N" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_3N">3 noches a la semana</label>
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_4N" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_4N">4 noches a la semana</label>
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_5N" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_5N">5 noches a la semana</label>
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_6N" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_6N">6 noches a la semana</label>
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_EN" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_EN">Todas las noches de la semana</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(5)" style="width: 150px;">Proximo</a>   
    </div>
</div> 

<div id="question_page_5_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_5_sp" data-role="content">
        <div><h3>Despertarse en la noche (entre la medianoche y las 6:00 a.m.):</h3><hr/><br/></div>
        <c:set var="quest5_2" value='&iquest;Cu&aacute;ntas veces se despierta su beb&eacute; en la noche y necesita volver a ponerlo a dormir?'/>
        <input id="ISQQuestion_5_2" name="ISQQuestion_5_2" type="hidden" value="${quest5_2}"/>
        <strong>${quest5_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest5_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_5_2" id="ISQQuestionEntry_5_2_DNW" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_5_2_DNW">No se despierta</label>
                <input type="radio" name="ISQQuestionEntry_5_2" id="ISQQuestionEntry_5_2_1PN" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_5_2_1PN">Una vez en la noche</label>
                <input type="radio" name="ISQQuestionEntry_5_2" id="ISQQuestionEntry_5_2_2PN" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_5_2_2PN">Dos veces en la noche</label>
                <input type="radio" name="ISQQuestionEntry_5_2" id="ISQQuestionEntry_5_2_3PN" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_5_2_3PN">Tres veces en la noche</label>
                <input type="radio" name="ISQQuestionEntry_5_2" id="ISQQuestionEntry_5_2_4PN" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_5_2_4PN">Cuatro veces en la noche</label>
                <input type="radio" name="ISQQuestionEntry_5_2" id="ISQQuestionEntry_5_2_5NOM" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_5_2_5NOM">Cinco o m&aacute;s veces en la noche</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(6)" style="width: 150px;">Proximo</a>   
    </div>
</div> 

<div id="question_page_6_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_6_sp" data-role="content">
        <div><h3>Despertarse en la noche (entre la medianoche y las 6:00 a.m.):</h3><hr/><br/></div>
        <c:set var="quest6_2" value='Si su beb&eacute; se despierta &iquest;cu&aacute;nto tiempo en promedio le toma volverse a dormir?'/>
        <input id="ISQQuestion_6_2" name="ISQQuestion_6_2" type="hidden" value="${quest6_2}"/>
        <strong>${quest6_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest6_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_LT10" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_LT10">menos de 10 minutos</label>
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_10_20" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_10_20">de 10 a 20 minutos</label>
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_20_30" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_20_30">de 20 a 30 minutos</label>
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_30_40" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_30_40">de 30 a 40 minutos</label>
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_40_50" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_40_50">de 40 a 50 minutos</label>
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_50_60" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_50_60">de 50 a 60 minutos</label>
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_1HOL" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_1HOL">1 hora o m&aacute;s</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(5)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(7)" style="width: 150px;">Proximo</a>   
    </div>
</div> 

<div id="question_page_7_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_7_sp" data-role="content">
        <div><h3>Despertarse en la noche (entre la medianoche y las 6:00 a.m.):</h3><hr/><br/></div>
        <c:set var="quest7_2" value='&iquest;Por cu&aacute;nto tiempo ha persistido el problema de despertarse?'/>
        <input id="ISQQuestion_7_2" name="ISQQuestion_7_2" type="hidden" value="${quest7_2}"/>
        <strong>${quest7_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest7_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_NA" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_NA">No aplica/No hay problema que se despierte el beb&eacute;</label>
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_LT1M" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_LT1M">menos de 1 mes</label>
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_1M" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_1M">1 mes</label>
                <input type="radio" name="ISQQuestionEntry_7_2" id=ISQQuestionEntry_7_2_2M value="3" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_2M">2 meses</label>
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_3M" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_3M">3 meses</label>
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_4M" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_4M">4 meses</label>
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_5M" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_5M">5 meses</label>
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_6MOM" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_6MOM">6 meses o m&aacute;s</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(6)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(8)" style="width: 150px;">Proximo</a>   
    </div>
</div> 

<div id="question_page_8_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_8_sp" data-role="content">
        <div><h3>Dormir en la cama de los padres:</h3><hr/><br/></div>
        <c:set var="quest8_2" value='&iquest;Cu&aacute;ntas veces termina llev&aacute;ndose al beb&eacute; a su cama porque est&aacute; molesto y no se duerme?'/>
        <input id="ISQQuestion_8_2" name="ISQQuestion_8_2" type="hidden" value="${quest8_2}"/>
        <strong>${quest8_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest8_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_NLT1" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_NLT1">Nunca, o menos de una vez a la semana</label>
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_1N" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_1N">1 noche a la semana</label>
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_2N" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_2N">2 noches a la semana</label>
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_3N" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_3N">3 noches a la semana</label>
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_4N" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_4N">4 noches a la semana</label>
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_5N" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_5N">5 noches a la semana</label>
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_6N" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_6N">6 noches a la semana</label>
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_EN" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_EN">Todas las noches de la semana</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(7)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Proximo</a>   
    </div>
</div> 

<div id="question_page_9_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_9_sp" data-role="content">
        <div><h3>Dormir en la cama de los padres:</h3><hr/><br/></div>
        <c:set var="quest9_2" value='&iquest;Por cu&aacute;nto tiempo ha persistido el problema?'/>
        <input id="ISQQuestion_9_2" name="ISQQuestion_9_2" type="hidden" value="${quest9_2}"/>
        <strong>${quest9_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest9_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_NA" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_NA">No aplica/No hay problema</label>
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_LT1M" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_LT1M">menos de 1 mes</label>
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_1M" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_1M">1 mes</label>
                <input type="radio" name="ISQQuestionEntry_9_2" id=ISQQuestionEntry_9_2_2M value="3" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_2M">2 meses</label>
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_3M" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_3M">3 meses</label>
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_4M" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_4M">4 meses</label>
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_5M" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_5M">5 meses</label>
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_6MOM" value="7" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_6MOM">6 meses o m&aacute;s</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(8)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(10)" style="width: 150px;">Proximo</a>   
    </div>
</div> 

<div id="question_page_10_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_10_sp" data-role="content">
        <div><h3>Su opini&oacute;n</h3><hr/><br/></div>
        <c:set var="quest10" value='&iquest;Considera que el beb&eacute; tiene dificultad para dormir?'/>
        <input id="ISQQuestion_10_2" name="ISQQuestion_10_2" type="hidden" value="${quest10}"/>
        <strong>${quest10}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest10}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_10_2" id="ISQQuestionEntry_10_2_NO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_10_2_NO">No</label>
                <input type="radio" name="ISQQuestionEntry_10_2" id="ISQQuestionEntry_10_2_MILD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_10_2_MILD">S&iacute;, leve</label>
                <input type="radio" name="ISQQuestionEntry_10_2" id="ISQQuestionEntry_10_2_MOD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_10_2_MOD">S&iacute;, moderada</label>
                <input type="radio" name="ISQQuestionEntry_10_2" id="ISQQuestionEntry_10_2_SEVERE" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_10_2_SEVERE">S&iacute;, grave </label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
        
        <%@ include file="mobileFinishDialogs_SP.jsp" %>
        
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Anterior</a>
        <a href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Acabado</a>
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
<input id="ISQScore" name="ISQScore" type="hidden"/>
<input id="ISQProb" name="ISQProb" type="hidden"/>
<input id="ISQSevere" name="ISQSevere" type="hidden"/>
<input id="ISQResearch" name="ISQResearch" type="hidden"/>
<input id="language" name="language" type="hidden" value="${language}"/>
</form>
</body>
</html>
