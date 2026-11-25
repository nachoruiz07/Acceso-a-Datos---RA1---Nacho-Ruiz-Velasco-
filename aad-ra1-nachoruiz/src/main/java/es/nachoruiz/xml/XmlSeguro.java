package es.nachoruiz.xml;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class XmlSeguro {

    // No necesitamos crear objetos de esta clase
    private XmlSeguro() {
    }

    // ========================== DOM SEGURO

    // Prepara un analizador DOM seguro
    public static DocumentBuilderFactory fabricaDom()
            throws ParserConfigurationException {

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

        f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        f.setXIncludeAware(false);

        f.setExpandEntityReferences(false);

        return f;
    }

    // ========================== SAX SEGURO

    // Prepara un analizador SAX seguro
    public static SAXParserFactory fabricaSax()
            throws ParserConfigurationException, SAXException {

        SAXParserFactory f = SAXParserFactory.newInstance();

        f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        return f;
    }

    // ========================== STAX SEGURO

    // Prepara un analizador StAX para leer XML de forma segura
    public static XMLInputFactory fabricaStax() {

        XMLInputFactory f = XMLInputFactory.newFactory();

        f.setProperty(XMLInputFactory.SUPPORT_DTD, false);

        f.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        return f;
    }

    // ========================== TRANSFORMER SEGURO

    // Prepara la herramienta que permite guardar documentos DOM
    public static TransformerFactory fabricaTransformer()
            throws TransformerConfigurationException {

        TransformerFactory f = TransformerFactory.newInstance();

        f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        f.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");

        f.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        return f;
    }

    // ========================== XPATH SEGURO

    // Prepara las consultas XPath de forma segura
    public static XPathFactory fabricaXPath()
            throws XPathFactoryConfigurationException {

        XPathFactory f = XPathFactory.newInstance();

        f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        return f;
    }

    // ========================== LEER XML CON DOM

    // Abre un XML y devuelve su contenido como un documento DOM
    public static Document parsearDom(Path fichero)
            throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilder constructor = fabricaDom().newDocumentBuilder();

        constructor.setErrorHandler(new DefaultHandler());

        try (InputStream entrada = Files.newInputStream(fichero)) {

            return constructor.parse(entrada);

        }
    }
}