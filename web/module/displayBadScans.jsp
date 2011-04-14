<%@ include file="/WEB-INF/template/include.jsp"%>

<page height="100%">
<link
	href="${pageContext.request.contextPath}/moduleResources/chica/chica.css"
	type="text/css" rel="stylesheet" />
<SCRIPT LANGUAGE="JavaScript">
	function changeSelection(scanList){
		setWait();
		document.getElementById('moveForm').value = 'false';
		document.getElementById('scans').value = scanList;
		document.forms['input'].submit();
	}
	
	function setScans(scanList) {
		setWait();
		document.getElementById("ignoreButton").style.cursor = 'wait';
		document.getElementById('scans').value = scanList;
	}
	
	function ignoreForm(scanList) {
		setWait();
		document.getElementById("ignoreButton").style.cursor = 'wait';
		document.getElementById('rescannedForm').value = 'false';
		document.getElementById('scans').value = scanList;
	}
	
	function setWait() {
		document.body.style.cursor = 'wait';
        document.getElementById("rescannedButton").style.cursor = 'wait';
        document.getElementById("ignoreButton").style.cursor = 'wait';
        document.getElementById("exitButton").style.cursor = 'wait';
        document.getElementById("badScansSelection").style.cursor = 'wait';
	}
</script>
<body>
<form height="100%" name="input" method="post">

<table height="5%" width="100%" class="displayTiffHeader chicaBackground">
    <c:if test="${!empty badScans}">
	    <tr width="100%" align="center">
	        <td colspan="3" style="padding: 0px 0px 10px 0px">
	            <font color="white" size="4px">
	                <b>Please find and rescan the following form.  Click the "Rescanned" button once addressed or click "Ignore" to permanently remove it from the list.</b>
	            </font>
	        </td>
	    </tr>
    </c:if>
	<tr width="100%">
	    <td align="center" style="padding: 0px 0px 10px 0px">
	       <c:choose>
		       <c:when test="${empty badScans}">
		          <font color="white" size="4px">
		              <b>
		                  <c:out value="No Bad Scans Found"/><br/>
		                  <c:out value="Please allow a few seconds after clicking \"Exit\" for the GreaseBoard to refresh to see if any rescan attempts completed successfully."/>
		              </b>
		          </font>
		       </c:when>
		       <c:otherwise>
		           <font color="white" size="4px"><b>Form: </b></font>
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
	</tr>
	<tr width="100%">
	   <td align="center">
	       <c:if test="${!empty badScans}">
              <input type="submit" id="rescannedButton" class="badScansButton" value="Rescanned" onClick="setScans('${scanList}')" 
                  onMouseOver="this.style.cursor='hand'">
              <input type="submit" id="ignoreButton" class="badScansButton" value="Ignore" onClick="ignoreForm('${scanList}')" 
                  onMouseOver="this.style.cursor='hand'">
           </c:if>
           <input type="button" id="exitButton" class="badScansButton" value="Exit" onClick="window.close()" 
              onMouseOver="this.style.cursor='hand'">
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
<input type="hidden" value="true" name="moveForm" id="moveForm"/>
<input type="hidden" value="true" name="rescannedForm" id="rescannedForm"/>
<input type="hidden" value="" name="scans" id="scans"/>
</form>
</body>
</page>




