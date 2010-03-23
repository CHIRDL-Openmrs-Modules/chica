<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page  import="org.openmrs.web.WebConstants" %>
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

<html  style="height:100%;"  xmlns="http://www.w3.org/1999/xhtml">
	<head >
		<openmrs:htmlInclude file="/openmrs.css" />
		<openmrs:htmlInclude file="/style.css" />
		<openmrs:htmlInclude file="/openmrs.js" />
			<title>CHICA Greaseboard</title>
		
		<script type="text/javascript">
			/* variable used in js to know the context path */
			var openmrsContextPath = '${pageContext.request.contextPath}';
		</script>
		
		<!--  Page Title : '${pageTitle}' 
			OpenMRS Title: <spring:message code="openmrs.title"/>
		-->
		<c:choose>
			<c:when test="${!empty pageTitle}">
				<title>${pageTitle}</title>
			</c:when>
			<c:otherwise>
				<title><spring:message code="openmrs.title"/></title>
			</c:otherwise>
		</c:choose>		
		<SCRIPT LANGUAGE="JavaScript">
		<!-- Idea by:  Nic Wolfe -->
		<!-- This script and many more are available free online at -->
		<!-- The JavaScript Source!! http://javascript.internet.com -->
		function popUp(URL) {
			day = new Date();
			id = day.getTime();
			eval("page" + id + " = window.open(URL, '" + id + "', 'toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=0,width=250,height=250,left = 312,top = 284');");
		}

		function pagerPopUp(URL) {
			window.open(URL, '', 'toolbar=0, scrollbars=0,location=0,locationbar=0,statusbar=0,menubar=0,resizable=0,width=400,height=100,left = 312,top = 284');
		}
		
		function lookupPatient(){
			document.location.href = "viewPatient.form";
			return false;
		}

                function popupfull(url) 
                {
 		params  = 'width='+screen.width;
 		params += ', height='+screen.height;
 		params += ', top=0, left=0'
 		params += ', fullscreen=no';

 		newwin=window.open(url,'windowname4', params);
 		if (window.focus) {newwin.focus()}
 		return false;
 		}
		</script>
 
</head>

<body  style="height:100%;" >
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<table class="reportTable" cellspacing="0">
<tr>
<th>
<form name="input" action="weeklyReports.form" method="post">
<select name="locationName"  onchange="this.form.submit();">
<option>&lt;choose a location&gt;</option>
<c:forEach items="${locations}" var="location">
<option <c:if test="${locationName == location.name}">selected</c:if> value="${location.name}">${location.name}</option>
</c:forEach>
</select>
</form>
</th>
<c:forEach items="${psfsPrintedMap}" var="item">
<th>${item.key }</th>
</c:forEach>
</tr>
<tr>
<td>#&nbsp;PSF&nbsp;Printed</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${item.value.data }</td>
</c:forEach>
</tr>
<tr>
<td>#&nbsp;PSF&nbsp;scanned</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${psfsScannedMap[item.key].data }</td>
</c:forEach>
</tr>
<tr bgcolor="yellow">
<td>%&nbsp;of&nbsp;PSF&nbsp;Scanned</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${psfsPercentScannedMap[item.key].data }&nbsp;%</td>
</c:forEach>
</tr>
<tr>
<td>#&nbsp;scanned&nbsp;PSFs&nbsp;w >=1&nbsp;Box&nbsp;Chked</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${psfsScannedAnsweredMap[item.key].data }</td>
</c:forEach>
</tr>
<tr>
<td>#&nbsp;scanned&nbsp;PSFs&nbsp;w anything&nbsp;marked</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${psfsScannedAnythingMarkedMap[item.key].data }</td>
</c:forEach>
</tr>
<tr>
<td>%&nbsp;of&nbsp;scanned&nbsp;PSFs&nbsp;w >=1&nbsp;Box&nbsp;Chked</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${psfsPercentScannedAnsweredMap[item.key].data }&nbsp;%</td>
</c:forEach>
</tr>
<tr bgcolor="yellow">
<td>%&nbsp;of&nbsp;scanned&nbsp;PSFs with&nbsp;anything&nbsp;marked</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${psfsPercentScannedAnythingMarkedMap[item.key].data }&nbsp;%</td>
</c:forEach>
</tr>
<tr>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>&nbsp;</td>
</c:forEach>
<td>&nbsp;</td>
</tr>
<tr>
<td>#&nbsp;PWS&nbsp;Printed</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwssPrintedMap[item.key].data }</td>
</c:forEach>
</tr>
<tr>
<td>#&nbsp;PWS&nbsp;scanned</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwssScannedMap[item.key].data }</td>
</c:forEach>
</tr>
<tr bgcolor="yellow">
<td>%&nbsp;of&nbsp;PWS&nbsp;Scanned</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwssPercentScannedMap[item.key].data }&nbsp;%</td>
</c:forEach>
</tr>
<tr>
<td>#&nbsp;scanned&nbsp;PWSs&nbsp;w >=1&nbsp;Box&nbsp;Chked</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwssScannedAnsweredMap[item.key].data }</td>
</c:forEach>
</tr>

<tr>
<td>#&nbsp;scanned&nbsp;PWSs&nbsp;w anything&nbsp;marked</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwssScannedAnythingMarkedMap[item.key].data }</td>
</c:forEach>
</tr>
<tr>
<td>%&nbsp;of&nbsp;scanned&nbsp;PWSs&nbsp;w >=1 Box&nbsp;Chked</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwssPercentScannedAnsweredMap[item.key].data }&nbsp;%</td>
</c:forEach>
</tr>
<tr bgcolor="yellow">
<td>%&nbsp;of&nbsp;scanned&nbsp;PWSs with&nbsp;anything&nbsp;marked</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwssPercentScannedAnythingMarkedMap[item.key].data }&nbsp;%</td>
</c:forEach>
</tr>
<tr>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>&nbsp;</td>
</c:forEach>
<td>&nbsp;</td>
</tr>
<tr>
<td>#&nbsp;PSF&nbsp;Questions Printed&nbsp;&&nbsp;Scanned</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${psfQuestionsScannedMap[item.key].data }</td>
</c:forEach>
</tr>
<tr>
<td>#&nbsp;PSF&nbsp;Questions&nbsp;w >=&nbsp;1&nbsp;Box&nbsp;Chked</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${psfQuestionsScannedAnsweredMap[item.key].data }</td>
</c:forEach>
</tr>
<tr>
<td>%&nbsp;PSF&nbsp;Prompts&nbsp;w&nbsp;Response</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${psfPercentQuestionsScannedAnsweredMap[item.key].data }&nbsp;%</td>
</c:forEach>
</tr>
<tr bgcolor="yellow">
<td>%&nbsp;PSF&nbsp;Prompts&nbsp;w Response&nbsp;-&nbsp;adjusted&nbsp;for&nbsp;blanks</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${psfPercentQuestionsScannedAnsweredAdjustedMap[item.key].data  }&nbsp;%</td>
</c:forEach>
</tr>
<tr>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>&nbsp;</td>
</c:forEach>
<td>&nbsp;</td>
</tr>
<tr>
<td>#&nbsp;PWS&nbsp;Questions Printed&nbsp;&&nbsp;Scanned</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwsQuestionsScannedMap[item.key].data  }</td>
</c:forEach>
</tr>
<tr>
<td>#&nbsp;PWS&nbsp;Questions&nbsp;w >=&nbsp;1&nbsp;Box&nbsp;Chked</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwsQuestionsScannedAnsweredMap[item.key].data  }</td>
</c:forEach>
</tr>
<tr>
<td>%&nbsp;PWS&nbsp;Prompts&nbsp;w&nbsp;Response</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwsPercentQuestionsScannedAnsweredMap[item.key].data  }&nbsp;%</td>
</c:forEach>
</tr>
<tr bgcolor="yellow">
<td>%&nbsp;PWS&nbsp;Prompts&nbsp;w&nbsp;Response -&nbsp;adjusted&nbsp;for&nbsp;blanks</td>
<c:forEach items="${psfsPrintedMap}" var="item">
<td>${pwsPercentQuestionsScannedAnsweredAdjustedMap[item.key].data  }&nbsp;%</td>
</c:forEach>
</tr>
</table>
</body>
</html>