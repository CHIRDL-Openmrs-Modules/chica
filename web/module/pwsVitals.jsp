<div class="psf_container">
    <c:set var="vitalsProcessedFlag" value="" />
    <c:if test="${VitalsProcessed == 'false'}">
        <c:set var="vitalsProcessedFlag" value=" (Awaiting)" />
    </c:if>
    <header id="vitals_header">
        <a href="">
            <h4 class="logo">Vitals<c:out value="${vitalsProcessedFlag}" /></h4>
        </a>
        <a href="#">
            <div class="ui-icon ui-icon-minusthick white"></div>
        </a>
    </header>
    <section class="psf_results" id="vitals">
        <div class="patient_info_container">
            <table width="230" border="0">
                <tbody>
                    <tr>
                        <td align="right" style="width: 35;"><font color="red"><c:out value="${HeightA}"/></font>Height:</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty Height}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${Height}" />&nbsp;
                                    <c:out value="${HeightSUnits}" />&nbsp;(
                                    <c:out value="${HeightP}" />%)
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 35;"><font color="red"><c:out value="${WeightA}"/></font>Weight:</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty WeightKG}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${WeightKG}" />&nbsp;kg.&nbsp;(
                                    <c:out value="${WeightP}" />%)
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 35;"><font color="red"><c:out value="${BMIA}"/></font>BMI:</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty BMI}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${BMI}" />&nbsp;(
                                    <c:out value="${BMIP}" />%)
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 35;"><div class="hearing"><font color="red"><c:out value="${HearA}"/></font>Hear (L):</div></td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty HearL}">
                                    <div class="hearing">&nbsp;</div>
                                </c:when>
                                <c:otherwise>
                                    <div class="hearing"><c:out value="${HearL}" /></div>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="230" border="0">
                <tbody>
                    <tr>
                        <td align="right" style="width: 35;"><font color="red"><c:out value="${HCA}"/></font>Head Circ:</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty HC}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${HC}" /> cm. (
                                    <c:out value="${HCP}" />%)
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 35;"><font color="red"><c:out value="${TempA}"/></font>Temp:</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty Temperature}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${Temperature}" />&nbsp;&nbsp;
                                    <c:if test="${not empty Temperature_Method}">
                                        (
                                        <c:out value="${Temperature_Method}" />)
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 35;"><font color="red"><c:out value="${PulseA}"/></font>Pulse:</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty Pulse}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${Pulse}" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 35;"><div class="hearing"><font color="red"><c:out value="${HearA}"/></font>Hear (R):</div></td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty HearR}">
                                    <div class="hearing">&nbsp;</div>
                                </c:when>
                                <c:otherwise>
                                    <div class="hearing"><c:out value="${HearR}" /></div>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="230" border="0">
                <tbody>
                    <tr>
                        <td align="right" style="width: 35;"><font color="red"><c:out value="${RRA}"/></font>RR:</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty RR}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${RR}" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 35;"><font color="red"><c:out value="${BPA}"/></font>BP:</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty BP}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${BP}" /> (
                                    <c:out value="${BPP}" />)
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 35;"><font color="red"><c:out value="${PulseOxA}"/></font>Pulse Ox:</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty PulseOx}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${PulseOx}" />%
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 35;"><div class="vision"><font color="red"><c:out value="${VisionLA}"/></font>Vision (L):</div></td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty VisionL}">
                                    <div class="vision">&nbsp;</div>
                                </c:when>
                                <c:otherwise>
                                    <div class="vision">
                                    	<c:out value="${VisionL}" />&nbsp;<c:out value="${VisionL_Corrected}" />
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="290" border="0">
                <tbody>
                    <tr>
                        <td align="right" style="width: 70;">Weight:</td>
                        <td align="left" style="width: 220;">
                            <c:choose>
                                <c:when test="${empty Weight}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${Weight}" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 70;">Prev Weight:</td>
                        <td align="left" style="width: 220;">
                            <c:choose>
                                <c:when test="${empty PrevWeight}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${PrevWeight}" />&nbsp;(
                                    <c:out value="${PrevWeightDate}" />)
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 70;">&nbsp;</td>
                        <td align="left" style="width: 220;">&nbsp;</td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 70;"><div class="vision"><font color="red"><c:out value="${VisionRA}"/></font>Vision (R):</div></td>
                        <td align="left" style="width: 220;">
                            <c:choose>
                                <c:when test="${empty VisionR}">
                                    <div class="vision">&nbsp;</div>
                                </c:when>
                                <c:otherwise>
                                    <div class="vision">
                                    	<c:out value="${VisionR}" />&nbsp;<c:out value="${VisionR_Corrected}" />
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="vitals_legend">
            <span id="vitalsLegendEskenaziEpic" class="vitals_lengend_span"><font color="red">*</font>=Abnormal, A=Axillary, R=Rectal, O=Oral</span>
            <span id="vitalsLegendIUHCerner" class="vitals_lengend_span"><font color="red">*</font>=Abnormal, U=Uncorrected, C=Corrected, A=Axillary, R=Rectal, O=Oral, F=Failed, P=Passed</span>
        </div>
    </section>
</div>