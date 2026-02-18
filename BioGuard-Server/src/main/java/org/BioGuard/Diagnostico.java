package org.BioGuard;

/**
 * Representa el resultado de un diagnóstico de ADN.
 *
 * // Objetivo
 *    Almacenar la información de un virus detectado en una muestra:
 *    nombre del virus, nivel de infecciosidad y posición en la secuencia.
 */
public class Diagnostico {

    private final String nombreVirus;
    private final String nivelInfecciosidad;
    private final int posicionInicio;
    private final int posicionFin;

    /**
     * Constructor completo.
     *
     * @param nombreVirus Nombre del virus detectado
     * @param nivelInfecciosidad Nivel de infecciosidad
     * @param posicionInicio Posición inicial en la secuencia
     * @param posicionFin Posición final en la secuencia
     */
    public Diagnostico(String nombreVirus, String nivelInfecciosidad, int posicionInicio, int posicionFin) {
        this.nombreVirus = nombreVirus;
        this.nivelInfecciosidad = nivelInfecciosidad;
        this.posicionInicio = posicionInicio;
        this.posicionFin = posicionFin;
    }

    /**
     * Obtiene el nombre del virus.
     */
    public String getNombreVirus() {
        return nombreVirus;
    }

    /**
     * Obtiene el nivel de infecciosidad.
     */
    public String getNivelInfecciosidad() {
        return nivelInfecciosidad;
    }

    /**
     * Obtiene la posición inicial.
     */
    public int getPosicionInicio() {
        return posicionInicio;
    }

    /**
     * Obtiene la posición final.
     */
    public int getPosicionFin() {
        return posicionFin;
    }

    /**
     * Convierte a formato CSV.
     */
    public String toCsvString() {
        return String.join(",",
                nombreVirus,
                nivelInfecciosidad,
                String.valueOf(posicionInicio),
                String.valueOf(posicionFin)
        );
    }

    /**
     * Representación en String del diagnóstico.
     * Este método es el que se muestra en la respuesta del cliente.
     */
    @Override
    public String toString() {
        return String.format("%s [%s] - Posición: %d - %d",
                nombreVirus,
                nivelInfecciosidad,
                posicionInicio,
                posicionFin
        );
    }
}