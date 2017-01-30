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
                <div id="medDialog" title="Medication List" class="ui-dialog-titlebar ui-widget-header">
                    <table id="medTable">
                        <c:if test="${not empty Med1_A || not empty Med1_B }">
                            <tr class="trAlignLeft">
                                <td class="tdBorderTop"><c:out value="${Med1_A}"/></td>
                            </tr>
                            <tr class="trAlignLeft">
                                <td><c:out value="${Med1_B}"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty Med2_A || not empty Med2_B }">
                            <tr class="trAlignLeft">
                                <td class="tdBorderTop"><c:out value="${Med2_A}}"/></td>
                            </tr>
                            <tr class="trAlignLeft">
                                <td><c:out value="${Med2_B}"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty Med3_A || not empty Med3_B }">
                            <tr class="trAlignLeft">
                                <td class="tdBorderTop"><c:out value="${Med3_A}"/></td>
                            </tr>
                            <tr class="trAlignLeft">
                                <td><c:out value="${Med3_B}"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty Med4_A || not empty Med4_B }">
                            <tr class="trAlignLeft">
                                <td class="tdBorderTop"><c:out value="${Med4_A}"/></td>
                            </tr>
                            <tr class="trAlignLeft">
                                <td><c:out value="${Med4_B}"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty Med5_A || not empty Med5_B }">
                            <tr class="trAlignLeft">
                                <td class="tdBorderTop"><c:out value="${Med5_A}"/></td>
                            </tr>
                            <tr class="trAlignLeft">
                                <td><c:out value="${Med5_B}"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty Med6_A || not empty Med6_B }">
                            <tr class="trAlignLeft">
                                <td class="tdBorderTop"><c:out value="${Med6_A}"/></td>
                            </tr>
                            <tr class="trAlignLeft">
                                <td><c:out value="${Med6_B}"/></td>
                            </tr>
                        </c:if>
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
                <div id="formSelectionDialog" title="CHICA Recommended Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
                    <div id="formSelectionDialogContainer">
                        <div id="formLoading">
                           <span id="formLoadingPanel"><img src="${pageContext.request.contextPath}/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
                        </div>
                        <div id="formServerError">
                            <div id="formServerErrorText" class="ui-state-error"></div>
                            <br/><br/><a href="#" id="retryButton" class="icon-button ui-state-default ui-corner-all">Retry</a>
                        </div>
                        <div id="noForms">
                            There are no recommended handouts for ${PatientName}.
                        </div>
                        <div id="recommendedHandoutsContainer">
                            <div class="recommendedHandoutsMultiselect">Ctrl+click to select multiple forms</div>
				            <div class="recommendedHandoutsFormListContainer">
				               <ol id="recommendedHandoutsFormList"></ol>
				            </div>
				            <div class="recommendedHandoutsCombineButtonPanel">
				            <a href="#" id="recommendedHandoutsSelectAllButton" class="force-print-icon-button ui-state-default ui-corner-all">Select All</a>
	                           <a href="#" id="recommendedHandoutsCombineButton" class="force-print-icon-button ui-state-default ui-corner-all">Combine Forms</a>
	                        </div>
                        </div>
			            <div class="recommendedHandoutContainer">
				           <object class="recommendedHandoutObject" data="" onreadystatechange="return formLoaded();" onload="formLoaded();" type="application/pdf">
				              <span class="force-print-black-text">It appears your Web browser is not configured to display PDF files. 
				              <a style="color:blue" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
				           </object>
				        </div>
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
                <div id="noSelectedFormsDialog" title="No Selection" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
				    <div style="margin: 0 auto;text-align: center;">
				        <div style="color:#000000;"><p><b>Please select at least two forms to combine.</b></p></div>
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