var english = false;
var formInstance = null;
var numberOfQuestions = 0;
var finishAttempts = 0;
var formTitleText = "";
var questionCompletionCriteria = 15;



var openParen = "&#40";
var closeParen = "&#41";
var nTilde = "&#241";
var colon = "&#58";
var comma = "&#44";
var slash = "&#47";
var semicolon = "&#58";
var questionMark = "&#63";
var apostrophe = "&#39";
var slash = "&#47";
var oAcute = "&#243";
var aAcute = "&#225";
var eAcute = "&#233";
var uAcute = "&#250";
var iAcute = "&#237";
var invQuestionMark = "&#191";
var copyrightSymbol = "&#169";
var ampersand = "&#38";
var hyphen = "$#45";
var NTilde = "&#181";
var period = "&#46";


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
	numberOfQuestions = $("input[id^='EatingDisorderQuestion_']").length / 2;

}


function submitEmptyForm() {
	setLanguageField();
	document.getElementById("EatingDisorderForm").submit();
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
	
	//Strings with spanish characters need hex code
	//HTML with spanish characters can use HTML codes
	var formTitleText = "Eating Habits";
	var langButtonText = "Espa" + nTilde + "ol";
	var startButtonText = "Start";
    var vitalsButtonText = "Staff";
 
    var instructions = "<p> Please choose the option that best describes your eating habits.</p>"; 
    
    //Need spanish translation
    f (!english) {
    	
    	formTitleText = "";
        langButtonText = "English"; 
        startButtonText = "Comienzo";
        vitalsButtonText = "Personal";    
        instructions = "<p></p>";
         
	}
    
    $("#confirmLangButton .ui-btn-text").html(langButtonText);
    $("#instructions").html(instructions);
    $("#startButton .ui-btn-text").html(startButtonText);
    $(".vitalsButton .ui-btn-text").text(vitalsButtonText);
    $("#formTitle").html(formTitleText);
   
}

function changePage(newPageNum) {
    var newPage = "#question_page_" + newPageNum;
    if (!english) {
        newPage = newPage + "_sp";
    }   
    $.mobile.changePage(newPage, { transition: "none", reverse: false });
}

function setLanguageFromForm(patientName, birthdate) {
    setLanguage(patientName, birthdate);
    
    // Transfer the answers for the questions
    for (var i = 1; i <= numberOfQuestions; i++) {
    	if (english) {
	    	setQuestionCheckboxes("TRAQQuestionEntry_" + i + "_2", "TRAQQuestionEntry_" + i);
	    } else {
	    	setQuestionCheckboxes("TRAQQuestionEntry_" + i, "TRAQQuestionEntry_" + i + "_2");
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
	$("#not_finished_dialog").popup("close");
	$("#not_finished_dialog_sp").popup("close");
	setLanguageField();
	interpretResults();
	var submitForm = $("#EatingDisorderForm"); 
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
	
	
	var questionName = "EatingDisorderQuestionEntry_";
	for (var i = 1; i <= numberOfQuestions; i++) {
		if(!$("input[name='" + questionName + i  + "']").is(':checked')){
		   return false;
		}
	}
	
	return true;
}


function insertChoices(questionNumber){
	
	
	var choiceNever = "Never";
	var choiceRarely = "Rarely";
	var choiceSometimes = "Sometimes";
	var choiceOften = "Often";
	var choiceUsually = "Usually";
	var choiceAlways = "Always";
	
	
	fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "vertical"
	});
	
	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="EatingDisorderQuestionEntry_' + questionNumber + '" id="EatingDisorderQuestionEntry_' + questionNumber + '_NEVER" value="0" data-theme="b" />';
	fieldSet += '<label for="EatingDisorderQuestionEntry_' + questionNumber + '_NEVER">' + choiceNever + '</label>';
	fieldSet += '<input type="radio" name="EatingDisorderQuestionEntry_' + questionNumber +'" id="EatingDisorderQuestionEntry_' + questionNumber + '_RARELY" value="1" data-theme="b" />';
	fieldSet += '<label for="EatingDisorderQuestionEntry_' + questionNumber + '_RARELY">' + choiceRarely + '</label>';
	fieldSet += '<input type="radio" name="EatingDisorderQuestionEntry_' + questionNumber + '" id="EatingDisorderQuestionEntry_' + questionNumber + '_SOMETIMES" value="2" data-theme="b" />';
    fieldSet += '<label for="EatingDisorderQuestionEntry_' + questionNumber + '_SOMETIMES">' + choiceSometimes + '</label>';
    fieldSet += '<input type="radio" name="EatingDisorderQuestionEntry_' + questionNumber + '" id="EatingDisorderQuestionEntry_' + questionNumber + '_OFTEN" value="3" data-theme="b" />';
    fieldSet += '<label for="EatingDisorderQuestionEntry_' + questionNumber + '_OFTEN">' + choiceOften + '</label>';
    fieldSet += '<input type="radio" name="EatingDisorderQuestionEntry_' + questionNumber + '" id="EatingDisorderQuestionEntry_' + questionNumber + '_USUALLY" value="4" data-theme="b" />';
    fieldSet += '<label for="EatingDisorderQuestionEntry_' + questionNumber + '_USUALLY">' + choiceUsually + '</label>';
	fieldSet += '<input type="radio" name="EatingDisorderQuestionEntry_' + questionNumber + '" id="EatingDisorderQuestionEntry_' + questionNumber + '_ALWAYS" value="5" data-theme="b" />';
    fieldSet += '<label for="EatingDisorderQuestionEntry_' + questionNumber + '_ALWAYS">' + choiceAlways + '</label>';
	
    fieldSetElement.append(fieldSet);
   
	$(".choice"+questionNumber).append(fieldSetElement);
	$(".choice"+questionNumber).triggerHandler("create");
	
	
}

function showBlockingMessage() {
	var message = "Saving Answers...";
	
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
//Set interpretation from eJIT answers
function interpretResults() {

	var answer = {};
	answer[1] = 'losingweight';
	answer[2] = 'skipmeals';
	answer[3] = 'otheroutofcontrol';
	answer[4] = 'toolittleinsulin';
	answer[5] = 'morealone';
	answer[6] = 'hardloseweight';
	answer[7] = 'avoidchecking';
	answer[8] = 'makevomit';
	answer[9] = 'keephigh';
	answer[10] = 'skipmeals';
	answer[11] = 'spillketones';
	answer[12] = 'feelfat';
	answer[13] = 'skipdose';
	answer[14] = 'outofcontrol';
	answer[15] = 'alternateeating';
	answer[16] = 'ratherbethin';
	var score = {};

	var edscore = 0;
	var never = 0;
	var often = 3;
	for(var i = 1; i<= numberOfQuestions;  i++){
		var value = parseInt($("input[name=EatingDisorderQuestionEntry_" + i + "]:checked").val());
		if (!isNaN(value)){
			score[answer[i]] = value;
			edscore = edscore + value;
		}
	}
	$("#EDS_interpretation").val("negative");
	if ((edscore >= 20 ||
		 score['toolittleinsulin'] > never ||
		 score['makevomit'] > never ||
		 score['keephigh'] > never ||
		 score['spillketones'] > never ||
		 score['feelfat'] > never ||
		 score['skipdose'] > never ||
		 score['alternateeating'] > never ||
		 score['ratherbethin'] > never ||
		 score['losingweight'] > often ||
		 score['skipmeals'] > often ||
		 score['outofcontrol'] > often ||
		 score['morealone'] > often ||
		 score['hardloseweight'] > often ||
		 score['avoidchecking'] > often ||
		 score['takebettercare'] > often ))
		{
		 	$("#EDS_interpretation").val("positive");
		}
}


