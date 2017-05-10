<%@ include file="/WEB-INF/template/include.jsp" %>
    <!DOCTYPE html>
    <openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
    <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/pwsIUHCerner.css" type="text/css" />
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
        <script src="${pageContext.request.contextPath}/moduleResources/chica/timeout-dialog.js"></script>
        <title>CHICA Physician Encounter Form</title>
    </head>

    <body>
        <div class="page_container">
            <div class="main_container">
                <form id="pwsForm" name="pwsForm" action="pws.form" method="post">
                    <header class="main_header">
                        <div class="main_header_container">
                            <div class="mrn_container">
                                <span><c:out value="${MRN}"/></span>
                            </div>
                            <div class="main_header_title">
                                <span>CHICA</span>
                            </div>
                            <div class="top_button_container">
                                <a href="#" id="saveDraftButtonTop" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Save Draft</a>
                                <a href="#" id="submitButtonTop" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Sign</a>
                            </div>
                        </div>
                    </header>
                    <section class="main_section" id="main_section">
                        <%@ include file="pwsPatientInfo.jsp" %>
                        <%@ include file="pwsHandouts.jsp" %>
                        <%@ include file="pwsVitals.jsp" %>
                        <%@ include file="pwsPsfResults.jsp" %>
                        <%@ include file="pwsQuestions.jsp" %>
                        <div class="bottom_button_container">
                            <section class="bottom_button_section">
                                <a href="#" id="saveDraftButtonBottom" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Save Draft</a>
                                <a href="#" id="submitButtonBottom" class="icon-button mediumButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Sign</a>
                            </section>
                        </div>
                    </section>
                    <%@ include file="pwsDialogs.jsp" %>
                    <input type=hidden id="Choice1" name="Choice1" />
                    <input type=hidden id="Choice2" name="Choice2" />
                    <input type=hidden id="Choice3" name="Choice3" />
                    <input type=hidden id="Choice4" name="Choice4" />
                    <input type=hidden id="Choice5" name="Choice5" />
                    <input type=hidden id="Choice6" name="Choice6" />
                    <input id="patientId" name="patientId" type="hidden" value="${patient.patientId}" />
                    <input id="encounterId" name="encounterId" type="hidden" value="${encounterId}" />
                    <input id="sessionId" name="sessionId" type="hidden" value="${sessionId}" />
                    <input id="formId" name="formId" type="hidden" value="${formId}" />
                    <input id="formInstanceId" name="formInstanceId" type="hidden" value="${formInstanceId}" />
                    <input id="locationId" name="locationId" type="hidden" value="${locationId}" />
                    <input id="locationTagId" name="locationTagId" type="hidden" value="${locationTagId}" />
                    <input id="maxElements" name="maxElements" type="hidden" value="5" />
                    <input id="language" name="language" type="hidden" value="${language}" />
                    <input id="formInstance" name="formInstance" type="hidden" value="${formInstance}" />
                    <input id="providerId" name="providerId" type="hidden" value="${providerId}" />
                    <input id="patientNameForcePrint" name="patientNameForcePrint" type="hidden" value="${PatientName}" />
                    <input id="sessionTimeout" name="sessionTimeout" type="hidden" value="${pageContext.session.maxInactiveInterval}" />
                    <input id="sessionTimeoutWarning" name="sessionTimeoutWarning" type="hidden" value="${sessionTimeoutWarning}" />
                </form>
            </div>
        </div>
    </body>

    </html>