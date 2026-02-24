package org.BioGuard.service.reporte.modelo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelo que representa un reporte de mutaciones para un paciente.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class ReporteMutacion {

    private String documentoPaciente;
    private String idMuestraActual;
    private LocalDateTime fechaMuestraActual;
    private String secuenciaActual;
    private List<ComparacionMuestra> comparaciones;

    public ReporteMutacion(String documentoPaciente, String idMuestraActual,
                           LocalDateTime fechaMuestraActual, String secuenciaActual) {
        this.documentoPaciente = documentoPaciente;
        this.idMuestraActual = idMuestraActual;
        this.fechaMuestraActual = fechaMuestraActual;
        this.secuenciaActual = secuenciaActual;
    }

    public void setComparaciones(List<ComparacionMuestra> comparaciones) {
        this.comparaciones = comparaciones;
    }

    public String getDocumentoPaciente() { return documentoPaciente; }
    public String getIdMuestraActual() { return idMuestraActual; }
    public LocalDateTime getFechaMuestraActual() { return fechaMuestraActual; }
    public String getSecuenciaActual() { return secuenciaActual; }
    public List<ComparacionMuestra> getComparaciones() { return comparaciones; }

    /**
     * Clase interna para representar la comparación con una muestra anterior.
     */
    public static class ComparacionMuestra {
        private final String idMuestraAnterior;
        private final LocalDateTime fechaMuestraAnterior;
        private final double porcentajeSimilitud;
        private final List<MutacionDetalle> mutaciones;

        public ComparacionMuestra(String idMuestraAnterior, LocalDateTime fechaMuestraAnterior,
                                  double porcentajeSimilitud, List<MutacionDetalle> mutaciones) {
            this.idMuestraAnterior = idMuestraAnterior;
            this.fechaMuestraAnterior = fechaMuestraAnterior;
            this.porcentajeSimilitud = porcentajeSimilitud;
            this.mutaciones = mutaciones;
        }

        public String getIdMuestraAnterior() { return idMuestraAnterior; }
        public LocalDateTime getFechaMuestraAnterior() { return fechaMuestraAnterior; }
        public double getPorcentajeSimilitud() { return porcentajeSimilitud; }
        public List<MutacionDetalle> getMutaciones() { return mutaciones; }
    }

    /**
     * Detalle de una mutación específica.
     */
    public static class MutacionDetalle {
        private final int posicionInicio;
        private final int posicionFin;
        private final String baseOriginal;
        private final String baseNueva;

        public MutacionDetalle(int posicionInicio, int posicionFin,
                               String baseOriginal, String baseNueva) {
            this.posicionInicio = posicionInicio;
            this.posicionFin = posicionFin;
            this.baseOriginal = baseOriginal;
            this.baseNueva = baseNueva;
        }

        public int getPosicionInicio() { return posicionInicio; }
        public int getPosicionFin() { return posicionFin; }
        public String getBaseOriginal() { return baseOriginal; }
        public String getBaseNueva() { return baseNueva; }
    }
}