package org.BioGuard.model;

/**
 * Modelo que representa un virus en el catálogo del sistema BioGuard.
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

    public Virus() {}

    public Virus(String id, String nombre, String tipo, int nivelPeligrosidad, String sintomas, String tratamiento) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.nivelPeligrosidad = nivelPeligrosidad;
        this.sintomas = sintomas;
        this.tratamiento = tratamiento;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getNivelPeligrosidad() { return nivelPeligrosidad; }
    public void setNivelPeligrosidad(int nivelPeligrosidad) { this.nivelPeligrosidad = nivelPeligrosidad; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
}