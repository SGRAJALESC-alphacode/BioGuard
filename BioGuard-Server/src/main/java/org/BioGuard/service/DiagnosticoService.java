package org.BioGuard.service;

import org.BioGuard.model.Diagnostico;
import org.BioGuard.model.Paciente;
import org.BioGuard.exception.DiagnosticoException;
import org.BioGuard.exception.MuestraNoEncontradaException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de diagnósticos.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class DiagnosticoService implements IDiagnosticoService {

    private final Map<String, Diagnostico> diagnosticos = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> indicesPorPaciente = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Diagnostico crearDiagnostico(Paciente paciente, String sintomas, String resultado)
            throws DiagnosticoException {

        if (paciente == null || paciente.getId() == null) {
            throw new DiagnosticoException("Paciente inválido para el diagnóstico");
        }

        String id = String.valueOf(idGenerator.getAndIncrement());
        Diagnostico diagnostico = new Diagnostico(
                id,
                paciente,
                sintomas,
                resultado,
                LocalDateTime.now()
        );

        diagnosticos.put(id, diagnostico);
        indexarPorPaciente(diagnostico);

        return diagnostico;
    }

    @Override
    public Optional<Diagnostico> buscarPorId(String id) {
        return Optional.ofNullable(diagnosticos.get(id));
    }

    @Override
    public List<Diagnostico> buscarPorPaciente(String pacienteId)
            throws MuestraNoEncontradaException {

        Set<String> ids = indicesPorPaciente.get(pacienteId);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return ids.stream()
                .map(diagnosticos::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Diagnostico> listarTodos() {
        return new ArrayList<>(diagnosticos.values());
    }

    @Override
    public boolean eliminarDiagnostico(String id) {
        Diagnostico eliminado = diagnosticos.remove(id);
        if (eliminado != null) {
            desindexarPorPaciente(eliminado);
            return true;
        }
        return false;
    }

    private void indexarPorPaciente(Diagnostico diagnostico) {
        String pacienteId = diagnostico.getPaciente().getId();
        indicesPorPaciente.computeIfAbsent(pacienteId, k -> ConcurrentHashMap.newKeySet())
                .add(diagnostico.getId());
    }

    private void desindexarPorPaciente(Diagnostico diagnostico) {
        String pacienteId = diagnostico.getPaciente().getId();
        Set<String> ids = indicesPorPaciente.get(pacienteId);
        if (ids != null) {
            ids.remove(diagnostico.getId());
            if (ids.isEmpty()) {
                indicesPorPaciente.remove(pacienteId);
            }
        }
    }
}