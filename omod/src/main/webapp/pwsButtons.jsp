<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
            <div class="handouts_buttons_container">
                <div class="buttonsData">
                    <a href="#" id="formPrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Recommended</a>
                </div>
                <div class="buttonsData">
                    <a href="#" id="forcePrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Other</a>
                </div>
            </div>