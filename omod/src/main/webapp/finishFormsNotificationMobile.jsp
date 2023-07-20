<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/finishFormsNotificationMobile.form" />
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" /> 
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
           <c:choose>
           <c:when test="${empty (notifications)}">
               <div align="left"><p>There are no more items to complete for ${patient.givenName}&nbsp;${patient.familyName}.  Please click the "Finish" button.</p></div>
               <br/>
           </c:when>
           <c:otherwise>
               <div align="left"><p>There are some additional items to complete for ${patient.givenName}&nbsp;${patient.familyName}.</p></div>
               <br/>
               <ol type="1">
                   <c:forEach items="${notifications}" var="notification">
                       <li><div align="left" style="width: 100%;">${notification.statement}</div></li>
                       <ol type="a">
                           <c:forEach items="${notification.subStatements}" var="subStatement" varStatus="status">
                                <li><div style="margin-left: 20px; width: 100%;">${subStatement}</div></li>
                           </c:forEach>
                       </ol>
                       <br/>
                   <br/>
                   </c:forEach>
               </ol>
           </c:otherwise>
           </c:choose>
           <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
                <a data-theme="b" data-role="button" onclick="finish()" rel="external" data-ajax="false" style="width: 150px;">Finish</a>
            </div>
       </form>  
    </div>
    
    <%@ include file="finishFormsDialogs.jsp" %>
</div>
<input type="hidden" name="userQuitForm" id="userQuitForm" value="${userQuitForm}" />
</body>
</html>
