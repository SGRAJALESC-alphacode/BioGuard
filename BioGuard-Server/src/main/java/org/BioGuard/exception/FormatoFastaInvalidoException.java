package org.BioGuard.exception;

/**
 * Excepción lanzada cuando un archivo FASTA no cumple con el formato esperado.
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class FormatoFastaInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String nombreArchivo;
    private final int lineaError;
    private final String contenidoError;

    // Constantes para mensajes comunes
    public static final String ERROR_SIN_HEADER = "El archivo no contiene un header válido (debe comenzar con '>')";
    public static final String ERROR_SIN_SECUENCIA = "El archivo no contiene secuencia de ADN";
    public static final String ERROR_CARACTERES_INVALIDOS = "La secuencia contiene caracteres no válidos (solo se permiten A, T, C, G)";
    public static final String ERROR_HEADER_MAL_FORMADO = "El header no tiene el formato correcto: >nombre_virus|nivel_infecciosidad";

    /**
     * Constructor para errores generales de formato.
     *
     * @param mensaje Descripción del error
     */
    public FormatoFastaInvalidoException(String mensaje) {
        super("Error de formato FASTA: " + mensaje);
        this.nombreArchivo = "Desconocido";
        this.lineaError = -1;
        this.contenidoError = null;
    }

    /**
     * Constructor para errores en un archivo específico.
     *
     * @param mensaje Descripción del error
     * @param archivo Nombre del archivo con error
     */
    public FormatoFastaInvalidoException(String mensaje, String archivo) {
        super(String.format("Error en archivo %s: %s", archivo, mensaje));
        this.nombreArchivo = archivo;
        this.lineaError = -1;
        this.contenidoError = null;
    }

    /**
     * Constructor para errores en una línea específica.
     *
     * @param mensaje Descripción del error
     * @param archivo Nombre del archivo
     * @param linea Número de línea con error
     * @param contenido Contenido de la línea que causó el error
     */
    public FormatoFastaInvalidoException(String mensaje, String archivo, int linea, String contenido) {
        super(String.format("Error en archivo %s, línea %d: %s\nContenido: '%s'",
                archivo, linea, mensaje, contenido));
        this.nombreArchivo = archivo;
        this.lineaError = linea;
        this.contenidoError = contenido;
    }

    /**
     * Constructor para errores de secuencia.
     *
     * @param secuencia La secuencia inválida
     * @param archivo El archivo de origen
     * @return Una excepción con mensaje formateado
     */
    public static FormatoFastaInvalidoException porSecuenciaInvalida(String secuencia, String archivo) {
        return new FormatoFastaInvalidoException(
                String.format("Secuencia inválida: '%s' contiene caracteres no permitidos", secuencia),
                archivo, -1, secuencia);
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public int getLineaError() {
        return lineaError;
    }

    public String getContenidoError() {
        return contenidoError;
    }
}