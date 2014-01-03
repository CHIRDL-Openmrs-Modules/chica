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


<form name="forcePrintSSNMRN" method="post" >
<c:choose>
	<c:when test="${!empty param.validate}">
		<span class="alertInvalidMRN"><p><b>MRN "${param.mrnLookup}" is not valid.<br>Retype the MRN #. Press OK to display the record.</b></p></span>
    </c:when>
 	<c:otherwise>
        <p><b>Type the MRN #. Press OK to display the record.</b></p>
    </c:otherwise>
</c:choose>
<b>MRN <span style="font-size:8pt"></span></b>&nbsp;<input type="text" size="8" name="mrnLookup" tabindex="1"/><br><br>
<table>
<tr>
<td><input type="button" value="Cancel" onclick='window.close()' tabindex="3"/></td>
<td><input type="submit" value="OK" tabindex="2"/></td>
</tr>
</table>
<input type="hidden" name="validate" value="validate"/>
<input type="hidden" value="${patientId}" name="patientId" />

</form>
<script language="javascript">
    	document.forcePrintSSNMRN.mrnLookup.focus()
 </script>
		<br/>
		</div>
	</div>
</body>
</html>