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
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/TRAQMobile.css">

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
<c:set var="semicolon" value="&#58"/>
<!--  <c:set var="questionMark" value="&#63"/> -->
<c:set var="apostrophe" value="&#39"/>
<c:set var="comma" value="&#44"/>
<c:set var="slash" value="&#47"/>
<c:set var="o_acute" value="&#243"/>
<c:set var="a_acute" value="&#225"/>
<c:set var="e_acute" value="&#233"/>
<c:set var="invQuestionMark" value="&#191"/>
<c:set var="copyrightSymbol" value="&#169"/>
<c:set var="ampersand" value="&#38"/>
<c:set var="hyphen" value="$#45"/>

<!-- Titles/Headers/Footers/Copyright -->
<c:set var="formName" value='Transition Readiness Assessment Questionnaire${openParen}TRAQ${closeParen}'/>
<c:set var="formName_sp" value='Cuestionario de Evaluaci${o_acute}n para la Preparaci${o_acute}n de la Transici${o_acute}n ${openParen}TRAQ${closeParen}:'/>
<c:set var="headerManageMedication" value='Managing Medications${colon}'/>
<c:set var="headerManageMedication_sp" value='Manejo de la medicaci${o_acute}n ${openParen}para pacientes que han tomado o toman medicaci${o_acute}n${closeParen}'/>
<c:set var="headerApptKeeping" value='Appointment Keeping${colon}'/>
<c:set var="headerTrackingHealthIssues" value='Tracking Health Issues${colon}'/>
<c:set var="headerTalkingWithProviders" value='Talking with Providers${colon}'/>
<c:set var="headerManagingDailyActivities" value='Managing Daily Activities${colon}'/>
<c:set var="headerFormCompleted" value='Transition Readiness Assessment Questionnaire Complete'/>
<c:set var="copyright" value='Copyright ${copyrightSymbol}${semicolon} Wood${comma} Sawicki${comma} Reiss${comma} Livingood ${ampersand} Kraemer${comma} 2014'/>

<!-- Questions (English) -->
<c:set var="question1"  value='Do you fill a prescription if you need to${questionMark}' scope="request"/>
<c:set var="question2"  value='Do you know what to do if you are having a bad reaction to your medications${questionMark}' scope="request"/>
<c:set var="question3"  value='Do you take medications correctly and on your own${questionMark}' scope="request"/>
<c:set var="question4"  value='Do you reorder medications before they run out${questionMark}' scope="request"/>
<c:set var="question5"  value='Do you call the doctor${apostrophe}s office to make an appointment${questionMark}' scope="request" />
<c:set var="question6"  value='Do you follow-up on any referral for tests${comma} check${hyphen}ups or labs${questionMark}' scope="request" />
<c:set var="question7"  value='Do you arrange for your ride to medical appointments${questionMark}' scope="request"/>
<c:set var="question8"  value='Do you call the doctor about unusual changes in your health ${openParen}For example${colon} Allergic reactions${closeParen}${questionMark}' scope="request"/>
<c:set var="question9"  value='Do you apply for health insurance if you lose your current coverage${questionMark}' scope="request"/>
<c:set var="question10" value='Do you know what your current health insurance covers${questionMark}' scope="request"/>
<c:set var="question11" value='Do you manage your money & budget household expenses ${openParen}For example${colon} use checking${slash}debit card${closeParen}${questionMark}' scope="request"/>
<c:set var="question12" value='Do you fill out the medical history form${comma} including a list of your allergies${questionMark}' scope="request"/>
<c:set var="question13" value='Do you keep a calendar or list of medical and other appointments${questionMark}' scope="request"/>
<c:set var="question14" value='Do you make a list of questions before the doctor${apostrophe}s visit${questionMark}' scope="request"/>
<c:set var="question15" value='Do you get financial help with school or work${questionMark}' scope="request"/>
<c:set var="question16" value='Do you tell the doctor or nurse what you are feeling${questionMark} scope="request"'/>
<c:set var="question17" value='Do you answer questions that are asked by the doctor${comma} nurse${comma} or clinic staff${questionMark}' scope="request"/>
<c:set var="question18" value='Do you help plan or prepare meals${slash}food${questionMark}' scope="request"/>
<c:set var="question19" value='Do you keep home${slash}room clean or clean-up after meals${questionMark}'  scope="request"/>
<c:set var="question20" value='Do you use neighborhood stores and services ${openParen}For example${colon} Grocery stores and pharmacy stores${closeParen}${questionMark}' scope="request"/>

<!-- Questions (Spanish) -->
<c:set var="question1_2" value='${invQuestionMark}Si la necesit${a_acute}s${comma} vas a buscar tu medicaci${o_acute}n a la farmacia${questionMark}' scope="request"/>
<c:set var="question2_2" value='${invQuestionMark}Sab${e_acute}s qu${e_acute} hacer si ten${e_acute}s una reacci${o_acute}n adversa a tu  medicaci${o_acute}n ${openParen}por ejemplo${colon} reacci${o_acute}n al${e_acute}rgica${comma} diarrea por la  medicaci${o_acute}n${questionMark}' scope="request"/>
<c:set var="question3_2" value='${invQuestionMark}Tom${a_acute}s por vos mismo tus medicamentos tal como te los indicaron${questionMark}' scope="request"/>

<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
<form id="TRAQForm" method="POST" action="TRAQMobile.form" method="post" enctype="multipart/form-data">
	<c:if test="${errorMessage != null}">
	    <div id="error_dialog" class="extended-header" data-role="dialog" data-close-btn="none"
	    		 data-dismissible="false" data-theme="b" data-overlay-theme="c">
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
	        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
	        	onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" 
	        	data-icon="forward" data-transition="pop">${staffButtonText}</a>
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
	        <h1>${headerFormCompleted}</h1>
	    </div>
	    <div data-role="content" style="margin: 0 auto;text-align: center;" >
	        <strong><span>The Transition Readiness Assessment Questionnaire has already been completed and successfully submitted. "
	        + "It cannot be accessed again.</span></strong>
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


<!-- Form pages (English) - two questions per page -->	
	<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
	    <div data-role="header" >
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
	        		onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" 
	        		data-icon="forward" data-transition="pop">Staff</a>
	    </div>
	    <div id="content_1" data-role="content">
	        <div><h3>${headerManageMedication}</h3><hr/><br/></div>
	        <c:set var="QNumber" value="1"/>
	        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
		    </div>
		    <c:set var="QNumber" value="2"/>
		    <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
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
	        <a id="langPage2Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
	        		onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right"
	        		 data-icon="forward" data-transition="pop">Staff</a>
	    </div>
	    <div id="content_2" data-role="content">
	        <div><h3>${headerManageMedication}</h3><hr/><br/></div>
	        <c:set var="QNumber" value="3"/>
	       	<input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
		    </div>
		    <c:set var="QNumber" value="4"/>
		    <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
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
	        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
	        		onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" 
	        		data-icon="forward" data-transition="pop">Staff</a>
	    </div>
		<div id="content_3" data-role="content">
			<div>
				<h3>${headerApptKeeping}</h3>
				<hr />
				<br />
			</div>
			<c:set var="QNumber" value="5" />
			<input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
	    		<script>
	    			insertChoices("${QNumber}");
	    		</script>
	    	</div>
			<c:set var="QNumber" value="6" />
			<input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
    			<script>
    				insertChoices("${QNumber}");
    			</script>
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
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage4Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
	        		onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" 
	        		data-icon="forward" data-transition="pop">Staff</a>
	    </div>
	    <div id="content_4" data-role="content">
	        <div><h3>${headerApptKeeping}</h3><hr/><br/></div>
	        <c:set var="QNumber" value="7"/>
	        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
		    </div>
		    <c:set var="QNumber" value="8"/>
		    <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    			<script>
		    				insertChoices("${QNumber}");
		    			</script>
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
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage5Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" 
	        		onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" 
	        		data-icon="forward" data-transition="pop">Staff</a>
	    </div>
	    <div id="content_5" data-role="content">
	        <div><h3>${headerApptKeeping}</h3><hr/><br/></div>
	        <c:set var="QNumber" value="9"/>
	        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
	        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
    			<script>
    				insertChoices("${QNumber}");
    			</script>
    		</div>
		    <c:set var="QNumber" value="10"/>
		    <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
    			<script>
    				insertChoices("${QNumber}");
    			</script>
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
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage6Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
	    </div>
	    <div id="content_6" data-role="content">
	        <div><h3>${headerApptKeeping}</h3><hr/><br/></div>
	        <c:set var="QNumber" value="11"/>
	        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
    			<script>
    				insertChoices("${QNumber}");
    			</script>
    		</div>
		    <div><h3>${headerTrackingHealthIssues}</h3><hr/><br/></div>
		    <c:set var="QNumber" value="12"/>
		    <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
    			<script>
    				insertChoices("${QNumber}");
    			</script>
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
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage7Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
	    </div>
	    <div id="content_7" data-role="content">
	        <div><h3>${headerTrackingHealthIssues}</h3><hr/><br/></div>
	        <c:set var="QNumber" value="13"/>
	        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
		    </div>
		    <c:set var="QNumber" value="14"/>
		    <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
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
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage8Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
	    </div>
	    <div id="content_8" data-role="content">
	        <div><h3>${headerTrackingHealthIssues}</h3><hr/><br/></div>
	        <c:set var="QNumber" value="15"/>
	         <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
		    </div>
		     <div><h3>Talking with Providers&#58</h3><hr/><br/></div>
		     <c:set var="QNumber" value="16"/>
		     <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
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
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage9Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
	    </div>
	    <div id="content_9" data-role="content">
	        <div><h3>${headerTrackingHealthIssues}</h3><hr/><br/></div>
	        <c:set var="QNumber" value="17"/>
	        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
		    </div>
		    <div><h3>${headerManagingDailyActivities}</h3><hr/><br/></div>
		    <c:set var="QNumber" value="18"/>
		    <c:set var="question" value='Do you help plan or prepare meals&#47food&#63'/>
	        <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="${question}"/>
		    <strong>${question}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${question}")'></a>
					 <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    			<script>
		    				insertChoices("${QNumber}");
		    			</script>
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
	        <h1>${formName}</h1>
	        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
	        <a id="langPage10Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
	        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Staff</a>
	    </div>
	    <div id="content_10" data-role="content">
	        <div><h3>${headerManagingDailyActivities}</h3><hr/><br/></div>
	        <c:set var="QNumber" value="19"/>
	         <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
		    </div>
		    <c:set var="QNumber" value="20"/>
		     <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
	        <c:set var="questionName"  value="question${QNumber}"/>
		    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
		    	<script>
		    		insertChoices("${QNumber}");
		    	</script>
		    </div>
	        <div style="float:right;"><span style="float: right;font-size: 50%;">${copyright}</span></div>
	    </div>
	    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
	       <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(9)" style="width: 150px;">Previous</a>
	        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Continue</a>
	    </div>
	</div>

<!-- Form pages (Spanish) - two questions per page -->
	  <div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
		    <div data-role="header" >
		        <h1>${formName_sp}</h1>
		        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
		        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
		        <a data-role="button" onclick="parent.navigateToVitals()" data-theme="b" class="vitalsButton ui-btn-right" data-icon="forward" data-transition="pop">Personal</a>
		    </div>
		    <div id="content_1_sp" data-role="content">
		        <div><h3>${headerManageMedication_sp}</h3><hr/><br/></div>
		        <c:set var="QNumber" value="1_2"/>
		         <input id="TRAQQuestion_${QNumber}" name="TRAQQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
		        <c:set var="questionName"  value="question${QNumber}"/>
			    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
			    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;" >
			    	<script>
			    		insertChoices("${QNumber}");
			    	</script>
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
