package org.BioGuard.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para comparar secuencias de ADN y detectar mutaciones.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class SecuenciaComparator {

    /**
     * Resultado de una comparación de secuencias.
     */
    public static class Diferencia {
        private final int posicionInicio;
        private final int posicionFin;
        private final String secuenciaOriginal;
        private final String secuenciaNueva;

        public Diferencia(int posicionInicio, int posicionFin, String original, String nueva) {
            this.posicionInicio = posicionInicio;
            this.posicionFin = posicionFin;
            this.secuenciaOriginal = original;
            this.secuenciaNueva = nueva;
        }

        public int getPosicionInicio() { return posicionInicio; }
        public int getPosicionFin() { return posicionFin; }
        public String getSecuenciaOriginal() { return secuenciaOriginal; }
        public String getSecuenciaNueva() { return secuenciaNueva; }

        @Override
        public String toString() {
            return String.format("%d-%d: '%s' -> '%s'",
                    posicionInicio, posicionFin, secuenciaOriginal, secuenciaNueva);
        }
    }

    /**
     * Compara dos secuencias y retorna las diferencias encontradas.
     *
     * @param actual Secuencia actual
     * @param anterior Secuencia anterior (histórica)
     * @return Lista de diferencias encontradas
     */
    public List<Diferencia> comparar(String actual, String anterior) {
        List<Diferencia> diferencias = new ArrayList<>();

        if (actual == null || anterior == null) {
            return diferencias;
        }

        int longitudMax = Math.max(actual.length(), anterior.length());
        int i = 0;

        while (i < longitudMax) {
            // Si hay diferencia en esta posición
            if (i >= actual.length() || i >= anterior.length() ||
                    actual.charAt(i) != anterior.charAt(i)) {

                int inicio = i;
                StringBuilder original = new StringBuilder();
                StringBuilder nueva = new StringBuilder();

                // Acumular mientras sean diferentes o hasta el final
                while (i < longitudMax &&
                        (i >= actual.length() || i >= anterior.length() ||
                                actual.charAt(i) != anterior.charAt(i))) {

                    if (i < anterior.length()) {
                        original.append(anterior.charAt(i));
                    } else {
                        original.append('-'); // Posición que no existe en original
                    }

                    if (i < actual.length()) {
                        nueva.append(actual.charAt(i));
                    } else {
                        nueva.append('-'); // Posición que no existe en nueva
                    }

                    i++;
                }

                diferencias.add(new Diferencia(
                        inicio, i - 1, original.toString(), nueva.toString()
                ));
            } else {
                i++;
            }
        }

        return diferencias;
    }

    /**
     * Calcula el porcentaje de similitud entre dos secuencias.
     */
    public double calcularSimilitud(String actual, String anterior) {
        if (actual == null || anterior == null || actual.isEmpty() || anterior.isEmpty()) {
            return 0.0;
        }

        int minLength = Math.min(actual.length(), anterior.length());
        int coincidencias = 0;

        for (int i = 0; i < minLength; i++) {
            if (actual.charAt(i) == anterior.charAt(i)) {
                coincidencias++;
            }
        }

        return (coincidencias * 100.0) / minLength;
    }
}