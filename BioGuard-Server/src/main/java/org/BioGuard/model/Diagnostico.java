package org.BioGuard.model;

import java.time.LocalDateTime;

/**
 * Modelo que representa un diagnóstico médico en el sistema BioGuard.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class Diagnostico {

    private String id;
    private Paciente paciente;
    private String sintomas;
    private String resultado;
    private LocalDateTime fecha;

    public Diagnostico() {}

    public Diagnostico(String id, Paciente paciente, String sintomas, String resultado, LocalDateTime fecha) {
        this.id = id;
        this.paciente = paciente;
        this.sintomas = sintomas;
        this.resultado = resultado;
        this.fecha = fecha;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}