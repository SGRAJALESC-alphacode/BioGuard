package org.BioGuard.model;

/**
 * Modelo que representa un virus en el sistema BioGuard.
 *
 * <p>Un virus tiene nombre, nivel de infecciosidad y secuencia de ADN.
 * Esta clase se utiliza para almacenar y transportar información viral
 * entre las diferentes capas de la aplicación.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 */
public class Virus {

    private String nombre;
    private String nivel;
    private String secuencia;

    /**
     * Constructor vacío requerido para serialización.
     */
    public Virus() {
    }

    /**
     * Constructor con todos los campos.
     *
     * @param nombre Nombre del virus
     * @param nivel Nivel de infecciosidad
     * @param secuencia Secuencia de ADN
     */
    public Virus(String nombre, String nivel, String secuencia) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.secuencia = secuencia;
    }

    /**
     * Obtiene el nombre del virus.
     *
     * @return Nombre del virus
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del virus.
     *
     * @param nombre Nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el nivel de infecciosidad.
     *
     * @return Nivel ("Poco Infeccioso", "Normal", "Altamente Infeccioso")
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Establece el nivel de infecciosidad.
     *
     * @param nivel Nuevo nivel
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Obtiene la secuencia de ADN.
     *
     * @return Secuencia (solo caracteres A, T, C, G)
     */
    public String getSecuencia() {
        return secuencia;
    }

    /**
     * Establece la secuencia de ADN.
     *
     * @param secuencia Nueva secuencia
     */
    public void setSecuencia(String secuencia) {
        this.secuencia = secuencia;
    }

    /**
     * Retorna el nivel como número para comparaciones.
     *
     * @return 1 para Poco Infeccioso, 2 para Normal, 3 para Altamente Infeccioso
     */
    public int getNivelNumerico() {
        switch (nivel) {
            case "Poco Infeccioso": return 1;
            case "Normal": return 2;
            case "Altamente Infeccioso": return 3;
            default: return 0;
        }
    }

    /**
     * Verifica si la secuencia es válida (solo ATCG).
     *
     * @return true si es válida
     */
    public boolean secuenciaEsValida() {
        return secuencia != null && secuencia.matches("^[ATCG]+$");
    }

    @Override
    public String toString() {
        return String.format("Virus{nombre='%s', nivel='%s', secuencia='%s...'}",
                nombre, nivel, secuencia != null && secuencia.length() > 10 ?
                        secuencia.substring(0, 10) + "..." : secuencia);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Virus virus = (Virus) o;
        return nombre != null ? nombre.equals(virus.nombre) : virus.nombre == null;
    }

    @Override
    public int hashCode() {
        return nombre != null ? nombre.hashCode() : 0;
    }
}