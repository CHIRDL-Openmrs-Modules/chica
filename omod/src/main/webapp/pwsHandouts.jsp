<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<div class="handouts_container" id="handouts_container">
    <header id="handouts_header">
        <a href="">
            <h4 class="handouts_logo">Handouts</h4>
        </a>
        <a href="#">
            <div class="ui-icon ui-icon-minusthick white"></div>
        </a>
    </header>
    <section class="fixed_height_section" id="handouts">
        <div class="handouts_buttons_container">
            <div class="buttonsData">
                <a href="#" id="formPrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Recommended</a>
            </div>
            <div class="buttonsData">
                <a href="#" id="forcePrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Other</a>
            </div>
        </div>
    </section>
</div>