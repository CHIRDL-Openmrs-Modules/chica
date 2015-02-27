<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require
    allPrivileges="View Encounters, View Patients, View Concept Classes"
    otherwise="/login.htm" redirect="/module/chica/greaseBoard.form" />

<%@ page import="org.openmrs.web.WebConstants"%>
<%
    pageContext.setAttribute("msg",
            session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
    pageContext.setAttribute("msgArgs",
            session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
    pageContext.setAttribute("err",
            session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
    pageContext.setAttribute("errArgs",
            session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
    session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
    session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
    session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
    session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
%>

<html>
<head>
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.css"/>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.timer.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/greaseBoard.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/chica.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery.floatThead.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/core.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/aes.js"></script>
<style>
#overlay { 
  display:none; 
  position:absolute; 
  background:#000000; 
  z-index: 3 !important ;
}

#img-load { 
  position:absolute; 
}

.mrnFieldset {
    border: 0;
    font-size: 10px;
    height: 100%;
    width:100%;
    color:#000000;
    margin-top:-5px;    
}

label {
  display: block;
  margin: 30px 0 0 0;
}

.manualCheckinLabelLeft, .manualCheckinLabelRight {
  width:110px;
  height:20px;
  text-align:right;
  float:left;
  padding-right:2px;
}

.manualCheckinLabelRight {
  width:200px;
}

.manualCheckinField, .manualCheckinAddressField {
  width:200px;
  height:20px;
  text-align:left;
  float:left;
}

.manualCheckinAddressField {
  width:400px;
}

.manualCheckinTextField {
  width:100%;
}

.manualCheckinLeftRow, .manualCheckinRightRow {
  width:398px;
  height:30px;
}

.manualCheckinRightRow {
  width:415px;
}

.stateOverflow {
    max-height: 200px;
}

.stationOverflow {
    max-height: 350px;
}

.doctorOverflow {
    max-height: 300px;
}

.raceOverflow {
    max-height: 140px;
}

.insuranceOverflow {
    max-height: 100px;
}

#pagerIssue {
    padding-top: 20px;
}

#pagerDescription {
    width: 270px;
    height: 100px;
    resize: none;
}

#pagerSaving {
    margin: 0 auto;
    text-align: center;
    padding-top: 10px;
}

#pagerError {
    padding-top: 10px;
}

.ui-dialog-shadow { 
    box-shadow: 10px 10px 5px #2E2E2E;
}

.ui-dialog { 
    z-index: 1002 !important ;
}

.ui-selectmenu { 
    z-index: 2 !important ;
}
</style>
<openmrs:htmlInclude file="/openmrs.css" />
<openmrs:htmlInclude file="/style.css" />
<openmrs:htmlInclude file="/openmrs.js" />
<title>CHICA Greaseboard</title>


<!--  Page Title : '${pageTitle}' 
            OpenMRS Title: <spring:message code="openmrs.title"/>
        -->
<c:choose>
    <c:when test="${!empty pageTitle}">
        <title>${pageTitle}</title>
    </c:when>
    <c:otherwise>
        <title><spring:message code="openmrs.title" /></title>
    </c:otherwise>
</c:choose>
</head>

<body onload="startTimer(${refreshPeriod})">
    <div class="headerarea chicaBackground" style="overflow: hidden;width:100%;">
        <table width="100%">
            <tr>
                <td width="100%" class="formTitleStyle"><b>Patients
                        (${currentUser})</b></td>
            </tr>
        </table>
    </div>
    <div class="encounterarea" id="middle">
        <div id="overlay">
		  <img src="/openmrs/moduleResources/chica/images/ajax-loader-circle.gif" id="img-load" />
		</div>
        <div id="badScansArea">
            <table width="100%"
                style="border: 0; frame: void; cellspacing: 0px; border-width: 1px; border-bottom-width: 3px; border-style: solid; border-color: black">
                <tr>
                    <td id="badScansCell" class="ui-state-error"
                        style="font-size: 13px; color: red; padding: 5px 0px 5px 5px; vertical-align: middle">
                        <b>Bad Scans Found: </b> <c:set var="tiffFiles" value="" />
                        <button id="viewBadScans" class="icon-button-medium ui-state-default ui-corner-all">View</button>
                    </td>
                </tr>
            </table>
        </div>
        <table id="patientTable" style="width: 100%; overflow: auto;" class="chicaBackground">
            <thead>
              <tr>
                    <th class="ln chicaTableHeader"><b>Last</b></th>
                    <th class="fn chicaTableHeader"><b>First</b></th>
                    <th class="mrn chicaTableHeader"><b>MRN</b></th>
                    <th class="dob chicaTableHeader"><b>DOB</b></th>
                    <th class="sex chicaTableHeader"><b>Sex</b></th>
                    <th class="MD chicaTableHeader"><b>MD</b></th>
                    <th class="aptTime chicaTableHeader"><b>Appt</b></th>
                    <th class="chkTime chicaTableHeader"><b>Check-in</b></th>
                    <th class="reprint chicaTableHeader"><b>Rpnt</b></th>
                    <th class="status chicaTableHeader"><b>Status</b></th>
                    <th class="action chicaTableHeader"><b>Action</b></th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
    <div style="height: 80px;padding-top:15px;padding-bottom:30px;" class="chicaBackground">
        <table class="chicaBackground greaseBoardFooter">
            <tr>
                <td width="30%">
                    <table width="100%">
                        <tr>
                            <td align="right"><b><span style="color: white; text-shadow: 1px 1px #000000;">Need Vitals:</span></b></td>
                            <td align="left"><span id="needVitals" style="color: white; text-shadow: 1px 1px #000000;">0</span></td>
                        </tr>
                        <tr>
                            <td align="right"><b><span style="color: white; text-shadow: 1px 1px #000000;">Waiting for MD:</span></b></td>
                            <td align="left"><span id="waitingForMD" style="color: white; text-shadow: 1px 1px #000000;">0</span></td>
                        </tr>
                    </table>
                </td>
                <td width="40%">
                    <table width="100%">
                        <tr>
                            <td align="right">
                                <button id="checkinButton" class="icon-button-extra-large ui-state-default ui-corner-all">Check-in Patient</button>
                            </td>
                            <td align="left">
                                <button id="viewEncountersButton" class="icon-button-extra-large ui-state-default ui-corner-all">View Encounters</button>
                            </td>
                        </tr>
                        <tr>
                            <td align="right">
                                <button id="printHandoutsButton" class="icon-button-extra-large ui-state-default ui-corner-all">Print Patient Handouts</button>
                            </td>
                            <td align="left">
                                <button id="selectPagerButton" class="icon-button-extra-large-help ui-state-default ui-corner-all">"Get Help Now!"</button>
                            </td>
                        </tr>
                    </table>
                </td>
                <td width="30%">
                    <table width="100%" style="font-size: 8pt">
                        <tr>
                            <td><span class="waitTextStyle">__</span>&nbsp;<span style="color: white; text-shadow: 1px 1px #000000;">Wait, Please
                                STAY until GREEN</span></td>
                        </tr>
                        <tr>
                            <td><span class="inProcessTextStyle">__</span>&nbsp;<span style="color: white; text-shadow: 1px 1px #000000;">Transaction
                                in Process</span></td>
                        </tr>
                        <tr>
                            <td><span class="formReadyTextStyle">__</span>&nbsp;<span style="color: white; text-shadow: 1px 1px #000000;">Form Ready</span></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
    <div id="forcePrintDialog" title="Available Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
         <div class="greaseBoard-force-print-content">
              <div class="force-print-forms-loading">
                  <span id="formsLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
              </div>
              <div class="force-print-forms-server-error">
                  <div class="force-print-forms-server-error-text ui-state-error"></div>
                  <br/><br/><a href="#" class="force-print-retry-button force-print-icon-button ui-state-default ui-corner-all">Retry</a>
              </div>
              <div class="force-print-forms-container">
                  <fieldset class="force-print-fieldset">
                      <select class="force-print-forms"></select>
                  </fieldset>
              </div>
              <div class="force-print-form-container">
                 <object class="force-print-form-object" data="" onreadystatechange="return forcePrint_formLoaded();" onload="forcePrint_formLoaded();">
                    <span class="force-print-black-text">It appears your Web browser is not configured to display PDF files. 
                    <a style="color:blue" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
                 </object>
              </div>
              <div class="force-print-form-loading">
                 <span><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Creating form...</span>
              </div>
              <form>
                  <input type="hidden" id="patientId" />
                  <input type="hidden" id="sessionId" />
                  <input type="hidden" id="locationId" />
                  <input type="hidden" id="locationTagId" />
                  <input type="hidden" id="mrn" />
              </form>
         </div>
     </div>
     <div id="listErrorDialog" title="Patient List Error" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div id="listErrorResultDiv" style="color:black;"></div>
    </div>
    <div id="checkinMRNDialog" title="Check-in Patient" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div style="margin: 0 auto;text-align: center;">
            <div style="color:#000000;"><p><b>Type the MRN #. Press OK to display the record.</b></p></div>
            <div id="mrnLoading" class="force-print-form-loading">
                 <span><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Verifying MRN...</span>
              </div>
            <div id="mrnError" style="text-align:center;"><span id="mrnMessage" class="alertText"></span></div>
            <div style="padding-bottom:10px;"><input type="text" size="20" id="mrnLookup" tabindex="1"/></div>
        </div>
    </div>
    <div id="viewEncountersMRNDialog" title="View Encounters" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div style="margin: 0 auto;text-align: center;">
            <div style="color:#000000;"><p><b>Type the MRN #. Press OK to display the patient's encounters.</b></p></div>
            <div id="encounterMrnLoading" class="force-print-form-loading">
                 <span><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Verifying MRN...</span>
              </div>
            <div id="encounterMrnError" style="text-align:center;"><span id="encounterMrnMessage" class="alertText"></span></div>
            <div style="padding-bottom:10px;"><input type="text" size="20" id="encounterMrnLookup" tabindex="1"/></div>
        </div>
    </div>
    <div id="printHandoutsMRNDialog" title="Print Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div style="margin: 0 auto;text-align: center;">
            <div style="color:#000000;"><p><b>Type the MRN #. Press OK to display the patient handouts.</b></p></div>
            <div id="printHandoutsMrnLoading" class="force-print-form-loading">
                 <span><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Verifying MRN...</span>
              </div>
            <div id="printHandoutsMrnError" style="text-align:center;"><span id="printHandoutsMrnMessage" class="alertText"></span></div>
            <div style="padding-bottom:10px;"><input type="text" size="20" id="printHandoutsMrnLookup" tabindex="1"/></div>
        </div>
    </div>
    <div id="manualCheckinDialog" title="Check-in Patient" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div id="manualCheckinLoading" style="height:400px;margin: 0 auto;text-align: center;">
             <span style="color:#000000;"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading information...</span>
        </div>
        <div id="manualCheckinSaving" style="height:400px;margin: 0 auto;text-align: center;">
             <span id="savingContainer" style="color:#000000;"></span>
        </div>
        <div id="manualCheckinError" style="text-align:center;"><span id="manualCheckinMessage" class="alertText"></span></div>
        <div id="manualCheckinComplete" style="text-align:center;"><span id="manualCheckinCompleteMessage" style="color:#000000;"></span></div>
        <div id="manualCheckin" style="margin: 0 auto;text-align: center;width:745px;height:400px;float:left;">
            <form id="manualCheckinForm" action="${pageContext.request.contextPath}/moduleServlet/chica/chica">
	            <div id="manualCheckinLeft" style="width:325px;height:320px;float:left;">
	                <div class="manualCheckinLeftRow">
	                 <div class="manualCheckinLabelLeft">
	                     <span style="color:#000000;text-align:right;">MRN:</span>
	                 </div>
	                 <div class="manualCheckinField">
	                     <input class="manualCheckinTextField" type="text" id="manualCheckinMrnDisplay" name="manualCheckinMrnDisplay" disabled/>
	                 </div>
	                </div>
	                <div class="manualCheckinLeftRow">
	                    <div class="manualCheckinLabelLeft">
	                        <span style="color:#000000;text-align:right;">Station:</span>
	                    </div>
	                 <div class="manualCheckinField">
	                  <fieldset class="mrnFieldset" style="float:left;">
	                      <select id="manualCheckinStation" name="manualCheckinStation" class="tableSelect"></select>
	                  </fieldset>
	                 </div>
	                </div>
	                <div class="manualCheckinLeftRow">
	                    <div class="manualCheckinLabelLeft">
	                        <span style="color:#000000;text-align:right;">Doctor:</span>
	                    </div>
	                 <div class="manualCheckinField">
	                  <fieldset class="mrnFieldset" style="float:left;">
	                      <select id="manualCheckinDoctor" name="manualCheckinDoctor" class="tableSelect"></select>
	                  </fieldset>
	                 </div>
	                </div>
	                <div class="manualCheckinLeftRow">
	                    <div class="manualCheckinLabelLeft">
	                        <span style="color:#000000;text-align:right;">SSN:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input type="text" id="manualCheckinSSNOne" name="manualCheckinSSNOne" size="3" maxlength="3"/>
	                        <span style="color:#000000">-</span>
	                        <input type="text" id="manualCheckinSSNTwo" name="manualCheckinSSNTwo" size="2" maxlength="2"/>
	                        <span style="color:#000000">-</span>
	                        <input type="text" id="manualCheckinSSNThree" name="manualCheckinSSNThree" size="4" maxlength="4"/>
	                    </div>
	                </div>
	                <div class="manualCheckinLeftRow">
	                    <div class="manualCheckinLabelLeft">
	                        <span style="color:#000000;text-align:right;">Last Name:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input class="manualCheckinTextField" type="text" id="manualCheckinLastName" name="manualCheckinLastName"/>
	                    </div>
	                </div>
	                <div class="manualCheckinLeftRow">
	                    <div class="manualCheckinLabelLeft">
	                        <span style="color:#000000;text-align:right;">First Name:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input class="manualCheckinTextField" type="text" id="manualCheckinFirstName" name="manualCheckinFirstName"/>
	                    </div>
	                </div>
	                <div class="manualCheckinLeftRow">
	                    <div class="manualCheckinLabelLeft">
	                        <span style="color:#000000;text-align:right;">Middle Name:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input class="manualCheckinTextField" type="text" id="manualCheckinMiddleName" name="manualCheckinMiddleName"/>
	                    </div>
	                </div>
	                <div class="manualCheckinLeftRow">
	                    <div class="manualCheckinLabelLeft">
	                        <span style="color:#000000;text-align:right;">Sex:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <fieldset class="mrnFieldset" style="float:left;">
	                            <select id="manualCheckinSex" name="manualCheckinSex" class="tableSelect">
	                                <option value="U">Select One</option>
									<option value="F">F - female</option>
									<option value="M">M - male</option>
	                            </select>
	                        </fieldset>
	                    </div>
	                </div>
	                <div class="manualCheckinLeftRow">
                        <div class="manualCheckinLabelLeft">
                            <span style="color:#000000;text-align:right;">DOB:</span>
                        </div>
                        <div class="manualCheckinField">
                            <span style="color:#000000;"><input type="text" id="manualCheckinDob" name="manualCheckinDob"/></span>
                        </div>
                    </div>
	            </div>
	            <div id="manualCheckinRight" style="width:415px;height:320px;margin: 0 auto;text-align:center;float:right;">
	                <div class="manualCheckinRightRow">
	                    <div class="manualCheckinLabelRight">
	                        <span style="color:#000000;text-align:right;">Next of Kin Last Name:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input class="manualCheckinTextField" type="text" id="manualCheckinNOKLastName" name="manualCheckinNOKLastName"/>
	                    </div>
	                </div>
	                <div class="manualCheckinRightRow">
	                    <div class="manualCheckinLabelRight">
	                        <span style="color:#000000;text-align:right;">Next of Kin First Name:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input class="manualCheckinTextField" type="text" id="manualCheckinNOKFirstName" name="manualCheckinNOKFirstName"/>
	                    </div>
	                </div>
	                <div class="manualCheckinRightRow">
	                    <div class="manualCheckinLabelRight">
	                        <span style="color:#000000;text-align:right;">Day Phone:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input class="manualCheckinTextField" type="text" id="manualCheckinPhone" name="manualCheckinPhone"/>
	                    </div>
	                </div>
	                <div class="manualCheckinRightRow">
	                    <div class="manualCheckinLabelRight">
	                        <span style="color:#000000;text-align:right;">Street Address:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input class="manualCheckinTextField" type="text" id="manualCheckinStreetAddress" name="manualCheckinStreetAddress"/>
	                    </div>
	                </div>
	                <div class="manualCheckinRightRow">
	                    <div class="manualCheckinLabelRight">
	                        <span style="color:#000000;text-align:right;">Street Address 2:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input class="manualCheckinTextField" type="text" id="manualCheckinStreetAddress2" name="manualCheckinStreetAddress2"/>
	                    </div>
	                </div>
	                <div class="manualCheckinRightRow">
	                    <div class="manualCheckinLabelRight">
	                        <span style="color:#000000;text-align:right;">City:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input class="manualCheckinTextField" type="text" id="manualCheckinCity" name="manualCheckinCity"/>
	                    </div>
	                </div>
	                <div class="manualCheckinRightRow">
	                    <div class="manualCheckinLabelRight">
	                        <span style="color:#000000;text-align:right;">State:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <fieldset class="mrnFieldset" style="float:left;">
	                            <select id="manualCheckinState" name="manualCheckinState" class="tableSelect">
	                                <option></option>
	                                <option value="AL">AL - Alabama</option>
									<option value="AK">AK - Alaska</option>
									<option value="AZ">AZ - Arizona</option>
									<option value="AR">AR - Arkansas</option>
									<option value="CA">CA - California</option>
									<option value="CO">CO - Colorado</option>
									<option value="CT">CT - Connecticut</option>
									<option value="DE">DE - Delaware</option>
									<option value="DC">DC - District of Columbia</option>
									<option value="FL">FL - Florida</option>
									<option value="GA">GA - Georgia</option>
									<option value="HI">HI - Hawaii</option>
									<option value="ID">ID - Idaho</option>
									<option value="IL">IL - Illinois</option>
									<option value="IN">IN - Indiana</option>
									<option value="IA">IA - Iowa</option>
									<option value="KS">KS - Kansas</option>
									<option value="KY">KY - Kentucky</option>
									<option value="LA">LA - Louisiana</option>
									<option value="ME">ME - Maine</option>
									<option value="MD">MD - Maryland</option>
									<option value="MA">MA - Massachusetts</option>
									<option value="MI">MI - Michigan</option>
									<option value="MN">MN - Minnesota</option>
									<option value="MS">MS - Mississippi</option>
									<option value="MO">MO - Missouri</option>
									<option value="MT">MT - Montana</option>
									<option value="NE">NE - Nebraska</option>
									<option value="NV">NV - Nevada</option>
									<option value="NH">NH - New Hampshire</option>
									<option value="NJ">NJ - New Jersey</option>
									<option value="NM">NM - New Mexico</option>
									<option value="NY">NY - New York</option>
									<option value="NC">NC - North Carolina</option>
									<option value="ND">ND - North Dakota</option>
									<option value="OH">OH - Ohio</option>
									<option value="OK">OK - Oklahoma</option>
									<option value="OR">OR - Oregon</option>
									<option value="PA">PA - </option>
									<option value="RI">RI - Rhode Island</option>
									<option value="SC">SC - South Carolina</option>
									<option value="SD">SD - South Dakota</option>
									<option value="TN">TN - Tennessee</option>
									<option value="TX">TX - Texas</option>
									<option value="UT">UT - Utah</option>
									<option value="VT">VT - Vermont</option>
									<option value="VA">VA - Virginia</option>
									<option value="WA">WA - Washington</option>
									<option value="WV">WV - West Virginia</option>
									<option value="WI">WI - Wisconsin</option>
									<option value="WY">WY - Wyoming</option>
	                            </select>
	                        </fieldset>
	                    </div>
	                </div>
	                <div class="manualCheckinRightRow">
	                    <div class="manualCheckinLabelRight">
	                        <span style="color:#000000;text-align:right;">Zip:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <input class="manualCheckinTextField" type="text" id="manualCheckinZip" name="manualCheckinZip"/>
	                    </div>
	                </div>
	                <div class="manualCheckinRightRow">
	                    <div class="manualCheckinLabelRight">
	                        <span style="color:#000000;text-align:right;">Race:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <fieldset class="mrnFieldset" style="float:left;">
	                            <select id="manualCheckinRace" name="manualCheckinRace" class="tableSelect">
	                                <option></option>
	                            </select>
	                        </fieldset>
	                    </div>
	                </div>
	                <div class="manualCheckinRightRow">
	                    <div class="manualCheckinLabelRight">
	                        <span style="color:#000000;text-align:right;">Insurance Category:</span>
	                    </div>
	                    <div class="manualCheckinField">
	                        <fieldset class="mrnFieldset" style="float:left;">
	                            <select id="manualCheckinInsuranceCategory" name="manualCheckinInsuranceCategory" class="tableSelect">
	                                <option></option>
	                            </select>
	                        </fieldset>
	                    </div>
	                </div>
	            </div>
	            <input type="hidden" id="manualCheckinNewPatient" value="false"/>
	            <input type="hidden" id="manualCheckinMrn" name="manualCheckinMrn"/>
            </form>
        </div>
    </div>
    <div id="otherDoctorDialog" title="Other Doctor" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div style="margin: 0 auto;text-align: center;">
            <div style="color:#000000;"><p><b>Are you sure you want to select OTHER as the Doctor?</b></p></div>
        </div>
    </div>
    <div id="otherStationDialog" title="Other Station" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div style="margin: 0 auto;text-align: center;">
            <div style="color:#000000;"><p><b>Are you sure you want to select OTHER as the Station?</b></p></div>
        </div>
    </div>
    <div id="newPatientDialog" title="New Patient" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div style="margin: 0 auto;text-align: center;">
            <div id="newPatientText" style="color:#000000;"><p><b>Are you sure you want to select OTHER as the Station?</b></p></div>
        </div>
    </div>
    <div id="pagerDialog" title="Page Request" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div id="pagerBody">
	        <div>
		        <div>
		            <span style="color:#000000;text-align:right;"><span style="color: red;text-shadow: 1px 1px #000000;"><b>*</b></span> Your Name:</span>
		        </div>
		        <div>
		            <input type="text" id="pagerName" name="pagerName" size="35"/>
		        </div>
	        </div>
	        <div id="pagerIssue">
	            <span style="color:#000000;text-align:right;">Please specify the issue and the best number to reach you:</span>
	        </div>
	        <div>
	            <textarea id="pagerDescription" maxlength="160"></textarea>
	            <span id="pagerTextCount" style="color:#000000;">0 of 160 character max</span>
	        </div>
	        <div id="pagerError" style="text-align:center;"><span id="pagerErrorMessage" class="alertText"></span></div>
	        <div id="pagerSaving">
	             <span style="color:#000000;"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Sending request...</span>
	        </div>
        </div>
        <div id="pagerComplete" style="text-align:center;"><span style="color:#000000;">Page request successfully sent.</span></div>
    </div>
    <div id="adhdWorkupDialog" title="ADHD Workup" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
        <div style="margin: 0 auto;text-align: center;">
            <div style="color:#000000;"><p><b>Are you sure you want to initiate an ADHD Workup?</b></p></div>
        </div>
    </div>
    <input type="hidden" id="badScans" />
</body>
</html>

