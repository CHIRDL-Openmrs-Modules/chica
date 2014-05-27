<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/testFormMobile.form" />
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script>
function finish() {
	$("#loadingDialog").popup("open");
	document.getElementById('complete_form').submit();
}
</script>
</head>
<body>

<div id="finished_form" data-url="finished_form" data-role="page">
	<div data-role="header">
	   <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	</div>

	<div data-role="content">
	   <form id="complete_form" method="POST" data-ajax="false">
	       <div align="center"><p>There are no more forms to complete for ${patient.givenName}&nbsp;${patient.familyName}.  Please click "Finish" to return to the patient list.</p></div>
	       <br/>
	       <div align="center"><p>No hay más formas para completar para ${patient.givenName}&nbsp;${patient.familyName}. Haga clic en "Finish" para volver a la lista de pacientes.</p></div>
	       <br/>
	       <div align="center">
	           <a data-theme="b" data-role="button" onclick="finish()" rel="external" data-ajax="false" style="width: 100px;">Finish</a>
	       </div>
	   </form>	
	</div>
	<div id="loadingDialog" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
        <div data-role="content">
            <div style="margin: 0 auto;text-align: center;">
                Loading...
            </div>
        </div>
    </div>
</div>

</body>
</html>
