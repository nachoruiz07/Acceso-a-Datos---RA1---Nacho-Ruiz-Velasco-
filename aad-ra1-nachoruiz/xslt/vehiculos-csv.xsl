<?xml version="1.0" encoding="UTF-8"?>

<!-- Convierte el XML de vehículos en un CSV separado por ; -->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- El resultado será texto plano -->
    <xsl:output method="text" encoding="UTF-8"/>

    <!-- Plantilla para el elemento principal <taller> -->
    <xsl:template match="/taller">

        <!-- Escribo la cabecera del CSV -->
        <xsl:text>MATRICULA;MARCA;MODELO;AÑO;ESTADO&#10;</xsl:text>

        <!-- Recorro los vehículos del XML -->
        <xsl:for-each select="vehiculo">

            <!-- Matrícula -->
            <xsl:value-of select="@matricula"/>
            <xsl:text>;</xsl:text>

            <!-- Marca -->
            <xsl:value-of select="normalize-space(marca)"/>
            <xsl:text>;</xsl:text>

            <!-- Modelo -->
            <xsl:value-of select="normalize-space(modelo)"/>
            <xsl:text>;</xsl:text>

            <!-- Año -->
            <xsl:value-of select="anio"/>
            <xsl:text>;</xsl:text>

            <!-- Estado -->
            <xsl:value-of select="normalize-space(estado)"/>

            <!-- Salto de línea -->
            <xsl:text>&#10;</xsl:text>

        </xsl:for-each>

    </xsl:template>

</xsl:stylesheet>