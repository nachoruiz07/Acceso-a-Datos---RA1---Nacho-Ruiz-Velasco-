package es.nachoruiz.xml;

import es.nachoruiz.modelo.Vehiculo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LectorXml {


    // ========================== DOM

    // Lee el XML con DOM y devuelve una lista de vehículos
    public List<Vehiculo> leerConDom(Path fichero)
            throws IOException, SAXException, ParserConfigurationException {

        Document doc = XmlSeguro.parsearDom(fichero);

        NodeList nodos = doc.getElementsByTagName("vehiculo");

        List<Vehiculo> vehiculos = new ArrayList<>();

        for (int i = 0; i < nodos.getLength(); i++) {

            vehiculos.add(aVehiculo((Element) nodos.item(i)));

        }

        return vehiculos;
    }


    // Convierte un elemento <vehiculo> del XML en un objeto Vehiculo
    static Vehiculo aVehiculo(Element e) {

        return new Vehiculo(
                e.getAttribute("matricula").trim(),
                texto(e, "marca"),
                texto(e, "modelo"),
                Integer.parseInt(texto(e, "anio")),
                texto(e, "estado")
        );
    }


    // Obtiene el texto de un elemento hijo del XML
    private static String texto(Element padre, String hijo) {

        NodeList lista = padre.getElementsByTagName(hijo);

        if (lista.getLength() == 0) {

            throw new IllegalArgumentException(
                    "Falta el elemento <" + hijo + "> en un vehículo"
            );
        }

        Node nodo = lista.item(0);

        return nodo.getTextContent().trim();
    }



    // ========================== SAX

    // Lee el XML con SAX y devuelve una lista de vehículos
    public List<Vehiculo> leerConSax(Path fichero)
            throws IOException, SAXException, ParserConfigurationException {

        SAXParser analizador = XmlSeguro.fabricaSax().newSAXParser();

        ManejadorVehiculos manejador = new ManejadorVehiculos();

        try (InputStream entrada = Files.newInputStream(fichero)) {

            analizador.parse(entrada, manejador);

        }

        return manejador.vehiculos;
    }


    // Recibe los eventos SAX y construye los objetos Vehiculo
    private static final class ManejadorVehiculos extends DefaultHandler {

        private final List<Vehiculo> vehiculos = new ArrayList<>();

        private final StringBuilder texto = new StringBuilder();

        private String matricula;
        private String marca;
        private String modelo;
        private int anio;
        private String estado;


        // Se ejecuta cuando SAX encuentra una etiqueta de apertura
        @Override
        public void startElement(
                String uri,
                String localName,
                String qName,
                Attributes atributos) {

            texto.setLength(0);

            if (qName.equals("vehiculo")) {

                matricula = atributos.getValue("matricula").trim();

                marca = null;
                modelo = null;
                anio = 0;
                estado = null;
            }
        }


        // Recoge el texto que hay dentro de las etiquetas
        @Override
        public void characters(char[] ch, int inicio, int longitud) {

            texto.append(ch, inicio, longitud);

        }


        // Se ejecuta cuando SAX encuentra una etiqueta de cierre
        @Override
        public void endElement(String uri, String localName, String qName) {

            String contenido = texto.toString().trim();

            switch (qName) {

                case "marca" -> marca = contenido;

                case "modelo" -> modelo = contenido;

                case "anio" -> anio = Integer.parseInt(contenido);

                case "estado" -> estado = contenido;

                case "vehiculo" -> vehiculos.add(
                        new Vehiculo(matricula, marca, modelo, anio, estado)
                );

                default -> {
                    // Las demás etiquetas se ignoran
                }
            }
        }
    }



    // ========================== XPATH

    // Busca un vehículo por el atributo matrícula
    public Vehiculo buscarPorMatricula(Path fichero, String matricula)
            throws IOException, SAXException, ParserConfigurationException,
            XPathExpressionException, XPathFactoryConfigurationException {

        Document doc = XmlSeguro.parsearDom(fichero);

        XPath xpath = XmlSeguro.fabricaXPath().newXPath();

        // Paso la matrícula como una variable de XPath
        xpath.setXPathVariableResolver(
                nombre -> "matricula".equals(nombre.getLocalPart()) ? matricula : null
        );

        NodeList nodos = (NodeList) xpath.evaluate(
                "/taller/vehiculo[@matricula = $matricula]",
                doc,
                XPathConstants.NODESET
        );

        if (nodos.getLength() == 0) {

            return null;

        }

        return aVehiculo((Element) nodos.item(0));
    }


    // Busca los vehículos cuyo año sea mayor que el indicado
    public List<Vehiculo> filtrarPorAnioMinimo(Path fichero, int minimo)
            throws IOException, SAXException, ParserConfigurationException,
            XPathExpressionException, XPathFactoryConfigurationException {

        Document doc = XmlSeguro.parsearDom(fichero);

        XPath xpath = XmlSeguro.fabricaXPath().newXPath();

        // Paso el año mínimo como una variable de XPath
        xpath.setXPathVariableResolver(
                nombre -> "min".equals(nombre.getLocalPart()) ? minimo : null
        );

        NodeList nodos = (NodeList) xpath.evaluate(
                "/taller/vehiculo[number(anio) > $min]",
                doc,
                XPathConstants.NODESET
        );

        List<Vehiculo> vehiculos = new ArrayList<>();

        for (int i = 0; i < nodos.getLength(); i++) {

            vehiculos.add(aVehiculo((Element) nodos.item(i)));

        }

        return vehiculos;
    }
}