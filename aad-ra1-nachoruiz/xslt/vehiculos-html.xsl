<?xml version="1.0" encoding="UTF-8"?>

<!-- Convierte el XML de vehículos en una página HTML -->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- Indico que el resultado será HTML -->
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <!-- Plantilla que se aplica al elemento principal <taller> -->
    <xsl:template match="/taller">

        <html>

            <head>
                <meta charset="UTF-8"/>
                <title>Vehículos del taller</title>

                <!-- Estilos de la página -->
                <style>
                    body {
                    font-family: sans-serif;
                    margin: 2em;
                    }

                    table {
                    border-collapse: collapse;
                    }

                    th, td {
                    border: 1px solid #999;
                    padding: 4px 10px;
                    text-align: left;
                    }

                    th {
                    background: #1F3864;
                    color: white;
                    }

                    .pendiente {
                    color: #b00020;
                    font-weight: bold;
                    }
                </style>

            </head>

            <body>

                <!-- Título con la fecha del XML -->
                <h1>
                    Vehículos del taller a fecha
                    <xsl:value-of select="@fecha"/>
                </h1>

                <!-- Número total de vehículos -->
                <p>
                    <xsl:value-of select="count(vehiculo)"/>
                    vehículos.
                </p>

                <!-- Tabla de vehículos -->
                <table>

                    <tr>
                        <th>Matrícula</th>
                        <th>Marca</th>
                        <th>Modelo</th>
                        <th>Año</th>
                        <th>Estado</th>
                    </tr>

                    <!-- Recorro los vehículos y los ordeno por marca -->
                    <xsl:for-each select="vehiculo">

                        <xsl:sort select="marca"/>

                        <tr>

                            <td>
                                <xsl:value-of select="@matricula"/>
                            </td>

                            <td>
                                <xsl:value-of select="marca"/>
                            </td>

                            <td>
                                <xsl:value-of select="modelo"/>
                            </td>

                            <td>
                                <xsl:value-of select="anio"/>
                            </td>

                            <td>

                                <!-- Resalto los vehículos que necesitan atención -->
                                <xsl:if test="estado = 'Pendiente'
                                              or estado = 'En reparación'
                                              or estado = 'En revisión'">

                                    <xsl:attribute name="class">
                                        pendiente
                                    </xsl:attribute>

                                </xsl:if>

                                <xsl:value-of select="estado"/>

                            </td>

                        </tr>

                    </xsl:for-each>

                </table>

            </body>

        </html>

    </xsl:template>

</xsl:stylesheet>