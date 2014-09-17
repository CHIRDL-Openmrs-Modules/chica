<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/formAuthentication.form" />
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/finishFormsWeb.css">
</head>
<body>

<div id="content">
    <div>
       <h2>${patient.givenName} ${patient.familyName}</h2>
    </div>

    <div>
        <div><p>Submission successful!  There are no more forms to complete for ${patient.givenName}&nbsp;${patient.familyName}.  Please click "Close" to close the window.</p></div>
        <br/>
        <div>
            <input type="button" onclick="window.close()" value="Close" style="width: 100px;"/>
        </div>
    </div>
    <form id="authenticationForm" method="POST">
        <input id="formName" name="formName" type="hidden" value="${formName}"/>
        <input id="formPage" name="formPage" type="hidden" value="${formPage}"/>
        <input id="mrn" name="mrn" type="hidden" value="${mrn}"/>
    </form>
</div>

</body>
</html>
