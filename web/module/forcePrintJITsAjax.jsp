<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="${pageContext.request.contextPath}/moduleResources/chica/pws.css" type="text/css" rel="stylesheet" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITsAjax.js"></script>
        <title>CHICA Physician Encounter Form</title>
    </head>

    <body>
        <form>
            <div id="formsLoading">
                <span id="formsLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
            </div>
            <div id="formContainer">
                <fieldset>
                    <label for="forms">Select a form</label>
                    <select name="forms" id="forms"></select>
                </fieldset>
            </div>
            <input type="hidden" value="${patientId}" name="patientId" />
            <input type="hidden" value="${sessionId}" name="sessionId" />        
        </form>
    </body>
</html> 