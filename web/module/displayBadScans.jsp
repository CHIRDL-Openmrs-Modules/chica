<%@ include file="/WEB-INF/template/include.jsp"%>

<page height="100%">
<link
	href="${pageContext.request.contextPath}/moduleResources/chica/chica.css"
	type="text/css" rel="stylesheet" />
<SCRIPT LANGUAGE="JavaScript">
	function changeSelection(scanList){
		document.getElementById('deleteForm').value = 'false';
		document.getElementById('scans').value = scanList;
		document.forms["input"].submit();
	}
	
	function setScans(scanList) {
		document.getElementById('scans').value = scanList;
	}
</script>
<form height="100%" name="input" method="post">

<table height="5%" width="100%" class="displayTiffHeader chicaBackground">
    <tr width="100%">
        <td colspan="3" style="padding: 0px 0px 10px 0px"><font color="white" size="4px"><b>Please find and address the following forms.  Click the "Resolved" button once addressed to remove it from the list.</b></font></td>
    </tr>
	<tr width="100%">
	    <td width="48%" align="right" valign="bottom" style="vertical-align:middle">
	       <font color="white" size="4px"><b>Form: </b></font>
	       <c:choose>
		       <c:when test="${empty badScans}">
		          <font color="white" size="4px"><b><c:out value="No Bad Scans Found"/></b></font>
		       </c:when>
		       <c:otherwise>
		           <c:set var="scanList" value=""/>
		           <c:forEach items="${badScans}" var="badScan" varStatus="status">
		              <c:choose>
                         <c:when test="${status.count == 1 }">
                            <c:set var="scanList" value="${badScan}"/>
                         </c:when>
                         <c:otherwise>
                            <c:set var="scanList" value="${scanList},${badScan}"/>
                         </c:otherwise>
                      </c:choose>
		           </c:forEach>
			       <select id="badScansSelection" name="badScansSelection" onchange="changeSelection('${scanList}')">
			           <c:forEach items="${badScans}" var="badScan" varStatus="status">
			             <c:choose>
			               <c:when test="${selectedForm == badScan }">
			                 <option value="${badScan}" selected>${status.count}</option>
			               </c:when>
			               <c:otherwise>
			                 <option value="${badScan}">${status.count}</option>
			               </c:otherwise>
			             </c:choose>
			           </c:forEach>
		           </select>
	           </c:otherwise>
           </c:choose>
	    </td>
	    <td width="4%"></td>
		<td width="48%" align="left">
		  <c:if test="${!empty badScans}">
		      <INPUT TYPE="submit" class="exitButton"VALUE="Resolved" onClick="setScans('${scanList}')">
		  </c:if>
		  <INPUT TYPE="button" class="exitButton"VALUE="Exit" onClick="window.close()">
		</td>
	</tr>
	<c:if test="${moveError == 'true'}">
	   <tr width="100%">
	       <td width="100%" bgcolor="red" colspan="3">
	           <font color="white">Error moving the bad scan.  Check the server log for details.</font>
	       </td>
	   </tr>
	</c:if>
</table>

<table width="100%" height="95%" class="tiffs">
	<tr height="90%">
		<td width="50%">
		  <object width="100%" height="100%" classid="CLSID:106E49CF-797A-11D2-81A2-00E02C015623">
		    <c:if test="${!empty badScans}">
				<param id="param" name="src" value="${selectedForm}">
	
				<embed id="embed" width="100%" height="100%" src="${selectedForm}" type="image/tiff">
			</c:if>
	      </object>
	    </td>
	</tr>
</table>
<input type="hidden" value="true" name="deleteForm" id="deleteForm"/>
<input type="hidden" value="" name="scans" id="scans"/>
</form>
</page>




