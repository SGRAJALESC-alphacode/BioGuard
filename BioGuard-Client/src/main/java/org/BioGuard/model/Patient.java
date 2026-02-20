package org.BioGuard.model;

public class Patient {

    private String id;
    private String nombre;
    private int edad;
    private String genero;
    private String telefono;

    public Patient() {}

    public Patient(String id, String nombre, int edad, String genero, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.telefono = telefono;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return String.format("Patient{id='%s', nombre='%s', edad=%d}", id, nombre, edad);
    }
}