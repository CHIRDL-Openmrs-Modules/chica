<%@ include file="/WEB-INF/template/include.jsp" %>

<page height="100%" >
<script type="text/javascript">
function setNAString(name){
	if (name.toLowerCase() == "notavailable"){
		return "N/A";
		}
	return name;
}
</script>
<link href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" rel="stylesheet" />
<form height="100%" name="input" action="displayTiff.form" method="get">

<table height="5%" width="100%" class="displayTiffHeader chicaBackground">
<tr width="100%">
<td width="25%">
<INPUT TYPE="button" class="exitButton" VALUE="Exit" onClick="history.go(-1);return true;"> </td>
<td width="25%" class="displayPSFTiffHeaderSegment" >
<b>PSF: <script type="text/javascript">
document.write (setNAString("${psffilename}"));
</script></b></td>
<td width="50%" class="displayPWStiffHeaderSegment"><b>PWS:  <script type="text/javascript">
document.write (setNAString("${pwsfilename}"));
</script> </b></td>
</tr>
</table>

<table width="100%" height="95%"  
class="tiffs"> 
<tr  height="90%"> 
<td width="50%">
<object width="100%" height="100%"
  classid="CLSID:106E49CF-797A-11D2-81A2-00E02C015623">
 <param name="src" value="${psfdir}${psffilename}.tif">
 
  <embed width="100%" height="100%"
    src="${psfdir}${psffilename}.tif" type="image/tiff"
    >

</object>
</td>
<td width="50%">
<object width="100%" height="100%"
  classid="CLSID:106E49CF-797A-11D2-81A2-00E02C015623">
 <param name="src" value="${pwsdir}${pwsfilename}.tif">
 
  <embed width="100%" height="100%"
    src="${pwsdir}${pwsfilename}.tif" type="image/tiff"
    >

</object>
</td>
</tr>
</table>
</form>
</page>








