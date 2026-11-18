package es.nachoruiz.modelo;

// Creo esta clase vehículo, que en mi caso, me va a servir para mi caso de la temática
public class Vehiculo {

    //Atributos

    private String matricula;
    private String marca;
    private String modelo;
    private int anio;
    private String estado;

    //Constructor
    public Vehiculo(String matricula, String marca, String modelo, int anio, String estado) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.estado = estado;
    }

    // Getters y Setters

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


    // Método toString

    @Override
    public String toString() {
        return "Vehiculo{" +
                "matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anio=" + anio +
                ", estado='" + estado + '\'' +
                '}';
    }

}
