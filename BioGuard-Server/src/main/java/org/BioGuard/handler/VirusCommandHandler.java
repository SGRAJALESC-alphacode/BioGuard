package org.BioGuard.handler;

import org.BioGuard.model.Virus;
import org.BioGuard.service.IVirusService;
import java.util.List;

public class VirusCommandHandler {

    private final IVirusService service;
    private final ResponseFormatter fmt;

    public VirusCommandHandler(IVirusService service, ResponseFormatter fmt) {
        this.service = service;
        this.fmt = fmt;
    }

    public String handleRegistroSimple(String data) {
        try {
            String[] p = data.split("\\|");
            if (p.length < 2) return fmt.error("Formato: nombre|nivel");

            Virus virus = new Virus();
            virus.setNombre(p[0]);
            virus.setNivelPeligrosidad(nivelToInt(p[1]));
            if (p.length > 2) virus.setSintomas(p[2]);

            return fmt.virusRegistrado(service.registrarVirus(virus));
        } catch (Exception e) {
            return fmt.error(e.getMessage());
        }
    }

    public String handleRegistroCompleto(String data) {
        try {
            String[] p = data.split(",");
            if (p.length < 3) return fmt.error("Requiere nombre,tipo,nivel");

            Virus virus = new Virus();
            virus.setNombre(p[0].trim());
            virus.setTipo(p[1].trim());
            virus.setNivelPeligrosidad(Integer.parseInt(p[2].trim()));
            if (p.length > 3) virus.setSintomas(p[3].trim());
            if (p.length > 4) virus.setTratamiento(p[4].trim());

            return fmt.virusRegistrado(service.registrarVirus(virus));
        } catch (NumberFormatException e) {
            return fmt.error("Nivel debe ser número (1-5)");
        }
    }

    public String handleConsulta(String nombre) {
        List<Virus> list = service.buscarPorNombre(nombre, true);
        return list.isEmpty() ? fmt.error("Virus no encontrado") : fmt.virusInfo(list.get(0));
    }

    public String handleListar(String ignored) {
        return fmt.listaVirus(service.listarTodos());
    }

    private int nivelToInt(String nivel) {
        switch (nivel.toLowerCase()) {
            case "poco infeccioso": return 1;
            case "normal": return 2;
            case "altamente infeccioso": return 3;
            default:
                try { return Integer.parseInt(nivel); }
                catch (NumberFormatException e) { return 2; }
        }
    }
}