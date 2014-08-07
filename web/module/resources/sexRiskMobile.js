var english = false;
var formInstance = null;
var finishAttempts = 0;
var age = 0.0;
var ageRange = null;
var gender = null;

$(document).on("pageinit", function() {
    // Initialize all pages because radio button reset will not work properly.
    $("div[type='question_page']").page();
    
    $(document).ajaxStart(function() {
		showBlockingMessage();
	});

	$(document).ajaxStop(function() {
		$.unblockUI();
	});
});

function init(patientName, birthdate, formInst, language, age, gender) {
	// This looks backwards, but the setLanguage method reverses it.
	if (language.toUpperCase() == "ENGLISH") {
		english = false;
	} else {
		english = true;
	}
	
	setLanguage(patientName, birthdate);
	formInstance = formInst;
	
	this.age = age;
	if (age < 15) {
		ageRange = "early";
	} else if (age >= 15 && age < 18) {
		ageRange = "middle";
	} else if (age >= 18) {
		ageRange = "late";
	}
	
	this.gender = gender;
}

function setLanguage(patientName, birthdate) {
	english = !english;
    var langButtonText = "Español";
    var additionalQuestions = "The following are some additional questions about sexual behavior.";
    var startButtonText = "Start";
    var vitalsButtonText = "Vitals"
    if (!english) {
        langButtonText = "English";
        additionalQuestions = "Las preguntas siguientes son adicionales acerca del comportamiento sexual.";
        startButtonText = "Comienzo";
        vitalsButtonText = "Vitales";
    }
    
    $("#confirmLangButton .ui-btn-text").text(langButtonText);
    $("#additionalQuestions").text(additionalQuestions);
    $("#startButton .ui-btn-text").text(startButtonText);
    $("#vitalsButton .ui-btn-text").text(vitalsButtonText);
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    
    // Transfer answers
    for (var i = 1; i < 6; i++) {
	    if (english) {
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_2_" + ageRange + "_" + gender + "_Yes", "#QuestionEntry_" + i + "_" + ageRange + "_" + gender + "_Yes");
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_2_" + ageRange + "_" + gender + "_No", "#QuestionEntry_" + i + "_" + ageRange + "_" + gender + "_No");
	    } else {
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_" + ageRange + "_" + gender + "_Yes", "#QuestionEntry_" + i + "_2_" + ageRange + "_" + gender + "_Yes");
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_" + ageRange + "_" + gender + "_No", "#QuestionEntry_" + i + "_2_" + ageRange + "_" + gender + "_No");
	    }
    }
    
    changePage();
}

function setQuestionCheckboxes(initialCheckBoxId, newCheckBoxId) {
	var initialField = $(initialCheckBoxId);
	var newField = $(newCheckBoxId);
	if (initialField.length && newField.length) {
		if (initialField.is(":checked")) {
			newField.prop("checked", true);
			initialField.prop("checked", false);
			newField.checkboxradio('refresh');
			initialField.checkboxradio('refresh');
		}
	}
}

function submitEmptyForm() {
	setLanguageField();
	document.getElementById("sexRiskForm").submit();
}

function setLanguageField() {
	if (english) {
		$("#language").val("english");
	} else {
		$("#language").val("spanish");
	}
}

function attemptFinishForm() {
	finishAttempts++;
	if (areAllQuestionsAnswered()) {
		finishForm();
	} else if (finishAttempts == 1) {
    	if (english) {
    	    $("#not_finished_dialog_" + ageRange + "_" + gender).popup("open", { transition: "pop"});
    	} else {
    		$("#not_finished_dialog_" + ageRange + "_" + gender + "_sp").popup("open", { transition: "pop"});
    	}
	} else if (finishAttempts >= 2) {
		if (english) {
    	    $("#not_finished_final_dialog_" + ageRange + "_" + gender).popup("open", { transition: "pop"});
    	} else {
    		$("#not_finished_final_dialog_" + ageRange + "_" + gender + "_sp").popup("open", { transition: "pop"});
    	}
	}
}

function finishForm() {
	//run an AJAX post request to your server-side script, $this.serialize() is the data from your form being added to the request
	//$.mobile.changePage("#empty_page");
	$("#finish_error_dialog_early_M").popup("close");
	$("#finish_error_dialog_early_F").popup("close");
	$("#finish_error_dialog_middle_M").popup("close");
	$("#finish_error_dialog_middle_F").popup("close");
	$("#finish_error_dialog_late_M").popup("close");
	$("#finish_error_dialog_late_F").popup("close");
	$("#finish_error_dialog_early_M_sp").popup("close");
	$("#finish_error_dialog_early_F_sp").popup("close");
	$("#finish_error_dialog_middle_M_sp").popup("close");
	$("#finish_error_dialog_middle_F_sp").popup("close");
	$("#finish_error_dialog_late_M_sp").popup("close");
	$("#finish_error_dialog_late_F_sp").popup("close");
	$("#not_finished_final_dialog_early_M").popup("close");
	$("#not_finished_final_dialog_early_F").popup("close");
	$("#not_finished_final_dialog_middle_M").popup("close");
	$("#not_finished_final_dialog_middle_F").popup("close");
	$("#not_finished_final_dialog_late_M").popup("close");
	$("#not_finished_final_dialog_late_F").popup("close");
	$("#not_finished_final_dialog_early_M_sp").popup("close");
	$("#not_finished_final_dialog_early_F_sp").popup("close");
	$("#not_finished_final_dialog_middle_M_sp").popup("close");
	$("#not_finished_final_dialog_middle_F_sp").popup("close");
	$("#not_finished_final_dialog_late_M_sp").popup("close");
	$("#not_finished_final_dialog_late_F_sp").popup("close");
	calculateScore();
	setLanguageField();
	var submitForm = $("#sexRiskForm"); 
	var token = getAuthenticationToken();
    $.ajax({
    	beforeSend: function (xhr) {
		    xhr.setRequestHeader ("Authorization", token );
	    },
        "cache": false,
        "data": submitForm.serialize(),
        "type": "POST",
        "url": submitForm.attr("action"),
        "timeout": 30000, // optional if you want to handle timeouts (which you should)
        "error": handleFinishFormError, // this sets up jQuery to give me errors
        "success": function (xml) {
        	window.parent.closeIframe();
        }
    });
}

function handleFinishFormError() {
	if (english) {
	    $("#finish_error_dialog_" + ageRange + "_" + gender).popup("open", { transition: "pop"});
	} else {
		$("#finish_error_dialog_" + ageRange + "_" + gender + "_sp").popup("open", { transition: "pop"});
	}
}

function calculateScore() {
	var score = 0;
	var maxQuestions = 0;
	if (gender.toUpperCase() === "M") {
		if (age < 15) {
			ageRange = "early";
			maxQuestions = 3;
		} else if (age >= 15 && age < 18) {
			ageRange = "middle";
			maxQuestions = 4;
		} else if (age >= 18) {
			ageRange = "late";
			maxQuestions = 5;
		}
	} else if (gender.toUpperCase() === "F") {
		if (age < 15) {
			ageRange = "early";
			maxQuestions = 4;
		} else if (age >= 15 && age < 18) {
			ageRange = "middle";
			maxQuestions = 4;
		} else if (age >= 18) {
			ageRange = "late";
			maxQuestions = 5;
		}
	}
		
	var valueFound = false;
	for (var i = 1; i <= maxQuestions; i++) {
	    if (english) {
	    	$("input[name=QuestionEntry_" + i + "_" + ageRange + "_" + gender + "]:checked").each(function() {
	    		valueFound = true;
	    		var value = parseInt($(this).val())
	            score = score + value;
	        });
	    } else {
	    	$("input[name=QuestionEntry_" + i + "_2_" + ageRange + "_" + gender + "]:checked").each(function() {
	    		valueFound = true;
	    		var value = parseInt($(this).val())
	            score = score + value;
	        });
	    }
    }
	
	if (valueFound) {
		$("#SexRiskScore").val(score);
	}
}

function areAllQuestionsAnswered() {
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
	}
	
	var questionName = "QuestionEntry_";
	for (var i = 1; i < 6; i++) {
		var fieldName = questionName + i + spanishChar + "_" + ageRange + "_" + gender;
		if ($("#" + fieldName + "_Yes").length) {
			if (!$("input[name='" + fieldName +"']:checked").val()) {
			   return false;
			}
		}
	}
	
	return true;
}

function changePage() {
    var newPage = "#question_page_" + ageRange + "_" + gender;
    if (!english) {
        newPage = newPage + "_sp";
    }
    
    $.mobile.changePage(newPage, { transition: "none", reverse: false });
}

function showBlockingMessage() {
	var message = "Saving Answers...";
	if (!english) {
		message = "Ahorrar Respuestas...";
	}
	
	var blockUIMessage = '<table><tr><td><h3><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif" /></h3></td><td style="white-space: nowrap;vertical-align: center;"><h3>&nbsp;' + message + '</h3></td></tr></table>';
	$.blockUI({ css: { 
        border: "1px solid black", 
        padding: "15px", 
        width: "300px",
        backgroundColor: "#A9A9A9", 
        "-webkit-border-radius": "10px", 
        "-moz-border-radius": "10px", 
        color: "#000" 
    }, 
    message: blockUIMessage});
}