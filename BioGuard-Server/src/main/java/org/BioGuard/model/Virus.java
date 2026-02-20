package org.BioGuard.model;

/**
 * Modelo que representa un virus en el sistema BioGuard.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class Virus {

    private String id;
    private String nombre;
    private String tipo;
    private int nivelPeligrosidad;
    private String sintomas;
    private String tratamiento;
    private String secuencia;  // ← NUEVO CAMPO

    public Virus() {}

    public Virus(String id, String nombre, String tipo, int nivelPeligrosidad,
                 String sintomas, String tratamiento, String secuencia) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.nivelPeligrosidad = nivelPeligrosidad;
        this.sintomas = sintomas;
        this.tratamiento = tratamiento;
        this.secuencia = secuencia;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getNivelPeligrosidad() { return nivelPeligrosidad; }
    public void setNivelPeligrosidad(int nivelPeligrosidad) {
        this.nivelPeligrosidad = nivelPeligrosidad;
    }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }

    public String getSecuencia() { return secuencia; }  // ← NUEVO
    public void setSecuencia(String secuencia) { this.secuencia = secuencia; }  // ← NUEVO

    @Override
    public String toString() {
        return String.format("Virus{id='%s', nombre='%s', nivel=%d}",
                id, nombre, nivelPeligrosidad);
    }
}