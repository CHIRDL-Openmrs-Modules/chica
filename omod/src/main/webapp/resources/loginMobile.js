$(document).on("pageshow", function(){
	loadCredentials();
	$("#username_field").focus();
	$("#login_button").on("click", null, function() {
		$("#loadingDialog").popup("open", { transition: "pop"});
		storeCredentials();
	    document.getElementById("loginForm").submit();
	});
	$("#password_field").keypress(function(e){
        if ( e.which == 13 ) {
        	$.mobile.loading("show");
            storeCredentials();
        	document.getElementById("loginForm").submit();  
        }
    });
	$("#username_field").keypress(function(e){
        if ( e.which == 13 ) {
        	$.mobile.loading("show");
            storeCredentials();
        	document.getElementById("loginForm").submit(); 
        }
    });
	
	var error = document.getElementById("loginResultDiv").innerHTML;
	if (error != null && error.trim().length > 0) {
		$("#invalidLogin").popup("open", { transition: "pop"});
	}
});

function storeCredentials() {
	var encryptKey = new Date();
	var encryptKeyStr = encryptKey.toDateString();
	var passVal = document.getElementById("password_field").value;
	var encryptedPassVal = CryptoJS.AES.encrypt(passVal, encryptKeyStr);
	window.localStorage.setItem("password", encryptedPassVal);
	var userVal = document.getElementById("username_field").value;
	var encryptedUserVal = CryptoJS.AES.encrypt(userVal, encryptKeyStr);
	window.localStorage.setItem("username", encryptedUserVal);
	window.localStorage.setItem("keyDate", encryptKeyStr);
}

function loadCredentials() {
	var encryptKey = window.localStorage.getItem("keyDate");
	if (encryptKey == null || encryptKey.trim().length == 0) {
		return;
	}
	
	var encryptedUsername = window.localStorage.getItem("username");
	if (encryptedUsername != null) {
		var decrypted = CryptoJS.AES.decrypt(encryptedUsername, encryptKey);
	    document.getElementById("username_field").value = decrypted.toString(CryptoJS.enc.Utf8);
	}
	var encryptedPassword = window.localStorage.getItem("password");
	if (encryptedPassword != null) {
		var decrypted = CryptoJS.AES.decrypt(encryptedPassword, encryptKey);
	    document.getElementById("password_field").value = decrypted.toString(CryptoJS.enc.Utf8);
	}
}