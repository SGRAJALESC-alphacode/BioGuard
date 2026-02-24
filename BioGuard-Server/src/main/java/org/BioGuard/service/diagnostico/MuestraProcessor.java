package org.BioGuard.service.diagnostico;

import org.BioGuard.model.*;
import org.BioGuard.service.IVirusService;
import org.BioGuard.exception.DiagnosticoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Procesador de muestras para detectar virus en secuencias de ADN.
 *
 * <p>Responsabilidad Única: Analizar secuencias de ADN y detectar
 * la presencia de virus registrados en el sistema.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MuestraProcessor {

    private final IVirusService virusService;

    public MuestraProcessor(IVirusService virusService) {
        this.virusService = virusService;
    }

    /**
     * Detecta virus en una secuencia de ADN.
     *
     * @param secuencia Secuencia a analizar
     * @return Lista de hallazgos (virus encontrados y sus posiciones)
     */
    public List<Diagnostico.HallazgoVirus> detectarVirus(String secuencia) {
        List<Diagnostico.HallazgoVirus> hallazgos = new ArrayList<>();
        List<Virus> virusRegistrados = virusService.listarTodos();

        for (Virus virus : virusRegistrados) {
            String virusSecuencia = virus.getSecuencia();
            if (virusSecuencia == null || virusSecuencia.isEmpty()) {
                continue;
            }

            List<Integer> posiciones = buscarVirusEnSecuencia(secuencia, virusSecuencia);

            for (Integer inicio : posiciones) {
                int fin = inicio + virusSecuencia.length() - 1;
                hallazgos.add(new Diagnostico.HallazgoVirus(
                        virus.getNombre(), inicio, fin));
            }
        }

        return hallazgos;
    }

    /**
     * Busca todas las ocurrencias de una subcadena en una secuencia.
     *
     * @param secuencia Secuencia completa
     * @param patron Subcadena a buscar
     * @return Lista de posiciones de inicio
     */
    private List<Integer> buscarVirusEnSecuencia(String secuencia, String patron) {
        List<Integer> posiciones = new ArrayList<>();
        int index = 0;
        while ((index = secuencia.indexOf(patron, index)) != -1) {
            posiciones.add(index);
            index += patron.length();
        }
        return posiciones;
    }

    /**
     * Valida que una secuencia sea válida para análisis.
     *
     * @param secuencia Secuencia a validar
     * @throws DiagnosticoException Si la secuencia es inválida
     */
    public void validarSecuencia(String secuencia) throws DiagnosticoException {
        if (secuencia == null || secuencia.trim().isEmpty()) {
            throw new DiagnosticoException("La secuencia no puede estar vacía");
        }
        if (secuencia.length() > 10000) {
            throw new DiagnosticoException("La secuencia es demasiado larga (máx 10000 caracteres)");
        }
        if (!secuencia.matches("^[ATCG]*$")) {
            throw new DiagnosticoException("La secuencia solo puede contener A, T, C, G");
        }
    }

    /**
     * Calcula el porcentaje de similitud entre dos secuencias.
     *
     * @param secuencia1 Primera secuencia
     * @param secuencia2 Segunda secuencia
     * @return Porcentaje de similitud (0-100)
     */
    public double calcularSimilitud(String secuencia1, String secuencia2) {
        if (secuencia1 == null || secuencia2 == null) {
            return 0;
        }

        int minLength = Math.min(secuencia1.length(), secuencia2.length());
        int coincidencias = 0;

        for (int i = 0; i < minLength; i++) {
            if (secuencia1.charAt(i) == secuencia2.charAt(i)) {
                coincidencias++;
            }
        }

        return (coincidencias * 100.0) / minLength;
    }
}