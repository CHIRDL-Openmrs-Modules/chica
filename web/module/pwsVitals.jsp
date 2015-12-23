            <div id="vitals">
                    <div class="flagCell">
                        <b><font style="color:black;">A</font></b>
                    </div>
                    <div class="vitalsNames">
                        <b>Vital Signs:</b>
                    </div>                  
                    <c:choose>
                            <c:when test="${empty VitalsProcessed}">
                                <c:set var="vitalsProcessedFlag" value="" />
                                <c:set var="vitalsProcessedClass" value=""/>
                            </c:when>
                            <c:when test="${VitalsProcessed == 'true'}">
                               <c:set var="vitalsProcessedFlag" value="vitals received" />
                               <c:set var="vitalsProcessedClass" value=""/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="vitalsProcessedFlag" value="awaiting vitals" />
                                <c:set var="vitalsProcessedClass" value="awaitingVitalsClass"/>
                            </c:otherwise>
                        </c:choose>   
                    <div class="vitalsValues ${vitalsProcessedClass}">
                        <c:out value="${vitalsProcessedFlag}" />
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${HeightA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Height:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty Height}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${Height}"/>&nbsp;<c:out value="${HeightSUnits}"/>&nbsp;(<c:out value="${HeightP}"/>%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${WeightA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Weight:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty WeightKG}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${WeightKG}"/>&nbsp;kg.&nbsp;(<c:out value="${WeightP}"/>%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${BMIA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        BMI:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty BMI}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${BMI}"/>&nbsp;(<c:out value="${BMIP}"/>%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${HCA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Head Circ:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty HC}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${HC}"/> cm. (<c:out value="${HCP}"/>%)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${TempA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Temp:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty Temperature}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${Temperature}"/>&nbsp;&nbsp;
                                <c:if test="${not empty Temperature_Method}">
                                    (<c:out value="${Temperature_Method}"/>)
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${PulseA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Pulse:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty Pulse}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${Pulse}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${RRA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        RR:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty RR}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${RR}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${BPA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        BP:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty BP}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${BP}"/> (<c:out value="${BPP}"/>)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${PulseOxA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Pulse Ox:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty PulseOx}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${PulseOx}"/>%
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${HearA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Hear (L):<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty HearL}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${HearL}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${HearA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Hear (R):<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty HearR}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${HearR}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${VisionLA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Vision (L):<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty VisionL}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${VisionL}"/>&nbsp;<c:out value="${VisionL_Corrected}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b><c:out value="${VisionRA}"/></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Vision (R):<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty VisionR}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${VisionR}"/>&nbsp;<c:out value="${VisionR_Corrected}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Weight:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty Weight}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${Weight}"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flagCell">
                        <b></b><br/>
                    </div>
                    <div class="vitalsNames">
                        Prev WT:<br/>
                    </div>
                    <div class="vitalsValues">
                        <c:choose>
                            <c:when test="${empty PrevWeight}">
                                &nbsp;
                            </c:when>
                            <c:otherwise>
                                <c:out value="${PrevWeight}"/>&nbsp;(<c:out value="${PrevWeightDate}"/>)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div id="vitalsLegend">
                    <b><font style="color:red;">*</font>=Abnormal, U=Uncorrected,<br/>
                    C=Corrected, A=Axillary,
                    R=Rectal, O=Oral,<br/>
                    F=Failed, P=Passed</b></div>
                </div>