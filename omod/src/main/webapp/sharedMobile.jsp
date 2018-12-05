<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/sharedMobile.form" />
<div id="quit_passcode_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Quit</h1>
    </div>
    <div data-role="content">
        <span id="passcode_text">Please enter the passcode to quit.</span>
        <div style="margin: 0 auto;text-align: center;">
            <input type="number" masktype="password" id="quit_passcode" name="quit_passcode" placeholder="Passcode"/>
            <a id="quit_passcode_ok_button" href="#" data-role="button" data-theme="b" data-inline="true" style="width: 200px;">OK</a>
            <a id="quit_passcode_cancel_button" href="#" data-role="button" data-inline="true" data-theme="b" onclick="history.go(-1)" style="width: 200px;">Cancel</a>
        </div>
    </div>
</div>

<div id="passcode_error_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <div>
            <h3 style="text-align: center;">Passcode Error</h3>
        </div>
    </div>
    <div data-role="content">
        <span id="passcode_error_text"></span>
        <div style="margin: 0 auto;text-align: center;">
            <a id="passcode_error_ok_button" href="#" data-role="button" data-inline="true" data-theme="b" onclick="history.go(-1)" style="width: 200px;">OK</a>
        </div>
    </div>
</div>
