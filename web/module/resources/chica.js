function readText(text) {
    var lang = "en";
    readIt(text, lang);
}

function readTextSpanish(text) {
    var lang = "es";
    var encodedText = encodeURIComponent(text);
    var newEncodedText = encodedText.replace("%C2%BF", "");
    newEncodedText = newEncodedText.replace("%%C2%A1", "");
    newEncodedText = newEncodedText.replace("%C3%BA", "u");
    newEncodedText = newEncodedText.replace("%C3%A1", "a");
    newEncodedText = newEncodedText.replace("%C3%A9", "e");
    newEncodedText = newEncodedText.replace("%C3%AD", "i");
    newEncodedText = newEncodedText.replace("%C3%B3", "o");
    newEncodedText = newEncodedText.replace("%C3%BC", "u");
    newEncodedText = newEncodedText.replace("%C3%B1", "n");
    var newText = decodeURIComponent(newEncodedText);
	readIt(newText, lang);
}

function readIt(text, lang) {
	var remainingText = "";
	var audioElement = document.createElement('audio'); 
    audioElement.addEventListener('ended', function(){
    	if (remainingText != "") {
    		remainingText = queueText(remainingText, lang, this);
    	}
    }, false);
	
    remainingText = queueText(text, lang, audioElement);
}

function queueText(text, lang, audioElement) {
	var remainingText = "";
	if (text.length > 100) {
		var firstChars = text.substr(0, 99);
        var commaIndex = firstChars.lastIndexOf(',');
        var periodIndex = firstChars.lastIndexOf('.');
        var questionIndex = firstChars.lastIndexOf('?');
        var exclamIndex = firstChars.lastIndexOf('?');
        if (questionIndex >= 0 && questionIndex < 100) {
        	remainingText = parseAndPlay(text, questionIndex, audioElement, lang);
        } else if (periodIndex >= 0 && periodIndex < 100) {
        	remainingText = parseAndPlay(text, periodIndex, audioElement, lang);
        } else if (exclamIndex >= 0 && exclamIndex < 100) {
        	remainingText = parseAndPlay(text, exclamIndex, audioElement, lang);
        } else if (commaIndex >= 0 && commaIndex < 100) {
        	remainingText = parseAndPlay(text, commaIndex, audioElement, lang);
        } else {
        	var firstChars = text.substr(0, 99);
        	var lastSpaceIndex = firstChars.lastIndexOf(" ");
        	remainingText = parseAndPlay(text, lastSpaceIndex, audioElement, lang);
        }
    } else {
        audioElement.setAttribute('src', "http://translate.google.com/translate_tts?tl=" + lang + "&q=" + encodeURIComponent(text)); 
        audioElement.play();
    }
	
	return remainingText;
}

function parseAndPlay(text, index, audioElement, lang) {
	var speak = text.substr(0, index + 1);
    var remainingText = text.substr(index + 1, text.length);
    audioElement.setAttribute('src', "http://translate.google.com/translate_tts?tl=" + lang + "&q=" + encodeURIComponent(speak));
    audioElement.play();
    return remainingText;
}

function login(successFunction, errorFunction) {
//	$.mobile.loading("show");
	var username = "";
	var password = "";
	var encryptKey = window.localStorage.getItem("keyDate");
	if (encryptKey == null || encryptKey.trim().length == 0) {
//		$.mobile.loading("hide");
		return;
	}
	
	var encryptedUsername = window.localStorage.getItem("username");
	if (encryptedUsername != null) {
		var decrypted = CryptoJS.AES.decrypt(encryptedUsername, encryptKey);
	    username = decrypted.toString(CryptoJS.enc.Utf8);
	}
	
	var encryptedPassword = window.localStorage.getItem("password");
	if (encryptedPassword != null) {
		var decrypted = CryptoJS.AES.decrypt(encryptedPassword, encryptKey);
	    password = decrypted.toString(CryptoJS.enc.Utf8);
	}
	
    var url = "/openmrs/moduleServlet/chica/chicaMobile";
    var action = "action=authenticateUser&username=" + username + "&password=" + password;
    $.ajax({
        "cache": false,
        "async": true,
        "dataType": "xml",
        "data": action,
        "type": "POST",
        "url": url,
        "timeout": 30000, // optional if you want to handle timeouts (which you should)
        "error": errorFunction, // this sets up jQuery to give me errors
        "success": function (xml) {
        	successFunction(xml);
        	$.mobile.loading('hide');
        }
    });
}