package es.nachoruiz.servicio;

// Excepción propia de la aplicación del taller
public class TallerException extends Exception {

    private static final long serialVersionUID = 1L;

    // Crea una excepción con un mensaje para el usuario
    public TallerException(String mensaje) {

        super(mensaje);

    }

    // Crea una excepción con un mensaje y su causa original
    public TallerException(String mensaje, Throwable causa) {

        super(mensaje, causa);

    }
}