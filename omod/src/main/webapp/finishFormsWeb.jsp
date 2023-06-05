<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/finishFormsWeb.form" />
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/finishFormsWeb.css">

<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/jquery-ui.min.css" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/jquery-ui.structure.min.css" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/jquery-ui.theme.min.css" />

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
