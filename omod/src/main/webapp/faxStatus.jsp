<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp" %>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables-1.10.6.min.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables_themeroller-1.10.6.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables-1.10.6.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/atd/jquery-ui-1.11.4.css">
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/moduleResources/chica/faxStatus.css" type="text/css" rel="stylesheet" />


<script type="text/javascript" charset="utf8" src="${pageContext.request.contextPath}/moduleResources/atd/jquery-1.11.1.min.js"></script>
<script type="text/javascript" charset="utf8" src="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables-1.10.6.min.js"></script>
<script type="text/javascript" charset="utf8" src="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables-1.10.6.js"></script>
  <script>
  $( function() {
    $j( "#dialog" ).dialog();
    $j("#viewimagebutton").button();
    
  } );
  </script>

<script LANGUAGE="JavaScript">
	var statusTable;
	
	
	$(document).ready(function() {
		statusTable = $('#faxStatusTable').dataTable(
				{ 

					
				"columns": [ 
		            {"mData": "transmitTimeAsString"},
		            {"mData": "patientMRN"},
		            {"mData": "patientLastName"},
		            {"mData": "patientFirstName"},
		            {"mData": "location"},
		            {"mData": "faxNumber"},
		            {"mData": "subject"},
		            {"mData": "statusText"},
		            {"mData": "idTag"},
		            {"mData": "image"},
		            {"mData": "uniqueJobID"}
				            ],

				"columnDefs" : [
       				 	{	"targets": 'image',
       				 		"render": function (data, url, row, meta){
       				 			return imagedialog(data, url, row, meta);
       				 	}
       				 	}],
       				 		
				"jQueryUI": true, 
				"pagingType": "full", 
				"filter": true,
				"sScrollY" : "500px",
				"bProcessing" : true,
				"oLanguage": {
					"sLengthMenu": "Display _MENU_ records per page",
					"sInfo": "Showing _START_ to _END_ of _TOTAL_ records",
					"sInfoEmtpy": "Showing 0 to 0 of 0 records"
				},
				 "order": [[ 0, "desc" ]]
				});
		
	} );
	
	
	function imagelink(data, type, row, meta){
		var idtag = row['idTag'];
		var text = "";
		if (idtag == "" || idtag.startsWith("V")){
			text = '<span> Image not available </span>';
		}else{
			text = '<a href="#" class="view_image">View image</a>';
		}
		return text;
	}
	
	function imagedialog(data, type, row, meta){
		var idtag = row['idTag'];
		var text = "";
		if (idtag == "" || idtag.startsWith("V")){
			text = '<span> Image not available </span>';
		}else{
			//todo
			
		}
		return text;
	}
	
	
</script>


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
		<form id = "status" method="post" action = "faxStatus.form" >   	
			<c:if test="${noAccessToHost == 'true'}">
			     <span class="alert" style="font-size: 16px" >Error connecting to the network fax web service. Please check network connections to host!</span>
	        </c:if>
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
									<th id="idTag" class='id_tag'>ID Tag</th>
									<th id="image" class='image'>Image</th>
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
										<td></td>
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
				<tr><td><input  type="Submit" name="queryStatus" id="queryStatus" value="Refresh"/></td>
					</tr>
			</table>
			<br/>
			<br/>
		</form>
	</div>
	 
</form>
</body>


</html>
