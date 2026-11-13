package es.nachoruiz.ficheros;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// En esta clase se gestionan los métodos usados en los ficheros (crear/copiar/mover/listar/buscar por patrón/eliminar)
public class GestorFicheros {

    // Crear directorio, si ya existe no falla, solo devuelve una excepción

    public Path crearDirectorio(Path directorio) throws IOException{
        return Files.createDirectories(directorio);
    }

    // Crear fichero

    public Path crearFichero(Path rutaFichero) throws IOException{

        //Instacio una ruta, la cual es la ruta padre del fichero incluido en el método
        Path rutaPadre = rutaFichero.toAbsolutePath().getParent();

        if(rutaPadre != null){
            Files.createDirectories(rutaPadre);
        }

        return Files.createFile(rutaFichero);
    }

    // Copiar fichero

    // si sobreescribir devuelve false, y el destino ya existe, lanza una excepción para reemplazar lo que ya existe

    public Path copiar(Path rutaOrigen, Path rutaDestino, boolean sobreescribir) throws IOException{
        if(sobreescribir){
            return Files.copy(rutaOrigen, rutaDestino, StandardCopyOption.REPLACE_EXISTING);
        }
        return Files.copy(rutaOrigen, rutaDestino);
    }

    // Mover (o renombrar)

    public Path mover(Path rutaOrigen, Path rutaDestino, boolean sobreescribir) throws IOException{
        if(sobreescribir){
            return Files.move(rutaOrigen, rutaDestino, StandardCopyOption.REPLACE_EXISTING);
        }
        return Files.move(rutaOrigen, rutaDestino);
    }

    // Eliminar (DIRECTORIO O FICHERO VACÍO)

    public void eliminar(Path rutaFichero) throws IOException{
        Files.deleteIfExists(rutaFichero);
    }

    // Eliminar recursivo (con todo su contenido dentro, si no existe no hace nada)

    public void eliminarRecursivo(Path rutaFichero) throws IOException{

        //Si no existe el fichero a eliminar, no hace nada

        if(!Files.exists(rutaFichero)){
            return;
        }

        Files.walkFileTree(rutaFichero, new SimpleFileVisitor<>(){

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                //Detecta el archivo, y lo borra

                Files.delete(file);

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if(exc != null){
                    throw exc;
                }

                //Si no salta ninguna excepción, borra el directorio, cuando ya esta vacío
                //Detecta cuando se ha terminado de recorrer un directorio

                Files.delete(dir);

                return FileVisitResult.CONTINUE;
            }
        });
    }

    // Listar archivos de un directorio
    public List<Path> listar (Path rutaDirectorio, String patron) throws IOException {

        //Donde se van a almacenar
        List<Path> resultado = new ArrayList<>();

        //Hace un flujo donde se almacenan las rutas siempre que cumplan el patron
        try(DirectoryStream<Path> flujo = Files.newDirectoryStream(rutaDirectorio, patron)){

            //Las almaceno en el resultado
            for(Path ruta : flujo){
                resultado.add(ruta);
            }

        }
        resultado.sort(null);
        return resultado;
    }

    // Buscar archivos
    public List<Path> buscarRecursivo(Path directorio, String patronGlob) throws IOException {

        //Nos va a servir para asignarle el requisito del patron
        PathMatcher comparador = FileSystems.getDefault().getPathMatcher("glob:" + patronGlob);

        //Creo un flujo en el que se almacenan las rutas del directorio
        try (Stream<Path> flujo = Files.walk(directorio)) {

            // Devuelve la búsqueda que cumpla el filtro de ser un archivo, el patron añadido, lo ordena, y lo pasa
            // a lista
            return flujo.filter(Files::isRegularFile)
                    .filter(p -> comparador.matches(p.getFileName()))
                    .sorted()
                    .toList();
        }
    }

    //Describir un archivo o directorio

    public String describir(Path ruta) throws IOException {

        //Obtengo los atributos del archivo, como el nombre, tamaño, etc...
        BasicFileAttributes atributos = Files.readAttributes(ruta, BasicFileAttributes.class);

        //Obtengo la fecha exacta de la última modificación que se ha hecho y la convierto
        // a hora local de mi ordenador
        LocalDateTime fechaModificado = LocalDateTime.ofInstant(atributos.lastModifiedTime().toInstant(), ZoneId.systemDefault());

        return "Nombre:       " + ruta.getFileName() + "\n"

                + "Tipo:         " + (atributos.isDirectory() ? "directorio" : "fichero") + "\n"

                + "Tamaño:       " + atributos.size() + " bytes\n"

                // Pongo los nanosegundos de la información que me proporcionan a 0, para no verlos también

                + "Modificado:   " + fechaModificado.withNano(0) + "\n"

                + "Legible:      " + Files.isReadable(ruta) + "\n"

                + "Escribible:   " + Files.isWritable(ruta);
    }


    //Resolver una ruta de manera segura

    public Path resolverSeguro(Path base, String relativa) {

        //Convierto la ruta en absoluta, y la normalizo para eliminar elementos innecesarios de ella
        Path baseNormal = base.toAbsolutePath().normalize();

        //Con la ruta base ya normalizada, añado la relativa para completar la ruta por completo, volviendo a
        //normalizar por si dentro de esta ruta relativa añadida se encuentra algún otro elemento innecesario
        Path resultado = baseNormal.resolve(relativa).normalize();

        //Compruebo que la ruta final no se salga de la ruta permitida
        if (!resultado.startsWith(baseNormal)) {

            throw new IllegalArgumentException("La ruta «" + relativa + "» se sale del directorio permitido");

        }

        return resultado;
    }



    }

