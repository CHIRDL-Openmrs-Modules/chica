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

<div id="loading_form_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="content">
        <div style="margin: 0 auto;text-align: center;">
            Cargando el formulario...
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