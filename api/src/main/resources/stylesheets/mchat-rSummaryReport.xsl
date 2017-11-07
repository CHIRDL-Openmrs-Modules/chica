<?xml version="1.0" encoding="ISO-8859-1"?>

 <xsl:stylesheet version="2.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 
 <xsl:template match="/"> 
	<html>
		<body>
		  <div style="border: 1px solid black; width: 600px; height: 800px; margin: 0 auto;">
			<table align="center">
				<xsl:apply-templates select="/Records"/> 
				<xsl:apply-templates select="/Records/Record/Field"/> 
			</table>
		  </div>	
		</body>
	</html>
 </xsl:template>
 
 <xsl:template match="Records">
	<tr>
		<xsl:variable name="patientName" select="Record/Field[@id = 'patient_name']"/>
		<td align="left" style="padding: 0px 10px 10px 0px; width: 110px;"><b>Name:</b></td>
		<td align="left" style="padding: 0px 70px 10px 0px; width: 150px;"><b><xsl:value-of select="$patientName/Value"/></b></td>
		<xsl:variable name="mrn" select="Record/Field[@id = 'MRN']"/>
		<td align="left" style="padding: 0px 0px 10px 0px; width: 100px;"><b>MRN:</b></td>
		<td align="left" style="padding: 0px 0px 10px 0px; width: 150px;"><b><xsl:value-of select="$mrn/Value"/></b></td>
	</tr>
	<tr>
		<xsl:variable name="dob" select="Record/Field[@id = 'date_of_birth']"/>
		<td align="left" style="padding: 0px 10px 10px 0px; width: 110px;"><b>Date of Birth:</b></td>
		<td align="left" style="padding: 0px 70px 10px 0px; width: 150px;"><b><xsl:value-of select="$dob/Value"/></b></td>
		<xsl:variable name="provider" select="Record/Field[@id = 'provider']"/>
		<td align="left" style="padding: 0px 0px 10px 0px; width: 100px;"><b>Provider:</b></td>
		<td align="left" style="padding: 0px 0px 10px 0px; width: 150px;"><b><xsl:value-of select="$provider/Value"/></b></td>
	</tr>
	<tr>
		<xsl:variable name="dor" select="Record/Field[@id = 'date_of_report']"/>
		<td align="left" style="padding: 0px 10px 10px 0px; width: 110px;"><b>Date of Report:</b></td>
		<td align="left" style="padding: 0px 70px 10px 0px; width: 150px;"><b><xsl:value-of select="$dor/Value"/></b></td>
		<xsl:variable name="dov" select="Record/Field[@id = 'date_of_visit']"/>
		<td align="left" style="padding: 0px 0px 10px 0px; width: 100px;"><b>Date of Visit:</b></td>
		<td align="left" style="padding: 0px 0px 10px 0px; width: 150px;"><b><xsl:value-of select="$dov/Value"/></b></td>
	</tr>
	<tr>
		<td colspan="4" align="center" style="padding: 20px 0px 0px 0px"><h2>Report of M-CHAT-R&#153; Assessment Score</h2></td>
	</tr>
	<tr>
		<td colspan="4" align="center">
			<table border="0" style="border:none; empty-cells:show; border-collapse:collapse">
				<tr>
					<th align="left" style="padding: 10px 20px 0px 10px; border:none;"></th>
					<th align="center" style="padding: 10px 20px 0px 10px; border:none;">Patient Score</th>
					<th align="center" style="padding: 10px 20px 0px 10px; border:none;">Interpretation</th>
				</tr>
				<tr>
					<td style="padding: 10px 20px 0px 10px; border: 1px solid black;"># of Items Failed:</td>
					<xsl:variable name="failed" select="Record/Field[@id = 'total_failed']"/>
					<td align="center" style="padding: 10px 20px 0px 10px; border: 1px solid black;"><xsl:value-of select="$failed/Value"/></td>
					<xsl:variable name="interpretation" select="Record/Field[@id = 'interpretation']"/>
					<td align="left" style="padding: 10px 02px 0px 10px; border: 1px solid black;"><xsl:value-of select="$interpretation/Value"/></td>
				</tr>
			</table>
		</td>
	</tr>
 </xsl:template> 
 
 <xsl:template match="Field"> 
 
 </xsl:template>

</xsl:stylesheet> 