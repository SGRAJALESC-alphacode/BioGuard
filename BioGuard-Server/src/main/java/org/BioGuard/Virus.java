package org.BioGuard;

public class Virus {
    private String nombre;
    private String nivelInfecciosidad; // "Poco Infeccioso", "Normal", "Altamente Infeccioso"
    private String secuencia;

    public Virus() {}

    public Virus(String nombre, String nivelInfecciosidad, String secuencia) {
        this.nombre = nombre;
        this.nivelInfecciosidad = nivelInfecciosidad;
        this.secuencia = secuencia;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNivelInfecciosidad() { return nivelInfecciosidad; }
    public void setNivelInfecciosidad(String nivelInfecciosidad) { this.nivelInfecciosidad = nivelInfecciosidad; }

    public String getSecuencia() { return secuencia; }
    public void setSecuencia(String secuencia) { this.secuencia = secuencia; }

    // Método para verificar si es altamente infeccioso
    public boolean esAltamenteInfeccioso() {
        return "Altamente Infeccioso".equalsIgnoreCase(nivelInfecciosidad);
    }

    @Override
    public String toString() {
        return "Virus{" +
                "nombre='" + nombre + '\'' +
                ", nivel='" + nivelInfecciosidad + '\'' +
                ", secuencia.length=" + (secuencia != null ? secuencia.length() : 0) +
                '}';
    }
}