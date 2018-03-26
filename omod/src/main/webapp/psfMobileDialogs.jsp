<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/psfMobileDialogs.form" />

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
            <a href="#" data-role="button" data-rel="back" data-inline="true" data-theme="b" style="width: 150px;">Aceptar</a>
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
<a id='lnkSubmitError' href="#submitErrorDialog" data-rel="dialog" data-transition="pop" data-position-to="window" style='display:none;'></a>
<div id="submitErrorDialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c" >
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