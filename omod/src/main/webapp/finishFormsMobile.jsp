<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/finishFormsMobile.form" />
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" /> 
<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/finishFormsMobile.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.timer.js"></script>
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
           <div align="center">
               <a data-theme="b" data-role="button" onclick="finish()" rel="external" data-ajax="false" style="width: 100px;">Finish</a>
           </div>
       </form>  
    </div>
           
    <%@ include file="finishFormsDialogs.jsp" %>
</div>
<input type="hidden" name="userQuitForm" id="userQuitForm" value="${userQuitForm}" />
</body>
</html>
