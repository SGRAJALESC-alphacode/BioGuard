package org.BioGuard.exception;

/**
 * Excepción lanzada cuando hay errores en la configuración del sistema.
 *
 * <p>Esta excepción se utiliza para indicar problemas con parámetros de configuración
 * inválidos, archivos de configuración faltantes, puertos incorrectos, o cualquier
 * otro error relacionado con la configuración de la aplicación.</p>
 *
 * <p>Es una excepción de tipo {@link RuntimeException} para no obligar a capturarla
 * en cada método, pero proporciona información detallada para depuración.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 */
public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String parametro;
    private final String valorInvalido;

    /** Códigos de error comunes */
    public static final String ERROR_PUERTO_INVALIDO = "PUERTO_INVALIDO";
    public static final String ERROR_HOST_INVALIDO = "HOST_INVALIDO";
    public static final String ERROR_ARCHIVO_NO_ENCONTRADO = "ARCHIVO_NO_ENCONTRADO";
    public static final String ERROR_FORMATO_INVALIDO = "FORMATO_INVALIDO";
    public static final String ERROR_VALOR_FUERA_RANGO = "VALOR_FUERA_RANGO";

    /**
     * Constructor básico con mensaje de error.
     *
     * @param mensaje Descripción del error
     */
    public ConfigurationException(String mensaje) {
        super("Error de configuración: " + mensaje);
        this.parametro = "desconocido";
        this.valorInvalido = null;
    }

    /**
     * Constructor para error en un parámetro específico.
     *
     * @param parametro Nombre del parámetro con error
     * @param mensaje Descripción del error
     */
    public ConfigurationException(String parametro, String mensaje) {
        super(String.format("Error en parámetro '%s': %s", parametro, mensaje));
        this.parametro = parametro;
        this.valorInvalido = null;
    }

    /**
     * Constructor para valor inválido en un parámetro.
     *
     * @param parametro Nombre del parámetro
     * @param valorInvalido El valor que causó el error
     * @param mensaje Descripción del error
     */
    public ConfigurationException(String parametro, String valorInvalido, String mensaje) {
        super(String.format("Error en parámetro '%s' con valor '%s': %s",
                parametro, valorInvalido, mensaje));
        this.parametro = parametro;
        this.valorInvalido = valorInvalido;
    }

    /**
     * Constructor con causa subyacente.
     *
     * @param mensaje Descripción del error
     * @param causa Causa original de la excepción
     */
    public ConfigurationException(String mensaje, Throwable causa) {
        super("Error de configuración: " + mensaje, causa);
        this.parametro = "desconocido";
        this.valorInvalido = null;
    }

    /**
     * Constructor para error en archivo de configuración.
     *
     * @param archivo Nombre del archivo
     * @param causa Causa original
     */
    public static ConfigurationException archivoNoEncontrado(String archivo, Throwable causa) {
        return new ConfigurationException(
                "archivo",
                archivo,
                "Archivo de configuración no encontrado",
                causa
        );
    }

    /**
     * Constructor completo con todos los detalles.
     *
     * @param parametro Nombre del parámetro
     * @param valorInvalido Valor inválido
     * @param mensaje Descripción
     * @param causa Causa original
     */
    private ConfigurationException(String parametro, String valorInvalido, String mensaje, Throwable causa) {
        super(String.format("Error en parámetro '%s' con valor '%s': %s",
                parametro, valorInvalido, mensaje), causa);
        this.parametro = parametro;
        this.valorInvalido = valorInvalido;
    }

    /**
     * Método fábrica para error de puerto inválido.
     *
     * @param puerto Puerto inválido
     * @return Excepción configurada
     */
    public static ConfigurationException puertoInvalido(int puerto) {
        return new ConfigurationException(
                "puerto",
                String.valueOf(puerto),
                "El puerto debe estar entre 1 y 65535"
        );
    }

    /**
     * Método fábrica para error de host inválido.
     *
     * @param host Host inválido
     * @return Excepción configurada
     */
    public static ConfigurationException hostInvalido(String host) {
        return new ConfigurationException(
                "host",
                host,
                "El host no puede ser null o vacío"
        );
    }

    /**
     * Método fábrica para error de timeout inválido.
     *
     * @param timeout Timeout inválido
     * @return Excepción configurada
     */
    public static ConfigurationException timeoutInvalido(int timeout) {
        return new ConfigurationException(
                "timeout",
                String.valueOf(timeout),
                "El timeout no puede ser negativo"
        );
    }

    /**
     * Obtiene el nombre del parámetro que causó el error.
     *
     * @return Nombre del parámetro
     */
    public String getParametro() {
        return parametro;
    }

    /**
     * Obtiene el valor inválido que causó el error.
     *
     * @return Valor inválido (puede ser null)
     */
    public String getValorInvalido() {
        return valorInvalido;
    }

    /**
     * Verifica si el error tiene información del parámetro.
     *
     * @return true si se especificó un parámetro
     */
    public boolean tieneParametro() {
        return !"desconocido".equals(parametro) && parametro != null;
    }

    /**
     * Verifica si el error tiene información del valor inválido.
     *
     * @return true si se especificó un valor
     */
    public boolean tieneValorInvalido() {
        return valorInvalido != null;
    }

    /**
     * Obtiene un mensaje detallado para logging.
     *
     * @return Mensaje estructurado con toda la información
     */
    public String getMensajeDetallado() {
        StringBuilder sb = new StringBuilder("ConfigurationException{");
        if (tieneParametro()) {
            sb.append("parametro='").append(parametro).append('\'');
        }
        if (tieneValorInvalido()) {
            if (tieneParametro()) sb.append(", ");
            sb.append("valor='").append(valorInvalido).append('\'');
        }
        sb.append(", mensaje='").append(getMessage()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}