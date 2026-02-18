package org.BioGuard.exception;

/**
 * Excepción lanzada cuando no se encuentra un virus en el catálogo viral.
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class VirusNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String nombreVirus;
    private final String criterioBusqueda;

    /**
     * Constructor para búsqueda por nombre.
     *
     * @param nombre El nombre del virus no encontrado
     */
    public VirusNotFoundException(String nombre) {
        super(String.format("No se encontró el virus: %s en el catálogo", nombre));
        this.nombreVirus = nombre;
        this.criterioBusqueda = "nombre";
    }

    /**
     * Constructor para búsqueda por cualquier criterio.
     *
     * @param criterio El campo por el que se buscó (ej. "id", "secuencia")
     * @param valor El valor buscado
     */
    public VirusNotFoundException(String criterio, String valor) {
        super(String.format("No se encontró virus con %s: %s", criterio, valor));
        this.nombreVirus = valor;
        this.criterioBusqueda = criterio;
    }

    /**
     * Obtiene el nombre del virus buscado.
     *
     * @return El nombre del virus
     */
    public String getNombreVirus() {
        return nombreVirus;
    }

    /**
     * Obtiene el criterio usado en la búsqueda.
     *
     * @return El criterio de búsqueda
     */
    public String getCriterioBusqueda() {
        return criterioBusqueda;
    }
}