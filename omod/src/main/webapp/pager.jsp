<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pager.form" />
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

<html  xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<openmrs:htmlInclude file="/openmrs.css" />
		<openmrs:htmlInclude file="/style.css" />
		<openmrs:htmlInclude file="/openmrs.js" />
			<title>Page request</title>
		<script type="text/javascript">
			/* variable used in js to know the context path */
			var openmrsContextPath = '${pageContext.request.contextPath}';
		</script>
		<script type="text/javascript">
<!-- borrowed from http://dadabase.de/dev/window_resizer.html -->



function setSize(width,height) {
	if (window.outerWidth) {
		window.outerWidth = width;
		window.outerHeight = height;
	}
	else if (window.resizeTo) {
		window.resizeTo(width,height);
	}
	window.moveTo(50,50);
}

function useConfirmationForm() {
	setTimeout('self.close();',5000);
	window.resizeTo(525,300); 
	window.moveTo(400,300);
}

function writeConfirmationMessage(success) {
	var text = "";
	if (success){
		text =  "Your page to CHICA support has been sent successfully!";
	} else {
		text =  "The paging network was unable to send your page. Please call 317-278-0552 directly for assistance.";
	}
	document.write(text.bold());
	
}

function validateForm()
{
    
    if(document.pager.reporter.value=="")
    {
      alert("Please fill in your name");
      document.pager.reporter.focus();
      return (false);
    }
    else 
     {
     
     return (true);
     }
}

<!-- borrowed from http://www.mediacollege.com/internet/javascript/form/limit-characters.html -->
function limitText(limitField, limitCount, limitNum) {
	if (limitField.value.length > limitNum) {
		limitField.value = limitField.value.substring(0, limitNum);
	} else {
		limitCount.value = limitNum - limitField.value.length;
	}
}


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
		
	</head>

<body  style="scrollbars:no"onload="javascript:setSize(600,560); <c:if test="${!empty sendPage}"> javascript:useConfirmationForm();</c:if>" onkeydown="if (event.keyCode==8) {event.keyCode=0; return event.keyCode }">
	<div id="pageBody" class="greaseBoardBackground"  style="width:100%">		
		<div id="contentMinimal">
			<c:if test="${msg != null}">
				<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" /></div>
			</c:if>
			<c:if test="${err != null}">
				<div id="openmrs_error"><spring:message code="${err}" text="${err}" arguments="${errArgs}"/></div>
			</c:if>
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />

	<c:choose>
			<c:when test="${!empty sendPage}">
			<script type="text/javascript">
			<c:choose>
			<c:when test="${!empty pageResponse}">
			var text = "${pageResponse}";
			document.write(text.bold());
			</c:when>
			<c:otherwise>
			writeConfirmationMessage(${pagerSuccess});
			</c:otherwise>
			</c:choose>
			</script>
			</c:when>
			
	<c:otherwise>
						
<form height="100%" name="pager" action="pager.form" method="get">
  <p><strong>Please enter your name, your reason for calling, and the best number to reach you. </br></br>
    &nbsp;</strong>
  <hr />
<p>
  Your name please: (Required)</p>
  <p>
    <input name="reporter" type="text" tabindex="1" value="" size="50" />
  </p>
  </p>
  <p>Problem and the best number to reach you: (Optional)</p>
<p>
  <textarea name="message" cols="50" rows="5" tabindex="1" 
  onKeyDown="limitText(this.form.message,this.form.countdown,160);"
  onKeyUp="limitText(this.form.message,this.form.countdown,160);"></textarea>
  <br>
<font size="1">(Maximum characters: 160)<br>
You have <input readonly type="text" name="countdown" size="3" value="160"> characters left.</font>
  <br><br>
</p>
<table>
  <tr>
<td><input type="submit" value="Send Request Now" onClick="return validateForm()" tabindex="2"/>  <input type="button" value="Cancel" onclick='window.close()' tabindex="3"/></td>
<td>&nbsp;</td>
</tr>
</table>
<input type="hidden" name="sendPage" value="true"/>
</form>
</c:otherwise>
</c:choose>	
</div>
</div>
</body>
</html>
