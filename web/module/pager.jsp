<%@ include file="/WEB-INF/template/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page import="org.openmrs.web.WebConstants" %>

	
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />

<html>
<title>Page request</title>
<head>
</head>
<body  >
<form name="pager" method="post"  >
<table>
<tr>
<td>
<c:choose>
<c:when test="${success}"><b>Your page to CHICA support has been sent successfully!</b>
<br />
</c:when>
<c:otherwise><b>The paging network was unable to send your page. Please call 317-278-0552 directly for assistance.</b>
<br />
</c:otherwise>
</c:choose>
</td>
</tr>
<tr>
<td>
<center>
<input type="button"  value="OK"  onclick='window.close()' tabindex="3"/>
</center> 
</td>
</tr>
</form>
</body>
</html>
