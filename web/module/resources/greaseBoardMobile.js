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
	
	$("#goButton").focus(function() {
		  $("#goButton").click();
	});
});

function startTimer() {
    var timer = $.timer(function () {
        populateList();
    });

    timer.set({
        time: 30000,
        autostart: true
    });
    
//    populateList();
    // This delay allows the wait cursor to display when loading the patient list.
    setTimeout("populateList()", 250);
}

function finishForm(id) {
	$("#loadingDialog").popup("open");
	//$.mobile.loading("show");
    $("#patientId").val(id);
    login(parsePatientSelectionResult, handleListAuthenticationAjaxError);
}

function checkPasscode() {
//    $.mobile.loading("show");
    var passcode = $("#passcode").val();
    var url = "/openmrs/moduleServlet/chica/chicaMobile";
    var action = "action=verifyPasscode&passcode=" + passcode;
    $.ajax({
        "cache": false,
            "dataType": "xml",
            "data": action,
            "type": "POST",
            "url": url,
            "timeout": 30000, // optional if you want to handle timeouts (which you should)
        "error": handlePasscodeAjaxError, // this sets up jQuery to give me errors
        "success": function (xml) {
//        	$.mobile.loading("show");
            parsePasscodeResult(xml);
//            $.mobile.loading("hide");
        }
    });
}

function populateList() {
//    $.mobile.loading("show");
    var url = "/openmrs/moduleServlet/chica/chicaMobile";
    $.ajax({
        "cache": false,
            "dataType": "xml",
            "data": "action=patientsWithForms",
            "type": "POST",
            "url": url,
            "timeout": 30000, // optional if you want to handle timeouts (which you should)
        "error": handlePatientListAjaxError, // this sets up jQuery to give me errors
        "success": function (xml) {
            parsePatientList(xml);
//            $.mobile.loading("hide");
        }
    });
}

function handlePatientListAjaxError(xhr, textStatus, error) {
//	$.mobile.loading("hide");
    var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to retrieve the patient list.";
    }
    
    $("#listErrorResultDiv").html("<p>" + error + "</p>");
    $("#listError").popup("open");
}

function handlePasscodeAjaxError(xhr, textStatus, error) {
//    $.mobile.loading("hide");
    var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to verify the passcode.";
    }
    
    $("#passcodeErrorResultDiv").html("<p>" + error + "</p>");
    $("#passcodeError").popup("open");
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
        	if (error == "Please log in.") {
        		//$("#listLoginResultDiv").html("<p>Your session has timed out.  Please log in.</p>");
	            //$("#listLogIn").popup("open");
        		login(parseLoginResult, handleListAuthenticationAjaxError);
        	} else {
        		$("#listErrorResultDiv").html("<p>" + error + "</p>");
	            $("#listError").popup("open");
        	}
        }
        
        var count = 2;
        $(responseXML).find("patient").each(function () {
            var firstName = $(this).find("firstName").text();
            var lastName = $(this).find("lastName").text();
            var patientId = $(this).find("id").text();
            var theme = "b";
            if ((count%2) == 1) {
            	theme = "b";
            }
            
            content = content + '<li data-theme ="' + theme + '"onclick="finishForm(' + patientId + ')" id="' + patientId + '" data-role="list-divider"><h1>' + firstName + ' ' + lastName + '</h1></li>';
            count++;
        });

        content = content + "</ul>";
        content = content + '<form id="submitForm" method="POST" data-ajax="false"><input type="hidden" name="patientId" id="patientId" value="" />';
        $(responseXML).find("patient").each(function () {
            var patientId = $(this).find("id").text();
            var formLoop = 0;
            $(this).find("formInstances").each(function () {
                var formId = $(this).find("formId").text();
                var formInstanceId = $(this).find("formInstanceId").text();
                var locationId = $(this).find("locationId").text();
                content = content + '<input type="hidden" name="' + patientId + '_formId_' + formLoop + '" value="' + formId + '" />';
                content = content + '<input type="hidden" name="' + patientId + '_formInstanceId_' + formLoop + '" value="' + formInstanceId + '" />';
                content = content + '<input type="hidden" name="' + patientId + '_locationId_' + formLoop + '" value="' + locationId + '" />';
                formLoop = formLoop + 1;
            });
        });

        content = content + "</form>";
        $("#patientList").html(content);
        $("div[type='patient_page']").page();
        $("#patientList").listview("refresh");
    }
}

function parseLoginResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "true") {
        	populateList();
        } else {
        	$("#listLoginResultDiv").html("<p>Your session has timed out.  Please log in.</p>");
            $("#listLogIn").popup("open");
        }
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
            $("#listLogIn").popup("open");
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
            $("#listLogIn").popup("open");
        }
    }
}

function handleListAuthenticationAjaxError(xhr, textStatus, error) {
//    $.mobile.loading("hide");
    $("#listLoginResultDiv").html("<p>An error occurred on the patient list: " + error + ".  Please log in.</p>");
    $("#listLogIn").popup("open");
}

function handlePasscodeAuthenticationAjaxError(xhr, textStatus, error) {
//    $.mobile.loading("hide");
    $("#passcodeLoginDiv").html("<p>An error occurred checking the passcode: " + error + ".  Please log in.</p>");
    $("#logInPasscode").popup("open");
}

function parsePasscodeResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
        var result = $(responseXML).find("result").text();
        if (result == "success") {
        	$.mobile.changePage("#patient_list_page", { transition: "fade"});
//        	$.mobile.loading("show");
            startTimer();
        } else {
        	if (result == "Please log in.") {
	            //$("#passcodeLoginDiv").html("<p>Your session has timed out.  Please log in.</p>");
	            //$("#logInPasscode").popup("open");
        		login(parsePasscodeLoginResult, handlePasscodeAuthenticationAjaxError);
            } else {
	            $("#passcodeResultDiv").html("<p>" + result + "</p>");
	            $("#invalidPasscode").popup("open");
            }
        }
    }
}