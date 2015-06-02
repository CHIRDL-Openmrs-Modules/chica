var audioElement = document.createElement('audio');

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
	audioElement.pause();
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
    var action = "action=authenticateUser";
    var token = getAuthenticationToken();
    $.ajax({
    	beforeSend: function (xhr) {
		    xhr.setRequestHeader ("Authorization", token );
	    },
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

function getAuthenticationToken() {
	var username = "";
	var password = "";
	var encryptKey = window.localStorage.getItem("keyDate");
	if (encryptKey == null || encryptKey.trim().length == 0) {
		return makeBaseAuth(username, password);
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
	
	return makeBaseAuth(username, password);
}

function makeBaseAuth(username, password) {
  var tok = username + ':' + password;
  var hash = "";
  if (typeof btoa === "undefined") {
      hash = Base64.encode(tok);
  } else {
	  hash = btoa(tok);
  }
  
  return "Basic " + hash;
}

var Base64 = {
		  
	    // private property
	    _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
	  
	    // public method for encoding
	    encode: function(input) {
	      var output = "";
	      var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
	      var i = 0;
	  
	      input = Base64._utf8_encode(input);
	  
	      while (i < input.length) {
	  
	        chr1 = input.charCodeAt(i++);
	        chr2 = input.charCodeAt(i++);
	        chr3 = input.charCodeAt(i++);
	  
	        enc1 = chr1 >> 2;
	        enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
	        enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
	        enc4 = chr3 & 63;
	  
	        if (isNaN(chr2)) {
	          enc3 = enc4 = 64;
	        } else if (isNaN(chr3)) {
	          enc4 = 64;
	        }
	  
	        output = output + this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) + this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);
	  
	      }
	  
	      return output;
	    },
	  
	    // public method for decoding
	    decode: function(input) {
	      var output = "";
	      var chr1, chr2, chr3;
	      var enc1, enc2, enc3, enc4;
	      var i = 0;
	  
	      input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
	  
	      while (i < input.length) {
	  
	        enc1 = this._keyStr.indexOf(input.charAt(i++));
	        enc2 = this._keyStr.indexOf(input.charAt(i++));
	        enc3 = this._keyStr.indexOf(input.charAt(i++));
	        enc4 = this._keyStr.indexOf(input.charAt(i++));
	  
	        chr1 = (enc1 << 2) | (enc2 >> 4);
	        chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
	        chr3 = ((enc3 & 3) << 6) | enc4;
	  
	        output = output + String.fromCharCode(chr1);
	  
	        if (enc3 != 64) {
	          output = output + String.fromCharCode(chr2);
	        }
	        if (enc4 != 64) {
	          output = output + String.fromCharCode(chr3);
	        }
	  
	      }
	  
	      output = Base64._utf8_decode(output);
	  
	      return output;
	  
	    },
	  
	    // private method for UTF-8 encoding
	    _utf8_encode: function(string) {
	      string = string.replace(/\r\n/g, "\n");
	      var utftext = "";
	  
	      for (var n = 0; n < string.length; n++) {
	  
	        var c = string.charCodeAt(n);
	  
	        if (c < 128) {
	          utftext += String.fromCharCode(c);
	        } else if ((c > 127) && (c < 2048)) {
	          utftext += String.fromCharCode((c >> 6) | 192);
	          utftext += String.fromCharCode((c & 63) | 128);
	        } else {
	          utftext += String.fromCharCode((c >> 12) | 224);
	          utftext += String.fromCharCode(((c >> 6) & 63) | 128);
	          utftext += String.fromCharCode((c & 63) | 128);
	        }
	  
	      }
	  
	      return utftext;
	    },
	  
	    // private method for UTF-8 decoding
	    _utf8_decode: function(utftext) {
	      var string = "";
	      var i = 0;
	      var c = c1 = c2 = 0;
	  
	      while (i < utftext.length) {
	  
	        c = utftext.charCodeAt(i);
	  
	        if (c < 128) {
	          string += String.fromCharCode(c);
	          i++;
	        } else if ((c > 191) && (c < 224)) {
	          c2 = utftext.charCodeAt(i + 1);
	          string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
	          i += 2;
	        } else {
	          c2 = utftext.charCodeAt(i + 1);
	          c3 = utftext.charCodeAt(i + 2);
	          string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
	          i += 3;
	        }
	  
	      }
	  
	      return string;
	    }
	  
	  }
