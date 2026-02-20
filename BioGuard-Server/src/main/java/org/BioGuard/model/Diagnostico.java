package org.BioGuard.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que representa un diagnóstico médico.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class Diagnostico {

    private String id;
    private String documentoPaciente;
    private String idMuestra;
    private List<HallazgoVirus> virusDetectados;
    private LocalDateTime fecha;

    public Diagnostico() {
        this.virusDetectados = new ArrayList<>();
    }

    public Diagnostico(String documentoPaciente, String idMuestra) {
        this.documentoPaciente = documentoPaciente;
        this.idMuestra = idMuestra;
        this.virusDetectados = new ArrayList<>();
        this.fecha = LocalDateTime.now();
        this.id = generarId();
    }

    private String generarId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss.SSS");
        return documentoPaciente + "_" + fecha.format(formatter);
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDocumentoPaciente() { return documentoPaciente; }
    public void setDocumentoPaciente(String documentoPaciente) {
        this.documentoPaciente = documentoPaciente;
    }

    public String getIdMuestra() { return idMuestra; }
    public void setIdMuestra(String idMuestra) { this.idMuestra = idMuestra; }

    public List<HallazgoVirus> getVirusDetectados() { return virusDetectados; }
    public void setVirusDetectados(List<HallazgoVirus> virusDetectados) {
        this.virusDetectados = virusDetectados;
    }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    /**
     * Agrega un virus detectado al diagnóstico.
     */
    public void agregarHallazgo(HallazgoVirus hallazgo) {
        this.virusDetectados.add(hallazgo);
    }

    /**
     * Clase interna para representar un hallazgo de virus.
     */
    public static class HallazgoVirus {
        private String nombreVirus;
        private int posicionInicio;
        private int posicionFin;

        public HallazgoVirus(String nombreVirus, int posicionInicio, int posicionFin) {
            this.nombreVirus = nombreVirus;
            this.posicionInicio = posicionInicio;
            this.posicionFin = posicionFin;
        }

        public String getNombreVirus() { return nombreVirus; }
        public int getPosicionInicio() { return posicionInicio; }
        public int getPosicionFin() { return posicionFin; }

        @Override
        public String toString() {
            return nombreVirus + "," + posicionInicio + "," + posicionFin;
        }
    }
}