<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ page import="org.openmrs.module.chirdlutil.util.Util"%>
<%@ page import="org.openmrs.Patient" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/PSQMobile.form" />
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/PSQMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<c:set var="formName" value="PEDIATRIC SLEEP QUESTIONNAIRE:"/>
<c:set var="formName_sp" value="CUESTIONARIO PEDIATRICO DE SUE&#241O:"/>
<c:set var="option1" value="Yes"/>
<c:set var="option2" value="No"/>
<c:set var="option3" value="Don't Know"/>
<c:set var="option1_sp" value="Si"/>
<c:set var="option2_sp" value="No"/>
<c:set var="option3_sp" value="No se"/>
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
<form id="PSQForm" method="POST" action="PSQMobile.form" method="post" enctype="multipart/form-data">
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
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')"></a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop"></a>
    </div>

   <div data-role="content" >
        <strong><span id="additionalQuestions"></span></strong>
        <div><br/>
		</div>
        <strong><span id="instructions"></span></span></strong>
        <div><br/></div> 
    </div>

    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage(1)" style="width: 150px;"></a>
    </div>
    
</div>

<div data-role="page" id="form_completed_page" data-theme="b">
    <div data-role="header" >
        <h1>${formName} Form Completed</h1>
    </div>
    <div data-role="content" style="margin: 0 auto;text-align: center;" >
        <strong><span>The PSQ form has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-inline="true" data-theme="b" data-role="button" data-rel="back" style="width: 150px;">Back</a>
    </div>
</div>

<div id="quit_confirm_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Confirm Quit</h1>
    </div>
    <div data-role="content">
        <span>Are you sure you want to quit?</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#quit_dialog" data-role="button" data-rel="dialog" data-inline="true" data-theme="b" style="width: 150px;">Yes</a>
            <a href="#" data-role="button" data-rel="back" data-inline="true" data-theme="b" style="width: 150px;">No</a>
        </div>
    </div>
</div>

<div id="quit_confirm_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Confirmar Salir</h1>
    </div>
    <div data-role="content">
        <span>&#191;Est&#225; seguro de que desea salir?</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#quit_dialog_sp" data-role="button" data-rel="dialog" data-inline="true" data-theme="b" style="width: 150px;">Si</a>
            <a href="#" data-role="button" data-rel="back" data-inline="true" data-theme="b" style="width: 150px;">No</a>
        </div>
    </div>
</div>


<div id="quit_dialog" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Finished</h1>
    </div>
    <div data-role="content">
        <span>Thank you for filling out the form.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a onclick="submitForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">OK</a>
        </div>
    </div>
</div>


<div id="quit_dialog_sp" class="extended-header" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Acabado</h1>
    </div>
    <div data-role="content">
        <span>Gracias por rellenar el formulario.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a onclick="submitForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Aceptar</a>
        </div>
    </div>
</div>


<c:set var="copyright" value=''/>
<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formName}</h1>
         <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_1" data-role="content">
        <c:set var="quest1" value='While sleeping, does your child have trouble breathing, or struggle to breathe?'/>
        <input id="PSQQuestion_1" name="PSQQuestion_1" type="hidden" value="${quest1}"/>
        <strong>${quest1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest1}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="PSQQuestionEntry_1" id="PSQQuestionEntry_1_Y" value="yes" data-theme="c" />
                <label for="PSQQuestionEntry_1_Y">${option1}</label>
                <input type="radio" name="PSQQuestionEntry_1" id="PSQQuestionEntry_1_N" value="no" data-theme="c" />
                <label for=PSQQuestionEntry_1_N>${option2}</label>
                <input type="radio" name="PSQQuestionEntry_1" id="PSQQuestionEntry_1_DK" value="don't know" data-theme="c" />
                <label for="PSQQuestionEntry_1_DK">${option3}</label>           
            </fieldset>
        </div>
        
        <c:set var="quest2" value='Have you ever seen your child stop breathing during the night?'/>
        <input id="PSQQuestion_2" name="PSQQuestion_2" type="hidden" value="${quest2}"/>
        <strong>${quest2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                 <input type="radio" name="PSQQuestionEntry_2" id="PSQQuestionEntry_2_Y" value="yes" data-theme="c" />
                <label for="PSQQuestionEntry_2_Y">${option1}</label>
                <input type="radio" name="PSQQuestionEntry_2" id="PSQQuestionEntry_2_N" value="no" data-theme="c" />
                <label for=PSQQuestionEntry_2_N>${option2}</label>
                <input type="radio" name="PSQQuestionEntry_2" id="PSQQuestionEntry_2_DK" value="don't know" data-theme="c" />
                <label for="PSQQuestionEntry_2_DK">${option3}</label>           
            </fieldset>
        </div>
        
        <c:set var="quest3" value='Have you ever seen your child wake up with a snorting sound?'/>
        <input id="PSQQuestion_3" name="PSQQuestion_3" type="hidden" value="${quest3}"/>
        <strong>${quest3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest3}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                  <input type="radio" name="PSQQuestionEntry_3" id="PSQQuestionEntry_3_Y" value="yes" data-theme="c" />
                <label for="PSQQuestionEntry_3_Y">${option1}</label>
                <input type="radio" name="PSQQuestionEntry_3" id="PSQQuestionEntry_3_N" value="no" data-theme="c" />
                <label for=PSQQuestionEntry_3_N>${option2}</label>
                <input type="radio" name="PSQQuestionEntry_3" id="PSQQuestionEntry_3_DK" value="don't know" data-theme="c" />
                <label for="PSQQuestionEntry_3_DK">${option3}</label>  
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
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitals</a>
    </div>
    <div id="content_2" data-role="content">
        <c:set var="quest4" value='>Does your child occasionally wet the bed?'/>
        <input id="PSQQuestion_4" name="PSQQuestion_4" type="hidden" value="${quest4}"/>
        <strong>${quest4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest4}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="PSQQuestionEntry_4" id="PSQQuestionEntry_4_Y" value="yes" data-theme="c" />
                <label for="PSQQuestionEntry_4_Y">${option1}</label>
                <input type="radio" name="PSQQuestionEntry_4" id="PSQQuestionEntry_4_N" value="no" data-theme="c" />
                <label for=PSQQuestionEntry_4_N>${option2}</label>
                <input type="radio" name="PSQQuestionEntry_4" id="PSQQuestionEntry_4_DK" value="don't know" data-theme="c" />
                <label for="PSQQuestionEntry_4_DK">${option3}</label>         
            </fieldset>
        </div>
        <c:set var="quest5" value='>Does your child have a problem with sleepiness during the day?'/>
        <input id="PSQQuestion_5" name="PSQQuestion_5" type="hidden" value="${quest5}"/>
        <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest5}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
             <fieldset data-role="controlgroup" data-type="horizontal">
                 <input type="radio" name="PSQQuestionEntry_5" id="PSQQuestionEntry_5_Y" value="yes" data-theme="c" />
                <label for="PSQQuestionEntry_5_Y">${option1}</label>
                <input type="radio" name="PSQQuestionEntry_5" id="PSQQuestionEntry_5_N" value="no" data-theme="c" />
                <label for=PSQQuestionEntry_5_N>${option2}</label>
                <input type="radio" name="PSQQuestionEntry_5" id="PSQQuestionEntry_5_DK" value="don't know" data-theme="c" />
                <label for="PSQQuestionEntry_5_DK">${option3}</label>          
            </fieldset>
        </div>
         <c:set var="quest6" value='>Does your child wake up with headaches in the morning?'/>
        <input id="PSQQuestion_6" name="PSQQuestion_6" type="hidden" value="${quest6}"/>
        <strong>${quest6}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest6}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="PSQQuestionEntry_6" id="PSQQuestionEntry_6_Y" value="yes" data-theme="c" />
                <label for="PSQQuestionEntry_6_Y">${option1}</label>
                <input type="radio" name="PSQQuestionEntry_6" id="PSQQuestionEntry_6_N" value="no" data-theme="c" />
                <label for=PSQQuestionEntry_6_N>${option2}</label>
                <input type="radio" name="PSQQuestionEntry_6" id="PSQQuestionEntry_6_DK" value="don't know" data-theme="c" />
                <label for="PSQQuestionEntry_6_DK">${option3}</label>         
            </fieldset>
        </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
        
        <%@ include file="mobileFinishDialogs.jsp" %>
        
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Previous</a>
        <a  href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Finish</a>
    </div>
</div>

    
<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>${formName_sp}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_1_sp" data-role="content">      
        <c:set var="quest1_2" value='¿Cuando duerme su hijo/a tiene problemas o dificultad para respirar?'/>
        <input id="PSQQuestion_1_2" name="PSQQuestion_1_2" type="hidden" value="${quest1_2}"/>
        <strong>${quest1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="PSQQuestionEntry_1_2" id="PSQQuestionEntry_1_2_Y" value="yes" data-theme="c" />
                <label for="PSQQuestionEntry_1_2_Y">${option1_sp}</label>
                <input type="radio" name="PSQQuestionEntry_1_2" id="PSQQuestionEntry_1_2_N" value="no" data-theme="c" />
                <label for="PSQQuestionEntry_1_2_N">${option2_sp}</label>
                <input type="radio" name="PSQQuestionEntry_1_2" id="PSQQuestionEntry_1_2_DK" value="don't know" data-theme="c" />
                <label for="PSQQuestionEntry_1_2_DK">${option3_sp}</label>               
            </fieldset>
        </div>
        <c:set var="quest2_2" value='¿Alguna vez ha visto a su hijo parar de respirar por la noche?'/>
        <input id="PSQQuestion_2_2" name="PSQQuestion_2_2" type="hidden" value="${quest2_2}"/>
        <strong>${quest2_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest2_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="PSQQuestionEntry_2_2" id="PSQQuestionEntry_2_2_Y" value="yes" data-theme="c" />
                <label for="PSQQuestionEntry_2_2_Y">${option1_sp}</label>
                <input type="radio" name="PSQQuestionEntry_2_2" id="PSQQuestionEntry_2_2_N" value="no" data-theme="c" />
                <label for="PSQQuestionEntry_2_2_N">${option2_sp}</label>
                <input type="radio" name="PSQQuestionEntry_2_2" id="PSQQuestionEntry_2_2_DK" value="don't know" data-theme="c" />
                <label for="PSQQuestionEntry_2_2_DK">${option3_sp}</label>               
            </fieldset>
        </div>
        <c:set var="quest3_2" value='¿Alguna vez ha visto a su hijo despertarse con un bufido?'/>
        <input id="PSQQuestion_3_2" name="PSQQuestion_3_2" type="hidden" value="${quest3_2}"/>
        <strong>${quest3_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest3_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="PSQQuestionEntry_3_2" id="PSQQuestionEntry_3_2_Y" value="yes" data-theme="c" />
                <label for="PSQQuestionEntry_3_2_Y">${option1_sp}</label>
                <input type="radio" name="PSQQuestionEntry_3_2" id="PSQQuestionEntry_3_2_N" value="no" data-theme="c" />
                <label for="PSQQuestionEntry_3_2_N">${option2_sp}</label>
                <input type="radio" name="PSQQuestionEntry_3_2" id="PSQQuestionEntry_3_2_DK" value="don't know" data-theme="c" />
                <label for="PSQQuestionEntry_3_2_DK">${option3_sp}</label>               
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
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Vitales</a>
    </div>
    <div id="content_2_sp" data-role="content">       
        
        <c:set var="quest4_2" value='¿Su hijo/a de vez en cuando moja la cama? '/>
        <input id="PSQQuestion_4_2" name="PSQQuestion_4_2" type="hidden" value="${quest4_2}"/>
        <strong>${quest4_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest4_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="PSQQuestionEntry_4_2" id="PSQQuestionEntry_4_2_Y" value="0" data-theme="c" />
                <label for="PSQQuestionEntry_4_2_Y">${option1_sp}</label>
                <input type="radio" name="PSQQuestionEntry_4_2" id="PSQQuestionEntry_4_2_N" value="1" data-theme="c" />
                <label for="PSQQuestionEntry_4_2_N">${option2_sp}</label>
                <input type="radio" name="PSQQuestionEntry_4_2" id="PSQQuestionEntry_4_2_DK" value="2" data-theme="c" />
                <label for="PSQQuestionEntry_4_2_DK">${option3_sp}</label>                
            </fieldset>
        </div>
        <c:set var="quest5" value='¿Su hijo/a se va durmiendo durante el día?'/>
        <input id="PSQQuestion_5_2" name="PSQQuestion_5_2" type="hidden" value="${quest5}"/>
        <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest5}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="PSQQuestionEntry_5_2" id="PSQQuestionEntry_5_2_Y" value="0" data-theme="c" />
                <label for="PSQQuestionEntry_5_2_Y">${option1_sp}</label>
                <input type="radio" name="PSQQuestionEntry_5_2" id="PSQQuestionEntry_5_2_N" value="1" data-theme="c" />
                <label for="PSQQuestionEntry_5_2_N">${option2_sp}</label>
                <input type="radio" name="PSQQuestionEntry_5_2" id="PSQQuestionEntry_5_2_DK" value="2" data-theme="c" />
                <label for="PSQQuestionEntry_5_2_DK">${option3_sp}</label>              
            </fieldset>
        </div>
        <c:set var="quest6" value='¿Su hijo se queja de dolor de cabeza por las mañanas, cuando se despierta?'/>
        <input id="PSQQuestion_6_2" name="PSQQuestion_6_2" type="hidden" value="${quest6}"/>
        <strong>${quest5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest6}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="PSQQuestionEntry_6_2" id="PSQQuestionEntry_6_2_Y" value="0" data-theme="c" />
                <label for="PSQQuestionEntry_6_2_Y">${option1_sp}</label>
                <input type="radio" name="PSQQuestionEntry_6_2" id="PSQQuestionEntry_6_2_N" value="1" data-theme="c" />
                <label for="PSQQuestionEntry_6_2_N">${option2_sp}</label>
                <input type="radio" name="PSQQuestionEntry_6_2" id="PSQQuestionEntry_6_2_DK" value="2" data-theme="c" />
                <label for="PSQQuestionEntry_6_2_DK">${option3_sp}</label>              
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
         <%@ include file="mobileFinishDialogs_SP.jsp" %>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(1)" style="width: 150px;">Anterior</a>
        <a  href="#" onclick="attemptFinishForm()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Acabado</a>
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
<input id="PSQScore" name="PSQScore" type="hidden"/>
</form>
</body>
</html>
