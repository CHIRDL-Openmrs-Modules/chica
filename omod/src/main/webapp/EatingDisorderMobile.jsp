<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ page import="org.openmrs.module.chirdlutil.util.Util"%>
<%@ page import="org.openmrs.Patient" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/EatingDisorderMobile.form" />
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
<script src="${pageContext.request.contextPath}/moduleResources/chica/EatingDisorderMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chicaMobile.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/EatingDisorderMobile.css">

</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>


<%@ include file="specialCharacters.jsp" %>

<!-- Titles/Headers/Footers/Buttons/Copyright  -->
<c:set var="formName" value='Eating Habits Questionnaire'/>
<!--  Spanish translation for form name is not available-->
<c:set var="formName_sp" value='Eating Habits Questionnaire (SPANISH)'/>

<c:set var="quitButtonText" value="Quit"/>
<c:set var="quitButtonText_sp" value="Dejar"/>

<!-- Questions (English) -->
<c:set var="question1"  value='Losing weight is an important goal to me.' scope="request"/>
<c:set var="question2"  value='I skip meals and${slash}or snacks.' scope="request"/>
<c:set var="question3"  value='Other people have told me that my eating is out of control.' scope="request"/>
<c:set var="question4"  value='When I overeat${comma} I don${apostrophe}t take enough insulin to cover the food.' scope="request"/>
<c:set var="question5"  value='I eat more when I am alone than when I am with others.' scope="request" />
<c:set var="question6"  value='I feel that it${apostrophe}s difficult to lose weight and control my diabetes at the same time.' scope="request" />
<c:set var="question7"  value='I avoid checking my blood sugar when I feel like it is out of range.' scope="request"/>
<c:set var="question8"  value='I make myself vomit.' scope="request"/>
<c:set var="question9"  value='I try to keep my blood sugar high so that I will lose weight.' scope="request"/>
<c:set var="question10" value='I try to eat to the point of spilling ketones in my urine.' scope="request"/>
<c:set var="question11" value='I feel fat when I take all of my insulin.' scope="request"/>
<c:set var="question12" value='Other people tell me to take better care of my diabetes.' scope="request"/>
<c:set var="question13" value='After I overeat${comma} I skip my next insulin dose.' scope="request"/>
<c:set var="question14" value='I feel that my eating is out of control.' scope="request"/>
<c:set var="question15" value='I alternate between eating very little and eating huge amounts.' scope="request"/>
<c:set var="question16" value='I would rather be thin than to have good control of my diabetes.' scope="request"/>

<!-- Questions (Spanish) -->
<!-- Currently not available-->
<!-- Created Spanish question vars and filled with English until Spanish is available. -->
<c:set var="question1_2"  value='${invQuestionMark}Question1 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question2_2"  value='${invQuestionMark}Question2 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question3_2"  value='${invQuestionMark}Question3 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question4_2"  value='${invQuestionMark}Question4 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question5_2"  value='${invQuestionMark}Question5 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question6_2"  value='${invQuestionMark}Question6 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question7_2"  value='${invQuestionMark}Question7 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question8_2"  value='${invQuestionMark}Question8 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question9_2"  value='${invQuestionMark}Question9 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question10_2" value='${invQuestionMark}Question10 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question11_2" value='${invQuestionMark}Question11 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question12_2" value='${invQuestionMark}Question12 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question13_2" value='${invQuestionMark}Question13 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question14_2" value='${invQuestionMark}Question14 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question15_2" value='${invQuestionMark}Question15 (SPANISH)${questionMark}' scope="request"/>
<c:set var="question16_2" value='${invQuestionMark}Question16 (SPANISH)${questionMark}' scope="request"/>


<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}')">

<form id="EatingDisorderForm" method="POST" action="EatingDisorderMobile.form" method="post" enctype="multipart/form-data">
    <c:if test="${errorMessage != null}">
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
    <div data-role="header" >
        <h1 id="formTitle">${formName}</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
    </div>
    <div data-role="content" id="informationContent">
        <strong><span id="instructions"></span></strong>
        <div><br/></div>
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
        
            <c:set var="QNumber" value="1" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="2" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
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
         
            <c:set var="QNumber" value="3" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="4" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
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
            
            <c:set var="QNumber" value="5" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="6" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
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
        
           <c:set var="QNumber" value="7" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="8" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
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

    <c:set var="PNumber" value="5" />
    <div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
        <div data-role="header">
            <h1>${formName}</h1>
            <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
            <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
            <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
        </div>
        <div id="content_${PNumber}" data-role="content">
        
            <c:set var="QNumber" value="9" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="10" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
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

    <c:set var="PNumber" value="6" />
    <div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
        <div data-role="header">
            <h1>${formName}</h1>
            <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
            <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
            <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
        </div>
        <div id="content_${PNumber}" data-role="content">
        
             <c:set var="QNumber" value="11" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="12" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
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

    <c:set var="PNumber" value="7" />
    <div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
        <div data-role="header">
            <h1>${formName}</h1>
            <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
            <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
            <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
        </div>
        
        <div id="content_${PNumber}" data-role="content">
        
            <c:set var="QNumber" value="13" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="14" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
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

    <c:set var="PNumber" value="8" />
    <div id="question_page_${PNumber}" data-role="page" data-theme="b" type="question_page">
       <div data-role="header">
            <h1>${formName}</h1>
            <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
            <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
            <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText}</a>
        </div>
        
        <div id="content_${PNumber}" data-role="content">
        
           <c:set var="QNumber" value="15" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
           
            <c:set var="QNumber" value="16" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <%@ include file="mobileFinishDialogs.jsp" %>
        </div>
       <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
            <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Previous</a>
            <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Continue</a>
        </div>
    </div>
    
    <!-- Form pages (Spanish) - two questions per page -->  
    <c:set var="PNumber" value="1" />
    <div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
        <div data-role="header">
            <h1>${formName_sp}</h1>
            <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
            <a id="langPage${PNumber}Button" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
            <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
        </div>        
           <div id="content_${PNumber}" data-role="content">
            <c:set var="QNumber" value="1_2" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script> insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="2_2" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
        </div>
        <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
            <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber} + 1)" style="width: 150px;">Proximo</a>
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
                
            <c:set var="QNumber" value="3_2" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
    
            <c:set var="QNumber" value="4_2" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
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
            <a id="langPage${PNumber}SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
            <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
        </div>  
        
        <div id="content_${PNumber}" data-role="content">
    
            <c:set var="QNumber" value="5_2" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
            <c:set var="questionName" value="question${QNumber}" />
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
    
            <c:set var="QNumber" value="6_2" />
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}" />
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
        
        <div id="content_${PNumber}" data-role="content">
            
            <c:set var="QNumber" value="7_2"/>
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
            <c:set var="questionName"  value="question${QNumber}"/>
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            
            <c:set var="QNumber" value="8_2"/>
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
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

    <c:set var="PNumber" value="5" />
    <div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
        <div data-role="header" >
           <h1>${formName_sp}</h1>
            <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
           <a id="langPage${PNumber}SPButton"  data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
            <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right"  data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
        </div>
        
        <div id="content_${PNumber}" data-role="content">
            
            <c:set var="QNumber" value="9_2"/>
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
            <c:set var="questionName"  value="question${QNumber}"/>
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="10_2"/>
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
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

    <c:set var="PNumber" value="6" />
    <div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
        <div data-role="header" >
            <h1>${formName_sp}</h1>
            <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
            <a id="langPage${PNumber}SPButton"  data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
            <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
        </div>
        
        <div id="content_${PNumber}" data-role="content">

            <c:set var="QNumber" value="11_2"/>
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
            <c:set var="questionName"  value="question${QNumber}"/>
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="12_2"/>
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
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
    
    <c:set var="PNumber" value="7" />
    <div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
        <div data-role="header" >
            <h1>${formName_sp}</h1>
            <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
            <a id="langPage${PNumber}SPButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
            <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
        </div>
        
        <div id="content_${PNumber}" data-role="content">
            
            <c:set var="QNumber" value="13_2"/>
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
            <c:set var="questionName"  value="question${QNumber}"/>
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <c:set var="QNumber" value="14_2"/>
            <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
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

    <c:set var="PNumber" value="8" />
    <div id="question_page_${PNumber}_sp" data-role="page" data-theme="b" type="question_page">
        <div data-role="header" >
            <h1>${formName_sp}</h1>
            <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
            <a id="langPage${PNumber}SPButton"  data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
            <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">${quitButtonText_sp}</a>
        </div>
        
        <div id="content_${PNumber}" data-role="content">
        
        
            <c:set var="QNumber" value="15_2"/>
             <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
            <c:set var="questionName"  value="question${QNumber}"/>
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            

             <c:set var="QNumber" value="16_2"/>
             <input id="EatingDisorderQuestion_${QNumber}" name="EatingDisorderQuestion_${QNumber}" type="hidden" value="question${QNumber}"/>
            <c:set var="questionName"  value="question${QNumber}"/>
            <strong>${requestScope[questionName]}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("question${QNumber}")'></a>
            <div class="choice${QNumber}" data-role="fieldcontain" style="margin-top:0px;"><script>insertChoices("${QNumber}");</script></div>
            
            <div style="float:right;"><span style="float: right;font-size: 75%;">${copyright}</span></div>
            <%@ include file="mobileFinishDialogs_SP.jsp" %>
        </div>
        <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
            <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="changePage(${PNumber}-1)" style="width: 150px;">Anterior</a>
            <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Continuar</a>
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
    <input id="EDS_interpretation" name="EDS_interpretation" type="hidden"  value="${EDS_interpretation}"/>

</form>
</body>
</html>