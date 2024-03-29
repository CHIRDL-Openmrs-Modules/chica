<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/externalFormLoader.form" />
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/externalFormLoader.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/recommendedHandouts.css" />

<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/jquery-ui.min.css" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/jquery-ui.structure.min.css" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/jquery-ui.theme.min.css" />

<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/externalFormLoader.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/recommendedHandouts.js"></script>
<title>CHICA ${formName}</title>
</head>
<body>

<div id="content">
    <div>
        <c:choose>
	        <c:when test="${hasErrors eq 'true'}">
	           <c:choose>
	               <c:when test="${mrn ne null}">
	                   <div class="ui-state-error"><h2><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>Error loading form ${formName} for ${mrn}</h2></div>
	               </c:when>
	               <c:otherwise>
	                   <div class="ui-state-error"><h2><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>Error loading form ${formName}</h2></div>
	               </c:otherwise>
	           </c:choose>
               <c:choose>
                   <c:when test="${missingUser eq 'true'}">
                       <div><p>No CHICA user was provided for authentication.</p></div>
                   </c:when>
                   <c:when test="${missingPassword eq 'true'}">
                       <div><p>No CHICA password was provided for authentication.</p></div>
                   </c:when>
                   <c:when test="${failedAuthentication eq 'true'}">
                       <div><p>Invalid username/password provided to the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${missingForm eq 'true'}">
                       <div><p>A valid formName parameter was not provided to the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${invalidForm eq 'true'}">
                       <div><p>A form with the name ${formName} cannot be found in the CHICA system.</p></div> 
                   </c:when>
                   <c:when test="${missingFormPage eq 'true'}">
                       <div><p>A valid formPage parameter was not provided to the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${missingMRN eq 'true'}">
                       <div><p>A valid mrn parameter was not provided to the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${invalidPatient eq 'true'}">
                       <div><p>A patient with MRN ${mrn} cannot be found in the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${missingEncounter eq 'true'}">
                       <div><p>A valid encounter within the past ${formTimeLimit} hours cannot be found for patient ${mrn} in the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${missingFormInstance eq 'true'}">
                       <div><p>The form ${formName} does not exist or has already been submitted for patient ${mrn} in the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${missingStartState eq 'true'}">
                       <div><p>A valid startState parameter was not provided to the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${missingEndState eq 'true'}">
                       <div><p>A valid endState parameter was not provided to the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${invalidStartState eq 'true'}">
                       <div><p>A start state with name ${startState} cannot be found in the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${invalidEndState eq 'true'}">
                       <div><p>An end state with name ${endState} cannot be found in the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${missingProviderId eq 'true'}">
                       <div><p>A valid providerId parameter was not provided to the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${invalidProviderId eq 'true'}">
                       <div><p>A provider with providerId ${providerId} cannot be found in the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${missingVendor eq 'true'}">
                       <div><p>A valid vendor parameter was not provided to the CHICA system.</p></div>
                   </c:when>
                   <c:when test="${invalidVendor eq 'true'}">
                       <div><p>A vendor with name ${vendor} cannot be found in the CHICA system.</p></div>
                   </c:when>
               </c:choose>
	        </c:when>
	        <c:otherwise>
	           <div><p>Loading ${formName} CHICA form for ${mrn}.  Please wait...</p></div>
	        </c:otherwise>
        </c:choose>
        <br/>
        <c:if test="${hasErrors eq 'true'}">
	        <c:if test="${showHandouts eq 'true'}">
	           <div id="buttons">
		           <div class="buttonsData">
		               <div class="button">
	                       <a href="#" id="recommendedHandoutsButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Recommended Handouts</a>
	                   </div>
	                   <div class="button">
	                       <a href="#" id="forcePrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Other Handouts</a>
	                   </div>
	               </div>
               </div>
	        </c:if>
	        <div>Please close this window at your convenience.</div>
        </c:if>
    </div>
    <form id="loadForm" method="POST">
        <input id="formName" name="formName" type="hidden" value="${formName}"/>
        <input id="formPage" name="formPage" type="hidden" value="${formPage}"/>
        <input id="mrn" name="mrn" type="hidden" value="${mrn}"/>
        <input id="hasErrors" name="hasErrors" type="hidden" value="${hasErrors}"/>
        <input id="startState" name="startState" type="hidden" value="${startState}"/>
        <input id="endState" name="endState" type="hidden" value="${endState}"/>
        <input id="sessionId" name="endState" type="hidden" value="${sessionId}"/>
        <input id="providerId" name="providerId" type="hidden" value="${providerId}"/>
        <input id="vendor" name="vendor" type="hidden" value="${providerId}"/>
        <input id="patientId" name="patientId" type="hidden" value="${patientId}"/>
        <input id="locationId" name="locationId" type="hidden" value="${locationId}"/>
        <input id="locationTagId" name="locationTagId" type="hidden" value="${locationTagId}"/>
        <input id="encounterId" name="encounterId" type="hidden" value="${encounterId}" />
        <c:choose>
            <c:when test="${showHandouts eq 'true'}">
                <input id="loadRecommendedHandouts" name="loadRecommendedHandouts" type="hidden" value="true" />
            </c:when>
            <c:otherwise>
                <input id="loadRecommendedHandouts" name="loadRecommendedHandouts" type="hidden" value="false" />
            </c:otherwise>
        </c:choose>
    </form>
</div>
<%@ include file="forcePrintJITs.jsp" %>
<%@ include file="recommendedHandouts.jsp" %>
</body>
</html>
