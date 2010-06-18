<%@ include file="/WEB-INF/template/include.jsp" %>

<page height="100%" >
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<form height="100%" name="input" action="displayTiff.form" method="get">

<table height="5%" width="100%" class="displayTiffHeader chicaBackground">
<tr width="100%">
<td width="25%">
<INPUT TYPE="button" class="exitButton" VALUE="Exit" onClick="history.go(-1);return true;"> </td>
<td width="25%" class="displayLeftTiffHeaderSegment" >
<c:if test= "${!empty leftImageFormname}">
<b>${leftImageFormname}:&nbsp;${leftImageForminstance}</b>
</c:if>
<c:if test= "${empty leftImageFormname}">
N/A
</c:if>
</td>
<td width="50%" class="displayRighttiffHeaderSegment">
<b>
<c:if test= "${!empty rightImageFormname}">
${rightImageFormname}:&nbsp;${rightImageForminstance}
</c:if>
<c:if test= "${empty rightImageFormname}">
N/A
</c:if>
</b>
</td>
</tr>
</table>

<table width="100%" height="95%"  
class="tiffs"> 
<tr  height="90%"> 
<td width="50%">
<object width="100%" height="100%"
  classid="CLSID:106E49CF-797A-11D2-81A2-00E02C015623">
 <param name="src" value="${leftImagefilename}">
 
  <embed width="100%" height="100%"
    src="${leftImagefilename}" type="image/tiff"
    >

</object>
</td>
<td width="50%">
<object width="100%" height="100%"
  classid="CLSID:106E49CF-797A-11D2-81A2-00E02C015623">
 <param name="src" value="${rightImagefilename}">
 
  <embed width="100%" height="100%"
    src="${rightImagefilename}" type="image/tiff"
    >

</object>
</td>
</tr>
</table>
</form>
</page>








