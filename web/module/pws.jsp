<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/pws.css" type="text/css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/pws.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.js"></script>
        <title>CHICA Physician Encounter Form</title>
    </head>

    <body>
    	<div id="formContainer">
            <form id="pwsForm" name="pwsForm" action="pws.form" method="post">
                <div id="titleContainer">
                    <div id="submitFormTop">
                        <a href="#" id="submitButtonTop" class="icon-button ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Submit</a>
                    </div>
                    <div id="title">
                        <h3>CHICA Physician Encounter Form</h3>
                    </div>
                    <div id="mrn">
                        <h3><c:out value="${MRN}"/></h3>
                    </div>
                </div>
                <div id="infoLeft">
                    <b>Patient:</b> <c:out value="${PatientName}"/><br/>
                    <b>DOB:</b> <c:out value="${DOB}"/> <b>Age:</b> <c:out value="${Age}"/><br/>
                    <b>Doctor:</b> <c:out value="${Doctor}"/>
                </div>
                <div id="infoRight">
                    <b>MRN:</b> <c:out value="${MRN}"/><br/>
                    <b>Date:</b> <c:out value="${VisitDate}"/><br/>
                <b>Time:</b> <c:out value="${VisitTime}"/></div>
                
                <%@ include file="pwsVitals.jsp" %>
                
                <%@ include file="pwsPhysicalExam.jsp" %>
                               
                <div id="buttons">
                    <div class="buttonsData">
                        <a href="#" id="formPrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Recommended Handouts</a>
                    </div>
                    <div class="buttonsData">
                        <a href="#" id="forcePrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Other Handouts</a>
                    </div>
                    <!-- <c:if test="${not empty diag1}">
	                	<div class="buttonsData">
	                        <a href="#" id="problemButton" class="icon-button ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Problem List</a>
	                    </div>
                    </c:if>
                    <c:if test="${not empty Med1_A || not empty Med1_B || not empty Med2_A || not empty Med2_B || 
                                  not empty Med3_A || not empty Med3_B || not empty Med4_A || not empty Med4_B || 
                                  not empty Med5_A || not empty Med5_B || not empty Med6_A || not empty Med6_B}">
	                    <div class="buttonsData">
	                        <input id="medButton" type="button" value="Medications"/>
	                    </div>
                    </c:if> -->
                </div>
                
                <%@ include file="pwsQuestions.jsp" %>
                                
                <div id="submitContainer">
                    <a href="#" id="submitButtonBottom" class="icon-button ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Submit</a>
                </div>
                
                <%@ include file="pwsDialogs.jsp" %>
                
                <div id="problemDialog" title="Problem List" class="ui-dialog-titlebar ui-widget-header">
                    <table id="problemTable">
                        <tr>
                            <td class="padding5"><c:out value="${diag1}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag2}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag3}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag4}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag5}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag6}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag7}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag8}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag9}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag10}"/></td>
                        </tr>
                    </table>
                </div>
                <div id="medDialog" title="Medication List" class="ui-dialog-titlebar ui-widget-header">
                    <table id="medTable">
                        <c:if test="${not empty Med1_A || not empty Med1_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med1_A}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med1_B}"/></td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med2_A || not empty Med2_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med2_A}}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med2_B}"/></td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med3_A || not empty Med3_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med3_A}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med3_B}"/></td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med4_A || not empty Med4_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med4_A}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med4_B}"/></td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med5_A || not empty Med5_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med5_A}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med5_B}"/></td>
	                        </tr>
                        </c:if>
                        <c:if test="${not empty Med6_A || not empty Med6_B }">
	                        <tr class="trAlignLeft">
	                            <td class="tdBorderTop"><c:out value="${Med6_A}"/></td>
	                        </tr>
	                        <tr class="trAlignLeft">
	                            <td><c:out value="${Med6_B}"/></td>
	                        </tr>
                        </c:if>
                    </table>
                </div>
                <div id="confirmSubmitDialog" title="Confirm" class="ui-overlay">
                    <div id="confirmText">
                        <span>Click OK to finalize the form.</span>
                    </div>
                </div>
                <div id="submitWaitDialog" class="noTitle">
                    <div id="submitWaitText">
                        <span>Submitting...</span>
                    </div>
                </div>
                <div id="formTabDialog" title="CHICA Recommended Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
                    <div id="formTabDialogContainer" style="overflow-x: hidden;overflow-y: hidden;" >
	                    <div id="formLoading">
	                       <span id="formLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
	                    </div>
	                    <div id="formServerError">
	                        <div id="formServerErrorText" class="ui-state-error"></div>
	                        <br/><br/><a href="#" id="retryButton" class="icon-button ui-state-default ui-corner-all">Retry</a>
	                    </div>
	                    <div id="noForms">
	                        There are no recommended handouts for ${PatientName}.
	                    </div>                   
	                    <div id="formTabContainer">                       	     
	                        <div id="tabs"></div>
	                    </div>
	                </div>
                </div>
                <div id="forcePrintDialog" title="Other CHICA Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
                    <div class="pws-force-print-content">
			             <div class="force-print-forms-loading">
			                 <span id="formsLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
			             </div>
			             <div class="force-print-forms-server-error">
			                 <div class="force-print-forms-server-error-text ui-state-error"></div>
			                 <br/><br/><a href="#" class="force-print-retry-button force-print-icon-button ui-state-default ui-corner-all">Retry</a>
			             </div>
			             <div class="force-print-forms-container">
			                 <div class="force-print-patient-name">Please choose a form for ${PatientName}.</div>
			                 <fieldset class="force-print-fieldset">
			                     <select class="force-print-forms"></select>
			                 </fieldset>
			             </div>
			             <div class="force-print-form-container">
			                <object class="force-print-form-object" data="" onreadystatechange="return forcePrint_formLoaded();" onload="forcePrint_formLoaded();">
			                   <span class="force-print-black-text">It appears your Web browser is not configured to display PDF files. 
			                   <a style="color:blue" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
			                </object>
			             </div>
			             <div class="force-print-form-loading">
			                <span><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Creating form...</span>
			             </div>
			             <input type="hidden" value="${patientId}" id="patientId" />
			             <input type="hidden" value="${sessionId}" id="sessionId" />
			             <input type="hidden" value="${locationId}" id="locationId" />
			             <input type="hidden" value="${locationTagId}" id="locationTagId" />
                    </div>
                </div>
                <input type=hidden id= "Choice1" name="Choice1"/>
			    <input type=hidden id= "Choice2" name="Choice2"/>
			    <input type=hidden id= "Choice3" name="Choice3"/>
			    <input type=hidden id= "Choice4" name="Choice4"/>
			    <input type=hidden id= "Choice5" name="Choice5"/>
			    <input type=hidden id= "Choice6" name="Choice6"/>
			    <input id="patientId" name="patientId" type="hidden" value="${patient.patientId}"/>
				<input id="encounterId" name="encounterId" type="hidden" value="${encounterId}"/>
				<input id="sessionId" name="sessionId" type="hidden" value="${sessionId}"/>
				<input id="formId" name="formId" type="hidden" value="${formId}"/>
				<input id="formInstanceId" name="formInstanceId" type="hidden" value="${formInstanceId}"/>
				<input id="locationId" name="locationId" type="hidden" value="${locationId}"/>
				<input id="locationTagId" name="locationTagId" type="hidden" value="${locationTagId}"/>
				<input id="maxElements" name="maxElements" type="hidden" value="5"/>
				<input id="language" name="language" type="hidden" value="${language}"/>
				<input id="formInstance" name="formInstance" type="hidden" value="${formInstance}"/>
				<input id="providerId" name="providerId" type="hidden" value="${providerId}"/>
            </form>
    	</div>
    </body>
</html> 
