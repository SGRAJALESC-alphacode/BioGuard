package org.BioGuard.exception;

/**
 * Excepción lanzada cuando hay errores en la configuración del sistema.
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class ConfiguracionException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConfiguracionException(String mensaje) {
        super("[ERROR CONFIGURACIÓN] " + mensaje);
    }

    public ConfiguracionException(String mensaje, Throwable causa) {
        super("[ERROR CONFIGURACIÓN] " + mensaje, causa);
    }
}