var english = false;
var formInstance = null;
var finishAttempts = 0;
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

function init(patientName, birthdate, formInst, language, gender) {
	// This looks backwards, but the setLanguage method reverses it.
	if (language.toUpperCase() == "ENGLISH") {
		english = false;
	} else {
		english = true;
	}
	
	setLanguage(patientName, birthdate);
	formInstance = formInst;
	
	this.gender = gender;
	
	$("#question_2_container").hide();
	$("#question_2_container_sp").hide();
	
	$("#QuestionEntry_1_Yes").click(function() {
		if (gender.toUpperCase() == "F") {
			$("#question_2_container").show();
			$("#question_2_container_sp").show();
		}
	});
	
	$("#QuestionEntry_1_2_Yes").click(function() {
		if (gender.toUpperCase() == "F") {
			$("#question_2_container").show();
			$("#question_2_container_sp").show();
		}
	});
	
	$("#QuestionEntry_1_No").click(function() {
		$("#question_2_container").hide();
		$("#question_2_container_sp").hide();
		$("#QuestionEntry_2_No").prop("checked", false).checkboxradio('refresh');
		$("#QuestionEntry_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#QuestionEntry_2_2_No").prop("checked", false).checkboxradio('refresh');
		$("#QuestionEntry_2_2_Yes").prop("checked", false).checkboxradio('refresh');
		
	});
	
	$("#QuestionEntry_1_2_No").click(function() {
		$("#question_2_container").hide();
		$("#question_2_container_sp").hide();
		$("#QuestionEntry_2_No").prop("checked", false).checkboxradio('refresh');
		$("#QuestionEntry_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#QuestionEntry_2_2_No").prop("checked", false).checkboxradio('refresh');
		$("#QuestionEntry_2_2_Yes").prop("checked", false).checkboxradio('refresh');
	});
}

function setLanguage(patientName, birthdate) {
	english = !english;
    var langButtonText = "Español";
    var additionalQuestions = "The following are some additional questions about sexual behavior.";
    var startButtonText = "Start";
    var vitalsButtonText = "Vitals";
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
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_2_Yes", "#QuestionEntry_" + i + "_Yes");
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_2_No", "#QuestionEntry_" + i + "_No");
	    } else {
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_Yes", "#QuestionEntry_" + i + "_2_Yes");
	    	setQuestionCheckboxes("#QuestionEntry_" + i + "_No", "#QuestionEntry_" + i + "_2_No");
	    }
    }
    
    changePage(1);
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
    	    $("#not_finished_dialog").popup("open", { transition: "pop"});
    	} else {
    		$("#not_finished_dialog_sp").popup("open", { transition: "pop"});
    	}
	} else if (finishAttempts >= 2) {
		if (english) {
    	    $("#not_finished_final_dialog").popup("open", { transition: "pop"});
    	} else {
    		$("#not_finished_final_dialog_sp").popup("open", { transition: "pop"});
    	}
	}
}

function finishForm() {
	//run an AJAX post request to your server-side script, $this.serialize() is the data from your form being added to the request
	//$.mobile.changePage("#empty_page");
	$("#finish_error_dialog").popup("close");
	$("#finish_error_dialog_sp").popup("close");
	$("#not_finished_final_dialog").popup("close");
	$("#not_finished_final_dialog_sp").popup("close");
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
	    $("#finish_error_dialog").popup("open", { transition: "pop"});
	} else {
		$("#finish_error_dialog_sp").popup("open", { transition: "pop"});
	}
}

function areAllQuestionsAnswered() {
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
	}
	
	var questionName = "QuestionEntry_1" + spanishChar;
	if (!$("input[name='" + questionName +"']:checked").val()) {
	   return false;
	}
	
    if (gender.toUpperCase() == "F" && $("input[name='" + questionName +"']").val() == "yes") {
		questionName = "QuestionEntry_2" + spanishChar;
		if (!$("input[name='" + questionName +"']:checked").val()) {
		   return false;
		}
    }
	
	return true;
}

function changePage(pageNum) {
    var newPage = "#question_page_" + pageNum;
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