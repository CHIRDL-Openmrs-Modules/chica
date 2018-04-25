
$(document).on("pageinit", function() {
	pageInit();
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
	
	var dietician = $("input[name='" + questionName + 6 + spanishExtension + "']:checked").val(); 
	if (dietician == "yes") 
		$("#Dietician").val("positive_diet_visit");
	
	var diabetesEducator = $("input[name='" + questionName + 7 + spanishExtension + "']:checked").val(); 
	if (diabetesEducator == "yes") 
		$("#DiabetesEducator").val("positive_diet_educator_visit");
	
}