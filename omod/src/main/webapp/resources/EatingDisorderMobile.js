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
	numberOfQuestions = $("input[id^='EatingDisorderQuestion_']").length;

}


function submitEmptyForm() {
	setLanguageField();
	document.getElementById("EatingDisorderMobile.form").submit();
}

function setLanguageField() {
	if (english) {
		$("#language").val("english");
	} else {
		$("#language").val("spanish");
	}
}

function setLanguage(patientName, birthdate) {
	
	//Strings with spanish characters need hex code
	//HTML with spanish characters can use HTML codes
	
	var formTitleText = "Eating Habit Questionnaire";
    var langButtonText = "Espa" + nTilde + "ol";
	var startButtonText = "Start";
    var vitalsButtonText = "Staff";
 
    var instructions = "<p><em class=\"bolderNonItalic\">Directions to Youth and Young Adults</em>: Please check the box that best describes your "
    	+ "eating habits. There are no right or wrong answers" + comma + " and your answers will remain confidential and private.</p><hr/>";
    
    //No Spanish instructions
    
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
	$("#not_finished_final_dialog").popup("close");
	$("#not_finished_dialog").popup("close");
	
	//No Spanish popups
	
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
	$("#finish_error_dialog").popup("open", { transition: "pop"});
	}
}


function areAllQuestionsAnswered() {
	var spanishExtension = "_2";
	if (english) {
		spanishExtension = "";
	}
	
	var questionName = "EatingDisorderQuestionEntry_";
	for (var i = 1; i <= numberOfQuestions; i++) {
		if(!$("input[name='" + questionName + i + spanishExtension + "']").is(':checked')){
		   return false;
		}
	}
	
	return true;
}

function isSpanishQuestion(questionNumber){
	var spanishExtension = "_2";
	
	if (questionNumber.length <= spanishExtension.length){
		return false;
	}
	return (questionNumber.lastIndexOf(spanishExtension) == (questionNumber.length - spanishExtension.length));
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
//Completion criteria and calculations can go here.
function interpretResults() {

	
	var questionName = "EatingDisorderQuestionEntry_";
	var questionPassCriteria = 3;
	// Keep track of each answer so that we can determine if patient failed transition
	//for that topic
	//var answers = [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1];
	//var managingMedicationAnswers = [1,2,3,4];
	//var appointmentKeepingAnswers = [5,6,7,8,9,10,11];
	////var trackingHealthIssuesAnswers = [12,13,14,15];
	//var talkingWithProvidersAnswers = [16,17];
	//var dailyActivitiesAnswers = [18,19,20];
	//var eval = null;
	

	//If any are answered with value < 3, then TRAQ category failed
	//If any are answered where all values >= 3 then TRAQ category passed
	//If no questions are answered, then observation is not saved.
	// Determine Manage Medication Answers
	spanishExtension = "";
	var transitionReady = true;
	for(var i = 0, len= numberOfQuestions; i < len; i++){
		var value = parseInt($("input[name=" + questionName + " + i + spanishExtension + "]:checked").val());
		if (isNaN(value) || value < questionPassCriteria){
			transitionReady = false;
			break;
		}
	}
	
	$("#TRAQManagingMedications").val((transitionReady)? "passed" : "failed");
	
	
	
	transitionReady = true;
	for(var i = 0, len= appointmentKeepingAnswers.length; i < len; i++){
		var value = parseInt($("input[name=TRAQQuestionEntry_" + appointmentKeepingAnswers[i] + spanishExtension + "]:checked").val());
		if (isNaN(value) || value < questionPassCriteria){
			transitionReady = false;
			break;
		}
	}
	
	$("#TRAQAppointmentKeeping").val((transitionReady)? "passed" : "failed");
	
	
	transitionReady = true;
	for(var i = 0, len= trackingHealthIssuesAnswers.length; i < len; i++){
		var value = parseInt($("input[name=TRAQQuestionEntry_" + trackingHealthIssuesAnswers[i] + spanishExtension + "]:checked").val());
		if (isNaN(value) || value < questionPassCriteria){
			transitionReady = false;
			break;
		}
	}
	
	$("#TRAQTrackingHealthIssues").val((transitionReady)? "passed" : "failed");
	
	
	transitionReady = true;
	for(var i = 0, len= talkingWithProvidersAnswers.length; i < len; i++){
		var value = parseInt($("input[name=TRAQQuestionEntry_" + talkingWithProvidersAnswers[i] + spanishExtension + "]:checked").val());
		if (isNaN(value) || value < questionPassCriteria){
			transitionReady = false;
			break;
		}
	}
	
	$("#TRAQTalkingWithProviders").val((transitionReady)? "passed" : "failed");
	
	
	transitionReady = true;
	for(var i = 0, len= dailyActivitiesAnswers.length; i < len; i++){
		var value = parseInt($("input[name=TRAQQuestionEntry_" + dailyActivitiesAnswers[i] + spanishExtension + "]:checked").val());
		if (isNaN(value) || value < questionPassCriteria){
			transitionReady = false;
			break;
		}
	}
	
	$("#TRAQDailyActivitiesAnswers").val((transitionReady)? "passed" : "failed");
	
	
	
	
	
	
}


