var english = false;
var formInstance = null;
var finishAttempts = 0;
var numQuestions = 0;

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
	
	numQuestions = $("input[id^='SCAREDQuestion_']").length / 2; // Divide by 2 to handle Spanish version
}

function submitEmptyForm() {
	setLanguageField();
	document.getElementById("SCAREDForm").submit();
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
    var additionalQuestions = "Here is a list of sentences that describe how people feel. Read each phrase and decide if it is \"Not True or Hardly Ever True\" or \"Somewhat True or Sometimes True\" or \"Very True or Often True\" for your child.";
    var instructions = '<p>Choose the response that seems to describe your child <span style="text-decoration: underline;">for the last 3 months.</span></p>';
    var startButtonText = "Start";
    var vitalsButtonText = "Vitals";
    if (!english) {
        langButtonText = "English";
        additionalQuestions = "";
        instructions = '<p></p>';
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
	var questionName = "SCAREDQuestionEntry_";
	
	for (var i = 1; i <= numQuestions; i++) {
	    if (english) {	
	    	if($("input[name=" + questionName + i + "]").is(':checked')){
	    		score += parseInt($("input[name=" + questionName + i + "]:checked").val());
	    	}
	    } else {
	    	if($("input[name=" + questionName + i + "_2]").is(':checked')){
	    		score += parseInt($("input[name=" + questionName + i + "_2]:checked").val());
	    	}	
	    }
    }
	
	if(score > 0){
		$("#SCAREDScore").val(score);
	}
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    
    // Transfer the answers for the 5 questions
    for (var i = 1; i <= numQuestions; i++) {
    	if (english) {
	    	setQuestionCheckboxes("SCAREDQuestionEntry_" + i + "_2", "SCAREDQuestionEntry_" + i);
	    } else {
	    	setQuestionCheckboxes("SCAREDQuestionEntry_" + i, "SCAREDQuestionEntry_" + i + "_2");
	    }
    }
        
    changePage(1);
}

function attemptFinishForm() {
	if (areAllQuestionsAnswered()) {
		finishForm(); 
	} else{
		if (english) {
    	    $("#not_finished_final_dialog").popup("open", { transition: "pop"});
    	} else {
    		$("#not_finished_final_dialog_sp").popup("open", { transition: "pop"});
    	}
	}
}

function finishForm() {
	//run an AJAX post request to your server-side script, $this.serialize() is the data from your form being added to the request
	$("#finish_error_dialog").popup("close");
	$("#finish_error_dialog_sp").popup("close");
	$("#not_finished_final_dialog").popup("close");
	$("#not_finished_final_dialog_sp").popup("close");
	setLanguageField();
	calculateScore();
	var submitForm = $("#SCAREDForm"); 
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

function setQuestionCheckboxes(initialName, newName) {
	// Determine if any of the radio buttons in the group are selected
	// If so, select the English/Spanish version
	if($("input[name='" + initialName + "']").is(':checked')) {
		var selectedValue = $("input[name='" + initialName + "']:checked").val();
		
		// Select the radio button by name and value
		$("input[name='" + newName + "'][value='" + selectedValue + "']").prop("checked",true);
		$("input[name='" + initialName + "'][value='" + selectedValue + "']").prop("checked",false);
		$("input[name='" + newName + "'][value='" + selectedValue + "']").checkboxradio('refresh');
		$("input[name='" + initialName + "'][value='" + selectedValue + "']").checkboxradio('refresh');
	}
}

function areAllQuestionsAnswered() {
	var spanishChar = "_2";
	if (english) {
		spanishChar = "";
	}
	
	var questionName = "SCAREDQuestionEntry_";
	for (var i = 1; i <= numQuestions; i++) {
		if(!$("input[name='" + questionName + i + spanishChar + "']").is(':checked')){
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