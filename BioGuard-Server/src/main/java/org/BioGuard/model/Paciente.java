package org.BioGuard.model;

/**
 * Modelo de Paciente para el sistema BioGuard.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class Paciente {

    private String documento;
    private String nombre;
    private String apellido;
    private int edad;
    private String correo;
    private String genero;
    private String ciudad;
    private String pais;

    public Paciente() {}

    public Paciente(String documento, String nombre, String apellido, int edad,
                    String correo, String genero, String ciudad, String pais) {
        this.documento = documento;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.correo = correo;
        this.genero = genero;
        this.ciudad = ciudad;
        this.pais = pais;
    }

    // Getters y Setters
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    @Override
    public String toString() {
        return String.format("Paciente{%s, %s %s, %d años, %s}",
                documento, nombre, apellido, edad, correo);
    }
}