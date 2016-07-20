<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/sessionTimeout.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
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
