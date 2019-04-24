<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
            <div id="problemDialog" title="Problem List" class="ui-dialog-titlebar ui-widget-header">
                    <table id="problemTable">
                        <tr>
                            <td class="padding5"><c:out value="${diag1}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag2}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag3}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag4}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag5}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag6}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag7}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag8}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag9}"/></td>
                        </tr>
                        <tr>
                            <td class="padding5"><c:out value="${diag10}"/></td>
                        </tr>
                    </table>
                </div>
                <div id="confirmSubmitDialog" title="Confirm" class="ui-overlay">
                    <div id="confirmText">
                        <span>Click OK to sign the form.</span>
                    </div>
                </div>
                <div id="submitWaitDialog" class="noTitle">
                    <div id="submitWaitText">
                        <span>Signing...</span>
                    </div>
                </div>
                <div id="saveDraftWaitDialog" class="noTitle">
                    <div id="saveDraftWaitText">
                        <span>Saving Draft...</span>
                    </div>
                </div>
                <div id="notesDialog" title="CHICA Notes" class="ui-dialog-titlebar ui-widget-header" >
                    <div id="notesDialogContainer">                   
                        <div id="notesTabContainer">
                            <div id="notesTabs" >
                                  <ul id="notesTabList">
								    <li><a href="#tabs-0">History and Physical</a></li>
								    <li><a href="#tabs-1">Assessment and Plan</a></li>								    
								  </ul>
								  <div id="tabs-0">
								     <textarea id="historyAndPhysicalText" name="historyAndPhysicalText" class="notesTextArea" maxlength="62000" placeholder="History and Physical...">${not empty historyAndPhysicalText ? historyAndPhysicalText : ''}</textarea> 							  	 
								  	 <span class="textCount" id="historyAndPhysicalTextCount">0 of 62000 character max</span>
								  </div>
								  <div id="tabs-1">								     
								     <textarea id="assessmentAndPlanText" name="assessmentAndPlanText" class="notesTextArea" maxlength="62000" placeholder="Assessment and Plan...">${not empty assessmentAndPlanText ? assessmentAndPlanText : ''}</textarea>							     
								     <span class="textCount" id="assessmentAndPlanTextCount">0 of 62000 character max</span>
								  </div>								  
                            </div>                       
                        </div>
                    </div>
                </div>
				<div id="saveDraftErrorDialog" title="Save Draft Error" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
                    <div style="margin: 0 auto;text-align: center;">
                        <div id="saveDraftErrorMessage" style="color:#000000;"><p><b>An error occurred saving the draft.</b></p></div>
                    </div>
                </div>
                <div id="saveDraftSuccessDialog" title="Save Draft" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
                    <div style="margin: 0 auto;text-align: center;">
                        <div style="color:#000000;"><p><b>Draft successfully saved.</b></p></div>
                    </div>
                </div>
                <div id="serverErrorDialog" title="Server Error" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
                    <div style="margin: 0 auto;text-align: center;">
                        <div id="serverErrorMessage" style="color:#000000;">${errorMessage}</div>
                    </div>
                </div>
                <%@ include file="forcePrintJITs.jsp" %>
                <%@ include file="recommendedHandouts.jsp" %>