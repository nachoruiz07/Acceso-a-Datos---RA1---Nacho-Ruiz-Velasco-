package es.nachoruiz;

import es.nachoruiz.modelo.Vehiculo;
import es.nachoruiz.xml.EscritorXML;
import es.nachoruiz.xml.LectorXml;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class MainEscrituraXML {

    public static void main(String[] args) throws Exception {

        // Creo los objetos para leer y escribir XML
        LectorXml lector = new LectorXml();
        EscritorXML escritor = new EscritorXML();

        // Defino las rutas de los archivos
        Path original = Path.of("datos", "vehiculos.xml");

        Path carpetaSalida = Path.of("salida");

        Path conDom = carpetaSalida.resolve("vehiculos-dom.xml");
        Path conStax = carpetaSalida.resolve("vehiculos-stax.xml");
        Path antes = carpetaSalida.resolve("vehiculos-dom-antes.xml");

        // Creo la carpeta de salida si no existe
        Files.createDirectories(carpetaSalida);


        // ========================== LEER VEHÍCULOS

        System.out.println("===== VEHÍCULOS ORIGINALES =====");

        // Leo los vehículos del XML de la actividad anterior
        List<Vehiculo> vehiculos = lector.leerConDom(original);

        System.out.println("Vehículos originales: " + vehiculos.size());


        // ========================== ESCRITURA DOM Y STAX

        System.out.println("\n===== ESCRITURA CON DOM Y STAX =====");

        LocalDate fecha = LocalDate.now();

        // Creo dos XML nuevos con los mismos vehículos
        escritor.escribirConDom(conDom, fecha, vehiculos);

        escritor.escribirConStax(conStax, fecha, vehiculos);

        System.out.println("XML creado con DOM: " + conDom);
        System.out.println("XML creado con StAX: " + conStax);


        // ========================== COMPARAR RESULTADOS

        System.out.println("\n===== COMPARACIÓN DOM Y STAX =====");

        // Leo los dos XML generados
        List<Vehiculo> listaDom = lector.leerConDom(conDom);
        List<Vehiculo> listaStax = lector.leerConSax(conStax);

        System.out.println("Vehículos del XML DOM: " + listaDom.size());
        System.out.println("Vehículos del XML StAX: " + listaStax.size());

        // Compruebo que contienen los mismos datos
        System.out.println("¿Los dos XML contienen los mismos vehículos? "
                + mismosVehiculos(listaDom, listaStax));


        // ========================== GUARDAR COPIA ORIGINAL

        // Guardo una copia antes de realizar modificaciones
        Files.copy(conDom, antes, StandardCopyOption.REPLACE_EXISTING);


        // ========================== AÑADIR VEHÍCULO

        System.out.println("\n===== AÑADIR VEHÍCULO =====");

        // Creo un vehículo con los tres caracteres especiales pedidos
        Vehiculo nuevo = new Vehiculo("1357STU", "Citroen", "C3", 2023, "Revisión <Premium> & \"Completa\"");

        // Lo añado al XML existente
        escritor.anadirVehiculo(conDom, nuevo);

        System.out.println("Vehículo añadido:");
        System.out.println(nuevo);


        // ========================== MODIFICAR VEHÍCULO

        System.out.println("\n===== MODIFICAR VEHÍCULO =====");

        // Modifico el año del Ford Focus
        Vehiculo fordAntes = lector.buscarPorMatricula(antes, "9012GHI");

        System.out.println("Antes: " + fordAntes);

        boolean modificado = escritor.actualizarAnio(conDom, "9012GHI", 2019);

        Vehiculo fordDespues = lector.buscarPorMatricula(conDom, "9012GHI");

        System.out.println("Después: " + fordDespues);

        System.out.println("¿Modificado correctamente? " + modificado);


        // ========================== ELIMINAR VEHÍCULO

        System.out.println("\n===== ELIMINAR VEHÍCULO =====");

        // Elimino el Renault Clio
        boolean eliminado = escritor.eliminarVehiculo(conDom, "5678DEF");

        System.out.println("¿Renault Clio eliminado? " + eliminado);

        // Compruebo que ya no existe en el XML
        Vehiculo renault = lector.buscarPorMatricula(conDom, "5678DEF");

        System.out.println("Búsqueda después de eliminar: " + renault);


        // ========================== COMPROBAR ERRORES

        System.out.println("\n===== COMPROBACIÓN DE ERRORES =====");

        // Intento añadir un vehículo con una matrícula que ya existe
        try {

            escritor.anadirVehiculo(conDom, nuevo);

        } catch (IllegalArgumentException e) {

            System.out.println("Matrícula repetida: " + e.getMessage());

        }

        // Intento modificar un vehículo inexistente
        boolean modificarInexistente = escritor.actualizarAnio(conDom, "NOEXISTE", 2020);

        System.out.println("¿Modificar vehículo inexistente? "
                + modificarInexistente);

        // Intento eliminar un vehículo inexistente
        boolean eliminarInexistente = escritor.eliminarVehiculo(conDom, "NOEXISTE");

        System.out.println("¿Eliminar vehículo inexistente? " + eliminarInexistente);


        // ========================== COMPROBAR XML FINAL

        System.out.println("\n===== COMPROBACIÓN DEL XML FINAL =====");

        // Leo de nuevo el XML modificado
        List<Vehiculo> finales = lector.leerConDom(conDom);

        System.out.println("Vehículos finales: " + finales.size());

        // Compruebo que los caracteres especiales se recuperan bien
        Vehiculo especial = lector.buscarPorMatricula(conDom, "1357STU");

        System.out.println("Vehículo con caracteres especiales:");
        System.out.println(especial);

        System.out.println("\n===== ARCHIVOS GENERADOS =====");

        System.out.println("XML antes de modificar: " + antes);
        System.out.println("XML después de modificar: " + conDom);
        System.out.println("XML generado con StAX: " + conStax);
    }


    // ========================== COMPARAR VEHÍCULOS

    // Comprueba que dos listas contienen los mismos vehículos
    private static boolean mismosVehiculos(
            List<Vehiculo> lista1,
            List<Vehiculo> lista2) {

        if (lista1.size() != lista2.size()) {

            return false;

        }

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