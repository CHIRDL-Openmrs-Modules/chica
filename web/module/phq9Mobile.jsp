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
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/phq9Mobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
<form id="phq9Form" method="POST" action="phq9Mobile.form" method="post" enctype="multipart/form-data">
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
        <h1>PHQ-9:</h1>
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
        <h1>PHQ-9:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_1" data-role="content">
        <strong><span>Over the <span style="text-decoration: underline;">last 2 weeks</span>, how often have you been bothered by any of the following problems?</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest1" value='Little interest or pleasure in doing things'/>
        <input id="PHQ9Question_1" name="PHQ9Question_1" type="hidden" value="${quest1}"/>
	    <strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest1}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            <input type="radio" name="PHQ9QuestionEntry_1" id="PHQ9QuestionEntry_1_NAO" value="0" data-theme="b" />
	            <label for="PHQ9QuestionEntry_1_NAO">Not At All</label>
	            <input type="radio" name="PHQ9QuestionEntry_1" id="PHQ9QuestionEntry_1_SD" value="1" data-theme="b" />
	            <label for="PHQ9QuestionEntry_1_SD">Several Days</label>
	            <input type="radio" name="PHQ9QuestionEntry_1" id="PHQ9QuestionEntry_1_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_1_MTHD">More Than Half the Days</label>
                <input type="radio" name="PHQ9QuestionEntry_1" id="PHQ9QuestionEntry_1_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_1_NED">Nearly Every Day</label>
	        </fieldset>
	    </div>
	    <br/>
	    <br/>
	    <c:set var="quest2" value='Feeling down, depressed, or hopeless'/>
        <input id="PHQ9Question_2" name="PHQ9Question_2" type="hidden" value="${quest2}"/>
	    <strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_2" id="PHQ9QuestionEntry_2_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_2_NAO">Not At All</label>
                <input type="radio" name="PHQ9QuestionEntry_2" id="PHQ9QuestionEntry_2_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_2_SD">Several Days</label>
                <input type="radio" name="PHQ9QuestionEntry_2" id="PHQ9QuestionEntry_2_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_2_MTHD">More Than Half the Days</label>
                <input type="radio" name="PHQ9QuestionEntry_2" id="PHQ9QuestionEntry_2_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_2_NED">Nearly Every Day</label>
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
        <h1>PHQ-9:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_2" data-role="content">
        <strong><span>Over the <span style="text-decoration: underline;">last 2 weeks</span>, how often have you been bothered by any of the following problems?</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest3" value='Trouble falling or staying asleep, or sleeping too much'/>
        <input id="PHQ9Question_3" name="PHQ9Question_3" type="hidden" value="${quest3}"/>
        <strong>${quest3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_3" id="PHQ9QuestionEntry_3_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_3_NAO">Not At All</label>
                <input type="radio" name="PHQ9QuestionEntry_3" id="PHQ9QuestionEntry_3_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_3_SD">Several Days</label>
                <input type="radio" name="PHQ9QuestionEntry_3" id="PHQ9QuestionEntry_3_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_3_MTHD">More Than Half the Days</label>
                <input type="radio" name="PHQ9QuestionEntry_3" id="PHQ9QuestionEntry_3_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_3_NED">Nearly Every Day</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest4" value='Feeling tired or having little energy'/>
        <input id="PHQ9Question_4" name="PHQ9Question_4" type="hidden" value="${quest4}"/>
        <strong>${quest4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_4" id="PHQ9QuestionEntry_4_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_4_NAO">Not At All</label>
                <input type="radio" name="PHQ9QuestionEntry_4" id="PHQ9QuestionEntry_4_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_4_SD">Several Days</label>
                <input type="radio" name="PHQ9QuestionEntry_4" id="PHQ9QuestionEntry_4_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_4_MTHD">More Than Half the Days</label>
                <input type="radio" name="PHQ9QuestionEntry_4" id="PHQ9QuestionEntry_4_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_4_NED">Nearly Every Day</label>
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
        <h1>PHQ-9:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage3Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_3" data-role="content">
        <strong><span>Over the <span style="text-decoration: underline;">last 2 weeks</span>, how often have you been bothered by any of the following problems?</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest5" value='Poor appetite or overeating'/>
        <input id="PHQ9Question_5" name="PHQ9Question_5" type="hidden" value="${quest5}"/>
        <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest5}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_5" id="PHQ9QuestionEntry_5_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_5_NAO">Not At All</label>
                <input type="radio" name="PHQ9QuestionEntry_5" id="PHQ9QuestionEntry_5_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_5_SD">Several Days</label>
                <input type="radio" name="PHQ9QuestionEntry_5" id="PHQ9QuestionEntry_5_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_5_MTHD">More Than Half the Days</label>
                <input type="radio" name="PHQ9QuestionEntry_5" id="PHQ9QuestionEntry_5_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_5_NED">Nearly Every Day</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest6" value='Feeling bad about yourself - or that you are a failure or have let yourself or your family down'/>
        <input id="PHQ9Question_6" name="PHQ9Question_6" type="hidden" value="${quest6}"/>
        <strong>${quest6}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest6}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_6" id="PHQ9QuestionEntry_6_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_6_NAO">Not At All</label>
                <input type="radio" name="PHQ9QuestionEntry_6" id="PHQ9QuestionEntry_6_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_6_SD">Several Days</label>
                <input type="radio" name="PHQ9QuestionEntry_6" id="PHQ9QuestionEntry_6_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_6_MTHD">More Than Half the Days</label>
                <input type="radio" name="PHQ9QuestionEntry_6" id="PHQ9QuestionEntry_6_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_6_NED">Nearly Every Day</label>
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
        <h1>PHQ-9:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage4Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_4" data-role="content">
        <strong><span>Over the <span style="text-decoration: underline;">last 2 weeks</span>, how often have you been bothered by any of the following problems?</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest7" value='Trouble concentrating on things, such as reading the newspaper or watching television'/>
        <input id="PHQ9Question_7" name="PHQ9Question_7" type="hidden" value="${quest7}"/>
        <strong>${quest7}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest7}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_7" id="PHQ9QuestionEntry_7_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_7_NAO">Not At All</label>
                <input type="radio" name="PHQ9QuestionEntry_7" id="PHQ9QuestionEntry_7_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_7_SD">Several Days</label>
                <input type="radio" name="PHQ9QuestionEntry_7" id="PHQ9QuestionEntry_7_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_7_MTHD">More Than Half the Days</label>
                <input type="radio" name="PHQ9QuestionEntry_7" id="PHQ9QuestionEntry_7_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_7_NED">Nearly Every Day</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest8" value='Moving or speaking so slowly that other people could have noticed?  Or the opposite - being so fidgety or restless that you have been moving around a lot more than usual'/>
        <input id="PHQ9Question_8" name="PHQ9Question_8" type="hidden" value="${quest8}"/>
        <strong>${quest8}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest8}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_8" id="PHQ9QuestionEntry_8_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_8_NAO">Not At All</label>
                <input type="radio" name="PHQ9QuestionEntry_8" id="PHQ9QuestionEntry_8_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_8_SD">Several Days</label>
                <input type="radio" name="PHQ9QuestionEntry_8" id="PHQ9QuestionEntry_8_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_8_MTHD">More Than Half the Days</label>
                <input type="radio" name="PHQ9QuestionEntry_8" id="PHQ9QuestionEntry_8_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_8_NED">Nearly Every Day</label>
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
        <h1>PHQ-9:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage5Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_5" data-role="content">
        <strong><span>Over the <span style="text-decoration: underline;">last 2 weeks</span>, how often have you been bothered by any of the following problems?</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest9" value='Thoughts that you would be better off dead or of hurting yourself in some way'/>
        <input id="PHQ9Question_9" name="PHQ9Question_9" type="hidden" value="${quest9}"/>
        <strong>${quest9}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest9}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_9" id="PHQ9QuestionEntry_9_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_9_NAO">Not At All</label>
                <input type="radio" name="PHQ9QuestionEntry_9" id="PHQ9QuestionEntry_9_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_9_SD">Several Days</label>
                <input type="radio" name="PHQ9QuestionEntry_9" id="PHQ9QuestionEntry_9_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_9_MTHD">More Than Half the Days</label>
                <input type="radio" name="PHQ9QuestionEntry_9" id="PHQ9QuestionEntry_9_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_9_NED">Nearly Every Day</label>
            </fieldset>
        </div>
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

<!-- <div id="question_page_10" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage10Button" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
    </div>
    <div id="content_10" data-role="content">
        <strong>In the past year have you felt depressed or sad most days, even if you felt okay sometimes?</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("In the past year have you felt depressed or sad most days, even if you felt okay sometimes?")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_10" id="PHQ9QuestionEntry_10_Yes" value="Y" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_Yes">Yes</label>
                <input type="radio" name="PHQ9QuestionEntry_10" id="PHQ9QuestionEntry_10_No" value="N" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_No">No</label>
            </fieldset>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(11)" style="width: 150px;">Next</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a>
    </div>
</div>

<div id="question_page_10" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage10Button" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
    </div>
    <div id="content_10" data-role="content">
        <strong>If you checked off <span style="text-decoration: underline;">any</span> problems, how <span style="text-decoration: underline;">difficult</span> have these problems made it for you to do your work, take care of things at home, or get along with other people?</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("If you checked off any problems, how difficult have these problems made it for you to do your work, take care of things at home, or get along with other people?")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_10" id="PHQ9QuestionEntry_10_NDAA" value="Not difficult at all" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_NDAA">Not difficult at all</label>
                <input type="radio" name="PHQ9QuestionEntry_10" id="PHQ9QuestionEntry_10_SD" value="Somewhat difficult" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_SD">Somewhat difficult</label>
                <input type="radio" name="PHQ9QuestionEntry_10" id="PHQ9QuestionEntry_10_VD" value="Very difficult" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_VD">Very difficult</label>
                <input type="radio" name="PHQ9QuestionEntry_10" id="PHQ9QuestionEntry_10_ED" value="Extremely difficult" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_ED">Extremely difficult</label>
            </fieldset>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="finishForm()" style="width: 150px;">Finish</a>
    </div>
</div>

<div id="question_page_12" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage12Button" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
    </div>
    <div id="content_12" data-role="content">
        <strong>Has there been a time in the past month when you have had serious thoughts about ending your life?</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("Has there been a time in the past month when you have had serious thoughts about ending your life?")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_12" id="PHQ9QuestionEntry_12_Yes" value="Y" data-theme="b" />
                <label for="PHQ9QuestionEntry_12_Yes">Yes</label>
                <input type="radio" name="PHQ9QuestionEntry_12" id="PHQ9QuestionEntry_12_No" value="N" data-theme="b" />
                <label for="PHQ9QuestionEntry_12_No">No</label>
            </fieldset>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(11)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(13)" style="width: 150px;">Next</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a>
    </div>
</div>

<div id="question_page_13" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage13Button" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
    </div>
    <div id="content_13" data-role="content">
        <strong>Have you ever, in your whole life, tried to kill yourself or made a suicide attempt?</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("Have you ever, in your whole life, tried to kill yourself or made a suicide attempt?")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_13" id="PHQ9QuestionEntry_13_Yes" value="Y" data-theme="b" />
                <label for="PHQ9QuestionEntry_13_Yes">Yes</label>
                <input type="radio" name="PHQ9QuestionEntry_13" id="PHQ9QuestionEntry_13_No" value="N" data-theme="b" />
                <label for="PHQ9QuestionEntry_13_No">No</label>
            </fieldset>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(11)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="finishForm()" style="width: 150px;">Finish</a>
    </div>
</div> -->

<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>PHQ-9:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_1_sp" data-role="content">
        <strong><span>Durante las &#250;ltimas 2 semanas, &#191;qu&#233; tan seguido le han afectado cualquiera de los siguientes problemas?</span></strong>
        <div><hr/><br/></div>
        <c:set var="quest1_2" value='Poco inter&#233;s o placer en hacer las cosas'/>
        <input id="PHQ9Question_1_2" name="PHQ9Question_1_2" type="hidden" value="${quest1_2}"/>
        <strong>${quest1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_1_2" id="PHQ9QuestionEntry_1_2_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_1_2_NAO">Ninguno</label>
                <input type="radio" name="PHQ9QuestionEntry_1_2" id="PHQ9QuestionEntry_1_2_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_1_2_SD">Varios D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_1_2" id="PHQ9QuestionEntry_1_2_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_1_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_1_2" id="PHQ9QuestionEntry_1_2_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_1_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest2_2" value='Se ha sentido deca&#237;do(a), deprimido(a), o sin esperanzas'/>
        <input id="PHQ9Question_2_2" name="PHQ9Question_2_2" type="hidden" value="${quest2_2}"/>
        <strong>${quest2_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest2_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_2_2" id="PHQ9QuestionEntry_2_2_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_2_2_NAO">Ninguno</label>
                <input type="radio" name="PHQ9QuestionEntry_2_2" id="PHQ9QuestionEntry_2_2_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_2_2_SD">Varios D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_2_2" id="PHQ9QuestionEntry_2_2_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_2_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_2_2" id="PHQ9QuestionEntry_2_2_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_2_2_NED">Casi Todos los D&#237;as</label>
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
        <input id="PHQ9Question_3_2" name="PHQ9Question_3_2" type="hidden" value="${quest3_2}"/>
        <strong>${quest3_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest3_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_3_2" id="PHQ9QuestionEntry_3_2_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_3_2_NAO">Ninguno</label>
                <input type="radio" name="PHQ9QuestionEntry_3_2" id="PHQ9QuestionEntry_3_2_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_3_2_SD">Varios D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_3_2" id="PHQ9QuestionEntry_3_2_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_3_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_3_2" id="PHQ9QuestionEntry_3_2_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_3_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest4_2" value='Se ha sentido cansado(a) o con poca energ&#237;a'/>
        <input id="PHQ9Question_4_2" name="PHQ9Question_4_2" type="hidden" value="${quest4_2}"/>
        <strong>${quest4_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest4_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_4_2" id="PHQ9QuestionEntry_4_2_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_4_2_NAO">Ninguno</label>
                <input type="radio" name="PHQ9QuestionEntry_4_2" id="PHQ9QuestionEntry_4_2_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_4_2_SD">Varios D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_4_2" id="PHQ9QuestionEntry_4_2_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_4_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_4_2" id="PHQ9QuestionEntry_4_2_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_4_2_NED">Casi Todos los D&#237;as</label>
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
        <input id="PHQ9Question_5_2" name="PHQ9Question_5_2" type="hidden" value="${quest5_2}"/>
        <strong>${quest5_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest5_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_5_2" id="PHQ9QuestionEntry_5_2_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_5_2_NAO">Ninguno</label>
                <input type="radio" name="PHQ9QuestionEntry_5_2" id="PHQ9QuestionEntry_5_2_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_5_2_SD">Varios D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_5_2" id="PHQ9QuestionEntry_5_2_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_5_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_5_2" id="PHQ9QuestionEntry_5_2_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_5_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest6_2" value='Se ha sentido mal con usted mismo(a) o que es un fracaso o que ha quedado mal con usted mismo(a) o con su familia'/>
        <input id="PHQ9Question_6_2" name="PHQ9Question_6_2" type="hidden" value="${quest6_2}"/>
        <strong>${quest6_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest6_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_6_2" id="PHQ9QuestionEntry_6_2_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_6_2_NAO">Ninguno</label>
                <input type="radio" name="PHQ9QuestionEntry_6_2" id="PHQ9QuestionEntry_6_2_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_6_2_SD">Varios D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_6_2" id="PHQ9QuestionEntry_6_2_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_6_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_6_2" id="PHQ9QuestionEntry_6_2_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_6_2_NED">Casi Todos los D&#237;as</label>
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
        <input id="PHQ9Question_7_2" name="PHQ9Question_7_2" type="hidden" value="${quest7_2}"/>
        <strong>${quest7_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest7_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_7_2" id="PHQ9QuestionEntry_7_2_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_7_2_NAO">Ninguno</label>
                <input type="radio" name="PHQ9QuestionEntry_7_2" id="PHQ9QuestionEntry_7_2_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_7_2_SD">Varios D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_7_2" id="PHQ9QuestionEntry_7_2_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_7_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_7_2" id="PHQ9QuestionEntry_7_2_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_7_2_NED">Casi Todos los D&#237;as</label>
            </fieldset>
        </div>
        <br/>
        <br/>
        <c:set var="quest8_2" value='&#191;Se ha estado moviendo o hablando tan lento que otras personas podr&#237;an notarlo?, o por el contrario ha estado tan inquieto(a) o agitado(a), que se ha estado moviendo mucho m&#225;s de lo normal'/>
        <input id="PHQ9Question_8_2" name="PHQ9Question_8_2" type="hidden" value="${quest8_2}"/>
        <strong>${quest8_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest8_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_8_2" id="PHQ9QuestionEntry_8_2_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_8_2_NAO">Ninguno</label>
                <input type="radio" name="PHQ9QuestionEntry_8_2" id="PHQ9QuestionEntry_8_2_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_8_2_SD">Varios D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_8_2" id="PHQ9QuestionEntry_8_2_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_8_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_8_2" id="PHQ9QuestionEntry_8_2_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_8_2_NED">Casi Todos los D&#237;as</label>
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
        <input id="PHQ9Question_9_2" name="PHQ9Question_9_2" type="hidden" value="${quest9_2}"/>
        <strong>${quest9_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest9_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_9_2" id="PHQ9QuestionEntry_9_2_NAO" value="0" data-theme="b" />
                <label for="PHQ9QuestionEntry_9_2_NAO">Ninguno</label>
                <input type="radio" name="PHQ9QuestionEntry_9_2" id="PHQ9QuestionEntry_9_2_SD" value="1" data-theme="b" />
                <label for="PHQ9QuestionEntry_9_2_SD">Varios D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_9_2" id="PHQ9QuestionEntry_9_2_MTHD" value="2" data-theme="b" />
                <label for="PHQ9QuestionEntry_9_2_MTHD">Mas de la Mitad de los D&#237;as</label>
                <input type="radio" name="PHQ9QuestionEntry_9_2" id="PHQ9QuestionEntry_9_2_NED" value="3" data-theme="b" />
                <label for="PHQ9QuestionEntry_9_2_NED">Casi Todos los D&#237;as</label>
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

<!-- <div id="question_page_10_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage10SPButton" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
    </div>
    <div id="content_10_sp" data-role="content">
        <strong>&#191;En el año pasado se ha sentido deprimido o triste la mayor&#237;a de los d&#237;as, aun cuando se siente bien algunas veces?</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("&#191;En el año pasado se ha sentido deprimido o triste la mayor&#237;a de los d&#237;as, aun cuando se siente bien algunas veces?")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_10_2" id="PHQ9QuestionEntry_10_2_Yes" value="Y" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_2_Yes">Si</label>
                <input type="radio" name="PHQ9QuestionEntry_10_2" id="PHQ9QuestionEntry_10_2_No" value="N" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_2_No">No</label>
            </fieldset>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(11)" style="width: 150px;">Proximo</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Dejar de</a>
    </div>
</div>

<div id="question_page_10_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage10SPButton" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
    </div>
    <div id="content_10_sp" data-role="content">
        <strong>Si usted marc&#243; cualquiera de estos problemas, &#191;qu&#233; tan dif&#237;cil fue hacer su trabajo, las tareas del hogar o llevarse bien con otras personas debido a tales problemas?</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("Si usted marc&#243; cualquiera de estos problemas, &#191;qu&#233; tan dif&#237;cil fue hacer su trabajo, las tareas del hogar o llevarse bien con otras personas debido a tales problemas?")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_10_2" id="PHQ9QuestionEntry_10_2_NDAA" value="Not difficult at all" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_2_NDAA">Para nada dif&#237;cil</label>
                <input type="radio" name="PHQ9QuestionEntry_10_2" id="PHQ9QuestionEntry_10_2_SD" value="Somewhat difficult" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_2_SD">Un poco dif&#237;cil</label>
                <input type="radio" name="PHQ9QuestionEntry_10_2" id="PHQ9QuestionEntry_10_2_VD" value="Very difficult" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_2_VD">Muy dif&#237;cil</label>
                <input type="radio" name="PHQ9QuestionEntry_10_2" id="PHQ9QuestionEntry_10_2_ED" value="Extremely difficult" data-theme="b" />
                <label for="PHQ9QuestionEntry_10_2_ED">Extremadamente dif&#237;cil</label>
            </fieldset>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="finishForm()" style="width: 150px;">Terminar</a>
    </div>
</div>

<div id="question_page_12_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage12SPButton" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
    </div>
    <div id="content_12_sp" data-role="content">
        <strong>&#191;En el mes pasado hubo algún momento donde usted pens&#243; seriamente en terminar con su vida?</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("=&#191;En el mes pasado hubo algún momento donde usted pens&#243; seriamente en terminar con su vida?")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_12_2" id="PHQ9QuestionEntry_12_2_Yes" value="Y" data-theme="b" />
                <label for="PHQ9QuestionEntry_12_2_Yes">Si</label>
                <input type="radio" name="PHQ9QuestionEntry_12_2" id="PHQ9QuestionEntry_12_2_No" value="N" data-theme="b" />
                <label for="PHQ9QuestionEntry_12_2_No">No</label>
            </fieldset>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(11)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(13)" style="width: 150px;">Proximo</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Dejar de</a>
    </div>
</div>

<div id="question_page_13_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage13SPButton" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
    </div>
    <div id="content_13_sp" data-role="content">
        <strong>&#191;Alguna vez en su vida, trato de matarse o trato de suicidarse?</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("&#191;Alguna vez en su vida, trato de matarse o trato de suicidarse?")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="PHQ9QuestionEntry_13_2" id="PHQ9QuestionEntry_13_2_Yes" value="Y" data-theme="b" />
                <label for="PHQ9QuestionEntry_13_2_Yes">Si</label>
                <input type="radio" name="PHQ9QuestionEntry_13_2" id="PHQ9QuestionEntry_13_2_No" value="N" data-theme="b" />
                <label for="PHQ9QuestionEntry_13_2_No">No</label>
            </fieldset>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(12)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="finishForm()" style="width: 150px;">Terminar</a>
    </div>
</div> -->

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
<input id="PHQ9Score" name="PHQ9Score" type="hidden"/>
<input id="PHQ9Interpretation" name="PHQ9Interpretation" type="hidden"/>
<input id="language" name="language" type="hidden" value="${language}"/>
</form>
</body>
</html>
