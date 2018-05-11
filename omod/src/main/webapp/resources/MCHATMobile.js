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
	var additionalQuestions = "Please fill out the following about how your child usually is. Please try to answer every question. If the behavior is rare (e.g., you've seen it once or twice), please answer as if the child does not do it.";
    var startButtonText = "Start";
    var vitalsButtonText = "Staff";
    if (!english) {
        langButtonText = "English";
		additionalQuestions = "Por favor conteste acerca de como su niño(a) es usualmente. Por favor trata de contestar cada pregunta. Si el comportamiento de su niño no ocurre con frecuencia, conteste como si no lo hiciera.";
        startButtonText = "Comienzo";
        vitalsButtonText = "Personal";
    }
    
    $("#confirmLangButton .ui-btn-text").text(langButtonText);
    $("#additionalQuestions").text(additionalQuestions);
    $("#startButton .ui-btn-text").text(startButtonText);
    $(".vitalsButton .ui-btn-text").text(vitalsButtonText);
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    
    // Transfer answers
    for (var i = 1; i < 24; i++) {
	    if (english) {
	    	setQuestionCheckboxes("#Choice_" + i + "_sp_Yes", "#Choice_" + i + "_Yes");
	    	setQuestionCheckboxes("#Choice_" + i + "_sp_No", "#Choice_" + i + "_No");
	    } else {
	    	setQuestionCheckboxes("#Choice_" + i + "_Yes", "#Choice_" + i + "_sp_Yes");
	    	setQuestionCheckboxes("#Choice_" + i + "_No", "#Choice_" + i + "_sp_No");
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
	if (areAllQuestionsAnswered()) {
		finishForm();
	} else {
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
	var MchatTotalItemsFailed = 0;
	var MchatCriticalItemsFailed = 0;
	var critical = [2,7,9,13,14,15];
	var valueFound = false; 
	var criticalValueFound = false;
	for (var i = 1; i < 24; i++) {
		if (english) {
			$("input[name=Choice_" + i + "]:checked").each(function() {
				valueFound = true;
				var value = $(this).val();
				if(value == "failed"){
					MchatTotalItemsFailed++;
				}				
				//var value = parseInt($(this).val());
				//MchatTotalItemsFailed += value;
				//if(critical.indexOf(i) > -1 && value == 1) {
				if(critical.indexOf(i) > -1) {
					criticalValueFound = true;
					if (value == "failed") {
						MchatCriticalItemsFailed++;
					}
				}
			});
		} else {
			$("input[name=Choice_" + i + "_sp]:checked").each(function() {
				valueFound = true;
				var value = $(this).val();
				if (value == "failed"){
					MchatTotalItemsFailed++;
				}
				//var value = parseInt($(this).val());
				//MchatTotalItemsFailed += value;
				//if(critical.indexOf(i) > -1 && value == 1) {
				if(critical.indexOf(i) > -1) {
					criticalValueFound = true;
					if (value == "failed") {
						MchatCriticalItemsFailed++;
					}
				}
			});
		}
	
	}
	
	if (valueFound) {
		$("#MchatTotalItemsFailed").val(MchatTotalItemsFailed);
	}
	
	if (criticalValueFound) {
		$("#MchatCriticalItemsFailed").val(MchatCriticalItemsFailed);
	}
	
	//alert("Total Items Failed: " + MchatTotalItemsFailed + "\nTotal Critical Items Failed: " + MchatCriticalItemsFailed);
}

function areAllQuestionsAnswered() {
	var spanishChar = "_sp";
	if (english) {
		spanishChar = "";
	}
	
	var questionName = "Choice_";
	for (var i = 1; i < 24; i++) {
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