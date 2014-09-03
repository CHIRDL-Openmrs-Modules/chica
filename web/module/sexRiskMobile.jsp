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
<script src="${pageContext.request.contextPath}/moduleResources/chica/sexRiskMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<body style="font-size: 20px" onLoad="init('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}', '${formInstance}', '${language}', '${AgeInYears}', '${patient.gender}')">
<form id="sexRiskForm" method="POST" action="sexRiskMobile.form" method="post" enctype="multipart/form-data">
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

<div data-role="page" id="instruction_page" data-theme="b" style="font-size: 20px">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a id="vitalsButton" data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>

    <div data-role="content" >
        <strong><span id="additionalQuestions">The following are some additional questions about sexual behavior.</span></strong>
        <div><br/></div>
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage()" style="width: 150px;">Start</a>
    </div>
</div>

<div id="question_page_early_M" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div data-role="content">
        <c:set var="quest_early_M_1" value='Are you having unprotected sex?'/>
        <input id="Question_1_early_M" name="Question_1_early_M" type="hidden" value="${quest_early_M_1}"/>
        <strong>${quest_early_M_1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_early_M_1}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_early_M" id="QuestionEntry_1_early_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_1_early_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_1_early_M" id="QuestionEntry_1_early_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_1_early_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_early_M_2" value='Was your first time having sexual intercourse more than 3 years ago?'/>
        <input id="Question_2_early_M" name="Question_2_early_M" type="hidden" value="${quest_early_M_2}"/>
        <strong>${quest_early_M_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_early_M_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_early_M" id="QuestionEntry_2_early_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_2_early_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_2_early_M" id="QuestionEntry_2_early_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_2_early_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_early_M_3" value='Have you or your partner been sexually active without using birth control?'/>
        <input id="Question_3_early_M" name="Question_3_early_M" type="hidden" value="${quest_early_M_3}"/>
        <strong>${quest_early_M_3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_early_M_3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_early_M" id="QuestionEntry_3_early_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_early_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_3_early_M" id="QuestionEntry_3_early_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_early_M_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_early_M" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_early_M" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_early_M" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Finish</a>
    </div>
</div>

<div id="question_page_early_F" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div data-role="content">
        <c:set var="quest_early_F_1" value='Are you having unprotected sex?'/>
        <input id="Question_1_early_F" name="Question_1_early_F" type="hidden" value="${quest_early_F_1}"/>
        <strong>${quest_early_F_1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_early_F_1}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_early_F" id="QuestionEntry_1_early_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_1_early_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_1_early_F" id="QuestionEntry_1_early_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_1_early_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_early_F_2" value='Was your first time having sexual intercourse more than 3 years ago?'/>
        <input id="Question_2_early_F" name="Question_2_early_F" type="hidden" value="${quest_early_F_2}"/>
        <strong>${quest_early_F_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_early_F_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_early_F" id="QuestionEntry_2_early_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_2_early_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_2_early_F" id="QuestionEntry_2_early_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_2_early_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_early_F_3" value='Have you been sexually active and had a late or missed period within the last 2 months?'/>
        <input id="Question_3_early_F" name="Question_3_early_F" type="hidden" value="${quest_early_F_3}"/>
        <strong>${quest_early_F_3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_early_F_3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_early_F" id="QuestionEntry_3_early_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_early_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_3_early_F" id="QuestionEntry_3_early_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_early_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_early_F_4" value='Have you or your partner been sexually active without using birth control?'/>
        <input id="Question_4_early_F" name="Question_4_early_F" type="hidden" value="${quest_early_F_4}"/>
        <strong>${quest_early_F_4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_early_F_4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_early_F" id="QuestionEntry_4_early_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_early_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_4_early_F" id="QuestionEntry_4_early_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_early_F_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_early_F" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_early_F" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_early_F" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Finish</a>
    </div>
</div>

<div id="question_page_middle_M" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div data-role="content">
        <c:set var="quest_middle_M_1" value='Are you using a method to prevent pregnancy?'/>
        <input id="Question_1_middle_M" name="Question_1_middle_M" type="hidden" value="${quest_middle_M_1}"/>
        <strong>${quest_middle_M_1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_middle_M_1}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_middle_M" id="QuestionEntry_1_middle_M_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_1_middle_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_1_middle_M" id="QuestionEntry_1_middle_M_No" value="1" data-theme="c" />
                <label for="QuestionEntry_1_middle_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_M_2" value='Have you ever gotten someone pregnant?'/>
        <input id="Question_2_middle_M" name="Question_2_middle_M" type="hidden" value="${quest_middle_M_2}"/>
        <strong>${quest_middle_M_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_middle_M_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_middle_M" id="QuestionEntry_2_middle_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_2_middle_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_2_middle_M" id="QuestionEntry_2_middle_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_2_middle_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_M_3" value='Have your partners been both male and female?'/>
        <input id="Question_3_middle_M" name="Question_3_middle_M" type="hidden" value="${quest_middle_M_3}"/>
        <strong>${quest_middle_M_3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_middle_M_3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_middle_M" id="QuestionEntry_3_middle_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_middle_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_3_middle_M" id="QuestionEntry_3_middle_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_middle_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_M_4" value='Do you think you or your partner could have a sexually transmitted infection?'/>
        <input id="Question_4_middle_M" name="Question_4_middle_M" type="hidden" value="${quest_middle_M_4}"/>
        <strong>${quest_middle_M_4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_middle_M_4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_middle_M" id="QuestionEntry_4_middle_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_middle_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_4_middle_M" id="QuestionEntry_4_middle_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_middle_M_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_middle_M" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_middle_M" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_middle_M" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Finish</a>
    </div>
</div>

<div id="question_page_middle_F" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div data-role="content">
        <c:set var="quest_middle_F_1" value='Are you using a method to prevent pregnancy?'/>
        <input id="Question_1_middle_F" name="Question_1_middle_F" type="hidden" value="${quest_middle_F_1}"/>
        <strong>${quest_middle_F_1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_middle_F_1}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_middle_F" id="QuestionEntry_1_middle_F_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_1_middle_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_1_middle_F" id="QuestionEntry_1_middle_F_No" value="1" data-theme="c" />
                <label for="QuestionEntry_1_middle_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_F_2" value='Have you ever been pregnant?'/>
        <input id="Question_2_middle_F" name="Question_2_middle_F" type="hidden" value="${quest_middle_F_2}"/>
        <strong>${quest_middle_F_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_middle_F_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_middle_F" id="QuestionEntry_2_middle_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_2_middle_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_2_middle_F" id="QuestionEntry_2_middle_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_2_middle_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_F_3" value='Have your partners been both male and female?'/>
        <input id="Question_3_middle_F" name="Question_3_middle_F" type="hidden" value="${quest_middle_F_3}"/>
        <strong>${quest_middle_F_3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_middle_F_3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_middle_F" id="QuestionEntry_3_middle_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_middle_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_3_middle_F" id="QuestionEntry_3_middle_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_middle_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_F_4" value='Do you think you or your partner could have a sexually transmitted infection?'/>
        <input id="Question_4_middle_F" name="Question_4_middle_F" type="hidden" value="${quest_middle_F_4}"/>
        <strong>${quest_middle_F_4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_middle_F_4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_middle_F" id="QuestionEntry_4_middle_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_middle_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_4_middle_F" id="QuestionEntry_4_middle_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_middle_F_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_middle_F" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_middle_F" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_middle_F" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Finish</a>
    </div>
</div>

<div id="question_page_late_M" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div data-role="content">
        <c:set var="quest_late_M_1" value='Do you and your partner(s) always use condoms when you have sex?'/>
        <input id="Question_1_late_M" name="Question_1_late_M" type="hidden" value="${quest_late_M_1}"/>
        <strong>${quest_late_M_1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_late_M_1}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_late_M" id="QuestionEntry_1_late_M_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_1_late_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_1_late_M" id="QuestionEntry_1_late_M_No" value="1" data-theme="c" />
                <label for="QuestionEntry_1_late_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_M_2" value='Are you using a method to prevent pregnancy?'/>
        <input id="Question_2_late_M" name="Question_2_late_M" type="hidden" value="${quest_late_M_2}"/>
        <strong>${quest_late_M_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_late_M_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_late_M" id="QuestionEntry_2_late_M_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_2_late_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_2_late_M" id="QuestionEntry_2_late_M_No" value="1" data-theme="c" />
                <label for="QuestionEntry_2_late_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_M_3" value='Have you ever gotten someone pregnant?'/>
        <input id="Question_3_late_M" name="Question_3_late_M" type="hidden" value="${quest_late_M_3}"/>
        <strong>${quest_late_M_3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_late_M_3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_late_M" id="QuestionEntry_3_late_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_late_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_3_late_M" id="QuestionEntry_3_late_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_late_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_M_4" value='Have your partners been both male and female?'/>
        <input id="Question_4_late_M" name="Question_4_late_M" type="hidden" value="${quest_late_M_4}"/>
        <strong>${quest_late_M_4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_late_M_4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_late_M" id="QuestionEntry_4_late_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_late_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_4_late_M" id="QuestionEntry_4_late_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_late_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_M_5" value='Do you think you or your partner could have a sexually transmitted infection?'/>
        <input id="Question_5_late_M" name="Question_5_late_M" type="hidden" value="${quest_late_M_5}"/>
        <strong>${quest_late_M_5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_late_M_5}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_5_late_M" id="QuestionEntry_5_late_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_5_late_M_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_5_late_M" id="QuestionEntry_5_late_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_5_late_M_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_late_M" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_late_M" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_late_M" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Finish</a>
    </div>
</div>

<div id="question_page_late_F" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div data-role="content">
        <c:set var="quest_late_F_1" value='Do you and your partner(s) always use condoms when you have sex?'/>
        <input id="Question_1_late_F" name="Question_1_late_F" type="hidden" value="${quest_late_F_1}"/>
        <strong>${quest_late_F_1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_late_F_1}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_late_F" id="QuestionEntry_1_late_F_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_1_late_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_1_late_F" id="QuestionEntry_1_late_F_No" value="1" data-theme="c" />
                <label for="QuestionEntry_1_late_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_F_2" value='Are you using a method to prevent pregnancy?'/>
        <input id="Question_2_late_F" name="Question_2_late_F" type="hidden" value="${quest_late_F_2}"/>
        <strong>${quest_late_F_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_late_F_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_late_F" id="QuestionEntry_2_late_F_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_2_late_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_2_late_F" id="QuestionEntry_2_late_F_No" value="1" data-theme="c" />
                <label for="QuestionEntry_2_late_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_F_3" value='Have you ever been pregnant?'/>
        <input id="Question_3_late_F" name="Question_3_late_F" type="hidden" value="${quest_late_F_3}"/>
        <strong>${quest_late_F_3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_late_F_3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_late_F" id="QuestionEntry_3_late_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_late_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_3_late_F" id="QuestionEntry_3_late_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_late_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_F_4" value='Have your partners been both male and female?'/>
        <input id="Question_4_late_F" name="Question_4_late_F" type="hidden" value="${quest_late_F_4}"/>
        <strong>${quest_late_F_4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_late_F_4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_late_F" id="QuestionEntry_4_late_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_late_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_4_late_F" id="QuestionEntry_4_late_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_late_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_F_5" value='Do you think you or your partner could have a sexually transmitted infection?'/>
        <input id="Question_5_late_F" name="Question_5_late_F" type="hidden" value="${quest_late_F_5}"/>
        <strong>${quest_late_F_5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_late_F_5}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_5_late_F" id="QuestionEntry_5_late_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_5_late_F_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_5_late_F" id="QuestionEntry_5_late_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_5_late_F_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_late_F" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_late_F" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_late_F" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Finish</a>
    </div>
</div>

<div id="question_page_early_M_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div data-role="content">
        <c:set var="quest_early_M_1_sp" value='&#191;Est&#225;s teniendo sexo sin protecci&#243;n?'/>
        <input id="Question_1_2_early_M" name="Question_1_2_early_M" type="hidden" value="${quest_early_M_1_sp}"/>
        <strong>${quest_early_M_1_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_early_M_1_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_2_early_M" id="QuestionEntry_1_2_early_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_1_2_early_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_1_2_early_M" id="QuestionEntry_1_2_early_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_1_2_early_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_early_M_2_sp" value='&#191;Tuviste tu primera relaci&#243;n sexual con penetraci&#243;n hace m&#225;s de tres a&#241;os?'/>
        <input id="Question_2_2_early_M" name="Question_2_2_early_M" type="hidden" value="${quest_early_M_2_sp}"/>
        <strong>${quest_early_M_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_early_M_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_2_early_M" id="QuestionEntry_2_2_early_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_2_2_early_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_2_2_early_M" id="QuestionEntry_2_2_early_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_2_2_early_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_early_M_3_sp" value='&#191;Ha estado Ud. o su pareja activo sexualmente sin control de natalidad?'/>
        <input id="Question_3_2_early_M" name="Question_3_2_early_M" type="hidden" value="${quest_early_M_3_sp}"/>
        <strong>${quest_early_M_3_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_early_M_3_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_2_early_M" id="QuestionEntry_3_2_early_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_2_early_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_3_2_early_M" id="QuestionEntry_3_2_early_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_2_early_M_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_early_M_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_early_M_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_early_M_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Acabado</a>
    </div>
</div>

<div id="question_page_early_F_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div data-role="content">
        <c:set var="quest_early_F_1_sp" value='&#191;Est&#225;s teniendo sexo sin protecci&#243;n?'/>
        <input id="Question_1_2_early_F" name="Question_1_2_early_F" type="hidden" value="${quest_early_F_1_sp}"/>
        <strong>${quest_early_F_1_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_early_F_1_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_2_early_F" id="QuestionEntry_1_2_early_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_1_2_early_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_1_2_early_F" id="QuestionEntry_1_2_early_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_1_2_early_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_early_F_2_sp" value='&#191;Tuviste tu primera relaci&#243;n sexual con penetraci&#243;n hace m&#225;s de tres a&#241;os?'/>
        <input id="Question_2_2_early_F" name="Question_2_2_early_F" type="hidden" value="${quest_early_F_2_sp}"/>
        <strong>${quest_early_F_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_early_F_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_2_early_F" id="QuestionEntry_2_2_early_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_2_2_early_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_2_2_early_F" id="QuestionEntry_2_2_early_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_2_2_early_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_early_F_3_sp" value='&#191;Has sido sexualmente activa y has tenido un retraso en el per&#237;odo en los &#250;ltimos 2 meses?'/>
        <input id="Question_3_2_early_F" name="Question_3_2_early_F" type="hidden" value="${quest_early_F_3_sp}"/>
        <strong>${quest_early_F_3_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_early_F_3_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_2_early_F" id="QuestionEntry_3_2_early_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_2_early_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_3_2_early_F" id="QuestionEntry_3_2_early_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_2_early_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_early_F_4_sp" value='&#191;Ha estado Ud. o su pareja activo sexualmente sin control de natalidad?'/>
        <input id="Question_4_2_early_F" name="Question_4_2_early_F" type="hidden" value="${quest_early_F_4_sp}"/>
        <strong>${quest_early_F_4_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_early_F_4_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_2_early_F" id="QuestionEntry_4_2_early_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_2_early_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_4_2_early_F" id="QuestionEntry_4_2_early_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_2_early_F_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_early_F_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_early_F_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_early_F_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Acabado</a>
    </div>
</div>

<div id="question_page_middle_M_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div data-role="content">
        <c:set var="quest_middle_M_1_sp" value='&#191;Est&#225;s usando un m&#233;todo para prevenir embarazos?'/>
        <input id="Question_1_2_middle_M" name="Question_1_2_middle_M" type="hidden" value="${quest_middle_M_1_sp}"/>
        <strong>${quest_middle_M_1_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_middle_M_1_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_2_middle_M" id="QuestionEntry_1_2_middle_M_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_1_2_middle_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_1_2_middle_M" id="QuestionEntry_1_2_middle_M_No" value="1" data-theme="c" />
                <label for="QuestionEntry_1_2_middle_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_M_2_sp" value='&#191;Alguna vez Ud. ha embarazado a alguien?'/>
        <input id="Question_2_2_middle_M" name="Question_2_2_middle_M" type="hidden" value="${quest_middle_M_2_sp}"/>
        <strong>${quest_middle_M_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_middle_M_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_2_middle_M" id="QuestionEntry_2_2_middle_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_2_2_middle_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_2_2_middle_M" id="QuestionEntry_2_2_middle_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_2_2_middle_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_M_3_sp" value='&#191;Han sido tus parejas tanto hombres como mujeres?'/>
        <input id="Question_3_2_middle_M" name="Question_3_2_middle_M" type="hidden" value="${quest_middle_M_3_sp}"/>
        <strong>${quest_middle_M_3_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_middle_M_3_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_2_middle_M" id="QuestionEntry_3_2_middle_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_2_middle_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_3_2_middle_M" id="QuestionEntry_3_2_middle_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_2_middle_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_M_4_sp" value='&#191;Crees que t&#250; o tu pareja podr&#237;an tener una infecci&#243;n de transmisi&#243;n sexual?'/>
        <input id="Question_4_2_middle_M" name="Question_4_2_middle_M" type="hidden" value="${quest_middle_M_4_sp}"/>
        <strong>${quest_middle_M_4_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_middle_M_4_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_2_middle_M" id="QuestionEntry_4_2_middle_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_2_middle_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_4_2_middle_M" id="QuestionEntry_4_2_middle_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_2_middle_M_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_middle_M_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_middle_M_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_middle_M_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Acabado</a>
    </div>
</div>

<div id="question_page_middle_F_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div data-role="content">
        <c:set var="quest_middle_F_1_sp" value='&#191;Est&#225;s usando un m&#233;todo para prevenir embarazos?'/>
        <input id="Question_1_2_middle_F" name="Question_1_2_middle_F" type="hidden" value="${quest_middle_F_1_sp}"/>
        <strong>${quest_middle_F_1_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_middle_F_1_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_2_middle_F" id="QuestionEntry_1_2_middle_F_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_1_2_middle_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_1_2_middle_F" id="QuestionEntry_1_2_middle_F_No" value="1" data-theme="c" />
                <label for="QuestionEntry_1_2_middle_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_F_2_sp" value='&#191;Alguna vez ha estado Ud. embarazada?'/>
        <input id="Question_2_2_middle_F" name="Question_2_2_middle_F" type="hidden" value="${quest_middle_F_2_sp}"/>
        <strong>${quest_middle_F_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_middle_F_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_2_middle_F" id="QuestionEntry_2_2_middle_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_2_2_middle_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_2_2_middle_F" id="QuestionEntry_2_2_middle_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_2_2_middle_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_F_3_sp" value='&#191;Han sido tus parejas tanto hombres como mujeres?'/>
        <input id="Question_3_2_middle_F" name="Question_3_2_middle_F" type="hidden" value="${quest_middle_F_3_sp}"/>
        <strong>${quest_middle_F_3_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_middle_F_3_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_2_middle_F" id="QuestionEntry_3_2_middle_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_2_middle_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_3_2_middle_F" id="QuestionEntry_3_2_middle_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_2_middle_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_middle_F_4_sp" value='&#191;Crees que t&#250; o tu pareja podr&#237;an tener una infecci&#243;n de transmisi&#243;n sexual?'/>
        <input id="Question_4_2_middle_F" name="Question_4_2_middle_F" type="hidden" value="${quest_middle_F_4_sp}"/>
        <strong>${quest_middle_F_4_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_middle_F_4_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_2_middle_F" id="QuestionEntry_4_2_middle_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_2_middle_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_4_2_middle_F" id="QuestionEntry_4_2_middle_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_2_middle_F_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_middle_F_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_middle_F_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_middle_F_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Acabado</a>
    </div>
</div>

<div id="question_page_late_M_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div data-role="content">
        <c:set var="quest_late_M_1_sp" value='&#191;T&#250; y tu(s) pareja(s) usan siempre condones al tener sexo?'/>
        <input id="Question_1_2_late_M" name="Question_1_2_late_M" type="hidden" value="${quest_late_M_1_sp}"/>
        <strong>${quest_late_M_1_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_late_M_1_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_2_late_M" id="QuestionEntry_1_2_late_M_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_1_2_late_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_1_2_late_M" id="QuestionEntry_1_2_late_M_No" value="1" data-theme="c" />
                <label for="QuestionEntry_1_2_late_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_M_2_sp" value='&#191;Est&#225;s usando un m&#233;todo para prevenir embarazos?'/>
        <input id="Question_2_2_late_M" name="Question_2_2_late_M" type="hidden" value="${quest_late_M_2_sp}"/>
        <strong>${quest_late_M_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_late_M_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_2_late_M" id="QuestionEntry_2_2_late_M_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_2_2_late_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_2_2_late_M" id="QuestionEntry_2_2_late_M_No" value="1" data-theme="c" />
                <label for="QuestionEntry_2_2_late_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_M_3_sp" value='&#191;Alguna vez Ud. ha embarazado a alguien?'/>
        <input id="Question_3_2_late_M" name="Question_3_2_late_M" type="hidden" value="${quest_late_M_3_sp}"/>
        <strong>${quest_late_M_3_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_late_M_3_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_2_late_M" id="QuestionEntry_3_2_late_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_2_late_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_3_2_late_M" id="QuestionEntry_3_2_late_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_2_late_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_M_4_sp" value='&#191;Han sido tus parejas tanto hombres como mujeres?'/>
        <input id="Question_4_2_late_M" name="Question_4_2_late_M" type="hidden" value="${quest_late_M_4_sp}"/>
        <strong>${quest_late_M_4_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_late_M_4_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_2_late_M" id="QuestionEntry_4_2_late_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_2_late_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_4_2_late_M" id="QuestionEntry_4_2_late_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_2_late_M_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_M_5_sp" value='&#191;Crees que t&#250; o tu pareja podr&#237;an tener una infecci&#243;n de transmisi&#243;n sexual?'/>
        <input id="Question_5_2_late_M" name="Question_5_2_late_M" type="hidden" value="${quest_late_M_5_sp}"/>
        <strong>${quest_late_M_5_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_late_M_5_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_5_2_late_M" id="QuestionEntry_5_2_late_M_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_5_2_late_M_Yes">Si</label>
                <input type="radio" name="QuestionEntry_5_2_late_M" id="QuestionEntry_5_2_late_M_No" value="0" data-theme="c" />
                <label for="QuestionEntry_5_2_late_M_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_late_M_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_late_M_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_late_M_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Acabado</a>
    </div>
</div>

<div id="question_page_late_F_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div data-role="content">
        <c:set var="quest_late_F_1_sp" value='&#191;T&#250; y tu(s) pareja(s) usan siempre condones al tener sexo?'/>
        <input id="Question_1_2_late_F" name="Question_1_2_late_F" type="hidden" value="${quest_late_F_1_sp}"/>
        <strong>${quest_late_F_1_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_late_F_1_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_2_late_F" id="QuestionEntry_1_2_late_F_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_1_2_late_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_1_2_late_F" id="QuestionEntry_1_2_late_F_No" value="1" data-theme="c" />
                <label for="QuestionEntry_1_2_late_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_F_2_sp" value='&#191;Est&#225;s usando un m&#233;todo para prevenir embarazos?'/>
        <input id="Question_2_2_late_F" name="Question_2_2_late_F" type="hidden" value="${quest_late_F_2_sp}"/>
        <strong>${quest_late_F_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_late_F_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_2_late_F" id="QuestionEntry_2_2_late_F_Yes" value="0" data-theme="c" />
                <label for="QuestionEntry_2_2_late_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_2_2_late_F" id="QuestionEntry_2_2_late_F_No" value="1" data-theme="c" />
                <label for="QuestionEntry_2_2_late_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_F_3_sp" value='&#191;Alguna vez ha estado Ud. embarazada?'/>
        <input id="Question_3_2_late_F" name="Question_3_2_late_F" type="hidden" value="${quest_late_F_3_sp}"/>
        <strong>${quest_late_F_3_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_late_F_3_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_2_late_F" id="QuestionEntry_3_2_late_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_2_late_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_3_2_late_F" id="QuestionEntry_3_2_late_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_2_late_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_F_4_sp" value='&#191;Han sido tus parejas tanto hombres como mujeres?'/>
        <input id="Question_4_2_late_F" name="Question_4_2_late_F" type="hidden" value="${quest_late_F_4_sp}"/>
        <strong>${quest_late_F_4_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_late_F_4_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_2_late_F" id="QuestionEntry_4_2_late_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_2_late_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_4_2_late_F" id="QuestionEntry_4_2_late_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_2_late_F_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest_late_F_5_sp" value='&#191;Crees que t&#250; o tu pareja podr&#237;an tener una infecci&#243;n de transmisi&#243;n sexual?'/>
        <input id="Question_5_2_late_F" name="Question_5_2_late_F" type="hidden" value="${quest_late_F_5_sp}"/>
        <strong>${quest_late_F_5_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_late_F_5_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_5_2_late_F" id="QuestionEntry_5_2_late_F_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_5_2_late_F_Yes">Si</label>
                <input type="radio" name="QuestionEntry_5_2_late_F" id="QuestionEntry_5_2_late_F_No" value="0" data-theme="c" />
                <label for="QuestionEntry_5_2_late_F_No">No</label>
            </fieldset>
        </div>
        <div id="not_finished_dialog_late_F_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="not_finished_final_dialog_late_F_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <div id="finish_error_dialog_late_F_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Acabado</a>
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
<input id="SexRiskScore" name="SexRiskScore" type="hidden"/>
<input id="language" name="language" type="hidden" value="${language}"/>
</form>
</body>
</html>
