<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/psfMobile.form" />
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
<script src="${pageContext.request.contextPath}/moduleResources/chica/sexRiskMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
</head>
<c:set var="search" value="'" />
<c:set var="replace" value="\\'" />
<c:set var="newFirstName" value="${fn:replace(patient.givenName, search, replace)}"/>
<c:set var="newLastName" value="${fn:replace(patient.familyName, search, replace)}"/>
<body onLoad="init('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}', '${formInstance}', '${language}', '${patient.gender}')">
<form id="sexRiskForm" method="POST" action="sexRiskMobile.form" method="post" enctype="multipart/form-data">
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
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguage('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Quit</a>
    </div>

    <div data-role="content" >
        <strong><span id="additionalQuestions">The following are some additional questions about sexual behavior.</span></strong>
        <div><br/></div>
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="startButton" href="#" data-role="button" data-theme="b" onclick="changePage(1)" style="width: 150px;">Start</a>
    </div>
</div>

<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">Espa&#241;ol</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Quit</a>
    </div>
    <div data-role="content">
        <c:set var="quest_1" value='Have you had sex (including intercourse or oral sex) within the past year?'/>
        <input id="Question_1" name="Question_1" type="hidden" value="${quest_1}"/>
        <strong>${quest_1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_1}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1" id="QuestionEntry_1_Yes" value="yes" data-theme="c" />
                <label for="QuestionEntry_1_Yes">Yes</label>
                <input type="radio" name="QuestionEntry_1" id="QuestionEntry_1_No" value="no" data-theme="c" />
                <label for="QuestionEntry_1_No">No</label>
            </fieldset>
        </div>
        <div id="question_2_container">
            <br/>
            <c:set var="quest_2" value='Have you had sex (including intercourse or oral sex) within the past 5 days?'/>
            <input id="Question_2" name="Question_2" type="hidden" value="${quest_2}"/>
            <strong>${quest_2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${quest_2}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_2" id="QuestionEntry_2_Yes" value="yes" data-theme="c" />
                    <label for="QuestionEntry_2_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_2" id="QuestionEntry_2_No" value="no" data-theme="c" />
                    <label for="QuestionEntry_2_No">No</label>
                </fieldset>
            </div>
        </div>
        <%@ include file="mobileFinishDialogs.jsp" %>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Continue</a>
    </div>
</div>

<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" >
        <h1>Sex Risk Screener:</h1>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a data-role="button" href="#" class="ui-btn-left" data-theme="b" onclick="setLanguageFromForm('${newFirstName}&nbsp;${newLastName}', '${patient.birthdate}')">English</a>
        <a data-role="button" onclick="parent.quitForm()" data-theme="b" class="quitButton ui-btn-right" data-icon="forward" data-transition="pop">Dejar</a>
    </div>
    <div data-role="content">
        <c:set var="quest_1_sp" value='&#191;Has tenido sexo (incluyendo relaciones sexuales o sexo oral) dentro del a&#241;o pasado?'/>
        <input id="Question_1_2" name="Question_1_2" type="hidden" value="${quest_1_sp}"/>
        <strong>${quest_1_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_1_sp}")'></a>
        <div data-role="fieldcontain" style="margin-top:0px;">
            <fieldset data-role="controlgroup" data-type="horizontal">
                <input type="radio" name="QuestionEntry_1_2" id="QuestionEntry_1_2_Yes" value="yes" data-theme="c" />
                <label for="QuestionEntry_1_2_Yes">Si</label>
                <input type="radio" name="QuestionEntry_1_2" id="QuestionEntry_1_2_No" value="no" data-theme="c" />
                <label for="QuestionEntry_1_2_No">No</label>
            </fieldset>
        </div>
        <div id="question_2_container_sp">
            <br/>
            <c:set var="quest_2_sp" value='&#191;Has tenido sexo (incluyendo relaciones sexuales o sexo oral) dentro del 5 dias pasados?'/>
            <input id="Question_2_2" name="Question_2_2" type="hidden" value="${quest_2_sp}"/>
            <strong>${quest_2_sp}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${quest_2_sp}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_2_2" id="QuestionEntry_2_2_Yes" value="yes" data-theme="c" />
                    <label for="QuestionEntry_2_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_2_2" id="QuestionEntry_2_2_No" value="no" data-theme="c" />
                    <label for="QuestionEntry_2_2_No">No</label>
                </fieldset>
            </div>
        </div>
        <%@ include file="mobileFinishDialogs_SP.jsp" %>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="attemptFinishForm()" style="width: 150px;">Continuar</a>
    </div>
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
