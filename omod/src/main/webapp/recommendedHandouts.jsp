<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/recommendedHandouts.form" />
<div id="recommended-handouts-form-selection-dialog" title="CHICA Recommended Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
    <div id="recommended-handouts-form-selection-dialog-container">
        <div id="recommended-handouts-form-loading">
           <span id="recommended-handouts-form-loading-panel"><img src="${pageContext.request.contextPath}/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
        </div>
        <div id="recommended-handouts-form-server-error">
            <div id="recommended-handouts-form-server-error-text" class="ui-state-error"></div>
            <br/><br/><a href="#" id="recommended-handouts-retry-button" class="icon-button ui-state-default ui-corner-all">Retry</a>
        </div>
        <div id="recommended-handouts-no-forms">
            There are no recommended handouts for ${PatientName}.
        </div>
        <div id="recommended-handouts-container">
            <div class="recommended-handouts-multiselect">Ctrl+click to select multiple forms</div>
            <div class="recommended-handouts-form-list-container">
               <ol id="recommended-handouts-form-list"></ol>
            </div>
            <div class="recommended-handouts-combine-button-panel">
            <a href="#" id="recommended-handouts-select-all-button" class="ui-state-default ui-corner-all">Select All</a>
               <a href="#" id="recommended-handouts-combine-button" class="ui-state-default ui-corner-all">Combine Forms</a>
            </div>
        </div>
        <div class="recommended-handout-container">
        <!-- CHICA-948 Remove data and type attributes so IE doesn't cause an authentication error when loading the page. -->
           <object class="recommended-handout-object" onreadystatechange="return formLoaded();" onload="formLoaded();">
              <span class="recommended-handouts-black-text">It appears your Web browser is not configured to display PDF files. 
              <a style="color:blue" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
           </object>
        </div>
    </div>
</div>
<div id="recommended-handouts-no-selected-forms-dialog" title="No Selection" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
    <div style="margin: 0 auto;text-align: center;">
        <div style="color:#000000;"><p><b>Please select at least two forms to combine.</b></p></div>
    </div>
</div>