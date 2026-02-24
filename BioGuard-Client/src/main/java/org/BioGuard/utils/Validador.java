package org.BioGuard.utils;

/**
 * Clase con métodos de validación reutilizables.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class Validador {

    /**
     * Valida que una secuencia de ADN solo contenga ATCG.
     *
     * @param secuencia Secuencia a validar
     * @return true si es válida
     */
    public static boolean validarSecuenciaADN(String secuencia) {
        return secuencia != null && secuencia.matches("^[ATCG]+$");
    }

    /**
     * Valida que un documento no esté vacío.
     *
     * @param documento Documento a validar
     * @return true si es válido
     */
    public static boolean validarDocumento(String documento) {
        return documento != null && !documento.trim().isEmpty();
    }

    /**
     * Valida que una edad sea un número válido.
     *
     * @param edad Edad como string
     * @return true si es un número válido
     */
    public static boolean validarEdad(String edad) {
        try {
            int e = Integer.parseInt(edad);
            return e > 0 && e < 150;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}