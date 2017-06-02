<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>View Fax Requests</title>
</head>

<h3>Fax Requests</h3>

<body>

<div id= "status_div"> 
		<c:if test= "${error}=='serverError'">
			<h2>Server error: operation failed.</h2>
		</c:if>
		<form method="post" action = "faxStatus.form" >   			
			<table style="padding-left: 5px; padding-top: 5px;" width="100%">
				<tr>
				<td>
					<div id= "faxStatus_div">
						<table id="faxStatusTable" class="display" cellspacing="0" width="100%">
							<thead>
								<tr>
									<th id="transitTime">Transmit Time</th>
									<th id="MRN">MRN</th>
									<th id="lastName">Last Name</th>
									<th id="firstName">First Name</th>
									<th id="location">Location</th>
									<th id="faxNumber">Fax Number</th>
									<th id="subject">Subject</th>
									<th id="statusText">Status</th>
									<th id="idTag">ID Tag</th>
									<th id="uniqueID">Unique Job ID</th>
								</tr>
							</thead>
							
							<tbody>
								<c:forEach var="faxStatusResults" items="${faxStatusRows}" varStatus="status">
									<tr>
										<td>${faxStatusResults.transmitTimeAsString}</td>
										<td>${faxStatusResults.patientMRN }</td>
										<td>${faxStatusResults.patientLastName}</td>
										<td>${faxStatusResults.patientFirstName}</td>
										<td>${faxStatusResults.location}</td>
										<td>${faxStatusResults.faxNumber}</td>
										<td>${faxStatusResults.subject}</td>
										<td>${faxStatusResults.statusText}</td>
										<td>${faxStatusResults.idTag}</td>
										<td>${faxStatusResults.uniqueJobID}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</td>
				</tr>
			</table>
			
			<hr size="3" color="black"/>
			
			<table align="right">
				<tr><td><input type="Submit" name="queryStatus" id="queryStatus" value="Query Fax Status"/></td>
					</tr>
			</table>
			<br/>
			<br/>
		</form>
	</div> 
</form>
</body>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables-1.10.6.min.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables_themeroller-1.10.6.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/atd/jquery-ui-1.11.4.min.css">

<script type="text/javascript" charset="utf8" src="${pageContext.request.contextPath}/moduleResources/atd/jquery-1.11.1.min.js"></script>
<script type="text/javascript" charset="utf8" src="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables-1.10.6.min.js"></script>
<script type="text/javascript">
	var statusTable;
	
	$(document).ready(function() {
		statusTable = $('#faxStatusTable').dataTable(
				{
					// Table options have been updated for DataTables version 1.10
					"columns": [  { "sName": "transmitTimeAsString", "bSortable": true},
			
				                 { "sName": "patientMRN", "bSortable": true} ,
				                 { "sName": "patientLastName", "bSortable": true},
				                 { "sName": "patientFirstName", "bSortable": true},
				                 { "sName": "location", "bSortable": true},
				                 { "sName": "faxNumber", "bSortable": false},
				                 { "sName": "subject", "bSortable": true},
				                 { "sName": "statusText", "bSortable": true},				           
				                 { "sName": "idTag", "bSortable": true,
				                	 "mRender": function ( data, type, row){
				                	 return '<button type="button">Click Me!</button>';
				                 }},
				                 { "sName": "uniqueJobID", "bSortable": true}
				              ],
					"jQueryUI": true, 
					"pagingType": "full_numbers", 
					"filter": false});
	} );
	
	function backToConfigManager()
	{
		window.location = '${pageContext.request.contextPath}/module/atd/configurationManager.form';
	}
</script>
</html>
