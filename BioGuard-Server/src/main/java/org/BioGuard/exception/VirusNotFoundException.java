package org.BioGuard.exception;

/**
 * Excepción lanzada cuando no se encuentra un virus en el catálogo viral.
 *
 * <p>Esta excepción proporciona información detallada sobre el criterio de búsqueda
 * utilizado y el valor buscado para facilitar la depuración y el manejo de errores.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 */
public class VirusNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String valorBuscado;
    private final String criterioBusqueda;

    /** Constante para búsqueda por ID */
    public static final String CRITERIO_ID = "ID";

    /** Constante para búsqueda por nombre */
    public static final String CRITERIO_NOMBRE = "nombre";

    /** Constante para búsqueda por secuencia genética */
    public static final String CRITERIO_SECUENCIA = "secuencia";

    /** Constante para búsqueda por tipo de virus */
    public static final String CRITERIO_TIPO = "tipo";

    /**
     * Constructor para búsqueda por nombre (mantiene compatibilidad con código existente).
     *
     * @param nombre El nombre del virus no encontrado
     */
    public VirusNotFoundException(String nombre) {
        super(String.format("No se encontró el virus '%s' en el catálogo", nombre));
        this.valorBuscado = nombre;
        this.criterioBusqueda = CRITERIO_NOMBRE;
    }

    /**
     * Constructor para búsqueda por cualquier criterio.
     *
     * @param criterio El campo por el que se buscó (ej. "ID", "nombre", "secuencia")
     * @param valor El valor buscado
     */
    public VirusNotFoundException(String criterio, String valor) {
        super(String.format("No se encontró virus con %s: '%s'", criterio, valor));
        this.valorBuscado = valor;
        this.criterioBusqueda = criterio;
    }

    /**
     * Constructor con causa subyacente.
     *
     * @param mensaje Descripción del error
     * @param causa Causa original de la excepción
     */
    public VirusNotFoundException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.valorBuscado = "desconocido";
        this.criterioBusqueda = "desconocido";
    }

    /**
     * Método fábrica para crear excepción cuando no se encuentra un virus por ID.
     *
     * @param id El ID buscado
     * @return Excepción configurada
     */
    public static VirusNotFoundException porId(String id) {
        return new VirusNotFoundException(CRITERIO_ID, id);
    }

    /**
     * Método fábrica para crear excepción cuando no se encuentra un virus por nombre.
     *
     * @param nombre El nombre buscado
     * @return Excepción configurada
     */
    public static VirusNotFoundException porNombre(String nombre) {
        return new VirusNotFoundException(CRITERIO_NOMBRE, nombre);
    }

    /**
     * Obtiene el valor que se buscó.
     *
     * @return El valor buscado
     */
    public String getValorBuscado() {
        return valorBuscado;
    }

    /**
     * Obtiene el criterio usado en la búsqueda.
     *
     * @return El criterio de búsqueda
     */
    public String getCriterioBusqueda() {
        return criterioBusqueda;
    }

    /**
     * Verifica si la búsqueda fue por nombre.
     *
     * @return true si se buscó por nombre
     */
    public boolean busquedaPorNombre() {
        return CRITERIO_NOMBRE.equals(criterioBusqueda);
    }

    /**
     * Verifica si la búsqueda fue por ID.
     *
     * @return true si se buscó por ID
     */
    public boolean busquedaPorId() {
        return CRITERIO_ID.equals(criterioBusqueda);
    }

    /**
     * Obtiene un mensaje detallado para logging.
     *
     * @return Mensaje estructurado con información completa
     */
    public String getMensajeDetallado() {
        return String.format("VirusNotFoundException{criterio='%s', valor='%s', mensaje='%s'}",
                criterioBusqueda, valorBuscado, getMessage());
    }
}