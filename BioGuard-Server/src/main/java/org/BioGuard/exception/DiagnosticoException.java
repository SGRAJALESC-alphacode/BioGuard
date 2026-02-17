package org.BioGuard.exception;

/**
 * Excepción lanzada durante el proceso de diagnóstico de ADN.
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class DiagnosticoException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String documentoPaciente;
    private final String muestraId;
    private final int codigoError;

    // Códigos de error estándar
    public static final int ERROR_GENERAL = 1000;
    public static final int ERROR_SECUENCIA_VACIA = 1001;
    public static final int ERROR_SECUENCIA_CORTA = 1002;
    public static final int ERROR_SIN_VIRUS = 1003;
    public static final int ERROR_LECTURA_MUESTRA = 1004;
    public static final int ERROR_GUARDADO_RESULTADOS = 1005;
    public static final int ERROR_ANALISIS_FALLIDO = 1006;

    /**
     * Constructor básico con solo mensaje.
     *
     * @param message Descripción del error
     */
    public DiagnosticoException(String message) {
        super(message);
        this.documentoPaciente = null;
        this.muestraId = null;
        this.codigoError = ERROR_GENERAL;
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Descripción del error
     * @param cause Causa original
     */
    public DiagnosticoException(String message, Throwable cause) {
        super(message, cause);
        this.documentoPaciente = null;
        this.muestraId = null;
        this.codigoError = ERROR_GENERAL;
    }

    /**
     * Constructor para errores asociados a un paciente.
     *
     * @param message Descripción del error
     * @param documento Documento del paciente
     */
    public DiagnosticoException(String message, String documento) {
        super(String.format("Paciente %s: %s", documento, message));
        this.documentoPaciente = documento;
        this.muestraId = null;
        this.codigoError = ERROR_GENERAL;
    }

    /**
     * Constructor para errores con paciente y muestra específicos.
     *
     * @param message Descripción del error
     * @param documento Documento del paciente
     * @param muestra Identificador de la muestra
     */
    public DiagnosticoException(String message, String documento, String muestra) {
        super(String.format("Paciente %s - Muestra %s: %s", documento, muestra, message));
        this.documentoPaciente = documento;
        this.muestraId = muestra;
        this.codigoError = ERROR_GENERAL;
    }

    /**
     * Constructor completo con código de error.
     *
     * @param codigo Código de error específico
     * @param message Descripción del error
     * @param documento Documento del paciente
     * @param muestra Identificador de la muestra
     */
    public DiagnosticoException(int codigo, String message, String documento, String muestra) {
        super(String.format("[CODIGO %d] Paciente %s - Muestra %s: %s", codigo, documento, muestra, message));
        this.documentoPaciente = documento;
        this.muestraId = muestra;
        this.codigoError = codigo;
    }

    // Métodos de fábrica para casos comunes

    /**
     * Crea una excepción para secuencia vacía.
     *
     * @param documento Documento del paciente
     * @return DiagnosticoException configurada
     */
    public static DiagnosticoException secuenciaVacia(String documento) {
        return new DiagnosticoException(
                ERROR_SECUENCIA_VACIA,
                "La secuencia de ADN está vacía",
                documento,
                null
        );
    }

    /**
     * Crea una excepción para secuencia demasiado corta.
     *
     * @param documento Documento del paciente
     * @param longitud Longitud de la secuencia
     * @return DiagnosticoException configurada
     */
    public static DiagnosticoException secuenciaCorta(String documento, int longitud) {
        return new DiagnosticoException(
                ERROR_SECUENCIA_CORTA,
                String.format("Secuencia demasiado corta para análisis: %d nucleótidos", longitud),
                documento,
                null
        );
    }

    /**
     * Crea una excepción para cuando no hay virus registrados.
     *
     * @return DiagnosticoException configurada
     */
    public static DiagnosticoException sinVirusRegistrados() {
        return new DiagnosticoException(
                ERROR_SIN_VIRUS,
                "No hay virus registrados en el sistema para realizar el diagnóstico",
                null,
                null
        );
    }

    /**
     * Crea una excepción para error de lectura de muestra.
     *
     * @param documento Documento del paciente
     * @param muestra Nombre de la muestra
     * @param causa Causa del error
     * @return DiagnosticoException configurada
     */
    public static DiagnosticoException errorLecturaMuestra(String documento, String muestra, Throwable causa) {
        DiagnosticoException ex = new DiagnosticoException(
                ERROR_LECTURA_MUESTRA,
                "Error al leer el archivo de la muestra",
                documento,
                muestra
        );
        ex.initCause(causa);
        return ex;
    }

    /**
     * Crea una excepción para error al guardar resultados.
     *
     * @param documento Documento del paciente
     * @param muestra Nombre de la muestra
     * @param causa Causa del error
     * @return DiagnosticoException configurada
     */
    public static DiagnosticoException errorGuardadoResultados(String documento, String muestra, Throwable causa) {
        DiagnosticoException ex = new DiagnosticoException(
                ERROR_GUARDADO_RESULTADOS,
                "Error al guardar los resultados del diagnóstico",
                documento,
                muestra
        );
        ex.initCause(causa);
        return ex;
    }

    /**
     * Crea una excepción para error durante el análisis.
     *
     * @param documento Documento del paciente
     * @param muestra Nombre de la muestra
     * @param posicion Posición donde falló el análisis
     * @return DiagnosticoException configurada
     */
    public static DiagnosticoException errorAnalisis(String documento, String muestra, int posicion) {
        return new DiagnosticoException(
                ERROR_ANALISIS_FALLIDO,
                String.format("Error al analizar la posición %d de la secuencia", posicion),
                documento,
                muestra
        );
    }

    // Getters

    /**
     * Obtiene el documento del paciente asociado al error.
     *
     * @return Documento del paciente o null si no aplica
     */
    public String getDocumentoPaciente() {
        return documentoPaciente;
    }

    /**
     * Obtiene el identificador de la muestra asociada al error.
     *
     * @return ID de la muestra o null si no aplica
     */
    public String getMuestraId() {
        return muestraId;
    }

    /**
     * Obtiene el código numérico del error.
     *
     * @return Código de error
     */
    public int getCodigoError() {
        return codigoError;
    }

    /**
     * Verifica si el error está asociado a un paciente específico.
     *
     * @return true si tiene documento de paciente
     */
    public boolean tienePaciente() {
        return documentoPaciente != null && !documentoPaciente.isEmpty();
    }

    /**
     * Verifica si el error está asociado a una muestra específica.
     *
     * @return true si tiene ID de muestra
     */
    public boolean tieneMuestra() {
        return muestraId != null && !muestraId.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DiagnosticoException: ");
        sb.append(getMessage());

        if (codigoError != ERROR_GENERAL) {
            sb.append(" [Código: ").append(codigoError).append("]");
        }

        return sb.toString();
    }
}