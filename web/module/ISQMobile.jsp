<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/psfMobile.form" />
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
        <h1>Infant Sleep Questionaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>

    <div data-role="content" >
        <strong><span id="additionalQuestions">The following are some additional questions about depression.</span></strong>
        <div><br/></div>
        <strong><span id="instructions">Over the <span style="text-decoration: underline;">last 2 weeks</span>, how often have you been bothered by any of the following problems?</span></strong>
        <div><br/></div>
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage(1)" style="width: 150px;">Start</a>
    </div>
    
</div><!-- /page one -->

<div data-role="page" id="form_completed_page" data-theme="b">
    <div data-role="header" >
        <h1>PHQ-9 Form Completed</h1>
    </div>
    <div data-role="content" style="margin: 0 auto;text-align: center;" >
        <strong><span>The PHQ-9 form has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
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

<c:set var="copyright" value='Copyright &#169; Pfizer Inc. All rights reserved. Reproduced with permission.'/>
<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_1" data-role="content">
        <div><hr/><br/></div>
        <h3>Going to bed/to sleep:</h3>
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
                <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_30_40" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_1_30_40">30 to 40 minutes</label>
                <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_40_50" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_1_40_50">40 to 50 minutes</label>
                <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_50_60" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_1_40_50">50 to 60 minutes</label>
                <input type="radio" name="ISQQuestionEntry_1" id="ISQQuestionEntry_1_1HRORLONGER" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_1_1HRORLONGER">1 hour or longer</label>
	        </fieldset>
	    </div>
	    <br/>
	    <br/>
	    <c:set var="quest2" value='How many times a week do you have problems settling him/her on average?'/>
        <input id="ISQQuestion_2" name="ISQQuestion_2" type="hidden" value="${quest2}"/>
	    <strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_2" id="ISQQuestionEntry_2_LT1PW" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_2_LT1PW">Less than once a week</label>
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
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Next</a>
        <!-- <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a> -->
    </div>
</div>

<div id="question_page_2" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_2" data-role="content">
        <div><hr/><br/></div>
        <c:set var="quest3" value='How long has the settling problem been going on?'/>
        <input id="ISQQuestion_3" name="ISQQuestion_3" type="hidden" value="${quest3}"/>
        <strong>${quest3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_LT1M" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_3_LT1M">Less than 1 month</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_1M" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_3_1M">1 month</label>
                <input type="radio" name="ISQQuestionEntry_3" id=ISQQuestionEntry_3_2M value="2" data-theme="b" />
                <label for="ISQQuestionEntry_3_2M">2 months</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_3M" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_3_3M">3 months</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_4M" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_3_3M">4 months</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_5M" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_3_5M">5 months</label>
                <input type="radio" name="ISQQuestionEntry_3" id="ISQQuestionEntry_3_6MOM" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_3_6MOM">6 months or more</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <h3>Waking at night (between midnight and 6:00 a.m.):</h3>
        <c:set var="quest4" value='How many nights per week does you baby wake on the average?'/>
        <input id="ISQQuestion_4" name="ISQQuestion_4" type="hidden" value="${quest4}"/>
        <strong>${quest4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_NLT1" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_4_NLT1">None or less than once a week</label>
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_1N" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_4_1N">1 night a week</label>
                <input type="radio" name="ISQQuestionEntry_4" id="ISQQuestionEntry_4_2n" value="2" data-theme="b" />
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
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Next</a>
        <!-- <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a> -->
    </div>
</div>

<div id="question_page_3" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage3Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_3" data-role="content">
        <div><hr/><br/></div>
        <h3>Waking at night (between midnight and 6:00 a.m.):</h3>
        <c:set var="quest5" value='Poor appetite or overeating'/>
        <input id="ISQQuestion_5" name="ISQQuestion_5" type="hidden" value="${quest5}"/>
        <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest5}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_DNW" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_5_DNW">Does not wake</label>
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_1PN" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_5_1PN">once a night</label>
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_2PN" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_5_2PN">twice a night</label>
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_3PN" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_5_3PN">3 times a night</label>
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_4PN" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_5_4PN">4 times a night</label>
                <input type="radio" name="ISQQuestionEntry_5" id="ISQQuestionEntry_5_5NOM" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_5_5NOM">5 or more times a night</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest6" value='If you baby wakes, how long does it take for your baby to go back to sleep on the average?'/>
        <input id="ISQQuestion_6" name="ISQQuestion_6" type="hidden" value="${quest6}"/>
        <strong>${quest6}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest6}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_6" id="ISQQuestionEntry_6_LT10" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_6_LT10">less than 10 minutes</label>
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
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Next</a>
        <!-- <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a> -->
    </div>
</div>

<div id="question_page_4" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage4Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_4" data-role="content">
   		 <h3>Waking at night (between midnight and 6:00 a.m.):</h3>
        <div><hr/><br/></div>
        <c:set var="quest7" value='How long has the waking problem been going on?'/>
        <input id="ISQQuestion_7" name="ISQQuestion_7" type="hidden" value="${quest7}"/>
        <strong>${quest7}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest7}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_LT1M" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_7_LT1M">Less than 1 month</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_1M" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_7_1M">1 month</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_2M" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_7_2M">2 months</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_3M" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_7_3M">3 months</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_4M" value="4" data-theme="b" />
                <label for="ISQQuestionEntry_7_4M">4 months</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_5M" value="5" data-theme="b" />
                <label for="ISQQuestionEntry_7_5M">5 months</label>
                <input type="radio" name="ISQQuestionEntry_7" id="ISQQuestionEntry_7_6OM" value="6" data-theme="b" />
                <label for="ISQQuestionEntry_7_6OM">6 months or more</label>
            </fieldset>
        </div>
        <c:set var="quest8" value='How often do you end up taking your baby into your bed because he/she is updset and won&rsquo;t sleep'/>
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
        
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(5)" style="width: 150px;">Next</a>
        <!-- <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a> -->
    </div>
</div>

<div id="question_page_5" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage4Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_5" data-role="content">
   		 <h3>Sleeping in parent&rsquos bed:</h3>
        
         <div><hr/><br/></div>
        <c:set var="quest9" value='How long has the problem been going on?'/>
        <input id="ISQQuestion_9" name="ISQQuestion_9" type="hidden" value="${quest9}"/>
        <strong>${quest9}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest9}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_LT1M" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_9_LT1M">Less than 1 month</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_1M" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_9_1M">1 month</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_2M" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_9_2M">2 months</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_3M" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_9_3M">3 months</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_4M" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_9_4M">4 months</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_5M" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_9_5M">5 months</label>
                <input type="radio" name="ISQQuestionEntry_9" id="ISQQuestionEntry_9_6MOM" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_9_6MOM">6 months or more</label>
            </fieldset>
        </div>
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
                <input type="radio" name="ISQQuestionEntry_10" id="ISQQuestionEntry_20_SEVERE" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_20_SEVERE">Yes, severe</label>
            </fieldset>
        </div>
        
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(5)" style="width: 150px;">Next</a>
        <!-- <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a> -->
    </div>
</div>

<div id="question_page_6" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Infant Sleep Questionaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage5Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_6" data-role="content">
        <h3>"Your views":</h3>
        <div><hr/><br/></div>
       
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
        <div id="not_finished_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Not Completed</h1>
            </div>
            <div data-role="content">
                <span>This form is not complete.  Please complete before continuing.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">Close</a>
                </div>
            </div>
        </div>
        <div id="not_finished_final_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Not Completed</h1>
            </div>
            <div data-role="content">
                <span>This form is still not complete.  Are you sure you want to continue?</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href=""  onclick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">Yes</a>
                    <a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">No</a>
                </div>
            </div>
        </div>
        <div id="finish_error_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Error</h1>
            </div>
            <div data-role="content">
                <span>There was an error submitting the form.  Please press 'OK' to try again.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="" onclick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Previous</a>
        <a  href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Finish</a>
    </div>
</div>

<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUEÑO INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_1_sp" data-role="content">
        <strong><span>Durante las &#250;ltimas 2 semanas, &#191;qu&#233; tan seguido le han afectado cualquiera de los siguientes problemas?</span></strong>
        <strong><span>&iquest;Cuánto tiempo en promedio tarda generalmente su bebé en disponerse a dormir? (marque solo una casilla</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest1_2" value='Poco inter&#233;s o placer en hacer las cosas'/>
        <input id="ISQQuestion_1_2" name="ISQQuestion_1_2" type="hidden" value="${quest1_2}"/>
        <strong>${quest1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_NAO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_NAO">Ninguno</label>
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_SD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_SD">Varios D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_MTHD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_1_2" id="ISQQuestionEntry_1_2_NED" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_1_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest2_2" value='Se ha sentido deca&#237;do(a), deprimido(a), o sin esperanzas'/>
        <input id="ISQQuestion_2_2" name="ISQQuestion_2_2" type="hidden" value="${quest2_2}"/>
        <strong>${quest2_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest2_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_NAO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_NAO">Ninguno</label>
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_SD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_SD">Varios D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_MTHD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_2_2" id="ISQQuestionEntry_2_2_NED" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_2_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Proximo</a>
        <!-- <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Dejar de</a> -->
    </div>
</div>

<div id="question_page_2_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>PHQ-9:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_2_sp" data-role="content">
        <strong><span>Durante las &#250;ltimas 2 semanas, &#191;qu&#233; tan seguido le han afectado cualquiera de los siguientes problemas?</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest3_2" value='Dificultad para dormir o permanecer dormido(a), o ha dormido demasiado'/>
        <input id="ISQQuestion_3_2" name="ISQQuestion_3_2" type="hidden" value="${quest3_2}"/>
        <strong>${quest3_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest3_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_NAO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_NAO">Ninguno</label>
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_SD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_SD">Varios D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_MTHD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_3_2" id="ISQQuestionEntry_3_2_NED" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_3_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest4_2" value='Se ha sentido cansado(a) o con poca energ&#237;a'/>
        <input id="ISQQuestion_4_2" name="ISQQuestion_4_2" type="hidden" value="${quest4_2}"/>
        <strong>${quest4_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest4_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_NAO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_NAO">Ninguno</label>
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_SD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_SD">Varios D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_MTHD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_4_2" id="ISQQuestionEntry_4_2_NED" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_4_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Proximo</a>
        <!-- <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Dejar de</a> -->
    </div>
</div>

<div id="question_page_3_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>PHQ-9:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage3SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_3_sp" data-role="content">
        <strong><span>Durante las &#250;ltimas 2 semanas, &#191;qu&#233; tan seguido le han afectado cualquiera de los siguientes problemas?</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest5_2" value='Con poco apetito o ha comido en exceso'/>
        <input id="ISQQuestion_5_2" name="ISQQuestion_5_2" type="hidden" value="${quest5_2}"/>
        <strong>${quest5_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest5_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_5_2" id="ISQQuestionEntry_5_2_NAO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_5_2_NAO">Ninguno</label>
                <input type="radio" name="ISQQuestionEntry_5_2" id="ISQQuestionEntry_5_2_SD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_5_2_SD">Varios D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_5_2" id="ISQQuestionEntry_5_2_MTHD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_5_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_5_2" id="ISQQuestionEntry_5_2_NED" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_5_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest6_2" value='Se ha sentido mal con usted mismo(a) o que es un fracaso o que ha quedado mal con usted mismo(a) o con su familia'/>
        <input id="ISQQuestion_6_2" name="ISQQuestion_6_2" type="hidden" value="${quest6_2}"/>
        <strong>${quest6_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest6_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_NAO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_NAO">Ninguno</label>
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_SD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_SD">Varios D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_MTHD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_6_2" id="ISQQuestionEntry_6_2_NED" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_6_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Proximo</a>
        <!-- <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Dejar de</a> -->
    </div>
</div>

<div id="question_page_4_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>PHQ-9:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage4SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_4_sp" data-role="content">
        <strong><span>Durante las &#250;ltimas 2 semanas, &#191;qu&#233; tan seguido le han afectado cualquiera de los siguientes problemas?</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest7_2" value='Ha tenido dificultad para concentrarse en cosas tales como leer el peri&#243;dico o ver televisi&#243;n'/>
        <input id="ISQQuestion_7_2" name="ISQQuestion_7_2" type="hidden" value="${quest7_2}"/>
        <strong>${quest7_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest7_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_NAO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_NAO">Ninguno</label>
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_SD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_SD">Varios D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_MTHD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_7_2" id="ISQQuestionEntry_7_2_NED" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_7_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest8_2" value='&#191;Se ha estado moviendo o hablando tan lento que otras personas podr&#237;an notarlo?, o por el contrario ha estado tan inquieto(a) o agitado(a), que se ha estado moviendo mucho m&#225;s de lo normal'/>
        <input id="ISQQuestion_8_2" name="ISQQuestion_8_2" type="hidden" value="${quest8_2}"/>
        <strong>${quest8_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest8_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_NAO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_NAO">Ninguno</label>
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_SD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_SD">Varios D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_MTHD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_8_2" id="ISQQuestionEntry_8_2_NED" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_8_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(5)" style="width: 150px;">Proximo</a>
        <!-- <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Dejar de</a> -->
    </div>
</div>

<div id="question_page_5_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>PHQ-9:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage5SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_5_sp" data-role="content">
        <strong><span>Durante las &#250;ltimas 2 semanas, &#191;qu&#233; tan seguido le han afectado cualquiera de los siguientes problemas?</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest9_2" value='Ha pensado que estar&#237;a mejor muerto(a) o se le ha ocurrido lastimarse de alguna manera'/>
        <input id="ISQQuestion_9_2" name="ISQQuestion_9_2" type="hidden" value="${quest9_2}"/>
        <strong>${quest9_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest9_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_NAO" value="0" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_NAO">Ninguno</label>
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_SD" value="1" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_SD">Varios D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_MTHD" value="2" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="ISQQuestionEntry_9_2" id="ISQQuestionEntry_9_2_NED" value="3" data-theme="b" />
                <label for="ISQQuestionEntry_9_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
        <div id="not_finished_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>No Completado</h1>
            </div>
            <div data-role="content">
                <span>Esta forma no es completa. Por favor complete antes de continuar.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">Cerca</a>
                </div>
            </div>
        </div>
        <div id="not_finished_final_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>No Completado</h1>
            </div>
            <div data-role="content">
                <span>Esta forma a&#250;n no est&#225; completa. &#191;Est&#225; seguro que desea continuar?</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="" onclick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">Si</a>
                    <a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">No</a>
                </div>
            </div>
        </div>
        <div id="finish_error_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Error</h1>
            </div>
            <div data-role="content">
                <span>Hubo un error al enviar el formulario. Por favor, pulse 'OK' para intentarlo de nuevo.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="" onclick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Anterior</a>
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
<input id="ISQInterpretation" name="ISQInterpretation" type="hidden"/>
<input id="language" name="language" type="hidden" value="${language}"/>
</form>
</body>
</html>
