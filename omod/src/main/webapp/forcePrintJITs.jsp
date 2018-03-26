<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/forcePrintJITs.form" />
<div id="force-print-dialog" title="Available Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
    <div class="force-print-content">
         <div class="force-print-forms-loading">
             <span id="force-print-forms-loading-panel"><img src="${pageContext.request.contextPath}/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
         </div>
         <div class="force-print-forms-server-error">
             <div class="force-print-forms-server-error-text ui-state-error"></div>
             <br/><br/><a href="#" class="force-print-retry-button force-print-icon-button ui-state-default ui-corner-all">Retry</a>
         </div>
         <div class="force-print-no-forms force-print-black-text">
             There are no available handouts.
         </div>
         <div class="force-print-forms-container">
             <div class="force-print-patient-name">Please choose form(s) for ${patientName}.</div>
             <div class="force-print-multiple-select">Ctrl+click to select multiple forms</div>
             <div class="force-print-form-list-container">
                <ol id="force-print-form-list"></ol>
             </div>
             <div class="force-print-create-button-panel">
                <a href="#" id="force-print-create-forms-button" class="ui-state-default ui-corner-all">Create</a>
             </div>
         </div>
         <div class="force-print-form-container">
         	<!-- CHICA-948 Remove data and type attributes so IE doesn't cause an authentication error when loading the page. -->
            <object class="force-print-form-object" onreadystatechange="return forcePrint_formLoaded();" onload="forcePrint_formLoaded();">
               <span class="force-print-black-text">It appears your Web browser is not configured to display PDF files. 
               <a style="color:blue" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
            </object>
         </div>
         <div class="force-print-form-loading">
            <span><img src="${pageContext.request.contextPath}/moduleResources/chica/images/ajax-loader.gif"/>Creating form...</span>
         </div>
         <input type="hidden" id="patientId" />
         <input type="hidden" id="sessionId" />
         <input type="hidden" id="locationId" />
         <input type="hidden" id="locationTagId" />
         <input type="hidden" id="mrn" />
         <input type="hidden" id="patientName" />
    </div>
</div>
<div id="force-print-no-force-prints-dialog" title="No Selection" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
    <div style="margin: 0 auto;text-align: center;">
        <div style="color:#000000;"><p><b>Please select at least one form to create.</b></p></div>
    </div>
</div>
<div id="force-print-multiple-output-types-dialog" title="Forms" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
    <div id="force-print-multiple-output-types-result-div" style="color:black;"></div>
</div>
<div id="force-print-error-dialog" title="Error" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
    <div id="force-print-error-result-div" style="color:black;"></div>
</div>