var formId;
var formInstanceId;
var encounterId;
var english = false;
var loadedForms = [];

$(document).on("pagebeforeshow", "#confirm_page", function() {
    $(document).ajaxStart(function() {
		showBlockingMessage();
	});

	$(document).ajaxStop(function() {
		$.unblockUI();
		$("#blockUIMessage").html("");
	});
	
    $("#denyButton").click(function () {
    	if (english) {
    	    $("#deny_dialog").popup("open", { transition: "pop"});
    	} else {
    		$("#deny_dialog_sp").popup("open", { transition: "pop"});
    	}
    });
    
    $('#loading_form_dialog').dialog();
    
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
    
    $("#vitals_passcode").keypress(function(e){
	    if ( e.which == 13 ) 
	     checkPasscode();    
	});
    
	$("#lnkVitalsPasscode").click();
});

$(document).on("pagebeforeshow", "#question_page", function() {
    displayQuestions();
});

$(document).on("pagebeforeshow", "#question_page_sp", function() {
	displayQuestions();
});

$(document).on("pagebeforeshow", "#confirm_page", function() {
    startLoginTimer();
});

$(document).on("pageshow", "#additionalForms_page", function() {
	var content = $("#content_frame").html();
	if (content.trim().length == 0) {
		$.mobile.changePage( "#vitals_page", { transition: "fade" });
	}
});

function init(patientName, birthdate, formInst, formId, formInstanceId, encounterId) {
	this.formId = formId;
	this.formInstanceId = formInstanceId;
	this.encounterId = encounterId;
	setLanguage(patientName, birthdate);
	formInstance = formInst;
	
	if (!shouldShowVitalsButton()) {
		$("#confirmVitalsButton").hide();
		$("#vitalsDirectButton").hide();
		$("#vitalsDirectButton_sp").hide();
	}
}

function displayQuestions() {
	var contentPage = $("#content_1");
	var htmlData = contentPage.html();
	if (htmlData.trim().length === 0) {
		checkAuthentication();
	}
}

function backToQuestions() {
    var newPage = "#question_page";
    if (!english) {
        newPage = newPage + "_sp";
    }
    
    $.mobile.changePage(newPage, { transition: "fade", reverse: false });
}

function checkAuthentication() {
	$("#submitErrorButton").unbind("click");
	$("#submitErrorButton").click(function() {
		checkAuthentication();
	});

	setTimeout(loadDynamicQuestions, 1);
}

function handleAuthenticationAjaxError(xhr, textStatus, error) {
  var submitDiv = document.getElementById("submitErrorDiv");
  if (textStatus === "timeout") {
      if (english) {
    	  submitDiv.innerHTML = "<p>The server took too long to verify the user.  Please try again.</p>";
      } else {
    	  submitDiv.innerHTML = "<p>El servidor ha tardado demasiado tiempo para verificar el usuario. Por favor, inténtelo de nuevo.</p>";
      }
      
      $("#lnkSubmitError").click();
  } else {
      if (english) {
    	  submitDiv.innerHTML = "<p>An error occurred on the server: " + error + ".  Please try again.</p>";
      } else {
    	  submitDiv.innerHTML = "<p>Se ha producido un error en el servidor. Por favor, inténtelo de nuevo.</p>";
      }
      
      $("#lnkSubmitError").click();
  }
}

function completeForm() {
	// Set the language
	setLanguageField();
	
	$("#language").val();
	$("#submitErrorButton").unbind("click");
	$("#submitErrorButton").click(function() {
		completeForm();
	});
	
	$("#lnkLoadingDialog").click();
	populateValues();
	login(parseLoginSubmitResult, handleAuthenticationAjaxTimerError);
}

function setLanguageField() {
	if (english) {
		$("#language").val("english");
	} else {
		$("#language").val("spanish");
	}
}

function loadQuestions() {
	if (english) {
		$("#load_error_dialog").dialog("close");
	} else {
		$("#load_error_dialog_sp").dialog("close");
	}
	
	checkAuthentication();
}

function loadDynamicQuestions() {
  var url = "/openmrs/moduleServlet/chica/chicaMobile";
  var action = "action=getPrioritizedElements&formId=" + formId + "&formInstanceId=" + formInstanceId + "&encounterId=" + 
  	encounterId + "&maxElements=5";
  var message = "Loading Questions...";
  if (!english) {
	  message = "Cargando Preguntas...";
  }
  
  var token = getAuthenticationToken();
  
  $("#blockUIMessage").html('<table><tr><td><h3><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif" /></h3></td><td style="white-space: nowrap;vertical-align: center;"><h3>&nbsp;' + message + '</h3></td></tr></table>');
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
      "error": handleLoadQuestionError, // this sets up jQuery to give me errors
      "success": function (xml) {
          parseQuestionsResult(xml);
      }
  });
}

function handleLoadQuestionError() {
	if (english) {
    	$.mobile.changePage( "#load_error_dialog", { transition: "pop" });
	} else {
    	$.mobile.changePage( "#load_error_dialog_sp", { transition: "pop" });
	}
}

function handleSaveQuestionError() {
	if (english) {
    	$.mobile.changePage( "#save_error_dialog", { transition: "pop" });
	} else {
    	$.mobile.changePage( "#save_error_dialog_sp", { transition: "pop" });
	}
}

function saveQuestions() {
	$("#submitErrorButton").unbind("click");
	$("#submitErrorButton").click(function() {
		saveQuestions();
	});
	
	if (english) {
		$("#save_error_dialog").dialog("close");
	} else {
		$("#save_error_dialog_sp").dialog("close");
	}
	
	saveDynamicQuestions(true);
}

function saveDynamicQuestions(autoloadNextQuestions) {
	$("#finish_error_dialog").popup("close");
	$("#finish_error_dialog_sp").popup("close");
	$("#not_finished_final_dialog").popup("close");
	$("#not_finished_final_dialog_sp").popup("close");
	
    var patientId = $("#patientId").val();
	var locationId = $("#locationId").val();
	var locationTagId = $("#locationTagId").val();
	var url = "/openmrs/moduleServlet/chica/chicaMobile";
	var action = "action=saveExportElements&formId=" + formId
			+ "&formInstanceId=" + formInstanceId + "&encounterId="
			+ encounterId + "&patientId=" + patientId + "&locationId="
			+ locationId + "&locationTagId=" + locationTagId;
	var questionAction = buildQuestionAction();
	var message = "Saving Answers...";
	if (!english) {
		message = "Ahorrar Respuestas...";
	}
	var token = getAuthenticationToken();
	$("#blockUIMessage").html(
			'<table><tr><td><h3><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif" /></h3></td><td style="white-space: nowrap"><h3>&nbsp;' + message + '</h3></td></tr></table>');
	$.ajax({
		beforeSend: function (xhr) {
		    xhr.setRequestHeader ("Authorization", token );
	    },
		"cache" : false,
		"dataType" : "xml",
		"data" : action + questionAction,
		"type" : "POST",
		"url" : url,
		"timeout" : 30000, // optional if you want to handle timeouts (which
							// you should)
		"error" : handleSaveQuestionError, // this sets up jQuery to give me
											// errors
		"success" : function(xml) {
			if (autoloadNextQuestions) {
				parseSaveQuestionsResult(xml);
			} else {
				$.mobile.changePage("#vitals_page", {
					transition : "fade"
				});
				var contentPage = $("#content_1").html("");
				var contentPage = $("#content_1").html("");
			}
		}
	});
}

function parseQuestionsResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
    	var contentPage = $("#content_1");
    	var spanishContentPage = $("#content_1_sp");
        var content = "";
        var spanishContent = "";
        var error = $(responseXML).find("error").text();
        if (error !== null && error.trim().length > 0) {
    		if (english) {
	        	$("#errorResultDiv").html("<p>" + error + "</p>");
	        	$.mobile.changePage( "#server_error_dialog", { transition: "pop" });
        	} else {
        		$("#errorResultDiv_sp").html("<p>Se ha producido un error en el servidor. Por favor, inténtelo de nuevo.</p>");
	        	$.mobile.changePage( "#server_error_dialog_sp", { transition: "pop" });
        	}
        }
        
        var count = 0;
        var spanishCount = 0;
        $(responseXML).find("Record").each(function () {
        	$(this).find("Field").each(function () {
	            var fieldName = $(this).attr("id");
	            var value = $(this).find("Value").text();
	            var questionNumber = getQuestionNumber(fieldName);
	            var spanishIndex = fieldName.indexOf("_SP");
	            var isSpanish = false;
	            
	            if (spanishIndex < 0) {
		            if (count !== 0) {
		            	content = content + "<br/>";
		            }
		            
		            var questions = createQuestionData(value, questionNumber, "", "Yes", isSpanish);
		            content = content + questions;
		            count++;
	            } else {
	            	if (spanishCount !== 0) {
	            		spanishContent = spanishContent + "<br/>";
		            }
	            	
	            	isSpanish = true;
	            	var questionsSp = createQuestionData(value, questionNumber, "_2", "Si", isSpanish);
	            	spanishContent = spanishContent + questionsSp;
	            	spanishCount++;
	            }
        	});
        });

        contentPage.html(content);
        spanishContentPage.html(spanishContent);
        
        if (count === 0) {
        	$.unblockUI();
        	$("#blockUIMessage").html("");
        	$.mobile.loading("hide");
        	// Check to see if there are other forms to process
        	loadNewForms();
        } else {
        	var newPage = "#question_page";
            if (!english) {
                newPage = newPage + "_sp";
            }
            
            $.mobile.changePage(newPage, { transition: "fade", reverse: false });
            $("div[type='question_page']").trigger("create");
            $.mobile.silentScroll(0);
        }
    }
}

function loadNewForms() {
	$.mobile.changePage( "#loading_form_dialog", { transition: "pop"});
	getPatientForms();
}

function buildQuestionAction() {
	var englishAction = getQuestionSelectionAction("#content_1");
	var spanishAction = getQuestionSelectionAction("#content_1_sp");
	
	return englishAction + spanishAction;
}

function getQuestionSelectionAction(divData) {
	var action = "";
	$(divData).find("div").each(function () {
		$(this).find("fieldset").each(function () {
			var inputFound = false;
			var fieldName = "";
			$(this).find("input").each(function () {
				fieldName = $(this).attr("name");
				var fieldId = $(this).attr("id");
				var selected = $("#" + fieldId + ":checked");
				if (selected.length > 0) {
					inputFound = true;
				    var selectedVal = selected.val();
				    action = action + "&" + fieldName + "=" + selectedVal;
				}
			});
			
			if (!inputFound && fieldName !== null && fieldName.length > 0) {
				 action = action + "&" + fieldName + "=";
			}
		});
	});
	
	return action;
}

function parseSaveQuestionsResult(responseXML) {
	if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "true") {
        	checkAuthentication();
        } else {
        	var error = "An error occurred on the server.";
        	  if (textStatus === "timeout") {
        	      error = "The server took too long to get the questions.";
        	  }
        	  
        	  if (english) {
  	        	$("#errorResultDiv").html("<p>" + error + "</p>");
  	        	$.mobile.changePage( "#server_error_dialog", { transition: "pop" });
          	} else {
          		$("#errorResultDiv_sp").html("<p>Se ha producido un error en el servidor. Por favor, inténtelo de nuevo.</p>");
  	        	$.mobile.changePage( "#server_error_dialog_sp", { transition: "pop" });
          	}
        }
    }
}

function createQuestionData(value, questionNumber, spanishText, yesButtonName, isSpanish) {
	var content = "";
	if (isSpanish) {
		content = content + '<strong>' + value + '</strong><a data-role="button" data-inline="true" class="custom-button" onclick=\'readTextSpanish("' + value + '")\'></a>';
	} else {
		content = content + '<strong>' + value + '</strong><a data-role="button" data-inline="true" class="custom-button" onclick=\'readText("' + value + '")\'></a>';
	}
	
    content = content + '<div data-role="fieldcontain" style="margin-top:0px;">';
    content = content + '<fieldset data-role="controlgroup" data-type="horizontal">';
    content = content + '<input type="radio" name="QuestionEntry_' + questionNumber + spanishText + '" id="QuestionEntry_' + questionNumber + spanishText + '_Yes" value="Y" data-theme="c" />';
    content = content + '<label for="QuestionEntry_' + questionNumber + spanishText + '_Yes">' + yesButtonName + '</label>';
    content = content + '<input type="radio" name="QuestionEntry_' + questionNumber + spanishText + '" id="QuestionEntry_' + questionNumber + spanishText + '_No" value="N" data-theme="c" />';
    content = content + '<label for="QuestionEntry_' + questionNumber + spanishText + '_No">No</label>';
    
    // CHICA-514 Adding NA as an available option, set the value to NoAnswer since this is what is checked in the controller
    content = content + '<input type="radio" name="QuestionEntry_' + questionNumber + spanishText + '" id="QuestionEntry_' + questionNumber + spanishText + '_NA" value="NoAnswer" data-theme="c" />';
    content = content + '<label for="QuestionEntry_' + questionNumber + spanishText + '_NA">N/A</label>';
    
    content = content + '</fieldset></div>';
    return content;
}

function handleQuestionsAjaxError(xhr, textStatus, error) {
  var errorMessage = "An error occurred on the server.";
  if (textStatus === "timeout") {
	  errorMessage = "The server took too long to get the questions.";
  }
  
  if (english) {
  	$("#errorResultDiv").html("<p>" + errorMessage + "</p>");
  	$.mobile.changePage( "#server_error_dialog", { transition: "pop" });
	} else {
		$("#errorResultDiv_sp").html("<p>Se ha producido un error en el servidor. Por favor, inténtelo de nuevo.</p>");
  	$.mobile.changePage( "#server_error_dialog_sp", { transition: "pop" });
	}
}

function getQuestionNumber(fieldName) {
	var questionIndex = fieldName.indexOf("Question");
	var index = fieldName.indexOf("_");
	if (index >= 0) {
		// Spanish question
		var underscoreIndex = fieldName.indexOf("_");
		return fieldName.substring(questionIndex + 8, underscoreIndex);
	} else {
		// English question
		return fieldName.substring(questionIndex + 8, fieldName.length);
	}
}

function setLanguage(patientName, birthdate) {
	if (birthdate !== null && birthdate.trim().length > 0) {
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
    var vitalsButtonText = "Staff";
    if (!english) {
        langButtonText = "English";
        parentText = "Padres de familia: Muchas gracias por tomarse la molestia de contestar las siguientes preguntas acerca de su nino(a).  Las respuestas de estas preguntas seran: ayundar a su doctor a dar mejor atencion medica.  Si su nino(a) tiene 12 anos o mas, por favor su nino(a) debe contestar las preguntas el (ella) solo(a).  Sus respuestas seran completamente privadas.  No necesita contestar ninguna pregunta que no desee contestar.  Si usted tiene preguntas acerca de este cuestionario, haga el favor de hablar sobre ellas con su doctor.  Por favor llene los circulos de la forma mas completa que le sea posible con un lapiz o lapiz tinta.";
        instructions = "<p>1) Por favor devuelva el aparato a la información si la información del paciente que aparece es incorrecta.</p><p>2) Por favor, confirme esta forma es para:<br/>Nombre: " + patientName + "<br/>Fecha de nacimiento: " + birthdate + "</p>";
        confirmButtonText = "Confirmar";
        denyButtonText = "Negar";
        vitalsButtonText = "Personal";
    }
    
    $("#confirmLangButton .ui-btn-text").text(langButtonText);
    $("#parentText").text(parentText);
    $("#instructions").html(instructions);
    $("#confirmButton .ui-btn-text").text(confirmButtonText);
    $("#denyButton .ui-btn-text").text(denyButtonText);
    $("#confirmVitalsButton .ui-btn-text").text(vitalsButtonText);
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    // Transfer answers
    for (var i = 1; i < 100; i++) {
	    if (english) {
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_2_Yes", "#QuestionEntry_" + i + "_Yes");
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_2_No", "#QuestionEntry_" + i + "_No");
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_2_NA", "#QuestionEntry_" + i + "_NA");
	    } else {
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_Yes", "#QuestionEntry_" + i + "_2_Yes");
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_No", "#QuestionEntry_" + i + "_2_No");
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_NA", "#QuestionEntry_" + i + "_2_NA");
	    }
    }
    
    var newPage = "#question_page";
    if (!english) {
        newPage = newPage + "_sp";
    }
    
    $.mobile.changePage(newPage, { transition: "fade", reverse: false });
}

function setQuestionCheckboxes(initialCheckBoxId, newCheckBoxId) {
	if ($(initialCheckBoxId).is(":checked")) {
		$(newCheckBoxId).prop("checked", true);
		$(initialCheckBoxId).prop("checked", false);
		$(newCheckBoxId).checkboxradio("refresh");
		$(initialCheckBoxId).checkboxradio("refresh");
	}
}

function getPatientForms() {
  var url = "/openmrs/moduleServlet/chica/chicaMobile";
  var sessionId = $("#sessionId").val();
  var token = getAuthenticationToken();
  $.ajax({
	  beforeSend: function (xhr) {
		    xhr.setRequestHeader ("Authorization", token );
	    },
      "cache": false,
      "dataType": "xml",
      "data": "action=getPatientSecondaryForms&sessionId=" + sessionId,
      "type": "POST",
      "url": url,
      "timeout": 30000, // optional if you want to handle timeouts (which you should)
      "error": handleFormsAjaxError, // this sets up jQuery to give me errors
      "success": function (xml) {
          parsePatientForms(xml);
      }
  });
}

function handleFormsAjaxError(xhr, textStatus, error) {
	if (english) {
    	$.mobile.changePage( "#forms_error_dialog", { transition: "pop" });
	} else {
    	$.mobile.changePage( "#forms_error_dialog_sp", { transition: "pop" });
	}
}

function parsePatientForms(responseXML) {
    // no matches returned
    if (responseXML === null) {
    	$.mobile.loading("hide");
        return false;
    } else {
        var error = $(responseXML).find("error").text();
        if (error !== null && error.trim().length > 0) {
    		if (english) {
	        	$("#errorResultDiv").html("<p>" + error + "</p>");
	        	$.mobile.changePage( "#server_error_dialog", { transition: "pop" });
        	} else {
        		$("#errorResultDiv_sp").html("<p>Se ha producido un error en el servidor. Por favor, inténtelo de nuevo.</p>");
	        	$.mobile.changePage( "#server_error_dialog_sp", { transition: "pop" });
        	}
        }

        setLanguageField();
        
        var patientXmls = $(responseXML).find("patient");
        var patientId = $("#patientId").val();
    	var encounterId = $("#encounterId").val();
    	var locationTagId = $("#locationTagId").val();
    	var sessionId = $("#sessionId").val();
    	var currentFormId = $("#formId").val();
    	var language = $("#language").val();
        for (var i = 0; i < patientXmls.length; i++) {
        	var patientXml = patientXmls[i];
        	var formPatientId = $(patientXml).find("id").text();
	    	if (patientId != formPatientId) {
	    		continue;
	    	}
	    	
	    	var formInstances =  $(patientXml).find("formInstance");
	        for (var j = 0; j < formInstances.length; j++) {
	        	var formInstance = formInstances[j];
	            var formFormId = $(formInstance).find("formId").text();
	            if (currentFormId == formFormId) {
	            	continue;
	            }
	            
	            var formInstanceId = $(formInstance).find("formInstanceId").text();
	            var locationId = $(formInstance).find("locationId").text();
	            var formUrl = $(formInstance).find("url").text();
	            var sessionForm = locationId + "_" + locationTagId + "_" + formFormId + "_" + formInstanceId;
	            if (!hasLoadedForm(sessionForm)) {
	            	$.unblockUI();
	            	$("#blockUIMessage").html("");
                    var ifr=$('<iframe/>', {
                        id:'formFrame',
                        src:formUrl + "?patientId=" + patientId + "&encounterId=" + encounterId + "&formInstance=" + sessionForm + "&sessionId=" + sessionId + "&language=" + language,
                        load:function(){
                        	$.mobile.changePage("#additionalForms_page", { transition: "fade"});
                        }
                    });
                    $('#content_frame').html(ifr);  
                    addLoadedForm(sessionForm);
	            	return false;
	            } else {
	            	console.warn("Form: " + sessionForm + " has already been loaded.");
	            }
	        }
        }
        
        $.mobile.loading("hide");
        $('#content_frame').html("");
        
        // Check to see if we need to show the vitals
        if (!shouldShowVitalsButton()) {
        	// Submit the form
        	completeForm();
        } else {
        	// Go to the vitals page
            if (english) {
            	$.mobile.changePage("#finished_dialog", { transition: "pop" });
            } else {
            	$.mobile.changePage("#finished_dialog_sp", { transition: "pop" });
            }
        }
    }
}

function hasLoadedForm(formInfo) {
	var index;
	for (index = 0; index < loadedForms.length; ++index) {
		var loadedForm = loadedForms[index];
		if (formInfo === loadedForm) {
			return true;
		}
	}
	
	return false;
}

function addLoadedForm(formInfo) {
	loadedForms[loadedForms.length] = formInfo;
}

function closeIframe() {
	$.mobile.changePage( "#loading_form_dialog", { transition: "pop"});
	getPatientForms();
}

function attemptLoadForms() {
	$("#loading_form_dialog").dialog("close");
	$("#loading_form_dialog_sp").dialog("close");
	closeIframe();
}

function checkPasscode() {
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
      "error": handlePasscodeError, // this sets up jQuery to give me errors
      "success": function (xml) {
          parsePasscodeResult(xml);
      }
  });
}

function handlePasscodeError() {
	$("#submitErrorButton").unbind("click");
	$("#submitErrorButton").click(function() {
		checkPasscode();
	});
	
	handleAuthenticationAjaxError();
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
        $("#passcodeErrorResultDiv").html("<p>" + result + "</p>");
        $("#lnkPasscodeError").click();
      }
  }
}

function finishVitals() {
	if (!validate()) {
        return;
    }
    
	$( "#confirm_submit_dialog" ).popup( "open", { transition: "pop"} );
}

function showBlockingMessage() {
	var blockUIMessage = '<table><tr><td><h3><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif" /></h3></td><td style="white-space: nowrap;vertical-align: center;"><h3>&nbsp;Please wait...</h3></td></tr></table>';
	var divHtml = $("#blockUIMessage").html();
	if (divHtml !== null && divHtml.length > 0) {
		blockUIMessage = divHtml;
	}
	
	$.blockUI({ css: { 
        border: "1px solid black", 
        padding: "15px", 
        width: '300px',
        backgroundColor: "#A9A9A9", 
        "-webkit-border-radius": "10px", 
        "-moz-border-radius": "10px", 
        color: "#000" 
    }, 
    message: blockUIMessage});
}

function finishedValidation(fieldId) {
	$("#validation_error_dialog").popup("close");
	$(fieldId).focus();
	$(fieldId).addClass("error-field");
}

function validate() {
    var height = $("#height").val();
    if (!validateNumericField(height, 4, 3, 1)) {
        displayValidationError("Height", new Array("999.9", "999"));
        return false;
    }
    
    var weight = $("#weight").val();
    if (!validateNumericField(weight, 5, 3, 2)) {
        displayValidationError("Weight", new Array("999.99", "999"));
        return false;
    }
    
    var hc = $("#hc").val();
    if (!validateNumericField(hc, 4, 3, 1)) {
        displayValidationError("HC", new Array("999.9", "999"));
        return false;
    }
    
    var systBp = $("#BPS").val();;
    if (!validateNumericField(systBp, 3, 3, 0)) {
        displayValidationError("Systolic BP", new Array("999"));
        return false;
    }
    
    var diastBp = $("#BPD").val();
    if (!validateNumericField(diastBp, 3, 3, 0)) {
        displayValidationError("Diastolic BP", new Array("999"));
        return false;
    }
    
    var temp = $("#temp").val();
    if (!validateNumericField(temp, 4, 3, 1)) {
        displayValidationError("Temp", new Array("999.9", "999"));
        return false;
    }
    
    var pulse = $("#Pulse").val();
    if (!validateNumericField(pulse, 3, 3, 0)) {
        displayValidationError("Pulse", new Array("999"));
        return false;
    }
    
    var rr = $("#RR").val();
    if (!validateNumericField(rr, 3, 3, 0)) {
        displayValidationError("RR", new Array("999"));
        return false;
    }
    
    var pulseOx = $("#PulseOx").val();
    if (!validateNumericField(pulseOx, 3, 3, 0)) {
        displayValidationError("Pulse Ox", new Array("999"));
        return false;
    }
    
    var visionL = $("#VisionL").val();
    if (!validateNumericField(visionL, 3, 3, 0)) {
        displayValidationError("Vision Left", new Array("999"));
        return false;
    }
    
    var visionR = $("#VisionR").val();
    if (!validateNumericField(visionR, 3, 3, 0)) {
        displayValidationError("Vision Right", new Array("999"));
        return false;
    }
    
    return true;
}

function validateNumericField(value, totalPlaces, beforeDec, afterDec) {
	if (value == null) {
		return true;
	}
	
	value = value.trim();
    var index = value.indexOf(".");
    if (index < 0) {
        // The value's length cannot be greater than the length available before the decimal.
        if (value.length > beforeDec) {
            return false;
        } else {
    		if (value.trim().length === 0) {
    			return true;
    		} else if (isNaN(parseInt(value, 10))) {
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
        	if (data.trim().length === 0) {
    			return true;
    		} else if (isNaN(parseInt(data, 10))) {
    			return false;
    		}
        }
        
        // Check the length after the decimal
        data = value.substring(index + 1, value.length);
        if (data.length > afterDec) {
            return false;
        } else {
        	if (data.trim().length === 0) {
    			return true;
    		} else if (isNaN(parseInt(data, 10))) {
    			return false;
    		}
        }
    }
    
    return true;
}

function displayValidationError(fieldName, expectedFormats, fieldId) {
    var message = fieldName + " can only be in format ";
    for (var i = 0; i < expectedFormats.length; i++) {
        if (i === 0) {
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
	var height = document.getElementById("height");
	if (height != undefined && height != null) {
		setDecimalValueFields(height.value, "HeightP", "HeightS");
	}
	
	var weight = document.getElementById("weight");
	if (weight != undefined && weight != null) {
		setDecimalValueFields(weight.value, "WeightP", "WeightS");
	}
	
	var temp = document.getElementById("temp");
	if (temp != undefined && temp != null) {
		setDecimalValueFields(temp.value, "TempP", "TempS");
	}
	
	var hc = document.getElementById("hc");
	if (hc != undefined && hc != null) {
		setDecimalValueFields(hc.value, "HCP", "HCS");
	}
}

function setDecimalValueFields(value, beforeDecName, afterDecName) {
    if (value !== null && value.trim().length > 0) {
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

/**
 * Timer to keep the user logged in while form is completed.
 */
function startLoginTimer() {
    var timer = $.timer(function () {
    	login(parseLoginTimerResult, handleAuthenticationAjaxTimerError);
    });

    timer.set({
        time: 600000,
        autostart: true
    });
}

function parseLoginTimerResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "true") {
        	// Do nothing
        } else {
        	setTimeout(startLoginTimer, 30000);
        }
    }
}

function parseLoginSubmitResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "true") {
        	document.getElementById("psfForm").submit();
        } else {
        	login(parseLoginSubmitResult, handleAuthenticationAjaxTimerError);
        }
    }
}

function handleAuthenticationAjaxTimerError(xhr, textStatus, error) {
	setTimeout(startLoginTimer, 30000);
}

function openVitalsConfirm() {
	$("#quit_to_vitals_dialog").popup("open", { transition: "pop"});
}

function openVitalsConfirmSpanish() {
	$("#quit_to_vitals_dialog_sp").popup("open", { transition: "pop"});
}

function navigateToVitals() {
	$.mobile.changePage( "#vitals_page", { transition: "fade" });
}

function saveSendToVitals() {
	saveDynamicQuestions(false);
}

function attemptSaveQuestions() {
	if (areAllQuestionsAnswered()) {
		saveDynamicQuestions(true);
	} else {
		if (english) {
    	    $("#not_finished_final_dialog").popup("open", { transition: "pop"});
    	} else {
    		$("#not_finished_final_dialog_sp").popup("open", { transition: "pop"});
    	}
	}
}

function areAllQuestionsAnswered() {
	var content = $("#content_1_sp");
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
		content = $("#content_1");
	}
	
	var stop = false;
	content.find("fieldset").each(function() {
		var check = $(this).find("input");
		var name = $(check).attr("name");
		if (!$("input[name='" + name + "']:checked").val()) {
		   stop = true;
		   return false;
		}
	});
	
	if (stop) {
		return false;
	}
	
	return true;
}

function shouldShowVitalsButton() {
	 var showVitals = $("#showVitals").val();
     if (showVitals == "false") {
     	return false;
     }
     
     return true;
}