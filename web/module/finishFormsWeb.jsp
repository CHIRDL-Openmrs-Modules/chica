<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/finishFormsWeb.form" />
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/finishFormsWeb.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<title>Submission Successful</title>
</head>
<body>

<div id="content">
    <div>
       <h2>${patient.givenName} ${patient.familyName}</h2>
    </div>

    <div>
	    <div><p>Successfully signed!  There are no more forms to complete for ${patient.givenName}&nbsp;${patient.familyName}.</p></div>
	    <br/>
	    <div>Please close this window at your convenience.</div>
    </div>
</div>

</body>
</html>
