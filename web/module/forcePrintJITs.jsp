<%@ include file="/WEB-INF/template/include.jsp" %>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.js"></script>
<script>
$(document).ready(function () {
    $(".force-print-form-object").height($(window).height() - 220);
    $(window).resize(function() {
        // Update the iframe height
        $(".force-print-form-object").height($(window).height() - 220);
        // Update the height of the select
        $(".force-print-forms").selectmenu().selectmenu("menuWidget").css({"max-height":($(window).height() * 0.60) + "px"});
    });
    
    forcePrint_loadForms();
});
</script>
	
<title>Force Print Handouts</title>	
</head>
<body class="force-print-body">
    <div class="force-print-content">
		<form>
			 <div class="force-print-forms-loading">
	             <span id="formsLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
	         </div>
	         <div class="force-print-forms-server-error">
	             <div class="force-print-forms-server-error-text ui-state-error"></div>
	             <br/><br/><a href="#" class="force-print-retry-button force-print-icon-button ui-state-default ui-corner-all">Retry</a>
	             <a href="#" class="force-print-retry-close-button force-print-icon-button ui-state-default ui-corner-all">Close</a>
	         </div>
	         <div class="force-print-forms-container">
	             <fieldset class="force-print-fieldset">
	                 <select class="force-print-forms"></select>
	             </fieldset>
	         </div>
	         <div class="force-print-button-panel">
	             <a href="#" class="force-print-close-button force-print-icon-button ui-state-default ui-corner-all">Close</a>
	         </div>
	         <div class="force-print-form-container">
	            <object class="force-print-form-object" data="" onreadystatechange="return forcePrint_formLoaded();" onload="return forcePrint_formLoaded();">
	               <span class="force-print-black-text"><p>It appears your Web browser is not configured to display PDF files. 
                   <a class="force-print-black-text" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</p></span>
	            </object>
	         </div>
	         <div class="force-print-form-loading">
	            <span><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Creating form...</span>
	         </div>
	         <input type="hidden" value="${patientId}" id="patientId" />
	         <input type="hidden" value="${sessionId}" id="sessionId" />
	         <input type="hidden" value="${locationId}" id="locationId" />
	         <input type="hidden" value="${locationTagId}" id="locationTagId" />
		</form>
	</div>
</body>
</html>
