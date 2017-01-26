var patientListFail = 0;
var timeOutVar;
var refreshPeriod = 60000; // Use global property to override this, see startTimer() below
var defaultAjaxTimeout = 60000;

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
	
	// DWE CHICA-761
    $("#showAllCheckbox").click(function(){
    	clearTimeout(timeOutVar);
    	populateList();
    });
    
    // DWE CHICA-884
    $("#confidentialityOKButton").click(function () {
    	finishConfidentialityDialog();
        return false;
    });
    
	$.mobile.changePage("#passcode_page", { transition: "fade"});
});

$(document).ready(function(){
	// DWE CHICA-761
	// Add additional click event listener to the default jquery clear button behavior
	// IMPORTANT: DO NOT call populateList() from here
	// This will send too many requests to the server
	// Especially if the user is typing slow
	$('#searchAllPatientsDIV a.ui-input-clear').on('click', function(event){
    	$("#searchAllPatients").val("");
		clearTimeout(timeOutVar); // prevent the list from being refreshed by the timer on the page
		filterPatientList();
		timeOutVar = setTimeout("populateList()", refreshPeriod);	
	});
	
	// Fix to allow the search field to display correctly in IE
	$('#searchAllPatientsDIV div.ui-input-search').addClass("searchAllPatientsInput");
});

$(document).on("pageshow", "#patient_list_page", function(){
	// DWE CHICA-761
	// Bind debounce functionality so that the 
	// search is performed only after the user has stopped typing for 1000ms
	// IMPORTANT: DO NOT call populateList() from here
	// This will send too many requests to the server
	// Especially if the user is typing slow
	$("#searchAllPatients").on('keyup', $.debounce(function(event){
		var searchValue = $("#searchAllPatients").val();
		
		// Only perform the search if
		// the enter key was pressed, there has been >= 1 characters entered, or
		// the backspace key was pressed
		if(event.keyCode == 13 || searchValue.length >=1 || event.keyCode == 8)
		{
			clearTimeout(timeOutVar); // prevent the list from being refreshed by the timer on the page
			filterPatientList();
			timeOutVar = setTimeout("populateList()", refreshPeriod);	
		}
	}, 1000, false));
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
	
	var period = $("#refreshPeriod").val();
	if(period.length > 0)
	{
		refreshPeriod = period * 1000;
	}
	
	timeOutVar = setTimeout("populateList()", 1);
}

function finishForm(patientId, encounterId, sessionId, ageInYears, firstName) {
	clearTimeout(timeOutVar);
    $("#patientId").val(patientId);
    $("#encounterId").val(encounterId);
    $("#sessionId").val(sessionId);
    
    if(($("#displayConfidentialityNoticeMobileGreaseBoard").val() === 'true') && (ageInYears >= 12))
    {
		$("#confidentialityNoticeHeader h3").text("Confidentiality Notice");
		$("#confidentialityNoticeDiv").html("<p>Please tell the family that " + firstName + " should answer each question.</p>");
		$("#confidentialityOKButton .ui-btn-text").text('OK');
    	$("#confidentialityDialog").popup("open", { transition: "pop"});
    }
    else
    {
    	$("#loadingDialog").popup("open", { transition: "pop"});
    	login(parsePatientSelectionResult, handleListAuthenticationAjaxError);
    }
}

function checkPasscode() {
    var passcode = $("#passcode").val();
    var url = ctx + "/moduleServlet/chica/chicaMobile";
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
        "timeout": defaultAjaxTimeout, // optional if you want to handle timeouts (which you should)
        "error": handlePasscodeAjaxError, // this sets up jQuery to give me errors
        "success": function (xml) {
            parsePasscodeResult(xml);
        }
    });
}

function populateList() {
	var showAllPatients = $("#showAllCheckbox").is(':checked'); // DWE CHICA-761
    var url = ctx + "/moduleServlet/chica/chicaMobile";
    var token = getAuthenticationToken();
    $.ajax({
    	beforeSend: function (xhr) {
		    xhr.setRequestHeader ("Authorization", token );
	    },
        "cache": false,
        "dataType": "xml",
        "data": "action=patientsWithPrimaryForm&showAllPatients=" + showAllPatients,
        "type": "POST",
        "url": url,
        "timeout": defaultAjaxTimeout, // optional if you want to handle timeouts (which you should)
        "error": handlePatientListAjaxError, // this sets up jQuery to give me errors
        "success": function (xml) {
        	patientListFail = 0;
            parsePatientList(xml);
            clearTimeout(timeOutVar); // Clear the timer here so that multiple timers don't exist (the user clicks the refresh button several times)
            timeOutVar = setTimeout("populateList()", refreshPeriod);
        }
    });
}

function handlePatientListAjaxError(xhr, textStatus, error) {
	patientListFail++;
	if (patientListFail < 4) {
		// try populating again before informing user.
		timeOutVar = setTimeout("populateList()", 1);
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
            
            // DWE CHICA-761 Allow the user to search by patient name or MRN
            var mrn = removeSpecialCharacters($(this).find("mrn").text()); //.replace(new RegExp('-', 'g'), ''); 
            var fullName = removeSpecialCharacters(firstName) + ' ' + removeSpecialCharacters(lastName);
            
            // DWE CHICA-884
            var ageInYears = $(this).find("ageInYears").text();
            var newFirstName = firstName.replace("'", "\\'");
            
            content = content + '<li data-theme ="' + theme + '"onclick="finishForm(' + patientId + ', ' + encounterId + ', ' + sessionId + ', ' + ageInYears + ', \'' + newFirstName + '\');" id="' + patientId + '" data-role="list-divider" data-mrn="' + mrn + '" data-fullname="' + fullName + '"><h1 style="font-size:20px;"><span style="color:red">' + flagStatus + "</span>" + firstName + ' ' + lastName + '</h1></li>';
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
        
        filterPatientList(); // DWE CHICA-761
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

// DWE CHICA-761
// Filter using the value entered in the search field
// Searching will be performed with white space, special characters, and leading zeros removed
function filterPatientList()
{
    var searchString = removeLeadingZeros(removeSpecialCharacters(removeWhiteSpace($("#searchAllPatients").val())));
    var regExp = new RegExp(searchString, "i");
    
    $("#patientList li").each(function () {
    	if ($(this).data("fullname").search(regExp) < 0 && $(this).data("mrn").search(regExp) < 0) {
            $(this).hide();
        } else {
            $(this).show()
        }
    });
}

//DWE CHICA-884
//Binds the popafterclose event to the confidentiality dialog to allow the loading dialog to display
//and then submit the form
function finishConfidentialityDialog()
{
	$("#confidentialityDialog").on("popupafterclose", function(){$("#loadingDialog").popup("open", { transition: "pop"}); login(parsePatientSelectionResult, handleListAuthenticationAjaxError);});
	$("#confidentialityDialog").popup("close");
	
}