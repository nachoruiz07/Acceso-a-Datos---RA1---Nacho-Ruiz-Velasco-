package es.nachoruiz.xml;

import es.nachoruiz.modelo.Vehiculo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.IOException;
import java.io.OutputStream;

import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.time.LocalDate;
import java.util.List;

public class EscritorXML {

    // ========================== ESCRIBIR CON DOM

    // Crea un XML nuevo a partir de una lista de vehículos
    public void escribirConDom(Path destino, LocalDate fecha, List<Vehiculo> vehiculos)
            throws IOException, ParserConfigurationException, TransformerException {

        // Creo un documento DOM vacío
        Document doc = XmlSeguro.fabricaDom().newDocumentBuilder().newDocument();

        // Creo el elemento principal <taller>
        Element raiz = doc.createElement("taller");

        // Añado el atributo fecha
        raiz.setAttribute("fecha", fecha.toString());

        doc.appendChild(raiz);

        // Convierto cada vehículo en un elemento XML
        for (Vehiculo v : vehiculos) {

            raiz.appendChild(crearElemento(doc, v));

        }

        // Guardo el XML en el fichero
        guardar(doc, destino);
    }


    // ========================== AÑADIR VEHÍCULO

    // Añade un vehículo a un XML existente
    public void anadirVehiculo(Path fichero, Vehiculo nuevo)
            throws IOException, SAXException,
            ParserConfigurationException, TransformerException {

        // Leo el XML existente con DOM
        Document doc = XmlSeguro.parsearDom(fichero);

        // Compruebo que la matrícula no esté repetida
        if (buscar(doc, nuevo.getMatricula()) != null) {

            throw new IllegalArgumentException(
                    "Ya existe un vehículo con matrícula " + nuevo.getMatricula()
            );
        }

        // Añado el nuevo vehículo al documento
        doc.getDocumentElement().appendChild(crearElemento(doc, nuevo));

        // Guardo los cambios
        guardar(doc, fichero);
    }


    // ========================== MODIFICAR VEHÍCULO

    // Cambia el año de un vehículo buscando por su matrícula
    public boolean actualizarAnio(Path fichero, String matricula, int nuevoAnio)
            throws IOException, SAXException,
            ParserConfigurationException, TransformerException {

        // Compruebo que el año sea válido
        if (nuevoAnio <= 0) {

            throw new IllegalArgumentException("El año debe ser positivo");

        }

        // Leo el XML
        Document doc = XmlSeguro.parsearDom(fichero);

        // Busco el vehículo que quiero modificar
        Element vehiculo = buscar(doc, matricula);

        // Si no existe, no modifico nada
        if (vehiculo == null) {

            return false;

        }

        // Cambio el contenido de <anio>
        vehiculo.getElementsByTagName("anio")
                .item(0)
                .setTextContent(Integer.toString(nuevoAnio));

        // Guardo el XML modificado
        guardar(doc, fichero);

        return true;
    }


    // ========================== ELIMINAR VEHÍCULO

    // Elimina un vehículo buscando por su matrícula
    public boolean eliminarVehiculo(Path fichero, String matricula)
            throws IOException, SAXException,
            ParserConfigurationException, TransformerException {

        // Leo el XML
        Document doc = XmlSeguro.parsearDom(fichero);

        // Busco el vehículo que quiero eliminar
        Element vehiculo = buscar(doc, matricula);

        // Si no existe, devuelvo false
        if (vehiculo == null) {

            return false;

        }

        // Elimino el vehículo del documento
        vehiculo.getParentNode().removeChild(vehiculo);

        // Guardo los cambios
        guardar(doc, fichero);

        return true;
    }


    // ========================== BUSCAR VEHÍCULO

    // Busca un vehículo por matrícula y comprueba que no esté repetido
    private Element buscar(Document doc, String matricula) {

        NodeList nodos = doc.getElementsByTagName("vehiculo");

        Element encontrado = null;

        for (int i = 0; i < nodos.getLength(); i++) {

            Element e = (Element) nodos.item(i);

            if (e.getAttribute("matricula").trim().equals(matricula)) {

                // Si ya se había encontrado otro, la matrícula está repetida
                if (encontrado != null) {

                    throw new IllegalArgumentException(
                            "Matrícula repetida en el XML: " + matricula
                    );
                }

                encontrado = e;
            }
        }

        return encontrado;
    }


    // ========================== CREAR ELEMENTO

    // Convierte un objeto Vehiculo en un elemento XML
    private Element crearElemento(Document doc, Vehiculo v) {

        Element e = doc.createElement("vehiculo");

        // La matrícula será un atributo
        e.setAttribute("matricula", v.getMatricula());

        // Añado los cuatro elementos hijos
        e.appendChild(campo(doc, "marca", v.getMarca()));

        e.appendChild(campo(doc, "modelo", v.getModelo()));

        e.appendChild(campo(doc, "anio", Integer.toString(v.getAnio())));

        e.appendChild(campo(doc, "estado", v.getEstado()));

        return e;
    }


    // ========================== CAMPO DOM

    // Crea una etiqueta XML con su texto
    private Element campo(Document doc, String nombre, String valor) {

        Element e = doc.createElement(nombre);

        // DOM guardará correctamente los caracteres especiales
        e.setTextContent(valor);

        return e;
    }


    // ========================== GUARDAR DOM

    // Guarda un documento DOM utilizando un fichero temporal
    private void guardar(Document doc, Path destino)
            throws IOException, TransformerException {

        // Elimino espacios de formato que no hacen falta
        quitarEspaciosVacios(doc.getDocumentElement());

        // Preparo la herramienta para guardar el documento
        Transformer t = XmlSeguro.fabricaTransformer().newTransformer();

        // Configuro la salida del XML
        t.setOutputProperty(OutputKeys.INDENT, "yes");

        t.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4"
        );

        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        // Creo un fichero temporal
        Path temporal = crearTemporalJuntoA(destino);

        try {

            // Escribo el XML en el temporal
            try (OutputStream salida = Files.newOutputStream(temporal)) {

                t.transform(new DOMSource(doc), new StreamResult(salida));

            }

            // Sustituyo el destino por el temporal terminado
            reemplazar(temporal, destino);

        } finally {

            // Borro el temporal si todavía existe
            Files.deleteIfExists(temporal);

        }
    }


    // ========================== QUITAR ESPACIOS

    // Elimina espacios vacíos para evitar líneas en blanco al guardar
    private void quitarEspaciosVacios(Node nodo) {

        NodeList hijos = nodo.getChildNodes();

        for (int i = hijos.getLength() - 1; i >= 0; i--) {

            Node hijo = hijos.item(i);

            if (hijo.getNodeType() == Node.TEXT_NODE
                    && hijo.getTextContent().isBlank()) {

                nodo.removeChild(hijo);

            } else if (hijo.getNodeType() == Node.ELEMENT_NODE) {

                quitarEspaciosVacios(hijo);

            }
        }
    }


    // ========================== ESCRIBIR CON STAX

    // Crea un XML nuevo escribiendo las etiquetas una a una
    public void escribirConStax(Path destino, LocalDate fecha, List<Vehiculo> vehiculos)
            throws IOException, XMLStreamException {

        // Creo un fichero temporal
        Path temporal = crearTemporalJuntoA(destino);

        try {

            try (OutputStream salida = Files.newOutputStream(temporal)) {

                // Creo el escritor StAX
                XMLStreamWriter w = XMLOutputFactory.newFactory()
                        .createXMLStreamWriter(salida, "UTF-8");

                try {

                    // Inicio el documento XML
                    w.writeStartDocument("UTF-8", "1.0");

                    w.writeCharacters("\n");

                    // Abro la etiqueta principal <taller>
                    w.writeStartElement("taller");

                    w.writeAttribute("fecha", fecha.toString());

                    // Recorro todos los vehículos
                    for (Vehiculo v : vehiculos) {

                        w.writeCharacters("\n    ");

                        // Abro la etiqueta <vehiculo>
                        w.writeStartElement("vehiculo");

                        // Escribo la matrícula como atributo
                        w.writeAttribute("matricula", v.getMatricula());

                        // Escribo los elementos hijos
                        campo(w, "marca", v.getMarca());

                        campo(w, "modelo", v.getModelo());

                        campo(w, "anio", Integer.toString(v.getAnio()));

                        campo(w, "estado", v.getEstado());

                        w.writeCharacters("\n    ");

                        // Cierro </vehiculo>
                        w.writeEndElement();
                    }

                    w.writeCharacters("\n");

                    // Cierro </taller>
                    w.writeEndElement();

                    // Termino el documento XML
                    w.writeEndDocument();

                    w.flush();

                } finally {

                    w.close();

                }
            }

            // Sustituyo el destino por el temporal
            reemplazar(temporal, destino);

        } finally {

            Files.deleteIfExists(temporal);

        }
    }


    // ========================== CAMPO STAX

    // Escribe una etiqueta y su contenido utilizando StAX
    private void campo(XMLStreamWriter w, String nombre, String valor)
            throws XMLStreamException {

        w.writeCharacters("\n        ");

        w.writeStartElement(nombre);

        // StAX escapa los caracteres especiales
        w.writeCharacters(valor);

        w.writeEndElement();
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