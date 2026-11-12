package es.nachoruiz.ficheros;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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





    }


}
