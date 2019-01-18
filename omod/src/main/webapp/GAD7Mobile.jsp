<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ page import="org.openmrs.module.chirdlutil.util.Util"%>
<%@ page import="org.openmrs.Patient" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/GAD7Mobile.form" />
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/browserFixMobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.blockUI.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
		<script>var ctx = "${pageContext.request.contextPath}";</script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/GAD7Mobile.js" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/GAD7Mobile.css">
	
	</head>
    <c:set var="search" value="'" />
    <c:set var="replace" value="\\'" />
    <c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
    <c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>


    <%@ include file="specialCharacters.jsp" %>
	
	<!-- Titles/Headers/Footers/Buttons/Copyright  -->
	<c:set var="formName" value='Generalized Anxiety Disorder ${openParen}GAD-7${closeParen}'/>
	<c:set var="formName_sp" value=''/>
	<c:set var="headerScreenQuestion" value='Over the last 2 weeks${comma} how often have you been bothered by the following problems${questionMark}'/>
	<c:set var="headerScreenQuestion_sp" value='Durante las ${uAcute}ltimas 2 semanas, ${invQuestionMark}con qu${eAcute} frecuencia ha sentido molestias por los siguientes problemas${questionMark}' />
	
	<c:set var="quitButtonText" value="Quit"/>
	<c:set var="quitButtonText_sp" value="Dejar"/>
	<c:set var="copyright" value='Developed by Drs. Robert L. Spitzer, Janet B.W. Williams, Kurt Kroenke and colleagues, with an educational grant from Pfizer Inc.  No permission required to reproduce, translate, display or distribute. '/>
	
	<!-- Questions (English) -->
	<c:set var="question1"  value='Feeling nervous${comma} anxious or on edge' scope="request"/>
	<c:set var="question2"  value='Not being able to stop or control worrying' scope="request"/>
	<c:set var="question3"  value='Worrying too much about different things' scope="request"/>
	<c:set var="question4"  value='Trouble relaxing' scope="request"/>
	<c:set var="question5"  value='Being so restless that it is hard to sit still ' scope="request" />
	<c:set var="question6"  value='Becoming easily annoyed or irritable' scope="request" />
	<c:set var="question7"  value='Feeling afraid as if something awful might happen ' scope="request"/>
	
	<!-- Questions (Spanish) -->
	<c:set var="question1_2" value='Sentirse nervioso/a, intranquilo/a o con los nervios de punta' scope="request"/>
	<c:set var="question2_2" value='No poder dejar de preocuparse o no poder controlar la preocupaci�n' scope="request"/>
	<c:set var="question3_2" value='Preocuparse demasiado por diferentes cosa' scope="request"/>
	<c:set var="question4_2" value='Dificultad para relajarse' scope="request"/>
	<c:set var="question5_2" value='Estar tan inquieto/a que es dif�cil permanecer sentado/a tranquilamente ' scope="request"/>
	<c:set var="question6_2" value='Molestarse o ponerse irritable f�cilmente' scope="request"/>
	<c:set var="question7_2" value='Sentir miedo como si algo terrible pudiera pasar' scope="request"/>
	
	<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">
	
	<form id="GAD7Form" method="POST" action="GAD7Mobile.form" method="post" enctype="multipart/form-data">
		<c:if test="${c != null}">
		    <div id="error_dialog" class="extended-header" data-role="dialog" data-close-btn="none" data-dismissible="false"
		        data-theme="b" data-overlay-theme="c">
		        <div data-role="header" data-theme="b"><h1>Error</h1></div>
		        <div data-role="content"><span>${errorMessage}</span>
		            <div style="margin: 0 auto;text-align: center;">
		            	<a href="#" data-role="button" data-inline="true" data-theme="b" onclick="submitEmptyForm()" style="width: 150px;">OK</a>
		            </div>
		        </div>
		    </div>
		</c:if>
		
		
	<!-- Instructions/Start page -->
		<div data-role="page" id="instruction_page" data-theme="b">
		    <div data-role="header">
		        <h1 id="formTitle">${formName}</h1>
		        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
		        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
		        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
		    </div>
		    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
		        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage(1)" style="width: 150px;">Start</a>
		    </div>
		
		</div>
		
		
	<!-- Form pages (English) - two questions per page -->	
		<c:set var="PNumber" value="1" />
		<div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
		    <div data-role="header">
		        <h1>${formName}</h1>
		        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
		        <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
		        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
		    </div>
		    
		    <div id="content_${PNumber}" data-role="content">
		    
		        <div><h3>${headerScreenQuestion}</h3><hr/></div>
		            
		        <c:set var="QNumber" value="1" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script> insertChoices("${QNumber}");</script></div>
		        
		        <c:set var="QNumber" value="2" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
		    </div>
		    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber} + 1)" style="width: 150px;">Next</a>
		    </div>
		</div>
	
		<c:set var="PNumber" value="2" />
		<div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
		    <div data-role="header">
		        <h1>${formName}</h1>
		        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
		        <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
		        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
		    </div>
		    
		    <div id="content_${PNumber}" data-role="content">
		    
		        <div><h3>${headerScreenQuestion}</h3><hr/></div>
		            
		        <c:set var="QNumber" value="3" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        <c:set var="QNumber" value="4" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
		    </div>
		    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Previous</a>
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Next</a>
		    </div>
		</div>
	
		<c:set var="PNumber" value="3" />
		<div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
		    <div data-role="header">
		        <h1>${formName}</h1>
		        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
		        <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
		        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
		    </div>
		    
		    <div id="content_${PNumber}" data-role="content">
		    
		        <div><h3>${headerScreenQuestion}</h3><hr/></div>
		            
		        <c:set var="QNumber" value="5" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        <c:set var="QNumber" value="6" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        <div style="float: right;">
		            <span style="float: right; font-size: 75%;">${copyright}</span>
		        </div>
		    </div>
		    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Previous</a>
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Next</a>
		    </div>
		</div>
		
		<c:set var="PNumber" value="4" />
		<div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
		    <div data-role="header">
		        <h1>${formName}</h1>
		        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
		        <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
		        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
		    </div>
		    
		    <div id="content_${PNumber}" data-role="content">
		    
		        <div><h3>${headerScreenQuestion}</h3><hr/></div>
		            
		        <c:set var="QNumber" value="7" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        <c:set var="QNumber" value="8" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        <div style="float: right;">
		            <span style="float: right; font-size: 75%;">${copyright}</span>
		        </div>
		    </div>
		   <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Previous</a>
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Next</a>
		    </div>
		</div>
	
		
	
	<!-- Form pages (Spanish) - two questions per page -->
		<c:set var="PNumber" value="1" />
		<div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
		    <div data-role="header">
		        <h1>${formName_sp}</h1>
		        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
		        <a id="langPage${PNumber}SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
		        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
		    </div>
		    
		    <div id="content_${PNumber}_sp" data-role="content">
		
		        <div><h3>${headerScreenQuestion_sp}</h3><hr/></div>
		
		        <c:set var="QNumber" value="1_2" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		
		        <c:set var="QNumber" value="2_2" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
		    </div>
		    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Proximo</a>
		    </div>
		</div>
			
			
		<c:set var="PNumber" value="2" />
		<div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
		    <div data-role="header">
		        <h1>${formName_sp}</h1>
		        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
		        <a id="langPage${PNumber}SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
		        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
		    </div>
		    
		    <div id="content_${PNumber}_sp" data-role="content">
	     
	            <div><h3>${headerScreenQuestion_sp}</h3><hr/></div>
		            
		        <c:set var="QNumber" value="3_2" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		
		        <c:set var="QNumber" value="4_2" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        
		        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
		    </div>
		    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Anterior</a>
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Proximo</a>
		    </div>
		</div>
			
			
		<c:set var="PNumber" value="3" />
		<div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
		    <div data-role="header">
		        <h1>${formName_sp}</h1>
		        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
		        <a id="langPage${PNumber}SPButton"  data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
		        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
		    </div>
		    
		    <div id="content_${PNumber}" data-role="content">
	     
				<div><h3>${headerScreenQuestion_sp}</h3><hr/></div>
		
		        <c:set var="QNumber" value="5_2" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		
		        <c:set var="QNumber" value="6_2" />
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}" />
		        <c:set var="questionName" value="question${QNumber}" />
		        <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
		        <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        <div style="float: right;">
		            <span style="float: right; font-size: 75%;">${copyright}</span>
		        </div>
		    </div>
		    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Anterior</a>
		        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Proximo</a>
		    </div>
		</div>
		
		
		<c:set var="PNumber" value="4" />
		<div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
		    <div data-role="header" >
		        <h1>${formName_sp}</h1>
		        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
		        <a id="langPage${PNumber}SPButton"  data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
		        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
		    </div>
		    
		    <div id="content_${PNumber}" data-role="content"><div><h3>${headerScreenQuestion_sp}</h3><hr/></div>
		        
		        <c:set var="QNumber" value="7_2"/>
		        <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}"/>
		        <c:set var="questionName"  value="question${QNumber}"/>
			    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
			    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
			    
			    <c:set var="QNumber" value="8_2"/>
			    <input id="Question_${QNumber}" name="Question_${QNumber}" type="hidden" value="question${QNumber}"/>
		        <c:set var="questionName"  value="question${QNumber}"/>
			    <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
			    <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
		        
		        <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
		    </div>
		    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
		       	  <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Anterior</a>
			      <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}+1)" style="width: 150px;">Proximo</a>    
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
	
	</form>
	</body>
</html>