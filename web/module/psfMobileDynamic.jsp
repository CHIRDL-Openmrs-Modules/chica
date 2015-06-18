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
<script src="${pageContext.request.contextPath}/moduleResources/chica/psfMobileDynamic.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.timer.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>

<style>

/*specify a height for the header so we can line-up the elements, the default is 40px*/
#additionalForms_page .ui-header {
    height : 0px;
}

/*set the content to be full-width and height except it doesn't overlap the header or footer*/
#additionalForms_page .ui-content {
    position : fixed;
    top      : 0px;
    right    : 0;
    bottom   : 0px;
    left     : 0;
    width    : 100%;
    height   : 100%;
}

/*absolutely position the footer to the bottom of the page*/
#additionalForms_page .ui-footer {
    height   : 0px;
}

#formFrame {
    position : fixed;
    top      : 0px;
    right    : 0;
    bottom   : 0px;
    left     : 0;
    width    : 100%;
    height   : 100%;
}

#loading_form_dialog .ui-dialog-contain {
    width: 200px;
    max-width: 200px;
    margin: 10% auto 15px auto;
    padding: 0;
    position: relative;
}

</style>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${formId}', '${formInstanceId}', '${encounterId}')">
<form id="psfForm" method="POST" data-ajax="false">
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
<div data-role="page" id="confirm_page" data-theme="b">
    <div data-role="header" class="single-line-header" >
        <h1>Pre-Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" data-theme="b" class="ui-btn-left" onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a id="confirmVitalsButton" data-role="button" href="#vitals_page" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="fade">Vitals</a>
    </div>

    <div data-role="content" >
        <strong><span id="parentText">Parents: Thank you for answering these questions about your child.  The answers will help your doctor provide better quality of care.  If your child is age 12 or older, he/she should answer the questions privately.  Answers are confidential, but if you prefer not to answer that is allowed.  You may want to talk about these questions with your doctor.</span></strong>
        <div><br/></div>
        <hr/>
        <strong><span id="instructions"><p>1) Please return the device back to the front desk if the patient information listed is incorrect.</p><p>2) Please confirm this form is for:<br/>Name: ${patient.givenName}&nbsp;${patient.familyName}<br/>Date of Birth: ${patient.birthdate}</p></span></strong>
        <div id="deny_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Confirm</h1>
            </div>
            <div data-role="content">
                <span>If you are sure the incorrect patient is displayed, Press 'OK' and return the device to the receptionist.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="${pageContext.request.contextPath}/module/chica/greaseBoardMobile.form" data-inline="true" data-theme="b" data-role="button" rel="external" style="width: 150px;">OK</a>
                    <a href="#confirm_page" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">Cancel</a>
                </div>
            </div>
        </div>
        
        <div id="deny_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Confirmar</h1>
            </div>
            <div data-role="content">
                <span>Si est&#225; seguro de que el paciente incorrecta aparece, pulse 'Aceptar' y devolver el dispositivo a la recepcionista.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="${pageContext.request.contextPath}/module/chica/greaseBoardMobile.form" data-inline="true" data-theme="b" data-role="button" rel="external" style="width: 150px;">Acceptar</a>
                    <a href="#confirm_page" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">Cancelar</a>
                </div>
            </div>
        </div>
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="confirmButton" href="#" data-role="button" data-theme="b" onclick="backToQuestions()" style="width: 150px;">Confirm</a>
        <a id="denyButton" href="#" data-role="button" data-theme="b" style="width: 150px;">Deny</a>
    </div>
    
</div><!-- /page one -->

<div id="server_error_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Error</h1>
    </div>
    <div data-role="content">
        <div id="errorResultDiv"></div>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#" data-role="button" data-rel="back" data-inline="true" data-theme="b" style="width: 150px;">OK</a>
        </div>
    </div>
</div>

<div id="server_error_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Error</h1>
    </div>
    <div data-role="content">
        <div id="errorResultDiv_sp"></div>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#" data-role="button" data-rel="back" data-inline="true" data-theme="b" style="width: 150px;">Acceptar</a>
        </div>
    </div>
</div>

<div id="loading_form_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="content">
        <div style="margin: 0 auto;text-align: center;">
            Loading Form...
        </div>
    </div>
</div>

<div id="finished_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Finished</h1>
    </div>
    <div data-role="content">
        <span>Thank you for filling out the form.  The MA/nurse will collect the device from you.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#vitals_page" data-role="button" data-inline="true" data-theme="b" data-transition="fade" style="width: 150px;">OK</a>
        </div>
    </div>
</div>

<div id="finished_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Acabado</h1>
    </div>
    <div data-role="content">
        <span>Gracias por rellenar el formulario. La MA/enfermera recoger&#225; el aparato de usted.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#vitals_page" data-role="button" data-inline="true" data-theme="b" data-transition="fade" style="width: 150px;">Aceptar</a>
        </div>
    </div>
</div>

<div data-role="page" id="form_completed_page" data-theme="b">
    <div data-role="header" >
        <h1>Pre-screening Form Completed</h1>
    </div>
    <div data-role="content" style="margin: 0 auto;text-align: center;" >
        <strong><span>The Pre-screening form has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="${pageContext.request.contextPath}/module/chica/greaseBoardMobile.form" data-inline="true" data-theme="b" data-role="button" rel="external" style="width: 150px;">Patient List</a>
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
        <span>Thank you for filling out the form.  The nurse will collect the device from you.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#vitals_page" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">OK</a>
        </div>
    </div>
</div>

<div id="quit_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Acabado</h1>
    </div>
    <div data-role="content">
        <span>Gracias por rellenar el formulario. La enfermera recoger&#225; el aparato de usted.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#vitals_page" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Aceptar</a>
        </div>
    </div>
</div>

<div id="load_error_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Error</h1>
    </div>
    <div data-role="content">
        <span>An error occurred loading the questions.  Click 'OK' to try again.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="" data-role="button" data-inline="true" data-theme="b" onclick="loadQuestions()" style="width: 150px;">OK</a>
        </div>
    </div>
</div>

<div id="load_error_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Error</h1>
    </div>
    <div data-role="content">
        <span>Se ha producido un error al cargar las preguntas. Haga clic en 'Aceptar' para intentarlo de nuevo.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="" data-role="button" data-inline="true" data-theme="b" onclick="loadQuestions()" style="width: 150px;">Aceptar</a>
        </div>
    </div>
</div>

<div id="save_error_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Error</h1>
    </div>
    <div data-role="content">
        <span>An error occurred saving the questions.  Click 'OK' to try again.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="" data-role="button" data-inline="true" data-theme="b" onclick="saveQuestions()" style="width: 150px;">OK</a>
        </div>
    </div>
</div>

<div id="save_error_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Error</h1>
    </div>
    <div data-role="content">
        <span>Se ha producido un error al guardar las preguntas. Haga clic en 'Aceptar' para intentarlo de nuevo.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="" data-role="button" data-inline="true" data-theme="b" onclick="saveQuestions()" style="width: 150px;">Aceptar</a>
        </div>
    </div>
</div>

<div id="forms_error_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Error</h1>
    </div>
    <div data-role="content">
        <span>An error occurred loading the next form.  Click 'OK' to try again.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="" data-role="button" data-inline="true" data-theme="b" onclick="attemptLoadForms()" style="width: 150px;">OK</a>
        </div>
    </div>
</div>

<div id="forms_error_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Error</h1>
    </div>
    <div data-role="content">
        <span>Se ha producido un error al cargar la siguiente forma. Haga clic en 'Aceptar' para intentarlo de nuevo.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="" data-role="button" data-inline="true" data-theme="b" onclick="attemptLoadForms()" style="width: 150px;">Aceptar</a>
        </div>
    </div>
</div>

<div id="question_page" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Pre-Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" data-theme="b" class="ui-btn-left" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="openVitalsConfirm()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div data-role="content">
        <div id="content_1"></div>
        <div id="quit_to_vitals_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Vitals</h1>
            </div>
            <div data-role="content">
                <span>All answers on this page will be submitted before proceeding to the Vitals page.  Are you sure you want to continue?</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a data-inline="true" data-rel="back" data-theme="b" data-role="button" onclick="saveSendToVitals()" style="width: 150px;">Yes</a>
                    <a data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">No</a>
                </div>
            </div>
        </div>
        <div id="not_finished_final_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Not Completed</h1>
            </div>
            <div data-role="content">
                <span>There are unanswered questions on this page.  Are you sure you want to continue to the next page?</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href=""  onclick="saveDynamicQuestions(true);" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">Yes</a>
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
                    <a href="" onclick="saveDynamicQuestions(true)" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptSaveQuestions()" style="width: 150px;">Next</a>
        <!-- <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a> -->
    </div>
</div>
<div id="question_page_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Pre-Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" data-theme="b" class="ui-btn-left" onclick="setLanguageFromForm('${newFirstName}&nbsp;${NewLastName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="openVitalsConfirmSpanish()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div data-role="content">
        <div id="content_1_sp"></div>
        <div id="quit_to_vitals_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Vitales</h1>
            </div>
            <div data-role="content">
                <span>Todas las respuestas en esta p&#225;gina se presentar&#225;n antes de proceder a la p&#225;gina vitales.  &#191;Est&#225; seguro que desea continuar?</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a data-inline="true" data-rel="back" data-theme="b" data-role="button" onclick="saveSendToVitals()" style="width: 150px;">Si</a>
                    <a data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">No</a>
                </div>
            </div>
        </div>
        <div id="not_finished_final_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>No Completado</h1>
            </div>
            <div data-role="content">
                <span>Hay preguntas sin respuesta en esta p&#225;gina. &#191;Est&#225; seguro de que desea continuar a la siguiente p&#225;gina?</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="" onclick="saveDynamicQuestions(true)" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">Si</a>
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
                    <a href="" onclick="saveDynamicQuestions(true)" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-theme="b" onclick="attemptSaveQuestions()" style="width: 150px;">Proximo</a>
        <!-- <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Dejar de</a> -->
    </div>
</div>

<div id="additionalForms_page" data-role="page" data-theme="b">
    <div data-role="header"></div>
    <div id="content_frame" data-role="content">
        <iframe id="formFrame" src="" seamless></iframe>
    </div>
    <div data-role="footer"></div>
</div>

<div id="blockUIMessage" data-role="page">

</div>

<div id="vitals_page" data-role="page" data-theme="b">
    <div id="vitals_header" data-role="header" >
        <a id="backQuestionsButton" data-role="button" data-icon="back" data-theme="b" data-rel="back">Questions</a>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <h1>(***Medical Staff Only***)</h1>
    </div>
    <div id="content_vitals" data-role="content">
        <a id='lnkVitalsPasscode' href="#vitals_passcode_dialog" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <a id='lnkPasscodeError' href="#passcodeError" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <div id="vitals_passcode_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="c">
            <div data-role="header" data-theme="b">
                <h1>Passcode</h1>
            </div>
            <div data-role="content">
                <span>Please enter the passcode to access the vitals page.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <input type="number" masktype="password" id="vitals_passcode" name="vitals_passcode" placeholder="Passcode"/>
                    <a id="backQuestionsButton" data-role="button" data-icon="back" data-theme="b" onclick="history.go(-2)" data-inline="true" style="width: 200px;">Questions</a>
                    <a href="#" id="goButton" onclick="checkPasscode()" data-role="button" data-inline="true" data-theme="b" style="width: 200px;">Go</a>
                </div>
            </div>
        </div>
        <div id="passcodeError" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="c">
            <div data-role="header" data-theme="b">
                <div>
                    <h3 style="text-align: center;">Passcode Error</h3>
                </div>
            </div>
            <div data-role="content">
                <div id="passcodeErrorResultDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="#vitals_passcode_dialog" data-rel="popup" data-position-to="window" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <div class="ui-grid-a" style="padding-bottom: 0px;">
            <div class="ui-block-a" style="width: 42%">
              <div class="ui-grid-c">
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <c:choose>
                          <c:when test="${Height_HL != null }">
                            <strong><span style="line-height: 50px;"><c:out value="${Height_HL}"/>&nbsp;Height:</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">Height:</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;">
                      <c:choose>
                        <c:when test="${HeightP == '' && HeightS == ''}">
                            <input type="number" id="height" name="height" step="any" value=""/>
                        </c:when>
                        <c:otherwise>
                            <input type="number" id="height" name="height" step="any" value="${HeightP == '' ? 0 : HeightP}.${HeightS == '' ? 0 : HeightS}"/>
                        </c:otherwise>
                      </c:choose>
                      </span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">${HeightSUnits}</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <c:choose>
                          <c:when test="${Weight_HL != null }">
                            <strong><span style="line-height: 50px;"><c:out value="${Weight_HL}"/>&nbsp;Weight:</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">Weight:</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;">
                      <c:choose>
                        <c:when test="${WeightP == '' && WeightS == ''}">
                            <input type="number" id="weight" name="weight" step="any" value=""/>
                        </c:when>
                        <c:otherwise>
                            <input type="number" id="weight" name="weight" step="any" value="${WeightP == '' ? 0 : WeightP}.${WeightS == '' ? 0 : WeightS}"/>
                        </c:otherwise>
                      </c:choose>
                      </span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <c:choose>
                          <c:when test="${WeightSUnits == 'oz.' }">
                            <strong><span style="line-height: 50px;">lb.oz</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">${WeightSUnits}</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <c:choose>
                          <c:when test="${HC_HL != null }">
                            <strong><span style="line-height: 50px;"><c:out value="${HC_HL}"/>&nbsp;HC:</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">HC:</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;">
                      <c:choose>
                        <c:when test="${HCP == '' && HCS == ''}">
                            <input type="number" id="hc" name="hc" step="any" value=""/>
                        </c:when>
                        <c:otherwise>
                            <input type="number" id="hc" name="hc" step="any" value="${HCP == '' ? 0 : HCP}.${HCS == '' ? 0 : HCS}"/>
                        </c:otherwise>
                      </c:choose>
                      </span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">cm.</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <c:choose>
                          <c:when test="${BP_HL != null }">
                            <strong><span style="line-height: 50px;"><c:out value="${BP_HL}"/>&nbsp;BP:</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">BP:</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="BPS" name="BPS" value="${BPS}"/></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;padding-left:10px;width: 15px;">
                      <span style="line-height: 50px;"><c:out value="/"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;padding-left:10px;">
                      <span style="line-height: 50px;"><input type="number" id="BPD" name="BPD" value="${BPD}"/></span>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Temp:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;">
                      <c:choose>
                        <c:when test="${TempP == '' && TempS == ''}">
                            <input type="number" id="temp" name="temp" step="any" value=""/>
                        </c:when>
                        <c:otherwise>
                            <input type="number" id="temp" name="temp" step="any" value="${TempP == '' ? 0 : TempP}.${TempS == '' ? 0 : TempS}"/>
                        </c:otherwise>
                      </c:choose>
                      </span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">deg. F</span></strong>
                  </div>
                  <div class="ui-block-a" style="text-align: center;margin-bottom: 10px;margin-top: 10px;width: 100%;">
                      <fieldset data-role="controlgroup" data-type="horizontal" style="margin: auto;">
                            <input type="radio" name="TemperatureType" id="Temperature_Method_Oral" value="Oral Temp Type" data-theme="c" ${TemperatureType == 'Oral Temp Type' ? 'checked' : ''}/>
                            <label for="Temperature_Method_Oral">Oral</label>
                            <input type="radio" name="TemperatureType" id="Temperature_Method_Rectal" value="Rectal Temp Type" data-theme="c" ${TemperatureType == 'Rectal Temp Type' ? 'checked' : ''}/>
                            <label for="Temperature_Method_Rectal">Rectal</label>
                            <input type="radio" name="TemperatureType" id="Temperature_Method_Axillary" value="Axillary Temp Type" data-theme="c" ${TemperatureType == 'Axillary Temp Type' ? 'checked' : ''}/>
                            <label for="Temperature_Method_Axillary">Axillary</label>
                        </fieldset>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Pulse:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="Pulse" name="Pulse" value="${Pulse}"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">/min.</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">RR:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;p;">
                      <span style="line-height: 50px;"><input type="number" id="RR" name="RR" value="${RR}"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;">
                      <span style="line-height: 50px;"></span>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Pulse Ox:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;margin-bottom: 8px;">
                      <span style="line-height: 50px;"><input type="number" id="PulseOx" name="PulseOx" value="${PulseOx}"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">%</span></strong>
                  </div>
              </div>
            </div>
            <div class="ui-block-b" style="width: 58%">
               <div class="ui-grid-b">
                  <div class="ui-block-b" style="height: 25px;text-align: center;width: 100%;">
                      <strong><span>Uncooperative/Unable to Screen:</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;margin-bottom: 20px;">
                      <span><input type="checkbox" id="NoVision" name="NoVision" value="Y" style="vertical-align: top; margin: 0px;" ${NoVision == "Y" ? 'checked' : ''}/><label for="NoVision">Vision</label></span>
                  </div>
                  <div class="ui-block-b" style="height: 50px;margin-bottom: 20px;">
                      <span><input type="checkbox" id="NoHearing" name="NoHearing" value="Y" style="vertical-align: top; margin: 0px;" ${NoHearing == "Y" ? 'checked' : ''}/><label for="NoHearing">Hearing</label></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;margin-bottom: 20px;">
                      <span><input type="checkbox" id="NoBP" name="NoBP" value="Y" style="vertical-align: top; margin: 0px;" ${NoBP == "Y" ? 'checked' : ''}/><label for="NoBP">BP</label></span>
                  </div>
                  <div class="ui-block-a" style="text-align: center;width: 5%"></div>
                  <div class="ui-block-b" style="height: 50px;text-align: center;width: 85%;">
                      <div class="ui-grid-a" style="text-align: center;">
                        <div class="ui-block-a" style="height: 50px;text-align: right;width: 50%;">
                            <c:choose>
                              <c:when test="${VisionL_HL != null }">
                                <strong><span style="line-height: 50px;"><c:out value="${VisionL_HL}"/>&nbsp;Vision Left: 20/</span></strong>
                              </c:when>
                              <c:otherwise>
                                <strong><span style="line-height: 50px;">Vision Left: 20/</span></strong>
                              </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="ui-block-b" style="height: 50px;width: 50%;padding-left: 10px;margin-bottom: 10px;">
                            <span><input type="number" id="VisionL" name="VisionL" value="${VisionL}"/></span>
                        </div>
                      </div>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;width: 5%"></div>
                  <div class="ui-block-a" style="text-align: center;width: 5%"></div>
                  <div class="ui-block-b" style="height: 50px;text-align: center;width: 85%;">
                      <div class="ui-grid-a" style="text-align: center;">
                        <div class="ui-block-a" style="height: 50px;text-align: right;width: 50%;">
                            <c:choose>
                              <c:when test="${VisionR_HL != null }">
                                <strong><span style="line-height: 50px;"><c:out value="${VisionR_HL}"/>&nbsp;Vision Right: 20/</span></strong>
                              </c:when>
                              <c:otherwise>
                                <strong><span style="line-height: 50px;">Vision Right: 20/</span></strong>
                              </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="ui-block-b" style="height: 50px;width: 50%;padding-left: 10px;margin-bottom: 10px;">
                            <span><input type="number" id="VisionR" name="VisionR" value="${VisionR}"/></span>
                        </div>
                      </div>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;width: 10%"></div>
                  <div class="ui-block-a" style="text-align: center;height: 50px;margin-bottom: 20px;width: 20%"></div>
                  <div class="ui-block-b" style="text-align: center;height: 50px;margin-bottom: 20px;width: 60%">
                      <span><input type="checkbox" id="Vision_Corrected" name="Vision_Corrected" value="Y" style="vertical-align: top; margin: 0px;" ${Vision_Corrected == "Y" ? 'checked' : ''}/><label for="Vision_Corrected">Vision Corrected?</label></span>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;margin-bottom: 20px;width: 20%"></div>
                  <div class="ui-block-a" style="height: 60px;width: 100%;padding-top:10px;">
                      <div class="ui-grid-a">
                          <div class="ui-block-a" style="text-align: right;width: 50%;padding-right:10px;">
                           <c:choose>
                              <c:when test="${HearL_HL != null }">
                                <strong><span style="line-height: 50px;"><c:out value="${HearL_HL}"/>&nbsp;Left Ear @ 25db:</span></strong>
                              </c:when>
                              <c:otherwise>
                                <strong><span style="line-height: 50px;">Left Ear @ 25db:</span></strong>
                              </c:otherwise>
                           </c:choose>
                          </div>
                          <div class="ui-block-b" style="width: 50%;height: 50px;display: table">
                              <div data-role="fieldcontain" style="display: table-cell;">
                                <fieldset data-role="controlgroup" data-type="horizontal">
                                    <input type="radio" name="HearL" id="HearL_pass" value="P" data-theme="c" ${HearL == "P" ? 'checked' : '' }/>
                                    <label for="HearL_pass">P</label>
                                    <input type="radio" name="HearL" id="HearL_fail" value="F" data-theme="c" ${HearL == "F" ? 'checked' : '' }/>
                                    <label for="HearL_fail">F</label>
                                </fieldset>
                              </div>
                          </div>
                      </div>
                  </div>
                  <div class="ui-block-a" style="height: 60px;width: 100%;padding-top:10px;">
                      <div class="ui-grid-a">
                          <div class="ui-block-a" style="text-align: right;width: 50%;padding-right:10px;">
                           <c:choose>
                              <c:when test="${HearR_HL != null }">
                                <strong><span style="line-height: 50px;"><c:out value="${HearR_HL}"/>&nbsp;Right Ear @ 25db:</span></strong>
                              </c:when>
                              <c:otherwise>
                                <strong><span style="line-height: 50px;">Right Ear @ 25db:</span></strong>
                              </c:otherwise>
                           </c:choose>
                          </div>
                          <div class="ui-block-b" style="width: 50%;height: 50px;display: table">
                              <div data-role="fieldcontain" style="display: table-cell;">
                                <fieldset data-role="controlgroup" data-type="horizontal">
                                    <input type="radio" name="HearR" id="HearR_pass" value="P" data-theme="c" ${HearR == "P" ? 'checked' : '' }/>
                                    <label for="HearR_pass">P</label>
                                    <input type="radio" name="HearR" id="HearR_fail" value="F" data-theme="c" ${HearR == "F" ? 'checked' : '' }/>
                                    <label for="HearR_fail">F</label>
                                </fieldset>
                              </div>
                          </div>
                      </div>
                  </div>
                  <div class="ui-block-a" style="text-align: center;height: 25px;margin-bottom: 0px;width: 14%"></div>
                  <div class="ui-block-b" style="text-align: center;height: 25px;margin-bottom: 0px;width: 86%"></div>
                  <div class="ui-block-a" style="text-align: center;height: 60px;margin-bottom: 0px;width: 14%"></div>
                  <div class="ui-block-b" style="text-align: center;height: 60px;margin-bottom: 0px;width: 86%;">
                      <span><input type="checkbox" id="Handout_Reviewed_by_MA" name="Handout_Reviewed_by_MA" value="diet and exercise" style="vertical-align: top; margin: 0px;" ${Handout_Reviewed_by_MA == "diet and exercise" ? 'checked' : '' }/><label for="Handout_Reviewed_by_MA">Diet and Exercise Handout Given<label></span>
                  </div>
               </div>
            </div>
        </div>
        <div class="ui-grid-a">
            <div class="ui-block-a">
                <input type="checkbox" id="SickVisit" name="SickVisit" value="Y" ${SickVisit == "Y" ? 'checked' : '' }/><label for="SickVisit">Sick Visit</label>
            </div>
            <div class="ui-block-b">
                <input type="checkbox" id="RefuseToComplete" name="RefuseToComplete" value="Y"/><label for="RefuseToComplete">Patient refused to complete form</label>
            </div>
            <div class="ui-block-a">
                <input type="checkbox" id="TwoIDsChecked" name="TwoIDsChecked" value="Y"/><label for="TwoIDsChecked">Two IDs checked</label>
            </div>
            <div class="ui-block-b">
                <input type="checkbox" id="LeftWithoutTreatment" name="LeftWithoutTreatment" value="Y"/><label for="LeftWithoutTreatment">Patient left without treatment</label>
            </div>
        </div>
        <div id="validation_error_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <div>
                    <h3 style="text-align: center;">Validation Error</h3>
                </div>
            </div>
            <div data-role="content">
                <span id="validationError"></span>
                <div style="margin: 0 auto;text-align: center;">
                    <a id="validationOkButton" href="#" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <div id="invalidLogin" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Invalid Login</h1>
            </div>
            <div data-role="content">
                <div id="loginResultDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a data-inline="true" onclick="showLoginDialog()" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <a id='lnkSubmitError' href="#submitErrorDialog" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <div id="submitErrorDialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Error</h1>
            </div>
            <div data-role="content">
                <div id="submitErrorDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a id="submitErrorButton" data-rel="back" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <div id="confirm_submit_dialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Confirm</h1>
            </div>
            <div data-role="content">
                <div>If all patient questions and vitals information are complete, please click 'Submit' to permanently submit the form.</div>
                <div style="margin: 0 auto;text-align: center;">
                    <a id="confirm_submit_submit_button" data-role="button" data-theme="b" data-inline="true" onclick="completeForm()" style="width: 150px;">Submit</a>
                    <a id="confirm_submit_cancel_button" data-rel="back" data-role="button" data-theme="b" data-inline="true" style="width: 150px;">Cancel</a>
                </div>
            </div>
        </div>
        <a id='lnkLoadingDialog' href="#loadingDialog" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <div id="loadingDialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
            <div data-role="content">
                <div style="margin: 0 auto;text-align: center;">
                    Saving...
                </div>
            </div>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a data-theme="b" data-role="button" onclick="finishVitals()" rel="external" data-ajax="false" style="width: 150px;">Submit</a>
    </div>
</div>
<input id="HeightP" name="HeightP" type="hidden"/>
<input id="HeightS" name="HeightS" type="hidden"/>
<input id="WeightP" name="WeightP" type="hidden"/>
<input id="WeightS" name="WeightS" type="hidden"/>
<input id="TempP" name="TempP" type="hidden"/>
<input id="TempS" name="TempS" type="hidden"/>
<input id="HCP" name="HCP" type="hidden"/>
<input id="HCS" name="HCS" type="hidden"/>
<input id="patientId" name="patientId" type="hidden" value="${patient.patientId}"/>
<input id="encounterId" name="encounterId" type="hidden" value="${encounterId}"/>
<input id="sessionId" name="sessionId" type="hidden" value="${sessionId}"/>
<input id="formId" name="formId" type="hidden" value="${formId}"/>
<input id="formInstanceId" name="formInstanceId" type="hidden" value="${formInstanceId}"/>
<input id="locationId" name="locationId" type="hidden" value="${locationId}"/>
<input id="locationTagId" name="locationTagId" type="hidden" value="${locationTagId}"/>
<input id="maxElements" name="maxElements" type="hidden" value="5"/>
<input id="language" name="language" type="hidden" value="${language}"/>
<input id="formInstance" name="formInstance" type="hidden" value="${formInstance}"/>

<!-- DWE CHICA-430 Add hidden input to allow these parameters to be included when the checkboxes are unchecked -->
<input id="Vision_Corrected" name="Vision_Corrected" type="hidden" value="N"/>
<input id="SickVisit" name="SickVisit" type="hidden" value="N"/>
<input id="NoBP" name="NoBP" type="hidden" value="N"/>
<input id="NoHearing" name="NoHearing" type="hidden" value="N"/>
<input id="NoVision" name="NoVision" type="hidden" value="N"/>
<input id="Handout_Reviewed_by_MA" name="Handout_Reviewed_by_MA" type="hidden" value="N"/>
</form>
</body>
</html>
