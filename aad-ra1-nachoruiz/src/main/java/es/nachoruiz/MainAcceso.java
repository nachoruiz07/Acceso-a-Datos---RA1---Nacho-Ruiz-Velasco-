package es.nachoruiz;

import es.nachoruiz.ficheros.AccesoFicheros;
import es.nachoruiz.ficheros.BenchmarkLectura;
import es.nachoruiz.modelo.Vehiculo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MainAcceso {

    public static void main(String[] args) {

        // En este Main voy a probar las distintas formas de acceso a ficheros
        // que he implementado en la clase AccesoFicheros.

        // Creo el objeto que me permite utilizar los métodos de AccesoFicheros.
        AccesoFicheros acceso = new AccesoFicheros();

        // Defino las rutas de los ficheros que voy a utilizar.
        // El CSV original está en datos y no lo voy a modificar.
        Path csv = Path.of("datos", "vehiculos.txt");

        // Los archivos que genera el programa se guardarán en salida.
        Path salida = Path.of("salida");
        Path binario = salida.resolve("vehiculos.dat");
        Path texto = salida.resolve("registro.txt");

        try {

            // Creo el directorio salida si todavía no existe.
            Files.createDirectories(salida);


            // ========================== TEXTO SECUENCIAL

            // Leo el CSV y convierto cada fila en un objeto Vehiculo.
            System.out.println("===== LECTURA DEL CSV =====");

            List<Vehiculo> vehiculos = acceso.leerCsv(csv);

            // Recorro la lista para mostrar los vehículos que he leído.
            for (Vehiculo vehiculo : vehiculos) {

                System.out.println(vehiculo);

            }

            // Compruebo cuántas líneas contiene el fichero original.
            System.out.println("Número de líneas del CSV: " + acceso.contarLineas(csv));


            // ========================== ESCRITURA DE TEXTO

            System.out.println("\n===== ESCRITURA DE TEXTO =====");

            // Escribo una primera línea en registro.txt.
            // Utilizo false porque quiero sustituir el contenido anterior.
            acceso.escribirLineas(texto, List.of("Primera ejecución"), false);

            // Añado otra línea al final del fichero.
            // Utilizo true para no borrar la línea anterior.
            acceso.escribirLineas(texto, List.of("Segunda ejecución"), true);

            // Leo el fichero para comprobar que contiene las dos líneas.
            System.out.println(acceso.leerLineasEnMemoria(texto));


            // ========================== BINARIO SECUENCIAL

            System.out.println("\n===== BINARIO SECUENCIAL =====");

            // Guardo los objetos Vehiculo en un fichero binario.
            // Cada registro ocupa siempre el mismo número de bytes.
            acceso.escribirBinario(binario, vehiculos);

            // Muestro cuántos registros se han escrito y el tamaño del fichero.
            System.out.println("Registros escritos: " + vehiculos.size());

            System.out.println("Tamaño de cada registro: "
                    + AccesoFicheros.TAM_REGISTRO + " bytes");

            System.out.println("Tamaño total del fichero: "
                    + Files.size(binario) + " bytes");

            // Vuelvo a leer el binario para comprobar que se han guardado los vehículos.
            List<Vehiculo> vehiculosBinario = acceso.leerBinario(binario);

            System.out.println("Registros leídos del binario: " + vehiculosBinario.size());


            // ========================== BINARIO ALEATORIO

            System.out.println("\n===== ACCESO ALEATORIO =====");

            // Voy a trabajar con el vehículo de índice 2.
            // Como los índices empiezan en 0, corresponde al tercer vehículo.
            int indice = 2;

            // Leo directamente ese registro y muestro sus datos originales.
            System.out.println("Vehículo antes de modificarlo:");
            System.out.println(acceso.leerRegistro(binario, indice));

            // Modifico únicamente el año de ese registro.
            // RandomAccessFile permite hacerlo sin reescribir todos los vehículos.
            acceso.actualizarAnio(binario, indice, 2019);

            // Vuelvo a leer el mismo registro para comprobar que se ha modificado.
            System.out.println("Vehículo después de modificarlo:");
            System.out.println(acceso.leerRegistro(binario, indice));

            // Compruebo cuántos registros contiene el fichero binario.
            System.out.println("Total de registros: " + acceso.contarRegistros(binario));


            // ========================== BENCHMARK

            System.out.println("\n===== COMPARACIÓN DE VELOCIDADES =====");

            // Ejecuto el benchmark de la profesora con 200.000 líneas.
            // Compara BufferedReader, Files.readAllLines, Files.lines y Scanner.
            // Realiza cinco mediciones por método y muestra la mediana.
            BenchmarkLectura.ejecutar(200_000, System.out);


        } catch (IOException e) {

            // Muestro el error si se produce algún problema al trabajar con ficheros.
            System.out.println("Error al acceder a los ficheros: " + e.getMessage());

        } catch (IllegalArgumentException e) {

            // Muestro el error si algún dato tiene un formato incorrecto.
            System.out.println("Datos incorrectos: " + e.getMessage());

        }

    }
}