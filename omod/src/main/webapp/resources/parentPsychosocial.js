
$(document).on("pageinit", function() {
	pageInit();
	
	$("#question_8_container").hide();
	$("#question_8_container_sp").hide();
	$("#question_9_container").hide();
	$("#question_9_container_sp").hide();
	
	$("#ParentPsychosocialQuestionEntry_6_Yes, #ParentPsychosocialQuestionEntry_6_2_Yes").click(function() {
		$("#question_8_container").show();
		$("#question_8_container_sp").show();
	});
	
	$("#ParentPsychosocialQuestionEntry_6_No, #ParentPsychosocialQuestionEntry_6_2_No").click(function() {
		$("#question_8_container").hide();
		$("#question_8_container_sp").hide();
		$("#ParentPsychosocialQuestionEntry_8_Yes").prop("checked", false).checkboxradio('refresh');
		$("#ParentPsychosocialQuestionEntry_8_No").prop("checked", false).checkboxradio('refresh');
		$("#ParentPsychosocialQuestionEntry_8_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#ParentPsychosocialQuestionEntry_8_2_No").prop("checked", false).checkboxradio('refresh');
	});
	
	$("#ParentPsychosocialQuestionEntry_7_Yes, #ParentPsychosocialQuestionEntry_7_2_Yes").click(function() {
		$("#question_9_container").show();
		$("#question_9_container_sp").show();
	});
	
	$("#ParentPsychosocialQuestionEntry_7_No, #ParentPsychosocialQuestionEntry_7_2_No").click(function() {
		$("#question_9_container").hide();
		$("#question_9_container_sp").hide();
		$("#ParentPsychosocialQuestionEntry_9_Yes").prop("checked", false).checkboxradio('refresh');
		$("#ParentPsychosocialQuestionEntry_9_No").prop("checked", false).checkboxradio('refresh');
		$("#ParentPsychosocialQuestionEntry_9_2_Yes").prop("checked", false).checkboxradio('refresh');
		$("#ParentPsychosocialQuestionEntry_9_2_No").prop("checked", false).checkboxradio('refresh');
	});
	
});

function calculateScore() {
	
	var questionName = "ParentPsychosocialQuestionEntry_";
	
	var spanishExtension = "_2";
	if (english) spanishExtension = "";
	
	for (var i = 1; i <= 5; i++) {
		var value =  $("input[name='" + questionName + i + spanishExtension + "']:checked").val();
		if (value == "yes") {
			$("#SocialWorker").val("positive_social_worker");
			break;
		}
	}
	
	var dietician = $("input[name='" + questionName + 8 + spanishExtension + "']:checked").val(); 
	if (dietician == "yes") 
		$("#Dietician").val("positive_diet_visit");
	else if (dietician == "no")
		$("#Dietician").val("positive_diet");
	
	var diabetesEducator = $("input[name='" + questionName + 9 + spanishExtension + "']:checked").val(); 
	if (diabetesEducator == "yes") 
		$("#DiabetesEducator").val("positive_diet_educator_visit");
	else if (diabetesEducator == "no")
		$("#DiabetesEducator").val("positive_diet_educator");
	
}