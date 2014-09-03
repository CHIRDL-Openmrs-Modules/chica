var english = false;
var formInstance = null;
$(document).ready(function () {
	$(document).ajaxStart(function() {
	    $.mobile.loading('show');
	});

	$(document).ajaxStop(function() {
	    $.mobile.loading('hide');
	});
	
    $("#denyButton").click(function () {
    	if (english) {
    	    $( "#deny_dialog" ).popup( "open", { transition: "pop"} );
    	} else {
    		$( "#deny_dialog_sp" ).popup( "open", { transition: "pop"} );
    	}
    });
    $("#backPsfButton").click(function () {
        nextPage(1);
        return false;
    });
    $("#Temperature_Method_Oral").click(function () {
    	$("#Temperature_Method_Rectal").attr("checked", false).checkboxradio("refresh");
    	$("#Temperature_Method_Axillary").attr("checked", false).checkboxradio("refresh");
    });
    $("#Temperature_Method_Rectal").click(function () {
    	$("#Temperature_Method_Oral").attr("checked", false).checkboxradio("refresh");
        $("#Temperature_Method_Axillary").attr("checked", false).checkboxradio("refresh");
    });
    $("#Temperature_Method_Axillary").click(function () {
    	$("#Temperature_Method_Rectal").attr("checked", false).checkboxradio("refresh");
        $("#Temperature_Method_Oral").attr("checked", false).checkboxradio("refresh");
    });
    $("#height").blur(function() {
    	var height = document.getElementById("height").value.trim();
    	document.getElementById("height").value = height;
        if (!validateNumericField(height, 4, 3, 1)) {
            displayValidationError("Height", new Array("999.9", "999"), "#height");
        } else {
        	$("#height").removeClass("error-field");
        }
	});
    $("#weight").blur(function() {
    	var weight = document.getElementById("weight").value.trim();
    	document.getElementById("weight").value = weight;
    	if (!validateNumericField(weight, 5, 3, 2)) {
            displayValidationError("Weight", new Array("999.99", "999"), "#weight");
        } else {
        	$("#weight").removeClass("error-field");
        }
	});
    $("#hc").blur(function() {
    	var hc = document.getElementById("hc").value.trim();
    	document.getElementById("hc").value = hc;
    	if (!validateNumericField(hc, 4, 3, 1)) {
            displayValidationError("HC", new Array("999.9", "999"), "#hc");
        } else {
        	$("#hc").removeClass("error-field");
        }
	});
    $("#BPS").blur(function() {
    	var systBp = document.getElementById("BPS").value.trim();
    	document.getElementById("BPS").value = systBp;
        if (!validateNumericField(systBp, 3, 3, 0)) {
            displayValidationError("Systolic BP", new Array("999"), "#BPS");
        } else {
        	$("#BPS").removeClass("error-field");
        }
	});
    $("#BPD").blur(function() {
    	var diastBp = document.getElementById("BPD").value.trim();
    	document.getElementById("BPD").value = diastBp;
        if (!validateNumericField(diastBp, 3, 3, 0)) {
            displayValidationError("Diastolic BP", new Array("999"), "#BPD");
        } else {
        	$("#BPD").removeClass("error-field");
        }
	});
    $("#temp").blur(function() {
    	var temp = document.getElementById("temp").value.trim();
    	document.getElementById("temp").value = temp;
        if (!validateNumericField(temp, 4, 3, 1)) {
            displayValidationError("Temp", new Array("999.9", "999"), "#temp");
        } else {
        	$("#temp").removeClass("error-field");
        }
	});
    $("#Pulse").blur(function() {
    	var pulse = document.getElementById("Pulse").value.trim();
    	document.getElementById("Pulse").value = pulse;
        if (!validateNumericField(pulse, 3, 3, 0)) {
            displayValidationError("Pulse", new Array("999"), "#Pulse");
        } else {
        	$("#Pulse").removeClass("error-field");
        }
	});
    $("#RR").blur(function() {
    	var rr = document.getElementById("RR").value.trim();
    	document.getElementById("RR").value = rr;
        if (!validateNumericField(rr, 3, 3, 0)) {
            displayValidationError("RR", new Array("999"), "#RR");
        } else {
        	$("#RR").removeClass("error-field");
        }
	});
    $("#PulseOx").blur(function() {
    	var pulseOx = document.getElementById("PulseOx").value.trim();
    	document.getElementById("PulseOx").value = pulseOx;
        if (!validateNumericField(pulseOx, 3, 3, 0)) {
            displayValidationError("Pulse Ox", new Array("999"), "#PulseOx");
        } else {
        	$("#PulseOx").removeClass("error-field");
        }
	});
    $("#VisionL").blur(function() {
    	var visionL = document.getElementById("VisionL").value.trim();
    	document.getElementById("VisionL").value = visionL;
        if (!validateNumericField(visionL, 3, 3, 0)) {
            displayValidationError("Vision Left", new Array("999"), "#VisionL");
        } else {
        	$("#VisionL").removeClass("error-field");
        }
	});
    $("#VisionR").blur(function() {
    	var visionR = document.getElementById("VisionR").value.trim();
    	document.getElementById("VisionR").value = visionR;
        if (!validateNumericField(visionR, 3, 3, 0)) {
            displayValidationError("Vision Right", new Array("999"), "#VisionR");
        } else {
        	$("#VisionR").removeClass("error-field");
        }
	});
    
    // Initialize all pages because radio button reset will not work properly.
    $("div[type='question_page']").page();

    $(window).on("navigate", function (event, data) {
	  var direction = data.state.direction;
	  if (direction == "back" || direction == "forward") {
		checkSession();
	  }
	});
    
    $("#goButton").focus(function() {
		  $("#goButton").click();
	});
});

$(document).on("pageshow", "#vitals_page", function(){
	$("#vitals_passcode").click(function() {
		$("#vitals_passcode").focus();
	});
	
	$("#height").click(function() {
		$("#height").focus();
	});
	
	$( "#vitals_passcode_dialog" ).popup({
        afteropen: function( event, ui ) {
        	$("#vitals_passcode").trigger("click");
        },
	    afterclose: function( event, ui ) {
	    	$("#height").trigger("click");
	    }
    });
    
    $('#vitals_passcode').keypress(function(e){
	    if ( e.which == 13 ) 
	     checkPasscode();    
	});
    
	$("#lnkVitalsPasscode").click();
});

function finishedValidation(fieldId) {
	$("#validation_error_dialog").popup("close");
	$(fieldId).focus();
	$(fieldId).addClass("error-field");
}

function init(patientName, birthdate, formInst) {
	setLanguage(patientName, birthdate);
	formInstance = formInst;
	checkSession();
}

function checkSession() {
	var formInstances = sessionStorage.getItem("formInstances");
	if (formInstances != null) {
		var index = formInstances.indexOf(formInstance);
		if (index >= 0) {
			$.mobile.changePage("#form_completed_page", { transition: "fade"});
		}
	}
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    
//    // Reset answers
//    $("input[id^='QuestionEntry']").prop("checked", false).checkboxradio("refresh");
    
    // Transfer answers
    for (var i = 1; i < 21; i++) {
	    if (english) {
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_2_Yes", "#QuestionEntry_" + i + "_Yes");
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_2_No", "#QuestionEntry_" + i + "_No");
	    } else {
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_Yes", "#QuestionEntry_" + i + "_2_Yes");
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_No", "#QuestionEntry_" + i + "_2_No");
	    }
    }
    
    nextPage(1);
}

function setQuestionCheckboxes(initialCheckBoxId, newCheckBoxId) {
	if ($(initialCheckBoxId).is(":checked")) {
		$(newCheckBoxId).prop("checked", true);
		$(initialCheckBoxId).prop("checked", false);
		$(newCheckBoxId).checkboxradio('refresh');
		$(initialCheckBoxId).checkboxradio('refresh');
	}
}

function setLanguage(patientName, birthdate) {
	if (birthdate != null && birthdate.trim().length > 0) {
		// Remove the time
		var index = birthdate.indexOf(" ");
		if (index >= 0) {
			var dateStr = birthdate.substr(0, index);
			dateStr = dateStr.replace(new RegExp("-", 'g'), "/");
			var newDate = new Date(dateStr);
			birthdate = (newDate.getMonth() + 1) + "/" + newDate.getDate() + "/" + newDate.getFullYear();
		}
	}
	
    english = !english;
    var langButtonText = "Español";
    var parentText = "Parents: Thank you for answering these questions about your child.  The answers will help your doctor provide better quality of care.  If your child is age 12 or older, he/she should answer the questions privately.  Answers are confidential, but if you prefer not to answer that is allowed.  You may want to talk about these questions with your doctor.";
    var instructions = "<p>1) Please return the device back to the front desk if the patient information listed is incorrect.</p><p>2) Please confirm this form is for:<br/>Name: " + patientName + "<br/>Date of Birth: " + birthdate + "</p>";
    var confirmButtonText = "Confirm";
    var denyButtonText = "Deny";
    if (!english) {
        langButtonText = "English";
        parentText = "Padres de familia: Muchas gracias por tomarse la molestia de contestar las siguientes preguntas acerca de su nino(a).  Las respuestas de estas preguntas seran: ayundar a su doctor a dar mejor atencion medica.  Si su nino(a) tiene 12 anos o mas, por favor su nino(a) debe contestar las preguntas el (ella) solo(a).  Sus respuestas seran completamente privadas.  No necesita contestar ninguna pregunta que no desee contestar.  Si usted tiene preguntas acerca de este cuestionario, haga el favor de hablar sobre ellas con su doctor.  Por favor llene los circulos de la forma mas completa que le sea posible con un lapiz o lapiz tinta.";
        instructions = "<p>1) Por favor devuelva el aparato a la información si la información del paciente que aparece es incorrecta.</p><p>2) Por favor, confirme esta forma es para:<br/>Nombre: " + patientName + "<br/>Fecha de nacimiento: " + birthdate + "</p>";
        confirmButtonText = "Confirmar";
        denyButtonText = "Negar";
    }
    
    $("#confirmLangButton .ui-btn-text").text(langButtonText);
    $("#parentText").text(parentText);
    $("#instructions").html(instructions);
    $("#confirmButton .ui-btn-text").text(confirmButtonText);
    $("#denyButton .ui-btn-text").text(denyButtonText);
}

function nextPage(newPageNum) {
    var newPage = "#question_page_" + newPageNum;
    if (!english) {
        newPage = newPage + "_sp";
    }
    
    $.mobile.changePage(newPage, { transition: "fade", reverse: false });
}

function previousPage(newPageNum) {
    var newPage = "#question_page_" + newPageNum;
    if (!english) {
        newPage = newPage + "_sp";
    }
    
    $.mobile.changePage(newPage, { transition: "fade", reverse: true });
}

function finishForm() {
	if (english) {
	    $.mobile.changePage( "#quit_dialog", { transition: "pop" } );
	} else {
		$.mobile.changePage( "#quit_dialog_sp", { transition: "pop" } );
	}
}

function confirmQuit() {
	if (english) {
	    $.mobile.changePage( "#quit_confirm_dialog", { transition: "pop" });
	} else {
		$.mobile.changePage( "#quit_confirm_dialog_sp", { transition: "pop" });
	}
}

function finishVitals() {
	if (!validate()) {
        return;
    }
    
	$( "#confirm_submit_dialog" ).popup( "open", { transition: "pop"} );
}

function checkAuthentication() {
	login(parseLoginResult, handleAuthenticationAjaxError);
}

function submitEmptyForm() {
	document.getElementById("psfForm").submit();
}

function handleAuthenticationAjaxError(xhr, textStatus, error) {
//    $.mobile.loading("hide");
    //$("#confirm_submit_dialog").popup("close");
    if (textStatus === "timeout") {
        var submitDiv = document.getElementById("submitErrorDiv");
        submitDiv.innerHTML = "<p>The server took too long to verify the user.  Please try again.</p>";
        //$("#submitErrorDialog").popup("open");
        $("#lnkSubmitError").click();
    } else {
        var submitDiv = document.getElementById("submitErrorDiv");
        submitDiv.innerHTML = "<p>An error occurred on the server: " + error + ".  Please try again.</p>";
        //$("#submitErrorDialog").popup("open");
        $("#lnkSubmitError").click();
    }
}

function parseLoginResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "true") {
//        	$.mobile.loading("hide");
        	//$("#confirm_submit_dialog").popup("close");
        	//$("#loadingDialog").popup("open");
        	$("#lnkLoadingDialog").click();
        	populateValues();
        	var formInstances = sessionStorage.getItem("formInstances");
        	if (formInstances != null) {
        		formInstances = formInstances + "," + formInstance;
        	} else {
        		formInstances = formInstance;
        	}
        	
        	sessionStorage.setItem("formInstances", formInstances);
        	document.getElementById("psfForm").submit();
        } else {
    		var resultDiv = document.getElementById("loginResultDiv");
            resultDiv.innerHTML = "<p>Invalid username/password.</p>";
            $("#invalidLogin").popup("open", { transition: "pop"});
        }
    }
}

function showLoginDialog() {
	$("#invalidLogin").popup("close");
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
	value = value.trim();
    var index = value.indexOf(".");
    if (index < 0) {
        // The value's length cannot be greater than the length available before the decimal.
        if (value.length > beforeDec) {
            return false;
        } else {
    		if (value.trim().length == 0) {
    			return true;
    		} else if (isNaN(parseInt(value))) {
    			return false;
    		}
    		return true;
        }
    } else {
        // Check the length before the decimal
        var data = value.substring(0, index);
        if (data.length > beforeDec) {
            return false;
        } else {
        	if (data.trim().length == 0) {
    			return true;
    		} else if (isNaN(parseInt(data))) {
    			return false;
    		}
        }
        
        // Check the length after the decimal
        data = value.substring(index + 1, value.length);
        if (data.length > afterDec) {
            return false;
        } else {
        	if (data.trim().length == 0) {
    			return true;
    		} else if (isNaN(parseInt(data))) {
    			return false;
    		}
        }
    }
    
    return true;
}

function displayValidationError(fieldName, expectedFormats, fieldId) {
    var message = fieldName + " can only be in format ";
    for (var i = 0; i < expectedFormats.length; i++) {
        if (i == 0) {
            message+= expectedFormats[i];
        } else {
            message+= " or " + expectedFormats[i];
        }
    }
    
    message += "."; 
    $("#validationOkButton").attr("onClick", "finishedValidation('" + fieldId + "')");
    $("#validationError").html(message);
    $("#validation_error_dialog").popup( "open", { transition: "pop"} );
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

function showPasscode() {
	$( "#passcodeError" ).popup( "close" );
	$( "#lnkVitalsPasscode" ).click();
}

function checkPasscode() {
//    $.mobile.loading("show");
    var passcode = $("#vitals_passcode").val();
    var url = "/openmrs/moduleServlet/chica/chicaMobile";
    var action = "action=verifyPasscode&passcode=" + passcode;
    var token = getAuthenticationToken();
    $.ajax({
    	beforeSend: function (xhr) {
		    xhr.setRequestHeader ("Authorization", token );
	    },
        "cache": false,
        "dataType": "xml",
        "data": action,
        "type": "POST",
        "url": url,
        "timeout": 30000, // optional if you want to handle timeouts (which you should)
        "error": handlePasscodeAjaxError, // this sets up jQuery to give me errors
        "success": function (xml) {
            parsePasscodeResult(xml);
//            $.mobile.loading("hide");
        }
    });
}

function parsePasscodeResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "success") {
        	$("#vitals_passcode_dialog").popup("close");
        	$("#vitals_passcode").val("");
        } else {
        	if (result == "Please log in.") {
	            //$("#passcodeLoginDiv").html("<p>Your session has timed out.  Please log in.</p>");
	            //$("#logInPasscode").popup("open");
        		login(parsePasscodeLoginResult, handlePasscodeAuthenticationAjaxError);
            } else {
	            $("#passcodeErrorResultDiv").html("<p>" + result + "</p>");
	            $("#lnkPasscodeError").click();
	            //$('#lnkPasscodeError').popup();
	            //$("#lnkPasscodeError").popup("open");
            }
        }
    }
}

function handlePasscodeAjaxError(xhr, textStatus, error) {
//    $.mobile.loading("hide");
    var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to verify the passcode.";
    }
    
    $("#passcodeErrorResultDiv").html("<p>" + error + "</p>");
    $("#lnkPasscodeError").click();
}

function parsePasscodeLoginResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "true") {
        	checkPasscode();
        } else {
        	$("#passcodeErrorResultDiv").html("<p>Your session has timed out.  Please log in.</p>");
        	$("#lnkPasscodeError").click();
        }
    }
}

function handlePasscodeAuthenticationAjaxError(xhr, textStatus, error) {
//    $.mobile.loading("hide");
    $("#passcodeErrorResultDiv").html("<p>An error occurred checking the passcode: " + error + ".  Please log in.</p>");
    $("#lnkPasscodeError").click();
}