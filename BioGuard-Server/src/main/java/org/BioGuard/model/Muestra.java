package org.BioGuard.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una muestra de ADN enviada por un paciente.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class Muestra {

    private String id;
    private String documentoPaciente;
    private String secuencia;
    private LocalDateTime fecha;
    private String archivoPath;

    public Muestra() {}

    public Muestra(String documentoPaciente, String secuencia) {
        this.documentoPaciente = documentoPaciente;
        this.secuencia = secuencia;
        this.fecha = LocalDateTime.now();
        this.id = generarId(documentoPaciente);
    }

    private String generarId(String documento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return documento + "_" + fecha.format(formatter);
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDocumentoPaciente() { return documentoPaciente; }
    public void setDocumentoPaciente(String documentoPaciente) {
        this.documentoPaciente = documentoPaciente;
    }

    public String getSecuencia() { return secuencia; }
    public void setSecuencia(String secuencia) { this.secuencia = secuencia; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getArchivoPath() { return archivoPath; }
    public void setArchivoPath(String archivoPath) { this.archivoPath = archivoPath; }

    /**
     * Obtiene el nombre del archivo para guardar la muestra.
     */
    public String getNombreArchivo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "muestra_" + fecha.format(formatter) + ".fasta";
    }
}