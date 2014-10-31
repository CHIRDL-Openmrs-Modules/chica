<%@ include file="/WEB-INF/template/include.jsp" %>
<!DOCTYPE html>
<html>
<head>
<link href="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.js"></script>
	
<title>Force Print Handouts</title>	
</head>
<body>
	<form>
		 <div id="formsLoading">
             <span id="formsLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
         </div>
         <div id="formsServerError">
             <div id="formsServerErrorText" class="ui-state-error"></div>
             <br/><br/><a href="#" id="retryButton" class="icon-button ui-state-default ui-corner-all">Retry</a>
             <a href="#" id="retryCloseButton" class="icon-button ui-state-default ui-corner-all">Close</a>
         </div>
         <div id="formsContainer">
             <fieldset>
                 <label for="forms">Select a form</label>
                 <select name="forms" id="forms"></select>
             </fieldset>
         </div>
         <div id="buttonPanel">
             <a href="#" id="createButton" class="icon-button ui-state-default ui-corner-all">Create</a>
             <a href="#" id="closeButton" class="icon-button ui-state-default ui-corner-all">Close</a>
         </div>
         <div id="frameContainer">
            <iframe id="formFrame" src="" frameBorder="0" seamless></iframe>
         </div>
         <div id="frameLoading">
            <span id="frameLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Creating form...</span>
         </div>
         <div id="frameError">
            <div id="frameServerErrorText" class="ui-state-error"></div>
         </div>
         <input type="hidden" value="${patientId}" id="patientId" />
         <input type="hidden" value="${sessionId}" id="sessionId" />
         <input type="hidden" value="${locationId}" id="locationId" />
         <input type="hidden" value="${locationTagId}" id="locationTagId" />
	</form>
</body>
</page>
