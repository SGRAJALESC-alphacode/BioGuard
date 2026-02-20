package org.BioGuard.service;

import org.BioGuard.model.Paciente;
import org.BioGuard.exception.PacienteDuplicadoException;
import org.BioGuard.exception.MuestraNoEncontradaException;

import java.util.List;
import java.util.Optional;

public interface IPacienteService {
    Paciente registrarPaciente(Paciente paciente) throws PacienteDuplicadoException;
    Optional<Paciente> buscarPorId(String id);
    List<Paciente> buscarPorNombre(String nombre);
    Paciente actualizarPaciente(Paciente paciente) throws MuestraNoEncontradaException;
    boolean eliminarPaciente(String id);
    List<Paciente> listarTodos();
}