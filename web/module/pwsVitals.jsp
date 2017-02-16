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
            <div class="ui-icon ui-icon-minusthick"></div>
        </a>
    </header>
    <section class="psf_results" id="vitals">
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="right" style="width: 5;"><font color="red"><c:out value="${HeightA}"/></font>
                        </td>
                        <td align="right" style="width: 50;">Height:</td>
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
                        <td align="right" style="width: 5;"><font color="red"><c:out value="${WeightA}"/></font>
                        </td>
                        <td align="right" style="width: 50;">Weight:</td>
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
                        <td align="right" style="width: 5;"><font color="red"><c:out value="${BMIA}"/></font><font color="red">*</font>
                        </td>
                        <td align="right" style="width: 50;">BMI:</td>
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
                        <td align="right" style="width: 5;"><font color="red"><c:out value="${HearA}"/></font>
                        </td>
                        <td align="right" style="width: 50;">Hear (L):</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty HearL}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${HearL}" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="right" style="width: 5;"><font color="red"><c:out value="${HCA}"/></font>
                        </td>
                        <td align="right" style="width: 50;">Head Circ:</td>
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
                        <td align="right" style="width: 5;"><font color="red"><c:out value="${TempA}"/></font>
                        </td>
                        <td align="right" style="width: 50;">Temp:</td>
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
                        <td align="right" style="width: 5;"><font color="red"><c:out value="${PulseA}"/></font>
                        </td>
                        <td align="right" style="width: 50;">Pulse:</td>
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
                        <td align="right" style="width: 5;">
                            <c:out value="${HearA}" />
                        </td>
                        <td align="right" style="width: 50;">Hear (R):</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty HearR}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${HearR}" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="right" style="width: 5;"><font color="red"><c:out value="${RRA}"/></td>
                <td align="right" style="width: 50;">RR:</td>
                <td align="left" style="width: 195;">
                    <c:choose>
                        <c:when test="${empty RR}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <c:out value="${RR}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
              </tr>
              <tr>
                <td align="right" style="width: 5;"><font color="red"><c:out value="${BPA}"/></td>
                <td align="right" style="width: 50;">BP:</td>
                <td align="left" style="width: 195;">
                    <c:choose>
                        <c:when test="${empty BP}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <c:out value="${BP}"/> (<c:out value="${BPP}"/>)
                        </c:otherwise>
                    </c:choose>
                </td>
              </tr>
              <tr>
                <td align="right" style="width: 5;"><font color="red"><c:out value="${PulseOxA}"/></td>
                <td align="right" style="width: 50;">Pulse Ox:</td>
                <td align="left" style="width: 195;">
                    <c:choose>
                        <c:when test="${empty PulseOx}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <c:out value="${PulseOx}"/>%
                        </c:otherwise>
                    </c:choose>
                </td>
              </tr>
              <tr>
                <td align="right" style="width: 5;"><font color="red"><c:out value="${VisionLA}"/></font>
                        </td>
                        <td align="right" style="width: 50;">Vision (L):</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty VisionL}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${VisionL}" />&nbsp;
                                    <c:out value="${VisionL_Corrected}" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="right" style="width: 5;"></td>
                        <td align="right" style="width: 50;">Weight:</td>
                        <td align="left" style="width: 195;">
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
                        <td align="right" style="width: 5;"></td>
                        <td align="right" style="width: 50;">Prev Weight:</td>
                        <td align="left" style="width: 195;">
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
                        <td align="right" style="width: 5;">&nbsp;</td>
                        <td align="right" style="width: 50;">&nbsp;</td>
                        <td align="left" style="width: 195;">&nbsp;</td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 5;"><font color="red"><c:out value="${VisionRA}"/></font>
                        </td>
                        <td align="right" style="width: 50;">Vision (R):</td>
                        <td align="left" style="width: 195;">
                            <c:choose>
                                <c:when test="${empty VisionR}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${VisionR}" />&nbsp;
                                    <c:out value="${VisionR_Corrected}" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="vitals_legend">
            <span class="vitals_lengend_span"><font color="red">*</font>=Abnormal, A=Axillary, R=Rectal, O=Oral</span>
        </div>
    </section>
</div>