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
	
	// Keep track of each answer so that we can determine the value
	// for scoring and ISQResearch
	var answers = [0,0,0,0,0,0,0,0,0,0];
	var scorableAnswers = [1,2,4,5,6,8]; // Do not include question 3, 7, 9, and 10 in the total score
	
	for (var i = 1; i < 11; i++) {
	    if (english) {	
	    	answers[i -1] = parseInt($("input[name=ISQQuestionEntry_" + i + "]:checked").val());
	    	
	    } else {	
	    	answers[i -1] = parseInt($("input[name=ISQQuestionEntry_" + i + "_2]:checked").val());
	    }
    }
	
	// Determine ISQScore
	for(var i = 0; i < scorableAnswers.length; i++){
		var value = answers[scorableAnswers[i]-1];
		score += value;
	}
	
		$("#ISQScore").val(score);
		
		// Determine ISQProb
		if(score >= 6)
		{
			$("#ISQProb").val(1);
		}
		else
		{
			$("#ISQProb").val(0);
		}
		
		// Determine ISQSevere
		if(score >= 12)
		{
			$("#ISQSevere").val(1);
		}
		else
		{
			$("#ISQSevere").val(0);
		}
		
		// Determine ISQResearch
		// Here is the criteria 
		// (Question2Value >=5 OR Question4Value >=5) AND (Question3Value >=2 OR Question7Value >=2)
		// AND (Question1Value >=3 OR Question5Value >=3 OR Question6Value >= 2 OR Question8Value >=3)
		if((answers[1] >=5 || answers[3] >=5) && (answers[2] >=2 || answers[6] >=2) && (answers[0] >=3 || answers[4] >=3 || answers[5] >= 2 || answers[7] >=3)){
			$("#ISQResearch").val(1);
		}
		else{
			$("#ISQResearch").val(0);
		}
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    
    // Transfer the answers for the 10 questions
    for (var i = 1; i < 11; i++) {
    	if (english) {
	    	setQuestionCheckboxes("ISQQuestionEntry_" + i + "_2", "ISQQuestionEntry_" + i);
	    } else {
	    	setQuestionCheckboxes("ISQQuestionEntry_" + i, "ISQQuestionEntry_" + i + "_2");
	    }
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
	
	var questionName = "ISQQuestionEntry_";
	for (var i = 1; i < 11; i++) {
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