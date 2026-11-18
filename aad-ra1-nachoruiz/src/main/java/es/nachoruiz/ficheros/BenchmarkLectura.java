package es.nachoruiz.ficheros;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Comparación orientativa de distintas formas de leer un fichero de texto.
 * Este benchmark pertenece a la tarea AD-1.b.
 */
public final class BenchmarkLectura {

    // Interfaz que representa una operación de lectura.
    // Nos permite utilizar diferentes métodos de lectura en las mediciones.
    @FunctionalInterface
    private interface Lectura {
        long ejecutar(Path fichero) throws IOException;
    }

    // Constructor privado porque no necesitamos crear objetos de esta clase.
    private BenchmarkLectura() {
    }


    // ========================== EJECUTAR BENCHMARK

    // Genera un fichero temporal, mide cada forma de lectura 5 veces
    // y muestra la mediana de los tiempos obtenidos.
    public static void ejecutar(int lineas, PrintStream salida) throws IOException {

        // Creo un fichero temporal para realizar las pruebas.
        Path fichero = Files.createTempFile("benchmark-", ".txt");

        try {

            // Genero el contenido del fichero con el número de líneas indicado.
            generar(fichero, lineas);

            // Muestro el número de líneas y el tamaño del fichero.
            salida.printf(
                    "Fichero de prueba: %d líneas, %.1f MB%n",
                    lineas,
                    Files.size(fichero) / 1_048_576.0
            );


            // ========================== BUFFEREDREADER

            // Mido cuánto tarda en leer el fichero línea por línea.
            medir(salida, "BufferedReader (línea a línea)", fichero, f -> {

                long n = 0;

                try (BufferedReader r = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {

                    while (r.readLine() != null) {
                        n++;
                    }
                }

                return n;
            });


            // ========================== FILES.READALLLINES

            // Mido cuánto tarda en cargar todas las líneas en memoria.
            medir(salida, "Files.readAllLines (todo a memoria)", fichero,
                    f -> Files.readAllLines(f, StandardCharsets.UTF_8).size());


            // ========================== FILES.LINES

            // Mido cuánto tarda en contar las líneas mediante un Stream.
            medir(salida, "Files.lines (stream)", fichero, f -> {

                try (var flujo = Files.lines(f, StandardCharsets.UTF_8)) {

                    return flujo.count();
                }
            });


            // ========================== SCANNER

            // Mido cuánto tarda en leer el fichero con Scanner.
            medir(salida, "Scanner.nextLine", fichero, f -> {

                long n = 0;

                try (Scanner s = new Scanner(f, StandardCharsets.UTF_8)) {

                    while (s.hasNextLine()) {

                        s.nextLine();
                        n++;
                    }
                }

                return n;
            });

        } finally {

            // Elimino el fichero temporal al terminar las mediciones.
            Files.deleteIfExists(fichero);
        }
    }


    // ========================== GENERAR FICHERO

    // Genera un fichero de texto con datos ficticios de reparaciones.
    private static void generar(Path fichero, int lineas) throws IOException {

        try (var w = Files.newBufferedWriter(fichero, StandardCharsets.UTF_8)) {

            for (int i = 0; i < lineas; i++) {

                w.write(
                        i + ";Vehiculo de prueba " + i
                                + ";Reparacion" + (i % 10)
                                + ";" + (i % 500) + ".99"
                                + ";" + (i % 40)
                );

                w.newLine();
            }
        }
    }


    // ========================== MEDIR TIEMPOS

    // Ejecuta una operación de lectura 5 veces y calcula su mediana.
    private static void medir(
            PrintStream salida,
            String nombre,
            Path fichero,
            Lectura lectura
    ) throws IOException {

        // Creo una lista para guardar los tiempos obtenidos.
        List<Long> tiempos = new ArrayList<>();

        long lineasLeidas = 0;

        // Repito la medición cinco veces.
        for (int i = 0; i < 5; i++) {

            // Guardo el tiempo de inicio.
            long t0 = System.nanoTime();

            // Ejecuto la operación de lectura.
            lineasLeidas = lectura.ejecutar(fichero);

            // Calculo cuánto ha tardado y lo guardo en milisegundos.
            tiempos.add((System.nanoTime() - t0) / 1_000_000);
        }

        // Ordeno los tiempos de menor a mayor.
        Collections.sort(tiempos);

        // Muestro el tiempo central, que corresponde a la mediana.
        salida.printf(
                "  %-38s %6d ms (mediana)   líneas: %d%n",
                nombre,
                tiempos.get(2),
                lineasLeidas
        );
    }
}