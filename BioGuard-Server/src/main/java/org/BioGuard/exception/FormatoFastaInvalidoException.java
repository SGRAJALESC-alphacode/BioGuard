package org.BioGuard.exception;

/**
 * Excepción lanzada cuando un archivo FASTA no cumple con el formato esperado.
 *
 * <p>Esta excepción proporciona información detallada sobre errores de formato
 * en archivos FASTA, incluyendo el archivo, línea y contenido problemático
 * para facilitar la depuración.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 */
public class FormatoFastaInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String nombreArchivo;
    private final int lineaError;
    private final String contenidoError;

    // Constantes para mensajes comunes
    /** Error cuando el archivo no tiene header */
    public static final String ERROR_SIN_HEADER = "El archivo no contiene un header válido (debe comenzar con '>')";

    /** Error cuando no hay secuencia después del header */
    public static final String ERROR_SIN_SECUENCIA = "El archivo no contiene secuencia de ADN después del header";

    /** Error cuando la secuencia tiene caracteres no válidos */
    public static final String ERROR_CARACTERES_INVALIDOS = "La secuencia contiene caracteres no válidos (solo se permiten A, T, C, G)";

    /** Error cuando el header no tiene el formato correcto */
    public static final String ERROR_HEADER_MAL_FORMADO = "El header no tiene el formato correcto: >nombre_virus|nivel_infecciosidad";

    /**
     * Constructor para errores generales de formato sin archivo específico.
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
     * Constructor para errores en una línea específica del archivo.
     *
     * @param mensaje Descripción del error
     * @param archivo Nombre del archivo
     * @param linea Número de línea donde ocurrió el error
     * @param contenido Contenido de la línea que causó el error
     */
    public FormatoFastaInvalidoException(String mensaje, String archivo, int linea, String contenido) {
        super(String.format("Error en archivo %s, línea %d: %s%nContenido: '%s'",
                archivo, linea, mensaje, contenido));
        this.nombreArchivo = archivo;
        this.lineaError = linea;
        this.contenidoError = contenido;
    }

    /**
     * Constructor para errores con causa subyacente.
     *
     * @param mensaje Descripción del error
     * @param causa Causa original del error
     */
    public FormatoFastaInvalidoException(String mensaje, Throwable causa) {
        super("Error de formato FASTA: " + mensaje, causa);
        this.nombreArchivo = "Desconocido";
        this.lineaError = -1;
        this.contenidoError = null;
    }

    /**
     * Constructor para errores en archivo con causa subyacente.
     *
     * @param mensaje Descripción del error
     * @param archivo Nombre del archivo
     * @param causa Causa original del error
     */
    public FormatoFastaInvalidoException(String mensaje, String archivo, Throwable causa) {
        super(String.format("Error en archivo %s: %s", archivo, mensaje), causa);
        this.nombreArchivo = archivo;
        this.lineaError = -1;
        this.contenidoError = null;
    }

    /**
     * Método fábrica para crear una excepción por secuencia inválida.
     *
     * @param secuencia La secuencia que contiene caracteres inválidos
     * @param archivo El archivo donde se encontró la secuencia
     * @return Una excepción configurada con el mensaje apropiado
     */
    public static FormatoFastaInvalidoException porSecuenciaInvalida(String secuencia, String archivo) {
        String mensaje = String.format("Secuencia inválida: '%s' contiene caracteres no permitidos",
                truncarSecuencia(secuencia));
        return new FormatoFastaInvalidoException(mensaje, archivo, -1, secuencia);
    }

    /**
     * Método fábrica para crear una excepción por header mal formado.
     *
     * @param header El header que causó el error
     * @param archivo El archivo donde se encontró el header
     * @param linea Número de línea del header
     * @return Una excepción configurada con el mensaje apropiado
     */
    public static FormatoFastaInvalidoException porHeaderInvalido(String header, String archivo, int linea) {
        return new FormatoFastaInvalidoException(ERROR_HEADER_MAL_FORMADO, archivo, linea, header);
    }

    /**
     * Método fábrica para crear una excepción por falta de secuencia.
     *
     * @param archivo El archivo que no tiene secuencia
     * @param linea Línea donde debería comenzar la secuencia
     * @return Una excepción configurada con el mensaje apropiado
     */
    public static FormatoFastaInvalidoException porFaltaSecuencia(String archivo, int linea) {
        return new FormatoFastaInvalidoException(ERROR_SIN_SECUENCIA, archivo, linea, null);
    }

    /**
     * Obtiene el nombre del archivo donde ocurrió el error.
     *
     * @return Nombre del archivo o "Desconocido" si no se especificó
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Obtiene el número de línea donde ocurrió el error.
     *
     * @return Número de línea o -1 si no se especificó
     */
    public int getLineaError() {
        return lineaError;
    }

    /**
     * Obtiene el contenido de la línea que causó el error.
     *
     * @return Contenido problemático o null si no se especificó
     */
    public String getContenidoError() {
        return contenidoError;
    }

    /**
     * Trunca una secuencia larga para mostrar en mensajes de error.
     *
     * @param secuencia Secuencia a truncar
     * @return Secuencia truncada si es muy larga
     */
    private static String truncarSecuencia(String secuencia) {
        if (secuencia == null) return "null";
        if (secuencia.length() <= 50) return secuencia;
        return secuencia.substring(0, 47) + "...";
    }

    /**
     * Verifica si el error ocurrió en una línea específica.
     *
     * @return true si se especificó una línea de error
     */
    public boolean tieneLineaEspecifica() {
        return lineaError > 0;
    }

    /**
     * Verifica si el error tiene información de archivo.
     *
     * @return true si se especificó un archivo
     */
    public boolean tieneArchivo() {
        return !"Desconocido".equals(nombreArchivo) && nombreArchivo != null;
    }

    /**
     * Obtiene un resumen formateado del error para logging.
     *
     * @return String con información estructurada del error
     */
    public String getResumenError() {
        StringBuilder sb = new StringBuilder("Error FASTA: ");
        if (tieneArchivo()) {
            sb.append("Archivo=").append(nombreArchivo);
        }
        if (tieneLineaEspecifica()) {
            sb.append(", Línea=").append(lineaError);
        }
        sb.append(", Mensaje=").append(getMessage());
        return sb.toString();
    }
}