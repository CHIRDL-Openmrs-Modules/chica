<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/finishFormsWeb.form" />
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/cacheConfiguration.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<div id="content">
    <div id="title"><h1>Fax Request Status</h1></div>
</div>
</head>
<body>
	<table style="width: 100%; overflow: auto;" class="chicaBackground" id="encountersTable">
		<tbody>
			<thead id="faxStatusHeader">
		        <tr>
                    <th class="faxDate chicaTableHeader"><b>Transmit Date/Time
                       </b></th>
                    <th class="PatientMRN chicaTableHeader"><b>MRN</b></th>
                    <th class="PatientLastName chicaTableHeader"><b>Patient Last Name</b></th>
                    <th class="PatientLastName chicaTableHeader"><b>Patient First Name</b></th>
                    <th class="faxSubjectText chicaLocationHeader"><b>Location</b></th>
                    <th class="faxNumber chicaTableHeader"><b>FaxNumber</b></th>
                    <th class="faxSubjectText chicaTableHeader"><b>Subject</b></th>
                    <!--<th class="connectTime chicaTableHeader"><b>Connect Time</b></th> -->
				    <th class="formInstance chicaTableHeader"><b>Form Instance</b></th>
				    <th class="formInstance chicaTableHeader"><b>Attachment count</b></th>
                    <!-- <th class="numberOfAttempts chicaTableHeader"><b># of Attempts</b></th> -->
                    <th class="faxStatusText chicaTableHeader"><b>Status</b></th>
                    <th class="faxId chicaTableHeader"><b>Id tag</b>
                    <th class="uniqueJobID chicaTableHeader"><b>Unique Job ID</b></th>
               </tr>
		    </thead>
				<c:forEach items="${faxStatusRows}" var="row" varStatus="status">
					<c:choose>
						<c:when test='${(status.index)%2 eq 0}'>
							<c:set var="rowColor" value="tableRowOdd" scope="page" />
						</c:when>
						<c:otherwise>
							<c:set var="rowColor" value="tableRowEven" scope="page" />
						</c:otherwise>
					</c:choose>
					<tr style="text-align: left">
						<td class="faxDate ${rowColor}">${row.transmitTimeAsString}</td>
						<td class="patientMRN ${rowColor}">${row.patientMRN}</td>
						<td class="patientLastName ${rowColor}">${row.patientLastName}</td>
						<td class="patientFirstName ${rowColor}">${row.patientFirstName}</td>
						<td class="faxLocation ${rowColor}">${row.location}</td>
						<td class="faxNumber ${rowColor}">${row.faxNumber}</td>
						<td class="faxSubjectText ${rowColor}">${row.subject}</td>
						<!-- <td class="connectTime" {rowColor}">${row.connectTime}</td> -->
						<td class="formInstance ${rowColor}">${row.formInstanceString}</td>
						<td class="attachmentCount ${rowColor}">${row.attachmentCount}</td>
						<!-- <td class="numberOfAttempts ${rowColor}">${row.numberOfAttempts}</td> -->
						<td class="faxStatusText ${rowColor}">${row.statusText}</td>
						<td class="faxId" {rowColor}">${row.idTag}</td>
						<td class="uniqueJobID ${rowColor}">${row.uniqueJobID}</td>
						
						
					
					</tr>
				</c:forEach>
			</tbody>
	</table>

</body>
</html>
