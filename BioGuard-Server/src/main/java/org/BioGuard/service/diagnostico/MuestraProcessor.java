package org.BioGuard.service.diagnostico;

import org.BioGuard.model.*;
import org.BioGuard.service.IVirusService;
import org.BioGuard.exception.DiagnosticoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Procesador de muestras para detectar virus en secuencias de ADN.
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
     */
    public List<Diagnostico.HallazgoVirus> detectarVirus(String secuencia) {
        List<Diagnostico.HallazgoVirus> hallazgos = new ArrayList<>();
        List<Virus> virusRegistrados = virusService.listarTodos();

        for (Virus virus : virusRegistrados) {
            String virusSecuencia = virus.getSecuencia();
            if (virusSecuencia == null || virusSecuencia.isEmpty()) {
                continue;
            }

            int index = 0;
            while ((index = secuencia.indexOf(virusSecuencia, index)) != -1) {
                int fin = index + virusSecuencia.length() - 1;
                hallazgos.add(new Diagnostico.HallazgoVirus(
                        virus.getNombre(), index, fin));
                index += virusSecuencia.length();
            }
        }

        return hallazgos;
    }

    /**
     * Valida que la secuencia solo contenga ATCG y tenga longitud adecuada.
     */
    public void validarSecuencia(String secuencia) throws DiagnosticoException {
        if (secuencia == null || secuencia.trim().isEmpty()) {
            throw new DiagnosticoException("La secuencia no puede estar vacía");
        }
        if (secuencia.length() > 10000) {
            throw new DiagnosticoException("La secuencia es demasiado larga (máx 10000 caracteres)");
        }
        if (!secuencia.matches("^[ATCG]*$")) {
            throw new DiagnosticoException("La secuencia solo puede contener los caracteres A, T, C, G");
        }
    }
}