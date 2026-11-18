package es.nachoruiz.ficheros;

import es.nachoruiz.modelo.Vehiculo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class AccesoFicheros {

    // Número de caracteres que ocupará cada campo de texto en el binario
    public static final int CARACTERES_TEXTO = 20;

    // Tamaño total de cada registro de vehículo:
    // matrícula(40) + marca(40) + modelo(40) + año(4) + estado(40)
    public static final int TAM_REGISTRO =
            2 * CARACTERES_TEXTO +
                    2 * CARACTERES_TEXTO +
                    2 * CARACTERES_TEXTO +
                    4 +
                    2 * CARACTERES_TEXTO;

    // Posición del campo año dentro de cada registro
    private static final int OFFSET_ANIO = 3 * 2 * CARACTERES_TEXTO;


    // ========================== TEXTO

    // Lee un fichero de texto línea a línea con BufferedReader
    public List<String> leerLineasBuffered(Path fichero) throws IOException {

        List<String> lineas = new ArrayList<>();

        try (BufferedReader lector = Files.newBufferedReader(fichero, StandardCharsets.UTF_8)) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                lineas.add(linea);
            }
        }

        return lineas;
    }


    // Lee todas las líneas de un fichero de texto y las guarda en memoria
    public List<String> leerLineasEnMemoria(Path fichero) throws IOException {

        return Files.readAllLines(fichero, StandardCharsets.UTF_8);

    }


    // Cuenta las líneas de un fichero mediante un Stream
    public long contarLineas(Path fichero) throws IOException {

        try (Stream<String> flujo = Files.lines(fichero, StandardCharsets.UTF_8)) {

            return flujo.count();

        }
    }


    // Escribe líneas en un fichero de texto
    public void escribirLineas(Path fichero, List<String> lineas, boolean anadir) throws IOException {

        StandardOpenOption modo = anadir
                ? StandardOpenOption.APPEND
                : StandardOpenOption.TRUNCATE_EXISTING;

        Files.write(fichero, lineas, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, modo);
    }


    // Lee un CSV y convierte sus líneas en objetos Vehiculo
    public List<Vehiculo> leerCsv(Path fichero) throws IOException {

        List<Vehiculo> vehiculos = new ArrayList<>();

        List<String> lineas = leerLineasBuffered(fichero);

        for (int i = 1; i < lineas.size(); i++) {

            String linea = lineas.get(i);

            if (linea.isBlank()) {
                continue;
            }

            String[] c = linea.split(";", -1);

            if (c.length != 5) {
                throw new IllegalArgumentException(
                        "Línea " + (i + 1) + ": se esperaban 5 columnas y hay " + c.length
                );
            }

            try {

                vehiculos.add(new Vehiculo(
                        c[0].trim(),
                        c[1].trim(),
                        c[2].trim(),
                        Integer.parseInt(c[3].trim()),
                        c[4].trim()
                ));

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "Línea " + (i + 1) + ": año con formato incorrecto", e
                );
            }
        }

        return vehiculos;
    }


    // ========================== BINARIO SECUENCIAL

    // Escribe todos los vehículos en un fichero binario de longitud fija
    public void escribirBinario(Path fichero, List<Vehiculo> vehiculos) throws IOException {

        try (DataOutputStream salida = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(fichero)))) {

            for (Vehiculo v : vehiculos) {

                escribirTextoFijo(salida, v.getMatricula());

                escribirTextoFijo(salida, v.getMarca());

                escribirTextoFijo(salida, v.getModelo());

                salida.writeInt(v.getAnio());

                escribirTextoFijo(salida, v.getEstado());
            }
        }
    }


    // Lee todos los vehículos de un fichero binario
    public List<Vehiculo> leerBinario(Path fichero) throws IOException {

        long tamano = Files.size(fichero);

        if (tamano % TAM_REGISTRO != 0) {

            throw new IOException(
                    "El fichero binario está dañado: su tamaño (" + tamano
                            + " bytes) no es múltiplo del registro (" + TAM_REGISTRO + ")"
            );
        }

        List<Vehiculo> vehiculos = new ArrayList<>();

        try (DataInputStream entrada = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(fichero)))) {

            for (long i = 0; i < tamano / TAM_REGISTRO; i++) {

                vehiculos.add(leerRegistro(entrada));

            }

        } catch (EOFException e) {

            throw new IOException("El fichero binario terminó antes de lo esperado", e);

        }

        return vehiculos;
    }


    // ========================== BINARIO ALEATORIO

    // Lee un vehículo concreto del fichero mediante acceso aleatorio
    public Vehiculo leerRegistro(Path fichero, int indice) throws IOException {

        try (RandomAccessFile f = new RandomAccessFile(fichero.toFile(), "r")) {

            comprobarIndice(f, indice);

            f.seek((long) indice * TAM_REGISTRO);

            return leerRegistro(f);
        }
    }


    // Modifica el año de un vehículo mediante acceso aleatorio
    public void actualizarAnio(Path fichero, int indice, int nuevoAnio) throws IOException {

        try (RandomAccessFile f = new RandomAccessFile(fichero.toFile(), "rw")) {

            comprobarIndice(f, indice);

            f.seek((long) indice * TAM_REGISTRO + OFFSET_ANIO);

            f.writeInt(nuevoAnio);
        }
    }


    // Cuenta los registros que contiene el fichero binario
    public long contarRegistros(Path fichero) throws IOException {

        return Files.size(fichero) / TAM_REGISTRO;
    }


    // ========================== MÉTODOS AUXILIARES

    // Comprueba que el índice de un registro sea válido
    private void comprobarIndice(RandomAccessFile f, int indice) throws IOException {

        if (indice < 0 || ((long) indice + 1) * TAM_REGISTRO > f.length()) {

            throw new IllegalArgumentException("No existe el registro " + indice);

        }
    }


    // Lee los datos binarios y construye un objeto Vehiculo
    private Vehiculo leerRegistro(DataInput entrada) throws IOException {

        String matricula = leerTextoFijo(entrada);

        String marca = leerTextoFijo(entrada);

        String modelo = leerTextoFijo(entrada);

        int anio = entrada.readInt();

        String estado = leerTextoFijo(entrada);

        return new Vehiculo(matricula, marca, modelo, anio, estado);
    }


    // Escribe un texto ocupando siempre 20 caracteres
    private void escribirTextoFijo(DataOutput salida, String texto) throws IOException {

        for (int i = 0; i < CARACTERES_TEXTO; i++) {

            salida.writeChar(i < texto.length() ? texto.charAt(i) : '\0');

        }
    }


    // Lee un texto de longitud fija y elimina los caracteres de relleno
    private String leerTextoFijo(DataInput entrada) throws IOException {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < CARACTERES_TEXTO; i++) {

            char c = entrada.readChar();

            if (c != '\0') {

                sb.append(c);

            }
        }

        return sb.toString();
    }
}