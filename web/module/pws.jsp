<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<html style="height:100%;" xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link href="./resources/pws.css" type="text/css" rel="stylesheet" />
        <title>CHICA Physician Encounter Form</title>
    </head>

    <body>
        <form name="input" action="pws.form" method="post">
            <div id="container">
            	<div id="title">
                	<h3>CHICA Physician Encounter Form</h3>
                </div>
                <div id="mrn">
                	<h3>${MRN}</h3>
                </div>
            </div>
            <div id="infoLeft">
            	<b>Patient:</b> ${PatientName}<br/>
                <b>DOB:</b> ${DOB} <b>Age:</b> ${Age}<br/>
                <b>Doctor:</b> ${Doctor}
            </div>
            <div id="infoRight">
            	<b>MRN:</b> ${MRN}<br/>
                <b>Date:</b> ${VisitDate}<br/>
                <b>Time:</b> ${VisitTime}
            </div>
            <div id="vitals">
            	<div id="flags">
                	<b>A</b><br/>
                </div>
            </div>
        </form>
    </body>
</html> 