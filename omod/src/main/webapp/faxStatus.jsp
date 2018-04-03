<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp" %>
<!DOCTYPE html>
  <openmrs:require allPrivileges="Manage CHICA" otherwise="/login.htm" redirect="/module/chica/faxStatus.form" />
<html>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables-1.10.6.min.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables_themeroller-1.10.6.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css" /> 
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/moduleResources/chica/faxStatus.css" type="text/css" rel="stylesheet" />

<script>var ctx = "${pageContext.request.contextPath}";</script>
<script type="text/javascript" charset="utf8" src="${pageContext.request.contextPath}/moduleResources/atd/jquery-1.11.1.min.js"></script>
<script type="text/javascript" charset="utf8" src="${pageContext.request.contextPath}/moduleResources/atd/jquery.dataTables-1.10.6.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/faxStatus.js"></script>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>View Fax Requests</title>
</head>

<h3>Fax Service Status</h3>
<h4>Status of fax server requests sent from CHICA to clinic fax numbers.</h4>


<body>
</div>		
	<div id= "status_div"> 
		<c:if test= "${error}=='serverError'">
			<h2>Server error: operation failed.</h2>
		</c:if>
		<form id = "status" method="post" action = "faxStatus.form" >  
			<c:if test="${noAccessToHost == 'true'}">
			     <span class="alert" style="font-size: 16px" >Error connecting to the network fax web service. Please check network connections to host!</span>
	        </c:if>
	        
	        	<table  id = "searchCriteria" style="padding-left: 10px; padding-top: 10px;" >
		        	<tbody>
			        	<tr>
		                    <td>Number of fax records ( Default = 100) </td>
		                    <td><input type="text" name="count" class="ui-field-contain" value="${rowcount}" />
		                    <c:if test="${validInteger == 'false'}">
			     				<span class="alert" style="font-size: 16px" >Value entered is not numeric.</span>
	        				</c:if>
	        				</td>
		                </tr>
		          
			        	
	    				<tr> <td>Start Date: </td><td><input type="text" name="datepickerStart" id="datepickerStart" value = "${startDate}"></td>
	    				</tr>
	    				<tr> <td>Stop Date: </td><td><input type="text" name="datepickerStop" id="datepickerStop" value = "${stopDate}"></td>
	    				</tr>
	    				
					</tbody>
		        </table>
			<div style="padding-left: 10px; padding-top: 10px;" width="100%">
							<input  type="Submit" class="ui-button ui-widget ui-corner-all" name="queryStatus" id="queryStatus" value="Get Fax Status"/>
							<input type="button" class="ui-button ui-widget ui-corner-all" value="Exit" onclick="backToAdminPage();"/>
			</div>
	 
			<table style="padding-left: 10px; padding-top: 10px;" width="100%">
				
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
									<th id="recipientName">Recipient Name</th>
									<th id="location">Location</th>
									<th id="faxNumber">Fax Number</th>
									<th id="subject">Subject</th>
									<th id="statusText">Status</th>
									<th id="idTag">ID Tag</th>
									<th id="image" class='image'>Image/ID Tag</th>
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
										<td>${faxStatusResults.recipientName}</td>
										<td>${faxStatusResults.locationName}</td>
										<td>${faxStatusResults.faxNumber}</td>
										<td>${faxStatusResults.subject}</td>
										<td>${faxStatusResults.statusText}</td>
										<td>${faxStatusResults.idTag}</td>
										<td>${faxStatusResults.imageFileLocation}</td>
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
			
			
			<br/>
			<br/>
		</form>
	</div>
	
	<div id="viewImageDialog" title="Fax image" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">

		<iframe id="imageDisplay" class="form_pdf_object"  > 
			<span class="pdf_error">It appears your Web browser is not configured to display PDF files. 
			<a class="link" href='http://get.adobe.com/reader/'>Click here to download the Adobe PDF Reader.</a>  Please restart your browser once the installation is complete.</span>
		</iframe>
	</div>
	
</form>
</body>


</html>
