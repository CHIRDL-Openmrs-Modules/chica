<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="xml" version="1.0" encoding="ISO-8859-1" indent="no"/>
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="Field[@id='CurrentDate']">
		<xsl:element name="Field">
			<xsl:attribute name="id" select="@id"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="Field[@id='CurrentDate_SP']">
		<xsl:element name="Field">
			<xsl:attribute name="id" select="@id"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="Field[@id='CurrentTime']">
		<xsl:element name="Field">
			<xsl:attribute name="id" select="@id"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
