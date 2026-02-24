package org.BioGuard.handler;

import org.BioGuard.model.Virus;
import org.BioGuard.service.IVirusService;

import java.util.List;

/**
 * Manejador de comandos relacionados con virus.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class VirusCommandHandler {

    private final IVirusService virusService;

    public VirusCommandHandler(IVirusService virusService) {
        this.virusService = virusService;
    }

    public String handleRegistroSimple(String datos) {
        try {
            String[] partes = datos.split("\\|");
            if (partes.length < 2) {
                return "ERROR: Formato inválido. Se esperaba: nombre|nivel";
            }

            Virus virus = new Virus();
            virus.setNombre(partes[0].trim());
            virus.setNivelPeligrosidad(obtenerNivelNumerico(partes[1].trim()));

            if (partes.length > 2) {
                virus.setSecuencia(partes[2].trim());
            }

            Virus registrado = virusService.registrarVirus(virus);
            return "VIRUS_REGISTRADO:" + registrado.getId();

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String handleRegistroCompleto(String datos) {
        try {
            String[] partes = datos.split(",");
            if (partes.length < 3) {
                return "ERROR: Se requiere al menos nombre,tipo,nivel";
            }

            Virus virus = new Virus();
            virus.setNombre(partes[0].trim());
            virus.setTipo(partes[1].trim());
            virus.setNivelPeligrosidad(Integer.parseInt(partes[2].trim()));

            if (partes.length > 3) virus.setSintomas(partes[3].trim());
            if (partes.length > 4) virus.setTratamiento(partes[4].trim());
            if (partes.length > 5) virus.setSecuencia(partes[5].trim());

            Virus registrado = virusService.registrarVirus(virus);
            return "VIRUS_REGISTRADO:" + registrado.getId();

        } catch (NumberFormatException e) {
            return "ERROR: Nivel debe ser número (1-5)";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String handleConsulta(String nombre) {
        List<Virus> virus = virusService.buscarPorNombre(nombre.trim(), true);
        if (virus.isEmpty()) {
            return "ERROR: Virus no encontrado: " + nombre;
        }

        Virus v = virus.get(0);
        return String.format("VIRUS:%s,%s,%d,%s,%s,%s",
                v.getNombre(), v.getTipo(), v.getNivelPeligrosidad(),
                v.getSintomas() != null ? v.getSintomas() : "",
                v.getTratamiento() != null ? v.getTratamiento() : "",
                v.getSecuencia() != null ? v.getSecuencia() : "");
    }

    public String handleListar(String ignorado) {
        List<Virus> virus = virusService.listarTodos();
        if (virus.isEmpty()) {
            return "No hay virus registrados";
        }

        StringBuilder sb = new StringBuilder("VIRUS:");
        for (Virus v : virus) {
            sb.append(String.format("\n%s,%s,%d,%s",
                    v.getNombre(), v.getTipo(), v.getNivelPeligrosidad(),
                    v.getSecuencia() != null ? v.getSecuencia().substring(0, Math.min(20, v.getSecuencia().length())) + "..." : ""));
        }
        return sb.toString();
    }

    private int obtenerNivelNumerico(String nivel) {
        switch (nivel.toLowerCase()) {
            case "poco infeccioso": return 1;
            case "normal": return 2;
            case "altamente infeccioso": return 3;
            default:
                try {
                    return Integer.parseInt(nivel);
                } catch (NumberFormatException e) {
                    return 2;
                }
        }
    }
}