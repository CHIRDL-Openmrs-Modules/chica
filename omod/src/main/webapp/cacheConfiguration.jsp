<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp" %>
<!DOCTYPE html>
<openmrs:require allPrivileges="Manage CHICA" otherwise="/login.htm" redirect="/module/chica/cacheConfiguration.form" />
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/cacheConfiguration.css">

<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/jquery-ui.min.css" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/jquery-ui.structure.min.css" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/jquery-ui.theme.min.css" />
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
	            <li><a href="#formDraftCacheTab">Form Draft</a></li>
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
	        
            <div id="formDraftCacheTab">
                <fieldset>
                    <div class="item">Configuration Location: </div>
                    <div class="value">
                        <c:out value="${cacheConfigurationLocation}"/>
                    </div>
                    <div class="item">Items Expiry: </div>
                    <div class="value">
                        <c:out value="${formDraftCacheExpiry}"/>&nbsp;<c:out value="${formDraftCacheExpiryUnit}"/>&nbsp;(not modifiable)
                    </div>
                    <div class="item">Disk Size: </div>
                    <div class="value">
                        <c:out value="${formDraftCacheDiskSize}"/>&nbsp;<c:out value="${formDraftCacheDiskSizeUnit}"/>&nbsp;(not modifiable)
                    </div>
                    <div class="item">Heap Size: </div>
                    <div class="value">
                        <input type="number" id="formDraftCacheHeapSize" name="formDraftCacheHeapSize" value="<c:out value='${formDraftCacheHeapSize}'/>">
                        &nbsp;<c:out value="${formDraftCacheHeapSizeUnit}"/>
                    </div>
                </fieldset>
                <br/>
                <div id="formDraftCacheStatistics">
                   <h3>Statistics</h3>
                   <span>(times are in microseconds)</span>
                   <ul>
                       <c:forEach items="${formDraftCacheStatistics}" var="stat">
                           <li><div align="left" style="width: 100%;"><c:out value="${stat.name}"/>:&nbsp;<c:out value="${stat.value}"/></div></li>
                       </c:forEach>
                   </ul>
                </div>
                <div id="clearFormFromDraftCache">
                    <h3>Clear Specific Form Draft</h3>
                    <fieldset>
                        <div class="item">Form ID: </div>
                        <div class="value">
                            <input type="number" id="formCacheFormId" name="formCacheFormId" value=""/>
                        </div>
                        <div class="item">Form Instance ID: </div>
                        <div class="value">
                            <input type="number" id="formCacheFormInstanceId" name="formCacheFormInstanceId" value=""/>
                        </div>
                        <div class="item">Location ID: </div>
                        <div class="value">
                            <input type="number" id="formCacheLocationId" name="formCacheLocationId" value=""/>
                        </div>
                        <div class="item">Location Tag ID: </div>
                        <div class="value">
                            <input type="number" id="formCacheLocationTagId" name="formCacheLocationTagId" value=""/>
                        </div>
                    </fieldset>
                    <input type="button" id="clearFormDraftButton" class="clearCacheButtons" value="Clear Form"/>
                </div>
                <br/>
                <div id="clearFormDraftCache">
                    <h3>Clear All Form Drafts</h3>
                   <input type="button" id="clearFormDraftCacheButton" class="clearCacheButtons" value="Clear Cache"/>
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
        <div id="clearFormDraftCacheConfirmationDialog" title="Confirm Clear Cache" class="ui-dialog-titlebar ui-widget-header" style="overflow-x: hidden;">
            <div style="margin: 0 auto;text-align: center;">
                <div style="color:#000000;">Are you sure you want to clear the Form Draft Cache?</div>
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
