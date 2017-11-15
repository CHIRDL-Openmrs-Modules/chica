var english = false;
var formInstance = null;
var numberOfQuestions = 0;
var finishAttempts = 0;
var formTitleText = "";
var TRAQInformantText = "";
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
	numberOfQuestions = $("input[id^='TRAQQuestion_']").length / 2;

}


function submitEmptyForm() {
	setLanguageField();
	document.getElementById("TRAQForm").submit();
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
	
	var formTitleText = "Transition Readiness Assessment Questionnaire " + openParen + "TRAQ" + closeParen;
	var langButtonText = "Espa" + nTilde + "ol";
	var startButtonText = "Start";
    var vitalsButtonText = "Staff";
    TRAQInformantText = "Check here if you are a parent " + slash + " caregiver completing this form."
   
    var instructions = "<p><em class=\"bolderNonItalic\">Directions to Youth and Young Adults</em>: <em class=\"underline\">Please check the box that best describes <em class=\"bolder\">your</em> "
    	+ "skill level </em> in the following areas that are important for transition to adult health care. There is no right or wrong "
    	+ "answer and your answers will remain confidential and private.</p><hr/>";
    
    instructions += "<p><em class=\"bolderNonItalic\">Directions to Caregivers" + slash + "Parents" + colon + "</em> If your youth or young adult is unable to "
    		+ "complete the tasks below on their own, please check the box that best describes <em class=\"bolderNonItalic\">your</em> skill level.</p>";
    
    
    if (!english) {
    	
    	formTitleText = "Cuestionario de Evaluaci" + oAcute + "n para la Preparaci" + oAcute + "n de la Transici" + oAcute + "n " + openParen + "TRAQ" + closeParen;
        langButtonText = "English"; 
        startButtonText = "Comienzo";
        vitalsButtonText = "Personal";
        
         instructions = "<p><em class=\"bolderNonItalic\">Instrucciones para los j" + oAcute + "venes</em>" + colon + " Por favor marc" + aAcute 
         + " con una cruz la opci" + oAcute + "n que mejor describa tu capacidad para cada una de las siguientes " + aAcute 
         + "reas que son importantes para la transici" + oAcute + "n del cuidado de tu salud a la medicina del adulto. "
         + "No hay respuestas correctas ni incorrectas y las respuestas ser" + aAcute + "n confidenciales y privadas.</p><hr/>";
         
         instructions += "<p><em class=\"bolderNonItalic\">Instrucciones para padres" + slash + "cuidadores</em>" + colon + " Si el joven que est" + aAcute 
         + " a su cuidado no tiene la capacidad de comprender el signiﬁcado por s" + iAcute + " mismo de las preguntas que se mencionan m" + aAcute 
         + "s abajo, por favor, resp" + oAcute + "ndalas en forma conjunta y marque con una cruz el casillero que mejor describa la capacidad "
         + "del joven para realizarlas.</p>";
         
         TRAQInformantText = "Marque con una cruz, si usted es (padres " + slash + " cuidadores) quien est" + aAcute + " completando el formulario.";
       
	}
    
    
    $("#confirmLangButton .ui-btn-text").html(langButtonText);
    $("#instructions").html(instructions);
    $("#startButton .ui-btn-text").html(startButtonText);
    $(".vitalsButton .ui-btn-text").text(vitalsButtonText);
    $("#formTitle").html(formTitleText);
    $("#TRAQInformantCheckboxLabel .ui-btn-text").html(TRAQInformantText);
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
	calculateTransitionReadiness();
	var submitForm = $("#TRAQForm"); 
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
	var spanishExtension = "_2";
	if (english) {
		spanishExtension = "";
	}
	
	var questionName = "TRAQQuestionEntry_";
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
	
	
	var choiceDoNotKnow = "No, I do not know how.";
	var choiceNoButWantToLearn = "No, but I want to learn.";
	var choiceNoButLearning = "No, but I am learning to do this.";
	var choiceYesStarted = "Yes, I have started doing this.";
	var choiceYesAlways = "Yes, I always do this when I need to.";
	
	if (isSpanishQuestion(questionNumber)){
		choiceDoNotKnow  = "No, no s" + eAcute + " c" + oAcute + "mo hacerlo.";
		choiceNoButWantToLearn  = "No, pero quiero aprender a hacerlo."; 
		choiceNoButLearning  = "No, pero estoy aprendiendo a hacerlo."; 
		choiceYesStarted  = "S" + iAcute + ", ya he comendzado a hacerlo."; 
		choiceYesAlways  = "S" + iAcute + ", lo hago siempre que lo necesito."; 
	}
	
	fieldSetElement = $(document.createElement("fieldset"));
	fieldSetElement.attr({
		"data-role": "controlgroup",
		"data-type": "vertical"
	});
	
	var fieldSet = '';
	
	fieldSet += '<input type="radio" name="TRAQQuestionEntry_' + questionNumber + '" id="TRAQQuestionEntry_' + questionNumber + '_DO_NOT_KNOW" value="1" data-theme="b" />';
	fieldSet += '<label for="TRAQQuestionEntry_' + questionNumber + '_DO_NOT_KNOW">' + choiceDoNotKnow + '</label>';
	fieldSet += '<input type="radio" name="TRAQQuestionEntry_' + questionNumber +'" id="TRAQQuestionEntry_' + questionNumber + '_WANT_TO_LEARN" value="2" data-theme="b" />';
	fieldSet += '<label for="TRAQQuestionEntry_' + questionNumber + '_WANT_TO_LEARN">' + choiceNoButWantToLearn + '</label>';
	fieldSet += '<input type="radio" name="TRAQQuestionEntry_' + questionNumber + '" id="TRAQQuestionEntry_' + questionNumber + '_LEARNING" value="3" data-theme="b" />';
    fieldSet += '<label for="TRAQQuestionEntry_' + questionNumber + '_LEARNING">' + choiceNoButLearning + '</label>';
    fieldSet += '<input type="radio" name="TRAQQuestionEntry_' + questionNumber + '" id="TRAQQuestionEntry_' + questionNumber + '_STARTED" value="4" data-theme="b" />';
    fieldSet += '<label for="TRAQQuestionEntry_' + questionNumber + '_STARTED">' + choiceYesStarted + '</label>';
    fieldSet += '<input type="radio" name="TRAQQuestionEntry_' + questionNumber + '" id="TRAQQuestionEntry_' + questionNumber + '_ALWAYS" value="5" data-theme="b" />';
    fieldSet += '<label for="TRAQQuestionEntry_' + questionNumber + '_ALWAYS">' + choiceYesAlways + '</label>';
	
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


function meetsCompletionCriteria() {
	var countCompleted = 0;
	var completionStatus = "Incomplete"; 
	var formReady = false;
	var questionName = "TRAQQuestion";
	
	countCompleted = $("input[name^=" + questionName + "]:checked").length;
	    
	if (countCompleted >= questionCompletionCriteria) {
		completionStatus = "Complete";
		formReady = true;
	}

	$("#TRAQ").val(completionStatus);
	
	return formReady;
}

function calculateTransitionReadiness() {

	
	if (!meetsCompletionCriteria()){
		return;
	}
	var questionName = "TRAQQuestionEntry_";
	var questionPassCriteria = 3;
	// Keep track of each answer so that we can determine if patient failed transition
	//for that topic
	//var answers = [-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1];
	var managingMedicationAnswers = [1,2,3,4];
	var appointmentKeepingAnswers = [5,6,7,8,9,10,11];
	var trackingHealthIssuesAnswers = [12,13,14,15];
	var talkingWithProvidersAnswers = [16,17];
	var dailyActivitiesAnswers = [18,19,20];
	var eval = null;
	

	//If any are answered with value < 3, then TRAQ category failed
	//If any are answered where all values >= 3 then TRAQ category passed
	//If no questions are answered, then observation is not saved.
	// Determine Manage Medication Answers
	var spanishExtension = "_2";
	if (english) spanishExtension = "";
	var transitionReady = null;
	for(var i = 0, len= managingMedicationAnswers.length; i < len; i++){
		var value = parseInt($("input[name=TRAQQuestionEntry_" + managingMedicationAnswers[i] + spanishExtension + "]:checked").val());
		if (isNaN(value)){
			continue;
		}
		if (value < questionPassCriteria){
			transitionReady = false;
			break;
		}
		transitionReady = true;
	}
	if (transitionReady != null ){
		$("#TRAQManagingMedications").val((transitionReady)? "passed" : "failed");
	}
	
	
	transitionReady = null;
	for(var i = 0, len= appointmentKeepingAnswers.length; i < len; i++){
		var value = parseInt($("input[name=TRAQQuestionEntry_" + appointmentKeepingAnswers[i] + spanishExtension + "]:checked").val());
		if (isNaN(value)){
			continue;
		}
		if (value < questionPassCriteria){
			transitionReady = false;
			break;
		}
		transitionReady = true;
	}
	if (transitionReady != null ){
		$("#TRAQAppointmentKeeping").val((transitionReady)? "passed" : "failed");
	}
	
	transitionReady = null;
	for(var i = 0, len= trackingHealthIssuesAnswers.length; i < len; i++){
		var value = parseInt($("input[name=TRAQQuestionEntry_" + trackingHealthIssuesAnswers[i] + spanishExtension + "]:checked").val());
		if (isNaN(value)){
			continue;
		}
		if (value < questionPassCriteria){
			transitionReady = false;
			break;
		}
		transitionReady = true;
	}
	if (transitionReady != null ){
		$("#TRAQTrackingHealthIssues").val((transitionReady)? "passed" : "failed");
	}
	
	transitionReady = null;
	for(var i = 0, len= talkingWithProvidersAnswers.length; i < len; i++){
		var value = parseInt($("input[name=TRAQQuestionEntry_" + talkingWithProvidersAnswers[i] + spanishExtension + "]:checked").val());
		if (isNaN(value)){
			continue;
		}
		if (value < questionPassCriteria){
			transitionReady = false;
			break;
		}
		transitionReady = true;
	}
	if (transitionReady != null ){
		$("#TRAQTalkingWithProviders").val((transitionReady)? "passed" : "failed");
	}
	
	transitionReady = null;
	for(var i = 0, len= dailyActivitiesAnswers.length; i < len; i++){
		var value = parseInt($("input[name=TRAQQuestionEntry_" + dailyActivitiesAnswers[i] + spanishExtension + "]:checked").val());
		if (isNaN(value)){
			continue;
		}
		if (value < questionPassCriteria){
			transitionReady = false;
			break;
		}
		transitionReady = true;
	}
	if (transitionReady != null ){
		$("#TRAQDailyActivitiesAnswers").val((transitionReady)? "passed" : "failed");
	}
	
	
	
	
	
}


