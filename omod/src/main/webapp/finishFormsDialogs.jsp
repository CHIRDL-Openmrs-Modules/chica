<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/finishFormsDialogs.form" />
<div id="finished_dialog" class="extended-header" data-role="popup" data-history="false" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <c:choose>
            <c:when test="${language == 'english'}">
                <h1>Finished</h1>
            </c:when>
            <c:otherwise>
                <h1>Terminado</h1>
            </c:otherwise>
        </c:choose>
    </div>
    <div data-role="content">
        <c:choose>
            <c:when test="${language == 'english'}">
                <span>Thank you for filling out the form.  The MA/nurse will collect the device from you.</span>
            </c:when>
            <c:otherwise>
                <span>Gracias por rellenar el formulario. La MA/enfermera recoger&#225; el aparato de usted.</span>
            </c:otherwise>
        </c:choose>
        <div style="margin: 0 auto;text-align: center;">
            <a href="" data-role="button" data-inline="true" data-theme="b" data-transition="fade" style="width: 150px;" onclick="closeFinishDialog()">OK</a>
        </div>
    </div>
</div>

<div id="loadingDialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
    <div data-role="content">
        <div style="margin: 0 auto;text-align: center;">
            Loading...
        </div>
    </div>
</div>