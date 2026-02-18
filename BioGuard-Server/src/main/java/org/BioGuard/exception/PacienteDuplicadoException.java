package org.BioGuard.exception;

/**
 * Excepción lanzada cuando se intenta registrar un paciente con un documento
 * de identidad que ya existe en el sistema.
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class PacienteDuplicadoException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String documentoDuplicado;

    /**
     * Constructor con el documento duplicado.
     *
     * @param documento El número de documento que ya existe
     */
    public PacienteDuplicadoException(String documento) {
        super(String.format("Ya existe un paciente registrado con el documento: %s", documento));
        this.documentoDuplicado = documento;
    }

    /**
     * Constructor con documento y causa raíz.
     *
     * @param documento El número de documento que ya existe
     * @param cause La causa original del error
     */
    public PacienteDuplicadoException(String documento, Throwable cause) {
        super(String.format("Error al verificar documento duplicado: %s", documento), cause);
        this.documentoDuplicado = documento;
    }

    /**
     * Obtiene el documento que causó la duplicación.
     *
     * @return El documento duplicado
     */
    public String getDocumentoDuplicado() {
        return documentoDuplicado;
    }
}