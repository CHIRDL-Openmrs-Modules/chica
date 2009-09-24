<%@ include file="/WEB-INF/template/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page import="org.openmrs.web.WebConstants" %>
<%
	pageContext.setAttribute("msg", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	pageContext.setAttribute("msgArgs", session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
	pageContext.setAttribute("err", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	pageContext.setAttribute("errArgs", session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
	session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
	
%>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<openmrs:htmlInclude file="/openmrs.css" />
		<openmrs:htmlInclude file="/style.css" />
		<openmrs:htmlInclude file="/openmrs.js" />
		
		<script type="text/javascript">
			/* variable used in js to know the context path */
			var openmrsContextPath = '${pageContext.request.contextPath}';
			
			function exitForm(){
				document.location.href("greaseBoard.form");
			}
		</script>
		
	
		
		
		<title>Patient MRN required</title>
		
	</head>

<body>
	<div id="pageBody">
		<div id="contentMinimal">
			<c:if test="${msg != null}">
				<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" /></div>
			</c:if>
			<c:if test="${err != null}">
				<div id="openmrs_error"><spring:message code="${err}" text="${err}" arguments="${errArgs}"/></div>
			</c:if>
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />


<form name="viewPatient" method="post" >
<table width = "100%" class="formTitleStyle greaseBoardBackground" >
<tr>
<td>
<b>Search For Patient Encounters</b>
</td>
</tr>
</table>
	<c:choose>
	<c:when test="${!empty param.validate}">
		<span class="alert" ><p><b>MRN: "${param.mrnLookup}" is not a valid MRN.</b></p></span>
    </c:when>
 	<c:otherwise>
		<c:choose>
 		<c:when test="${!empty param.error}">
			<span class="alert" ><p><b>There is no record of an existing patient with MRN: "${param.mrnLookup}" 
					 <br>You may need to add a patient through manual checkin.</b></p></span>
    	</c:when>
    	<c:otherwise>
        	<p><b>Enter the patient MRN to display all encounters for that patient</b></p>
        </c:otherwise>
		</c:choose>
    </c:otherwise>
</c:choose>

<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MRN <span style="font-size:8pt;" ></span></b>&nbsp;&nbsp;<input type="text"  size="8" name="mrnLookup" style="width:150px;"tabindex="1"/>
<input type="submit" name="viewPatientFromEncounterPage" value="Enter" tabindex="2"/> <input type="button" value="Cancel" onclick="javascript:exitForm();" tabindex="3"/>


<input type="hidden" name="validate" value="validate"/>
</form>
<script language="javascript">
    	document.viewPatient.mrnLookup.focus()
 </script>
		<br/>
		</div>
	</div>
</body>
</html>