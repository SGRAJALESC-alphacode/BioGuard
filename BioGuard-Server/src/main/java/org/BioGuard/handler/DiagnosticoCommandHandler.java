package org.BioGuard.handler;

import org.BioGuard.model.Diagnostico;
import org.BioGuard.service.diagnostico.IDiagnosticoService;
import org.BioGuard.service.IPacienteService;
import org.BioGuard.exception.DiagnosticoException;
import java.util.List;
import java.util.Optional;

public class DiagnosticoCommandHandler {

    private final IDiagnosticoService service;
    private final IPacienteService pacienteService;
    private final ResponseFormatter fmt;

    public DiagnosticoCommandHandler(IDiagnosticoService service,
                                     IPacienteService pacienteService,
                                     ResponseFormatter fmt) {
        this.service = service;
        this.pacienteService = pacienteService;
        this.fmt = fmt;
    }

    public String handleEnviarMuestra(String data) {
        try {
            String[] p = data.split("\\|", 2);
            if (p.length < 2) return fmt.error("Formato: documento|secuencia");

            String doc = p[0].trim();
            String sec = p[1].trim();

            if (!pacienteService.buscarPorDocumento(doc).isPresent()) {
                return fmt.error("Paciente no existe");
            }

            if (!sec.matches("^[ATCG]+$")) {
                return fmt.error("Secuencia inválida (solo ATCG)");
            }

            Diagnostico d = service.procesarMuestra(doc, sec);
            return "DIAGNOSTICO:" + d.getId() + "|Virus:" + d.getVirusDetectados().size();

        } catch (DiagnosticoException e) {
            return fmt.error(e.getMessage());
        }
    }

    public String handleConsultarDiagnosticos(String doc) {
        List<Diagnostico> list = service.buscarPorPaciente(doc);
        return fmt.listaDiagnosticos(list);
    }

    public String handleVerDiagnostico(String id) {
        Optional<Diagnostico> opt = service.buscarPorId(id);
        if (!opt.isPresent()) return fmt.error("Diagnóstico no encontrado");

        Diagnostico d = opt.get();
        StringBuilder sb = new StringBuilder();
        sb.append("DIAGNOSTICO:").append(d.getId());
        sb.append("\nPaciente: ").append(d.getDocumentoPaciente());
        sb.append("\nFecha: ").append(d.getFecha());
        sb.append("\nVirus detectados: ").append(d.getVirusDetectados().size());

        for (Diagnostico.HallazgoVirus h : d.getVirusDetectados()) {
            sb.append("\n  ").append(h.getNombreVirus())
                    .append(": ").append(h.getPosicionInicio())
                    .append("-").append(h.getPosicionFin());
        }
        return sb.toString();
    }
}