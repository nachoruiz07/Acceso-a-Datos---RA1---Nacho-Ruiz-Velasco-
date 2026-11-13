package es.nachoruiz;

import es.nachoruiz.ficheros.GestorFicheros;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {

        // En este MAIN voy a probar los métodos implementados en la clase GestorFicheros

        //Creo el gestor:
        GestorFicheros gestor = new GestorFicheros();

        //Defino las rutas que me van a hacer falta utilizar:
        Path taller = Path.of("Taller");
        Path reparaciones = Path.of("Taller", "Reparaciones", "2026");
        Path informes = Path.of("Taller", "Informes");

        //Dejo fuera este fichero ya que es el que trabajo manualmente y me va a sevir para ejecutar los métodos
        // El último paso es borrarlo todo, entonces no quiero que este en concreto se borre
        Path ficheroOriginal = Path.of("datos", "vehiculos.txt");
        Path notas = Path.of("Taller", "notas.txt");

        Path ficheroCopiado = Path.of("Taller", "Informes", "vehiculos.txt");
        Path ficheroMovido = Path.of("Taller", "Reparaciones", "2026", "vehiculos.txt");

        try {
            // Creo los directorios

            gestor.crearDirectorio(taller);

            gestor.crearDirectorio(reparaciones);

            gestor.crearDirectorio(informes);

            // Creo un fichero

            gestor.crearFichero(notas);

            System.out.println("Fichero creado: " + notas);

            // Copio un fichero

            //No quiero que sobreescriba nada, así que lo dejo en false
            gestor.copiar(ficheroOriginal, ficheroCopiado, false);

            System.out.println("Fichero copiado: " + ficheroCopiado);

            // Mover fichero

            gestor.mover(ficheroCopiado, ficheroMovido, false);

            System.out.println("Archivo movido: " + ficheroMovido);

            // Listar directorio

            //El método listar devuelve una lista, por lo que con un for each puedo recorrer el contenido uno por uno
            //y mostrarlo
            for (Path ruta : gestor.listar(taller, "*")) {
                System.out.println(ruta);
            }

            // Buscar un archivo (txt)

            for (Path ruta : gestor.buscarRecursivo(taller, "*.txt")) {

                System.out.println(ruta);

            }


            // Describir un fichero

            System.out.println(gestor.describir(ficheroMovido));


            // Comprobar una ruta segura

            Path rutaSegura = gestor.resolverSeguro(taller, "Reparaciones/2026/vehiculos.txt");

            System.out.println("Ruta permitida: " + rutaSegura);


            // Intentamos acceder fuera de la carpeta permitida, para comprobar que funciona bien una excepción
            try {

                gestor.resolverSeguro(taller, "../../secreto.txt");

            } catch (IllegalArgumentException e) {

                System.out.println("Ruta rechazada: " + e.getMessage());

            }


            // Creo un scanner, para que luego de ejecutar todos los métodos, excluyendo eliminar, pueda hacer captura
            // de los resultados antes del borrado

            System.out.println("Pulsa ENTER para continuar");

            Scanner sc = new Scanner(System.in);
            sc.nextLine();


            //Eliminar un fichero

            gestor.eliminar(notas);

            System.out.println("Fichero eliminado: " + notas);


            //Eliminar un directorio de manera recursiva

            gestor.eliminarRecursivo(taller);

            System.out.println("Carpeta Taller eliminada correctamente");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}