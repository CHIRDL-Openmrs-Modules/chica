var english = false;
var formInstance = null;
var finishAttempts = 0;

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

function init(patientName, birthdate, formInst, language) {
	// This looks backwards, but the setLanguage method reverses it.
	if (language.toUpperCase() == "ENGLISH") {
		english = false;
	} else {
		english = true;
	}
	
	setLanguage(patientName, birthdate);
	formInstance = formInst;
}

function setLanguage(patientName, birthdate) {
	english = !english;
    var langButtonText = "Español";
    // var additionalQuestions = "Please answer these questions about your child. Keep in mind how your child usually behaves. If you have seen your child do the behavior a few times, but he or she does not usually do it, then please answer no. Please circle yes or no for every question. Thank you very much.";
	var additionalQuestions = "Please fill out the following about how your child usually is. Please try to answer every question. If the behavior is rare (e.g., you've seen it once or twice), please answer as if the child does not do it.";
    var startButtonText = "Start";
    var vitalsButtonText = "Vitals";
    if (!english) {
        langButtonText = "English";
        // additionalQuestions = "Por favor responda a estas preguntas sobre su hijo/a. Tenga en cuenta cómo su hijo/a se comporta habitualmente. Si usted ha visto a su hijo/a comportarse de una de estas maneras algunas veces, pero no es un comportamiento habitual, por favor responda no. Seleccione, rodeando con un círculo. Muchas gracias.";
		additionalQuestions = "Por favor conteste acerca de como su niño(a) es usualmente. Por favor trata de contestar cada pregunta. Si el comportamiento de su niño no ocurre con frecuencia, conteste como si no lo hiciera.";
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
    for (var i = 1; i < 21; i++) {
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
	if ($(initialCheckBoxId).is(":checked")) {
		$(newCheckBoxId).prop("checked", true);
		$(initialCheckBoxId).prop("checked", false);
		$(newCheckBoxId).checkboxradio('refresh');
		$(initialCheckBoxId).checkboxradio('refresh');
	}
}

function submitEmptyForm() {
	setLanguageField();
	document.getElementById("MCHATForm").submit();
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
	if (english) {
		$("#finish_error_dialog").popup("close");
		$("#not_finished_final_dialog").popup("close");
	} else {
		$("#finish_error_dialog_sp").popup("close");
		$("#not_finished_final_dialog_sp").popup("close");
	}
	
	setLanguageField();
	calculateScore();
	var submitForm = $("#MCHATForm"); 
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

function calculateScore() {
	var score = 0;
	var valueFound = false;
	for (var i = 1; i < 7; i++) {
	    if (english) {
	    	$("input[name=QuestionEntry_" + i + "]:checked").each(function() {
	    		valueFound = true;
	    		var value = parseInt($(this).val())
	            score = score + value;
	        });
	    } else {
	    	$("input[name=QuestionEntry_" + i + "_2]:checked").each(function() {
	    		valueFound = true;
	    		var value = parseInt($(this).val())
	            score = score + value;
	        });
	    }
    }
	
	if (valueFound) {
		$("#MCHATScore").val(score);
	}
}

function areAllQuestionsAnswered() {
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
	}
	
	var questionName = "QuestionEntry_";
	for (var i = 1; i < 21; i++) {
		if (!$("input[name='" + questionName + i + spanishChar + "']:checked").val()) {
		   return false;
		}
	}
	
	return true;
}

function changePage(newPageNum) {
    var newPage = "#question_page_" + newPageNum;
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