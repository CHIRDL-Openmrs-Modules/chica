
$(document).on("pageinit", function() {
    pageInit();
	
	$('#NextNoInsulin').hide();
	$('#PreviousNoInsulin').hide();
	$('#NextNoInsulin_sp').hide();
	$('#PreviousNoInsulin_sp').hide();
	$('#NextShotsPump').hide();
	$('#NextShotsPump_sp').hide();
	$('#PreviousShotsPump').hide();
	$('#PreviousShotsPump_sp').hide();
	
	$("#DiabetesHistory_2_SHOTS, #DiabetesHistory_2_2_SHOTS").click(function() { 
		$("#question_insulin_pump_container").hide();
		$("#question_insulin_pump_container_sp").hide();
		$("#question_shots_container").show();
		$("#question_shots_container_sp").show();
		$("#DiabetesHistory_12_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$("#DiabetesHistory_12_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_2_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$('#NextShotsPump').show();
		$('#NextShotsPump_sp').show();
		$('#Next').hide();
		$('#Next_sp').hide();
		$('#NextNoInsulin').hide();
		$('#NextNoInsulin_sp').hide();
		$('#Previous').show();
		$('#Previous_sp').show();
		$('#PreviousNoInsulin').hide();
		$('#PreviousNoInsulin_sp').hide();
		$('#PreviousShotsPump').show();
		$('#PreviousShotsPump_sp').show();
		$('#PreviousFirstPage').hide();
		$('#PreviousFirstPage_sp').hide();
		
		$("#question_hypoglycemia_container").show();
		$("#question_hypoglycemia_container_sp").show();
	});
	
	$("#DiabetesHistory_2_NOT_ON_INSULIN, #DiabetesHistory_2_2_NOT_ON_INSULIN").click(function() {
		
		$("#DiabetesHistory_3_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_3_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_4_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_4_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_5_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_5_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_6_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_6_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_7_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_7_No").prop("checked", false).checkboxradio('refresh');
		
		$("#DiabetesHistory_10_ME").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_PARENT_OTHER").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_ALL").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$("#DiabetesHistory_3_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_3_2_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_4_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_4_2_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_5_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_5_2_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_6_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_6_2_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_7_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_7_2_No").prop("checked", false).checkboxradio('refresh');
		
		$("#DiabetesHistory_10_2_ME").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_2_PARENT_OTHER").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_2_ALL").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_12_2_No").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_13_2_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$('#Next').hide();
		$('#Next_sp').hide();
		$('#NextNoInsulin').show();
		$('#NextNoInsulin_sp').show();
		$('#Previous').hide();
		$('#Previous_sp').hide();
		$('#PreviousNoInsulin').show();
		$('#PreviousNoInsulin_sp').show();
		$('#NextShotsPump').hide();
		$('#NextShotsPump_sp').hide();
		
		$("#question_hypoglycemia_container").hide();
		$("#question_hypoglycemia_container_sp").hide();
	});

	$("#DiabetesHistory_2_INSULIN_PUMP, #DiabetesHistory_2_2_INSULIN_PUMP").click(function() {
		$("#question_shots_container").hide();
		$("#question_shots_container_sp").hide();
		$("#question_insulin_pump_container").show();
		$("#question_insulin_pump_container_sp").show();
		
		$("#DiabetesHistory_10_ME").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_PARENT_OTHER").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_ALL").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$("#DiabetesHistory_10_2_ME").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_2_PARENT_OTHER").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_10_2_ALL").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_MORE_THAN_ONCE_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_ONCE_A_WEEK").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_MORE_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_ONCE_A_MONTH").prop("checked", false).checkboxradio('refresh');
		$("#DiabetesHistory_11_2_LESS_THAN_ONCE_MONTH").prop("checked", false).checkboxradio('refresh');
		
		$('#Next').hide();
		$('#Next_sp').hide();
		$('#NextShotsPump').show();
		$('#NextShotsPump_sp').show();
		$('#NextNoInsulin').hide();
		$('#NextNoInsulin_sp').hide();
		$('#Previous').show();
		$('#Previous_sp').show();
		$('#PreviousNoInsulin').hide();
		$('#PreviousNoInsulin_sp').hide();
		$('#PreviousFirstPage').hide();
		$('#PreviousFirstPage_sp').hide();
		$('#PreviousShotsPump').show();
		$('#PreviousShotsPump_sp').show();
		
		$("#question_hypoglycemia_container").show();
		$("#question_hypoglycemia_container_sp").show();
	});
	
});

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

  
function areAllQuestionsAnswered() {

	var spanishExtension = "_2";
	if (english) {
		spanishExtension = "";
	}

	var questionName = "DiabetesHistory_";
	var noInsulin = false;
	var shots = false;
	var pump = false;
	
	var insulinMethod = ["DiabetesHistory_2" + spanishExtension];
	var noInsulinArray = ["DiabetesHistory_3" + spanishExtension, "DiabetesHistory_4" + spanishExtension, "DiabetesHistory_5" + spanishExtension, "DiabetesHistory_6" + spanishExtension, "DiabetesHistory_7" + spanishExtension, "DiabetesHistory_10" + spanishExtension, "DiabetesHistory_11" + spanishExtension, "DiabetesHistory_12" + spanishExtension, "DiabetesHistory_13" + spanishExtension];
	var shotsArray = ["DiabetesHistory_12" + spanishExtension, "DiabetesHistory_13" + spanishExtension];
	var pumpArray = ["DiabetesHistory_10" + spanishExtension,"DiabetesHistory_11" + spanishExtension];
	
	for (var i = 1; i <= numQuestions; i++) {
		var choiceName = questionName + i + spanishExtension;

		if ($.inArray(choiceName, insulinMethod ) > -1 ) {
			var value =  $(":radio[name='" + choiceName + "']:checked").val(); 
			if (value == 1) {
				noInsulin = true;
			} else if (value == 2) {
				shots = true;
			} else if (value == 3) {
				pump = true;
			}
		}
		
		if ($.inArray(choiceName, noInsulinArray ) > -1  && noInsulin) {
			if (!noInsulin) { 
				if(!$("input[name='" + choiceName + "']").is(':checked')){
					return false;
				}
			}
		} else if ($.inArray(choiceName, shotsArray ) > -1 && shots) {
			if (!shots) {
				if(!$("input[name='" + choiceName + "']").is(':checked')){
					return false;
				}
			}
		} else if ($.inArray(choiceName, pumpArray ) > -1 && pump) {
			if (!pump) {
				if(!$("input[name='" + choiceName + "']").is(':checked')){
					return false;
				}
			}
		} else {
			if(!$("input[name='" + choiceName + "']").is(':checked')){
				return false;
			}
		}
	}
	return true;
}

function calculateScore() { 

	var questionName = "DiabetesHistory_";
	var concerningHistory = [1,10,11,12,13,3];
	var hypoglycemia = [4,5,6,7];
	var hyperglycemia = [8,9];
	
	var spanishExtension = "_2";
	if (english) spanishExtension = "";
	
	var DHConcerningHistory = {};	
	var DHHypoglycemiaMap = {};	
	var DHHyperglycemiaMap = {};	
	
	var DHInterpretations = [];
	for (var i = 0, len = concerningHistory.length; i < len; i++) {
		var value =  $(":radio[name='" + questionName + concerningHistory[i] + spanishExtension +"']:checked").val();  
		if (value) {
			DHConcerningHistory[concerningHistory[i]] = value;
		}
	}
	for (var key in DHConcerningHistory) {
		if (DHConcerningHistory[1] == "yes" || DHConcerningHistory[10] == 2 || DHConcerningHistory[11] == 1 || DHConcerningHistory[12] == "no" || DHConcerningHistory[13] == 1 || 		DHConcerningHistory[3] == "no") {
			DHInterpretations.push("ConcerningHistory");
			break;
		}
	}

	
	for (var i = 0, len = hypoglycemia.length; i < len; i++) {
		var value =  $(":radio[name='" + questionName + hypoglycemia[i] + spanishExtension +"']:checked").val(); 
		if (value) {
			DHHypoglycemiaMap[hypoglycemia[i]] = value;
		}
	}
	for (var key in DHHypoglycemiaMap) {
		if (DHHypoglycemiaMap[4] == "no" || DHHypoglycemiaMap[5] == "yes" || DHHypoglycemiaMap[6] == "yes" || DHHypoglycemiaMap[7] == "no") {
			DHInterpretations.push("Hypoglycemia");
			break;
		}
	}

	
	for (var i = 0, len = hyperglycemia.length; i < len; i++) {
		var value =  $(":radio[name='" + questionName + hyperglycemia[i] + spanishExtension +"']:checked").val();  
		if (value) {
			DHHyperglycemiaMap[hyperglycemia[i]] = value;
		}
	}
	for (var key in DHHyperglycemiaMap) {
		if (DHHyperglycemiaMap[8] == "no" || DHHyperglycemiaMap[9] == "no") {
			DHInterpretations.push("Hyperglycemia");
			break;
		}
	}
	
	var DHInterpretationSplitValues = null;
	if (DHInterpretations.length){
		DHInterpretationSplitValues = DHInterpretations.toString().split(',').join('^^');
		$("#DiabetesInterpretation").val(DHInterpretationSplitValues);
	}
}

