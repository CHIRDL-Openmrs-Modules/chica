<div class="patient_information_container" id="patient_container">
    <header id="patient_header">
        <a href="#">
            <h4 class="logo">Patient</h4>
        </a>
        <a href="#">
            <div class="ui-icon ui-icon-minusthick white"></div>
        </a>
    </header>
    <section class="fixed_height_section" id="patient_name">
        <h3 class="patient_name_header"><c:out value="${PatientName}"/></h3>
        <div class="patient_info_container">
            <table width="350" border="0">
                <tbody>
                    <tr>
                        <td align="right">MRN:</td>
                        <td align="left"><c:out value="${MRN}"/></td>
                    </tr>
                    <tr>
                        <td align="right">DOB:</td>
                        <td align="left"><c:out value="${DOB}"/></td>
                    </tr>
                    <tr>
                        <td align="right">Age:</td>
                        <td align="left"><c:out value="${Age}"/></td>
                    </tr>
                    <tr>
                        <td align="right">Provider:</td>
                        <td align="left"><c:out value="${Doctor}"/></td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="patient_info_container">
            <table width="275" border="0">
                <tbody>
                    <tr>
                        <td align="right">Date:</td>
                        <td align="left"><c:out value="${VisitDate}"/></td>
                    </tr>
                    <tr>
                        <td align="right">Time:</td>
                        <td align="left"><c:out value="${VisitTime}"/></td>
                    </tr>
                    <tr>
                        <td align="right">Informant:</td>
                        <td align="left" ><c:out value="${Informant}"/></td>
                    </tr>
                    <tr>
                        <td align="right">Language:</td>
                        <td align="left"><c:out value="${Language}"/></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </section>
</div>