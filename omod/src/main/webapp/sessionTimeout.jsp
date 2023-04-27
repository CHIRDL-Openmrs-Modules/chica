<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/sessionTimeout.css">

<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/jquery-ui.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/jquery-ui.min.css" />
<openmrs:htmlInclude file="/scripts/jquery-ui/jquery-ui.structure.min.css" />
<openmrs:htmlInclude file="/scripts/jquery-ui/jquery-ui.theme.min.css" />

<title>CHICA Session Timeout</title>
</head>
<body>

<div id="content">
    <div>
        <div class="ui-state-error"><h2><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>The CHICA session has timed out.</h2></div>
        <br/>
        <div>Please close this window at your convenience.</div>
    </div>
</div>
</body>
</html>
