package org.BioGuard.business.validation;

/**
 * Interfaz para validadores de datos.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public interface IValidator<T> {

    /**
     * Valida un objeto.
     *
     * @param objeto Objeto a validar
     * @return true si es válido
     */
    boolean esValido(T objeto);

    /**
     * Obtiene mensaje de error si no es válido.
     *
     * @param objeto Objeto validado
     * @return Mensaje de error o "Válido" si es correcto
     */
    String getMensajeError(T objeto);
}