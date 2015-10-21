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
	
	var showVitals = window.parent.shouldShowVitalsButton();
	if (!showVitals) {
		$(".vitalsButton").hide();
	}
}

function submitEmptyForm() {
	setLanguageField();
	document.getElementById("phq9Form").submit();
}

function setLanguageField() {
	if (english) {
		$("#language").val("english");
	} else {
		$("#language").val("spanish");
	}
}

function setLanguage(patientName, birthdate) {
	english = !english;
    var langButtonText = "Español";
    var additionalQuestions = "The following are some additional questions about depression.";
    var instructions = '<p>Over the <span style="text-decoration: underline;">last 2 weeks</span>, how often have you been bothered by any of the following problems?</p>';
    var startButtonText = "Start";
    var vitalsButtonText = "Vitals";
    if (!english) {
        langButtonText = "English";
        additionalQuestions = "Las preguntas siguientes son adicionales acerca de la depresión.";
        instructions = '<p>Durante las últimas 2 semanas, ¿qué tan seguido le han afectado cualquiera de los siguientes problemas?</p>';
        startButtonText = "Comienzo";
        vitalsButtonText = "Vitales";
    }
    
    $("#confirmLangButton .ui-btn-text").text(langButtonText);
    $("#additionalQuestions").text(additionalQuestions);
    $("#instructions").html(instructions);
    $("#startButton .ui-btn-text").text(startButtonText);
    $(".vitalsButton .ui-btn-text").text(vitalsButtonText);
}

function changePage(newPageNum) {
    var newPage = "#question_page_" + newPageNum;
    if (!english) {
        newPage = newPage + "_sp";
    }
    
    $.mobile.changePage(newPage, { transition: "none", reverse: false });
}

function calculateScore() {
	var score = 0;
	var valueFound = false;
	for (var i = 1; i < 10; i++) {
	    if (english) {
	    	//value = $("input:radio[name=PHQ9QuestionEntry_" + i + "]").val();
	    	$("input[name=PHQ9QuestionEntry_" + i + "]:checked").each(function() {
	    		valueFound = true;
	    		var value = parseInt($(this).val())
	            score = score + value;
	        });
	    } else {
	    	//value = $("input:radio[name=PHQ9QuestionEntry_" + i + "_2]").val();
	    	$("input[name=PHQ9QuestionEntry_" + i + "_2]:checked").each(function() {
	    		valueFound = true;
	    		var value = parseInt($(this).val())
	            score = score + value;
	        });
	    }
    }
	
	if (valueFound) {
		$("#PHQ9Score").val(score);
		
		if (score > 0 && score < 10) {
			$("#PHQ9Interpretation").val("mild");
		} else if (score > 9 && score < 20) {
			$("#PHQ9Interpretation").val("moderate");
		} else if (score > 19) {
			$("#PHQ9Interpretation").val("severe");
		}
	}
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    
    // Transfer the answers for the 4 choice questions
    for (var i = 1; i < 10; i++) {
    	if (english) {
	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_2_NAO", "#PHQ9QuestionEntry_" + i + "_NAO");
	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_2_SD", "#PHQ9QuestionEntry_" + i + "_SD");
	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_2_MTHD", "#PHQ9QuestionEntry_" + i + "_MTHD");
	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_2_NED", "#PHQ9QuestionEntry_" + i + "_NED");
	    } else {
	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_NAO", "#PHQ9QuestionEntry_" + i + "_2_NAO");
	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_SD", "#PHQ9QuestionEntry_" + i + "_2_SD");
	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_MTHD", "#PHQ9QuestionEntry_" + i + "_2_MTHD");
	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_NED", "#PHQ9QuestionEntry_" + i + "_2_NED");
	    }
    }
    
//    // Transfer answers for Yes/No Questions
//    for (var i = 10; i < 14; i++) {
//	    if (english) {
//	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_2_Yes", "#PHQ9QuestionEntry_" + i + "_Yes");
//	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_2_No", "#PHQ9QuestionEntry_" + i + "_No");
//	    } else {
//	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_Yes", "#PHQ9QuestionEntry_" + i + "_2_Yes");
//	    	setQuestionCheckboxes("#PHQ9QuestionEntry_" + i + "_No", "#PHQ9QuestionEntry_" + i + "_2_No");
//	    }
//    }
    
    // Transfer answers for follow up question
    if (english) {
    	setQuestionCheckboxes("#PHQ9QuestionEntry_10_2_NDAA", "#PHQ9QuestionEntry_10_NDAA");
    	setQuestionCheckboxes("#PHQ9QuestionEntry_10_2_SD", "#PHQ9QuestionEntry_10_SD");
    	setQuestionCheckboxes("#PHQ9QuestionEntry_10_2_VD", "#PHQ9QuestionEntry_10_VD");
    	setQuestionCheckboxes("#PHQ9QuestionEntry_10_2_ED", "#PHQ9QuestionEntry_10_ED");
    } else {
    	setQuestionCheckboxes("#PHQ9QuestionEntry_10_NDAA", "#PHQ9QuestionEntry_10_2_NDAA");
    	setQuestionCheckboxes("#PHQ9QuestionEntry_10_SD", "#PHQ9QuestionEntry_10_2_SD");
    	setQuestionCheckboxes("#PHQ9QuestionEntry_10_VD", "#PHQ9QuestionEntry_10_2_VD");
    	setQuestionCheckboxes("#PHQ9QuestionEntry_10_ED", "#PHQ9QuestionEntry_10_2_ED");
    }
    
    changePage(1);
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
	calculateScore();
	var submitForm = $("#phq9Form"); 
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

function setQuestionCheckboxes(initialCheckBoxId, newCheckBoxId) {
	if ($(initialCheckBoxId).is(":checked")) {
		$(newCheckBoxId).prop("checked", true);
		$(initialCheckBoxId).prop("checked", false);
		$(newCheckBoxId).checkboxradio('refresh');
		$(initialCheckBoxId).checkboxradio('refresh');
	}
}

function areAllQuestionsAnswered() {
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
	}
	
	var questionName = "PHQ9QuestionEntry_";
	for (var i = 1; i < 10; i++) {
		if (!$("input[name='" + questionName + i + spanishChar + "']:checked").val()) {
		   return false;
		}
	}
	
	return true;
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