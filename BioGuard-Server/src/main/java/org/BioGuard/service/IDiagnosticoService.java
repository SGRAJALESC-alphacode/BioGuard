package org.BioGuard.service;

import org.BioGuard.model.Diagnostico;
import org.BioGuard.model.Paciente;
import org.BioGuard.exception.DiagnosticoException;

import java.util.List;
import java.util.Optional;

public interface IDiagnosticoService {
    Diagnostico crearDiagnostico(Paciente paciente, String sintomas, String resultado) throws DiagnosticoException;
    Optional<Diagnostico> buscarPorId(String id);
    List<Diagnostico> buscarPorPaciente(String pacienteId);
    List<Diagnostico> listarTodos();
}