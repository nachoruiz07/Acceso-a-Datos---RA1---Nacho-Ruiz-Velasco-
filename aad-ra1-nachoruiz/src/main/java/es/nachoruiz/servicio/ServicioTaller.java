package es.nachoruiz.servicio;

import es.nachoruiz.modelo.Vehiculo;
import es.nachoruiz.xml.EscritorXML;
import es.nachoruiz.xml.LectorXml;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServicioTaller {

    // Logger para guardar los detalles técnicos de los errores
    private static final Logger LOG =
            Logger.getLogger(ServicioTaller.class.getName());

    // Lector y escritor que ya tenemos de las actividades anteriores
    private final LectorXml lector = new LectorXml();
    private final EscritorXML escritor = new EscritorXML();


    // ========================== CONSTRUCTOR

    // Prepara un archivo de registro para los errores técnicos
    public ServicioTaller() throws IOException {

        Files.createDirectories(Path.of("salida"));

        // Evito que el Logger muestre los errores técnicos en consola
        LOG.setUseParentHandlers(false);

        // El detalle técnico se guardará en este archivo
        FileHandler manejador = new FileHandler(
                "salida/errores.log",
                true
        );

        manejador.setFormatter(new SimpleFormatter());

        LOG.addHandler(manejador);
    }


    // ========================== OPERACIÓN

    // Representa una operación que puede lanzar una excepción
    @FunctionalInterface
    private interface Operacion<T> {

        T ejecutar() throws Exception;

    }


    // ========================== CARGAR XML

    // Lee un XML y comprueba que los datos de sus vehículos sean válidos
    public List<Vehiculo> cargar(Path xml) throws TallerException {

        return ejecutar(xml, () -> {

            // Leo los vehículos utilizando DOM
            List<Vehiculo> vehiculos = lector.leerConDom(xml);

            // Compruebo los datos
            validarVehiculos(vehiculos);

            return vehiculos;
        });
    }


    // ========================== AÑADIR VEHÍCULO

    // Añade un vehículo utilizando el escritor de AD-1.d
    public void anadirVehiculo(Path xml, Vehiculo vehiculo)
            throws TallerException {

        ejecutar(xml, () -> {

            escritor.anadirVehiculo(xml, vehiculo);

            return null;
        });
    }


    // ========================== CREAR FICHERO NUEVO

    // Crea un fichero sin sobrescribir otro que ya exista
    public void crearExclusivo(Path fichero) throws TallerException {

        ejecutar(fichero, () -> {

            Files.writeString(
                    fichero,
                    "Fichero de prueba",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
            );

            return null;
        });
    }


    // ========================== VALIDAR VEHÍCULOS

    // Comprueba los años y las matrículas de los vehículos
    private void validarVehiculos(List<Vehiculo> vehiculos) {

        Set<String> matriculas = new HashSet<>();

        for (Vehiculo v : vehiculos) {

            // Una matrícula no puede estar vacía
            if (v.getMatricula() == null
                    || v.getMatricula().isBlank()) {

                throw new IllegalArgumentException(
                        "Hay un vehículo sin matrícula."
                );
            }

            // El año debe ser positivo
            if (v.getAnio() <= 0) {

                throw new IllegalArgumentException(
                        "El año del vehículo debe ser positivo."
                );
            }

            // Compruebo que la matrícula no esté repetida
            if (!matriculas.add(v.getMatricula())) {

                throw new IllegalArgumentException(
                        "Hay vehículos con matrículas repetidas."
                );
            }
        }
    }


    // ========================== GESTIONAR EXCEPCIONES

    // Ejecuta una operación y traduce sus errores a mensajes sencillos
    private <T> T ejecutar(Path fichero, Operacion<T> operacion)
            throws TallerException {

        // Obtengo solo el nombre del archivo, sin mostrar la ruta completa
        String nombre = fichero.getFileName().toString();

        try {

            return operacion.ejecutar();


            // ========================== ERRORES DE FICHEROS

            // El archivo no existe
        } catch (NoSuchFileException e) {

            throw error(
                    "No existe el fichero «" + nombre + "».",
                    e
            );

            // No tengo permiso para acceder al archivo
        } catch (AccessDeniedException e) {

            throw error(
                    "No tienes permiso para acceder a «" + nombre + "».",
                    e
            );

            // El archivo ya existe
        } catch (FileAlreadyExistsException e) {

            throw error(
                    "El fichero «" + nombre + "» ya existe.",
                    e
            );

            // Otros errores de lectura o escritura
        } catch (IOException e) {

            throw error(
                    "No se pudo leer o escribir «" + nombre + "».",
                    e
            );


            // ========================== ERRORES XML

            // XML mal formado o con una declaración DOCTYPE prohibida
        } catch (SAXParseException e) {

            String mensaje;

            // Distingo el error de DOCTYPE del resto de errores XML
            if (e.getMessage() != null
                    && e.getMessage().toLowerCase().contains("doctype")) {

                mensaje = "El XML de «" + nombre
                        + "» contiene una declaración DOCTYPE no permitida.";

            } else {

                mensaje = "El XML de «" + nombre
                        + "» no está bien formado.";
            }

            throw error(
                    mensaje + " (línea " + e.getLineNumber()
                            + ", columna " + e.getColumnNumber() + ").",
                    e
            );

            // Otros errores al interpretar XML
        } catch (SAXException | XMLStreamException e) {

            throw error(
                    "El contenido XML de «" + nombre
                            + "» no es válido o no está permitido.",
                    e
            );

            // Errores de las herramientas que procesan XML
        } catch (TransformerException
                 | ParserConfigurationException
                 | XPathExpressionException
                 | XPathFactoryConfigurationException e) {

            throw error(
                    "Error al procesar el XML de «" + nombre + "».",
                    e
            );


            // ========================== ERRORES DE DATOS

            // Un número tiene un formato incorrecto
        } catch (NumberFormatException e) {

            throw error(
                    "Hay un número con formato incorrecto en «"
                            + nombre + "».",
                    e
            );

            // Otros datos no válidos
        } catch (IllegalArgumentException e) {

            throw error(
                    "Hay datos no válidos en «" + nombre + "»: "
                            + e.getMessage(),
                    e
            );


            // ========================== ERROR INESPERADO

            // Cualquier otro error previsto como excepción
        } catch (Exception e) {

            throw error(
                    "Error inesperado al trabajar con «"
                            + nombre + "».",
                    e
            );
        }
    }


    // ========================== REGISTRAR ERROR

    // Guarda el error técnico y devuelve nuestra excepción propia
    private TallerException error(String mensajeUsuario, Exception causa) {

        // Guardo el mensaje y la excepción técnica en el archivo de log
        LOG.log(Level.WARNING, mensajeUsuario, causa);

        // Devuelvo una excepción con el mensaje claro y su causa
        return new TallerException(mensajeUsuario, causa);
    }
}