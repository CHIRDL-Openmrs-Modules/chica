var english = false;
var formInstance = null;

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
    var additionalQuestions = "The following are some additional questions about alcohol and drug use.";
    var startButtonText = "Start";
    var quitButtonText = "Quit";
    if (!english) {
        langButtonText = "English";
        additionalQuestions = "Las preguntas siguientes son adicionales acerca del consumo de alcohol y las drogas.";
        startButtonText = "Comienzo";
        quitButtonText = "Dejar";
    }
    
    $("#confirmLangButton .ui-btn-text").text(langButtonText);
    $("#additionalQuestions").text(additionalQuestions);
    $("#startButton .ui-btn-text").text(startButtonText);
    $(".quitButton .ui-btn-text").text(quitButtonText);
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    
    // Transfer answers
    for (var i = 1; i < 7; i++) {
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
	document.getElementById("CRAFFTForm").submit();
}

function setLanguageField() {
	if (english) {
		$("#language").val("english");
	} else {
		$("#language").val("spanish");
	}
}

function attemptFinishForm() {
	if (areAllQuestionsAnswered()) {
		finishForm(); 
	} else{
		if (english) {
    	    $("#not_finished_dialog").popup("open", { transition: "pop"});
    	} else {
    		$("#not_finished_dialog_sp").popup("open", { transition: "pop"});
    	}
	}
}

function finishForm() {
	//run an AJAX post request to your server-side script, $this.serialize() is the data from your form being added to the request
	if (english) {
		$("#finish_error_dialog").popup("close");
		$("#not_finished_dialog").popup("close");
	} else {
		$("#finish_error_dialog_sp").popup("close");
		$("#not_finished_dialog_sp").popup("close");
	}
	
	setLanguageField();
	calculateScore();
	var submitForm = $("#CRAFFTForm"); 
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
		$("#CRAFFTScore").val(score);
	}
}

function areAllQuestionsAnswered() {
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
	}
	
	var questionName = "QuestionEntry_";
	for (var i = 1; i < 7; i++) {
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
	
	var blockUIMessage = '<table><tr><td><h3><img src="' + ctx + '/moduleResources/chica/images/ajax-loader.gif" /></h3></td><td style="white-space: nowrap;vertical-align: center;"><h3>&nbsp;' + message + '</h3></td></tr></table>';
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