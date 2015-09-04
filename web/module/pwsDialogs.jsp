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
                        <span>Click OK to finalize the form.</span>
                    </div>
                </div>
                <div id="submitWaitDialog" class="noTitle">
                    <div id="submitWaitText">
                        <span>Submitting...</span>
                    </div>
                </div>
                <div id="formTabDialog" title="CHICA Recommended Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
                    <div id="formTabDialogContainer" style="overflow-x: hidden;overflow-y: hidden;">
                        <div id="formLoading">
                           <span id="formLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
                        </div>
                        <div id="formServerError">
                            <div id="formServerErrorText" class="ui-state-error"></div>
                            <br/><br/><a href="#" id="retryButton" class="icon-button ui-state-default ui-corner-all">Retry</a>
                        </div>
                        <div id="noForms">
                            There are no recommended handouts for ${PatientName}.
                        </div>
                        <div id="formTabContainer">
                            <div id="tabs"></div>
                        </div>
                    </div>
                </div>
                <div id="forcePrintDialog" title="Other CHICA Handouts" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
                    <div class="pws-force-print-content">
                         <div class="force-print-forms-loading">
                             <span id="formsLoadingPanel"><img src="/openmrs/moduleResources/chica/images/ajax-loader.gif"/>Loading forms...</span>
                         </div>
                         <div class="force-print-forms-server-error">
                             <div class="force-print-forms-server-error-text ui-state-error"></div>
                             <br/><br/><a href="#" class="force-print-retry-button force-print-icon-button ui-state-default ui-corner-all">Retry</a>
                         </div>
                         <div class="force-print-forms-container">
                             <div class="force-print-patient-name">Please choose a form for ${PatientName}.</div>
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
                         <input type="hidden" value="${patientId}" id="patientId" />
                         <input type="hidden" value="${sessionId}" id="sessionId" />
                         <input type="hidden" value="${locationId}" id="locationId" />
                         <input type="hidden" value="${locationTagId}" id="locationTagId" />
                    </div>
                </div>