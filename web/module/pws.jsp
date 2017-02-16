<%@ include file="/WEB-INF/template/include.jsp" %>
 <!DOCTYPE html>
 <openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
 <html xmlns="http://www.w3.org/1999/xhtml">

 <head>
     <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/pwsEskenaziEpic.css" type="text/css" />
     <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" />
     <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.css" />
     <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/timeout-dialog.css" />
     <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css" />
     <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css" />
     <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css" />
     <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
     <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
     <script>
         var ctx = "${pageContext.request.contextPath}";
     </script>
     <script src="${pageContext.request.contextPath}/moduleResources/chica/pws.js"></script>
     <script src="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.js"></script>
     <script src="${pageCoantext.request.contextPath}/moduleResources/chica/timeout-dialog.js"></script>
     <title>CHICA Physician Encounter Form</title>
 </head>

 <body>
     <div class="page_container">
         <div class="main_container">
             <header class="main_header">
                 <div class="main_header_container">
                     <div class="top_button_container">
                         <a href="#" id="saveDraftButtonTop" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Save Draft</a>
                         <a href="#" id="submitButtonTop" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Sign</a>
                     </div>
                     <div class="main_header_title">
                         <span>CHICA</span>
                     </div>
                     <div class="mrn_container">
                         <span><c:out value="${MRN}"/></span>
                     </div>
                 </div>
             </header>
             <section class="main_section" id="main_section">
                 <%@ include file="pwsPatientInfo.jsp" %>
                 <%@ include file="pwsHandouts.jsp" %>
                 <%@ include file="pwsVitals.jsp" %>
                 <%@ include file="pwsQualityIndicators.jsp" %>
                 <%@ include file="pwsPsfResults.jsp" %>
                 <%@ include file="pwsQuestions.jsp" %>
                 <div class="bottom_button_container">
                     <section class="bottom_button_section">
                         <a href="#" id="saveDraftButtonBottom" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Save Draft</a>
                         <a href="#" id="submitButtonBottom" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Sign</a>
                     </section>
                 </div>
             </section>
         </div>
     </div>
 </body>

 </html>