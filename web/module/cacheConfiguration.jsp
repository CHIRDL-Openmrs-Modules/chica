<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/finishFormsWeb.form" />
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/cacheConfiguration.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/cacheConfiguration.js"></script>
<title>Cache Configuration</title>
</head>
<body>

<div id="content">
    <div id="title"><h1>Cache Configuration</h1></div>

    <form id="cacheForm" name="cacheForm" action="cacheConfiguration.form" method="post">
	    <div id="cacheTabs">
	        <ul>
	            <li><a href="#ehrCacheTab">EHR Medical Record</a></li>
	            <li><a href="#immunizationCacheTab">Immunization</a></li>
	        </ul>
	        <div id="ehrCacheTab">
	            <fieldset>
		            <div class="item">Configuration Location: </div>
		            <div class="value">
		                <c:out value="${cacheConfigurationLocation}"/>
		            </div>
		            <div class="item">Items Expiry: </div>
		            <div class="value">
		                <c:out value="${EHRCacheExpiry}"/>&nbsp;<c:out value="${EHRCacheExpiryUnit}"/>&nbsp;(not modifiable)
		            </div>
		            <div class="item">Disk Size: </div>
                    <div class="value">
                        <c:out value="${EHRCacheDiskSize}"/>&nbsp;<c:out value="${EHRCacheDiskSizeUnit}"/>&nbsp;(not modifiable)
                    </div>
		            <div class="item">Heap Size: </div>
		            <div class="value">
			            <input type="number" id="EHRCacheHeapSize" name="EHRCacheHeapSize" value="<c:out value='${EHRCacheHeapSize}'/>">
			            &nbsp;<c:out value="${EHRCacheHeapSizeUnit}"/>
		            </div>
	            </fieldset>
	            <br/>
	            <div id="ehrCacheStatistics">
	               <h3>Statistics</h3>
	               <span>(times are in microseconds)</span>
	               <ul>
		               <c:forEach items="${EHRCacheStatistics}" var="stat">
	                       <li><div align="left" style="width: 100%;"><c:out value="${stat.name}"/>:&nbsp;<c:out value="${stat.value}"/></div></li>
	                   </c:forEach>
                   </ul>
	            </div>
	            <div id="clearEhrCache">
	               <input type="button" id="clearEHRMedicalRecordCacheButton" class="clearCacheButtons" value="Clear Cache"/>
	            </div>
	        </div>
	        <div id="immunizationCacheTab">
                <fieldset>
                    <div class="item">Configuration Location: </div>
                    <div class="value">
                        <c:out value="${cacheConfigurationLocation}"/>
                    </div>
                    <div class="item">Items Expiry: </div>
                    <div class="value">
                        <c:out value="${immunizationCacheExpiry}"/>&nbsp;<c:out value="${immunizationCacheExpiryUnit}"/>&nbsp;(not modifiable)
                    </div>
                    <div class="item">Disk Size: </div>
                    <div class="value">
                        <c:out value="${immunizationCacheDiskSize}"/>&nbsp;<c:out value="${immunizationCacheDiskSizeUnit}"/>&nbsp;(not modifiable)
                    </div>
                    <div class="item">Heap Size: </div>
                    <div class="value">
                        <input type="number" id="immunizationCacheHeapSize" name="immunizationCacheHeapSize" value="<c:out value='${immunizationCacheHeapSize}'/>">
                        &nbsp;<c:out value="${immunizationCacheHeapSizeUnit}"/>
                    </div>
                </fieldset>
                <br/>
                <div id="immunizationCacheStatistics">
                   <h3>Statistics</h3>
                   <span>(times are in microseconds)</span>
                   <ul>
                       <c:forEach items="${immunizationCacheStatistics}" var="stat">
                           <li><div align="left" style="width: 100%;"><c:out value="${stat.name}"/>:&nbsp;<c:out value="${stat.value}"/></div></li>
                       </c:forEach>
                   </ul>
                </div>
                <div id="clearImmunizationCache">
                   <input type="button" id="clearImmunizationCacheButton" class="clearCacheButtons" value="Clear Cache"/>
                </div>
            </div>
	    </div>
	    <div class="submit">
	       <input type="button" id="submitButton" value="Update"/>
	    </div>
	    <div id="errorDialog" title="Error" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
            <div style="margin: 0 auto;text-align: center;">
                <div id="errorMessage" style="color:#000000;">${errorMessage}</div>
            </div>
        </div>
        <div id="submitConfirmationDialog" title="Confirm Update" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
            <div style="margin: 0 auto;text-align: center;">
                <div style="color:#000000;">Are you sure you want to update the cache settings?</div>
            </div>
        </div>
        <div id="clearEHRMedicalRecordCacheConfirmationDialog" title="Confirm Clear Cache" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
            <div style="margin: 0 auto;text-align: center;">
                <div style="color:#000000;">Are you sure you want to clear the EHR Medical Record Cache?</div>
            </div>
        </div>
        <div id="clearImmunizationCacheConfirmationDialog" title="Confirm Clear Cache" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
            <div style="margin: 0 auto;text-align: center;">
                <div style="color:#000000;">Are you sure you want to clear the Immunization Cache?</div>
            </div>
        </div>
        <div id="clearCacheCompleteDialog" title="Clear Cache" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
            <div style="margin: 0 auto;text-align: center;">
                <div id="cacheMessage" style="color:#000000;"></div>
            </div>
        </div>
    </form>
</div>

</body>
</html>
