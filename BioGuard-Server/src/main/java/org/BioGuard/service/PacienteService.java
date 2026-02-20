package org.BioGuard.service;

import org.BioGuard.model.Paciente;
import org.BioGuard.exception.PacienteDuplicadoException;
import org.BioGuard.exception.MuestraNoEncontradaException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PacienteService implements IPacienteService {

    private final Map<String, Paciente> pacientes = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Paciente registrarPaciente(Paciente paciente) throws PacienteDuplicadoException {
        if (paciente == null) throw new IllegalArgumentException("Paciente no puede ser null");

        if (paciente.getId() == null) {
            paciente.setId(String.valueOf(idGenerator.getAndIncrement()));
        } else if (pacientes.containsKey(paciente.getId())) {
            throw new PacienteDuplicadoException("Ya existe paciente con ID: " + paciente.getId());
        }

        pacientes.put(paciente.getId(), paciente);
        return paciente;
    }

    @Override
    public Optional<Paciente> buscarPorId(String id) {
        return Optional.ofNullable(pacientes.get(id));
    }

    @Override
    public List<Paciente> buscarPorNombre(String nombre) {
        if (nombre == null) return Collections.emptyList();
        return pacientes.values().stream()
                .filter(p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    @Override
    public Paciente actualizarPaciente(Paciente paciente) throws MuestraNoEncontradaException {
        if (paciente == null || paciente.getId() == null)
            throw new IllegalArgumentException("Paciente o ID inválido");

        if (!pacientes.containsKey(paciente.getId())) {
            throw new MuestraNoEncontradaException("Paciente no encontrado: " + paciente.getId());
        }

        pacientes.put(paciente.getId(), paciente);
        return paciente;
    }

    @Override
    public boolean eliminarPaciente(String id) {
        return pacientes.remove(id) != null;
    }

    @Override
    public List<Paciente> listarTodos() {
        return new ArrayList<>(pacientes.values());
    }
}