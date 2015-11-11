var patientListFail = 0;

$(document).on("pagecreate", "#patient_list_page", function(){
	
	$(document).ajaxStart(function() {
	    $.mobile.loading('show');
	});

	$(document).ajaxStop(function() {
	    $.mobile.loading('hide');
	});
	
	$("#hiddenField").click(function() {
		$("#passcode").focus();
	});
	
	$.mobile.changePage("#passcode_page", { transition: "fade"});
});

$(document).on("pageshow", "#passcode_page", function(){
	$("#hiddenField").trigger("click");
	$('#passcode').keypress(function(e){
	    if (e.which == 13) {
	     checkPasscode();    
	    }
	});
	
	$("#invalidPasscode").popup({
        afterclose: function( event, ui ) {
	    	$("#passcode").focus();
	    }
    });
	
	$("#passcodeError").popup({
        afterclose: function( event, ui ) {
	    	$("#passcode").focus();
	    }
    });
	
	$("#error_dialog").popup({
        afterclose: function( event, ui ) {
	    	$("#passcode").focus();
	    }
    });
	
	if($("#span_errorMessage").text().length > 0)
	{
		 $("#error_dialog").popup("open", { transition: "pop"});
	}
	
	$("#goButton").focus(function() {
		  $("#goButton").click();
	});
});

function startTimer() {
    // This delay allows the wait cursor to display when loading the patient list.
	patientListFail = 0;
	$("#listError").popup("close");
    setTimeout("populateList()", 1);
}

function finishForm(patientId, encounterId, sessionId) {
	$("#loadingDialog").popup("open", { transition: "pop"});
	//$.mobile.loading("show");
    $("#patientId").val(patientId);
    $("#encounterId").val(encounterId);
    $("#sessionId").val(sessionId);
    login(parsePatientSelectionResult, handleListAuthenticationAjaxError);
}

function checkPasscode() {
    var passcode = $("#passcode").val();
    var url = "/openmrs/moduleServlet/chica/chicaMobile";
    var action = "action=verifyPasscode&passcode=" + passcode;
    var token = getAuthenticationToken();
    $.ajax({
    	beforeSend: function (xhr) {
		    xhr.setRequestHeader ("Authorization", token );
	    },
        "cache": false,
        "dataType": "xml",
        "data": action,
        "type": "POST",
        "url": url,
        "timeout": 30000, // optional if you want to handle timeouts (which you should)
        "error": handlePasscodeAjaxError, // this sets up jQuery to give me errors
        "success": function (xml) {
            parsePasscodeResult(xml);
        }
    });
}

function populateList() {
    var url = "/openmrs/moduleServlet/chica/chicaMobile";
    var token = getAuthenticationToken();
    $.ajax({
    	beforeSend: function (xhr) {
		    xhr.setRequestHeader ("Authorization", token );
	    },
        "cache": false,
        "dataType": "xml",
        "data": "action=patientsWithPrimaryForm",
        "type": "POST",
        "url": url,
        "timeout": 30000, // optional if you want to handle timeouts (which you should)
        "error": handlePatientListAjaxError, // this sets up jQuery to give me errors
        "success": function (xml) {
        	patientListFail = 0;
            parsePatientList(xml);
            setTimeout("populateList()", 30000);
        }
    });
}

function handlePatientListAjaxError(xhr, textStatus, error) {
	patientListFail++;
	if (patientListFail < 4) {
		// try populating again before informing user.
		setTimeout("populateList()", 1);
	} else {
	    var error = "An error occurred on the server.";
	    if (textStatus === "timeout") {
	        error = "The server took too long to retrieve the patient list.";
	    }
	    
	    $("#listErrorResultDiv").html("<p>" + error + "</p>");
	    $("#listError").popup("open", { transition: "pop"});
	}
}

function handlePasscodeAjaxError(xhr, textStatus, error) {
    var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to verify the passcode.";
    }
    
    $("#passcodeErrorResultDiv").html("<p>" + error + "</p>");
    $("#passcodeError").popup("open", { transition: "pop"});
}

function parsePatientList(responseXML) {
    // no matches returned
	$("#loadingDialog").popup("close");
    if (responseXML === null) {
        return false;
    } else {
        var content = "";
        var error = $(responseXML).find("error").text();
        if (error != null && error.trim().length > 0) {
    		$("#listErrorResultDiv").html("<p>" + error + "</p>");
            $("#listError").popup("open", { transition: "pop"});
        }
        
        var count = 2;
        $(responseXML).find("patient").each(function () {
            var firstName = $(this).find("firstName").text();
            var lastName = $(this).find("lastName").text();
            var patientId = $(this).find("id").text();
            var encounterId = $(this).find("encounterId").text();
            var sessionId = $(this).find("sessionId").text();
            var reprintStatus = $(this).find("reprintStatus").text();
            var flagStatus = "";
            if (reprintStatus === "true") {
            	flagStatus = "* ";
            }
            
            var theme = "b";
            if ((count%2) == 1) {
            	theme = "b";
            }
            
            content = content + '<li data-theme ="' + theme + '"onclick="finishForm(' + patientId + ', ' + encounterId + ', ' + sessionId + ')" id="' + patientId + '" data-role="list-divider"><h1 style="font-size:20px;"><span style="color:red">' + flagStatus + "</span>" + firstName + ' ' + lastName + '</h1></li>';
            count++;
        });

        content = content + "</ul>";
        content = content + '<form id="submitForm" method="POST" data-ajax="false"><input type="hidden" name="patientId" id="patientId" value="" /><input type="hidden" name="encounterId" id="encounterId" value="" /><input type="hidden" name="sessionId" id="sessionId" value="" />';
        $(responseXML).find("patient").each(function () {
            var patientId = $(this).find("id").text();
            var encounterId = $(this).find("encounterId").text();
            var formLoop = 0;
            var formInstanceNode = $(this).find("formInstance");
            if (formInstanceNode !== null) {
                var formId = $(formInstanceNode).find("formId").text();
                var formInstanceId = $(formInstanceNode).find("formInstanceId").text();
                var locationId = $(formInstanceNode).find("locationId").text();
                content = content + '<input type="hidden" name="' + patientId + '_' + encounterId + '_formId" value="' + formId + '" />';
                content = content + '<input type="hidden" name="' + patientId + '_' + encounterId + '_formInstanceId" value="' + formInstanceId + '" />';
                content = content + '<input type="hidden" name="' + patientId + '_' + encounterId + '_locationId" value="' + locationId + '" />';
            }
        });

        content = content + "</form>";
        $("#patientList").html(content);
        $("div[type='patient_page']").page();
        $("#patientList").listview("refresh");
    }
}

function parsePasscodeLoginResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "true") {
        	checkPasscode();
        } else {
        	$("#listLoginResultDiv").html("<p>Your session has timed out.  Please log in.</p>");
            $("#listLogIn").popup("open", { transition: "pop"});
        }
    }
}

function parsePatientSelectionResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "true") {
        	$("#submitForm").submit();
        } else {
        	$("#listLoginResultDiv").html("<p>Your session has timed out.  Please log in.</p>");
            $("#listLogIn").popup("open", { transition: "pop"});
        }
    }
}

function handleListAuthenticationAjaxError(xhr, textStatus, error) {
    $("#listLoginResultDiv").html("<p>An error occurred on the patient list: " + error + ".  Please log in.</p>");
    $("#listLogIn").popup("open", { transition: "pop"});
}

function handlePasscodeAuthenticationAjaxError(xhr, textStatus, error) {
    $("#passcodeLoginDiv").html("<p>An error occurred checking the passcode: " + error + ".  Please log in.</p>");
    $("#logInPasscode").popup("open", { transition: "pop"});
}

function parsePasscodeResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "success") {
        	$.mobile.changePage("#patient_list_page", { transition: "fade"});
            startTimer();
        } else {
        	if (result == "Please log in.") {
        		login(parsePasscodeLoginResult, handlePasscodeAuthenticationAjaxError);
            } else {
	            $("#passcodeResultDiv").html("<p>" + result + "</p>");
	            $("#invalidPasscode").popup("open", { transition: "pop"});
            }
        }
    }
}