<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ page import="org.openmrs.module.chirdlutil.util.Util"%>
<%@ page import="org.openmrs.Patient" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/SCAREDParentMobile.form" />
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/SCAREDParentMobile.css">
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/SCAREDParentMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<c:set var="formName" value="Screen for Child Anxiety Related Disorders:"/>
<c:set var="formName_sp" value="Des&oacute;rdenes Relacionados Con La Ansiedad En La Infancia:"/>
<c:set var="option1" value="Not true or hardly ever true"/>
<c:set var="option2" value="Sometimes true"/>
<c:set var="option3" value="Very true or often true"/>
<c:set var="option1_sp" value="Casi nunca o nunca es cierto"/>
<c:set var="option2_sp" value="Es cierto algunas veces"/>
<c:set var="option3_sp" value="Casi siempre o siempre es cierto"/>
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
<form id="SCAREDForm" method="POST" action="SCAREDParentMobile.form" method="post" enctype="multipart/form-data">
<c:if test="${errorMessage != null}">
    <div id="error_dialog" class="extended-header" data-role="dialog" data-close-btn="none" data-dismissible="false" data-theme="b" data-overlay-theme="c">
        <div data-role="header" data-theme="b">
            <h1>Error</h1>
        </div>
        <div data-role="content">
            <span>${errorMessage}</span>
            <div style="margin: 0 auto;text-align: center;">
                <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="submitEmptyForm()" style="width: 150px;">OK</a>
            </div>
        </div>
    </div>
</c:if>
<div data-role="page" id="instruction_page" data-theme="b">
    <div data-role="header" >
        <h1 id="formTitle">${formName}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>

    <div data-role="content" >
        <strong><span id="additionalQuestions">Here is a list of sentences that describe how people feel. Read each phrase and decide if it is "Not True or Hardly Ever True" or "Somewhat True or Sometimes True" or "Very True or Often True" for your child.</span></strong>
        <div><br/></div>
        <strong><span id="instructions">Choose the response that seems to describe your child <span style="text-decoration: underline;">for the last 3 months.</span></span></strong>
        <div><br/></div>
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage(1)" style="width: 150px;">Start</a>
    </div>
    
</div>

<div data-role="page" id="form_completed_page" data-theme="b">
    <div data-role="header" >
        <h1>${formName} Form Completed</h1>
    </div>
    <div data-role="content" style="margin: 0 auto;text-align: center;" >
        <strong><span>The SCARED form has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-inline="true" data-theme="b" data-role="button" data-rel="back" style="width: 150px;">Back</a>
    </div>
</div>

<%@ include file="mobileQuitConfirmDialog.jsp" %>

<c:set var="copyright" value=''/>
<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formName}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_1" data-role="content">
        <c:set var="quest1" value='My child gets really frightened for no reason at all.'/>
        <input id="SCAREDQuestion_1" name="SCAREDQuestion_1" type="hidden" value="${quest1}"/>
        <strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest1}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="SCAREDQuestionEntry_1" id="SCAREDQuestionEntry_1_NT" value="0" data-theme="b" />
                <label for="SCAREDQuestionEntry_1_NT">${option1}</label>
                <input type="radio" name="SCAREDQuestionEntry_1" id="SCAREDQuestionEntry_1_ST" value="1" data-theme="b" />
                <label for=SCAREDQuestionEntry_1_ST>${option2}</label>
                <input type="radio" name="SCAREDQuestionEntry_1" id="SCAREDQuestionEntry_1_OT" value="2" data-theme="b" />
                <label for="SCAREDQuestionEntry_1_OT">${option3}</label>           
            </fieldset>
        </div>
        <c:set var="quest2" value='My child is afraid to be alone in the house.'/>
        <input id="SCAREDQuestion_2" name="SCAREDQuestion_2" type="hidden" value="${quest2}"/>
        <strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="SCAREDQuestionEntry_2" id="SCAREDQuestionEntry_2_NT" value="0" data-theme="b" />
                <label for="SCAREDQuestionEntry_2_NT">${option1}</label>
                <input type="radio" name="SCAREDQuestionEntry_2" id="SCAREDQuestionEntry_2_ST" value="1" data-theme="b" />
                <label for=SCAREDQuestionEntry_2_ST>${option2}</label>
                <input type="radio" name="SCAREDQuestionEntry_2" id="SCAREDQuestionEntry_2_OT" value="2" data-theme="b" />
                <label for="SCAREDQuestionEntry_2_OT">${option3}</label>           
            </fieldset>
        </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_2" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formName}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_2" data-role="content">
    <c:set var="quest3" value='People tell me that my child worries too much.'/>
        <input id="SCAREDQuestion_3" name="SCAREDQuestion_3" type="hidden" value="${quest3}"/>
        <strong>${quest3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="SCAREDQuestionEntry_3" id="SCAREDQuestionEntry_3_NT" value="0" data-theme="b" />
                <label for="SCAREDQuestionEntry_3_NT">${option1}</label>
                <input type="radio" name="SCAREDQuestionEntry_3" id="SCAREDQuestionEntry_3_ST" value="1" data-theme="b" />
                <label for=SCAREDQuestionEntry_3_ST>${option2}</label>
                <input type="radio" name="SCAREDQuestionEntry_3" id="SCAREDQuestionEntry_3_OT" value="2" data-theme="b" />
                <label for="SCAREDQuestionEntry_3_OT">${option3}</label>           
            </fieldset>
        </div>
        <c:set var="quest4" value='My child is scared to go to school.'/>
        <input id="SCAREDQuestion_4" name="SCAREDQuestion_4" type="hidden" value="${quest4}"/>
        <strong>${quest4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="SCAREDQuestionEntry_4" id="SCAREDQuestionEntry_4_NT" value="0" data-theme="b" />
                <label for="SCAREDQuestionEntry_4_NT">${option1}</label>
                <input type="radio" name="SCAREDQuestionEntry_4" id="SCAREDQuestionEntry_4_ST" value="1" data-theme="b" />
                <label for=SCAREDQuestionEntry_4_ST>${option2}</label>
                <input type="radio" name="SCAREDQuestionEntry_4" id="SCAREDQuestionEntry_4_OT" value="2" data-theme="b" />
                <label for="SCAREDQuestionEntry_4_OT">${option3}</label>           
            </fieldset>
        </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_3" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formName}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_3" data-role="content">
    <c:set var="quest5" value='My child is shy.'/>
        <input id="SCAREDQuestion_5" name="SCAREDQuestion_5" type="hidden" value="${quest5}"/>
        <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest5}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
             <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="SCAREDQuestionEntry_5" id="SCAREDQuestionEntry_5_NT" value="0" data-theme="b" />
                <label for="SCAREDQuestionEntry_5_NT">${option1}</label>
                <input type="radio" name="SCAREDQuestionEntry_5" id="SCAREDQuestionEntry_5_ST" value="1" data-theme="b" />
                <label for=SCAREDQuestionEntry_5_ST>${option2}</label>
                <input type="radio" name="SCAREDQuestionEntry_5" id="SCAREDQuestionEntry_5_OT" value="2" data-theme="b" />
                <label for="SCAREDQuestionEntry_5_OT">${option3}</label>           
            </fieldset>
        </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
        
        <%@ include file="mobileFinishDialogs.jsp" %>
        
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Previous</a>
        <a  href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Finish</a>
    </div>
</div>
    
<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formName_sp}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_1_sp" data-role="content">      
        <c:set var="quest1_2" value='Le da miedo sin tener ning&uacute;n motivo.'/>
        <input id="SCAREDQuestion_1_2" name="SCAREDQuestion_1_2" type="hidden" value="${quest1_2}"/>
        <strong>${quest1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="SCAREDQuestionEntry_1_2" id="SCAREDQuestionEntry_1_2_NT" value="0" data-theme="b" />
                <label for="SCAREDQuestionEntry_1_2_NT">${option1_sp}</label>
                <input type="radio" name="SCAREDQuestionEntry_1_2" id="SCAREDQuestionEntry_1_2_ST" value="1" data-theme="b" />
                <label for="SCAREDQuestionEntry_1_2_ST">${option2_sp}</label>
                <input type="radio" name="SCAREDQuestionEntry_1_2" id="SCAREDQuestionEntry_1_2_OT" value="2" data-theme="b" />
                <label for="SCAREDQuestionEntry_1_2_OT">${option3_sp}</label>              
            </fieldset>
        </div>
        <c:set var="quest2_2" value='Le da miedo estar solo en casa.'/>
        <input id="SCAREDQuestion_2_2" name="SCAREDQuestion_2_2" type="hidden" value="${quest2_2}"/>
        <strong>${quest2_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest2_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="SCAREDQuestionEntry_2_2" id="SCAREDQuestionEntry_2_2_NT" value="0" data-theme="b" />
                <label for="SCAREDQuestionEntry_2_2_NT">${option1_sp}</label>
                <input type="radio" name="SCAREDQuestionEntry_2_2" id="SCAREDQuestionEntry_2_2_ST" value="1" data-theme="b" />
                <label for="SCAREDQuestionEntry_2_2_ST">${option2_sp}</label>
                <input type="radio" name="SCAREDQuestionEntry_2_2" id="SCAREDQuestionEntry_2_2_OT" value="2" data-theme="b" />
                <label for="SCAREDQuestionEntry_2_2_OT">${option3_sp}</label>               
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Proximo</a>      
    </div>
</div> 

<div id="question_page_2_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formName_sp}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_2_sp" data-role="content">       
        <c:set var="quest3_2" value='Las personas dicen que se preocupa demasiado.'/>
        <input id="SCAREDQuestion_3_2" name="SCAREDQuestion_3_2" type="hidden" value="${quest3_2}"/>
        <strong>${quest3_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest3_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="SCAREDQuestionEntry_3_2" id="SCAREDQuestionEntry_3_2_NT" value="0" data-theme="b" />
                <label for="SCAREDQuestionEntry_3_2_NT">${option1_sp}</label>
                <input type="radio" name="SCAREDQuestionEntry_3_2" id="SCAREDQuestionEntry_3_2_ST" value="1" data-theme="b" />
                <label for="SCAREDQuestionEntry_3_2_ST">${option2_sp}</label>
                <input type="radio" name="SCAREDQuestionEntry_3_2" id="SCAREDQuestionEntry_3_2_OT" value="2" data-theme="b" />
                <label for="SCAREDQuestionEntry_3_2_OT">${option3_sp}</label>               
            </fieldset>
        </div>
        <c:set var="quest4_2" value='Tiene miedo de ir al colegio.'/>
        <input id="SCAREDQuestion_4_2" name="SCAREDQuestion_4_2" type="hidden" value="${quest4_2}"/>
        <strong>${quest4_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest4_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="SCAREDQuestionEntry_4_2" id="SCAREDQuestionEntry_4_2_NT" value="0" data-theme="b" />
                <label for="SCAREDQuestionEntry_4_2_NT">${option1_sp}</label>
                <input type="radio" name="SCAREDQuestionEntry_4_2" id="SCAREDQuestionEntry_4_2_ST" value="1" data-theme="b" />
                <label for="SCAREDQuestionEntry_4_2_ST">${option2_sp}</label>
                <input type="radio" name="SCAREDQuestionEntry_4_2" id="SCAREDQuestionEntry_4_2_OT" value="2" data-theme="b" />
                <label for="SCAREDQuestionEntry_4_2_OT">${option3_sp}</label>                
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Proximo</a>   
    </div>
</div>

<div id="question_page_3_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formName_sp}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_3_sp" data-role="content">       
        <c:set var="quest5" value='Mi hijo(a) es t&iacute;mido(a).'/>
        <input id="SCAREDQuestion_5_2" name="SCAREDQuestion_50_2" type="hidden" value="${quest5}"/>
        <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest5}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                <input type="radio" name="SCAREDQuestionEntry_5_2" id="SCAREDQuestionEntry_5_2_NT" value="0" data-theme="b" />
                <label for="SCAREDQuestionEntry_5_2_NT">${option1_sp}</label>
                <input type="radio" name="SCAREDQuestionEntry_5_2" id="SCAREDQuestionEntry_5_2_ST" value="1" data-theme="b" />
                <label for="SCAREDQuestionEntry_5_2_ST">${option2_sp}</label>
                <input type="radio" name="SCAREDQuestionEntry_5_2" id="SCAREDQuestionEntry_5_2_OT" value="2" data-theme="b" />
                <label for="SCAREDQuestionEntry_5_2_OT">${option3_sp}</label>              
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
        
        <%@ include file="mobileFinishDialogs_SP.jsp" %>
        
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Anterior</a>
        <a href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Acabado</a>   
    </div>
</div> 

<div id="empty_page" data-role="page" data-theme="b">
</div>

<input id="formInstances" name="formInstances" type="hidden" value="${formInstances }"/>
<input id="patientId" name="patientId" type="hidden" value="${patient.patientId}"/>
<input id="encounterId" name="encounterId" type="hidden" value="${encounterId}"/>
<input id="formId" name="formId" type="hidden" value="${formId}"/>
<input id="formInstanceId" name="formInstanceId" type="hidden" value="${formInstanceId}"/>
<input id="locationId" name="locationId" type="hidden" value="${locationId}"/>
<input id="locationTagId" name="locationTagId" type="hidden" value="${locationTagId}"/>
<input id="sessionId" name="sessionId" type="hidden" value="${sessionId}"/>
<input id="language" name="language" type="hidden" value="${language}"/>
<input id="SCAREDScore" name="SCAREDScore" type="hidden"/>
</form>
</body>
</html>
