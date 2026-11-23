package es.nachoruiz;

import es.nachoruiz.modelo.Vehiculo;
import es.nachoruiz.xml.LectorXml;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class MainXML {

    public static void main(String[] args) throws Exception {

        // En este Main voy a probar la lectura de XML con DOM y SAX,
        // además de dos consultas XPath.

        // Creo el lector que contiene los métodos para trabajar con XML.
        LectorXml lector = new LectorXml();

        // Defino la ruta del fichero XML de los vehículos.
        Path fichero = Path.of("datos", "vehiculos.xml");


        // ========================== LECTURA CON DOM

        System.out.println("===== LECTURA CON DOM =====");

        // Leo los vehículos del XML utilizando DOM.
        List<Vehiculo> dom = lector.leerConDom(fichero);

        // Muestro cuántos vehículos se han leído.
        System.out.println("Vehículos leídos con DOM: " + dom.size());

        // Recorro la lista para mostrar los vehículos.
        for (Vehiculo vehiculo : dom) {

            System.out.println(vehiculo);

        }


        // ========================== LECTURA CON SAX

        System.out.println("\n===== LECTURA CON SAX =====");

        // Leo el mismo fichero XML utilizando SAX.
        List<Vehiculo> sax = lector.leerConSax(fichero);

        System.out.println("Vehículos leídos con SAX: " + sax.size());

        for (Vehiculo vehiculo : sax) {

            System.out.println(vehiculo);

        }


        // ========================== COMPARAR RESULTADOS

        System.out.println("\n===== COMPARACIÓN DOM Y SAX =====");

        // Compruebo que ambas técnicas devuelven los mismos vehículos.
        System.out.println("¿DOM y SAX devuelven lo mismo? "
                + mismosVehiculos(dom, sax));


        // ========================== XPATH POR ATRIBUTO

        System.out.println("\n===== XPATH POR MATRÍCULA =====");

        // Busco un vehículo concreto mediante el atributo matrícula.
        Vehiculo encontrado = lector.buscarPorMatricula(fichero, "9012GHI");

        System.out.println("Vehículo con matrícula 9012GHI:");

        System.out.println(encontrado);


        // ========================== XPATH CON CONDICIÓN NUMÉRICA

        System.out.println("\n===== XPATH POR AÑO =====");

        // Busco los vehículos cuyo año sea mayor que 2019.
        int anioMinimo = 2019;

        List<Vehiculo> posteriores = lector.filtrarPorAnioMinimo(fichero, anioMinimo);

        System.out.println("Vehículos posteriores a " + anioMinimo + ":");

        for (Vehiculo vehiculo : posteriores) {

            System.out.println(vehiculo);

        }

    }


    // ========================== MÉTODO AUXILIAR

    // Comprueba que las dos listas contienen los mismos vehículos
    // y que sus atributos coinciden.
    private static boolean mismosVehiculos(List<Vehiculo> lista1, List<Vehiculo> lista2) {

        // Si las listas tienen distinto tamaño, no son iguales.
        if (lista1.size() != lista2.size()) {

            return false;

        }

        // Recorro ambas listas y comparo los datos de cada vehículo.
        for (int i = 0; i < lista1.size(); i++) {

            Vehiculo v1 = lista1.get(i);
            Vehiculo v2 = lista2.get(i);

            if (!Objects.equals(v1.getMatricula(), v2.getMatricula())
                    || !Objects.equals(v1.getMarca(), v2.getMarca())
                    || !Objects.equals(v1.getModelo(), v2.getModelo())
                    || v1.getAnio() != v2.getAnio()
                    || !Objects.equals(v1.getEstado(), v2.getEstado())) {

                return false;

            }
        }

        return true;
    }
}