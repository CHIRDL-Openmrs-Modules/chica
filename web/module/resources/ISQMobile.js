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
	document.getElementById("ISQForm").submit();
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
    var additionalQuestions = "Here are a number of questions about your baby's sleeping habits.";
    var instructions = '<p>Please base your answers on what you have noticied over the last MONTH.</p>';
    var startButtonText = "Start";
    var vitalsButtonText = "Vitals";
    if (!english) {
        langButtonText = "English";
        additionalQuestions = "A continuación encontrará una serie de preguntas sobre los hábitos de sueño de su bebé.";
        instructions = '<p>Base sus respuestas en lo que haya observado durante el último MES.</p>';
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
	    	//value = $("input:radio[name=ISQQuestionEntry_" + i + "]").val();
	    	$("input[name=ISQQuestionEntry_" + i + "]:checked").each(function() {
	    		valueFound = true;
	    		var value = parseInt($(this).val())
	            score = score + value;
	        });
	    } else {
	    	//value = $("input:radio[name=ISQQuestionEntry_" + i + "_2]").val();
	    	$("input[name=ISQQuestionEntry_" + i + "_2]:checked").each(function() {
	    		valueFound = true;
	    		var value = parseInt($(this).val())
	            score = score + value;
	        });
	    }
    }
	
	if (valueFound) {
		$("#ISQScore").val(score);
		
		if (score > 0 && score < 10) {
			$("#ISQInterpretation").val("mild");
		} else if (score > 9 && score < 20) {
			$("#ISQInterpretation").val("moderate");
		} else if (score > 19) {
			$("#ISQInterpretation").val("severe");
		}
	}
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    
    // Transfer the answers for the 4 choice questions
    for (var i = 1; i < 10; i++) {
    	if (english) {
	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_2_NAO", "#ISQQuestionEntry_" + i + "_NAO");
	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_2_SD", "#ISQQuestionEntry_" + i + "_SD");
	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_2_MTHD", "#ISQQuestionEntry_" + i + "_MTHD");
	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_2_NED", "#ISQQuestionEntry_" + i + "_NED");
	    } else {
	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_NAO", "#ISQQuestionEntry_" + i + "_2_NAO");
	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_SD", "#ISQQuestionEntry_" + i + "_2_SD");
	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_MTHD", "#ISQQuestionEntry_" + i + "_2_MTHD");
	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_NED", "#ISQQuestionEntry_" + i + "_2_NED");
	    }
    }
    
//    // Transfer answers for Yes/No Questions
//    for (var i = 10; i < 14; i++) {
//	    if (english) {
//	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_2_Yes", "#ISQQuestionEntry_" + i + "_Yes");
//	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_2_No", "#ISQQuestionEntry_" + i + "_No");
//	    } else {
//	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_Yes", "#ISQQuestionEntry_" + i + "_2_Yes");
//	    	setQuestionCheckboxes("#ISQQuestionEntry_" + i + "_No", "#ISQQuestionEntry_" + i + "_2_No");
//	    }
//    }
    
    // Transfer answers for follow up question
    if (english) {
    	setQuestionCheckboxes("#ISQQuestionEntry_10_2_NDAA", "#ISQQuestionEntry_10_NDAA");
    	setQuestionCheckboxes("#ISQQuestionEntry_10_2_SD", "#ISQQuestionEntry_10_SD");
    	setQuestionCheckboxes("#ISQQuestionEntry_10_2_VD", "#ISQQuestionEntry_10_VD");
    	setQuestionCheckboxes("#ISQQuestionEntry_10_2_ED", "#ISQQuestionEntry_10_ED");
    } else {
    	setQuestionCheckboxes("#ISQQuestionEntry_10_NDAA", "#ISQQuestionEntry_10_2_NDAA");
    	setQuestionCheckboxes("#ISQQuestionEntry_10_SD", "#ISQQuestionEntry_10_2_SD");
    	setQuestionCheckboxes("#ISQQuestionEntry_10_VD", "#ISQQuestionEntry_10_2_VD");
    	setQuestionCheckboxes("#ISQQuestionEntry_10_ED", "#ISQQuestionEntry_10_2_ED");
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
	var submitForm = $("#ISQForm"); 
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
	
	var questionName = "ISQQuestionEntry_";
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