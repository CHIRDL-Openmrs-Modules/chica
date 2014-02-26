<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/psfMobile.form" />
<html>
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta name="viewport" content="user-scalable=no, initial-scale=1, width=device-width" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.css">
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script>
var page = 0;
var english = false;
$(document).bind("mobileinit", function(){
    $('#confirm_page').on('pageshow', function (event, ui) {
    	$("#QuestionsTab").addClass('ui-btn-active');
    });
});
$(document).ready(function () {
	page = 0;
	setLanguage();
	$("#denyButton").click(function () {
		$("#lnkDenyDialog").click();
    });
	$("#backPsfButton").click(function () {
        page = 0;
        nextPage();
    });
	$("#QuestionsTab").click(function () {
        loadLastPage();
    });
	
	// Initialize all pages because radio button reset will not work properly.
	$("div[type='question_page']").page();
});

function setLanguageFromForm() {
	setLanguage();
	
	// Reset answers
    $("input[id^='QuestionEntry']").prop("checked", false).checkboxradio("refresh");
    
	page = 0;
	nextPage();
}

function setLanguage() {
	english = !english;
	var langButtonText = "Español";
	var parentText = "Parents: Thank you for answering these questions about your child.  The answers will help your doctor provide better quality of care.  If your child is age 12 or older, he/she should answer the questions privately.  Answers are confidential, but if you prefer not to answer that is allowed.  You may want to talk about these questions with your doctor.";
	var instructions = "<p>1) Please return the device back to the front desk if the patient information listed is incorrect.</p><p>2) Please confirm this form is for:<br/>Name: ${patient.givenName}&nbsp;${patient.familyName}<br/>Date of Birth: ${patient.birthdate}</p>";
	var confirmButtonText = "Confirm";
	var denyButtonText = "Deny";
	var denyConfirm = "If you are sure the incorrect patient is displayed, Press 'OK' and return the device to the receptionist.";
	var okButtonText = "OK";
	var cancelButtonText = "Cancel";
	var quitConfirmText = "Are you sure you want to quit?";
	var yesButtonText = "Yes";
	var noButtonText = "No";
	var thankYouText = "Thank you for filling out the form.  The nurse will collect the device from you.";
	if (!english) {
		langButtonText = "English";
		parentText = "Padres de familia: Muchas gracias por tomarse la molestia de contestar las siguientes preguntas acerca de su nino(a).  Las respuestas de estas preguntas seran: ayundar a su doctor a dar mejor atencion medica.  Si su nino(a) tiene 12 anos o mas, por favor su nino(a) debe contestar las preguntas el (ella) solo(a).  Sus respuestas seran completamente privadas.  No necesita contestar ninguna pregunta que no desee contestar.  Si usted tiene preguntas acerca de este cuestionario, haga el favor de hablar sobre ellas con su doctor.  Por favor llene los circulos de la forma mas completa que le sea posible con un lapiz o lapiz tinta.";
		instructions = "<p>1) Por favor devuelva el aparato a la recepción si la información del paciente que aparece es incorrecta.</p><p>2) Por favor, confirme esta forma es para:<br/>Nombre: ${patient.givenName}&nbsp;${patient.familyName}<br/>Fecha de nacimiento: ${patient.birthdate}</p>";
		confirmButtonText = "Confirmar";
		denyButtonText = "Negar";
		denyConfirm = "Si está seguro de que el paciente incorrecta aparece, pulse \'Aceptar\' y devolver el dispositivo a la recepcionista.";
		okButtonText = "Aceptar";
		cancelButtonText = "Cancelar";
		quitConfirmText = "¿Seguro que quieres salir?";
		yesButtonText = "Si";
		thankYouText = "Gracias por rellenar el formulario. La enfermera recogerá el aparato de usted.";
	}
	
	$("#confirmLangButton .ui-btn-text").text(langButtonText);
	$("#parentText").text(parentText);
	$("#instructions").html(instructions);
	$("#confirmButton .ui-btn-text").text(confirmButtonText);
	$("#denyButton .ui-btn-text").text(denyButtonText);
	$("#denyConfirm").text(denyConfirm);
	$("#denyOkButton .ui-btn-text").text(okButtonText);
	$("#denyCancelButton .ui-btn-text").text(cancelButtonText);
	$("#quitConfirm").text(quitConfirmText);
	$("#quitConfirmYesButton .ui-btn-text").text(yesButtonText);
	$("#quitConfirmNoButton .ui-btn-text").text(noButtonText);
	$("#thankYouText").text(thankYouText);
	$("#quitDialogOkButton .ui-btn-text").text(okButtonText);
}

function nextPage() {
	page++;
	var newPage = '#question_page_' + page;
	if (!english) {
		newPage = newPage + "_sp";
	}
	
	$.mobile.changePage(newPage, { transition: "flip", reverse: false });
}

function previousPage() {
    page--;
    var newPage = '#question_page_' + page;
    if (!english) {
        newPage = newPage + "_sp";
    }
    
    $.mobile.changePage(newPage, { transition: "flip", reverse: true });
}

function loadLastPage() {
	var newPage = "";
	if (page == 0) {
		newPage = "#confirm_page";
	} else {
	    newPage = '#question_page_' + page;
	    if (!english) {
	        newPage = newPage + "_sp";
	    }
	}
    
    $.mobile.changePage(newPage, null);
}

function finishForm() {
	$("#lnkQuitDialog").click();
	//$("#psfForm").submit();
}

function confirmQuit() {
	$("#lnkQuitConfirmDialog").click();
}

function finishVitals() {
	if (!validate()) {
		return;
	}
	
	populateValues();
	//document.getElementById('psfForm').submit();
}

function validate() {
	var height = document.getElementById("height").value;
	if (!validateNumericField(height, 4, 3, 1)) {
		displayValidationError("Height", new Array("999.9", "999"));
		return false;
	}
	
	var weight = document.getElementById("weight").value;
    if (!validateNumericField(weight, 5, 3, 2)) {
        displayValidationError("Weight", new Array("999.99", "999"));
        return false;
    }
    
    var hc = document.getElementById("hc").value;
    if (!validateNumericField(hc, 4, 3, 1)) {
        displayValidationError("HC", new Array("999.9", "999"));
        return false;
    }
    
    var systBp = document.getElementById("BPS").value;
    if (!validateNumericField(systBp, 3, 3, 0)) {
        displayValidationError("Systolic BP", new Array("999"));
        return false;
    }
    
    var diastBp = document.getElementById("BPD").value;
    if (!validateNumericField(diastBp, 3, 3, 0)) {
        displayValidationError("Diastolic BP", new Array("999"));
        return false;
    }
    
    var temp = document.getElementById("temp").value;
    if (!validateNumericField(temp, 4, 3, 1)) {
        displayValidationError("Temp", new Array("999.9", "999"));
        return false;
    }
    
    var pulse = document.getElementById("Pulse").value;
    if (!validateNumericField(pulse, 3, 3, 0)) {
        displayValidationError("Pulse", new Array("999"));
        return false;
    }
    
    var rr = document.getElementById("RR").value;
    if (!validateNumericField(rr, 3, 3, 0)) {
        displayValidationError("RR", new Array("999"));
        return false;
    }
    
    var pulseOx = document.getElementById("PulseOx").value;
    if (!validateNumericField(pulseOx, 3, 3, 0)) {
        displayValidationError("Pulse Ox", new Array("999"));
        return false;
    }
    
    var visionL = document.getElementById("VisionL").value;
    if (!validateNumericField(visionL, 3, 3, 0)) {
        displayValidationError("Vision Left", new Array("999"));
        return false;
    }
    
    var visionR = document.getElementById("VisionR").value;
    if (!validateNumericField(visionR, 3, 3, 0)) {
        displayValidationError("Vision Right", new Array("999"));
        return false;
    }
    
    return true;
}

function validateNumericField(value, totalPlaces, beforeDec, afterDec) {
	var index = value.indexOf(".");
    if (index < 0) {
        // The value's length cannot be greater than the length available before the decimal.
        if (value.length > beforeDec) {
            return false;
        }
    } else {
        // Check the length before the decimal
        var data = value.substring(0, index);
        if (data.length > beforeDec) {
            return false;
        }
        
        // Check the length after the decimal
        data = value.substring(index + 1, value.length);
        if (data.length > afterDec) {
            return false;
        }
    }
    
    return true;
}

function displayValidationError(fieldName, expectedFormats) {
	var message = fieldName + " can only be in format ";
    for (var i = 0; i < expectedFormats.length; i++) {
        if (i == 0) {
            message+= expectedFormats[i];
        } else {
            message+= " or " + expectedFormats[i];
        }
    }
    
    message += ".";    
    $("#validationError").html(message);
    $("#lnkValidationError").click();
}

function populateValues() {
	setDecimalValueFields(document.getElementById("height").value, "HeightP", "HeightS");
	setDecimalValueFields(document.getElementById("weight").value, "WeightP", "WeightS");
	setDecimalValueFields(document.getElementById("temp").value, "TempP", "TempS");
	setDecimalValueFields(document.getElementById("hc").value, "HCP", "HCS");
}

function setDecimalValueFields(value, beforeDecName, afterDecName) {
	if (value != null && value.trim().length > 0) {
		var pValue = value;
	    var index = value.indexOf(".");
	    if (index >= 0) {
	        pValue = value.substring(0, index);
	        document.getElementById(beforeDecName).value = pValue;
	        if (index != value.length - 1) {
	            var sValue = value.substring(index + 1, value.length);
	            document.getElementById(afterDecName).value = sValue;
	        } else {
	        	document.getElementById(afterDecName).value = null;
	        }
	    } else {
	    	document.getElementById(beforeDecName).value = pValue;
	    	document.getElementById(afterDecName).value = null;
	    }
	} else {
		document.getElementById(beforeDecName).value = null;
        document.getElementById(afterDecName).value = null;
	}
} 
</script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.mobile-1.3.2.min.js"></script>
</head>
<body>
<form id="psfForm" method="POST">
<div data-role="page" id="confirm_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="confirmLangButton" data-role="button" href="#" class="ui-btn-right" onclick="setLanguage()"></a>
        <div data-role="navbar">
	        <ul>
	            <li><a id="QuestionsTab" href="#">Questions</a></li>
	            <li><a id="VitalsTab" href="#vitals_page">Vitals</a></li>
	        </ul>
    </div>
    </div>

    <div data-role="content" >
        <strong><span id="parentText"></span></strong>
        <div><br/></div>
        <div style="border-top:1px #000000 solid;position:fixed;height:2px;width:95% "></div>
        <span id="instructions"></span>
    </div><!-- /content -->
    
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a id="confirmButton" href="#" data-role="button" data-theme="b" onclick="nextPage()" style="width: 100px;"></a>
        <a id="denyButton" href="#" data-role="button" data-theme="b" style="width: 100px;"></a>
    </div>
    
    <a id='lnkDenyDialog' href="#deny_dialog" data-rel="dialog" data-transition="pop" style='display:none;'></a>
    
</div><!-- /page one -->

<div id="deny_dialog" data-url="deny_dialog" data-role="page">
    <div data-role="content">
        <span id="denyConfirm"></span>
        <div style="margin: 0 auto;text-align: center;">
	        <a id="denyOkButton" href="${pageContext.request.contextPath}/module/chica/greaseBoardMobile.form" data-inline="true" data-theme="b" data-role="button" rel="external">OK</a>
	        <a id="denyCancelButton" href="#confirm_page" data-inline="true" data-rel="back" data-role="button" data-theme="b">Cancel</a>
        </div>
    </div>
</div>

<div id="quit_confirm_dialog" data-url="quit_confirm_dialog" data-role="page">
    <div data-role="content">
        <span id="quitConfirm"></span>
        <div style="margin: 0 auto;text-align: center;">
            <a id="quitConfirmYesButton" href="#quit_dialog" data-role="button" data-rel="dialog" data-inline="true" data-theme="b" style="width: 100px;">Yes</a>
            <a id="quitConfirmNoButton" href="#" data-rel="back" data-role="button" data-inline="true" data-theme="b" style="width: 100px;">No</a>
        </div>
    </div>
    <a id='lnkQuitConfirmDialog' href="#quit_confirm_dialog" data-rel="dialog" data-transition="pop" style='display:none;'></a>
</div>

<div id="quit_dialog" data-url="quit_dialog" data-role="page">
    <div data-role="content">
        <span id="thankYouText">Thank you for filling out the form.  The nurse will collect the device from you.</span>
        <div style="margin: 0 auto;text-align: center;">
            <a id="quitDialogOkButton"href="#vitals_page" data-role="button" data-inline="true" data-theme="b" style="width: 100px;">OK</a>
        </div>
    </div>
    <a id='lnkQuitDialog' href="#quit_dialog" data-rel="dialog" data-transition="pop" style='display:none;'></a>
</div>

<div id="validation_error_dialog" data-url="validation_error_dialog" data-role="page">
    <div data-role="content">
        <span id="validationError"></span>
        <div style="margin: 0 auto;text-align: center;">
            <a id="validationOkButton" href="#" data-rel="back" data-role="button" data-inline="true" data-theme="b" style="width: 100px;">OK</a>
        </div>
    </div>
    <a id='lnkValidationError' href="#validation_error_dialog" data-rel="dialog" data-transition="pop" style='display:none;'></a>
</div>

<div id="question_page_1" data-url="question_page_1" data-role="page" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1Button" href="#" class="ui-btn-right" onclick="setLanguageFromForm()">Español</a>
    </div>
    <div id="content_1" data-role="content">
        <c:if test="${Question1 != null}">
            <p>${Question1}</p>
            <div data-role="fieldcontain">
	            <fieldset data-role="controlgroup" data-type="horizontal">
	                <input type="radio" name="QuestionEntry_1" id="QuestionEntry_1_Yes" value="Y" />
                    <label for="QuestionEntry_1_Yes">Yes</label>
					<input type="radio" name="QuestionEntry_1" id="QuestionEntry_1_No" value="N" />
                    <label for="QuestionEntry_1_No">No</label>
				</fieldset>
			</div>
        </c:if>
        <c:if test="${Question2 != null}">
            <br/>
            <p>${Question2}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_2" id="QuestionEntry_2_Yes" value="Y" />
                    <label for="QuestionEntry_2_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_2" id="QuestionEntry_2_No" value="N" />
                    <label for="QuestionEntry_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question3 != null}">
            <br/>
            <p>${Question3}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_3" id="QuestionEntry_3_Yes" value="Y" />
                    <label for="QuestionEntry_3_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_3" id="QuestionEntry_3_No" value="N" />
                    <label for="QuestionEntry_3_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question4 != null}">
            <br/>
            <p>${Question4}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_4" id="QuestionEntry_4_Yes" value="Y" />
                    <label for="QuestionEntry_4_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_4" id="QuestionEntry_4_No" value="N" />
                    <label for="QuestionEntry_4_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question5 != null}">
            <br/>
            <p>${Question5}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_5" id="QuestionEntry_5_Yes" value="Y" />
                    <label for="QuestionEntry_5_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_5" id="QuestionEntry_5_No" value="N" />
                    <label for="QuestionEntry_5_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <c:when test="${Question6 != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage()" style="width: 100px;">Next</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 100px;">Finish</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 100px;">Quit</a>
    </div>
</div>

<div id="question_page_2" data-url="question_page_2" data-role="page" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2Button" href="#" class="ui-btn-right" onclick="setLanguageFromForm()">Español</a>
    </div>
    <div id="content_2" data-role="content">
        <c:if test="${Question6 != null}">
            <p>${Question6}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_6" id="QuestionEntry_6_Yes" value="Y" />
                    <label for="QuestionEntry_6_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_6" id="QuestionEntry_6_No" value="N" />
                    <label for="QuestionEntry_6_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question7 != null}">
            <br/>
            <p>${Question7}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_7" id="QuestionEntry_7_Yes" value="Y" />
                    <label for="QuestionEntry_7_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_7" id="QuestionEntry_7_No" value="N" />
                    <label for="QuestionEntry_7_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question8 != null}">
            <br/>
            <p>${Question8}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_8" id="QuestionEntry_8_Yes" value="Y" />
                    <label for="QuestionEntry_8_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_8" id="QuestionEntry_8_No" value="N" />
                    <label for="QuestionEntry_8_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question9 != null}">
            <br/>
            <p>${Question9}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_9" id="QuestionEntry_9_Yes" value="Y" />
                    <label for="QuestionEntry_9_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_9" id="QuestionEntry_9_No" value="N" />
                    <label for="QuestionEntry_9_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question10 != null}">
            <br/>
            <p>${Question10}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_10" id="QuestionEntry_10_Yes" value="Y" />
                    <label for="QuestionEntry_10_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_10" id="QuestionEntry_10_No" value="N" />
                    <label for="QuestionEntry_10_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <a href="#" data-role="button" data-theme="b" onclick="previousPage()" style="width: 100px;">Previous</a>
            <c:when test="${Question11 != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage()" style="width: 100px;">Next</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 100px;">Finish</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 100px;">Quit</a>
    </div>
</div>

<div id="question_page_3" data-url="question_page_3" data-role="page" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage3Button" href="#" class="ui-btn-right" onclick="setLanguageFromForm()">Español</a>
    </div>
    <div id="content_3" data-role="content">
        <c:if test="${Question11 != null}">
            <p>${Question11}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_11" id="QuestionEntry_11_Yes" value="Y" />
                    <label for="QuestionEntry_11_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_11" id="QuestionEntry_11_No" value="N" />
                    <label for="QuestionEntry_11_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question12 != null}">
            <br/>
            <p>${Question12}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_12" id="QuestionEntry_12_Yes" value="Y" />
                    <label for="QuestionEntry_12_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_12" id="QuestionEntry_12_No" value="N" />
                    <label for="QuestionEntry_12_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question13 != null}">
            <br/>
            <p>${Question13}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_13" id="QuestionEntry_13_Yes" value="Y" />
                    <label for="QuestionEntry_13_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_13" id="QuestionEntry_13_No" value="N" />
                    <label for="QuestionEntry_13_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question14 != null}">
            <br/>
            <p>${Question14}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_14" id="QuestionEntry_14_Yes" value="Y" />
                    <label for="QuestionEntry_14_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_14" id="QuestionEntry_14_No" value="N" />
                    <label for="QuestionEntry_14_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question15 != null}">
            <br/>
            <p>${Question15}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_15" id="QuestionEntry_15_Yes" value="Y" />
                    <label for="QuestionEntry_15_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_15" id="QuestionEntry_15_No" value="N" />
                    <label for="QuestionEntry_15_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <a href="#" data-role="button" data-theme="b" onclick="previousPage()" style="width: 100px;">Previous</a>
            <c:when test="${Question16 != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage()" style="width: 100px;">Next</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 100px;">Finish</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 100px;">Quit</a>
    </div>
</div>

<div id="question_page_4" data-url="question_page_4" data-role="page" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage4Button" href="#" class="ui-btn-right" onclick="setLanguageFromForm()">Español</a>
    </div>
    <div id="content_4" data-role="content">
        <c:if test="${Question16 != null}">
            <p>${Question16}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_16" id="QuestionEntry_16_Yes" value="Y" />
                    <label for="QuestionEntry_16_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_16" id="QuestionEntry_16_No" value="N" />
                    <label for="QuestionEntry_16_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question17 != null}">
            <br/>
            <p>${Question17}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_17" id="QuestionEntry_17_Yes" value="Y" />
                    <label for="QuestionEntry_17_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_17" id="QuestionEntry_17_No" value="N" />
                    <label for="QuestionEntry_17_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question18 != null}">
            <br/>
            <p>${Question18}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_18" id="QuestionEntry_18_Yes" value="Y" />
                    <label for="QuestionEntry_18_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_18" id="QuestionEntry_18_No" value="N" />
                    <label for="QuestionEntry_18_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question19 != null}">
            <br/>
            <p>${Question19}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_19" id="QuestionEntry_19_Yes" value="Y" />
                    <label for="QuestionEntry_19_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_19" id="QuestionEntry_19_No" value="N" />
                    <label for="QuestionEntry_19_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question20 != null}">
            <br/>
            <p>${Question20}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_20" id="QuestionEntry_20_Yes" value="Y" />
                    <label for="QuestionEntry_20_Yes">Yes</label>
                    <input type="radio" name="QuestionEntry_20" id="QuestionEntry_20_No" value="N" />
                    <label for="QuestionEntry_20_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-theme="b" onclick="previousPage()" style="width: 100px;">Previous</a>
        <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 100px;">Finish</a>
    </div>
</div>
<div id="question_page_1_sp" data-url="question_page_1_sp" data-role="page" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage1SPButton" href="#" class="ui-btn-right" onclick="setLanguageFromForm()">English</a>
    </div>
    <div id="content_1_sp" data-role="content">
        <c:if test="${Question1_SP != null}">
            <p>${Question1_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_1_2" id="QuestionEntry_1_2_Yes" value="Y" />
                    <label for="QuestionEntry_1_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_1_2" id="QuestionEntry_1_2_No" value="N" />
                    <label for="QuestionEntry_1_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question2_SP != null}">
            <br/>
            <p>${Question2_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_2_2" id="QuestionEntry_2_2_Yes" value="Y" />
                    <label for="QuestionEntry_2_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_2_2" id="QuestionEntry_2_2_No" value="N" />
                    <label for="QuestionEntry_2_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question3_SP != null}">
            <br/>
            <p>${Question3_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_3_2" id="QuestionEntry_3_2_Yes" value="Y" />
                    <label for="QuestionEntry_3_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_3_2" id="QuestionEntry_3_2_No" value="N" />
                    <label for="QuestionEntry_3_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question4_SP != null}">
            <br/>
            <p>${Question4_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_4_2" id="QuestionEntry_4_2_Yes" value="Y" />
                    <label for="QuestionEntry_4_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_4_2" id="QuestionEntry_4_2_No" value="N" />
                    <label for="QuestionEntry_4_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question5_SP != null}">
            <br/>
            <p>${Question5_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_5_2" id="QuestionEntry_5_2_Yes" value="Y" />
                    <label for="QuestionEntry_5_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_5_2" id="QuestionEntry_5_2_No" value="N" />
                    <label for="QuestionEntry_5_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <c:when test="${Question6_SP != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage()" style="width: 100px;">Proximo</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 100px;">Terminar</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 100px;">Dejar de</a>
    </div>
</div>

<div id="question_page_2_sp" data-url="question_page_2_sp" data-role="page" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage2SPButton" href="#" class="ui-btn-right" onclick="setLanguageFromForm()">English</a>
    </div>
    <div id="content_2_sp" data-role="content">
        <c:if test="${Question6_SP != null}">
            <p>${Question6_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_6_2" id="QuestionEntry_6_2_Yes" value="Y" />
                    <label for="QuestionEntry_6_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_6_2" id="QuestionEntry_6_2_No" value="N" />
                    <label for="QuestionEntry_6_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question7_SP != null}">
            <br/>
            <p>${Question7_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_7_2" id="QuestionEntry_7_2_Yes" value="Y" />
                    <label for="QuestionEntry_7_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_7_2" id="QuestionEntry_7_2_No" value="N" />
                    <label for="QuestionEntry_7_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question8_SP != null}">
            <br/>
            <p>${Question8_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_8_2" id="QuestionEntry_8_2_Yes" value="Y" />
                    <label for="QuestionEntry_8_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_8_2" id="QuestionEntry_8_2_No" value="N" />
                    <label for="QuestionEntry_8_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question9_SP != null}">
            <br/>
            <p>${Question9_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_9_2" id="QuestionEntry_9_2_Yes" value="Y" />
                    <label for="QuestionEntry_9_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_9_2" id="QuestionEntry_9_2_No" value="N" />
                    <label for="QuestionEntry_9_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question10_SP != null}">
            <br/>
            <p>${Question10_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_10_2" id="QuestionEntry_10_2_Yes" value="Y" />
                    <label for="QuestionEntry_10_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_10_2" id="QuestionEntry_10_2_No" value="N" />
                    <label for="QuestionEntry_10_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <a href="#" data-role="button" data-theme="b" onclick="previousPage()" style="width: 100px;">Anterior</a>
            <c:when test="${Question11_SP != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage()" style="width: 100px;">Proximo</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 100px;">Terminar</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 100px;">Dejar de</a>
    </div>
</div>

<div id="question_page_3_sp" data-url="question_page_3_sp" data-role="page" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage3SPButton" href="#" class="ui-btn-right" onclick="setLanguageFromForm()">English</a>
    </div>
    <div id="content_3_sp" data-role="content">
        <c:if test="${Question11_SP != null}">
            <p>${Question11_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_11_2" id="QuestionEntry_11_2_Yes" value="Y" />
                    <label for="QuestionEntry_11_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_11_2" id="QuestionEntry_11_2_No" value="N" />
                    <label for="QuestionEntry_11_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question12_SP != null}">
            <br/>
            <p>${Question12_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_12_2" id="QuestionEntry_12_2_Yes" value="Y" />
                    <label for="QuestionEntry_12_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_12_2" id="QuestionEntry_12_2_No" value="N" />
                    <label for="QuestionEntry_12_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question13_SP != null}">
            <br/>
            <p>${Question13_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_13_2" id="QuestionEntry_13_2_Yes" value="Y" />
                    <label for="QuestionEntry_13_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_13_2" id="QuestionEntry_13_2_No" value="N" />
                    <label for="QuestionEntry_13_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question14_SP != null}">
            <br/>
            <p>${Question14_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_14_2" id="QuestionEntry_14_2_Yes" value="Y" />
                    <label for="QuestionEntry_14_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_14_2" id="QuestionEntry_14_2_No" value="N" />
                    <label for="QuestionEntry_14_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question15_SP != null}">
            <br/>
            <p>${Question15_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_15_2" id="QuestionEntry_15_2_Yes" value="Y" />
                    <label for="QuestionEntry_15_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_15_2" id="QuestionEntry_15_2_No" value="N" />
                    <label for="QuestionEntry_15_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <c:choose>
            <a href="#" data-role="button" data-theme="b" onclick="previousPage()" style="width: 100px;">Anterior</a>
            <c:when test="${Question16_SP != null}">
                <a href="#" data-role="button" data-theme="b" onclick="nextPage()" style="width: 100px;">Proximo</a>
            </c:when>
            <c:otherwise>
                <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 100px;">Terminar</a>
            </c:otherwise>
        </c:choose>
        <a href="#" data-role="button" data-theme="b" onclick="confirmQuit()" style="width: 100px;">Dejar de</a>
    </div>
</div>

<div id="question_page_4_sp" data-url="question_page_4_sp" data-role="page" type="question_page">
    <div data-role="header" data-position="fixed">
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
        <a id="langPage4SPButton" href="#" class="ui-btn-right" onclick="setLanguageFromForm()">English</a>
    </div>
    <div id="content_4_sp" data-role="content">
        <c:if test="${Question16_SP != null}">
            <p>${Question16_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_16_2" id="QuestionEntry_16_2_Yes" value="Y" />
                    <label for="QuestionEntry_16_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_16_2" id="QuestionEntry_16_2_No" value="N" />
                    <label for="QuestionEntry_16_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question17_SP != null}">
            <br/>
            <p>${Question17_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_17_2" id="QuestionEntry_17_2_Yes" value="Y" />
                    <label for="QuestionEntry_17_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_17_2" id="QuestionEntry_17_2_No" value="N" />
                    <label for="QuestionEntry_17_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question18_SP != null}">
            <br/>
            <p>${Question18_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_18_2" id="QuestionEntry_18_2_Yes" value="Y" />
                    <label for="QuestionEntry_18_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_18_2" id="QuestionEntry_18_2_No" value="N" />
                    <label for="QuestionEntry_18_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question19_SP != null}">
            <br/>
            <p>${Question19_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_19_2" id="QuestionEntry_19_2_Yes" value="Y" />
                    <label for="QuestionEntry_19_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_19_2" id="QuestionEntry_19_2_No" value="N" />
                    <label for="QuestionEntry_19_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
        <c:if test="${Question20_SP != null}">
            <br/>
            <p>${Question20_SP}</p>
            <div data-role="fieldcontain">
                <fieldset data-role="controlgroup" data-type="horizontal">
                    <input type="radio" name="QuestionEntry_20_2" id="QuestionEntry_20_2_Yes" value="Y" />
                    <label for="QuestionEntry_20_2_Yes">Si</label>
                    <input type="radio" name="QuestionEntry_20_2" id="QuestionEntry_20_2_No" value="N" />
                    <label for="QuestionEntry_20_2_No">No</label>
                </fieldset>
            </div>
        </c:if>
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-role="button" data-theme="b" onclick="previousPage()" style="width: 100px;">Anterior</a>
        <a href="#" data-role="button" data-theme="b" onclick="finishForm()" style="width: 100px;">Terminar</a>
    </div>
</div>

<div id="vitals_page" data-url="vitals_page" data-role="page">
    <div data-role="header" data-position="fixed">
        <a id="backPsfButton" data-role="button" data-icon="back">Questions</a>
        <h1>${patient.givenName}&nbsp;${patient.familyName}</h1>
    </div>
    <div id="content_vitals" data-role="content">
        <div class="ui-grid-a" style="padding-bottom: 20px;">
		    <div class="ui-block-a" style="width: 40%">
		      <div class="ui-grid-b">
		          <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
		              <span style="line-height: 50px;">Height:</span>
		          </div>
		          <div class="ui-block-b" style="height: 50px;">
		              <span style="line-height: 50px;"><input type="number" id="height" name="height"/></span>
		          </div>
		          <div class="ui-block-c" style="height: 50px;text-align: left;padding-left:10px;">
		              <span style="line-height: 50px;">${HeightSUnits}</span>
		          </div>
		          <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <span style="line-height: 50px;">Weight:</span>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="weight" name="weight"/></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;text-align: left;padding-left:10px;">
                      <c:choose>
                          <c:when test="${WeightSUnits == 'oz.' }">
                            <span style="line-height: 50px;">lb.oz</span>
                          </c:when>
                          <c:otherwise>
                            <span style="line-height: 50px;">${WeightSUnits}</span>
                          </c:otherwise>
                      </c:choose>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <span style="line-height: 50px;">HC:</span>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="hc" name="hc"/></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;text-align: left;padding-left:10px;">
                      <span style="line-height: 50px;">cm.</span>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <span style="line-height: 50px;">BP:</span>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="BPS" name="BPS"/></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;padding-left:10px;">
                      <span style="line-height: 50px;"><input type="number" id="BPD" name="BPD"/></span>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <span style="line-height: 50px;">Temp:</span>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="temp" name="temp"/></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;text-align: left;padding-left:10px;">
                      <span style="line-height: 50px;">def. F</span>
                  </div>
                  <div class="ui-block-a" style="text-align: center;margin-bottom: 10px;width: 100%;">
                      <fieldset data-role="controlgroup" data-type="horizontal" style="margin: auto;">
                            <input type="radio" name="Temperature_Method" id="Temperature_Method_Oral" value="Oral Temp Type" />
                            <label for="Temperature_Method_Oral">Oral</label>
                            <input type="radio" name="Temperature_Method" id="Temperature_Method_Rectal" value="Rectal Temp Type" />
                            <label for="Temperature_Method_Rectal">Rectal</label>
                            <input type="radio" name="Temperature_Method" id="Temperature_Method_Axillary" value="Axillary Temp Type" />
                            <label for="Temperature_Method_Axillary">Axillary</label>
                        </fieldset>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <span style="line-height: 50px;">Pulse:</span>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="Pulse" name="Pulse"/></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;text-align: left;padding-left:10px;">
                      <span style="line-height: 50px;">/min.</span>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <span style="line-height: 50px;">RR:</span>
                  </div>
                  <div class="ui-block-b" style="height: 50px;p;">
                      <span style="line-height: 50px;"><input type="number" id="RR" name="RR"/></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;text-align: left;">
                      <span style="line-height: 50px;"></span>
                  </div>
                  <div class="ui-block-a" style="height: 50px;text-align: right;padding-right:10px;">
                      <span style="line-height: 50px;">Pulse Ox:</span>
                  </div>
                  <div class="ui-block-b" style="height: 50px;">
                      <span style="line-height: 50px;"><input type="number" id="PulseOx" name="PulseOx"/></span>
                  </div>
                  <div class="ui-block-c" style="height: 50px;text-align: left;padding-left:10px;">
                      <span style="line-height: 50px;">%</span>
                  </div>
		      </div>
		    </div>
		    <div class="ui-block-b" style="width: 60%">
		       <div class="ui-grid-b">
                  <div class="ui-block-b" style="height: 25px;text-align: center;width: 100%;">
                      <span>Uncooperative/Unable to Screen:</span>
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
                            <span style="line-height: 50px;">Vision Left: 20/</span>
                        </div>
                        <div class="ui-block-b" style="height: 50px;width: 60%;padding-left: 10px">
                            <span><input type="number" id="VisionL" name="VisionL"/></span>
                        </div>
                      </div>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;width: 15%"></div>
                  <div class="ui-block-a" style="text-align: center;width: 15%"></div>
                  <div class="ui-block-b" style="height: 50px;text-align: center;width: 70%;">
                      <div class="ui-grid-a" style="text-align: center;">
                        <div class="ui-block-a" style="height: 50px;text-align: right;width: 40%;">
                            <span style="line-height: 50px;">Vision Right: 20/</span>
                        </div>
                        <div class="ui-block-b" style="height: 50px;width: 60%;padding-left: 10px">
                            <span><input type="number" id="VisionR" name="VisionR"/></span>
                        </div>
                      </div>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;width: 15%"></div>
                  <div class="ui-block-a" style="text-align: center;height: 50px;margin-bottom: 20px;width: 25%"></div>
                  <div class="ui-block-b" style="text-align: center;height: 50px;margin-bottom: 20px;width: 50%">
                      <span><input type="checkbox" id="Vision_Corrected" name="Vision_Corrected" value="Y" style="vertical-align: top; margin: 0px;"/><label for="Vision_Corrected">Vision Corrected?</label></span>
                  </div>
                  <div class="ui-block-c" style="text-align: center;height: 50px;margin-bottom: 20px;width: 25%"></div>
                  <div class="ui-block-a" style="height: 60px;width: 100%;padding-top:10px;">
                      <div class="ui-grid-a">
                          <div class="ui-block-a" style="text-align: right;width: 50%;padding-right:10px;">
	                       <span style="line-height: 50px;">Left Ear @ 25db:</span>
	                      </div>
	                      <div class="ui-block-b" style="width: 50%;height: 50px;display: table">
		                      <div data-role="fieldcontain" style="display: table-cell;">
		                        <fieldset data-role="controlgroup" data-type="horizontal">
		                            <input type="radio" name="HearL" id="HearL_pass" value="P" />
		                            <label for="HearL_pass">P</label>
		                            <input type="radio" name="HearL" id="HearL_fail" value="F"/>
		                            <label for="HearL_fail">F</label>
		                        </fieldset>
		                      </div>
	                      </div>
                      </div>
                  </div>
                  <div class="ui-block-a" style="height: 60px;width: 100%;padding-top:10px;">
                      <div class="ui-grid-a">
                          <div class="ui-block-a" style="text-align: right;width: 50%;padding-right:10px;">
                           <span style="line-height: 50px;">Right Ear @ 25db:</span>
                          </div>
                          <div class="ui-block-b" style="width: 50%;height: 50px;display: table">
                              <div data-role="fieldcontain" style="display: table-cell;">
                                <fieldset data-role="controlgroup" data-type="horizontal">
                                    <input type="radio" name="HearR" id="HearR_pass" value="P" />
                                    <label for="HearR_pass">P</label>
                                    <input type="radio" name="HearR" id="HearR_fail" value="F"/>
                                    <label for="HearR_fail">F</label>
                                </fieldset>
                              </div>
                          </div>
                      </div>
                  </div>
               </div>
		    </div>
	    </div>
	    <div class="ui-grid-a"">
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
    </div>
    <div data-role="footer" style="text-align:center;padding-bottom:20px;padding-top:20px;">
        <a href="#" data-theme="b" onclick="finishVitals()" style="width: 100px;">Submit</a>
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
</form>
</body>
</html>
