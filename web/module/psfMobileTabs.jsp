<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/psfMobile_tabs.form" />
<html>
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script>
$(document).bind("mobileinit", function(){
    $('#confirm_page').on('pageshow', function (event, ui) {
    	$("#confirm_page_tab").addClass('ui-btn-active');
    });
});
</script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/psfMobile.js" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
<style>
#quit_dialog .ui-header a {
    display: none;
}

#quit_dialog_sp .ui-header a {
    display: none;
}

.ui-popup-screen {
    right:0;
    position:fixed;
}

.custom-button {
    height: 40px !important;
    width: 40px !important;
}

.custom-button .ui-btn-inner {
    padding: 0 !important;    
}

.custom-button .ui-btn-inner .ui-btn-text {
    display: block !important;
    height: 40px !important;
    width: 100% !important;
    background-image: url('${pageContext.request.contextPath}/moduleResources/chica/images/speaker.png') !important;
    background-repeat: no-repeat  !important;
    background-position: center  !important; 
}

.error-field {
  background: red !important;
  color: white !important;
}
</style>
</head>
<body>
<form id="psfForm" method="POST">
<div data-role="page" id="tab_page" data-theme="b">
	<div data-role="tabs" id="tabs">
	  <div data-role="navbar">
	    <ul>
	      <li><a id="confirm_page_tab" href="#confirm_page" data-ajax="false">Questions</a></li>
	      <li><a id="questions_tab" href="#vitals_page" data-ajax="false">Staff</a></li>
	    </ul>
	  </div>
	</div>
</div>
<div data-role="page" id="confirm_page" data-theme="b">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-right" onclick="setLanguage('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
    </div>

    <div data-role="content" >
        <strong><span id="parentText">Parents: Thank you for answering these questions about your child.  The answers will help your doctor provide better quality of care.  If your child is age 12 or older, he/she should answer the questions privately.  Answers are confidential, but if you prefer not to answer that is allowed.  You may want to talk about these questions with your doctor.</span></strong>
        <div><br/></div>
        <hr/>
        <strong><span id="instructions"><p>1) Please return the device back to the front desk if the patient information listed is incorrect.</p><p>2) Please confirm this form is for:<br/>Name: ${patient.givenName}&nbsp;${patient.familyName}<br/>Date of Birth: ${patient.birthdate}</p></span></strong>
        <div id="deny_dialog" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Confirm</h1>
            </div>
            <div data-role="content">
                <span>If you are sure the incorrect patient is displayed, Press 'OK' and return the device to the receptionist.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="${pageContext.request.contextPath}/module/chica/greaseBoardMobile.form" data-inline="true" data-theme="b" data-role="button" rel="external" style="width: 150px;">OK</a>
                    <a href="#confirm_page" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">Cancel</a>
                </div>
            </div>
        </div>
        
        <div id="deny_dialog_sp" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Confirmar</h1>
            </div>
            <div data-role="content">
                <span>Si est&#225; seguro de que el paciente incorrecta aparece, pulse 'Aceptar' y devolver el dispositivo a la recepcionista.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="${pageContext.request.contextPath}/module/chica/greaseBoardMobile.form" data-inline="true" data-theme="b" data-role="button" rel="external" style="width: 150px;">Acceptar</a>
                    <a href="#confirm_page" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">Cancelar</a>
                </div>
            </div>
        </div>
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="confirmButton" href="#" data-role="button" data-theme="b" onclick="nextPage(1)" style="width: 150px;">Confirm</a>
        <a id="denyButton" href="#" data-role="button" data-theme="b" style="width: 150px;">Deny</a>
    </div>
    
</div><!-- /page one -->

<div data-role="page" id="form_completed_page" data-theme="b">
    <div data-role="header" data-position="fixed">
        <h1>Pre-screening Form Completed</h1>
    </div>
    <div data-role="content" style="margin: 0 auto;text-align: center;" >
        <strong><span>The Pre-screening form has already been completed and successfully submitted.  It cannot be accessed again.</span></strong>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="${pageContext.request.contextPath}/module/chica/greaseBoardMobile.form" data-inline="true" data-theme="b" data-role="button" rel="external" style="width: 150px;">Patient List</a>
    </div>
</div>

<div id="quit_confirm_dialog" data-role="dialog" data-dismissible="false" data-close-btn="none" data-theme="b" data-overlay-theme="c">
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

<div id="quit_confirm_dialog_sp" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
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

<div id="quit_dialog" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Finished</h1>
    </div>
    <div data-role="content">
        <span>Thank you for filling out the form.  The nurse will collect the device from you.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#vitals_page" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">OK</a>
        </div>
    </div>
</div>

<div id="quit_dialog_sp" data-role="dialog" data-dismissible="false" data-theme="b" data-overlay-theme="c">
    <div data-role="header" data-theme="b">
        <h1>Acabado</h1>
    </div>
    <div data-role="content">
        <span>Gracias por rellenar el formulario. La enfermera recoger&#225; el aparato de usted.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a href="#vitals_page" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Aceptar</a>
        </div>
    </div>
</div>

<div id="question_page_1" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
    </div>
    <div id="content_1" data-role="content">
        <c:if test="${Question1 != null}">
            <strong>${Question1}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question1)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_1" id="QuestionEntry_1_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_1_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_1" id="QuestionEntry_1_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_1_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question2 != null}">
            <br/>
            <strong>${Question2}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question2)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_2" id="QuestionEntry_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_2_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_2" id="QuestionEntry_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question3 != null}">
            <br/>
            <strong>${Question3}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question3)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_3" id="QuestionEntry_3_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_3_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_3" id="QuestionEntry_3_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_3_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question4 != null}">
            <br/>
            <strong>${Question4}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question4)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_4" id="QuestionEntry_4_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_4_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_4" id="QuestionEntry_4_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_4_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question5 != null}">
            <br/>
            <strong>${Question5}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question5)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_5" id="QuestionEntry_5_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_5_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_5" id="QuestionEntry_5_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_5_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <c:when test="${Question6 != null}">
                <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="nextPage(2)" style="width: 150px;">Next</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="finishForm()" style="width: 150px;">Finish</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-inline="true" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a>
    </div>
</div>

<div id="question_page_2" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2Button" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
    </div>
    <div id="content_2" data-role="content">
        <c:if test="${Question6 != null}">
            <strong>${Question6}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question6)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_6" id="QuestionEntry_6_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_6_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_6" id="QuestionEntry_6_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_6_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question7 != null}">
            <br/>
            <strong>${Question7}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question7)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_7" id="QuestionEntry_7_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_7_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_7" id="QuestionEntry_7_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_7_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question8 != null}">
            <br/>
            <strong>${Question8}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question8)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_8" id="QuestionEntry_8_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_8_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_8" id="QuestionEntry_8_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_8_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question9 != null}">
            <br/>
            <strong>${Question9}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question9)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_9" id="QuestionEntry_9_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_9_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_9" id="QuestionEntry_9_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_9_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question10 != null}">
            <br/>
            <strong>${Question10}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question10)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_10" id="QuestionEntry_10_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_10_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_10" id="QuestionEntry_10_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_10_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <a href="#" data-role="button" data-theme="b" onclick="previousPage(1)" style="width: 150px;">Previous</a>
            <c:when test="${Question11 != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage(3)" style="width: 150px;">Next</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 150px;">Finish</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a>
    </div>
</div>

<div id="question_page_3" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage3Button" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
    </div>
    <div id="content_3" data-role="content">
        <c:if test="${Question11 != null}">
            <strong>${Question11}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question11)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_11" id="QuestionEntry_11_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_11_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_11" id="QuestionEntry_11_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_11_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question12 != null}">
            <br/>
            <strong>${Question12}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question12)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_12" id="QuestionEntry_12_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_12_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_12" id="QuestionEntry_12_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_12_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question13 != null}">
            <br/>
            <strong>${Question13}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question13)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_13" id="QuestionEntry_13_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_13_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_13" id="QuestionEntry_13_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_13_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question14 != null}">
            <br/>
            <strong>${Question14}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question14)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_14" id="QuestionEntry_14_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_14_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_14" id="QuestionEntry_14_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_14_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question15 != null}">
            <br/>
            <strong>${Question15}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question15)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_15" id="QuestionEntry_15_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_15_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_15" id="QuestionEntry_15_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_15_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <a href="#" data-role="button" data-theme="b" onclick="previousPage(2)" style="width: 150px;">Previous</a>
            <c:when test="${Question16 != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage(4)" style="width: 150px;">Next</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 150px;">Finish</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Quit</a>
    </div>
</div>

<div id="question_page_4" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage4Button" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">Espa&#241;ol</a>
    </div>
    <div id="content_4" data-role="content">
        <c:if test="${Question16 != null}">
            <strong>${Question16}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question16)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_16" id="QuestionEntry_16_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_16_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_16" id="QuestionEntry_16_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_16_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question17 != null}">
            <br/>
            <strong>${Question17}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question17)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_17" id="QuestionEntry_17_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_17_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_17" id="QuestionEntry_17_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_17_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question18 != null}">
            <br/>
            <strong>${Question18}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question18)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_18" id="QuestionEntry_18_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_18_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_18" id="QuestionEntry_18_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_18_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question19 != null}">
            <br/>
            <strong>${Question19}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question19)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_19" id="QuestionEntry_19_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_19_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_19" id="QuestionEntry_19_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_19_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question20 != null}">
            <br/>
            <strong>${Question20}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readText("${fn:escapeXml(Question20)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_20" id="QuestionEntry_20_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_20_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_20" id="QuestionEntry_20_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_20_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-theme="b" onclick="previousPage(3)" style="width: 150px;">Previous</a>
        <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 150px;">Finish</a>
    </div>
</div>
<div id="question_page_1_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
    </div>
    <div id="content_1_sp" data-role="content">
        <c:if test="${Question1_SP != null}">
            <strong>${Question1_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question1_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_1_2" id="QuestionEntry_1_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_1_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_1_2" id="QuestionEntry_1_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_1_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question2_SP != null}">
            <br/>
            <strong>${Question2_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question2_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_2_2" id="QuestionEntry_2_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_2_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_2_2" id="QuestionEntry_2_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_2_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question3_SP != null}">
            <br/>
            <strong>${Question3_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question3_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_3_2" id="QuestionEntry_3_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_3_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_3_2" id="QuestionEntry_3_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_3_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question4_SP != null}">
            <br/>
            <strong>${Question4_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question4_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_4_2" id="QuestionEntry_4_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_4_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_4_2" id="QuestionEntry_4_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_4_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question5_SP != null}">
            <br/>
            <strong>${Question5_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question5_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_5_2" id="QuestionEntry_5_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_5_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_5_2" id="QuestionEntry_5_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_5_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <c:when test="${Question6_SP != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage(2)" style="width: 150px;">Proximo</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 150px;">Terminar</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Dejar de</a>
    </div>
</div>

<div id="question_page_2_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2SPButton" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
    </div>
    <div id="content_2_sp" data-role="content">
        <c:if test="${Question6_SP != null}">
            <strong>${Question6_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question6_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_6_2" id="QuestionEntry_6_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_6_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_6_2" id="QuestionEntry_6_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_6_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question7_SP != null}">
            <br/>
            <strong>${Question7_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question7_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_7_2" id="QuestionEntry_7_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_7_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_7_2" id="QuestionEntry_7_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_7_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question8_SP != null}">
            <br/>
            <strong>${Question8_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question8_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_8_2" id="QuestionEntry_8_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_8_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_8_2" id="QuestionEntry_8_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_8_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question9_SP != null}">
            <br/>
            <strong>${Question9_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question9_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_9_2" id="QuestionEntry_9_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_9_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_9_2" id="QuestionEntry_9_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_9_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question10_SP != null}">
            <br/>
            <strong>${Question10_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question10_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_10_2" id="QuestionEntry_10_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_10_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_10_2" id="QuestionEntry_10_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_10_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <a href="#" data-role="button" data-theme="b" onclick="previousPage(1)" style="width: 150px;">Anterior</a>
            <c:when test="${Question11_SP != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage(3)" style="width: 150px;">Proximo</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 150px;">Terminar</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Dejar de</a>
    </div>
</div>

<div id="question_page_3_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage3SPButton" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
    </div>
    <div id="content_3_sp" data-role="content">
        <c:if test="${Question11_SP != null}">
            <strong>${Question11_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question11_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_11_2" id="QuestionEntry_11_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_11_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_11_2" id="QuestionEntry_11_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_11_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question12_SP != null}">
            <br/>
            <strong>${Question12_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question12_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_12_2" id="QuestionEntry_12_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_12_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_12_2" id="QuestionEntry_12_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_12_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question13_SP != null}">
            <br/>
            <strong>${Question13_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question13_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_13_2" id="QuestionEntry_13_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_13_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_13_2" id="QuestionEntry_13_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_13_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question14_SP != null}">
            <br/>
            <strong>${Question14_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question14_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_14_2" id="QuestionEntry_14_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_14_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_14_2" id="QuestionEntry_14_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_14_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question15_SP != null}">
            <br/>
            <strong>${Question15_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question15_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_15_2" id="QuestionEntry_15_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_15_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_15_2" id="QuestionEntry_15_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_15_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <a href="#" data-role="button" data-theme="b" onclick="previousPage(2)" style="width: 150px;">Anterior</a>
            <c:when test="${Question16_SP != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage(4)" style="width: 150px;">Proximo</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 150px;">Terminar</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 150px;">Dejar de</a>
    </div>
</div>

<div id="question_page_4_sp" data-role="page" data-theme="b" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage4SPButton" data-role="button" href="#" class="ui-btn-right" onclick="setLanguageFromForm('${patient.givenName}&nbsp;${patient.familyName}', '${patient.birthdate}')">English</a>
    </div>
    <div id="content_4_sp" data-role="content">
        <c:if test="${Question16_SP != null}">
            <strong>${Question16_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question16_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_16_2" id="QuestionEntry_16_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_16_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_16_2" id="QuestionEntry_16_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_16_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question17_SP != null}">
            <br/>
            <strong>${Question17_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question17_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_17_2" id="QuestionEntry_17_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_17_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_17_2" id="QuestionEntry_17_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_17_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question18_SP != null}">
            <br/>
            <strong>${Question18_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question18_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_18_2" id="QuestionEntry_18_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_18_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_18_2" id="QuestionEntry_18_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_18_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question19_SP != null}">
            <br/>
            <strong>${Question19_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question19_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_19_2" id="QuestionEntry_19_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_19_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_19_2" id="QuestionEntry_19_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_19_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question20_SP != null}">
            <br/>
            <strong>${Question20_SP}</strong><a data-role="button" data-inline="true" class="custom-button" onclick='readTextSpanish("${fn:escapeXml(Question20_SP)}")'></a>
            <div data-role="fieldcontain" style="margin-top:0px;">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_20_2" id="QuestionEntry_20_2_Yes" value="Y" data-theme="c" />
                    <label for="QuestionEntry_20_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_20_2" id="QuestionEntry_20_2_No" value="N" data-theme="c" />
                    <label for="QuestionEntry_20_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-theme="b" onclick="previousPage(3)" style="width: 150px;">Anterior</a>
        <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 150px;">Terminar</a>
    </div>
</div>

<div id="vitals_page" data-role="page" data-theme="b">
    <div id="vitals_header" data-role="header" data-position="fixed">
        <a id="backPsfButton" data-role="button" data-icon="back">Questions</a>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <h1>(***Medical Staff Only***)</h1>
    </div>
    <div id="content_vitals" data-role="content">
        <a id='lnkVitalsPasscode' href="#vitals_passcode_dialog" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <a id='lnkPasscodeError' href="#passcodeError" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <div id="vitals_passcode_dialog" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="c">
            <div data-role="header" data-theme="b">
                <h1>Passcode</h1>
            </div>
            <div data-role="content">
                <span>Please enter the passcode to access the staff page.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <input type="number" masktype="password" id="vitals_passcode" name="vitals_passcode" placeholder="Passcode"/>
                    <a href="#" id="goButton" onclick="checkPasscode()" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">Go</a>
                </div>
            </div>
        </div>
        <div id="passcodeError" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="c">
            <div data-role="header" data-theme="b">
                <div>
                    <h3 style="text-align: center;">Passcode Error</h3>
                </div>
            </div>
            <div data-role="content">
                <div id="passcodeErrorResultDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="#vitals_passcode_dialog" data-rel="popup" data-position-to="window" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <div class="ui-grid-a" style="padding-bottom: 20px;">
            <div class="ui-block-a" style="width: 42%">
              <div class="ui-grid-c">
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Height:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="height" name="height"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">${HeightSUnits}</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Weight:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="weight" name="weight"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <c:choose>
                          <c:when test="${WeightSUnits == 'oz.' }">
                            <strong><span style="line-height: 50px;">lb.oz</span></strong>
                          </c:when>
                          <c:otherwise>
                            <strong><span style="line-height: 50px;">${WeightSUnits}</span></strong>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">HC:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="hc" name="hc"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">cm.</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">BP:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="BPS" name="BPS"/></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;padding-left:10px;width: 15px;">
                      <span style="line-height: 50px;"><c:out value="/"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;padding-left:10px;">
                      <span style="line-height: 50px;"><input type="number" id="BPD" name="BPD"/></span>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Temp:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="temp" name="temp"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">deg. F</span></strong>
                  </div>
                  <div class="ui-block-a" style="text-align: center;margin-bottom: 10px;margin-top: 10px;width: 100%;">
                      <fieldset data-role="controlgroup" data-type="horizontal" style="margin: auto;">
                            <input type="radio" name="Oral" id="Temperature_Method_Oral" value="Oral Temp Type" data-theme="c" />
                            <label for="Temperature_Method_Oral">Oral</label>
                            <input type="radio" name="Rectal" id="Temperature_Method_Rectal" value="Rectal Temp Type" data-theme="c" />
                            <label for="Temperature_Method_Rectal">Rectal</label>
                            <input type="radio" name="Axillary" id="Temperature_Method_Axillary" value="Axillary Temp Type" data-theme="c" />
                            <label for="Temperature_Method_Axillary">Axillary</label>
                        </fieldset>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Pulse:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="Pulse" name="Pulse"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">/min.</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">RR:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;p;">
                      <span style="line-height: 50px;"><input type="number" id="RR" name="RR"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;">
                      <span style="line-height: 50px;"></span>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <strong><span style="line-height: 50px;">Pulse Ox:</span></strong>
                  </div>
                  <div class="ui-block-b" style="height: 50px;margin-bottom: 10px;">
                      <span style="line-height: 50px;"><input type="number" id="PulseOx" name="PulseOx"/></span>
                  </div>
                  <div class="ui-block-d" style="height: 50px;text-align: left;padding-left:10px;">
                      <strong><span style="line-height: 50px;">%</span></strong>
                  </div>
              </div>
            </div>
            <div class="ui-block-b" style="width: 58%">
               <div class="ui-grid-b">
                  <div class="ui-block-b" style="height: 25px;text-align: center;width: 100%;">
                      <strong><span>Uncooperative/Unable to Screen:</span></strong>
                  </div>
                  <div class="ui-block-a" style="height: 50px;margin-bottom: 20px;">
                      <span><input type="checkbox" id="NoVision" name="NoVision" value="Y" style="vertical-align: top; margin: 0px;"/><label for="NoVision">Vision</label></span>
                  </div>
                  <div class="ui-block-b" style="height: 50px;margin-bottom: 20px;">
                      <span><input type="checkbox" id="NoHearing" name="NoHearing" value="Y" style="vertical-align: top; margin: 0px;"/><label for="NoHearing">Hearing</label></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;margin-bottom: 20px;">
                      <span><input type="checkbox" id="NoBP" name="NoBP" value="Y" style="vertical-align: top; margin: 0px;"/><label for="NoBP">BP</label></span>
                  </div>
                  <div class="ui-block-a" style="text-align: center;width: 15%"></div>
                  <div class="ui-block-b" style="height: 50px;text-align: center;width: 70%;">
                      <div class="ui-grid-a" style="text-align: center;">
                        <div class="ui-block-a" style="height: 50px;text-align: right;width: 40%;">
                            <strong><span style="line-height: 50px;">Vision Left: 20/</span></strong>
                        </div>
                        <div class="ui-block-b" style="height: 50px;width: 60%;padding-left: 10px;margin-bottom: 10px;">
                            <span><input type="number" id="VisionL" name="VisionL"/></span>
                        </div>
                      </div>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;width: 15%"></div>
                  <div class="ui-block-a" style="text-align: center;width: 15%"></div>
                  <div class="ui-block-b" style="height: 50px;text-align: center;width: 70%;">
                      <div class="ui-grid-a" style="text-align: center;">
                        <div class="ui-block-a" style="height: 50px;text-align: right;width: 40%;">
                            <strong><span style="line-height: 50px;">Vision Right: 20/</span></strong>
                        </div>
                        <div class="ui-block-b" style="height: 50px;width: 60%;padding-left: 10px;margin-bottom: 10px;">
                            <span><input type="number" id="VisionR" name="VisionR"/></span>
                        </div>
                      </div>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;width: 15%"></div>
                  <div class="ui-block-a" style="text-align: center;height: 50px;margin-bottom: 20px;width: 20%"></div>
                  <div class="ui-block-b" style="text-align: center;height: 50px;margin-bottom: 20px;width: 60%">
                      <span><input type="checkbox" id="Vision_Corrected" name="Vision_Corrected" value="Y" style="vertical-align: top; margin: 0px;"/><label for="Vision_Corrected">Vision Corrected?</label></span>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;margin-bottom: 20px;width: 20%"></div>
                  <div class="ui-block-a" style="height: 60px;width: 100%;padding-top:10px;">
                      <div class="ui-grid-a">
                          <div class="ui-block-a" style="text-align: right;width: 50%;padding-right:10px;">
                           <strong><span style="line-height: 50px;">Left Ear @ 25db:</span></strong>
                          </div>
                          <div class="ui-block-b" style="width: 50%;height: 50px;display: table">
                              <div data-role="fieldcontain" style="display: table-cell;">
                                <fieldset data-role="controlgroup" data-type="horizontal">
                                    <input type="radio" name="HearL" id="HearL_pass" value="P" data-theme="c" />
                                    <label for="HearL_pass">P</label>
                                    <input type="radio" name="HearL" id="HearL_fail" value="F" data-theme="c" />
                                    <label for="HearL_fail">F</label>
                                </fieldset>
                              </div>
                          </div>
                      </div>
                  </div>
                  <div class="ui-block-a" style="height: 60px;width: 100%;padding-top:10px;">
                      <div class="ui-grid-a">
                          <div class="ui-block-a" style="text-align: right;width: 50%;padding-right:10px;">
                           <strong><span style="line-height: 50px;">Right Ear @ 25db:</span></strong>
                          </div>
                          <div class="ui-block-b" style="width: 50%;height: 50px;display: table">
                              <div data-role="fieldcontain" style="display: table-cell;">
                                <fieldset data-role="controlgroup" data-type="horizontal">
                                    <input type="radio" name="HearR" id="HearR_pass" value="P" data-theme="c" />
                                    <label for="HearR_pass">P</label>
                                    <input type="radio" name="HearR" id="HearR_fail" value="F" data-theme="c" />
                                    <label for="HearR_fail">F</label>
                                </fieldset>
                              </div>
                          </div>
                      </div>
                  </div>
               </div>
            </div>
        </div>
        <div class="ui-grid-a">
            <div class="ui-block-a">
                <input type="checkbox" id="SickVisit" name="SickVisit" value="Y"/><label for="SickVisit">Sick Visit</label>
            </div>
            <div class="ui-block-b">
                <input type="checkbox" id="RefuseToComplete" name="RefuseToComplete" value="Y"/><label for="RefuseToComplete">Patient refused to complete form</label>
            </div>
            <div class="ui-block-a">
                <input type="checkbox" id="TwoIDsChecked" name="TwoIDsChecked" value="Y"/><label for="TwoIDsChecked">Two IDs checked</label>
            </div>
            <div class="ui-block-b">
                <input type="checkbox" id="LeftWithoutTreatment" name="LeftWithoutTreatment" value="Y"/><label for="LeftWithoutTreatment">Patient left without treatment</label>
            </div>
        </div>
        <div id="validation_error_dialog" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Validation Error</h1>
            </div>
            <div data-role="content">
                <span id="validationError"></span>
                <div style="margin: 0 auto;text-align: center;">
                    <a id="validationOkButton" href="#" data-role="button" data-inline="true" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <div id="invalidLogin" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Invalid Login</h1>
            </div>
            <div data-role="content">
                <div id="loginResultDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a data-inline="true" onclick="showLoginDialog()" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <a id='lnkSubmitError' href="#submitErrorDialog" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <div id="submitErrorDialog" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Error</h1>
            </div>
            <div data-role="content">
                <div id="submitErrorDiv"></div>
                <div style="margin: 0 auto;text-align: center;">
                    <a data-rel="back" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>
        <div id="confirm_submit_dialog" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Confirm</h1>
            </div>
            <div data-role="content">
                <div>Click 'Submit' to permanently submit the form.</div>
                <div style="margin: 0 auto;text-align: center;">
                    <a id="confirm_submit_submit_button" data-role="button" data-theme="b" data-inline="true" onclick="checkAuthentication()" style="width: 150px;">Submit</a>
                    <a id="confirm_submit_cancel_button" data-rel="back" data-role="button" data-theme="b" data-inline="true" style="width: 150px;">Cancel</a>
                </div>
            </div>
        </div>
        <a id='lnkLoadingDialog' href="#loadingDialog" data-rel="popup" data-transition="pop" data-position-to="window" style='display:none;'></a>
        <div id="loadingDialog" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a">
            <div data-role="content">
                <div style="margin: 0 auto;text-align: center;">
                    Loading...
                </div>
            </div>
        </div>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a data-theme="b" data-rol="button" onclick="finishVitals()" rel="external" data-ajax="false" style="width: 150px;">Submit</a>
    </div>
</div>
<input id="formInstances" name="formInstances" type="hidden" value="${formInstances }"/>
<input id="HeightP" name="HeightP" type="hidden"/>
<input id="HeightS" name="HeightS" type="hidden"/>
<input id="WeightP" name="WeightP" type="hidden"/>
<input id="WeightS" name="WeightS" type="hidden"/>
<input id="TempP" name="TempP" type="hidden"/>
<input id="TempS" name="TempS" type="hidden"/>
<input id="HCP" name="HCP" type="hidden"/>
<input id="HCS" name="HCS" type="hidden"/>
<input id="patientId" name="patientId" type="hidden" value="${patient.patientId}"/>
</form>
</body>
</html>
