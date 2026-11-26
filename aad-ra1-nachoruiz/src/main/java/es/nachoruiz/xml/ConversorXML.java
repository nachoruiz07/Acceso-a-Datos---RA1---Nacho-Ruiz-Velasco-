package es.nachoruiz.xml;

import es.nachoruiz.modelo.Vehiculo;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.List;

public class ConversorXML {

    // ========================== XML A HTML O CSV

    // Aplica una hoja XSLT al XML y guarda el resultado
    public void transformar(Path xml, Path hoja, Path destino)
            throws IOException, SAXException,
            ParserConfigurationException, TransformerException {

        // Leo el XML utilizando el analizador seguro
        Document origen = XmlSeguro.parsearDom(xml);

        // Creo el Transformer y le indico la hoja XSLT
        Transformer transformador = XmlSeguro.fabricaTransformer()
                .newTransformer(new StreamSource(hoja.toFile()));

        // Creo un fichero temporal
        Path temporal = crearTemporalJuntoA(destino);

        try {

            // Escribo el resultado de la transformación en el temporal
            try (OutputStream salida = Files.newOutputStream(temporal)) {

                transformador.transform(
                        new DOMSource(origen),
                        new StreamResult(salida)
                );

            }

            // Sustituyo el destino por el temporal terminado
            reemplazar(temporal, destino);

        } finally {

            // Elimino el temporal si todavía existe
            Files.deleteIfExists(temporal);

        }
    }


    // ========================== XML A JSON

    // Lee los vehículos del XML y genera un fichero JSON
    public void aJson(Path xml, Path destino)
            throws IOException, SAXException, ParserConfigurationException {

        // Leo los vehículos con el lector SAX de AD-1.c
        List<Vehiculo> vehiculos = new LectorXml().leerConSax(xml);

        // Construyo el contenido del JSON
        String json = construirJson(vehiculos);

        // Creo un fichero temporal
        Path temporal = crearTemporalJuntoA(destino);

        try {

            // Escribo el JSON utilizando UTF-8
            Files.writeString(temporal, json, StandardCharsets.UTF_8);

            // Sustituyo el destino por el temporal terminado
            reemplazar(temporal, destino);

        } finally {

            // Elimino el temporal si todavía existe
            Files.deleteIfExists(temporal);

        }
    }


    // ========================== CONSTRUIR JSON

    // Convierte una lista de vehículos en texto JSON
    public static String construirJson(List<Vehiculo> vehiculos) {

        StringBuilder sb = new StringBuilder();

        // Abro el objeto JSON y la lista de vehículos
        sb.append("{\n  \"vehiculos\": [\n");

        // Recorro los vehículos
        for (int i = 0; i < vehiculos.size(); i++) {

            Vehiculo v = vehiculos.get(i);

            // Construyo cada objeto JSON con sus campos
            sb.append("    {")
                    .append("\"matricula\": \"")
                    .append(escaparJson(v.getMatricula()))
                    .append("\", ")

                    .append("\"marca\": \"")
                    .append(escaparJson(v.getMarca()))
                    .append("\", ")

                    .append("\"modelo\": \"")
                    .append(escaparJson(v.getModelo()))
                    .append("\", ")

                    .append("\"anio\": ")
                    .append(v.getAnio())
                    .append(", ")

                    .append("\"estado\": \"")
                    .append(escaparJson(v.getEstado()))
                    .append("\"")

                    .append("}");

            // Añado una coma entre vehículos, excepto después del último
            sb.append(i < vehiculos.size() - 1 ? ",\n" : "\n");
        }

        // Cierro la lista y el objeto JSON
        sb.append("  ]\n}\n");

        return sb.toString();
    }


    // ========================== ESCAPAR JSON

    // Escapa caracteres especiales para que el JSON sea válido
    public static String escaparJson(String texto) {

        StringBuilder sb = new StringBuilder(texto.length() + 8);

        for (char c : texto.toCharArray()) {

            switch (c) {

                case '"' -> sb.append("\\\"");

                case '\\' -> sb.append("\\\\");

                case '\n' -> sb.append("\\n");

                case '\r' -> sb.append("\\r");

                case '\t' -> sb.append("\\t");

                case '\b' -> sb.append("\\b");

                case '\f' -> sb.append("\\f");

                default -> {

                    if (c < 0x20) {

                        sb.append(String.format("\\u%04x", (int) c));

                    } else {

                        sb.append(c);

                    }
                }
            }
        }

        return sb.toString();
    }


    // ========================== FICHERO TEMPORAL

    // Crea un temporal en la misma carpeta que el destino
    private Path crearTemporalJuntoA(Path destino) throws IOException {

        Path padre = destino.toAbsolutePath().getParent();

        Files.createDirectories(padre);

        return Files.createTempFile(padre, "tmp-", ".part");
    }


    // ========================== REEMPLAZAR FICHERO

    // Sustituye el destino por el fichero temporal
    private void reemplazar(Path temporal, Path destino) throws IOException {

        try {

            Files.move(
                    temporal,
                    destino,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (AtomicMoveNotSupportedException e) {

            Files.move(
                    temporal,
                    destino,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
}