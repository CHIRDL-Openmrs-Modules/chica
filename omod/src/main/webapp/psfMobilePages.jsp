<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/psfMobilePages.form" />
<div data-role="page" id="confirm_page" data-theme="b">
    <div data-role="header" class="single-line-header" >
        <h1>Pre-Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" data-theme="b" class="ui-btn-left" onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
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
        
        <%@ include file="confidentialityDialog.jsp" %>
        
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="confirmButton" href="#" data-role="button" data-theme="b" onclick="displayConfidentialityDialog();" style="width: 150px;">Confirm</a>
        <a id="denyButton" href="#" data-role="button" data-theme="b" style="width: 150px;">Deny</a>
    </div>
    
</div><!-- /page one -->
<div id="question_page" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Pre-Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" data-theme="b" class="ui-btn-left" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="confirmQuitForm()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Quit</a>
    </div>
    <div data-role="content">
        <div id="content_1"></div>
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
        <a data-role="button" onclick="confirmQuitForm()" data-theme="b" class="ui-btn-right" data-icon="forward" data-transition="pop">Dejar</a>
    </div>
    <div data-role="content">
        <div id="content_1_sp"></div>
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
<input type="hidden" name="ageInYears" id="ageInYears" value="${AgeInYears}" />
<input type="hidden" name="displayConfidentialityNotice" id="displayConfidentialityNotice" value="${DisplayConfidentialityNotice}" />
<input type="hidden" name="patientFirstName" id="patientFirstName" value="${patient.givenName}" />
<input type="hidden" name="userQuitForm" id="userQuitForm" value="${userQuitForm}" />