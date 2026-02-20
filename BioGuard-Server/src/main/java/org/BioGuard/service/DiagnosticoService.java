package org.BioGuard.service;

import org.BioGuard.model.Diagnostico;
import org.BioGuard.model.Paciente;
import org.BioGuard.exception.DiagnosticoException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DiagnosticoService implements IDiagnosticoService {

    private final Map<String, Diagnostico> diagnosticos = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Diagnostico crearDiagnostico(Paciente paciente, String sintomas, String resultado)
            throws DiagnosticoException {

        if (paciente == null || paciente.getId() == null) {
            throw new DiagnosticoException("Paciente inválido");
        }

        String id = String.valueOf(idGenerator.getAndIncrement());
        Diagnostico diagnostico = new Diagnostico(id, paciente, sintomas, resultado, LocalDateTime.now());

        diagnosticos.put(id, diagnostico);
        return diagnostico;
    }

    @Override
    public Optional<Diagnostico> buscarPorId(String id) {
        return Optional.ofNullable(diagnosticos.get(id));
    }

    @Override
    public List<Diagnostico> buscarPorPaciente(String pacienteId) {
        return diagnosticos.values().stream()
                .filter(d -> d.getPaciente() != null && pacienteId.equals(d.getPaciente().getId()))
                .toList();
    }

    @Override
    public List<Diagnostico> listarTodos() {
        return new ArrayList<>(diagnosticos.values());
    }
}