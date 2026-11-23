package es.nachoruiz.xml;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class XmlSeguro {

    // Constructor privado porque no necesito crear objetos de esta clase
    private XmlSeguro() {
    }


    // ========================== DOM SEGURO

    // Crea una fábrica DOM con la configuración de seguridad
    public static DocumentBuilderFactory fabricaDom() throws ParserConfigurationException {

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

        f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        f.setXIncludeAware(false);

        f.setExpandEntityReferences(false);

        return f;
    }


    // ========================== SAX SEGURO

    // Crea una fábrica SAX con la configuración de seguridad
    public static SAXParserFactory fabricaSax()
            throws ParserConfigurationException, SAXException {

        SAXParserFactory f = SAXParserFactory.newInstance();

        f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        return f;
    }


    // ========================== XPATH SEGURO

    // Crea una fábrica XPath con procesamiento seguro
    public static XPathFactory fabricaXPath()
            throws XPathFactoryConfigurationException {

        XPathFactory f = XPathFactory.newInstance();

        f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        return f;
    }


    // ========================== LEER XML CON DOM

    // Abre un fichero XML y lo convierte en un documento DOM
    public static Document parsearDom(Path fichero)
            throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilder constructor = fabricaDom().newDocumentBuilder();

        constructor.setErrorHandler(new DefaultHandler());

        try (InputStream entrada = Files.newInputStream(fichero)) {

            return constructor.parse(entrada);

        }
    }
}