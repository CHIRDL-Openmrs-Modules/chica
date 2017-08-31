<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ page import="org.openmrs.module.chirdlutil.util.Util"%>
<%@ page import="org.openmrs.Patient" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/TRAQMobile.form" />
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
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/TRAQMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>


</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<c:set var="openParen" value="&#40"/>
<c:set var="closeParen" value="&#41"/>
<c:set var="colon" value="&#58"/>
<c:set var="formName" value="Transition Readiness Assessment Questionnaire${openParen}TRAQ${closeParen}"/>
<c:set var="formName_sp" value="Cuestionario de Evaluación para la Preparación de la Transición:"/>
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
<form id="TRAQForm" method="POST" action="TRAQMobile.form" method="post" enctype="multipart/form-data">
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
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">${staffButtonText}</a>
    </div>

   <div data-role="content" >
        <strong><span id="instructions"></span></strong>
        <div><br/></div> 
   </div>

    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage(1)" style="width: 150px;">Start</a>
    </div>
    
</div>

<div data-role="page" id="form_completed_page" data-theme="b">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire Completed</h1>
    </div>
    <div data-role="content" style="margin: 0 auto;text-align: center;" >
        <strong><span>The Transition Readiness Assessment Questionnaire has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
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

<c:set var="copyright" value='Copyright &#169; Wood, Sawicki, Reiss, Livingood &#38 Kraemer, 2014'/>
<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_1" data-role="content">
        <div><h3>Managing Medications:</h3><hr/><br/></div>
        <c:set var="QNumber" value="1"/>
        <c:set var="question" value='Do you fill a prescription if you need to?'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset class="choices" data-role="controlgroup" data-type="vertical">
	       
	        </fieldset>
	    </div>
	    <c:set var="QNumber" value="2"/>
	    <c:set var="question" value='Do you know what to do if you are having a bad reaction to your medications?'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	           
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
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_2" data-role="content">
        <div><h3>Managing Medications:</h3><hr/><br/></div>
        <c:set var="QNumber" value="3"/>
        <c:set var="question" value='Do you take medications correctly and on your own?'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
		      <!--     <script type="text/javascript">
	            insertChoices(${QNumber});
	        	</script> --> 
	        </fieldset>
	    </div>
	    <c:set var="QNumber" value="4"/>
	    <c:set var="question" value='Do you reorder medications before they run out?'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset class="choices" data-role="controlgroup" data-type="vertical">
	             
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
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
			<div id="content_3" data-role="content">
				<div>
					<h3>Appointment Keeping:</h3>
					<hr />
					<br />
				</div>
				<c:set var="QNumber" value="5" />
				<c:set var="question"
					value='Do you call the doctor&#39s office to make an appointment&#63' />
				<input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}" /> 
				<strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
				<div data-role="fieldcontain" style="margin-top: 0px;">
					<fieldset data-role="controlgroup" data-type="vertical">
			            
	       			 </fieldset>
				</div>
				<c:set var="QNumber" value="6" />
				<c:set var="question"
					value='Do you follow-up on any referral for tests, check-ups or labs?' />
				<input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}" /> 
					<strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
					<div data-role="fieldcontain" style="margin-top: 0px;">
						<fieldset data-role="controlgroup" data-type="vertical">
				            
		       			 </fieldset>
					</div>
				<div style="float: right;">
					<span style="float: right; font-size: 50%;">${copyright}</span>
				</div>
			</div>
			<div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Next</a>
    </div>
</div>




<div id="question_page_4" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage4Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_4" data-role="content">
        <div><h3>Appointment Keeping:</h3><hr/><br/></div>
        <c:set var="QNumber" value="7"/>
        <c:set var="question" value='Do you arrange for your ride to medical appointments&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            
	        </fieldset>
	    </div>
	    <c:set var="QNumber" value="8"/>
	    <c:set var="question" value='Do you call the doctor about unusual changes in your health ${openParen}For example${colon} Allergic reactions&#41&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            
	        </fieldset>
	    </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(3)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(5)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_5" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage5Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_5" data-role="content">
        <div><h3>Appointment Keeping:</h3><hr/><br/></div>
        <c:set var="QNumber" value="9"/>
        <c:set var="question" value='Do you apply for health insurance if you lose your current coverage&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	           
	        </fieldset>
	    </div>
	    <c:set var="QNumber" value="10"/>
	    <c:set var="question" value='Do you know what your current health insurance covers&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            
	        </fieldset>
	    </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(4)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(6)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_6" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage6Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_6" data-role="content">
        <div><h3>Appointment Keeping:</h3><hr/><br/></div>
        <c:set var="QNumber" value="11"/>
        <c:set var="question" value='Do you manage your money & budget household expenses &#40For example&#58 use checking&#47debit card&#41&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            
	        </fieldset>
	    </div>
	    <div><h3>Tracking Health Issues&#58</h3><hr/><br/></div>
	    <c:set var="QNumber" value="12"/>
	    <c:set var="question" value='Do you fill out the medical history form&#44 including a list of your allergies&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            
	        </fieldset>
	    </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(5)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(7)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_7" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage7Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_7" data-role="content">
        <div><h3>Tracking Health Issues&#58</h3><hr/><br/></div>
        <c:set var="QNumber" value="13"/>
        <c:set var="question" value='Do you keep a calendar or list of medical and other appointments&#63'/>
       	<input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	           
	        </fieldset>
	    </div>
	    <c:set var="QNumber" value="14"/>
	    <c:set var="question" value='Do you make a list of questions before the doctor&#39s visit&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	           
	        </fieldset>
	    </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(6)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(8)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_8" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage8Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_8" data-role="content">
        <div><h3>Tracking Health Issues&#58</h3><hr/><br/></div>
        <c:set var="QNumber" value="15"/>
        <c:set var="question" value='Do you get financial help with school or work&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            
	        </fieldset>
	    </div>
	     <div><h3>Talking with Providers&#58</h3><hr/><br/></div>
	     <c:set var="QNumber" value="16"/>
	    <c:set var="question" value='Do you tell the doctor or nurse what you are feeling&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            
	        </fieldset>
	    </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(7)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_9" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage9Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_9" data-role="content">
        <div><h3>Talking with Providers&#58</h3><hr/><br/></div>
        <c:set var="QNumber" value="17"/>
        <c:set var="question" value='Do you answer questions that are asked by the doctor&#44 nurse&#44 or clinic staff&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            
	        </fieldset>
	    </div>
	    <div><h3>Managing Daily Activities&#58</h3><hr/><br/></div>
	    <c:set var="QNumber" value="18"/>
	    <c:set var="question" value='Do you help plan or prepare meals&#47food&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	            
	        </fieldset>
	    </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(8)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(10)" style="width: 150px;">Next</a>
    </div>
</div>

<div id="question_page_10" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Transition Readiness Assessment Questionnaire:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage10Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
    </div>
    <div id="content_10" data-role="content">
        <div><h3>Managing Daily Activities&#58</h3><hr/><br/></div>
        <c:set var="QNumber" value="19"/>
        <c:set var="question" value='Do you keep home&#47room clean or clean-up after meals&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	          
	        </fieldset>
	    </div>
	    
	    <c:set var="QNumber" value="20"/>
	    <c:set var="question" value='Do you use neighborhood stores and services &#40For example&#58 Grocery stores and pharmacy stores&#41&#63'/>
        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
	    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
	    <div data-role="fieldcontain" style="margin-top:0px;">
	        <fieldset data-role="controlgroup" data-type="vertical">
	           
	        </fieldset>
	    </div>
        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="onclick="attemptFinishForm()" style="width: 150px;">Next</a>
    </div>
</div>







<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>CUESTIONARIO SOBRE EL SUE&Ntilde;O INFANTIL:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
    </div>
    <div id="content_1_sp" data-role="content">
        <div><h3>Acostarlo/ponerlo a dormir:</h3><hr/><br/></div>
        <c:set var="quest1_2" value='&iquest;Cu&aacute;nto tiempo en promedio tarda generalmente su beb&eacute; en disponerse a dormir?'/>
        <input id="TRAQQuestion_1_2" name="TRAQQuestion_1_2" type="hidden" value="${quest1_2}"/>
        <strong>${quest1_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest1_2}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="vertical">
                
            </fieldset>
        </div>
        <div style="float:right;"><br/><span style="float: right;font-size: 50%;">${copyright}</span></div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(2)" style="width: 150px;">Proximo</a>      
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
<input id="TRAQScore" name="TRAQScore" type="hidden"/>
<input id="TRAQProb" name="TRAQProb" type="hidden"/>
<input id="TRAQSevere" name="TRAQSevere" type="hidden"/>
<input id="TRAQResearch" name="TRAQResearch" type="hidden"/>
<input id="language" name="language" type="hidden" value="${language}"/>
</form>
</body>
</html>
