<div class="patient_container" id="patient_container">
    <header id="patient_header">
        <a href="#">
            <h4 class="logo">Patient</h4>
        </a>
        <a href="#">
            <div class="ui-icon ui-icon-minusthick"></div>
        </a>
    </header>
    <section class="fixed_height_section" id="patient_name">
        <h3 class="patient_name_header"><c:out value="${PatientName}"/></h3>
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="right" style="width: 80px;">MRN:</td>
                        <td align="left" style="width: 170px;"><c:out value="${MRN}"/></td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 80px;">DOB:</td>
                        <td align="left" style="width: 170px;"><c:out value="${DOB}"/></td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 80px;">Age:</td>
                        <td align="left" style="width: 170px;"><c:out value="${Age}"/></td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 80px;">Provider:</td>
                        <td align="left" style="width: 170px;"><c:out value="${Doctor}"/></td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="250" border="0">
                <tbody>
                    <tr>
                        <td align="right" style="width: 80px;">Date:</td>
                        <td align="left" style="width: 170px;"><c:out value="${VisitDate}"/></td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 80px;">Time:</td>
                        <td align="left" style="width: 170px;"><c:out value="${VisitTime}"/></td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 80px;">Informant:</td>
                        <td align="left" style="width: 170px;"><c:out value="${Informant}"/></td>
                    </tr>
                    <tr>
                        <td align="right" style="width: 80px;">Language:</td>
                        <td align="left" style="width: 170px;"><c:out value="${Language}"/></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </section>
</div>