package es.nachoruiz;

import es.nachoruiz.xml.ConversorXML;
import es.nachoruiz.xml.LectorXml;

import java.nio.file.Files;
import java.nio.file.Path;

public class MainConversionXML {

    public static void main(String[] args) throws Exception {

        // Creo el conversor y el lector de XML
        ConversorXML conversor = new ConversorXML();
        LectorXml lector = new LectorXml();

        // Defino el XML que quiero convertir
        Path xml = Path.of("datos", "vehiculos.xml");

        // Defino las dos hojas XSLT
        Path hojaHtml = Path.of("xslt", "vehiculos-html.xsl");
        Path hojaCsv = Path.of("xslt", "vehiculos-csv.xsl");

        // Defino los archivos de salida
        Path salida = Path.of("salida");

        Path html = salida.resolve("vehiculos.html");
        Path csv = salida.resolve("vehiculos.csv");
        Path json = salida.resolve("vehiculos.json");

        // Creo la carpeta de salida si no existe
        Files.createDirectories(salida);


        // ========================== XML ORIGINAL

        System.out.println("===== XML ORIGINAL =====");

        // Compruebo cuántos vehículos hay en el XML
        System.out.println("Vehículos originales: "
                + lector.leerConDom(xml).size());


        // ========================== XML A HTML

        System.out.println("\n===== XML A HTML =====");

        // Aplico la hoja XSLT que genera una página HTML
        conversor.transformar(xml, hojaHtml, html);

        System.out.println("HTML generado: " + html);


        // ========================== XML A CSV

        System.out.println("\n===== XML A CSV =====");

        // Aplico la hoja XSLT que genera un archivo CSV
        conversor.transformar(xml, hojaCsv, csv);

        System.out.println("CSV generado: " + csv);


        // ========================== XML A JSON

        System.out.println("\n===== XML A JSON =====");

        // Genero el JSON utilizando Java
        conversor.aJson(xml, json);

        System.out.println("JSON generado: " + json);


        // ========================== COMPROBAR RESULTADOS

        System.out.println("\n===== COMPROBACIÓN DE ARCHIVOS =====");

        // Compruebo que se han creado los tres archivos
        System.out.println("¿Existe el HTML? " + Files.exists(html));

        System.out.println("¿Existe el CSV? " + Files.exists(csv));

        System.out.println("¿Existe el JSON? " + Files.exists(json));


        // ========================== MOSTRAR CSV

        System.out.println("\n===== CONTENIDO DEL CSV =====");

        // Muestro por consola el CSV generado
        System.out.println(Files.readString(csv));


        // ========================== MOSTRAR JSON

        System.out.println("\n===== CONTENIDO DEL JSON =====");

        // Muestro por consola el JSON generado
        System.out.println(Files.readString(json));


        // ========================== COMPROBAR CARACTERES

        System.out.println("\n===== COMPROBACIÓN DE CARACTERES ESPECIALES =====");

        // Pruebo que las comillas se escapan correctamente en JSON
        String prueba = "Revisión \"Premium\" & limpieza";

        System.out.println("Texto original: " + prueba);

        System.out.println("Texto escapado para JSON: "
                + ConversorXML.escaparJson(prueba));
    }
}