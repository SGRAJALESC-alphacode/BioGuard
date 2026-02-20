package org.BioGuard.handler;

import org.BioGuard.model.Paciente;
import org.BioGuard.model.Virus;
import org.BioGuard.model.Diagnostico;
import java.util.List;

public class ResponseFormatter {

    public String error(String msg) { return "ERROR: " + msg; }
    public String success(String msg) { return "OK: " + msg; }

    public String pacienteRegistrado(Paciente p) {
        return "PACIENTE_REGISTRADO:" + p.getDocumento();
    }

    public String pacienteInfo(Paciente p) {
        return String.format("PACIENTE:%s,%s,%s,%d,%s,%s,%s,%s",
                p.getDocumento(), p.getNombre(), p.getApellido(), p.getEdad(),
                p.getCorreo(), p.getGenero(), p.getCiudad(), p.getPais());
    }

    public String listaPacientes(List<Paciente> list) {
        if (list.isEmpty()) return "No hay pacientes";
        StringBuilder sb = new StringBuilder("PACIENTES:");
        for (Paciente p : list) sb.append("\n").append(p.getDocumento())
                .append(",").append(p.getNombre()).append(",").append(p.getApellido());
        return sb.toString();
    }

    public String virusRegistrado(Virus v) {
        return "VIRUS_REGISTRADO:" + v.getId();
    }

    public String virusInfo(Virus v) {
        return String.format("VIRUS:%s,%s,%d", v.getNombre(), v.getTipo(), v.getNivelPeligrosidad());
    }

    public String listaVirus(List<Virus> list) {
        if (list.isEmpty()) return "No hay virus";
        StringBuilder sb = new StringBuilder("VIRUS:");
        for (Virus v : list) sb.append("\n").append(v.getNombre())
                .append(",").append(v.getTipo()).append(",").append(v.getNivelPeligrosidad());
        return sb.toString();
    }

    public String diagnosticoCreado(Diagnostico d) {
        return "DIAGNOSTICO:" + d.getId() + "|Virus:" + d.getVirusDetectados().size();
    }

    public String listaDiagnosticos(List<Diagnostico> list) {
        if (list.isEmpty()) return "No hay diagnósticos";
        StringBuilder sb = new StringBuilder("DIAGNOSTICOS:");
        for (Diagnostico d : list) sb.append("\n").append(d.getId())
                .append(",").append(d.getFecha()).append(",")
                .append(d.getVirusDetectados().size()).append(" virus");
        return sb.toString();
    }
}