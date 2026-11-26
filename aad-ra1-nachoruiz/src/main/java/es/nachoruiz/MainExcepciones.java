package es.nachoruiz;

import es.nachoruiz.servicio.ServicioTaller;
import es.nachoruiz.servicio.TallerException;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;

import java.util.Set;

public class MainExcepciones {

    // ========================== MAIN

    public static void main(String[] args) {

        System.out.println("===== AD-1.f - GESTIÓN DE EXCEPCIONES =====");

        // Creo el servicio que gestionará los errores
        ServicioTaller servicio;

        try {

            servicio = new ServicioTaller();

        } catch (IOException e) {

            System.out.println(
                    "No se pudo preparar el registro de errores."
            );

            return;
        }

        // Defino la carpeta de los ficheros de prueba
        Path datos = Path.of("datos");


        // ========================== FICHERO INEXISTENTE

        probar("Fichero inexistente", () ->

                servicio.cargar(
                        datos.resolve("vehiculos-no-existe.xml")
                )
        );


        // ========================== SIN PERMISOS

        probarSinPermisos(
                servicio,
                datos.resolve("vehiculos-sin-permisos.xml")
        );


        // ========================== FICHERO YA EXISTENTE

        probar("Fichero ya existente", () ->

                servicio.crearExclusivo(
                        datos.resolve("vehiculos-ya-existe.xml")
                )
        );


        // ========================== XML MAL FORMADO

        probar("XML mal formado", () ->

                servicio.cargar(
                        datos.resolve("vehiculos-malformado.xml")
                )
        );


        // ========================== XML CON DOCTYPE

        probar("XML con DOCTYPE", () ->

                servicio.cargar(
                        datos.resolve("vehiculos-doctype.xml")
                )
        );


        // ========================== NÚMERO INCORRECTO

        probar("Año con formato incorrecto", () ->

                servicio.cargar(
                        datos.resolve("vehiculos-anio-incorrecto.xml")
                )
        );


        // ========================== DATOS NO VÁLIDOS

        probar("Datos no válidos", () ->

                servicio.cargar(
                        datos.resolve("vehiculos-datos-invalidos.xml")
                )
        );


        // ========================== MATRÍCULA REPETIDA

        probar("Matrícula repetida", () ->

                servicio.cargar(
                        datos.resolve("vehiculos-duplicado.xml")
                )
        );


        // ========================== FIN

        System.out.println(
                "\nTodas las pruebas han terminado sin detener el programa."
        );

        System.out.println(
                "Los detalles técnicos están guardados en salida/errores.log"
        );
    }


    // ========================== REPRESENTAR UNA PRUEBA

    // Operación de prueba que puede lanzar TallerException
    @FunctionalInterface
    private interface Caso {

        void ejecutar() throws TallerException;

    }


    // ========================== EJECUTAR UNA PRUEBA

    // Ejecuta un caso de error y muestra el mensaje al usuario
    private static void probar(String descripcion, Caso caso) {

        System.out.println("\n>> " + descripcion);

        try {

            // Ejecuto la operación que queremos probar
            caso.ejecutar();

            // Si no se produce ningún error, aviso
            System.out.println("   No se ha producido ningún error.");

        } catch (TallerException e) {

            // Muestro únicamente el mensaje para el usuario
            System.out.println(
                    "   Mensaje al usuario: " + e.getMessage()
            );

        }
    }


    // ========================== PRUEBA SIN PERMISOS

    // Quita temporalmente el permiso de lectura y después lo restaura
    private static void probarSinPermisos(
            ServicioTaller servicio,
            Path fichero) {

        System.out.println("\n>> Fichero sin permisos");

        Set<PosixFilePermission> permisosOriginales = null;

        try {

            // Guardo los permisos que tiene el fichero
            permisosOriginales = Files.getPosixFilePermissions(fichero);

            // Quito temporalmente todos los permisos
            Files.setPosixFilePermissions(fichero, Set.of());

            // Intento leer el fichero sin permisos
            try {

                servicio.cargar(fichero);

                System.out.println(
                        "   No se ha producido ningún error de permisos."
                );

            } catch (TallerException e) {

                System.out.println(
                        "   Mensaje al usuario: " + e.getMessage()
                );

            }

        } catch (IOException | UnsupportedOperationException e) {

            System.out.println(
                    "   No se pudo preparar la prueba de permisos."
            );

        } finally {

            // Restauro los permisos originales del fichero
            if (permisosOriginales != null) {

                try {

                    Files.setPosixFilePermissions(
                            fichero,
                            permisosOriginales
                    );

                } catch (IOException e) {

                    System.out.println(
                            "   No se pudieron restaurar los permisos."
                    );
                }
            }
        }
    }
}