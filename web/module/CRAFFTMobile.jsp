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
<script src="${pageContext.request.contextPath}/moduleResources/chica/CRAFFTMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
<form id="CRAFFTForm" method="POST" action="CRAFFTMobile.form" enctype="multipart/form-data">
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
    <div data-role="header">
        <h1>CRAFFT:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a id="vitalsButton" data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>

    <div data-role="content" >
        <strong><span id="additionalQuestions">The following are some additional questions about alcohol and drug use.</span></strong>
        <div><br/></div>
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" onclick="changePage(1)" data-role="button" data-theme="b" style="width: 150px;">Start</a>
    </div>
</div>
<c:set var="copyright" value='Copyright &#169; CHILDREN&#8216;S HOSPITAL BOSTON, 2009. ALL RIGHTS RESERVED. Reproduced with permission.'/>
<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header">
        <h1>CRAFFT:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_1" data-role="content">
        <c:set var="quest1" value='Have you ever ridden in a car driven by someone (including yourself) who was "high" or had been using alcohol or drugs?'/>
        <input id="Question_1" name="Question_1" type="hidden" value="${quest1}"/>
        <strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("Have you ever ridden in a car driven by someone (including yourself) who was high or had been using alcohol or drugs?")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1" id="QuestionEntry_1_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_1_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_1" id="QuestionEntry_1_No" value="0" data-theme="c" />
                <label for="QuestionEntry_1_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest2" value='Do you ever use alcohol or drugs to relax, feel better about yourself, or fit in?'/>
        <input id="Question_2" name="Question_2" type="hidden" value="${quest2}"/>
        <strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2" id="QuestionEntry_2_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_2_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_2" id="QuestionEntry_2_No" value="0" data-theme="c" />
                <label for="QuestionEntry_2_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest3" value='Do you ever use alcohol or drugs while you are by yourself, or alone?'/>
        <input id="Question_3" name="Question_3" type="hidden" value="${quest3}"/>
        <strong>${quest3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3" id="QuestionEntry_3_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_3" id="QuestionEntry_3_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_No">No</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_2" data-role="page" data-theme="b" type="question_page">
    <div data-role="header">
        <h1>CRAFFT:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_2" data-role="content">
        <c:set var="quest4" value='Do you ever forget things you did while using alcohol or drugs?'/>
        <input id="Question_4" name="Question_4" type="hidden" value="${quest4}"/>
        <strong>${quest4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4" id="QuestionEntry_4_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_4" id="QuestionEntry_4_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest5" value='Do your family or friends ever tell you that you should cut down on your drinking or drug use?'/>
        <input id="Question_5" name="Question_5" type="hidden" value="${quest5}"/>
        <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest5}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_5" id="QuestionEntry_5_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_5_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_5" id="QuestionEntry_5_No" value="0" data-theme="c" />
                <label for="QuestionEntry_5_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest6" value='Have you ever gotten into trouble while you were using alcohol or drugs?'/>
        <input id="Question_6" name="Question_6" type="hidden" value="${quest6}"/>
        <strong>${quest6}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest6}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_6" id="QuestionEntry_6_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_6_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_6" id="QuestionEntry_6_No" value="0" data-theme="c" />
                <label for="QuestionEntry_6_No">No</label>
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Previous</a>
        <a  href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Finish</a>
    </div>
</div>

<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header">
        <h1>CRAFFT:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_1_sp" data-role="content">
        <c:set var="quest1_2_sp" value='&#191;Ha viajado, alguna vez, en un carro o veh&#205;culo conducido por una persona (o usted mismo/a) que haya consumido alcohol, drogas o sustancias psicoactivas?'/>
        <input id="Question_1_2" name="Question_1_2" type="hidden" value="${quest1_2_sp}"/>
        <strong>${quest1_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_2" id="QuestionEntry_1_2_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_1_2_Yes">Si</label>
                <input type="radio" name="QuestionEntry_1_2" id="QuestionEntry_1_2_No" value="0" data-theme="c" />
                <label for="QuestionEntry_1_2_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest2_2_sp" value='&#191;Le han sugerido, alguna vez, sus amigos o su familia que disminuya el consumo de alcohol, drogas o sustancias psicoactivas?'/>
        <input id="Question_2_2" name="Question_2_2" type="hidden" value="${quest2_2_sp}"/>
        <strong>${quest2_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest2_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_2_2" id="QuestionEntry_2_2_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_2_2_Yes">Si</label>
                <input type="radio" name="QuestionEntry_2_2" id="QuestionEntry_2_2_No" value="0" data-theme="c" />
                <label for="QuestionEntry_2_2_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest3_2_sp" value='&#191;Ha usado, alguna vez, bebidas alcoh&#243;licas, drogas o sustancias psicoactivas para relajarse, para sentirse mejor consigo mismo o para integrarse a un grupo?'/>
        <input id="Question_3_2" name="Question_3_2" type="hidden" value="${quest3_2_sp}"/>
        <strong>${quest3_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest3_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_3_2" id="QuestionEntry_3_2_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_3_2_Yes">Si</label>
                <input type="radio" name="QuestionEntry_3_2" id="QuestionEntry_3_2_No" value="0" data-theme="c" />
                <label for="QuestionEntry_3_2_No">No</label>
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Proximo</a>
    </div>
</div>

<div id="question_page_2_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header">
        <h1>CRAFFT:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_2_sp" data-role="content">
        <c:set var="quest4_2_sp" value='&#191;Se ha metido, alguna vez, en l&#237;os o problemas al tomar alcohol, drogas o sustancias psicoactivas?'/>
        <input id="Question_4_2" name="Question_4_2" type="hidden" value="${quest4_2_sp}"/>
        <strong>${quest4_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest4_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_4_2" id="QuestionEntry_4_2_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_4_2_Yes">Si</label>
                <input type="radio" name="QuestionEntry_4_2" id="QuestionEntry_4_2_No" value="0" data-theme="c" />
                <label for="QuestionEntry_4_2_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest5_2_sp" value='&#191;Se le ha olvidado, alguna vez, lo que hizo mientras consum&#237;a alcohol, drogas o sustancias psicoactivas?'/>
        <input id="Question_5_2" name="Question_5_2" type="hidden" value="${quest5_2_sp}"/>
        <strong>${quest5_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest5_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_5_2" id="QuestionEntry_5_2_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_5_2_Yes">Si</label>
                <input type="radio" name="QuestionEntry_5_2" id="QuestionEntry_5_2_No" value="0" data-theme="c" />
                <label for="QuestionEntry_5_2_No">No</label>
            </fieldset>
        </div>
        <br/>
        <c:set var="quest6_2_sp" value='&#191;Alguna vez ha consumido, alcohol, drogas o alguna sustancia psicoactiva mientras estaba solo o sola, sin compa&#241;&#237;a?'/>
        <input id="Question_6_2" name="Question_6_2" type="hidden" value="${quest6_2_sp}"/>
        <strong>${quest6_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest6_2_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_6_2" id="QuestionEntry_6_2_Yes" value="1" data-theme="c" />
                <label for="QuestionEntry_6_2_Yes">Si</label>
                <input type="radio" name="QuestionEntry_6_2" id="QuestionEntry_6_2_No" value="0" data-theme="c" />
                <label for="QuestionEntry_6_2_No">No</label>
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
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Anterior</a>
        <a href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Acabado</a>
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
<input id="CRAFFTScore" name="CRAFFTScore" type="hidden"/>
<input id="language" name="language" type="hidden" value="${language}"/>
</form>
</body>
</html>
