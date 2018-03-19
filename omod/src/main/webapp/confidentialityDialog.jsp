<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/module/chica/loginMobile.form" redirect="/module/chica/confidentialityDialog.form" />
<div id="confidentialityDialog" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" style="width:400px;">
	<div data-role="header" data-theme="b">
 		<div  id="confidentialityNoticeHeader">
 			<h3 style="text-align: center;">Confidentiality Notice</h3>
        </div>
    </div>
    <div data-role="content">   
 		<div id="confidentialityNoticeDiv"><p>The following questions are confidential. ${newFirstName} should answer them privately.</p></div>          
        <div style="margin: 0 auto;text-align: center;">
            <a href="#" id="confidentialityOKButton" onClick="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">OK</a>
      	</div>
    </div>
</div>